package se.unlogic.hierarchy.core.interfaces;

import java.io.InputStream;



public interface SearchableItem {

	public String getID();

	/**
	 * @return the alias to this item relative to the module which generated the item
	 */
	public String getAlias();

	public String getTitle();

	public String getContentType();

	public InputStream getData() throws Exception;

	public long getLastModified();

	/**
	 * @return the access interface used to restrict access to the item. If this method returns null the access interface of the module is used instead.
	 */
	public AccessInterface getAccessInterface();
}
