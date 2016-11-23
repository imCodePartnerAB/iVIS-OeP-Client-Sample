package se.unlogic.hierarchy.core.utils;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import se.unlogic.hierarchy.core.interfaces.ViewFragment;


public interface ViewFragmentTransformer {

	ViewFragment createViewFragment(Document doc) throws TransformerConfigurationException, TransformerException;

}
