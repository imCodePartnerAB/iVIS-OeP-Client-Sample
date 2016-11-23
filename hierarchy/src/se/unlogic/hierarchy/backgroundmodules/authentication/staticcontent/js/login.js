var contextPath;
var loginModuleURI;
var iframeContent;
var loginLoadingIconHTML = "<span class=\"loading-icon\" style=\"height: 100px\" />"
var redirectURI = null;
var useModalRegistration;
	
$(document).ready(function(){
	
	var loginAlias = "#" + loginModuleURI.substring(loginModuleURI.lastIndexOf("/")+1 , loginModuleURI.length);
				
	if(document.location.hash == loginAlias) {
		initLoginDialog(getRequestParameters()["requesteduri"]);
	}
	
	$(window).bind('hashchange', function () {
		
		if(document.location.hash == loginAlias) {
			initLoginDialog(getRequestParameters()["requesteduri"]);
		}
		
	});	
	
	fixFlashWMode();
});

function initLoginDialog(redirectValue) {
	
	var $loginIframe = $("#login-iframe");
	var $logindialog = $("#login-dialog");
	
	$logindialog.dialog({
		autoOpen: true,
		minHeight: 205,
		width: 470,
		modal: true,
		resizable: false,
		position: ['center',180],
		dialogClass: "oh-ui-dialog",
		open: function() {
			$loginIframe.attr("src", iframeContent);
			$logindialog.html(loginLoadingIconHTML);
			$loginIframe.ready(function() {
				$logindialog.load(loginModuleURI + "?onlymodulehtml=true", function(response, status, xhr) {
					
					$(this).fadeIn(500);
										
					if(redirectValue != undefined) {
						redirectURI = contextPath + decodeURIComponent(redirectValue);
					}
					$logindialog.html(response);
					bindLoginEvents($logindialog, $loginIframe);
				});
			});
			
	    },
		close: function(event, ui) { 
			window.location.hash = "";
			$loginIframe.removeAttr();
		}
	});
	
}

function login(dialog, form, usernameField, passwordField){
	
	var params = {
			"username" : usernameField.val(),
			"password" : passwordField.val(),
			"onlymodulehtml" : true
		}
	
	dialog.html(loginLoadingIconHTML);
	
	$.ajax({
		type: "POST",
		cache: false,
		url: form.attr("action"),
		data: params,
		dataType: "html",
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
		error: function (xhr, ajaxOptions, thrownError) {  },
		success: function(response) {
			// redirect if requesteduri if set
			if(redirectURI != null) {
				window.location = redirectURI;
			} else {
				window.location = window.location.href.split('#')[0];
			}
		},
		statusCode: {
			401: function(xhr, ajaxOptions, thrownError) {
				dialog.html(xhr.responseText);
				bindLoginEvents(dialog,$("#login-iframe"));
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

function bindLoginEvents(dialog, iframe) {
	
	if(useModalRegistration){
	
		$("#registrationLink").bind("click", function(event) {
			dialog.dialog("close");
			var href = $(this).attr("href");
			window.location.hash = "#" + href.substring(href.lastIndexOf("/")+1 , href.length);
			return false;
		});		
	}
	
	$("#newPasswordLink").bind("click", function(event) {
		dialog.dialog("close");
		var href = $(this).attr("href");
		window.location.hash = "#" + href.substring(href.lastIndexOf("/")+1 , href.length);
		return false;
	});
	
	var contents = iframe.contents();
	var $form = $("#loginmoduleform");
	
	var $usernameField = $form.find("input[name='username']");
	var $passwordField = $form.find("input[name='password']");

	$usernameField.val(contents.find("input[name='username']").val());
	$passwordField.val(contents.find("input[name='password']").val());
	
	$form.bind("submit", function(event) {
		event.preventDefault();
		contents.find("form").submit();
		login(dialog, $(this), $usernameField, $passwordField);
	});
	
}

function getRequestParameters()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        
        if(hash[1] != undefined){
        
        	vars[hash[0]] = hash[1].split('#')[0];
        
        }else{
        
        	vars[hash[0]] = hash[1];
        }
        
        
    
    }
    return vars;
}
