package se.unlogic.hierarchy.core.utils;

import java.util.List;

import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public interface ViewFragmentModule<T extends ModuleDescriptor> {

	public T getModuleDescriptor();

	public List<LinkTag> getLinkTags();

	public List<ScriptTag> getScriptTags();
}
