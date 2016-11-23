function hideShow(id){
	if (document.getElementById){
		var element=document.getElementById(id);
		
		if (element) {
			
			if (element.style.display != 'none') {
				element.style.display = 'none';
			} else {
				element.style.display = 'block';
			}
		}
	}		    
}
