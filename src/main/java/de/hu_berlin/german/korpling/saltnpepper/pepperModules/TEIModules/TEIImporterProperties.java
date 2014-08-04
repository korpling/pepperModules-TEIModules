package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Hashtable;
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
	public static final String PROP_RENAME_TAG = "tag.rename";
	
	//tag naming config
	public final String PROP_LB_NAME = "LbName";
	public final String PROP_PB_NAME = "PbName";
	public final String PROP_W_NAME = "WName";
	public final String PROP_PHR_NAME = "PhrName";
	public final String PROP_BODYHEAD_NAME = "BodyHeadName";
	public final String PROP_DIV_NAME = "DivName";
	public final String PROP_P_NAME = "PName";
	public final String PROP_FOREIGN_NAME = "ForeignName";
	public final String PROP_FIGURE_NAME = "FigureName";
	public final String PROP_M_NAME = "MName";
	public final String PROP_UNCLEAR_NAME = "UnclearName";
	public final String PROP_SURPLUS_NAME = "SurplusName";
	public final String PROP_TITLE_NAME = "TitleName";
	public final String PROP_GAP_NAME = "GapName";
	public final String PROP_APP_NAME = "AppName";
	//other tags to be added here in the future
	public final String PROP_TEXT_NAME = "TextName";
	
	//annotation value config spans
	public final String PROP_LB_ANNO_VALUE = "LbAnnoValue";
	public final String PROP_PB_ANNO_VALUE = "PbAnnoValue";
	
	//annotation value config strucs
	public final String PROP_PHR_ANNO_VALUE = "PhrAnnoValue";
	public final String PROP_BODYHEAD_ANNO_VALUE = "BodyHeadAnnoValue";
	public final String PROP_DIV_ANNO_VALUE = "DivAnnoValue";
	public final String PROP_P_ANNO_VALUE = "PAnnoValue";
	public final String PROP_FIGURE_ANNO_VALUE = "FigureAnnoValue";
	public final String PROP_APP_ANNO_VALUE = "AppAnnoValue";
	public final String PROP_TEXT_ANNO_VALUE = "TextAnnoValue";
			
	
	
	
	
	//registration of properties
	public TEIImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_USER_DEFINED_DEFAULT_TOKENIZATION, Boolean.class, "The user declares the element responsible for tokenization. Default is <w> in this mode.", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SUB_TOKENIZATION, Boolean.class, "In this scenario, units smaller than ‘words’ exist. Elements within <w> etc. are possible", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_NO_INPUT_TOKENIZATION, Boolean.class, "Ask the tokenizer module to tokenize running text gathered according to same strategy as in SubTokenization", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SURPLUS_REMOVAL, Boolean.class, "Will text from <surplus> appear in Salt?", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_UNCLEAR_AS_TOKEN, Boolean.class, "Does <unclear> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_FOREIGN_AS_TOKEN, Boolean.class, "Does <foreign> exclusively include one token?", true, false));
		
		addProperty(new PepperModuleProperty<String>(PROP_LB_NAME, String.class, "Name of <lb>-annotation", "lb", false));
		addProperty(new PepperModuleProperty<String>(PROP_PB_NAME, String.class, "Name of <pb>-annotation", "pb", false));
		addProperty(new PepperModuleProperty<String>(PROP_W_NAME, String.class, "Name of <w>-annotation", "w", false));
		addProperty(new PepperModuleProperty<String>(PROP_PHR_NAME, String.class, "Name of <phr>-annotation", "phr", false));
		addProperty(new PepperModuleProperty<String>(PROP_BODYHEAD_NAME, String.class, "Name of <head>-annotation inside of <body>", "body", false));
		addProperty(new PepperModuleProperty<String>(PROP_DIV_NAME, String.class, "Name of <div>-annotation", "div", false));
		addProperty(new PepperModuleProperty<String>(PROP_P_NAME, String.class, "Name of <p>-annotation", "p", false));
		addProperty(new PepperModuleProperty<String>(PROP_FOREIGN_NAME, String.class, "Name of <foreign>-annotation", "foreign", false));
		addProperty(new PepperModuleProperty<String>(PROP_FIGURE_NAME, String.class, "Name of <figure>-annotation", "figure", false));
		addProperty(new PepperModuleProperty<String>(PROP_M_NAME, String.class, "Name of <m>-annotation", "m", false));
		addProperty(new PepperModuleProperty<String>(PROP_UNCLEAR_NAME, String.class, "Name of <unclear>-annotation", "unclear", false));
		addProperty(new PepperModuleProperty<String>(PROP_SURPLUS_NAME, String.class, "Name of <surplus>-annotation", "surplus", false));
		addProperty(new PepperModuleProperty<String>(PROP_TITLE_NAME, String.class, "Name of <title>-annotation", "title", false));
		addProperty(new PepperModuleProperty<String>(PROP_GAP_NAME, String.class, "Name of <gap>-annotation", "gap", false));
		addProperty(new PepperModuleProperty<String>(PROP_APP_NAME, String.class, "Name of <app>-annotation", "app", false));
		//other tags to be added here in the future
		addProperty(new PepperModuleProperty<String>(PROP_TEXT_NAME, String.class, "Name of <text>-annotation", "text", false));
		
		//annotation value config spans
		addProperty(new PepperModuleProperty<String>(PROP_LB_ANNO_VALUE, String.class, "default value for <lb>-annotation", "lb", false));
		addProperty(new PepperModuleProperty<String>(PROP_PB_ANNO_VALUE, String.class, "default value for <pb>-annotation", "pb", false));
		
		//annotation value config strucs
		addProperty(new PepperModuleProperty<String>(PROP_PHR_ANNO_VALUE, String.class, "default value for <phr>-annotation", "phr", false));
		addProperty(new PepperModuleProperty<String>(PROP_BODYHEAD_ANNO_VALUE, String.class, "default value for <head>-annotation inside of <body>", "head", false));
		addProperty(new PepperModuleProperty<String>(PROP_DIV_ANNO_VALUE, String.class, "default value for <div>-annotation", "div", false));
		addProperty(new PepperModuleProperty<String>(PROP_P_ANNO_VALUE, String.class, "default value for <p>-annotation", "p", false));
		addProperty(new PepperModuleProperty<String>(PROP_FIGURE_ANNO_VALUE, String.class, "default value for <figure>-annotation", "figrue", false));
		addProperty(new PepperModuleProperty<String>(PROP_APP_ANNO_VALUE, String.class, "default value for <app>-annotation", "app", false));
		addProperty(new PepperModuleProperty<String>(PROP_TEXT_ANNO_VALUE, String.class, "default value for <text>-annotation", "text", false));
	}
	
	//functions to retrieve property values
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
			if (propO!= null){
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
}
