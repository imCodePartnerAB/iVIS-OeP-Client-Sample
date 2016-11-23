package se.unlogic.hierarchy.filtermodules;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class SetAttributeByServerNameModule extends AnnotatedFilterModule {

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Keywords",description="Test if the request server name contains any of these keywords, given as comma separated list")
	protected String keywordsSetting;

	protected Set<String> keywords = new HashSet<String>();

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Attribute name",description="The attribute name to set on the request if the request server name contains any of \"Keywords\"")
	protected String attributeName;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Attribute value",description="The attribute value to set on the request if the request server name contains any of \"Keywords\"")
	protected String attributeValue;

	@Override
	public void init(FilterModuleDescriptor moduleDescriptor, SystemInterface systemInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, systemInterface, dataSource);

		this.getKeywords();
	}

	@Override
	public void update(FilterModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		this.getKeywords();
	}

	protected void getKeywords() {
		this.keywords.clear();
		if(!StringUtils.isEmpty(this.keywordsSetting)) {
			for(String keyword : this.keywordsSetting.split(",")) {
				this.keywords.add(keyword.trim());
			}
		}
	}

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws Exception {

		if(!CollectionUtils.isEmpty(this.keywords) && this.attributeName != null) {
			for(String keyword : this.keywords) {
				if(req.getServerName().contains(keyword)) {
					req.setAttribute(this.attributeName, this.attributeValue);
					break;
				}
			}
		} else {
			if(CollectionUtils.isEmpty(this.keywords)) {
				this.log.warn("Filter module " + this.moduleDescriptor + " has no keywords set, filtering is a no-op");
			}
			if(this.attributeName == null) {
				this.log.warn("Filter module " + this.moduleDescriptor + " has no attribute name set, filtering is a no-op");
			}
		}

		filterChain.doFilter(req, res, user, uriParser);

	}

}
