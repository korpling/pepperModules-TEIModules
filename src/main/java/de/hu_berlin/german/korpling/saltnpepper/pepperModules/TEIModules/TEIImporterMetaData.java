package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class TEIImporterMetaData {
	private Stack<String> pathStack = new Stack<String>();
	private Map<String, String> XPathMap= null;
	private Set<String> PathSet = new HashSet<String>();

	
	public Stack<String> getPathStack() {
		return pathStack;
	}

	public void setPathStack(Stack<String> pathStack) {
		this.pathStack = pathStack;
	}

	public Map<String, String> getXPathMap() {
		return XPathMap;
	}

	public void setXPathMap(Map<String, String> xPathMap) {
		XPathMap = xPathMap;
	}

	public Set<String> getPathSet() {
		return PathSet;
	}

	public void setPathSet(Set<String> pathSet) {
		PathSet = pathSet;
	}

	public TEIImporterMetaData(){
		
	}
	
	public void push(String tag){
		boolean run = true;
		for (int i= 1;run;i++){
			if (!PathSet.contains(getcurrentpath()+"/"+tag+"["+i+"]")){
				pathStack.push(tag+"["+i+"]");
				run = false;
			}
		}
		PathSet.add(getcurrentpath());
	}
	
	public String pop(){
		return(pathStack.pop());
		
	}
	
	public String getcurrentpath(){
		String temp = "/";
		for (int i = 0; (i < pathStack.size()); i++){
			temp = temp + pathStack.elementAt(i) + "/";
		}
		if (temp.length() > 0){
			temp = temp.substring(0,temp.length()-1);
		}
		return(temp);
	}
	
	public void push_to_XPathMap(String value){
		if (XPathMap== null){
			XPathMap= new Hashtable<>();
		}
		XPathMap.put(getcurrentpath(), value);
	}
	
	public void push_attribute_XPathMap(String attribute, String value){
		if (XPathMap== null){
			XPathMap= new Hashtable<>();
		}
		XPathMap.put(getcurrentpath() + "/@" + attribute, value);
	}
	
	
}







