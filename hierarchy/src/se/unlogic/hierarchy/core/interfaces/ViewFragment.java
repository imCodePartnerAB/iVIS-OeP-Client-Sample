package se.unlogic.hierarchy.core.interfaces;

import java.util.List;

import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.standardutils.xml.Elementable;


public interface ViewFragment extends Elementable{

	public String getHTML();

	public List<ScriptTag> getScripts();

	public List<LinkTag> getLinks();
}
