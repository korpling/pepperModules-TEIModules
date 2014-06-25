package de.hu_berlin.german.korpling.saltnpepper.pepperModules.TEIModules;

import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

public class TEIMapper extends PepperMapperImpl{
	@Override
	public DOCUMENT_STATUS mapSDocument() {
		TEIImporterProperties props = ((TEIImporterProperties) getProperties());
		SDocumentGraph docGraph = getSDocument().getSDocumentGraph();
		//System.out.println(docGraph);
		TEITagLibraryReader reader = new TEITagLibraryReader(props);
		reader.setsDocGraph(docGraph);
		this.readXMLResource(reader, getResourceURI());
		return(DOCUMENT_STATUS.COMPLETED);
	}
	private static Logger logger = LoggerFactory.getLogger(TEIMapper.class);
}
