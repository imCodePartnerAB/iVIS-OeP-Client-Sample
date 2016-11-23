$(document).ready(function() {
	 
	 $(".usergroup-list").each(function(j){
		 var $list = $(this);
		 var prefix = getPrefix($list);
		 var $url = $list.find("input[name='connectorURL']").val();
		 var template = getTemplate(prefix);
		 
		 var $searchInput = $( "#" + prefix + "-search");
		 
		 $searchInput.autocomplete({
		 	source: function(request, response) {
		 		return searchUsersAndGroups(request, response, $url, $searchInput, template);
			},
			select: function( event, ui ) {
				
				addEntry(ui.item, $list, prefix);
				
				$(this).val("");
				
				return false;
			},
			focus: function(event, ui) {
		        event.preventDefault();
		    }
		 });
		 
		 
		 var $entries = $list.find("li");
		 
		 $entries.each(function(j) {
				
			var $entry = $(this);
			
			initDeleteButton($entry, $list, prefix);
		}); 
		
		$(this).bind("change", function() {
			$(this).find("li").removeClass("lightbackground");
			$(this).find("li:odd").addClass("lightbackground");
		});
		
		$(this).trigger("change");
		
	 });
	 
	 $(".readonly-usergroup-list li:odd").addClass("lightbackground");
	 
});

function getPrefix($list){
	return $list.find("input[name='prefix']").val();
}

function getType($list){
	return $list.find("input[name='type']").val();
}

function searchUsersAndGroups(request, response, $searchURL, $searchInput, template) {
	
	$searchInput.addClass("ui-autocomplete-loading");
	
	$.ajax({
		url : $searchURL,
		dataType : "json",
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		data : {
			q : encodeURIComponent(request.term)
		},
		success : function(data) {
			
			if(data.hits != undefined && data.hits.length > 0) {
				
				response($.map(data.hits, function(item) {
					
					return {
						label : getLabel(template, item),
						value : item.ID,
						Name  : item.Name,
						ID : item.ID,
						Email : item.Email,
						Username : item.Username
					}
				}));
			} else {
				response(null);
			}
			
			$searchInput.removeClass("ui-autocomplete-loading");
			
		},
		error : function() {
			
			$searchInput.removeClass("ui-autocomplete-loading");
		}
	});
}

function getTemplate(prefix){
	return $("#" + prefix + "-template");
}

function addEntry(item, $list, prefix, template){
	
	if($("#" + prefix + "_" + item.value).length > 0) {
		return;
	}
	
	var $clone = getTemplate(prefix).clone();
	
	var label = getLabel($clone, item);
	
	$clone.find("span.text").text(label);
	$clone.find("input[name='" + prefix + "']").removeAttr("disabled").val(item.value);
	$clone.find("input[name='" + prefix + "-name']").removeAttr("disabled").attr('name', prefix + "-name" + item.value).val(label);
	$clone.attr("id", prefix + "_" + item.value);
	$clone.attr("class", prefix + "-list-entry");
	
	var $deleteButton = initDeleteButton($clone, $list, prefix);
	$deleteButton.attr("title", $deleteButton.attr("title") + " " + label);
	
	$list.find("li:last").before($clone);
	$clone.show();
	
	$list.trigger("change");
}

function getLabel($template, item){
	var label = item.Name;
	
	var showUsername = $template.hasClass("show-username-true");
	var showEmail = $template.hasClass("show-email-true");
	
	if(showUsername){
		label += " (" + item.Username;
	}
	
	if(showEmail){
		if(showUsername){
			label += ", ";
		} else {
			label += " (";
		}
		label += item.Email;
	}
	
	if(showUsername || showEmail){
		label += ")";
	}
	
	return label;
}

function initDeleteButton($entry, $list, prefix) {
	
	var $deleteButton = $entry.find("a.delete");
	
	$deleteButton.click(function(e) {
		e.preventDefault();
//		$("#" + prefix + "_" + $entry.find("input[type='hidden']").val()).removeClass("disabled").show();
		$entry.remove();
		
		$list.trigger("change");
		
	});
	
	return $deleteButton;
}
