var flowInstanceCountWeeks = null;
var flowInstanceCountValues = null;
var flowFamilyCountWeeks = null;
var flowFamilyCountValues = null;
var ratingWeeks = null;
var ratingValues = null;
var steps = null;
var stepAbortCount = null;
var stepUnsubmittedCount = null;
var familyID = null;
var moduleURL = null;

$(document).ready(function() {
	
	if(flowInstanceCountWeeks != null && flowInstanceCountValues != null){
		
		var cols = new Array();
		
		cols.push(flowInstanceCountValues);
		
		var chart = c3.generate({
			bindto: '#flowInstanceCountChart',
		    data: {
		        columns: cols,
		        type: 'area',
		    },
		    axis: {
		        x: {
		            type: 'category',
		            categories: flowInstanceCountWeeks
		        }
		    },
		    legend: {
		        hide: true
		    }
		});
	}
	
	if(flowFamilyCountWeeks != null && flowFamilyCountValues != null){
		
		var cols = new Array();
		
		cols.push(flowFamilyCountValues);
		
		var familyCountChart = c3.generate({
			bindto: '#flowFamilyCountChart',
		    data: {
		        columns: cols,
		        type: 'area',
		    },
		    axis: {
		        x: {
		            type: 'category',
		            categories: flowFamilyCountWeeks	            
		        }
		    },
		    legend: {
		        hide: true
		    }
		});
	}	
	
	if(ratingWeeks != null && ratingValues != null){
		
		var cols = new Array();
		
		cols.push(ratingValues);
		
		var ratingChart = c3.generate({
			bindto: '#ratingChart',
		    data: {
		        columns: cols,
		        type: 'area',
		    },
		    axis: {
		        x: {
		            type: 'category',
		            categories: ratingWeeks	            
		        }
		    },
		    legend: {
		        hide: true
		    }
		});
		
		ratingChart.axis.range({max: {y: 5}, min: {y: 1}});
	}	
	
	if(steps != null && stepAbortCount != null){
		
		var cols = new Array();
		
		cols.push(stepAbortCount);
		
		var chart1 = c3.generate({
			bindto: '#flowStepAbortCountChart',
		    data: {
		        columns: cols,
		        type: 'bar'
		    },
		    bar: {
		        width: {
		            ratio: 0.8
		        }
		    },
		    axis: {
		        x: {
		            type: 'category',
		            categories: steps
		        }
		    },
		    legend: {
		        hide: true
		    }

		});
		
		$("#abortVersion").change(function(e){
			
		    $.getJSON( moduleURL + "/versionabortcount/" + familyID + "/" + $(this).val(), function( json ) {
		    	
		    	var values = new Array();
		    	values.push(json.count);
		    	
		    	chart1.load({
		            columns: values,
		            categories: json.steps
		        });			    	
		    });
		    
		    $("#abortlink").attr("href",$("#abortbaseurl").val() + $(this).val());
		});	
	}
	
	if(steps != null && stepUnsubmittedCount != null){
		
		var cols = new Array();
		
		cols.push(stepUnsubmittedCount);
		
		var chart2 = c3.generate({
			bindto: '#flowStepUnsubmittedCountChart',
		    data: {
		        columns: cols,
		        type: 'bar'
		    },
		    bar: {
		        width: {
		            ratio: 0.8
		        }
		    },
		    axis: {
		        x: {
		            type: 'category',
		            categories: steps
		        }
		    },
		    legend: {
		        hide: true
		    }

		});
		
		$("#unsubmittedVersion").change(function(e){
			
		    $.getJSON( moduleURL + "/versionunsubmitcount/" + familyID + "/" + $(this).val(), function( json ) {
		    	
		    	var values = new Array();
		    	values.push(json.count);
		    	
		    	chart2.load({
		            columns: values,
		            categories: json.steps
		        });			    	
		    });
		    
		    $("#unsubmitlink").attr("href",$("#unsubmitbaseurl").val() + $(this).val());
		});			
	}
});