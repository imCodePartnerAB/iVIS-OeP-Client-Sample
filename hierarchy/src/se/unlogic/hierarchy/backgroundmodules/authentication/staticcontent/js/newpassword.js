var newPasswordModuleURI;
var newPasswordLoadingIconHTML = "<span class=\"loading-icon\" style=\"height: 100px\" />"
	
$(document).ready(function(){
	
	if(newPasswordModuleURI != undefined) {
		
		var newPasswordAlias = "#" + newPasswordModuleURI.substring(newPasswordModuleURI.lastIndexOf("/")+1 , newPasswordModuleURI.length);
		
		var hash = document.location.hash;
		
		var hashParts = hash.split("?");
		
		if(hashParts[0] == newPasswordAlias) {
			initNewPasswordDialog(hashParts);
		}
		
		$(window).bind('hashchange', function () {
			
			var hash = document.location.hash;
			var hashParts = hash.split("?");
			if(hashParts[0] == newPasswordAlias) {
				initNewPasswordDialog(hashParts);
			}
			
		});
		
		fixFlashWMode();
		
	}
});

function initNewPasswordDialog(hashParts) {
	
	var $newPasswordDialog = $("#newpassword-dialog");

	$newPasswordDialog.dialog({
		autoOpen: true,
		width: 600,
		minHeight: 200,
		modal: true,
		resizable: false,
		position: ['center',120],
		dialogClass: "oh-ui-dialog",
		open: function() {
			$newPasswordDialog.html(newPasswordLoadingIconHTML);
			$newPasswordDialog.load(newPasswordModuleURI + "?onlymodulehtml=true", function(response, status, xhr) {
				$(this).fadeIn(500);
				$newPasswordDialog.html(response);
				bindNewPasswordEvents($newPasswordDialog);
			});
			
	    },
		close: function(event, ui) { 
			window.location.hash = "";
		}
	});
	
}

function requestNewPassword(dialog, form){
	
	var newPasswordParams = {
			"username" : getInputValue(form, "username"),
			"email" : getInputValue(form, "email"),
			"captchaConfirmation" : getInputValue(form, "captchaConfirmation"),
			"onlymodulehtml" : true
		}
	
	dialog.html(newPasswordLoadingIconHTML);
	
	$.ajax({
		type: "POST",
		cache: false,
		url: form.attr("action"),
		data: newPasswordParams,
		dataType: "html",
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		error: function (xhr, ajaxOptions, thrownError) {  },
		success: function(response) {
			dialog.html(response);
			bindNewPasswordEvents(dialog);
		},
		statusCode: {
		    500: function(xhr, ajaxOptions, thrownError) {
				document.write(xhr.responseText);
		    }
		}		
	});
	
}

function getInputValue(form, name) {
	return form.find("input[name='" + name + "']").val();
}

function bindNewPasswordEvents(dialog) {

	$("#newpasswordmoduleform").bind("submit", function(event) {
		event.preventDefault();
		requestNewPassword(dialog, $(this));
	});
	
}
