var contextPath;
var registrationModuleURI;
var registrationLoadingIconHTML = "<span class=\"loading-icon\" style=\"height: 250px\" />"
var redirectURI = null;

$(document).ready(function(){
	
	if(registrationModuleURI != undefined) {
		var registrationAlias = "#" + registrationModuleURI.substring(registrationModuleURI.lastIndexOf("/")+1 , registrationModuleURI.length);
		
		var hash = document.location.hash;
		
		var hashParts = hash.split("?");
		
		if(hashParts[0] == registrationAlias) {
			initRegistrationDialog(getRequestParameters()["requesteduri"]);
		}
		
		$(window).bind('hashchange', function () {
			
			var hash = document.location.hash;
			var hashParts = hash.split("?");
			if(hashParts[0] == registrationAlias) {
				initRegistrationDialog(getRequestParameters()["requesteduri"]);
			}
			
		});
		
		fixFlashWMode();
	}
});

function initRegistrationDialog(redirectValue) {
	
	var $registrationDialog = $("#registration-dialog");

	$registrationDialog.dialog({
		autoOpen: true,
		width: 600,
		minHeight: 520,
		modal: true,
		resizable: false,
		position: ['center',120],
		dialogClass: "oh-ui-dialog",
		open: function() {
			$registrationDialog.html(registrationLoadingIconHTML);
			$registrationDialog.load(registrationModuleURI + "?onlymodulehtml=true", function(response, status, xhr) {
				$(this).fadeIn(500);
				
				$registrationDialog.html(response);
				
				if(redirectValue != undefined) {
					redirectURI = contextPath + decodeURIComponent(redirectValue);
				}
				
				bindRegistrationEvents($registrationDialog);
			});
			
	    },
		close: function(event, ui) { 
			window.location.hash = "";
		}
	});
	
}

function register(dialog, form){
	
	var registrationParams = {
		"firstname" : getInputValue(form, "firstname"),
		"lastname" : getInputValue(form, "lastname"),
		"email" : getInputValue(form, "email"),
		"emailConfirmation" : getInputValue(form, "emailConfirmation"),
		"username" : getInputValue(form, "username"),
		"password" : getInputValue(form, "password"),
		"passwordConfirmation" : getInputValue(form, "passwordConfirmation"),
		"captchaConfirmation" : getInputValue(form, "captchaConfirmation"),
		"birthYear" : form.find("select[name='birthYear']").val(),
		"gender" : form.find("input[name='gender']:checked").val(),
		"userConditionConfirmation" : form.find("input[name='userConditionConfirmation']").is(':checked'),
		"onlymodulehtml" : true
	};
	
	form.find(".attributeField").each(function() {
		var $this = $(this);
		registrationParams[$this.attr("name")] = $this.val();
	});
	
	dialog.html(registrationLoadingIconHTML);
	
	$.ajax({
		type: "POST",
		cache: false,
		url: form.attr("action"),
		data: registrationParams,
		dataType: "html",
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		error: function (xhr, ajaxOptions, thrownError) {  },
		success: function(response) {
			
			if(redirectURI != null) {
				window.location = redirectURI;
			} else {
				window.location = window.location.href.split('#')[0];
			}
			
		},
		statusCode: {
			401: function(xhr, ajaxOptions, thrownError) {
				dialog.html(xhr.responseText);
				var $captcha = $("#captchaimg");
				$captcha.attr("src", $captcha.attr("src") + "?" + (new Date()).getTime());
				bindRegistrationEvents(dialog);
		    },
		    403: function(xhr, ajaxOptions, thrownError) {
		    	dialog.dialog("close");
		    	window.location = window.location.href.split('#')[0];
		    },
		    500: function(xhr, ajaxOptions, thrownError) {
				document.write(xhr.responseText);
		    }
		}		
	});
	
}

function getInputValue(form, name) {
	return form.find("input[name='" + name + "']").val();
}

function bindRegistrationEvents(dialog) {

	$("#registrationmoduleform").bind("submit", function(event) {
		event.preventDefault();
		register(dialog, $(this));
	});
	
}

function getRequestParameters() {
    
	var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    
    for(var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        
        if(hash[1] != undefined ){
        
        	vars[hash[0]] = hash[1].split('#')[0];
        
        } else{
        
        	vars[hash[0]] = hash[1];
        }
        
    }
    
    return vars;
}
