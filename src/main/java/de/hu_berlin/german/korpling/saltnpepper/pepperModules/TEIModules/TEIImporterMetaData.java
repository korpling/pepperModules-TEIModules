package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

public class TEIImporterMetaData {
	private Stack<String> pathStack = new Stack<String>();
	private Map<String, String> XPathMap= null;

	
	public TEIImporterMetaData(){
		
	}
	
	public void push(String str){
		pathStack.push(str);
	}
	
	public String pop(){
		return(pathStack.pop());
	}
	
	public void putXPathText(String str){
		if (XPathMap == null){
			XPathMap = new Hashtable<>();
		}
		int size = pathStack.size();
		String xpath = "";
		for (int i=0; i<size;i++){
			xpath = xpath + "/" + pathStack.get(i);
		}
		XPathMap.put(xpath, str);
	}
	
	public void putXPathAttr(String str){
		if (XPathMap == null){
			XPathMap = new Hashtable<>();
		}
		int size = pathStack.size();
		String xpath = "";
		for (int i=0; i<size;i++){
			xpath = xpath + "/" + pathStack.get(i);
		}
		XPathMap.put(xpath, str);
	}
	
	public String getXPath(String str){
		return(XPathMap.get(str));
	}
}

