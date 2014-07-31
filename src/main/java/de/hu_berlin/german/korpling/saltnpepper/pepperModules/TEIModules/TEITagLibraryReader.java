package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
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
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;

/**
 * This class parses an xml file following the model of 'BesaReader'.
 * 
 * @author XMLTagExtractor
 **/
public class TEITagLibraryReader extends DefaultHandler2 implements
		TEITagLibrary {
	//options
	private Boolean user_defined_default_tokenization = false;
	private Boolean sub_tokenization = false;
	private Boolean no_input_tokenization = false;
	
	private Boolean surplus_removal = false;
	private Boolean unclear_as_token = false;
	private Boolean foreign_as_token = false;
	
	//naming config strings
	private String lb_name = "";
	private String pb_name = "";
	private String w_name = "";
	private String phr_bname = "";
	private String body_head_name = "";
	private String div_name = "";
	private String p_name = "";
	private String foreign_name = "";
	private String figure_name = "";
	private String m_name = "";
	private String unclear_name = "";
	private String surplus_name = "";
	private String title_name = "";
	private String gap_name = "";
	private String app_name = "";
	//other Strings to be added here in the future
	private String text_name = "";
	
	//annotation config values spans
	private String lb_anno_value = "";
	private String pb_anno_value = "";
	
	//annotation config values strucs
	private String phr_anno_value = "";
	private String body_head_anno_value = "";
	private String div_anno_value = "";
	private String p_anno_value = "";
	private String figure_anno_value = "";
	private String app_anno_value = "";
	private String text_anno_value = "";
	
	
	
	public void setUSER_DEFINED_DEFAULT_TOKENIZATION(){
		user_defined_default_tokenization = true;
	}
	public void setSUB_TOKENIZATION(){
		sub_tokenization = true;
	}
	public void setNO_INPUT_TOKENIZATION(){
		no_input_tokenization = true;
	}
	
	
	
	//tag used for tokenization with option "user defined default tokenization"
	private String default_token_tag = TAG_W;
	
	public void set_default_token_tag(String param){
		default_token_tag = param;
	}
	
	//returns whether the parser is inside <text>...</text>
	private Boolean insidetext = false;
	
	private EList <STYPE_NAME> tokenrelation = new BasicEList<STYPE_NAME>();
	
	//stacks for unary break elementes creating spans
	private Stack<SToken> lbSpanTokenStack = new Stack<SToken>();
	private Stack<SToken> pbSpanTokenStack = new Stack<SToken>();
	
	
	private Stack<SNode> sNodeStack= null;
	// returns stack containing node hierarchie
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
	
	private SDocumentGraph sDocGraph = null;
	private STextualDS primaryText = null;
	
	private SLayer primaryLayer = SaltFactory.eINSTANCE.createSLayer();
	
	public SDocumentGraph getsDocGraph() {
		return sDocGraph;
	}

	public void setsDocGraph(SDocumentGraph DocGraph) {
		sDocGraph = DocGraph;
	}
	
	//this constructor should usually be used!
	public TEITagLibraryReader(TEIImporterProperties props){
		//get the parameter values
		super();
		user_defined_default_tokenization = props.isUserDefinedDefaultTokenization();
		sub_tokenization = props.isSubTokenization();
		no_input_tokenization = props.isNoInputTokenization();
		
		surplus_removal = props.isSurplusRemoval();
		unclear_as_token = props.isUnclearAsToken();
		foreign_as_token = props.isForeignAsToken();
		
	}
	
	//JUnit-Test compatability constructor
		public TEITagLibraryReader(){
			//get the parameter values
			super();
			sDocGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		}
	
	public void startDocument () {
		sDocGraph.addSLayer(primaryLayer);
		primaryLayer.setSName("primary");
		tokenrelation.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
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
	
	private void setToken (StringBuilder str) {
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
		
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (TAG_LB.equals(qName)) {
			generic_break("lb", lbSpanTokenStack, lb_anno_value);
		}
		
		else if (TAG_PB.equals(qName)) {
			generic_break("pb", pbSpanTokenStack, pb_anno_value);
		}
		
		else if (TAG_W.equals(qName)) {
			setToken(txt);
			TagStack.push(TAG_W);
			if (!(user_defined_default_tokenization && default_token_tag==TAG_W)){
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
			phr_struc.createSAnnotation(null, "TAG_PHR", phr_anno_value);
			sDocGraph.addSNode(phr_struc);
			setDominatingStruc(phr_struc);
			getSNodeStack().add(phr_struc);
			
		}
		
		else if (TAG_HEAD.equals(qName)) {
			if (!(TagStack.peek()==TAG_FIGURE)){
				TagStack.push(TAG_HEAD);
				
				SStructure head_struc = SaltFactory.eINSTANCE.createSStructure();
				head_struc.createSAnnotation(null, "Heading", body_head_anno_value);
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
			div_struc.createSAnnotation(null, TAG_DIV, div_anno_value);
			div_struc.createSAnnotation(null, ATT_TYPE, attributes.getValue(ATT_TYPE));
			sDocGraph.addSNode(div_struc);
			setDominatingStruc(div_struc);
			getSNodeStack().add(div_struc);
		}
		
		else if (TAG_P.equals(qName)) {
			if (insidetext){
				TagStack.push(TAG_P);
				
				SStructure p_struc = SaltFactory.eINSTANCE.createSStructure();
				p_struc.createSAnnotation(null, TAG_P, p_anno_value);
				sDocGraph.addSNode(p_struc);
				setDominatingStruc(p_struc);
				getSNodeStack().add(p_struc);
			}
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
			figure_struc.createSAnnotation(null, "Figure", figure_anno_value);
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
			app_struc.createSAnnotation(null, TAG_APP, app_anno_value);
			
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
		
		else if (TAG_ORIGDATE.equals(qName)) {
		} else if (TAG_MSNAME.equals(qName)) {
		} else if (TAG_LANGUSAGE.equals(qName)) {
		} else if (TAG_TEXTLANG.equals(qName)) {
		} else if (TAG_CHANGE.equals(qName)) {
		} else if (TAG_SURNAME.equals(qName)) {
		} else if (TAG_PLACENAME.equals(qName)) {
		} else if (TAG_MSDESC.equals(qName)) {
		} 
		
		else if (TAG_TITLESTMT.equals(qName))  {
		}
		
		else if (TAG_REVISIONDESC.equals(qName)) {
		} 
		
		else if (TAG_LICENCE.equals(qName)) {
		} 
		
		else if (TAG_TEXT.equals(qName)) {
			TagStack.push(TAG_TEXT);
			//create STextualDS
			primaryText = SaltFactory.eINSTANCE.createSTextualDS();
			sDocGraph.addSNode(primaryText);
			insidetext = true;
			//represent the <text>-tag in Salt
			SStructure text_struc = SaltFactory.eINSTANCE.createSStructure();
			text_struc.createSAnnotation(null, "TAG_TEXT", text_anno_value);
			getSNodeStack().add(text_struc);
			sDocGraph.addSNode(text_struc);
		}
		
		else if (TAG_SUMMARY.equals(qName)) {
		} 
		
		else if (TAG_BODY.equals(qName)) {
			//nothing
		}
		
		else if (TAG_FORENAME.equals(qName)) {
		} else if (TAG_FILEDESC.equals(qName)) {
		} else if (TAG_LISTBIBL.equals(qName)) {
		} else if (TAG_COUNTRY.equals(qName)) {
		} else if (TAG_BIBLSCOPE.equals(qName)) {
		} else if (TAG_LANGUAGE.equals(qName)) {
		} else if (TAG_IDNO.equals(qName)) {
		} else if (TAG_SERIES.equals(qName)) {
		} else if (TAG_CREATION.equals(qName)) {
		} else if (TAG_ENCODINGDESC.equals(qName)) {
		} else if (TAG_COLLECTION.equals(qName)) {
		} else if (TAG_DATE.equals(qName)) {
		} else if (TAG_PUBLISHER.equals(qName)) {
		} else if (TAG_MSIDENTIFIER.equals(qName)) {
		} else if (TAG_AUTHOR.equals(qName)) {
		} else if (TAG_AUTHORITY.equals(qName)) {
		} else if (TAG_MSCONTENTS.equals(qName)) {
		} else if (TAG_PUBPLACE.equals(qName)) {
		} else if (TAG_TEI.equals(qName)) {
		} else if (TAG_REPOSITORY.equals(qName)) {
		} else if (TAG_MSPART.equals(qName)) {
		} else if (TAG_SOURCEDESC.equals(qName)) {
		} else if (TAG_PROFILEDESC.equals(qName)) {
		} else if (TAG_PUBLICATIONSTMT.equals(qName)) {
		} else if (TAG_LOCUS.equals(qName)) {
		} else if (TAG_AVAILABILITY.equals(qName)) {
		} else if (TAG_ORIGIN.equals(qName)) {
		} else if (TAG_MSITEM.equals(qName)) {
		} else if (TAG_INCIPIT.equals(qName)) {
		} else if (TAG_HISTORY.equals(qName)) {
		} else if (TAG_REF.equals(qName)) {
		} else if (TAG_BIBL.equals(qName)) {
		} else if (TAG_TEIHEADER.equals(qName)) {
		} 
		
		else if (TAG_AB.equals(qName)) {
		} 
		else if (TAG_OBJECTTYPE.equals(qName)) {
		} 
		else if (TAG_ORIGPLACE.equals(qName)) {
		} 
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (TAG_W.equals(qName)) {
			setToken(txt);
			if (!(user_defined_default_tokenization && default_token_tag==TAG_W)){
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
				getSNodeStack().peek().createSAnnotation(null, "Heading", txt.toString().trim());
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
		
		else if (TAG_ORIGDATE.equals(qName)) {
		} else if (TAG_MSNAME.equals(qName)) {
		} else if (TAG_LANGUSAGE.equals(qName)) {
		} else if (TAG_TEXTLANG.equals(qName)) {
		} else if (TAG_CHANGE.equals(qName)) {
		} else if (TAG_SURNAME.equals(qName)) {
		} else if (TAG_PLACENAME.equals(qName)) {
		} else if (TAG_MSDESC.equals(qName)) {
		} else if (TAG_TITLESTMT.equals(qName)) {
		} else if (TAG_REVISIONDESC.equals(qName)) {
		} else if (TAG_LICENCE.equals(qName)) {
		} 
		
		else if (TAG_TEXT.equals(qName)) {
			insidetext = false;
			getSNodeStack().pop();
		} 
		
		else if (TAG_FORENAME.equals(qName)) {
		} else if (TAG_FILEDESC.equals(qName)) {
		} else if (TAG_LISTBIBL.equals(qName)) {
		} else if (TAG_COUNTRY.equals(qName)) {
		} else if (TAG_BIBLSCOPE.equals(qName)) {
		} else if (TAG_LANGUAGE.equals(qName)) {
		} else if (TAG_IDNO.equals(qName)) {
		} else if (TAG_SERIES.equals(qName)) {
		} else if (TAG_CREATION.equals(qName)) {
		} else if (TAG_ENCODINGDESC.equals(qName)) {
		} else if (TAG_COLLECTION.equals(qName)) {
		} else if (TAG_DATE.equals(qName)) {
		} else if (TAG_PUBLISHER.equals(qName)) {
		} else if (TAG_MSIDENTIFIER.equals(qName)) {
		} else if (TAG_AUTHOR.equals(qName)) {
		} else if (TAG_AUTHORITY.equals(qName)) {
		} else if (TAG_MSCONTENTS.equals(qName)) {
		} else if (TAG_PUBPLACE.equals(qName)) {
		} else if (TAG_TEI.equals(qName)) {
		} else if (TAG_REPOSITORY.equals(qName)) {
		} else if (TAG_MSPART.equals(qName)) {
		} else if (TAG_SOURCEDESC.equals(qName)) {
		} else if (TAG_PROFILEDESC.equals(qName)) {
		} else if (TAG_PUBLICATIONSTMT.equals(qName)) {
		} else if (TAG_LOCUS.equals(qName)) {
		} else if (TAG_AVAILABILITY.equals(qName)) {
		} else if (TAG_ORIGIN.equals(qName)) {
		} else if (TAG_MSITEM.equals(qName)) {
		} else if (TAG_INCIPIT.equals(qName)) {
		} else if (TAG_HISTORY.equals(qName)) {
		} else if (TAG_REF.equals(qName)) {
		} else if (TAG_BIBL.equals(qName)) {
		} else if (TAG_TEIHEADER.equals(qName)) {
		} 
		
		else if (TAG_AB.equals(qName)) {
		} else if (TAG_OBJECTTYPE.equals(qName)) {
		} else if (TAG_ORIGPLACE.equals(qName)) {
		} 
		
		//remove adequate tag from TagStack
		if (!getTagStack().isEmpty())
			if (getTagStack().peek().equals(qName)) {
				getTagStack().pop();
			}
		
	}
}
