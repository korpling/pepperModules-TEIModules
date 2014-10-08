/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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
import java.util.LinkedList;
import java.util.Map;

import com.neovisionaries.i18n.LanguageCode;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class TEIImporterProperties extends PepperModuleProperties{
	//String values for properties
	public static final String PROP_DEFAULT_TOKENIZATION = "DefaultTokenization";
	public static final String PROP_SUB_TOKENIZATION = "SubTokenization";
	
	public static final String PROP_SURPLUS_REMOVAL = "SurplusRemoval";
	public static final String PROP_UNCLEAR_AS_TOKEN = "UnclearAsToken";
	public static final String PROP_FOREIGN_AS_TOKEN = "ForeignAsToken";
	
	public static final String PROP_USE_TOKENIZER = "UseTokenizer";
	public static final String PROP_USE_TOKENIZER_LANG = "UseTokenizerLang";
	
	public static final String PROP_DELETE_REDUNDANT_METADATA = "DeleteRedundantMetadata";
	public static final String PROP_SKIP_DEFAULT_ANNOTATIONS = "SkipDefaultAnnotations";
	
	public static final String PROP_RENAME_TAG = "tag.rename";
	public static final String PROP_RENAME_VALUES = "values.rename";
	public static final String PROP_MAPPINGS = "mapping.rename";

	
	
	/**
	 * constructor that also adds the properties 
	 */
	public TEIImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_DEFAULT_TOKENIZATION, Boolean.class, "One element is responsible for tokenization. Default is <w> in this mode.", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SUB_TOKENIZATION, Boolean.class, "In this scenario, units smaller than ‘words’ exist. Elements within <w> etc. are possible", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SURPLUS_REMOVAL, Boolean.class, "Will text from <surplus> appear in Salt?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_UNCLEAR_AS_TOKEN, Boolean.class, "Does <unclear> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_FOREIGN_AS_TOKEN, Boolean.class, "Does <foreign> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_USE_TOKENIZER, Boolean.class, "Do you want to tokenize the text?", false, false));
		addProperty(new PepperModuleProperty<String>(PROP_USE_TOKENIZER_LANG, String.class, "What language do you want to use for tokenization? (ISO 639-1 code)", false));
		
		addProperty(new PepperModuleProperty<Boolean>(PROP_DELETE_REDUNDANT_METADATA, Boolean.class, "Do you want metadata with a custom mapping to appear only once?", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SKIP_DEFAULT_ANNOTATIONS, Boolean.class, "Do you want to remove default annotations(e.g. p=p)?", false, false));
		
		addProperty(new PepperModuleProperty<String>(PROP_RENAME_TAG, String.class, "String containing the tag renaming configuration set by the user", "", false));
		addProperty(new PepperModuleProperty<String>(PROP_RENAME_VALUES, String.class, "String containing the value renaming configuration set by the user", "", false));
		
		addProperty(new PepperModuleProperty<String>(PROP_MAPPINGS, String.class, "String containing the metadata mappings set by the user", "", false));
	}
	
	/**
	 * method to retrieve value of default tokenization property
	 * @return boolean value set by the user(or default)
	 */
	public boolean isDefaultTokenization(){
		boolean retVal = false;
		String prop = getProperty(PROP_DEFAULT_TOKENIZATION).getValue().toString();
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
		String prop = getProperty(PROP_SUB_TOKENIZATION).getValue().toString();
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
		String prop = getProperty(PROP_SURPLUS_REMOVAL).getValue().toString();
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
		String prop = getProperty(PROP_UNCLEAR_AS_TOKEN).getValue().toString();
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
		String prop = getProperty(PROP_FOREIGN_AS_TOKEN).getValue().toString();
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
		String prop = getProperty(PROP_USE_TOKENIZER).getValue().toString();
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
		String prop = getProperty(PROP_DELETE_REDUNDANT_METADATA).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	/**
	 * method to retrieve value of skip default annotations
	 * @return boolean value set by the user(or default)
	 */
	public boolean isSkipAnnotations(){
		boolean retVal = false;
		String prop = getProperty(PROP_SKIP_DEFAULT_ANNOTATIONS).getValue().toString();
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
		Object propO = getProperty(PROP_USE_TOKENIZER_LANG).getValue();
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
			Object propO = getProperty(PROP_RENAME_TAG).getValue();
			
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
			Object propO = getProperty(PROP_RENAME_VALUES).getValue();
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
			
			if (getProperty(PROP_MAPPINGS)!= null){
				propO = getProperty(PROP_MAPPINGS).getValue();
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
}
