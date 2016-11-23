package se.unlogic.hierarchy.core.utils;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import se.unlogic.hierarchy.core.beans.SimpleViewFragment;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ModuleTransformerCache;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.standardutils.xml.XMLTransformer;


public class ModuleViewFragmentTransformer<T extends ModuleDescriptor> implements ViewFragmentTransformer{

	private final ModuleTransformerCache<T> moduleTransformerCache;
	private final ViewFragmentModule<T> module;
	private final String encoding;
	private boolean debugXML;

	public ModuleViewFragmentTransformer(ModuleTransformerCache<T> moduleTransformerCache, ViewFragmentModule<T> module, String encoding) {

		if(moduleTransformerCache == null){

			throw new NullPointerException("moduleTransformerCache cannot be null");
		}

		if(module == null){

			throw new NullPointerException("module cannot be null");
		}

		if(encoding == null){

			throw new NullPointerException("encoding cannot be null");
		}

		this.moduleTransformerCache = moduleTransformerCache;
		this.module = module;
		this.encoding = encoding;
	}

	@Override
	public ViewFragment createViewFragment(Document doc) throws TransformerConfigurationException, TransformerException {

		Transformer transformer = moduleTransformerCache.getModuleTranformer(module.getModuleDescriptor());

		if(transformer == null){

			throw new TransformerException("No transformer for module " + module.getModuleDescriptor() + " found in module transformer cache!");
		}

		StringWriter stringWriter = new StringWriter();

		XMLTransformer.transformToWriter(transformer, doc, stringWriter, encoding);

		if(debugXML){

			return new SimpleViewFragment(stringWriter.toString(), doc, module.getScriptTags(), module.getLinkTags());

		}else{

			return new SimpleViewFragment(stringWriter.toString(), module.getScriptTags(), module.getLinkTags());
		}
	}

	public boolean isDebugXML() {

		return debugXML;
	}


	public void setDebugXML(boolean debugXML) {

		this.debugXML = debugXML;
	}

}