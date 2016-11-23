package se.unlogic.standardutils.rss;

import java.util.Collection;
import java.util.Date;


public interface RSSChannel {

	public abstract Date getPubDate();

	public abstract Date getLastBuildDate();

	public abstract Integer getTtl();

	public abstract String getLanguage();

	public abstract String getCopyright();

	public abstract String getWebmaster();

	public abstract String getManagingEditor();

	public abstract Integer getItemsPerChannel();

	public abstract String getLink();

	public abstract String getDescription();

	public abstract String getTitle();

	public abstract Collection<String> getCategories();

	public abstract String getFeedLink();

}
