$(document).ready(function() {
	
	$("input[type='radio']").change(function() {
		
		var $flowFamilies = $("#chooseFlowFamilies");
		
		if(this.value == "true") {

			$flowFamilies.find("input[type='checkbox']").attr("disabled", "disabled");
			$flowFamilies.hide();
			
		} else if(this.value == "false") {
			
			$flowFamilies.find("input[type='checkbox']").removeAttr("disabled");
			$flowFamilies.show();
			
		}
		
	});
	
	var $flowFamilies = $("#chooseFlowFamilies").find(".flowfamily");
	
	$flowFamilies.sort(function(item1, item2) {
		return $(item1).data("name").localeCompare($(item2).data("name").toUpperCase());
	});
	
	$("#chooseFlowFamilies").html($flowFamilies);
	
	$("input[type='radio']:checked").trigger("change");
	
	$("#startTime, #endTime").timePicker({
		startTime: "00:00",
		endTime: "23:59",
		step: 15
	});
	
});