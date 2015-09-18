/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.AttributesImpl;

import com.neovisionaries.i18n.LanguageCode;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.exceptions.GraphInsertException;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SWordAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsFactory;

public class TEIMapper extends PepperMapperImpl {
	Logger logger = LoggerFactory.getLogger(TEIImporter.MODULE_NAME);

	@Override
	public DOCUMENT_STATUS mapSDocument() {
		TEIImporterProperties props = ((TEIImporterProperties) getProperties());
		SDocumentGraph docGraph = getSDocument().getSDocumentGraph();
		TEIImporterReader reader = new TEIImporterReader(props);
		reader.setsDocGraph(docGraph);
		this.readXMLResource(reader, getResourceURI());
		return (DOCUMENT_STATUS.COMPLETED);
	}

	public static class TEIImporterReader extends DefaultHandler2 implements TEITagLibrary {
		Logger logger = LoggerFactory.getLogger(TEIImporter.MODULE_NAME);
		// options
		private Boolean default_tokenization = false;
		private Boolean sub_tokenization = false;

		private Boolean surplus_removal = false;
		private Boolean unclear_as_token = false;
		private Boolean foreign_as_token = false;

		private Boolean use_tokenizer = false;
		private LanguageCode use_tokenizer_language = null;

		private Boolean del_redundant_metadata = null;
		private Boolean skip_default_annotations = false;
		private Boolean use_namespace = false;

		private Boolean generic_struct = false;
		private Boolean generic_span = false;
		private Boolean generic_attr = false;

		private Boolean token_anno_span = false;

		private Boolean lastPart = false;
		// private Boolean excludeMetadata = false;

		private Set<String> excludeMetaSet = null;

		// naming config strings
		private String lb_name = "lb";
		private String pb_name = "pb";
		private String w_name = "w";
		private String phr_name = "phr";
		private String body_head_name = "head";
		private String div_name = "div";
		private String p_name = "p";
		private String foreign_name = "foreign";
		private String figure_name = "figure";
		private String unclear_name = "unclear";
		// other Strings to be added here in the future
		private String text_name = "text";

		// annotation config values spans
		private String lb_anno_value = "lb";
		private String pb_anno_value = "pb";

		// annotation config values strucs
		private String phr_anno_value = "phr";
		private String body_head_anno_value = "head";
		private String div_anno_value = "div";
		private String p_anno_value = "p";
		private String figure_anno_value = "figure";
		private String text_anno_value = "text";

		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setDEFAULT_TOKENIZATION() {
			default_tokenization = true;
		}

		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setSUB_TOKENIZATION() {
			sub_tokenization = true;
		}

		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setGENERIC_STRUCT() {
			generic_struct = true;
		}

		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setGENERIC_SPAN() {
			generic_span = true;
		}

		/**
		 * Helper method for compatibility to unit test.
		 */
		public void setGENERIC_ATTR() {
			generic_attr = true;
		}

		/**
		 * The default value that is used for creating tokens when using the
		 * flag for creating tokens from only one tag-name.
		 */
		private String default_token_tag = TAG_W;

