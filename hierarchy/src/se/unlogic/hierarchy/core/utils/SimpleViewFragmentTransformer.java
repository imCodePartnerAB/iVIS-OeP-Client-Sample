package se.unlogic.hierarchy.core.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.SimpleViewFragment;
import se.unlogic.hierarchy.core.exceptions.ResourceNotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.xml.ClassPathURIResolver;
import se.unlogic.standardutils.xml.XMLTransformer;
import se.unlogic.standardutils.xsl.URIXSLTransformer;
import se.unlogic.standardutils.xsl.XSLTransformer;
import se.unlogic.standardutils.xsl.XSLVariableReader;


public class SimpleViewFragmentTransformer implements ViewFragmentTransformer {

	private final XSLTransformer xslTransformer;
	private final List<ScriptTag> scriptTags;
	private final List<LinkTag> linkTags;
	private final String encoding;
	private boolean debugXML;

	@SuppressWarnings("unchecked")
	public SimpleViewFragmentTransformer(String xslPath, String encoding, Class<?> baseClass, ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, URISyntaxException, TransformerConfigurationException, XPathExpressionException, ResourceNotFoundException{

		if(xslPath == null){

			throw new NullPointerException("xslPath cannot be null");
		}

		if(encoding == null){

			throw new NullPointerException("encoding cannot be null");
		}

		URL styleSheetURL = baseClass.getResource(xslPath);

		if(styleSheetURL == null){

			throw new ResourceNotFoundException(baseClass.getName().trim(), xslPath);
		}

		xslTransformer = new URIXSLTransformer(styleSheetURL.toURI(),ClassPathURIResolver.getInstance(), true);

		XSLVariableReader variableReader = new XSLVariableReader(styleSheetURL.toURI());

		List<ScriptTag> globalScripts = ModuleUtils.getGlobalScripts(variableReader);
		List<ScriptTag> localScripts = ModuleUtils.getScripts(variableReader, sectionInterface, "f", moduleDescriptor);

		this.scriptTags = CollectionUtils.combine(globalScripts, localScripts);

		this.linkTags = ModuleUtils.getLinks(variableReader, sectionInterface, "f", moduleDescriptor);

		this.encoding = encoding;
	}

	public SimpleViewFragmentTransformer(XSLTransformer xslTransformer, List<ScriptTag> scriptTags, List<LinkTag> linkTags, String encoding){

		if(xslTransformer == null){

			throw new NullPointerException("xslTransformer cannot be null");
		}

		if(encoding == null){

			throw new NullPointerException("encoding cannot be null");
		}

		this.xslTransformer = xslTransformer;
		this.scriptTags = scriptTags;
		this.linkTags = linkTags;
		this.encoding = encoding;
	}

	@Override
	public ViewFragment createViewFragment(Document doc) throws TransformerConfigurationException, TransformerException {

		StringWriter stringWriter = new StringWriter();

		XMLTransformer.transformToWriter(xslTransformer.getTransformer(), doc, stringWriter, encoding);

		if(debugXML){

			return new SimpleViewFragment(stringWriter.toString(), doc, scriptTags, linkTags);

		}else{

			return new SimpleViewFragment(stringWriter.toString(), scriptTags, linkTags);
		}
	}


	public boolean isDebugXML() {

		return debugXML;
	}


	public void setDebugXML(boolean debugXML) {

		this.debugXML = debugXML;
	}
}
