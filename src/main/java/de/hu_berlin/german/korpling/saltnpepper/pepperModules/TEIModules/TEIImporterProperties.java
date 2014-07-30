package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

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
	
	public static final String PROP_LB_NAME = "ForeignAsToken";
	public static final String PROP_PB_NAME = "ForeignAsToken";
	public static final String PROP_W_NAME = "ForeignAsToken";
	public static final String PROP_PHR_NAME = "ForeignAsToken";
	public static final String PROP_BODYHEAD_NAME = "ForeignAsToken";
	public static final String PROP_DIV_NAME = "ForeignAsToken";
	public static final String PROP_P_NAME = "ForeignAsToken";
	public static final String PROP_FOREIGN_NAME = "ForeignAsToken";
	public static final String PROP_FIGURE_NAME = "ForeignAsToken";
	public static final String PROP_M_NAME = "ForeignAsToken";
	public static final String PROP_UNCLEAR_NAME = "ForeignAsToken";
	public static final String PROP_SURPLUS_NAME = "ForeignAsToken";
	public static final String PROP_TITLE_NAME = "ForeignAsToken";
	public static final String PROP_GAP_NAME = "ForeignAsToken";
	public static final String PROP_APP_NAME = "ForeignAsToken";
	//other tags to be added here in the future
	public static final String PROP_TEXT_NAME = "ForeignAsToken";
	
	
	
	//registration of properties
	public TEIImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_USER_DEFINED_DEFAULT_TOKENIZATION, Boolean.class, "The user declares the element responsible for tokenization. Default is <w> in this mode.", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SUB_TOKENIZATION, Boolean.class, "In this scenario, units smaller than ‘words’ exist. Elements within <w> etc. are possible", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_NO_INPUT_TOKENIZATION, Boolean.class, "Ask the tokenizer module to tokenize running text gathered according to same strategy as in SubTokenization", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SURPLUS_REMOVAL, Boolean.class, "Will text from <surplus> appear in Salt?", false, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_UNCLEAR_AS_TOKEN, Boolean.class, "Does <unclear> exclusively include one token?", true, false));
		addProperty(new PepperModuleProperty<Boolean>(PROP_FOREIGN_AS_TOKEN, Boolean.class, "Does <foreign> exclusively include one token?", true, false));
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
}
