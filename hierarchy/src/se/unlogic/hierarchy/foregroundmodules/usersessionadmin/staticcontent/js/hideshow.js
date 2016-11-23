function hideShow(id){
	if (document.getElementById){
		var element=document.getElementById(id);
		
		if (element) {
			if (element.style.display == 'none' || element.style.display == '') {
				element.style.display = 'block';
			} else {
				element.style.display = 'none';
			}
		}
	}		    
}