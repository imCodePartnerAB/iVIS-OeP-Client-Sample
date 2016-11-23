package se.unlogic.hierarchy.filtermodules;

import java.io.IOException;
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

public class URLRewriteModule extends AnnotatedFilterModule {

	private static final String description = 	"The rules (regular expressions) matching and rewriting the requested URL. " +
	"Rules are given as \"<search> <replace>\", where <search> is the pattern matched " +
	"against the requested URL and <replace> is the replacement pattern. " +
	"Tip: Use $ for backreferences in <replace>. Rules are applied topdown.";

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Rules",description=description)
	protected String rules;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Return on first match",description="Whether to stop rewriting the URL after the first matched rule, or continue applying the following rules")
	protected boolean returnOnFirstMatch;

	protected List<Entry<Pattern,String>> patterns = new ArrayList<Entry<Pattern, String>>();

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

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FilterChain filterChain) throws Exception {

		String rewrittenURL = null;
		
		for(Entry<Pattern,String> entry : this.patterns) {
			Matcher matcher = entry.getKey().matcher(rewrittenURL == null ? req.getRequestURL() : rewrittenURL);
			if(matcher.find()) {
				
				rewrittenURL = matcher.replaceFirst(entry.getValue());
				if(this.returnOnFirstMatch) {
					this.log.info("URL " + req.getRequestURL() + " rewritten to " + rewrittenURL);
					this.sendRedirect(res, user, rewrittenURL);
					return;
				}
			}
		}
		
		if(rewrittenURL != null) {
			this.log.info("URL " + req.getRequestURL() + " rewritten to " + rewrittenURL);
			this.sendRedirect(res, user, rewrittenURL);
			return;
		}

		filterChain.doFilter(req, res, user, uriParser);
	}
	
	private void sendRedirect(HttpServletResponse res, User user, String rewrittenURL) {
		try {
			res.sendRedirect(rewrittenURL);
		} catch (IOException e) {
			log.warn("Error redirecting user " + user + " to " + rewrittenURL);
		}
	}

	private void compilePatterns() {
		this.patterns.clear();
		if(!StringUtils.isEmpty(this.rules)) {
			String[] rules = this.rules.split("\\n");
			for(String rule : rules) {
				String[] ruleParts = rule.split(" ");
				if(ruleParts.length == 2) {					
					this.patterns.add(new SimpleEntry<Pattern, String>(Pattern.compile(ruleParts[0]),ruleParts[1]));
				} else {
					this.log.warn("Rule expressed in invalid syntax, ignoring rule " + rule);
				}
			}
		}
	}
}
