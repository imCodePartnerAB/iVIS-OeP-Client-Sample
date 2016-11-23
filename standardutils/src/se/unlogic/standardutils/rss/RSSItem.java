package se.unlogic.standardutils.rss;

import java.util.Collection;
import java.util.Date;

public interface RSSItem {

	public abstract Collection<String> getCategories();

	public abstract String getAuthor();

	public abstract String getCommentsLink();

	public abstract String getGuid();

	public abstract Date getPubDate();

	public abstract String getDescription();

	public abstract String getLink();

	public abstract String getTitle();
}
