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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

public class TEIImporterMetaData {
	private Stack<String> pathStack = new Stack<String>();
	private Map<String, String> XPathMap= null;
	private Set<String> PathSet = new HashSet<String>();
	private Map<String, String> MappingMap= null;

	
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
		MappingMap = new Hashtable<>();
		//add default mappings to MappingMap
		MappingMap.put("/fileDesc/titleStmt/author", "author");
		MappingMap.put("/fileDesc/titleStmt/title", "title");
		
		
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
	
	public void add_to_SDoc(SDocument sdoc, Map<String,String> map){
		Set<String> keySet = map.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()){
			String tempkey = it.next();
			String tempvalue = map.get(tempkey);
			tempkey = tempkey.replace("[1]", "");
			if (tempvalue.length() > 0){
				System.out.println(tempkey);
				System.out.println(tempvalue);
				sdoc.createSMetaAnnotation(null, tempkey, tempvalue);
			}
		}
	}
}