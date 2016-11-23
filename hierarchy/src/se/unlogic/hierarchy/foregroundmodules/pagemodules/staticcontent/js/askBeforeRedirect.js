function askBeforeRedirect(message,url){
	if (confirm(message)){
		window.location = url;
	}
}	