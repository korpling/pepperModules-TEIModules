/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
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
package org.corpus_tools.peppermodules.TEIModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.common.io.Files;
import org.corpus_tools.peppermodules.TEIModules.TEIMapper.TEIImporterReader;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

public class TEIImporterReaderTest {
	String filePath = new File("").getAbsolutePath();

	private TEIImporterReader fixture = null;

	public TEIImporterReader getFixture() {
		return fixture;
	}

	public void setFixture(TEIImporterReader fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() {
		setFixture(new TEIImporterReader());
		filePath = filePath.concat("/src/test/resources/");
	}

	@Test
	public void initialize() throws FileNotFoundException, UnsupportedEncodingException {

		fixture.setSUB_TOKENIZATION();

		File outFile = new File(filePath.concat("no_token_test_1/no_token_test_1.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertNotNull(getFixture().getsDocGraph());
	}

	@Test
	public void ob_1() {
		fixture.setSUB_TOKENIZATION();
//		fixture.setGENERIC_STRUCT();
		fixture.setGENERIC_SPAN();

		File outFile = new File(filePath.concat("ob-1/16880831.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		try {

			Files.write(getFixture().getsDocGraph().getTextualDSs().get(0).getText(), Paths.get("testout").toFile(), Charset.defaultCharset());
		} catch (IOException e) {

		}

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());
		assertEquals("THE PROCEEDINGS ON THE King's Commissions Of the PEACE, And Oyer and Terminer, and Goal-delivery of Newgate, held for the City of London, and County of Middlesex, at Justice-Hall, in the Old-Baily, the 31st. of August, and 1st. of September, 1688. And in the Fourth Year of His Majesties Reign. AT the Sessions Held at Justice-Hall, in the Old-Baily, on Friday the 31st of August, and Saturday the 1st. of September, 1688. before the Right Honourable, Sir John Shorter , Kt. Lord Mayor of the City of London, and Sir Bartholomew Shower , Kt. and Recorder of the said City; together with other of His Majesties Justices of the City of London and County of Middlesex.", getFixture().getsDocGraph().getTextualDSs().get(0).getText());
		assertEquals(7, getFixture().getsDocGraph().getTokens().size());
	}

	@Test
	public void simple_p_notoken() {
		fixture.setSUB_TOKENIZATION();

		File outFile = new File(filePath.concat("no_token_test_1/no_token_test_1.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());
		assertEquals("Die Blätter sind fast rundlich eyförmig, auch ist es warm im Sommer.", getFixture().getsDocGraph().getTextualDSs().get(0).getText());
		assertEquals(3, getFixture().getsDocGraph().getTokens().size());
	}

	@Test
	public void simple_p_default_tag_w() {
		fixture.setDEFAULT_TOKENIZATION();

		File outFile = new File(filePath.concat("w_token_test/w_token_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());
		assertEquals("auch ist", getFixture().getsDocGraph().getTextualDSs().get(0).getText());
		assertEquals(2, getFixture().getsDocGraph().getTokens().size());
		for (SToken tok : getFixture().getsDocGraph().getTokens()) {
			assertNotNull(tok.getAnnotation("lemma"));
			assertNotNull(tok.getAnnotation("type"));
		}
		assertNotNull(getFixture().getsDocGraph().getSpans().get(0));
	}

	@Test
	public void head_test() {
		fixture.setSUB_TOKENIZATION();

		File outFile = new File(filePath.concat("head_test/head_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());
		assertEquals("Geſtalt. Die Blätter sind fast rundlich eyförmig, auch ist es warm im Sommer.", getFixture().getsDocGraph().getTextualDSs().get(0).getText());
		assertEquals(4, getFixture().getsDocGraph().getTokens().size());

		assertNotNull(getFixture().getsDocGraph().getSpans().get(0));

		assertEquals("Geſtalt.", (getFixture().getsDocGraph().getText((SNode) getFixture().getsDocGraph().getNodes().get(2))));
	}

	@Test
	public void figure_test() {
		fixture.setSUB_TOKENIZATION();

		File outFile = new File(filePath.concat("figure_test/figure_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());

		assertEquals(2, getFixture().getsDocGraph().getTokens().size());
		assertEquals("Der Stengel ist aufrecht und nicht", (getFixture().getsDocGraph().getText(getFixture().getsDocGraph().getTokens().get(0))));
		assertEquals("", (getFixture().getsDocGraph().getText(getFixture().getsDocGraph().getTokens().get(1))));
	}

	@Test
	public void genericStruct_test() {
		fixture.setSUB_TOKENIZATION();
		fixture.setGENERIC_STRUCT();

		File outFile = new File(filePath.concat("genericStruct_test/genericStruct_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());

		assertEquals(1, getFixture().getsDocGraph().getTokens().size());
		assertEquals("Der Stengel ist aufrecht und nicht.", (getFixture().getsDocGraph().getText(getFixture().getsDocGraph().getTokens().get(0))));
	}

	@Test
	public void genericSpan_test() {
		fixture.setSUB_TOKENIZATION();
		fixture.setGENERIC_SPAN();

		File outFile = new File(filePath.concat("genericSpan_test/genericSpan_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());

		assertEquals(2, getFixture().getsDocGraph().getTokens().size());
		assertEquals("Der Stengel ist aufrecht und nicht.", (getFixture().getsDocGraph().getText(getFixture().getsDocGraph().getTokens().get(0))));
		// testing the spans
		assertEquals("Der Stengel ist aufrecht und nicht.", getFixture().getsDocGraph().getText(getFixture().getsDocGraph().getSpans().get(0)));
		assertEquals("Die Blätter sind fast rundlich.", getFixture().getsDocGraph().getText(getFixture().getsDocGraph().getSpans().get(1)));

	}

	@Test
	public void pb_test() {
		fixture.setSUB_TOKENIZATION();

		File outFile = new File(filePath.concat("pb_test/pb_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertEquals(1, getFixture().getsDocGraph().getTextualDSs().size());
		assertEquals("Geſtalt. Die Blätter sind fast rundlich eyförmig, auch ist es warm im Sommer.", getFixture().getsDocGraph().getTextualDSs().get(0).getText());
		assertEquals(4, getFixture().getsDocGraph().getTokens().size());

		assertNotNull(getFixture().getsDocGraph().getSpans().get(0));

		assertEquals("Geſtalt.", (getFixture().getsDocGraph().getText((SNode) getFixture().getsDocGraph().getNodes().get(2))));
	}

	@Test
	public void genericAttr_test() {
		fixture.setSUB_TOKENIZATION();
		fixture.setGENERIC_STRUCT();
		fixture.setGENERIC_ATTR();

		File outFile = new File(filePath.concat("genericSpan_test/genericSpan_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertTrue(getFixture().getsDocGraph().getStructures().get(1).containsLabel("kop"));
		assertEquals("Tisch", getFixture().getsDocGraph().getStructures().get(1).getAnnotation("kop").getValue_STEXT());
		assertTrue(getFixture().getsDocGraph().getStructures().get(2).containsLabel("lat"));
		assertEquals("Ein", getFixture().getsDocGraph().getStructures().get(2).getAnnotation("lat").getValue_STEXT());
	}

	@Test
	public void genericSpanAttr_test() {
		fixture.setSUB_TOKENIZATION();
		fixture.setGENERIC_SPAN();
		fixture.setGENERIC_ATTR();

		File outFile = new File(filePath.concat("genericSpan_test/genericSpan_test.xml"));
		outFile.getParentFile().mkdirs();

		readXMLResource(getFixture(), URI.createFileURI(outFile.getAbsolutePath()));

		assertTrue(getFixture().getsDocGraph().getSpans().get(0).containsLabel("kop"));
		assertTrue(getFixture().getsDocGraph().getSpans().get(1).containsLabel("lat"));
	}

	protected void readXMLResource(DefaultHandler2 contentHandler, URI documentLocation) {
		if (documentLocation == null)
			throw new RuntimeException("Cannot load a xml-resource, because the given uri to locate file is null.");

		File exmaraldaFile = new File(documentLocation.toFileString());
		if (!exmaraldaFile.exists())
			throw new RuntimeException("Cannot load a xml-resource, because the file does not exist: " + exmaraldaFile);

		if (!exmaraldaFile.canRead())
			throw new RuntimeException("Cannot load a xml-resource, because the file can not be read: " + exmaraldaFile);

		SAXParser parser;
		XMLReader xmlReader;

		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			parser = factory.newSAXParser();
			xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(contentHandler);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Cannot load a xml-resource '" + exmaraldaFile.getAbsolutePath() + "'.", e);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load a xml-resource '" + exmaraldaFile.getAbsolutePath() + "'.", e);
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
				throw new RuntimeException("Cannot load a xml-resource '" + exmaraldaFile.getAbsolutePath() + "'.", e1);
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException("Cannot read xml-file'" + documentLocation + "', because of a nested exception. ", e);
		}
	}
}