		/**
		 * Declares the tag-name that is used for creating tokens.
		 * 
		 * @param param
		 *            The name of the tag to be used for creating tokens.
		 */
		public void set_default_token_tag(String param) {
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

		// stacks for unary break elementes creating spans
		/**
		 * stack for temporarily saving tokens later to be added to lbspan
		 */
		private Stack<SToken> lbSpanTokenStack = new Stack<SToken>();

		/**
		 * stack for temporarily saving tokens later to be added to pbspan
		 */
		private Stack<SToken> pbSpanTokenStack = new Stack<SToken>();
		//

		/**
		 * Map for storing tokens belonging to generic spans
		 */
		private Map<String, Stack<SToken>> genericSpanMap = null;

		private Map<String, Attributes> attrMap = null;

		/**
		 * stack that follows the parser in adding and removing certain elements
		 * that are also sNodes
		 */
		private Stack<SNode> sNodeStack = null;

		/**
		 * Method to retrieve sNodeStack and initialize if it is null
		 * 
		 * @return sNodeStack
		 */
		private Stack<SNode> getSNodeStack() {
			if (sNodeStack == null)
				sNodeStack = new Stack<SNode>();
			return (sNodeStack);
		}

		/**
		 * stack that follows the parser in adding and removing all elements
		 */
		private Stack<String> TagStack = new Stack<String>();

		/**
		 * Method to retrieve TagStack and initialize if it is null
		 * 
		 * @return TagStack
		 */
		private Stack<String> getTagStack() {
			if (TagStack == null)
				TagStack = new Stack<String>();
			return (TagStack);
		}

		/**
		 * stack that follows the parser in adding and removing all elements
		 */
		private Stack<SAnnotation> SAnnoStack = null;

		/**
		 * Method to retrieve SAnnoStack and initialize if it is null
		 * 
		 * @return SAnnoStack
		 */
		private Stack<SAnnotation> getSAnnoStack() {
			if (SAnnoStack == null)
				SAnnoStack = new Stack<SAnnotation>();
			return (SAnnoStack);
		}

		/**
		 * Stringbuilder used for collecting text between insidetext-tags
		 */
		StringBuilder txt = new StringBuilder();

		/**
		 * Stringbuilder used for collecting tags between metadata-tags
		 */
		StringBuilder meta_txt = new StringBuilder();

		/**
		 * SDocumentGraph variable
		 */
		private SDocumentGraph sDocGraph = null;

		/**
		 * Method to return the SDocumentGraph
		 * 
		 * @return sDocGraph
		 */
		public SDocumentGraph getsDocGraph() {
			return sDocGraph;
		}

		/**
		 * Instance of metadata-class
		 */
		private TEIImporterMetaData tei_metadata = new TEIImporterMetaData();

		/**
		 * primary text variable
		 */
		private STextualDS primaryText = null;

		/**
		 * Sets the SDocGraph
		 * 
		 * @param DocGraph
		 *            SDocumentGraph of the reader
		 */
		public void setsDocGraph(SDocumentGraph DocGraph) {
			sDocGraph = DocGraph;
		}

		/**
		 * properties instance that influences the behavior of the mapper
		 */
		private TEIImporterProperties props = null;

		/**
		 * tokenizer variable
		 */
		de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer tokenizer = null;

		/**
		 * gets the properties instance
		 * 
		 * @return props
		 */
		public TEIImporterProperties getProps() {
			return props;
		}

		/**
		 * sets the properties instance
		 * 
		 * @param props
		 *            the properties instance that is to be used by the mapper
		 */
		public void setProps(TEIImporterProperties props) {
			this.props = props;
		}

		/**
		 * The standard constructor that should always be used!
		 * 
		 * @param props
		 *            the properties instance that is to be used by the mapper
		 */
		public TEIImporterReader(TEIImporterProperties props) {
			// get the parameter values
			super();
			setProps(props);
			default_tokenization = props.isDefaultTokenization();
			sub_tokenization = props.isSubTokenization();

			surplus_removal = props.isSurplusRemoval();
			unclear_as_token = props.isUnclearAsToken();
			foreign_as_token = props.isForeignAsToken();

			use_tokenizer = props.isUseTokenizer();
			use_tokenizer_language = props.tokenizer_code();

			del_redundant_metadata = props.isDelMetadata();
			skip_default_annotations = props.isSkipAnnotations();

			generic_struct = props.isUseGenericStruct();
			generic_span = props.isUseGenericSpan();
			generic_attr = props.isUseGenericAttr();

			lastPart = props.isUseLastPart();

			token_anno_span = props.isUseTokenAnnoSpan();

			// fill metadata
			props.fillMappings();
			excludeMetaSet = props.retrieveExcludeMetadataSet();

			// annotation customization

			// naming config strings
			lb_name = props.getTagName(TAG_LB);
			pb_name = props.getTagName(TAG_PB);
			w_name = props.getTagName(TAG_W);
			phr_name = props.getTagName(TAG_PHR);
			body_head_name = props.getTagName(TAG_HEAD);
			div_name = props.getTagName(TAG_DIV);
			p_name = props.getTagName(TAG_P);
			foreign_name = props.getTagName(TAG_FOREIGN);
			figure_name = props.getTagName(TAG_FIGURE);

			unclear_name = props.getTagName(TAG_UNCLEAR);

			// other Strings to be added here in the future
			text_name = props.getTagName(TAG_TEXT);

			// annotation config values spans
			lb_anno_value = props.getValuesName(TAG_LB);
			pb_anno_value = props.getValuesName(TAG_PB);

			// annotation config values strucs
			phr_anno_value = props.getValuesName(TAG_PHR);
			body_head_anno_value = props.getValuesName(TAG_HEAD);
			div_anno_value = props.getValuesName(TAG_DIV);
			p_anno_value = props.getValuesName(TAG_P);
			figure_anno_value = props.getValuesName(TAG_FIGURE);

			text_anno_value = props.getValuesName(TAG_TEXT);
		}

		/**
		 * Legacy constructor used by the reader-jUnit-test
		 */
		public TEIImporterReader() {
			// get the parameter values
			super();
			sDocGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		}

		/**
		 * called when reader starts reading the input document
		 */
		public void startDocument() {

		}

		/**
		 * adds token to sDocGraph dominated by SStructure on top of SNodeStack
		 * 
		 * @param token
		 *            token to be added
		 */
		private void setDominatingToken(SToken token) {
			SDominanceRelation sDominatingRelation = SaltFactory.eINSTANCE.createSDominanceRelation();
			sDominatingRelation.setSource((SStructuredNode) (getSNodeStack().peek()));
			sDominatingRelation.setSStructuredTarget(token);
			sDocGraph.addSRelation(sDominatingRelation);
		}

		/**
		 * adds SStructure to sDocGraph dominated by SStructure on top of
		 * SNodeStack
		 * 
		 * @param struc
		 *            SStructure to be added
		 */
		private void setDominatingStruc(SStructure struc) {
			SDominanceRelation sDominatingRelation = SaltFactory.eINSTANCE.createSDominanceRelation();
			sDominatingRelation.setSource((SStructuredNode) (getSNodeStack().peek()));
			sDominatingRelation.setSStructuredTarget(struc);
			sDocGraph.addSRelation(sDominatingRelation);
		}

		/**
		 * adds a single space character to a primary text
		 * 
		 * @param text
		 *            primary text that is to be manipulated
		 */
		private void addSpace(STextualDS text) {
			text.setSText(text.getSText() + " ");
		}

		/**
		 * method to add annotations to nodes
		 * 
		 * @param node
		 *            node to be annotated
		 * @param name
		 *            annotation name
		 * @param value
		 *            annotation value
		 * @param namespace
		 *            namespace
		 */
		private void addDefaultAnnotation(SNode node, String name, String value, String namespace) {
			if (!skip_default_annotations) {
				node.createSAnnotation(namespace, name, value);
			}
		}

		private String retrieveNamespace(Boolean prop, String name) {
			String namespace = null;
			if (prop) {
				namespace = name;
				return (namespace);
			} else if (!prop) {
				return (namespace);
			}
			return ("erratic namespace");
		}

		/**
		 * adds an empty token to the sDocGraph
		 */
		private void setEmptyToken() {
			SToken temp_tok = null;
			if (primaryText.getSEnd() == null) {
				addSpace(primaryText);
			}

			temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
			setDominatingToken(temp_tok);

			push_spans(temp_tok);
		}

		/**
		 * creates a span for each token annotation
		 * 
		 */
		private void createTokenAnnoSpan(SToken token, SAnnotation anno) {
			if (token_anno_span) {
				SSpan span = sDocGraph.createSSpan(token);
				span.addSAnnotation(anno);
			}
		}

		/**
		 * adds an empty token to the sDocGraph. This token is dominated by an
		 * SStructure as required by the <gap>-tag
		 */
		private void setGapToken() {
			SToken temp_tok = null;
			temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
			while (!getSAnnoStack().isEmpty()) {
				createTokenAnnoSpan(temp_tok, getSAnnoStack().peek());

				if (temp_tok.getLabel(null, getSAnnoStack().peek().getName()) == null) {
					try {
						// FIXME this try catch should be removed when it is
						// clear what is causing the underlying problem: The
						// occuring error is that annotations cannot be added
						// twice for SWordAnnotation
						temp_tok.addSAnnotation(getSAnnoStack().pop());
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
				} else {
					getSAnnoStack().pop();
				}
			}
			setDominatingToken(temp_tok);

			push_spans(temp_tok);
		}

		/**
		 * method to check whether a SNode has outgoing sRelations
		 * 
		 * @param closingStructure
		 *            the SNode to be checked
		 * @return return true if one or more outgoing sRelations exist, false
		 *         alternatively
		 */
		private Boolean checkOutgoingRelations(SNode closingStructure) {
			if (closingStructure.getOutgoingSRelations().size() < 1) {
				return false;
			} else {
				return true;
			}
		}

		/**
		 * method to pop SNode, that checks whether there is at least one token
		 * assigned to the SNode; if not an empty token is added
		 */
		private void popNodeWithNoTokenCheck() {
			if (!getSNodeStack().empty()) {
				if (checkOutgoingRelations(getSNodeStack().peek())) {
					getSNodeStack().pop();
				}

				else {
					setEmptyToken();
					getSNodeStack().pop();
				}
			}
		}

		/**
		 * default method to add a token to the sDocGraph
		 * 
		 * @param str
		 *            Stringbuilder that contains the text to be used for the
		 *            token
		 */
		private void setStandardToken(StringBuilder str) {
			if (str.toString().trim().length() > 0) {
				if (primaryText != null) {
					SToken temp_tok = null;
					/*
					 * in case primaryText is empty, but exists, initialize
					 * primaryText with tempto avoid "null" as part of the
					 * string; otherwise add temp to primaryText
					 */
					if (str.length() > 0 && primaryText.getSText() == null) {
						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+", " ");
						tempstr = tempstr.trim();
						// needs to be named
						primaryText.setSText(tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, 0, primaryText.getSEnd());
						setDominatingToken(temp_tok);
					}

					/*
					 * add a single space character to split the first and last
					 * word fromtwo neighboring chunks of text*
					 */
					else if (str.length() > 0 && !(primaryText.getSText() == null)) {
						addSpace(primaryText);
						int oldposition = primaryText.getSEnd();

						String tempstr;
						tempstr = str.toString();
						tempstr = tempstr.replaceAll("\\s+", " ");
						tempstr = tempstr.trim();
						// needs to be named
						primaryText.setSText(primaryText.getSText() + tempstr);
						temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
						setDominatingToken(temp_tok);
					}
					while (!getSAnnoStack().isEmpty()) {
						createTokenAnnoSpan(temp_tok, getSAnnoStack().peek());
						if (temp_tok.getLabel(null, getSAnnoStack().peek().getName()) == null) {
							temp_tok.addSAnnotation(getSAnnoStack().pop());
						} else {
							getSAnnoStack().pop();
						}
					}
					// add token to stack for sspans
					push_spans(temp_tok);

				}
				str.setLength(0);
			}
		}

		/**
		 * adds a list of tokens to the sDocGraph
		 * 
		 * @param tokenlist
		 *            a list containing tokens
		 */
		private void setTokenList(List<String> tokenlist) {
			for (String tokstring : tokenlist) {
				SToken temp_tok = null;
				if (primaryText.getSText() != null) {
					addSpace(primaryText);
				}
				if (primaryText.getSText() == null) {
					primaryText.setSText("");
				}
				int oldposition = primaryText.getSEnd();
				if (primaryText.getSText() == null) {
					primaryText.setSText(tokstring);
				}

				else if (primaryText.getSText() != null) {
					primaryText.setSText(primaryText.getSText() + tokstring);
				}

				temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
				setDominatingToken(temp_tok);
				push_spans(temp_tok);
			}
		}

		/**
		 * creates list of tokens and calls setTokenList to add these tokens to
		 * the sDocGraph
		 * 
		 * @param str
		 *            StringBuilder that contains text that is tokenized and
		 *            subsequently added to the sDocGraph as a list of tokens
		 *            that was returned by tokenizing
		 */
		private void setTokenizedTokens(StringBuilder str) {
			if (str.toString().trim().length() > 0) {
				if (primaryText != null) {
					String tempstr;
					tempstr = str.toString();
					tempstr = tempstr.replaceAll("\\s+", " ");
					tempstr = tempstr.trim();
					List<String> tokenliste = tokenizer.tokenizeToString(tempstr, use_tokenizer_language);
					setTokenList(tokenliste);
				}
				str.setLength(0);
			}
		}

		/**
		 * sets tokens generically by checking whether tokenization is set by
		 * the user
		 * 
		 * @param str
		 *            StringBuilder that contains text that is to be added as
		 *            one or more tokens
		 */
		private void setToken(StringBuilder str) {
			if (str.toString().trim().length() > 0) {
				if (primaryText != null) {
					if (use_tokenizer) {
						setTokenizedTokens(str);
					} else if (!use_tokenizer) {
						setStandardToken(str);
					}
				}
			}
		}

		/**
		 * this is the generic method for unary elements creating spans in
		 * addition to calling this function, the tokens have to be added in
		 * setToken
		 * 
		 * @param tag
		 *            string that is used for annotating the span
		 * @param tokenStack
		 *            stack containing the germane tokens
		 * @param annovalue
		 *            annotation value that is to be annotated
		 */
		private void generic_break(String tag, Stack<SToken> tokenStack, String annovalue) {
			if (sub_tokenization) {
				setToken(txt);
			}

			EList<SToken> overlappingTokens = new BasicEList<SToken>();
			while (!(tokenStack).isEmpty()) {
				overlappingTokens.add(tokenStack.pop());
			}
			SSpan line = sDocGraph.createSSpan(overlappingTokens);
			if (line != null) {
				line.createSAnnotation(null, tag, annovalue);
				if (generic_attr && attrMap != null) {
					Attributes attributes = attrMap.get(tag);
					if (attributes != null) {
						int length = attributes.getLength();
						for (int i = 0; i < length; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							line.createSAnnotation(null, name, value);
						}
					}
				}
			}
		}

		private void addGenericAnno(SNode node, Attributes attr, String namespace) {
			int length = attr.getLength();
			for (int i = 0; i < length; i++) {
				String name = attr.getQName(i);
				String value = attr.getValue(i);

				SAnnotation tempAnno = sDocGraph.createSAnnotation(namespace, name, value);
				node.addSAnnotation(tempAnno);
			}
		}

		/**
		 * pushes the spans to the added stacks
		 * 
		 * @param tok
		 *            token that is pushed
		 */
		private void push_spans(SToken tok) {
			lbSpanTokenStack.push(tok);
			pbSpanTokenStack.push(tok);

			pushToGenerics(tok);
		}

		/**
		 * add a tag to the stack of spans
		 * 
		 * @param name
		 *            tag-name
		 */
		private void addToGenericSpans(String name, Attributes attr) {
			if (genericSpanMap == null) {
				genericSpanMap = new Hashtable<>();
			}
			if (attrMap == null) {
				attrMap = new Hashtable<>();
			}

			attrMap.put(name, new AttributesImpl(attr));

			Stack<SToken> gen_stack = new Stack<>();
			genericSpanMap.put(name, gen_stack);
		}

		/**
		 * pushes the tokens to the stacks of the generic spans
		 * 
		 * @param token
		 *            token that is pushed
		 */
		private void pushToGenerics(SToken token) {
			if (genericSpanMap != null) {
				Set<String> keySet = genericSpanMap.keySet();
				for (String s : keySet) {
					Stack<SToken> tempStack = genericSpanMap.get(s);
					tempStack.push(token);
				}
			}
		}

		/**
		 * method to collect characters between tags
		 * 
		 * @param ch
		 *            array of characters
		 * @param start
		 *            starting position
		 * @param length
		 *            length of the array
		 */
		@Override
		public void characters(char ch[], int start, int length) {
			// change tokenization to higher level
			if (insidetext) {
				StringBuilder tempstr = new StringBuilder();
				for (int i = start; i < start + length; i++) {
					tempstr.append(ch[i]);
				}
				txt.append(tempstr.toString().trim());
			}

			if (metadata) {
				StringBuilder tempstr = new StringBuilder();
				for (int i = start; i < start + length; i++) {
					tempstr.append(ch[i]);
				}
				meta_txt.append(tempstr.toString().trim());
			}

		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			if (TAG_TEIHEADER.equals(qName)) {
				metadata = true;
			}

			else if (metadata) {
				// put text between tags into a map
				tei_metadata.push(qName);

				// put attribute keys and values into a map
				for (int i = 0; i < attributes.getLength(); i++) {
					tei_metadata.push_attribute_XPathMap(attributes.getQName(i), attributes.getValue(i));
				}
			}

			else if (TAG_TEXT.equals(qName)) {
				String namespace = retrieveNamespace(use_namespace, text_name);

				getTagStack().push(TAG_TEXT);
				// create STextualDS
				primaryText = SaltFactory.eINSTANCE.createSTextualDS();
				sDocGraph.addSNode(primaryText);
				insidetext = true;
				// represent the <text>-tag in Salt
				SStructure text_struc = SaltFactory.eINSTANCE.createSStructure();
				addDefaultAnnotation(text_struc, text_name, text_anno_value, namespace);

				addGenericAnno(text_struc, attributes, namespace);

				getSNodeStack().add(text_struc);
				sDocGraph.addSNode(text_struc);
			}

			else if (insidetext) {
				tokenizer = sDocGraph.createTokenizer();

				if (TAG_LB.equals(qName)) {
					generic_break(lb_name, lbSpanTokenStack, lb_anno_value);
				}

				else if (TAG_PB.equals(qName)) {
					generic_break(pb_name, pbSpanTokenStack, pb_anno_value);
				}

				else if (TAG_W.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, w_name);

					setToken(txt);
					getTagStack().push(TAG_W);
					if (!(default_tokenization && default_token_tag == TAG_W)) {
						SStructure w_struc = SaltFactory.eINSTANCE.createSStructure();
						addGenericAnno(w_struc, attributes, namespace);
						SWordAnnotation wordanno = SaltSemanticsFactory.eINSTANCE.createSWordAnnotation();
						w_struc.addSAnnotation(wordanno);

						if (attributes.getValue(ATT_TYPE) != null) {
							w_struc.createSAnnotation(namespace, ATT_TYPE, attributes.getValue(ATT_TYPE));
						}

						if (attributes.getValue(ATT_LEMMA) != null) {
							w_struc.createSAnnotation(namespace, ATT_LEMMA, attributes.getValue(ATT_LEMMA));
						}

						if (attributes.getValue(ATT_XML_LANG) != null) {
							w_struc.createSAnnotation(namespace, "lang", attributes.getValue(ATT_XML_LANG));
						}

						sDocGraph.addSNode(w_struc);
						setDominatingStruc(w_struc);
						getSNodeStack().add(w_struc);
					}

					else {
						SWordAnnotation wordanno = SaltSemanticsFactory.eINSTANCE.createSWordAnnotation();
						getSAnnoStack().add(wordanno);

						int length = attributes.getLength();
						for (int i = 0; i < length; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(name);
							tempanno.setValue(attributes.getValue(value));
							tempanno.setNamespace(namespace);
							getSAnnoStack().add(tempanno);
						}
					}

				}

				else if (TAG_PHR.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, phr_name);

					getTagStack().push(TAG_PHR);

					SStructure phr_struc = SaltFactory.eINSTANCE.createSStructure();
					addDefaultAnnotation(phr_struc, phr_name, phr_anno_value, namespace);
					addGenericAnno(phr_struc, attributes, namespace);
					sDocGraph.addSNode(phr_struc);
					setDominatingStruc(phr_struc);
					getSNodeStack().add(phr_struc);

				}

				else if (TAG_HEAD.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, body_head_name);

					if (!(getTagStack().peek() == TAG_FIGURE)) {
						getTagStack().push(TAG_HEAD);

						SStructure head_struc = SaltFactory.eINSTANCE.createSStructure();
						addDefaultAnnotation(head_struc, body_head_name, body_head_anno_value, namespace);
						addGenericAnno(head_struc, attributes, namespace);
						sDocGraph.addSNode(head_struc);
						setDominatingStruc(head_struc);
						getSNodeStack().add(head_struc);
					} else if (getTagStack().peek() == TAG_FIGURE) {

					}
				}

				else if (TAG_DIV.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, div_name);

					getTagStack().push(TAG_DIV);

					SStructure div_struc = SaltFactory.eINSTANCE.createSStructure();
					addDefaultAnnotation(div_struc, div_name, div_anno_value, namespace);
					addGenericAnno(div_struc, attributes, namespace);
					sDocGraph.addSNode(div_struc);
					setDominatingStruc(div_struc);
					getSNodeStack().add(div_struc);
				}

