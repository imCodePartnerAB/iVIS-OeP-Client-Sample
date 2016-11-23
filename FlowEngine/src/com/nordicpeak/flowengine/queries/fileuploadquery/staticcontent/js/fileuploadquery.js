$(document).ready(function() {
	
	setQueryRequiredFunctions["FileUploadQueryInstance"] = makeFileUploatQueryRequired;
	
});

function makeFileUploatQueryRequired(queryID) {
	
	$("#query_" + queryID).find(".heading-wrapper h2").addClass("required");
	
}

function initFileUploadQuery(queryID) {
	
	var $query = $("#query_" + queryID);
	
	var $qloader = $query.find("input[type='file'].qloader.fileuploadquery");
	
	initQloader($qloader);
	
}