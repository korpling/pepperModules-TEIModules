package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class TEIImporterProperties extends PepperModuleProperties{
	//String values for properties
	public static final String PROP_USER_DEFINED_DEFAULT_TOKENIZATION = "UserDefinedDefaultTokenization";
	public static final String PROP_SUB_TOKENIZATION = "SubTokenization";
	public static final String PROP_NO_INPUT_TOKENIZATION = "NoInputTokenization";
	
	public static final String PROP_SURPLUS_REMOVAL = "SurplusRemoval";
	public static final String PROP_UNCLEAR_AS_TOKEN = "UnclearAsToken";
	public static final String PROP_FOREIGN_AS_TOKEN = "ForeignAsToken";
	
	public static final String PROP_USE_TOKENIZER = "UseTokenizer";
	public static final String PROP_USE_TOKENIZER_LANG = "UseTokenizerLang";
	
	public static final String PROP_RENAME_TAG = "tag.rename";
	public static final String PROP_RENAME_VALUES = "values.rename";

	
	
	//registration of properties
	public TEIImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_USER_DEFINED_DEFAULT_TOKENIZATION, Boolean.class, "The user declares the element responsible for tokenization. Default is <w> in this mode.", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SUB_TOKENIZATION, Boolean.class, "In this scenario, units smaller than ‘words’ exist. Elements within <w> etc. are possible", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_NO_INPUT_TOKENIZATION, Boolean.class, "Ask the tokenizer module to tokenize running text gathered according to same strategy as in SubTokenization", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SURPLUS_REMOVAL, Boolean.class, "Will text from <surplus> appear in Salt?", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_UNCLEAR_AS_TOKEN, Boolean.class, "Does <unclear> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_FOREIGN_AS_TOKEN, Boolean.class, "Does <foreign> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_USE_TOKENIZER, Boolean.class, "Do you want to tokenize the text?", false, false));
		addProperty(new PepperModuleProperty<String>(PROP_USE_TOKENIZER_LANG, String.class, "What language do you want to use for tokenization? (ISO 639-1 code)", false));
		
		addProperty(new PepperModuleProperty<String>(PROP_RENAME_TAG, String.class, "String containing the tag renaming configuration set by the user", "", false));
		addProperty(new PepperModuleProperty<String>(PROP_RENAME_VALUES, String.class, "String containing the value renaming configuration set by the user", "", false));
	}
	
	//methods to retrieve property values
	public boolean isUserDefinedDefaultTokenization(){
		boolean retVal = false;
		String prop = getProperty(PROP_USER_DEFINED_DEFAULT_TOKENIZATION).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isSubTokenization(){
		boolean retVal = false;
		String prop = getProperty(PROP_SUB_TOKENIZATION).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isNoInputTokenization(){
		boolean retVal = false;
		String prop = getProperty(PROP_NO_INPUT_TOKENIZATION).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isSurplusRemoval(){
		boolean retVal = false;
		String prop = getProperty(PROP_SURPLUS_REMOVAL).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isUnclearAsToken(){
		boolean retVal = false;
		String prop = getProperty(PROP_UNCLEAR_AS_TOKEN).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isForeignAsToken(){
		boolean retVal = false;
		String prop = getProperty(PROP_FOREIGN_AS_TOKEN).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isUseTokenizer(){
		boolean retVal = false;
		String prop = getProperty(PROP_USE_TOKENIZER).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
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
	
	public String languageString(String param){
		String retVal = "";
		Object propO = getProperty(param).getValue();
		String prop= null;
		if (propO!= null){
			prop= propO.toString();
		}
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = prop;
		}
		LinkedList<String> langlist = new LinkedList<>();
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
	
	
	private Map<String, String> tagRenameTable= null;
	/**
	 * 
	 * @param tagName
	 * @return
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
	
	private Map<String, String> tagRenameValuesTable= null;
	/**
	 * 
	 * @param tagName
	 * @return
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
}
