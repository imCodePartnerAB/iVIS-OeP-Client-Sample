package com.nordicpeak.flowengine.beans;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.json.JsonArray;
import se.unlogic.standardutils.json.JsonObject;

import com.nordicpeak.flowengine.enums.ModificationAction;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.managers.ManagedStep;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;

public class QueryModification {

	private final QueryInstance queryInstance;
	private final ModificationAction action;

	public QueryModification(QueryInstance queryInstance, ModificationAction action) {

		super();
		this.queryInstance = queryInstance;
		this.action = action;
	}

	public QueryInstance getQueryInstance() {

		return queryInstance;
	}

	public ModificationAction getAction() {

		return action;
	}

	@Override
	public String toString() {

		return "QueryModification [queryInstance=" + queryInstance + ", action=" + action + "]";
	}
	
	public JsonObject toJson(HttpServletRequest req, User user, QueryHandler queryHandler, MutableFlowInstanceManager mutableFlowInstanceManager, ManagedStep managedStep, String contextPath) throws Throwable {
	
		JsonObject queryModification = new JsonObject();
	
		queryModification.putField("queryID", queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getQueryID().toString());
		queryModification.putField("queryType", queryInstance.getClass().getSimpleName());
		queryModification.putField("action", action.toString());
		
		if(!action.equals(ModificationAction.HIDE)) {
			
			QueryResponse queryResponse = queryInstance.getFormHTML(req, user, null, queryHandler, mutableFlowInstanceManager.requiresAjaxPosting(queryInstance, managedStep), null);
			
			if(queryResponse != null) {

				String html = queryResponse.getHTML();
				queryModification.putField("formHTML", html);
				
				if(!CollectionUtils.isEmpty(queryResponse.getScripts())) {
					
					JsonArray scriptTags = new JsonArray();
					
					for(ScriptTag scriptTag : queryResponse.getScripts()) {
						
						JsonObject script = new JsonObject();
						script.putField("src", contextPath + scriptTag.getSrc());
						script.putField("type", scriptTag.getType());
						scriptTags.addNode(script);
						
					}
					
					queryModification.putField("Scripts", scriptTags);
					
				}
				
				if(!CollectionUtils.isEmpty(queryResponse.getLinks())) {
					
					JsonArray linkTags = new JsonArray();
					
					for(LinkTag linkTag : queryResponse.getLinks()) {
						
						JsonObject link = new JsonObject();
						link.putField("href", contextPath + linkTag.getHref());
						link.putField("media", linkTag.getMedia());
						link.putField("rel", linkTag.getRel());
						link.putField("type", linkTag.getType());
						linkTags.addNode(link);
						
					}
					
					queryModification.putField("Links", linkTags);
					
				}
				
			}
			
		}
		
		return queryModification;
		
	}

}
