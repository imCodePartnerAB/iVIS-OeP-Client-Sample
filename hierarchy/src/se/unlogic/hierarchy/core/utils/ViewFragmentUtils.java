package se.unlogic.hierarchy.core.utils;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;


public class ViewFragmentUtils {

	public static SimpleForegroundModuleResponse appendLinksAndScripts(SimpleForegroundModuleResponse moduleResponse, ViewFragment viewFragment) {

		if(viewFragment.getLinks() != null){

			moduleResponse.addLinks(viewFragment.getLinks());
		}

		if(viewFragment.getScripts() != null){

			moduleResponse.addScripts(viewFragment.getScripts());
		}

		return moduleResponse;
	}
}