				else if (TAG_P.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, p_name);

					getTagStack().push(TAG_P);
					SStructure p_struc = SaltFactory.eINSTANCE.createSStructure();
					addDefaultAnnotation(p_struc, p_name, p_anno_value, namespace);
					addGenericAnno(p_struc, attributes, namespace);
					sDocGraph.addSNode(p_struc);
					setDominatingStruc(p_struc);
					getSNodeStack().add(p_struc);
				}

				else if (TAG_FOREIGN.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, foreign_name);
					if (foreign_as_token) {
						setToken(txt);
						getTagStack().push(TAG_FOREIGN);

						int length = attributes.getLength();
						for (int i = 0; i < length; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(name);
							tempanno.setValue(attributes.getValue(value));
							tempanno.setNamespace(namespace);
							getSAnnoStack().add(tempanno);
						}
					}
				}

				else if (TAG_FIGURE.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, figure_name);

					setToken(txt);
					getTagStack().push(TAG_FIGURE);

					SStructure figure_struc = SaltFactory.eINSTANCE.createSStructure();
					addDefaultAnnotation(figure_struc, figure_name, figure_anno_value, namespace);
					addGenericAnno(figure_struc, attributes, namespace);
					sDocGraph.addSNode(figure_struc);
					setDominatingStruc(figure_struc);
					getSNodeStack().add(figure_struc);

					setEmptyToken();
				}

				else if (TAG_M.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, figure_name);
					setToken(txt);
					getTagStack().push(TAG_M);

					int length = attributes.getLength();
					for (int i = 0; i < length; i++) {
						String name = attributes.getQName(i);
						String value = attributes.getValue(i);
						SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
						tempanno.setSName(name);
						tempanno.setValue(attributes.getValue(value));
						tempanno.setNamespace(namespace);
						getSAnnoStack().add(tempanno);
					}
				}

				else if (TAG_UNCLEAR.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, unclear_name);

					if (unclear_as_token) {
						setToken(txt);
						getTagStack().push(TAG_M);

						SWordAnnotation wordanno = SaltSemanticsFactory.eINSTANCE.createSWordAnnotation();
						getSAnnoStack().add(wordanno);

						SAnnotation wordanno2 = SaltFactory.eINSTANCE.createSAnnotation();
						wordanno2.setSName(TAG_UNCLEAR);
						getSAnnoStack().add(wordanno2);

						int length = attributes.getLength();
						for (int i = 0; i < length; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
							tempanno.setSName(name);
							tempanno.setValue(attributes.getValue(value));
							tempanno.setNamespace(namespace);
							getSAnnoStack().add(tempanno);
						}

					}
				}

				else if (TAG_SURPLUS.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, unclear_name);
					setToken(txt);

					int length = attributes.getLength();
					for (int i = 0; i < length; i++) {
						String name = attributes.getQName(i);
						String value = attributes.getValue(i);
						SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
						tempanno.setSName(name);
						tempanno.setValue(attributes.getValue(value));
						tempanno.setNamespace(namespace);
						getSAnnoStack().add(tempanno);
					}
				}

				else if (TAG_GAP.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, unclear_name);
					setToken(txt);
					getTagStack().push(TAG_GAP);

					int length = attributes.getLength();
					for (int i = 0; i < length; i++) {
						String name = attributes.getQName(i);
						String value = attributes.getValue(i);
						SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
						tempanno.setSName(name);
						tempanno.setValue(attributes.getValue(value));
						tempanno.setNamespace(namespace);
						getSAnnoStack().add(tempanno);
					}

					setGapToken();
				}

				else if (TAG_APP.equals(qName)) {

				}

				else if (TAG_BODY.equals(qName)) {
					// nothing
				}

				else if (generic_struct) {
					String namespace = retrieveNamespace(use_namespace, qName);

					getTagStack().push(qName);

					SStructure gen_struc = SaltFactory.eINSTANCE.createSStructure();
					addDefaultAnnotation(gen_struc, qName, qName, namespace);
					sDocGraph.addSNode(gen_struc);
					setDominatingStruc(gen_struc);
					getSNodeStack().add(gen_struc);

					if (generic_attr) {
						int length = attributes.getLength();
						for (int i = 0; i < length; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);

							SAnnotation tempAnno = sDocGraph.createSAnnotation(namespace, name, value);
							gen_struc.addSAnnotation(tempAnno);
						}

					}

				}

				else if (generic_span) {
					addToGenericSpans(qName, attributes);
				}

			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

			if (TAG_TEIHEADER.equals(qName)) {
				metadata = false;
				Map<String, String> custommappings = props.getMappingTable();
				Map<String, String> united = tei_metadata.uniteMappings(custommappings);
				Map<String, String> sineonesmap = tei_metadata.remove_ones(tei_metadata.getXPathMap());
				Map<String, String> completedmappings = tei_metadata.mapToXpathMap(sineonesmap, united, del_redundant_metadata);

				tei_metadata.add_to_SDoc(sDocGraph.getSDocument(), completedmappings, lastPart, excludeMetaSet);
			}

			else if (metadata) {
				tei_metadata.push_to_XPathMap(meta_txt.toString());
				meta_txt.setLength(0);

				tei_metadata.pop();
			}

			else if (insidetext) {

				if (TAG_W.equals(qName)) {
					setToken(txt);
					if (!(default_tokenization && default_token_tag == TAG_W)) {
						popNodeWithNoTokenCheck();
					}
				}

				else if (TAG_PHR.equals(qName)) {
					popNodeWithNoTokenCheck();
				}

				else if (TAG_HEAD.equals(qName)) {
					String namespace = retrieveNamespace(use_namespace, body_head_name);
					if ((getTagStack().peek() == TAG_HEAD)) {
						if (txt.length() > 0) {
							setToken(txt);
						} else {
							setEmptyToken();
						}

						popNodeWithNoTokenCheck();
					}
					if (getTagStack().peek() == TAG_FIGURE) {

						getSNodeStack().peek().createSAnnotation(namespace, body_head_name, txt.toString().trim());
						txt.setLength(0);
					}

				}

				else if (TAG_DIV.equals(qName)) {
					popNodeWithNoTokenCheck();
				}

				else if (TAG_P.equals(qName)) {
					if (insidetext) {
						setToken(txt);

						popNodeWithNoTokenCheck();
					}
				}

				else if (TAG_FOREIGN.equals(qName)) {
					setToken(txt);
				}

				else if (TAG_FIGURE.equals(qName)) {
					popNodeWithNoTokenCheck();
				}

				else if (TAG_M.equals(qName)) {
					SAnnotation tempanno = SaltFactory.eINSTANCE.createSAnnotation();
					tempanno.setSName("morpheme");
					getSAnnoStack().add(tempanno);

					setToken(txt);
				}

				else if (TAG_UNCLEAR.equals(qName)) {
					if (unclear_as_token) {
						if (txt.length() == 0) {
							SToken temp_tok = null;
							temp_tok = sDocGraph.createSToken(primaryText, primaryText.getSEnd(), primaryText.getSEnd());
							setDominatingToken(temp_tok);

							push_spans(temp_tok);

							while (!getSAnnoStack().isEmpty()) {

								if (temp_tok.getLabel(null, getSAnnoStack().peek().getName()) == null) {
									temp_tok.addSAnnotation(getSAnnoStack().pop());
								} else {
									getSAnnoStack().pop();
								}
							}
						}
						setToken(txt);
					}
				}

				else if (TAG_TEXT.equals(qName)) {
					insidetext = false;

					popNodeWithNoTokenCheck();
				}

				else if (TAG_SURPLUS.equals(qName)) {
					if (surplus_removal) {
						txt.setLength(0);
					} else {
						setToken(txt);
					}
				}

				else if (TAG_TITLE.equals(qName)) {
					// part of <head>
				}

				else if (TAG_GAP.equals(qName)) {

				}

				else if (TAG_APP.equals(qName)) {
					// popNodeWithNoTokenCheck();
				}

				else if (TAG_LEM.equals(qName)) {

				}

				else if (TAG_BODY.equals(qName)) {
					// nothing
				}

				/*
				 * these empty clauses must not be removed. everything not to be
				 * handled generically has to appear before the generic clauses
				 * could be called, otherwise elements with ungeneric handling
				 * would be considered as generic elements at least partially,
				 * causing bugs
				 */

				else if (TAG_LB.equals(qName)) {

				}

				else if (TAG_PB.equals(qName)) {

				}

				else if (generic_struct) {
					setToken(txt);
					popNodeWithNoTokenCheck();
				}

				else if (generic_span) {
					generic_break(qName, genericSpanMap.get(qName), qName);
				}
			}

			// remove adequate tag from TagStack
			if (!getTagStack().isEmpty())
				if (getTagStack().peek().equals(qName)) {
					getTagStack().pop();
				}
		}
	}
}
