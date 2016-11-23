$(document).ready(function() {
	
	setQueryRequiredFunctions["TextFieldQueryInstance"] = makeTextFieldQueryRequired;
	
});

function initTextFieldQuery(queryID) {
	
	$("#query_" + queryID).find(".input-error input").tooltip({
		position : {
			my : "right top-38",
			at : "right+3 top",
			collision : "none"
		},
		track : false,
		content : function() {
			return $(this).next().attr("title") + "<span class=\"marker\"></span>";
		},
		show : {

			effect : 'none'
		},
		hide : {
			effect : 'none'
		}
	}).off("mouseover mouseout");
	
}

function makeTextFieldQueryRequired(queryID) {
	
	$("#query_" + queryID).find(".heading-wrapper h2").addClass("required");
	
}
