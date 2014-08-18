package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Map;
import java.util.Stack;

public class TEIImporterMetaData {
	private Stack<String> pathStack = new Stack<String>();
	private Map<String, String> xPathMap= null;

	
	public TEIImporterMetaData(){
		
	}
	
	public void push(String str){
		pathStack.push(str);
	}
	
	public String pop(){
		return(pathStack.pop());
	}
	
	public String getxPath(String str){
		int size = pathStack.size();
		String xpath = "";
		for (int i=0; i<size;i++){
			xpath = xpath + "/" + pathStack.get(i);
		}
		return(xpath);
	}
}

