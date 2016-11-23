/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;

public class XMLUtils {

	private static final DocumentBuilder DOCUMENT_BUILDER;
	private static final DocumentBuilder NAMESPACE_AWARE_DOCUMENT_BUILDER;

	static{
		try{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DOCUMENT_BUILDER = documentBuilderFactory.newDocumentBuilder();

			documentBuilderFactory.setNamespaceAware(true);

			NAMESPACE_AWARE_DOCUMENT_BUILDER = documentBuilderFactory.newDocumentBuilder();
		}catch(ParserConfigurationException e){
			throw new RuntimeException(e);
		}
	}

	public static Document createDomDocument() {

		return DOCUMENT_BUILDER.newDocument();
	}

	public static Document createNamespaceAwareDomDocument(String namespaceURI, String qualifiedName) {

		return NAMESPACE_AWARE_DOCUMENT_BUILDER.getDOMImplementation().createDocument(namespaceURI, qualifiedName, null);
	}

	public static String toString(Node node, String encoding, boolean indent) throws TransformerFactoryConfigurationError, TransformerException {

		Source source = new DOMSource(node);
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.ENCODING, encoding);

		if(indent){
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		xformer.transform(source, result);

		return sw.getBuffer().toString();
	}

	public static void toString(Node node, String encoding, Writer w, boolean indent) throws TransformerFactoryConfigurationError, TransformerException {

		Source source = new DOMSource(node);
		Result result = new StreamResult(w);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.ENCODING, encoding);

		if(indent){
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		xformer.transform(source, result);
	}

	public static Document parseXMLFile(String filename, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		return parseXMLFile(new File(filename), validating, namespaceAware);
	}

	public static Document parseXMLFile(File file, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(namespaceAware);
		factory.setValidating(validating);

		setFactoryParam(factory, validating, namespaceAware);

		Document doc = factory.newDocumentBuilder().parse(file);

		return doc;
	}

	public static Document parseXML(URI uri, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(namespaceAware);
		factory.setValidating(validating);

		setFactoryParam(factory, validating, namespaceAware);

		Document doc = factory.newDocumentBuilder().parse(uri.toString());

		return doc;
	}

	public static Document parseXML(InputSource inputSource, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(namespaceAware);
		factory.setValidating(validating);

		setFactoryParam(factory, validating, namespaceAware);

		Document doc = factory.newDocumentBuilder().parse(inputSource);

		return doc;
	}

	public static Document parseXML(InputStream stream, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(namespaceAware);
		factory.setValidating(validating);

		setFactoryParam(factory, validating, namespaceAware);

		Document doc = factory.newDocumentBuilder().parse(stream);

		return doc;
	}

	private static void setFactoryParam(DocumentBuilderFactory factory, boolean validating, boolean namespaceAware) throws ParserConfigurationException {

		try {
			factory.setFeature("http://xml.org/sax/features/namespaces", namespaceAware);
			factory.setFeature("http://xml.org/sax/features/validation", validating);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		} catch (ParserConfigurationException e) {}
	}

	public static Document parseXML(String string, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		return parseXML(StringUtils.getInputStream(string), validating, namespaceAware);
	}

	public static Document parseXML(String string, String encoding, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {

		return parseXML(new ByteArrayInputStream(string.getBytes(encoding)), validating, namespaceAware);
	}

	public static Element createElement(String name, Object value, Document doc) {

		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode(value.toString()));
		return element;
	}

	public static Element createCDATAElement(String name, Object value, Document doc) {

		Element element = doc.createElement(name);
		element.appendChild(doc.createCDATASection(value.toString()));
		return element;
	}

	public static void writeXMLFile(Node node, File file, boolean indent, String encoding) throws TransformerFactoryConfigurationError, TransformerException, FileNotFoundException {

		FileOutputStream outputStream = null;

		try{
			outputStream = new FileOutputStream(file);

			writeXML(node, outputStream, indent, encoding);

		}finally{

			StreamUtils.closeStream(outputStream);
		}
	}

	public static void writeXMLFile(Node node, String filename, boolean indent, String encoding) throws TransformerFactoryConfigurationError, TransformerException, FileNotFoundException {

		// Prepare the output file
		File file = new File(filename);

		writeXMLFile(node, file, indent, encoding);
	}

