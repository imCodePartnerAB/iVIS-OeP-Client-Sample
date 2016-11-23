var flashFixApplied = false;

function fixFlashWMode() {
	
	if(flashFixApplied){
		
		return;
	}
	
	$("embed").each(function() {
		var $this = $(this);
		var clone = $this.clone(true);
		clone.attr("wmode", "transparent");
		$this.before(clone);
		$this.remove();
	});
	
	$('iframe').each(function() {
		
		var url = $(this).attr("src")
				
		if(url != undefined && (url.lastIndexOf("http://www.youtube.com/embed/",0) === 0 || url.lastIndexOf("https://www.youtube.com/embed/",0) === 0) && !endsWith(url,"wmode=transparent")){
			
			if(url.indexOf("?") != -1){
				
				$(this).attr("src",url+"&wmode=transparent");
				
			}else{
				
				$(this).attr("src",url+"?wmode=transparent");
			}
		}
	});
	
	flashFixApplied = true;
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}