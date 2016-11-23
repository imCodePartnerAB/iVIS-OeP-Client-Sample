function hideShowParamTable(){
	var radioButton = document.getElementById('radiobutton');
	var subTable = document.getElementById('subtable');
	
	if(radioButton != null && subTable != null){
		
		if(radioButton.checked == true){
			subTable.style.display = '';
		}else{
			subTable.style.display = 'none';
		}					
	}
}