	public static void writeXML(Node node, OutputStream outputStream, boolean indent, String encoding) throws TransformerFactoryConfigurationError, TransformerException {

		// Prepare the DOM document for writing
		Source source = new DOMSource(node);

		// Prepare the output file
		Result result = new StreamResult(outputStream);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();

		xformer.setOutputProperty(OutputKeys.ENCODING, encoding);

		if(indent){
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		xformer.transform(source, result);
	}

	public static Element append(Document doc, Element targetElement, Elementable elementable) {

		if(elementable != null){

			return (Element) targetElement.appendChild(elementable.toXML(doc));
		}

		return null;
	}

	public static void append(Document doc, Element targetElement, Collection<? extends XMLable> beans) {

		if(beans != null && !beans.isEmpty()){

			for(XMLable xmlable : beans){
				targetElement.appendChild(xmlable.toXML(doc));
			}
		}
	}

	public static void append(Document doc, Element targetElement, String elementName, String subElementsName, Object[] values) {

		if(values != null){

			Element subElement = doc.createElement(elementName);
			targetElement.appendChild(subElement);

			for(Object value : values){

				appendNewCDATAElement(doc, subElement, subElementsName, value);
			}
		}
	}

	public static void append(Document doc, Element targetElement, String elementName, String subElementsName, List<? extends Object> values) {

		if(!CollectionUtils.isEmpty(values)){

			Element subElement = doc.createElement(elementName);
			targetElement.appendChild(subElement);

			for(Object value : values){

				appendNewCDATAElement(doc, subElement, subElementsName, value);
			}
		}
	}

	public static void append(Document doc, Element targetElement, String elementName, Collection<? extends XMLable> beans) {

		if(!CollectionUtils.isEmpty(beans)){

			Element subElement = doc.createElement(elementName);
			targetElement.appendChild(subElement);

			for(XMLable xmlable : beans){
				subElement.appendChild(xmlable.toXML(doc));
			}
		}
	}

	public static void appendAsElementName(Document doc, Element targetElement, String elementName, List<? extends Object> values) {

		if(!CollectionUtils.isEmpty(values)){

			Element subElement = doc.createElement(elementName);
			targetElement.appendChild(subElement);

			for(Object value : values){

				subElement.appendChild(doc.createElement(value.toString()));
			}
		}
	}

	public static void appendNewCDATAElement(Document doc, Element targetElement, String elementName, String value) {

		if(!StringUtils.isEmpty(value)){
			targetElement.appendChild(createCDATAElement(elementName, value, doc));
		}
	}

	public static void appendNewElement(Document doc, Element targetElement, String elementName, String value) {

		if(!StringUtils.isEmpty(value)){
			targetElement.appendChild(createElement(elementName, value, doc));
		}
	}

	public static Element appendNewElement(Document doc, Element targetElement, String elementName) {

		Element element = doc.createElement(elementName);

		targetElement.appendChild(element);

		return element;
	}

	public static void appendNewCDATAElement(Document doc, Element targetElement, String elementName, Object value) {

		if(value != null){
			appendNewCDATAElement(doc, targetElement, elementName, value.toString());
		}
	}

	public static void appendNewElement(Document doc, Element targetElement, String elementName, Object value) {

		if(value != null){
			appendNewElement(doc, targetElement, elementName, value.toString());
		}

	}

	/**
	 * Adds or replaces node in parent.
	 *
	 * @param parent
	 * @param node
	 * @throws Exception - Node cannot exist more than once, i.e. multiple nodes with the same name cannot exist in parent.
	 */
	public static void replaceSingleNode(Element parent, final Node node) throws RuntimeException {

		NodeList nodes = parent.getElementsByTagName(node.getNodeName());

		if(nodes.getLength() > 1){
			throw new RuntimeException("Parent element contains multiple nodes with the name " + node.getNodeName());
		}
		if(nodes.getLength() == 0){
			parent.appendChild(node);
		}else{
			parent.replaceChild(node, nodes.item(0));
		}
	}

	public enum TimeUnit {
		HOUR, MINUTE, SECOND;
	}

	//Replace all usage of this method with client side javascript instead
	@Deprecated
	public static Element getTimeUnits(Document doc, TimeUnit timeUnit) {

		switch(timeUnit){
			case HOUR:
				Element hoursElement = doc.createElement("hours");
				Element hourElement;
				for(int i = 0; i < 10; ++i){
					hourElement = doc.createElement("hour");
					XMLUtils.appendNewElement(doc, hourElement, "value", "0" + i);
					hoursElement.appendChild(hourElement);
				}
				for(int i = 10; i < 24; ++i){
					hourElement = doc.createElement("hour");
					XMLUtils.appendNewElement(doc, hourElement, "value", i);
					hoursElement.appendChild(hourElement);
				}
				return hoursElement;
			case MINUTE:
				Element minutesElement = doc.createElement("minutes");
				Element minuteElement;
				for(int i = 0; i < 10; ++i){
					minuteElement = doc.createElement("minute");
					XMLUtils.appendNewElement(doc, minuteElement, "value", "0" + i);
					minutesElement.appendChild(minuteElement);
				}
				for(int i = 10; i < 60; ++i){
					minuteElement = doc.createElement("minute");
					XMLUtils.appendNewElement(doc, minuteElement, "value", i);
					minutesElement.appendChild(minuteElement);
				}
				return minutesElement;
			case SECOND:
				Element secondsElement = doc.createElement("seconds");
				Element secondElement;
				for(int i = 0; i < 10; ++i){
					secondElement = doc.createElement("second");
					XMLUtils.appendNewElement(doc, secondElement, "value", "0" + i);
					secondsElement.appendChild(secondElement);
				}
				for(int i = 10; i < 60; ++i){
					secondElement = doc.createElement("second");
					XMLUtils.appendNewElement(doc, secondElement, "value", i);
					secondsElement.appendChild(secondElement);
				}
				return secondsElement;
		}
		return null;
	}

	public static String toValidElementName(String string) {

		if(string.length() >= 3 && string.substring(0, 3).equalsIgnoreCase("xml")){

			string = "___" + string.substring(3);

		}else if(string.substring(0,1).matches("[0-9]")){

			string = "_" + string.substring(1);
		}

		string = string.replaceAll("[åä]", "a");
		string = string.replaceAll("[ÅÄ]", "A");
		string = string.replace("ö", "o");
		string = string.replace("Ö", "O");

		return string.replaceAll("[^0-9a-zA-Z-.]", "_");
	}
}
