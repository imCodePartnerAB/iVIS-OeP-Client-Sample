$(document).ready(function() {
	
	$("#formatValidator").change(function() {
		
		var className = $("#formatValidator option:selected").val();
		
		var $messageInput = $("#invalidFormatMessage");
		
		if(className != "") {
		
			var validationMessage = $("#validatorMessage-" + className.replace(/\./g,"_")).val();
	
			if(validationMessage != null) {
				$messageInput.val(validationMessage);
			} else {
				$messageInput.val("");
			}
			
			$messageInput.parent().parent().show();
			
		} else {
			
			$messageInput.parent().parent().hide();
			
		}
		
	});
	
	var selectedValue = $("#formatValidator option:selected").val();
	
	if(selectedValue == "") {
		$("#invalidFormatMessage").parent().parent().hide();
	}
	
});