$(document).ready(function() {

	$(".modal .close").click(function(e) {
		e.preventDefault();
		$(this).parent().fadeOut("fast", function() {
			$(this).remove();
		});
	});
	
	$.tablesorter.addWidget({
		id : "onSortEvent",
		format : function(table) {
			$("thead th.headerSortDown span", table).attr("data-icon-after", "^");
			$("thead th.headerSortUp span", table).attr("data-icon-after", "_");
		}
	});

	$("table thead.sortable").each(function() {

		var $table = $(this).parent();

		var rows = $table.find("tbody tr");

		if (rows.length > 1) {
			var columns = $table.find("thead tr th");

			var sortColumn = columns.length - 3;
			var headers = "{ 0: { sorter: false }, " + (columns.length - 1) + ": { sorter: false } }";
			$table.tablesorter({
				widgets : [ 'zebra', 'onSortEvent' ],
				headers : eval("(" + headers + ")"),
				sortList : [ [ sortColumn, 1 ] ]
			});
		}

	});

	$("#mobilePhone").keyup(function() {
		
		if($(this).val() != "") {
			$("#contactBySMS").removeAttr("disabled").next("label").removeClass("disabled");
		} else {
			$("#contactBySMS").removeAttr("checked").attr("disabled", "disabled").next("label").addClass("disabled");
		}
		
	});
	
	$("#mobilePhone").trigger("keyup");

});