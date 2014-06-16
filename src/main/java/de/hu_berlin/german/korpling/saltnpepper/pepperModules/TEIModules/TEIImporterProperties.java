package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class TEIImporterProperties extends PepperModuleProperties{
	public static final String PROP_USER_DEFINED_DEFAULT_TOKENIZATION = "UserDefinedDefaultTokenization";
	public static final String PROP_SUB_TOKENIZATION = "SubTokenization";
	public static final String PROP_NO_INPUT_TOKENIZATION = "NoInputTokenization";
	
	public static final String PROP_SURPLUS_REMOVAL = "SurplusRemoval";
	
	public TEIImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_USER_DEFINED_DEFAULT_TOKENIZATION, Boolean.class, "The user declares the element responsible for tokenization. Default is <w> in this mode.", false, true));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SUB_TOKENIZATION, Boolean.class, "In this scenario, units smaller than ‘words’ exist. Elements within <w> etc. are possible", false, true));
		addProperty(new PepperModuleProperty<Boolean>(PROP_NO_INPUT_TOKENIZATION, Boolean.class, "Ask the tokenizer module to tokenize running text gathered according to same strategy as in SubTokenization", false, true));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SURPLUS_REMOVAL, Boolean.class, "Will text from <surplus> appear in Salt?", false, false));
	}
	
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
}
