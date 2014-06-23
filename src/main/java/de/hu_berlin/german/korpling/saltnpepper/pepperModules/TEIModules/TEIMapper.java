package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

public class TEIMapper extends PepperMapperImpl{
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		if(getSDocument()==null){
			setSDocument(SaltFactory.eINSTANCE.createSDocument());
		}
		
		TEIImporterProperties props = ((TEIImporterProperties) getProperties());
		
		SDocumentGraph docGraph = SaltFactory.eINSTANCE.createSDocumentGraph();		
		getSDocument().setSDocumentGraph(docGraph);
		TEITagLibraryReader reader = new TEITagLibraryReader(props);
		this.readXMLResource(reader, getResourceURI());
		return(DOCUMENT_STATUS.COMPLETED);
		
	}
	private static Logger logger = LoggerFactory.getLogger(TEIMapper.class);
}
