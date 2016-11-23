function viewComments(showAll){
	document.viewComments.viewComments.value=showAll;
	document.viewComments.submit();
}

var picSelectionState = 0;

function togglePicSelection(phrase1,phrase2,phrase3) {
	if(picSelectionState==2) {
		deselectAllPictures();
		$("#pictureSelectionButton").text(phrase1);
	} else if(picSelectionState==1){
		selectAllPictures();
		$("#pictureSelectionButton").text(phrase3);
	} else {
		selectPictures();
		$("#pictureSelectionButton").text(phrase2);		
	}
}

function selectAllPictures() {
	$("input[name='delete']").each(function() {
	    $(this).attr("checked","checked");
	    $(this).parent().css("display","block");
	});
	picSelectionState = 2;
}

function deselectAllPictures() {
	$("input[name='delete']").each(function() {
		$(this).removeAttr("checked");
		$(this).parent().css("display","none");
		$("#picSelectionSubmitButton").css("display","none");
	});
	picSelectionState = 0;
}

function selectPictures() {
	$("#picSelectionSubmitButton").css("display","block");
	$("input[name='delete']").parent().css("display","block");
	picSelectionState = 1;
}