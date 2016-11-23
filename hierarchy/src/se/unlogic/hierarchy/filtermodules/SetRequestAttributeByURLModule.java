package se.unlogic.hierarchy.filtermodules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.FilterChain;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class SetRequestAttributeByURLModule extends AnnotatedFilterModule {

	private static final String description = 	"The rules (regular expressions) matching the requested URL. " +
	"Rules are given as \"<search> <attribute-name> <attribute-value>\", where <search> is the pattern matched " +
	"against the requested URL. Required <attribute-name> and optional <attribute-value> is the attribute name and " +
	"value to be set on the request.";

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Rules",description=description)
	protected String rules;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Return on first match",description="Whether to stop adding attributes to the request after the first matched rule, or continue applying the following rules")
	protected boolean returnOnFirstMatch;

	protected List<Entry<Pattern, Entry<String,Object>>> patterns = new ArrayList<Entry<Pattern, Entry<String,Object>>>();

	@Override
	public void init(FilterModuleDescriptor moduleDescriptor, SystemInterface systemInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, systemInterface, dataSource);

		this.compilePatterns();
	}

	@Override
	public void update(FilterModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		this.compilePatterns();
	}

	private void compilePatterns() {
		this.patterns.clear();
		if(!StringUtils.isEmpty(this.rules)) {
			String[] rules = this.rules.split("\\n");
			for(String rule : rules) {
				String[] ruleParts = rule.trim().split(" ",3);
				if(ruleParts.length == 2) {
					this.patterns.add(new SimpleEntry<Pattern, Entry<String, Object>>(Pattern.compile(ruleParts[0]),new SimpleEntry<String, Object>(ruleParts[1], null)));
				} else if(ruleParts.length == 3) {
					this.patterns.add(new SimpleEntry<Pattern, Entry<String, Object>>(Pattern.compile(ruleParts[0]),new SimpleEntry<String, Object>(ruleParts[1], ruleParts[2])));
				} else {
					this.log.warn("Rule expressed in invalid syntax, ignoring rule " + rule);
				}
			}
		}
	}

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws Exception {

		for(Entry<Pattern,Entry<String,Object>> entry : this.patterns) {
			Matcher matcher = entry.getKey().matcher(req.getRequestURL());
			if(matcher.find()) {
				this.log.debug("Request attribute " + entry.getValue().getKey() + " set to " + entry.getValue().getValue());
				req.setAttribute(entry.getValue().getKey(), entry.getValue().getValue());
				if(this.returnOnFirstMatch) {
					break;
				}
			}
		}
		
		filterChain.doFilter(req, res, user, uriParser);
	}
}