var validationError = false;

$(document).ready(function() {
	
	$("table.coloredtable").each(function() {
		var $this = $(this);
		$this.find("tr:not(:first):visible:even").removeClass("odd").addClass("even");
		$this.find("tr:not(:first):visible:odd").removeClass("even").addClass("odd");
	});
	
	$(".sortable").sortable({
		cursor: 'move',
		update: function(event, ui) {
			
			if(validatePosition($(this), ui.item, ui.position)) {
				updateSortOrder($(this));
			}
			
		},
		stop: function(e, ui) {
			
			resetHighlightning();
			
			return validatePosition($(this), ui.item, ui.position);
			
		},
		start: function(e, ui) {
			
			highlightAffectedQueries(ui.item);
			
		}}).children().each(function(i) {
			if(validationError) {
				var item = $(this);
				var itemSortOrder = item.find('input[type="hidden"].sortorder').val();
				if(i != itemSortOrder) {
					item.parent().children().each(function(i) {
						if(itemSortOrder == i) {l
							$(this).before(item.detach());
							return;
						}
					});
				}
			}
	});
	
	$(".sortable").each(function() {
		updateSortOrder($(this));
	});
	
	$("#flowtype").change(function(e) {
		$(".flowTypeCategories").hide();
		$(".flowTypeCategories select").attr("disabled", "disabled");
		$("#flowTypeCategories_" + $(this).val() + " select").removeAttr("disabled");
		$("#flowTypeCategories_" + $(this).val()).show();
	});
	
	$("#flowtype").trigger("change");
	
//	$(".managerlist input[name='group']").change(function(e) {
//		
//		validateManagerUserAccess($(this).val());
//		
//	}).trigger("change");
//	
//	$(".managerlist input[name='user']").change(function(e) {
//		
//		validateManagerUserGroupAccess();
//		
//		var $this = $(this);
//		
//		if(!$this.is(":checked")) {
//			$this.next("input[type='hidden']").remove();
//		}
//		
//	}).trigger("change");
//	
//	$(".managerlist input[name='user'].disabled").each(function(e) {
//
//		var $this = $(this);
//		
//		if($this.is(":checked")) {
//			$this.attr("disabled", "disabled");
//			$this.after($("<input type='hidden' name='user' value='" + $this.val() + "'/>"));
//		}
//		
//	});
	
	$("#typeOfFlow").change(function(e) {
		
		var $flowForm = $("#flowForm");
		
		if($(this).val() == "EXTERNAL") {
			$flowForm.find(".internal").hide();
			$("#enabled").removeAttr("disabled");
			$("#externalLink").removeAttr("disabled").parent().parent().show();
		} else {
			$flowForm.find(".internal").show();
			$("#enabled").attr("disabled", "disabled");
			$("#externalLink").attr("disabled", "disabled").parent().parent().hide();
		}
		
	});
	
	$("#typeOfFlow").trigger("change");
	
});

//function validateManagerUserAccess(groupID) {
//	
//	$(".managerlist input[name='user'].group_" + groupID + ":checked").each(function(i) {
//		
//		var $this = $(this);
//		
//		var groups = $this.attr("class").split(" ");
//		
//		var hasAccess = false;
//		
//		$.each(groups, function(i, group) {
//			var trim = group.trim();
//			if(trim != "") {
//				if($("input[name='group']." + trim + ":checked").length > 0) {
//					hasAccess = true;
//					return false;
//				}
//			}
//		});
//		
//		if(!hasAccess) {
//			$this.attr("disabled", "disabled");
//			$this.after($("<input type='hidden' name='user' value='" + $this.val() + "'/>"));
//		} else {
//			$this.removeAttr("disabled");
//			$this.next().remove();
//		}
//		
//	});
//	
//}
//
//function validateManagerUserGroupAccess() {
//	
//	$(".managerlist input[name='group']:checked").each(function(i) {
//		
//		var $this = $(this);
//		
//		var groupID = $this.val();
//
//		if($("input[name='user'].group_" + groupID + ":checked").length != $("input[name='user'].group_" + groupID).length) {
//			if($this.attr("disabled") == undefined) {
//				$this.attr("disabled", "disabled");
//				$this.after($("<input type='hidden' name='group' value='" + $this.val() + "'/>"));
//			}
//		} else {
//			$this.removeAttr("disabled");
//			$this.next().remove();
//		}
//		
//	});
//	
//}

function updateSortOrder(obj) {
	obj.children().each(function(i) {
		$(this).find("input[type='hidden'].sortorder").val(i);
	});
}

function highlightAffectedQueries($item) {
	
	var $targetQueryIDs = $item.find("input.targetQueryIDs");
	
	if($targetQueryIDs.length > 0) {
		
		$targetQueryIDs.each(function() { $("#query_" + $(this).val()).addClass("affectedQuery");  });
		
	}
	
	var queryID = $item.attr("id").split("_")[1];
	
	var $relatedQueryIDs = $("input[value='" + queryID + "'].targetQueryIDs");
	
	if($relatedQueryIDs.length > 0) {
		
		$relatedQueryIDs.each(function() {
			$("#" + $(this).parent().attr("id")).addClass("affectedQuery");
		});
	
	}
	
}

function resetHighlightning() {
	
	$(".query").removeClass("affectedQuery");
	
}

function validatePosition($sortable, $item, newItemPosition) {
	
	var queryID = $item.attr("id").split("_")[1];
	
	if($($sortable.children(":first")).hasClass("query")) {
		return false;
	}
	
	var isValidPosition = true;
	
	var $targetQueryIDs = $("input[name='targetQueryIDs_" + queryID + "']");
	
	if($targetQueryIDs.length > 0) {
		
		$targetQueryIDs.each(function() {
			
			var $targetQuery = $("#query_" + $(this).val());
			
			if(newItemPosition.top >= $targetQuery.position().top) {
				isValidPosition = false;
				return;
			}
			
		});
		
		if(!isValidPosition) {
			return false;
		}
		
	}

	var $relatedQueryIDs = $("input[value='" + queryID + "'].targetQueryIDs");
	
	if($relatedQueryIDs.length > 0) {
		
		$relatedQueryIDs.each(function() {
			
			var $relatedQuery = $("#" + $(this).parent().attr("id"));
			
			if(newItemPosition.top <= $relatedQuery.position().top) {
				isValidPosition = false;
				return;
			}
			
		});
		
	}
	
	return isValidPosition;
	
}