package de.hu_berlin.german.korpling.saltnpepper.pepperModules.teiModules.tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.teiModules.TEITagLibrary;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.teiModules.TEITagLibraryReader;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import static org.junit.Assert.*;

public class TEITagLibraryReaderTest {

	private TEITagLibraryReader fixture = null;

	public TEITagLibraryReader getFixture() {
		return fixture;
	}

	public void setFixture(TEITagLibraryReader fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() {
		setFixture(new TEITagLibraryReader());
	}

	@Test
	public void initialize() throws 
			FileNotFoundException, UnsupportedEncodingException {
		
		fixture.setSUB_TOKENIZATION();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		System.out.println(outStream.toString());

		File outFile = new File("/home/andre/tei_test_files/no_token_test_1.xml");
		outFile.getParentFile().mkdirs();
		
		readXMLResource(getFixture(),
				URI.createFileURI(outFile.getAbsolutePath()));

		assertNotNull(getFixture().getsDocGraph());
	}
	
	@Test
	public void simple_p_notoken(){
		fixture.setSUB_TOKENIZATION();
		
		File outFile = new File("/home/andre/tei_test_files/no_token_test_1.xml");
		outFile.getParentFile().mkdirs();
		
		readXMLResource(getFixture(),
				URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1,getFixture().getsDocGraph().getSTextualDSs().size());
		assertEquals("Die Blätter sind fast rundlich eyförmig, auch ist es warm im Sommer.",getFixture().getsDocGraph().getSTextualDSs().get(0).getSText());
		assertEquals(1, getFixture().getsDocGraph().getSTokens().size());
	}
	
	@Test
	public void simple_p_default_tag_w(){
		fixture.setUSER_DEFINED_DEFAULT_TOKENIZATION();
		
		File outFile = new File("/home/andre/tei_test_files/w_token_test.xml");
		outFile.getParentFile().mkdirs();
		
		readXMLResource(getFixture(),
				URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1,getFixture().getsDocGraph().getSTextualDSs().size());
		assertEquals("auch ist",getFixture().getsDocGraph().getSTextualDSs().get(0).getSText());
		assertEquals(2, getFixture().getsDocGraph().getSTokens().size());
	}
	

	protected void readXMLResource(DefaultHandler2 contentHandler,
			URI documentLocation) {
		if (documentLocation == null)
			throw new RuntimeException(
					"Cannot load a xml-resource, because the given uri to locate file is null.");

		File exmaraldaFile = new File(documentLocation.toFileString());
		if (!exmaraldaFile.exists())
			throw new RuntimeException(
					"Cannot load a xml-resource, because the file does not exist: "
							+ exmaraldaFile);

		if (!exmaraldaFile.canRead())
			throw new RuntimeException(
					"Cannot load a xml-resource, because the file can not be read: "
							+ exmaraldaFile);

		SAXParser parser;
		XMLReader xmlReader;

		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			parser = factory.newSAXParser();
			xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(contentHandler);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Cannot load a xml-resource '"
					+ exmaraldaFile.getAbsolutePath() + "'.", e);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load a xml-resource '"
					+ exmaraldaFile.getAbsolutePath() + "'.", e);
		}
		try {
			InputStream inputStream = new FileInputStream(exmaraldaFile);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			xmlReader.parse(is);
		} catch (SAXException e) {

			try {
				parser = factory.newSAXParser();
				xmlReader = parser.getXMLReader();
				xmlReader.setContentHandler(contentHandler);
				xmlReader.parse(exmaraldaFile.getAbsolutePath());
			} catch (Exception e1) {
				throw new RuntimeException("Cannot load a xml-resource '"
						+ exmaraldaFile.getAbsolutePath() + "'.", e1);
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException("Cannot read xml-file'"
						+ documentLocation
						+ "', because of a nested exception. ", e);
		}
	}
}
