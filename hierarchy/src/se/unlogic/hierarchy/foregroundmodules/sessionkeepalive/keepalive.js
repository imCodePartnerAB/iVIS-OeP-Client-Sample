var keepAliveConnectorURL = "fullalias";
var keepAlivePollFrequency = pollFreq * 1000;


$(document).ready(function() {
		
	setTimeout(checkSessionStatus, keepAlivePollFrequency);
	
});

function checkSessionStatus(){
		
	$.ajax({url: keepAliveConnectorURL, cache: false, success:function(result){
				
		if(result != null && result == 1){
			
			setTimeout(checkSessionStatus, keepAlivePollFrequency);
		
		}else{
		
			alert("Odd result " + result);
		}
	}}); 
}