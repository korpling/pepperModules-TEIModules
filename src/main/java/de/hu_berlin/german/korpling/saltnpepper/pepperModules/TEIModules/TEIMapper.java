package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.List;
import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.neovisionaries.i18n.LanguageCode;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SWordAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsFactory;

public class TEIMapper extends PepperMapperImpl{
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		TEIImporterProperties props = ((TEIImporterProperties) getProperties());
		SDocumentGraph docGraph = getSDocument().getSDocumentGraph();
		TEIImporterReader reader = new TEIImporterReader(props);
		reader.setsDocGraph(docGraph);
		this.readXMLResource(reader, getResourceURI());
		return(DOCUMENT_STATUS.COMPLETED);
	}
	private static Logger logger = LoggerFactory.getLogger(TEIMapper.class);
	
	public static class TEIImporterReader extends DefaultHandler2 implements TEITagLibrary{
		//options
		private Boolean default_tokenization = false;
		private Boolean sub_tokenization = false;
		
		private Boolean surplus_removal = false;
		private Boolean unclear_as_token = false;
		private Boolean foreign_as_token = false;
		
		private Boolean use_tokenizer = false;
		private LanguageCode use_tokenizer_language = null;
		
		//naming config strings
		private String lb_name = "lb";
		private String pb_name = "pb";
		private String w_name = "w";
		private String phr_name = "phr";
		private String body_head_name = "head";
		private String div_name = "div";
		private String p_name = "p";
		private String foreign_name = "foreign";
		private String figure_name = "figure";
		private String m_name = "m";
		private String unclear_name = "unclear";
		private String surplus_name = "surplus";
		private String title_name = "title";
		private String gap_name = "gap";
		private String app_name = "app";
		//other Strings to be added here in the future
		private String text_name = "text";
		
		//annotation config values spans
		private String lb_anno_value = "lb";
		private String pb_anno_value = "pb";
		
		//annotation config values strucs
		private String phr_anno_value = "phr";
		private String body_head_anno_value = "head";
		private String div_anno_value = "div";
		private String p_anno_value = "p";
		private String figure_anno_value = "figure";
		private String app_anno_value = "app";
		private String text_anno_value = "text";
		
		
		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setDEFAULT_TOKENIZATION(){
			default_tokenization = true;
		}
		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setSUB_TOKENIZATION(){
			sub_tokenization = true;
		}
		
		
		/**
		 * The default value that is used for creating tokens when using the flag for creating
		 * tokens from only one tag-name.
		 */
		private String default_token_tag = TAG_W;
		
		/**
		 * Declares the tag-name that is used for creating tokens.
		 * @param param The name of the tag to be used for creating tokens.
		 */
		public void set_default_token_tag(String param){
			default_token_tag = param;
		}
		
		/**
		 * true if the parser is inside <text>...</text>
		 */
		private Boolean insidetext = false;
		
		/**
		 * true if the parser is inside <TEIHeader>...<TEIHeader>
		 */
		private Boolean metadata = false;
		
		//stacks for unary break elementes creating spans
		/**
		 * stack for temporarily saving tokens later to be added to lbspan
		 */
		private Stack<SToken> lbSpanTokenStack = new Stack<SToken>();
		/**
		 * stack for temporarily saving tokens later to be added to pbspan
		 */
		private Stack<SToken> pbSpanTokenStack = new Stack<SToken>();
		
		/**
		 * stack that follows the parser in adding and removing xml-elements
		 */
		private Stack<SNode> sNodeStack= null;
		/**
		 * Method to retrieve sNodeStack and initialize if it is null
		 */
		private Stack<SNode> getSNodeStack(){
			if (sNodeStack== null)
				sNodeStack= new Stack<SNode>();
			return(sNodeStack);
		}

		private Stack<String> TagStack = new Stack<String>();
		// returns stack containing xml-element hierarchie
		private Stack<String> getTagStack(){
			if (TagStack== null)
				TagStack= new Stack<String>();
			return(TagStack);
		}
		
		private Stack<SAnnotation> SAnnoStack = null;
		
		private Stack<SAnnotation> getSAnnoStack(){
			if (SAnnoStack == null) 
				SAnnoStack= new Stack<SAnnotation>();
			return(SAnnoStack);
			}
		
		//Stringbuilder used for collecting text between tags
		StringBuilder txt = new StringBuilder();
		//Stringbuilder used for collecting tags between metadata tags
		StringBuilder meta_txt = new StringBuilder();
		
		private SDocumentGraph sDocGraph = null;
		//add instance of metadata
		private TEIImporterMetaData tei_metadata = new TEIImporterMetaData();
		//add primaryText
		private STextualDS primaryText = null;
		
		private SLayer primaryLayer = SaltFactory.eINSTANCE.createSLayer();
		
		public SDocumentGraph getsDocGraph() {
			return sDocGraph;
		}

		public void setsDocGraph(SDocumentGraph DocGraph) {
			sDocGraph = DocGraph;
		}
		
		private TEIImporterProperties props= null;
		
		//initialize tokenizer
		de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer tokenizer = null;
		
		public TEIImporterProperties getProps() {
			return props;
		}
		public void setProps(TEIImporterProperties props) {
			this.props = props;
		}
		//this constructor should usually be used!
		public TEIImporterReader(TEIImporterProperties props){
			//get the parameter values
			super();
			setProps(props);
			default_tokenization = props.isDefaultTokenization();
			sub_tokenization = props.isSubTokenization();
			
			surplus_removal = props.isSurplusRemoval();
			unclear_as_token = props.isUnclearAsToken();
			foreign_as_token = props.isForeignAsToken();
			
			use_tokenizer = props.isUseTokenizer();
			use_tokenizer_language = props.tokenizer_code();
			
			
			//annotation customization
			
			//naming config strings
			lb_name = props.getTagName(TAG_LB);
			pb_name = props.getTagName(TAG_PB);
			w_name = props.getTagName(TAG_W);
			phr_name = props.getTagName(TAG_PHR);
			body_head_name = props.getTagName(TAG_HEAD);
			div_name = props.getTagName(TAG_DIV);
			p_name = props.getTagName(TAG_P);
			foreign_name = props.getTagName(TAG_FOREIGN);
			figure_name = props.getTagName(TAG_FIGURE);
			m_name = props.getTagName(TAG_M);
			unclear_name = props.getTagName(TAG_UNCLEAR);
			surplus_name = props.getTagName(TAG_SURPLUS);
			title_name = props.getTagName(TAG_TITLE);
			gap_name = props.getTagName(TAG_GAP);
			app_name = props.getTagName(TAG_APP);
			//other Strings to be added here in the future
			text_name = props.getTagName(TAG_TEXT);
			
			//annotation config values spans
			lb_anno_value = props.getValuesName(TAG_LB);
			pb_anno_value = props.getValuesName(TAG_PB);
			
			//annotation config values strucs
			phr_anno_value = props.getValuesName(TAG_PHR);
			body_head_anno_value = props.getValuesName(TAG_HEAD);
			div_anno_value = props.getValuesName(TAG_DIV);
			p_anno_value = props.getValuesName(TAG_P);
			figure_anno_value = props.getValuesName(TAG_FIGURE);
			app_anno_value = props.getValuesName(TAG_APP);
			text_anno_value = props.getValuesName(TAG_TEXT);
		}
		
		//JUnit-Test compatability constructor
			public TEIImporterReader(){
				//get the parameter values
				super();
				sDocGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
			}
		
		public void startDocument () {
			sDocGraph.addSLayer(primaryLayer);
			primaryLayer.setSName("primary");
	    }
		
		private void setDominatingToken (SToken token) {
			SDominanceRelation sDominatingRelation= SaltFactory.eINSTANCE.createSDominanceRelation();
			sDominatingRelation.setSource((SStructuredNode) (getSNodeStack().peek()));
			sDominatingRelation.setSStructuredTarget(token);
			sDocGraph.addSRelation(sDominatingRelation);
			
			//System.out.println(getSNodeStack().peek().toString());
		}
		
		//set the dominating struct 
		private void setDominatingStruc (SStructure struc) {
			SDominanceRelation sDominatingRelation= SaltFactory.eINSTANCE.createSDominanceRelation();
			sDominatingRelation.setSource((SStructuredNode) (getSNodeStack().peek()));
			sDominatingRelation.setSStructuredTarget(struc);
			sDocGraph.addSRelation(sDominatingRelation);
		}
		
		//adding space to a primary text
		private void addSpace (STextualDS text) {
			text.setSText(text.getSText()+" ");
		}
		
		//adding an empty token
		private void setEmptyToken(){
			SToken temp_tok = null;
			if (primaryText.getSEnd()==null){
				addSpace(primaryText);
			}
			
			temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
			setDominatingToken(temp_tok);
			
			push_spans(temp_tok);
		}
		
		//adding an empty token for the <gap>-struct
		private void setGapToken(){
			SToken temp_tok = null;
			temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
			for(int i=0; i<3; i++){
				temp_tok.addSAnnotation(getSAnnoStack().pop());
			}
			setDominatingToken(temp_tok);
			
			push_spans(temp_tok);
		}
		
		private void setStandardToken (StringBuilder str) {
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
					SToken temp_tok = null;
					/*in case primaryText is empty, but exists, initialize primaryText with temp
					 *to avoid "null" as part of the string; otherwise add temp to primaryText
					 */
					if (str.length() > 0  && primaryText.getSText()==null){
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						//needs to be named
						primaryText.setSText(tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, 0, primaryText.getSEnd());
						setDominatingToken(temp_tok);
					}
				
					/*add a single space character to split the first and last word from 
					 *two neighboring chunks of text*
					 */
					else if (str.length() > 0 && !(primaryText.getSText()==null)){
						addSpace(primaryText);
						int oldposition = primaryText.getSEnd();
						
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						//needs to be named
						primaryText.setSText(primaryText.getSText()+tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
						setDominatingToken(temp_tok);
					}
					while (!getSAnnoStack().isEmpty()) {
						temp_tok.addSAnnotation(getSAnnoStack().pop());
					}
					//add token to stack for sspans
					push_spans(temp_tok);
					
				}
				str.setLength(0);
			}
		}
		
		private void setTokenList (List<String> tokenlist){
			for (String tokstring: tokenlist){
				SToken temp_tok = null;
				if (primaryText.getSText() != null){
					addSpace(primaryText);
				}
				if (primaryText.getSText()==null){
					primaryText.setSText("");
				}
				int oldposition = primaryText.getSEnd();
				if (primaryText.getSText() == null){
					primaryText.setSText(tokstring);
				}
				
				else if (primaryText.getSText() != null){
					primaryText.setSText(primaryText.getSText()+tokstring);
				}
					
				temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
				setDominatingToken(temp_tok);
				push_spans(temp_tok);
			}
		}
		
		
		
		private void setTokenizedTokens (StringBuilder str) {
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+"," ");
						tempstr = tempstr.trim();
						List<String> tokenliste = tokenizer.tokenizeToString(tempstr, use_tokenizer_language);
						setTokenList(tokenliste);
				}	
				str.setLength(0);
			}
		}
		
		private void setToken (StringBuilder str){
			if (str.toString().trim().length() > 0){
				if (primaryText != null){
					if (use_tokenizer){
						setTokenizedTokens(str);
					}
					else if (!use_tokenizer){
						setStandardToken(str);
					}
				}
			}
		}
		
		
		
		
		//this is the generic method for unary elements creating spans
		//in addition to calling this function, the tokens have to be
		//added in setToken
		private void generic_break(String tag, Stack<SToken> tokenStack, String annovalue){
			if (sub_tokenization){
				setToken(txt);
			}

			EList <SToken> overlappingTokens = new BasicEList<SToken>();
			while (!(tokenStack).isEmpty()){
				overlappingTokens.add(tokenStack.pop());
			}
			SSpan line = sDocGraph.createSSpan(overlappingTokens);
			if (line != null){
				line.createSAnnotation(null, tag, annovalue);
			}

		}
		
		//the different StackStacks have to be added here, so the spans are pushed to them
		public void push_spans(SToken tok){
			lbSpanTokenStack.push(tok);
			pbSpanTokenStack.push(tok);
		}
		
		public void characters(char ch[], int start, int length) {
			//change tokenization to higher level
			if (insidetext){
				StringBuilder tempstr = new StringBuilder();
				for(int i=start; i<start+length; i++){
					tempstr.append(ch[i]);
				}
				txt.append(tempstr.toString().trim());
			}
			
			if (metadata){
				StringBuilder tempstr = new StringBuilder();
				for(int i=start; i<start+length; i++){
					tempstr.append(ch[i]);
				}
				meta_txt.append(tempstr.toString().trim());
			}
			
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			
			if (TAG_TEIHEADER.equals(qName)){
				metadata = true;
			}
			
			else if (metadata){
				//put text between tags into a map
				tei_metadata.push(qName);
				
				//put attribute keys and values into a map
				for (int i = 0; i < attributes.getLength();i++){
					tei_metadata.push_attribute_XPathMap(attributes.getQName(i), attributes.getValue(i));
				}
			}
			
			else if (TAG_TEXT.equals(qName)) {
				TagStack.push(TAG_TEXT);
				//create STextualDS
				primaryText = SaltFactory.eINSTANCE.createSTextualDS();
				sDocGraph.addSNode(primaryText);
				insidetext = true;
				//represent the <text>-tag in Salt
				SStructure text_struc = SaltFactory.eINSTANCE.createSStructure();
				text_struc.createSAnnotation(null, text_name, text_anno_value);
				getSNodeStack().add(text_struc);
				sDocGraph.addSNode(text_struc);
			}
			
			else if (insidetext){
				tokenizer = sDocGraph.createTokenizer();
				
				if (TAG_LB.equals(qName)) {
					generic_break(lb_name, lbSpanTokenStack, lb_anno_value);
				}
				
				else if (TAG_PB.equals(qName)) {
					generic_break(pb_name, pbSpanTokenStack, pb_anno_value);
				}
				
				else if (TAG_W.equals(qName)) {
					setToken(txt);
					TagStack.push(TAG_W);
					if (!(default_tokenization && default_token_tag==TAG_W)){
						SStructure w_struc = SaltFactory.eINSTANCE.createSStructure();
						SWordAnnotation wordanno = SaltSemanticsFactory.eINSTANCE.createSWordAnnotation();
						w_struc.addSAnnotation(wordanno);
						
						if(attributes.getValue(ATT_TYPE)!=null) {
							w_struc.createSAnnotation(null, ATT_TYPE, attributes.getValue(ATT_TYPE));
						}
						
						if(attributes.getValue(ATT_LEMMA)!=null) {
							w_struc.createSAnnotation(null, ATT_LEMMA, attributes.getValue(ATT_LEMMA));
						}
						
						if(attributes.getValue(ATT_XML_LANG)!=null) {
							w_struc.createSAnnotation(null, ATT_XML_LANG, attributes.getValue(ATT_XML_LANG));
						}
						
						sDocGraph.addSNode(w_struc);
						setDominatingStruc(w_struc);
						getSNodeStack().add(w_struc);
					}
					
					else {
						SWordAnnotation wordanno = SaltSemanticsFactory.eINSTANCE.createSWordAnnotation();
						getSAnnoStack().add(wordanno);
						
						if(attributes.getValue(ATT_TYPE)!=null) {
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(ATT_TYPE);
							tempanno.setValue(attributes.getValue(ATT_TYPE));
							getSAnnoStack().add(tempanno);
						}
						
						if(attributes.getValue(ATT_LEMMA)!=null) {
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(ATT_LEMMA);
							tempanno.setValue(attributes.getValue(ATT_LEMMA));
							getSAnnoStack().add(tempanno);
						}
						
						if(attributes.getValue(ATT_XML_LANG)!=null) {
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(ATT_XML_LANG);
							tempanno.setValue(attributes.getValue(ATT_XML_LANG));
							getSAnnoStack().add(tempanno);
						}
					}
					
				}
				
				else if (TAG_PHR.equals(qName)) {
					TagStack.push(TAG_PHR);
					
					SStructure phr_struc = SaltFactory.eINSTANCE.createSStructure();
					phr_struc.createSAnnotation(null, getProps().getTagName(qName) , phr_anno_value);
		//			phr_struc.createSAnnotation(null, phr_name, phr_anno_value);
					sDocGraph.addSNode(phr_struc);
					setDominatingStruc(phr_struc);
					getSNodeStack().add(phr_struc);
					
				}
				
				else if (TAG_HEAD.equals(qName)) {
					if (!(TagStack.peek()==TAG_FIGURE)){
						TagStack.push(TAG_HEAD);
						
						SStructure head_struc = SaltFactory.eINSTANCE.createSStructure();
						head_struc.createSAnnotation(null, body_head_name, body_head_anno_value);
						sDocGraph.addSNode(head_struc);
						setDominatingStruc(head_struc);
						getSNodeStack().add(head_struc);
					}
					else if(TagStack.peek()==TAG_FIGURE){
						
					}
				}
				
				else if (TAG_DIV.equals(qName)) {
					TagStack.push(TAG_DIV);
					
					SStructure div_struc = SaltFactory.eINSTANCE.createSStructure();
					div_struc.createSAnnotation(null, div_name, div_anno_value);
					div_struc.createSAnnotation(null, ATT_TYPE, attributes.getValue(ATT_TYPE));
					sDocGraph.addSNode(div_struc);
					setDominatingStruc(div_struc);
					getSNodeStack().add(div_struc);
				}
				
				else if (TAG_P.equals(qName)) {
					TagStack.push(TAG_P);	
					SStructure p_struc = SaltFactory.eINSTANCE.createSStructure();
					p_struc.createSAnnotation(null, p_name, p_anno_value);
					sDocGraph.addSNode(p_struc);
					setDominatingStruc(p_struc);
					getSNodeStack().add(p_struc);
				}
				
				else if (TAG_FOREIGN.equals(qName)) {
					if (foreign_as_token){
						setToken(txt);
						TagStack.push(TAG_FOREIGN);
						
						SWordAnnotation wordanno = SaltSemanticsFactory.eINSTANCE.createSWordAnnotation();
						getSAnnoStack().add(wordanno);
						if(attributes.getValue(ATT_XML_LANG)!=null) {
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(ATT_XML_LANG);
							tempanno.setValue(attributes.getValue(ATT_XML_LANG));
							getSAnnoStack().add(tempanno);
						}
					}
				}
				
				else if (TAG_FIGURE.equals(qName)) {
					setToken(txt);
					TagStack.push(TAG_FIGURE);
					
					SStructure figure_struc = SaltFactory.eINSTANCE.createSStructure();
					figure_struc.createSAnnotation(null, figure_name, figure_anno_value);
					sDocGraph.addSNode(figure_struc);
					setDominatingStruc(figure_struc);
					getSNodeStack().add(figure_struc);
					
					figure_struc.createSAnnotation(null, ATT_REND, attributes.getValue(ATT_REND));
					
					setEmptyToken();
				}
				
				else if (TAG_M.equals(qName)) {
					setToken(txt);
					TagStack.push(TAG_M);
				}
				
				else if (TAG_UNCLEAR.equals(qName)) {
					if(unclear_as_token){
						setToken(txt);
						TagStack.push(TAG_M);
		
						SWordAnnotation wordanno = SaltSemanticsFactory.eINSTANCE.createSWordAnnotation();
						getSAnnoStack().add(wordanno);
						
						SAnnotation wordanno2 = SaltFactory.eINSTANCE.createSAnnotation();
						wordanno2.setSName(TAG_UNCLEAR);
						getSAnnoStack().add(wordanno2);
						
						if(attributes.getValue(ATT_ATMOST)!=null && attributes.getValue(ATT_ATLEAST)!=null) {
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(ATT_ATMOST);
							tempanno.setValue(attributes.getValue(ATT_ATMOST));
							getSAnnoStack().add(tempanno);
							
							SAnnotation tempanno2 = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno2.setSName(ATT_ATLEAST);
							tempanno2.setValue(attributes.getValue(ATT_ATLEAST));
							getSAnnoStack().add(tempanno2);
						}
					}
				}
				
				else if (TAG_SURPLUS.equals(qName)) {
					setToken(txt);
				}
				
				else if (TAG_TITLE.equals(qName)) {
					//part of <head>
				}
				
				else if (TAG_GAP.equals(qName)) {
					setToken(txt);
					TagStack.push(TAG_FIGURE);
					
					SAnnotation reasonanno = SaltFactory.eINSTANCE.createSAnnotation();
					reasonanno.setSName(ATT_REASON);
					reasonanno.setValue(attributes.getValue(ATT_REASON));
					getSAnnoStack().add(reasonanno);
					
					SAnnotation extentanno = SaltFactory.eINSTANCE.createSAnnotation();
					extentanno.setSName(ATT_EXTENT);
					extentanno.setValue(attributes.getValue(ATT_EXTENT));
					getSAnnoStack().add(extentanno);
					
					SAnnotation unitanno = SaltFactory.eINSTANCE.createSAnnotation();
					unitanno.setSName(ATT_UNIT);
					unitanno.setValue(attributes.getValue(ATT_UNIT));
					getSAnnoStack().add(unitanno);
					
					setGapToken();
				}
				
				else if (TAG_APP.equals(qName)) {
					TagStack.push(TAG_APP);
					
					SStructure app_struc = SaltFactory.eINSTANCE.createSStructure();
					app_struc.createSAnnotation(null, app_name, app_anno_value);
					
					if(attributes.getValue(ATT_XML_LANG)!=null) {
						app_struc.createSAnnotation(null, ATT_RESP, attributes.getValue(ATT_RESP));
					}
					if(attributes.getValue(ATT_XML_LANG)!=null) {
						app_struc.createSAnnotation(null, ATT_TYPE, attributes.getValue(ATT_TYPE));
					}
					
					sDocGraph.addSNode(app_struc);
					setDominatingStruc(app_struc);
					getSNodeStack().add(app_struc);
					
				}
				
				else if (TAG_LEM.equals(qName)) {
					
				}
				
				else if (TAG_SUPPLIED.equals(qName)) {
					
				}
				
				
				else if (TAG_TEXT.equals(qName)) {
					TagStack.push(TAG_TEXT);
					//create STextualDS
					primaryText = SaltFactory.eINSTANCE.createSTextualDS();
					sDocGraph.addSNode(primaryText);
					insidetext = true;
					//represent the <text>-tag in Salt
					SStructure text_struc = SaltFactory.eINSTANCE.createSStructure();
					text_struc.createSAnnotation(null, text_name, text_anno_value);
					getSNodeStack().add(text_struc);
					sDocGraph.addSNode(text_struc);
				}
				
				else if (TAG_SUMMARY.equals(qName)) {
				} 
				
				else if (TAG_BODY.equals(qName)) {
					//nothing
				}
			}
		}
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			
			if (TAG_TEIHEADER.equals(qName)){
				metadata = false;
				
				tei_metadata.add_to_SDoc(sDocGraph.getSDocument(), tei_metadata.getXPathMap());
			}
			
			else if (metadata){
				tei_metadata.push_to_XPathMap(meta_txt.toString());
				meta_txt.setLength(0);
					
				tei_metadata.pop();
			}
			
			else if(insidetext){
				
			
				if (TAG_W.equals(qName)) {
					setToken(txt);
					if (!(default_tokenization && default_token_tag==TAG_W)){
						getSNodeStack().pop();
					}
				}
				
				else if (TAG_PHR.equals(qName)) {
					getSNodeStack().pop();
				}
				
				else if (TAG_HEAD.equals(qName)) {
					if ((TagStack.peek()==TAG_HEAD)){
						if (txt.length()>0) {
							setToken(txt);
						}
						else{
							setEmptyToken();
						}
						
						getSNodeStack().pop();
					}
					if (TagStack.peek()==TAG_FIGURE){
						getSNodeStack().peek().createSAnnotation(null, body_head_name, txt.toString().trim());
						txt.setLength(0);
					}
					
				}
				
				else if (TAG_DIV.equals(qName)) {
					getSNodeStack().pop();
				}
				
				else if (TAG_P.equals(qName)) {
					if (insidetext){
						setToken(txt);
						
						getSNodeStack().pop();
					}
				}
				
				else if (TAG_FOREIGN.equals(qName)) {
					setToken(txt);
				}
				
				
				else if (TAG_FIGURE.equals(qName)) {
					getSNodeStack().pop();
				}
				
				else if (TAG_M.equals(qName)) {
					SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
					tempanno.setSName("morpheme");
					getSAnnoStack().add(tempanno);
					
					setToken(txt);
				}
				
				else if (TAG_UNCLEAR.equals(qName)) {
					if(unclear_as_token){
						if (txt.length()==0){
							SToken temp_tok = null;
							temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
							setDominatingToken(temp_tok);
							
							push_spans(temp_tok);
							while (!getSAnnoStack().isEmpty()) {
								temp_tok.addSAnnotation(getSAnnoStack().pop());
							}
							
						}
						setToken(txt);
					}
				}
				
				else if (TAG_TEXT.equals(qName)) {
					insidetext = false;
					getSNodeStack().pop();
				}
				
				else if (TAG_SURPLUS.equals(qName)) {
					if (surplus_removal) {
						txt.setLength(0);
					}
					else {
						setToken(txt);
					}
				}
				
				else if (TAG_TITLE.equals(qName)) {
					//part of <head>
				}
				
				else if (TAG_GAP.equals(qName)) {
					
				}
				
				else if (TAG_APP.equals(qName)) {
					getSNodeStack().pop();
				}
				
				else if (TAG_LEM.equals(qName)) {
					
				}
				
				else if (TAG_SUPPLIED.equals(qName)) {
					
				}
				
				
				if (TAG_SUMMARY.equals(qName)) {
				} 
				
				else if (TAG_BODY.equals(qName)) {
					//nothing
				}
			}
			
			//remove adequate tag from TagStack
			if (!getTagStack().isEmpty())
				if (getTagStack().peek().equals(qName)) {
					getTagStack().pop();
				}
		}
	}
}