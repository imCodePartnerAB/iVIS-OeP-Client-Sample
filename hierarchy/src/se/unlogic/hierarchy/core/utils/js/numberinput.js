(function (OH) {
	OH.setNumberInput = (function (elements, params, selector) {
		if (params === undefined) { params = {}; }
		
		var defaultParams = {
			numberOfDecimals: 0,
			lowestNumber: 0,
			highestNumber: 9,
			replaceDot: false,
			replaceComma: true
		};
		
		params = $.extend({}, defaultParams, params);
		params.replaceComma = !params.replaceDot ? true : false; // Always replace comma if not replace dot
		
		var fixValue = (function (value, params) {
			if (value === "") {
				return "";
			}
			
			if (value === "," || value === ".") { // If only added , or . we add 0 before to make it a number
				return 0 + value;
			}
			
			var fixedValue = value.replace(",", "\.");
			
			var decimalLength = 0
			if (fixedValue.indexOf("\.") !== -1) {
		        decimalLength = fixedValue.split(".")[1].length || 0;
			}
		    
			if (decimalLength > params.numberOfDecimals) {
				 fixedValue = fixedValue.substring(0, fixedValue.indexOf("\.") + params.numberOfDecimals + 1);
			}
			
			if (params.replaceDot) {
				fixedValue = fixedValue.replace("\.", ",");
			}
			
			return fixedValue;
		});
		
		$.each(elements, function () {
		  $(this).on("keydown", selector, function (event) {
		    if (params.numberOfDecimals === 0 && (event.keyCode === 188 || event.keyCode === 110 || event.keyCode === 190)) // If no decimals allowed
		    {
		      event.preventDefault();
		    }
		    else if (event.keyCode === 46 || event.keyCode === 8 || event.keyCode === 190 || event.keyCode === 188 || event.keyCode === 9 || event.keyCode === 27 || event.keyCode === 13 || // Allow: backspace, delete, dot, comma, tab, escape and enter.
		      // Allow: Comma/dot in numpad
		      (event.keyCode === 110) ||
		      // Allow: Ctrl+A
		      (event.keyCode === 65 && event.ctrlKey === true) ||
		      // Allow: home, end, left, right
		      (event.keyCode >= 35 && event.keyCode <= 39)) {
		
		      if (($(this).val().indexOf(",") !== -1 || $(this).val().indexOf(".") !== -1)  && (event.keyCode === 190 || event.keyCode === 188 || event.keyCode === 110)) {
		        event.preventDefault();
		      }
		    }
		    else {
		      var value = $(this).val();
		      
		      if (event.shiftKey || (event.keyCode < 48+params.lowestNumber || event.keyCode > 48+params.highestNumber) && (event.keyCode < 96+params.lowestNumber || event.keyCode > 96+params.highestNumber)) {
		        event.preventDefault();
		      }
		    }
		  }).on("keyup", selector, function () {
			  $(this).val(fixValue($(this).val(), params));
		  }).on("paste", selector, function () {
		      return false;
		  }).val(fixValue($(this).val(), params));
		});
	});
})(OH);