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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.neovisionaries.i18n.LanguageCode;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class TEIImporterProperties extends PepperModuleProperties{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2871690146180228706L;
	//String values for properties
	public static final String PROP_TOKEN_TOKENIZATION_SUB = "token.tokenization.sub";
	public static final String PROP_TOKEN_TOKENIZATION_DEFAULTTAG = "token.tokenization.defaulttag";
	
	public static final String PROP_ELEMENT_SURPLUS_REMOVE = "element.surplus.remove";
	public static final String PROP_ELEMENT_UNCLEAR_TOKEN = "element.unclear.token";
	public static final String PROP_ELEMENT_FOREIGN_TOKEN = "element.foreign.token";
	
	public static final String PROP_TOKEN_TOKENIZE = "token.tokenize";
	public static final String PROP_TOKEN_TOKENIZE_LANG = "token.tokenize.lang";
	
	public static final String PROP_METADATA_REDUNDANT_REMOVE = "metadata.redundant.remove";
	public static final String PROP_ANNOTATION_DEFAULT_REMOVE = "annotation.default.remove";
	public static final String PROP_ANNOTATION_NAMESPACE = "annotation.namespace";
	
	public static final String PROP_ANNOTATION_ELEMENT_RENAME = "annotation.element.rename";
	public static final String PROP_ANNOTATION_VALUE_RENAME = "annotation.value.rename";
	public static final String PROP_METADATA_RENAME = "metadata.rename";
	
	public static final String PROP_ELEMENT_GENERIC_NODE = "element.generic.node";
	public static final String PROP_ELEMENT_GENERIC_ATTRIBUTE = "element.generic.attribute";
	
	public static final String PROP_ANNOTATION_TOKEN_SPAN = "annotation.token.span";
	
	public static final String PROP_METADATA_LASTPARTONLY = "metadata.lastpartonly";
	public static final String PROP_METADATA_REMOVE = "metadata.remove";
	public static final String PROP_METADATA_REMOVE_LIST = "metadata.remove.list";

	
	
	/**
	 * constructor that also adds the properties 
	 */
	public TEIImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_TOKEN_TOKENIZATION_SUB, Boolean.class, "In this scenario, units smaller than ‘words’ exist. Elements within <w> etc. are possible", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_TOKEN_TOKENIZATION_DEFAULTTAG, Boolean.class, "One element is responsible for tokenization. Default is <w> in this mode.", false, false));
		
		addProperty(new PepperModuleProperty<Boolean>(PROP_ELEMENT_SURPLUS_REMOVE, Boolean.class, "Will text from <surplus> appear in Salt?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_ELEMENT_UNCLEAR_TOKEN, Boolean.class, "Does <unclear> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_ELEMENT_FOREIGN_TOKEN, Boolean.class, "Does <foreign> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_TOKEN_TOKENIZE, Boolean.class, "Do you want to tokenize the text?", false, false));
		addProperty(new PepperModuleProperty<String>(PROP_TOKEN_TOKENIZE_LANG, String.class, "What language do you want to use for tokenization? (ISO 639-1 code)", false));
		
		addProperty(new PepperModuleProperty<Boolean>(PROP_METADATA_REDUNDANT_REMOVE, Boolean.class, "Do you want metadata with a custom mapping to appear only once?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_ANNOTATION_DEFAULT_REMOVE, Boolean.class, "Do you want to remove default annotations(e.g. p=p)?", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_ANNOTATION_NAMESPACE, Boolean.class, "Do you want to add the tag-namespace to SAnnotations?", false, false));
		
		addProperty(new PepperModuleProperty<String>(PROP_ELEMENT_GENERIC_NODE, String.class, "Do you want generic nodes? And if yes what kind?", "struct", false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_ELEMENT_GENERIC_ATTRIBUTE, Boolean.class, "Do you want to include generic attribute annotations?", false, false));
		
		addProperty(new PepperModuleProperty<Boolean>(PROP_ANNOTATION_TOKEN_SPAN, Boolean.class, "Do you want to have spans for all token annotations?", false, false));
		
		addProperty(new PepperModuleProperty<String>(PROP_ANNOTATION_ELEMENT_RENAME, String.class, "String containing the tag renaming configuration set by the user", "", false));
		addProperty(new PepperModuleProperty<String>(PROP_ANNOTATION_VALUE_RENAME, String.class, "String containing the value renaming configuration set by the user", "", false));
		
		addProperty(new PepperModuleProperty<String>(PROP_METADATA_RENAME, String.class, "String containing the metadata mappings set by the user", "", false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_METADATA_LASTPARTONLY, Boolean.class, "Do you want to remove everything from metadata but what is after the last '/'?", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_METADATA_REMOVE, Boolean.class, "Do you want to exclude metadata with keys defined in ExcludeMetadataList?", false, false));
		addProperty(new PepperModuleProperty<String>(PROP_METADATA_REMOVE_LIST, String.class, "List of keys of metadata to be omitted.", "", false));
	}
	
	/**
	 * method to retrieve value of default tokenization property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isDefaultTokenization(){
		boolean retVal = false;
		String prop = getProperty(PROP_TOKEN_TOKENIZATION_DEFAULTTAG).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of sub tokenization property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isSubTokenization(){
		boolean retVal = false;
		String prop = getProperty(PROP_TOKEN_TOKENIZATION_SUB).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of surplus removal property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isSurplusRemoval(){
		boolean retVal = false;
		String prop = getProperty(PROP_ELEMENT_SURPLUS_REMOVE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of unclear as token property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUnclearAsToken(){
		boolean retVal = false;
		String prop = getProperty(PROP_ELEMENT_UNCLEAR_TOKEN).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of foreign as token property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isForeignAsToken(){
		boolean retVal = false;
		String prop = getProperty(PROP_ELEMENT_FOREIGN_TOKEN).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of use tokenizer property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseTokenizer(){
		boolean retVal = false;
		String prop = getProperty(PROP_TOKEN_TOKENIZE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of delete redundant metadata property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isDelMetadata(){
		boolean retVal = false;
		String prop = getProperty(PROP_METADATA_REDUNDANT_REMOVE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of skip_default_annotaions
	 * @return boolean value set by the user(or default)
	 */
	public boolean isSkipAnnotations(){
		boolean retVal = false;
		String prop = getProperty(PROP_ANNOTATION_DEFAULT_REMOVE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of use_namespace
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseNamespace(){
		boolean retVal = false;
		String prop = getProperty(PROP_ANNOTATION_NAMESPACE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of generic.struct
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseGenericStruct(){
		boolean retVal = false;
		String prop = getProperty(PROP_ELEMENT_GENERIC_NODE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			if (prop.equals("struct")){
				retVal = true;
			}
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of generic.span
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseGenericSpan(){
		boolean retVal = false;
		String prop = getProperty(PROP_ELEMENT_GENERIC_NODE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			if (prop.equals("span")){
				retVal = true;
			}
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of generic.attributes
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseGenericAttr(){
		boolean retVal = false;
		String prop = getProperty(PROP_ELEMENT_GENERIC_ATTRIBUTE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of anno_token_span
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseTokenAnnoSpan(){
		boolean retVal = false;
		String prop = getProperty(PROP_ANNOTATION_TOKEN_SPAN).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of LastPartOnlyMetadata
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseLastPart(){
		boolean retVal = false;
		String prop = getProperty(PROP_METADATA_LASTPARTONLY).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of LastPartOnlyMetadata
	 * @return boolean value set by the user(or default)
	 */
	public boolean isUseExcludeMetadata(){
		boolean retVal = false;
		String prop = getProperty(PROP_METADATA_REMOVE).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of the custom annotation string
	 * @return values set by the user(or default)
	 */
	public String customAnnotationString(String param){
		String retVal = "";
		Object propO = getProperty(param).getValue();
		String prop= null;
		if (propO!= null){
			prop= propO.toString();
		}
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = prop;
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of the tokenization language
	 * @return value set by the user(or default)
	 */
	private String tokenizer_lang(){
		String retVal = "";
		Object propO = getProperty(PROP_TOKEN_TOKENIZE_LANG).getValue();
		String prop= null;
		if (propO!= null){
			prop= propO.toString();
		}
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = prop;
		}
		LinkedList<String> langlist = new LinkedList<String>();
		langlist.push("de");
		langlist.push("en");
		langlist.push("fr");
		langlist.push("it");
		
		if (langlist.contains(retVal)){
			return retVal;
		}
		
		//English is the default language of tokenization
		else{
			return("en");
		}
	}
	
	/**
	 * returns a language code that is supported by the tokenizer
	 * defaults to "en" if the user used a unsupported value
	 * @return supported language code)
	 */
	public LanguageCode tokenizer_code(){
		Map<String, LanguageCode> langmap = new Hashtable<>();
		langmap.put("de", LanguageCode.de);
		langmap.put("en", LanguageCode.en);
		langmap.put("fr", LanguageCode.fr);
		langmap.put("it", LanguageCode.it);
		
		return (langmap.get(tokenizer_lang()));
	}
	
	/**
	 * map containing tag renamings
	 */
	private Map<String, String> tagRenameTable= null;
	
	/**
	 * gets the name in the tagRenameTable fitting the wanted lookup
	 * @param tagName name to look up
	 * @return the demanded string from the tagRenameTable
	 */
	public String getTagName(String tagName){
		if (tagRenameTable== null){
			tagRenameTable= new Hashtable<>();
			Object propO = getProperty(PROP_ANNOTATION_ELEMENT_RENAME).getValue();
			
			String prop= null;
			if (propO.toString().trim()!= ""){
				prop= propO.toString();
				String[] renameParts= prop.split(";");
				for (String part: renameParts){
					String[] attVal= part.split(":");
					tagRenameTable.put(attVal[0], attVal[1]);
				}
			}
		}
		
		String retVal= tagRenameTable.get(tagName);
		if (retVal== null){
			retVal= tagName;
		}
		return(retVal);
		
	}
	
	/**
	 * map containing tag-value renamings
	 */
	private Map<String, String> tagRenameValuesTable= null;
	
	/**
	 * gets the name in the tagRenameValuesTable fitting the wanted lookup
	 * @param tagName name to look up
	 * @return the demanded string from the tagRenameValuesTable
	 */
	public String getValuesName(String tagName){
		if (tagRenameValuesTable== null){
			tagRenameValuesTable= new Hashtable<>();
			Object propO = getProperty(PROP_ANNOTATION_VALUE_RENAME).getValue();
			String prop= null;
			if (propO.toString().trim()!= ""){
				prop= propO.toString();
				String[] renameParts= prop.split(";");
				for (String part: renameParts){
					String[] attVal= part.split(":");
					tagRenameValuesTable.put(attVal[0], attVal[1]);
				}
			}
		}
		
		String retVal= tagRenameValuesTable.get(tagName);
		if (retVal== null){
			retVal= tagName;
		}
		return(retVal);
		
	}
	
	/**
	 * list containing the mappings set by the user
	 */
	private Map<String,String> mappingTable = null;
	
	/**
	 * gets the mappingTable
	 * @return mappingTable
	 */
	public Map<String, String> getMappingTable() {
		return mappingTable;
	}

	/**
	 * gets the name in the mapping mappingTable fitting the wanted lookup
	 * @param mappingString name to look up
	 * @return the demanded string from the mappingTable
	 */
	public String getMappings(String mappingString){
		String retVal= mappingTable.get(mappingString);

		return(retVal);
	}
	
	/**
	 * uses the customized mapping set by the user to fill mappingTable
	 */
	public void fillMappings(){
		if (mappingTable== null){
			mappingTable= new Hashtable<>();
			Object propO = null;
			
			if (getProperty(PROP_METADATA_RENAME)!= null){
				propO = getProperty(PROP_METADATA_RENAME).getValue();
			}
			else{
				propO = "";
			}
			
			String prop= null;
			if (propO.toString().trim()!= ""){
				prop= propO.toString();
				String[] renameParts= prop.split(";");
				for (String part: renameParts){
					String[] attVal= part.split(":");
					mappingTable.put(attVal[0], attVal[1]);
				}
			}
		}
		
	}
	
	public Set<String> retrieveExcludeMetadataSet(){
		Object propO = null;
		Set<String> excludeSet = null;
		if (getProperty(PROP_METADATA_REMOVE_LIST)!= null){
			propO = getProperty(PROP_METADATA_REMOVE_LIST).getValue();
			String prop = propO.toString();
			String[] propList = prop.split(";");
			excludeSet = new HashSet<>(Arrays.asList(propList));
		}
		return(excludeSet);
	}
}
