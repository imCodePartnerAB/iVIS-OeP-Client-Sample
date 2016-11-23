function hideShowParamTable(){
	var radioButton = document.getElementById('blankRadioButton');
	var paramDiv = document.getElementById('paramDiv');
	
	if(radioButton != null && paramDiv != null){
		if(radioButton.checked == true){
			paramDiv.style.display = 'none';
		}else{
			paramDiv.style.display = 'block';
		}					
	}
}