package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Stack;

public class TEIImporterMetaData {
	private Stack<String> pathStack = new Stack<String>();
	
	public TEIImporterMetaData(){
		
	}
	
	public void push(String str){
		pathStack.push(str);
	}
	
	public String pop(){
		return(pathStack.pop());
	}
	
}

