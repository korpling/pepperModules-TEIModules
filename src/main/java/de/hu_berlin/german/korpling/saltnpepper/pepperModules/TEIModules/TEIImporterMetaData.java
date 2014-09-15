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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

public class TEIImporterMetaData {
	private static Logger logger = LoggerFactory.getLogger(TEIImporterMetaData.class);
	
	/**
	 * stack saving the names of tags
	 */
	private Stack<String> pathStack = new Stack<String>();
	
	/**
	 * map with xpaths as keys and text between tags as values
	 */
	private Map<String, String> XPathMap= null;
	
	/**
	 * set containing all existing xpaths
	 */
	private Set<String> PathSet = new HashSet<String>();
	
	/**
	 * map with xpaths as keys and customized(or hardcoded) SMetaAnnotations as values
	 */
	private Map<String, String> MappingMap= null;

	/**
	 * gets pathStack
	 */
	public Stack<String> getPathStack() {
		return pathStack;
	}

	/**
	 * sets pathStack
	 */
	public void setPathStack(Stack<String> pathStack) {
		this.pathStack = pathStack;
	}

	/**
	 * gets XPathmap
	 */
	public Map<String, String> getXPathMap() {
		return XPathMap;
	}

	/**
	 * sets XPathmap
	 */
	public void setXPathMap(Map<String, String> xPathMap) {
		XPathMap = xPathMap;
	}

	/**
	 * gets PathSet
	 */
	public Set<String> getPathSet() {
		return PathSet;
	}

	/**
	 * sets PathSet
	 */
	public void setPathSet(Set<String> pathSet) {
		PathSet = pathSet;
	}

	/**
	 * constructor that also adds default mappings	 
	 */
	public TEIImporterMetaData(){
		MappingMap = new Hashtable<>();
		//add default mappings to MappingMap
		MappingMap.put("/fileDesc/titleStmt/author", "author");
		MappingMap.put("/fileDesc/titleStmt/title", "title");
	}
	
	/**
	 * commits mappings to XPathMap 
	 * @param delmetada boolean that determines whether
	 * redundant mappings will cause the deletion of
	 * the original key(xpath) value pair
	 */
	public void commitmappings(boolean delmetadata){
		if (delmetadata){
			//replace XPathMap-keys with mappings found in MappingMap
			Iterator<String> it = MappingMap.keySet().iterator();
			//forloop…
			logger.warn("");
		}
		else{
			//replicate XPathMap-keys with mappings found in MappingMap
		}
	}
	
	/**
	 * pushes tag-name to pathStack
	 * @param tag tag-name	 
	 */
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
	
	/**
	 * pops pathStack
	 * @return name of popped tag	 
	 */
	public String pop(){
		return(pathStack.pop());
	}
	
	/**
	 * returns current path
	 * @return current path of metadata 
	 */
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
	
	/**
	 * pushes text inside tags to XPathMap and creates empty Hashtable if
	 * XPathMap == null
	 * @param value that is pushed to XPathMap
	 */
	public void push_to_XPathMap(String value){
		if (XPathMap== null){
			XPathMap= new Hashtable<>();
		}
		XPathMap.put(getcurrentpath(), value);
	}
	
	/**
	 * pushes attributes to XPathMap and creates empty Hashtable if
	 * XPathMap == null
	 * @param attribute relevant attribute to be pushed
	 * @param value that is pushed to XPathMap
	 */
	public void push_attribute_XPathMap(String attribute, String value){
		if (XPathMap== null){
			XPathMap= new Hashtable<>();
		}
		XPathMap.put(getcurrentpath() + "/@" + attribute, value);
	}
	
	public Map<String,String> uniteMappings(Map<String,String> overwritingMap){
		MappingMap.putAll(overwritingMap);
		return (MappingMap);
	}
	
	/**
	 * adds SMetaAnnotations to SDocument(not SDocGraph!)
	 * @param sdoc SDocument
	 * @param map contains mappings from xpath to customized annotation
	 */
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