var imagePath;
var deleteFile = "Delete";

function initQloader($qloader) {
	
	$qloader.each(function(i){
		
		var $this = $(this);
		var limit = ($this.hasClass("qloader-limit-1") ? 1 : 0);
		
		$this.qloader({
			limit: limit,
			filerow_element: '<li class="finished"><div class="file"><span class="name"><img src="'+imagePath+'/file.png"/ class=\"vertical-align-middle marginright\"><span class="filename"></span></span><span class="italic"></span><a data-icon-after="t" href="javascript:void(0)" class="progress">' + deleteFile + '</a></div><div class="progressbar"><div style="width: 100%;" class="innerbar"></div></div></li>',
			use_element_index: false,
			filename_selector: 'span.filename',
			remove_selector: 'a.progress',
			file_container_element: $("#qloader-filelist"),
			filelist_before: null,
		}).bind("beforeDelete.qloader", function(event,filerow){
			$("#"+$(filerow).attr("rel")).remove();
		});
	});
	
}

function removeFile(e, id, deleteMessage, element) {
	
	if(e.preventDefault) {
		e.preventDefault();
	} else {
		e.returnValue = false;
	}
	
	if(confirm(deleteMessage)) {
		
		$(element).parent().parent().replaceWith("<input type=\"hidden\" name=\"" + id + "\" />");
		
	}
	
}