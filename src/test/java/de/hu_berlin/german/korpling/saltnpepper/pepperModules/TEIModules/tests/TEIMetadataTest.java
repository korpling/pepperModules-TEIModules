package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules.TEIImporterMetaData;

public class TEIMetadataTest {
	TEIImporterMetaData metadata = new TEIImporterMetaData();
	
	@Test
	public void pathStackTest(){
		metadata.push("fileDesc");
		metadata.push("titleStmt");
		metadata.push("title");
		metadata.pop();
		metadata.push("title");
		
		System.out.println(metadata.getcurrentpath());
		assertEquals("/fileDesc[1]/titleStmt[1]/title[2]", metadata.getcurrentpath());
		
		metadata.push_to_XPathMap("tag");
		
	}
}