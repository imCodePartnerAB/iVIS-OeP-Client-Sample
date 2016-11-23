/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.test;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.ValueDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class SettingTestModule extends SimpleForegroundModule {

	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> settings = new ArrayList<SettingDescriptor>();

		settings.add(SettingDescriptor.createCheckboxSetting("checkbox", "Checkbox", "Checkbox setting", false));

		settings.add(SettingDescriptor.createDropDownSetting("dropdown", "Dropdown", "Dropdown setting", false, "1", new ValueDescriptor("Value zero", "0"), new ValueDescriptor("Value one", "1"), new ValueDescriptor("Value two", "2")));
		settings.add(SettingDescriptor.createDropDownSetting("dropdownreq", "Dropdown (required)", "Required dropdown setting", true, "1", new ValueDescriptor("Value zero", "0"), new ValueDescriptor("Value one", "1"), new ValueDescriptor("Value two", "2")));

		settings.add(SettingDescriptor.createMultiListSetting("multilist", "Multilist", "Multilist setting", false, "1", new ValueDescriptor("Value zero", "0"), new ValueDescriptor("Value one", "1"), new ValueDescriptor("Value two", "2"), new ValueDescriptor("Value three", "3")));
		settings.add(SettingDescriptor.createMultiListSetting("multilistreq", "Multilist (required)", "Required multilist setting", true, "1", new ValueDescriptor("Value zero", "0"), new ValueDescriptor("Value one", "1"), new ValueDescriptor("Value two", "2"), new ValueDescriptor("Value three", "3")));

		settings.add(SettingDescriptor.createRadioButtonSetting("radiobutton", "Radiobutton", "Radiobutton setting", false, "2", new ValueDescriptor("Value zero", "0"), new ValueDescriptor("Value one", "1"), new ValueDescriptor("Value two", "2")));
		settings.add(SettingDescriptor.createRadioButtonSetting("radiobuttonreq", "Radiobutton (reuqired)", "Required radiobutton setting", true, "2", new ValueDescriptor("Value zero", "0"), new ValueDescriptor("Value one", "1"), new ValueDescriptor("Value two", "2")));

		settings.add(SettingDescriptor.createTextAreaSetting("textarea", "Textarea", "Textarea setting for long values", false, "default textarea value\nline two", null));
		settings.add(SettingDescriptor.createTextFieldSetting("textfield", "Textfield", "Textfield setting for long values", false, "default text field value", null));

		return settings;
	}

	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		MutableSettingHandler mutableSettingHandler = this.moduleDescriptor.getMutableSettingHandler();

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>Module settings test</h1>");

		for (SettingDescriptor settingDescriptor : this.getSettings()) {

			List<String> values = mutableSettingHandler.getStrings(settingDescriptor.getId());

			String valueString = null;

			if (values != null) {
				valueString = StringUtils.toCommaSeparatedString(values);
			}

			stringBuilder.append("<p>" + settingDescriptor.getName() + ": " + valueString + " </p>");
		}

		stringBuilder.append("</div>");

		return new SimpleForegroundModuleResponse(stringBuilder.toString(),getDefaultBreadcrumb());

	}
}
