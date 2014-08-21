package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class TEIImporterMetaData {
	private Stack<String> pathStack = new Stack<String>();
	private Map<String, String> XPathMap= null;
	private Set<String> PathSet = new HashSet<String>();

	
	public TEIImporterMetaData(){
		
	}
	
	public void push(String str){
		pathStack.push(str);
	}
	
	public String pop(){
		return(pathStack.pop());
	}
	
	private String getPath(String oldpath, String newstring, int recursion){
		int index = recursion;
		if (PathSet.contains(oldpath+"/"+newstring+"["+index+"]")){
			index += 1;
			return (getPath(oldpath, newstring, index));
		}
		else{
			PathSet.add(oldpath+"/"+newstring+"["+index+"]");
			return(oldpath+"/"+newstring+"["+index+"]");
		}
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

