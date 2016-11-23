package se.unlogic.hierarchy.core.interfaces;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;


public interface ModuleTransformerCache<T extends ModuleDescriptor> {

	public Transformer getModuleTranformer(T descriptor) throws TransformerConfigurationException;
}
