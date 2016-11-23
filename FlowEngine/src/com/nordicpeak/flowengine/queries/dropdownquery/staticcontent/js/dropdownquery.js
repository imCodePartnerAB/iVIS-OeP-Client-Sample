$(document).ready(function() {
	
	setQueryRequiredFunctions["DropDownQueryInstance"] = makeDropDownQueryRequired;
	
});

function initDropDownQuery(queryID) {
	
	var $query = $("#query_" + queryID);
	
	if($query.hasClass("hasFreeTextAlternative")) {
		
		var $select = $query.find("select");
		
		$select.change(function(e, data) {
			
			var $this = $(this);
			
			if($this.val() == "freeTextAlternative") {
				$("#" + $this.attr("name") + "Value").removeAttr("disabled").parent().show();
			} else {
				$("#" + $this.attr("name") + "Value").attr("disabled", "disabled").parent().hide();
			}
			
			if(data == undefined || !data.manual) {
			
				if($query.hasClass("enableAjaxPosting")) {
					
					runDropDownEvaluators($this, queryID);
					
				};
			
			}
			
		});
		
		$select.trigger("change", [{manual: true}]);
		
	} else {
		
		if($query.hasClass("enableAjaxPosting")) {
			
			$query.find("select").change(function() {
				
				runDropDownEvaluators($(this), queryID);
				
			});
			
		}
		
	}
	
}

function runDropDownEvaluators($this, queryID) {
	
	var parameters = {};
	
	parameters[$this.attr("name")] = $this.val();
	
	runQueryEvaluators(queryID, parameters);
	
}

function makeDropDownQueryRequired(queryID) {
	
	$("#query_" + queryID).find(".heading-wrapper h2").addClass("required");
	
}