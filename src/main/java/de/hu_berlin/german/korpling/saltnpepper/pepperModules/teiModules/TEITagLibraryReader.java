package de.hu_berlin.german.korpling.saltnpepper.pepperModules.teiModules;

import java.util.Stack;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

/**
 * This class parses an xml file following the model of 'BesaReader'.
 * 
 * @author XMLTagExtractor
 **/
public class TEITagLibraryReader extends DefaultHandler2 implements
		TEITagLibrary {
	//here are the options temporarily
	private Boolean USER_DEFINED_DEFAULT_TOKENIZATION = false;
	private Boolean SUB_TOKENIZATION = false;
	private Boolean NO_INPUT_TOKENIZATION = false;
	
	public void setUSER_DEFINED_DEFAULT_TOKENIZATION(){
		USER_DEFINED_DEFAULT_TOKENIZATION = true;
	}
	
	public void setSUB_TOKENIZATION(){
		SUB_TOKENIZATION = true;
	}
	
	public void setNO_INPUT_TOKENIZATION(){
		NO_INPUT_TOKENIZATION = true;
	}
	
	private String default_token_tag = TAG_W;
	
	private Boolean insidetext = false;
	
	private EList <STYPE_NAME> tokenrelation = new BasicEList<STYPE_NAME>();
	
	private Stack<SNode> sNodeStack= null;
	private Stack<SNode> getSNodeStack(){
		if (sNodeStack== null)
			sNodeStack= new Stack<SNode>();
		return(sNodeStack);
	}


	private Stack<String> TagStack = new Stack<String>();
	private Stack<String> getTagStack(){
		if (TagStack== null)
			TagStack= new Stack<String>();
		return(TagStack);
	}

	private SDocumentGraph sDocGraph = null;
	private STextualDS primaryText = null;
	

	private SLayer primaryLayer = SaltFactory.eINSTANCE.createSLayer();
	
	public SDocumentGraph getsDocGraph() {
		return sDocGraph;
	}

	public void setsDocGraph(SDocumentGraph sDocGraph) {
		this.sDocGraph = sDocGraph;
	}

	public void startDocument ()
    {
		sDocGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		sDocGraph.addSLayer(primaryLayer);
		primaryLayer.setSName("primary");
		tokenrelation.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
    }
	
	public void characters(char ch[], int start, int length) {
		if (SUB_TOKENIZATION && insidetext){
			String temp = "";
			for (int i = start; i < start + length; i++) {
					temp = temp + ch[i];
			}
			temp = temp.replaceAll("\\s+"," ");
			temp = temp.trim();
			if (primaryText != null){
				/*in case primaryText is empty, but exists, initialize primaryText with temp
				 *to avoid "null" as part of the string; otherwise add temp to primaryText
				 */
				if (!temp.isEmpty() && primaryText.getSText()==null){
					primaryText.setSText(temp);
					SToken temp_tok = sDocGraph.createSToken(primaryText, 0, primaryText.getSEnd());
					SStructure temp_struc = sDocGraph.createSStructure(temp_tok);
					System.out.println(sDocGraph.getSText(temp_struc));
				}
			
				/*add a single space character to split the first and last word from 
				 *two neighboring chunks of text*
				 */
				else if (!temp.isEmpty() && !(primaryText.getSText()==null)){
					int oldposition = primaryText.getSEnd();
					temp = " "+temp;
					primaryText.setSText(primaryText.getSText()+temp);
					SToken temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
					SStructure temp_struc = sDocGraph.createSStructure(temp_tok);
					System.out.println(sDocGraph.getSText(temp_struc));
					
				}
			}
		}
		
		if (USER_DEFINED_DEFAULT_TOKENIZATION && insidetext){
			if (TagStack.peek()==default_token_tag){
				String temp = "";
				for (int i = start; i < start + length; i++) {
						temp = temp + ch[i];
				}
				temp = temp.replaceAll("\\s+"," ");
				temp = temp.trim();
				if (primaryText != null){
					/*in case primaryText is empty, but exists, initialize primaryText with temp
					 *to avoid "null" as part of the string; otherwise add temp to primaryText
					 */
					if (!temp.isEmpty() && primaryText.getSText()==null){
						primaryText.setSText(temp);
						SToken temp_tok = sDocGraph.createSToken(primaryText, 0, primaryText.getSEnd());
						SStructure temp_struc = sDocGraph.createSStructure(temp_tok);
						System.out.println(sDocGraph.getSText(temp_struc));
					}
				
					/*add a single space character to split the first and last word from 
					 *two neighboring chunks of text*
					 */
					else if (!temp.isEmpty() && !(primaryText.getSText()==null)){
						int oldposition = primaryText.getSEnd();
						temp = " "+temp;
						primaryText.setSText(primaryText.getSText()+temp);
						SToken temp_tok = sDocGraph.createSToken(primaryText, oldposition, primaryText.getSEnd());
						SStructure temp_struc = sDocGraph.createSStructure(temp_tok);
						System.out.println(sDocGraph.getSText(temp_struc));
						
					}
				}
			}
		}
			
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (TAG_LB.equals(qName)) {
			
		}
		
		else if (TAG_W.equals(qName)) {
			TagStack.push(TAG_W);
			
		}
		
		else if (TAG_PHR.equals(qName)) {
			
		}
		
		else if (TAG_HEAD.equals(qName)) {
			
		}
		
		else if (TAG_DIV.equals(qName)) {
			
		}
		
		else if (TAG_P.equals(qName)) {
			TagStack.push(TAG_P);
		}
		
		else if (TAG_FOREIGN.equals(qName)) {
			
		}
		
		else if (TAG_PB.equals(qName)) {
			
		}
		
		else if (TAG_FIGURE.equals(qName)) {
			
		}
		
		else if (TAG_M.equals(qName)) {
			
		}
		
		else if (TAG_UNCLEAR.equals(qName)) {
			
		}
		
		else if (TAG_SURPLUS.equals(qName)) {
			
		}
		
		else if (TAG_TITLE.equals(qName)) {
			
		}
		
		else if (TAG_GAP.equals(qName)) {
			
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
			primaryText = SaltFactory.eINSTANCE.createSTextualDS();
			sDocGraph.addSNode(primaryText);
			insidetext = true;
			SStructure textnode = SaltFactory.eINSTANCE.createSStructure();
			sDocGraph.addSNode(textnode);
			getSNodeStack().add(textnode);
			
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
		if (TAG_LB.equals(qName)) {
			
		}
		
		else if (TAG_W.equals(qName)) {
			TagStack.pop();
			
		}
		
		else if (TAG_PHR.equals(qName)) {
			
		}
		
		else if (TAG_HEAD.equals(qName)) {
			
		}
		
		else if (TAG_DIV.equals(qName)) {
			
		}
		
		else if (TAG_P.equals(qName)) {
			TagStack.pop();
		}
		
		else if (TAG_FOREIGN.equals(qName)) {
			
		}
		
		else if (TAG_PB.equals(qName)) {
			
		}
		
		else if (TAG_FIGURE.equals(qName)) {
			
		}
		
		else if (TAG_M.equals(qName)) {
			
		}
		
		else if (TAG_UNCLEAR.equals(qName)) {
			
		}
		
		else if (TAG_SURPLUS.equals(qName)) {
			
		}
		
		else if (TAG_TITLE.equals(qName)) {
			
		}
		
		else if (TAG_GAP.equals(qName)) {
			
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
		if (!getTagStack().isEmpty())
			if (getTagStack().peek().equals(qName)) {
				getTagStack().pop();
			}
	}
}
