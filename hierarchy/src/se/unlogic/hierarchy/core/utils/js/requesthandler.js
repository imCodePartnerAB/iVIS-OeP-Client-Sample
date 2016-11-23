(function (OH) {
	OH.reqHandler = (function () {
	    var _showProgress = false;
	    var _self = this;
	    var _showErrorDialog = true;
	
	    var _request = (function (url, data, success, type, options) {
	    	
	    	var defaults = {
    			showErrDialog: false,
    	        async: true,
    	        traditional: false,
    	        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
    	        beforeSend: ajaxBegin,
	            complete: ajaxComplete,
	            error: function (data) { ajaxFailure(data) },
	            showErrorDialog: true // check if this really works with extra argument to ajax
	    	};
	    	
	    	options = $.extend({}, defaults, options);
	    	
	    	options.url = url;
	    	options.data = data;
	    	options.success = succes;
	    	options.type = type;
	    	
	    	_self._showErrorDialog = options.showErrorDialog;
	        
	        $.ajax(options);
	    });
	
	    var post = (function (url, data, success, options) {
	        _request(url, data, success, "POST", options);
	    });
	
	    var get = (function (url, data, success, options) {
	        _request(url, data, success, "GET", options);
	    });
	
	    var ajaxBegin = (function () {
	        _self._showProgress = true;
	
	        setTimeout(function () {
	            if (_self._showProgress) {
	                $('#ajax-spinner').dialog("open");
	            }
	        }, 400);
	    });
	
	    var ajaxComplete = (function () {
	        _self._showProgress = false;
	
	        if ($("#ajax-spinner").hasClass('ui-dialog-content')) {
	            $("#ajax-spinner").dialog("close");
	        }
	
	        // Get new logout timeout after each ajax request since session timeout should have been reset by the request
	        /*
	        $.ajax({
	            url: $("#LoginTimeOutAjaxUrl").val(),
	            type: 'GET',
	            success: function (data) {
	                logoutTimeOut = autoLogoutHandler.getLogoutTimeout(data);
	                autoLogoutHandler.logoutTimeOutNotification();
	            }
	        });
	        */
	    });
	
	    // If failurehandling is to be implemented, proxy for complete for now
	    var ajaxFailure = (function (data, showErrDialog) {
	        if (_self._showErrorDialog) {
	            $("#message-dialog").html(data).dialog("open");
	        }
	
	        ajaxComplete();
	    });
	
	    return {
	        post: post,
	        get: get
	    };
	})();
})(OH);