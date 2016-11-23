$(document).ready(function () {

    setQueryRequiredFunctions["DropDownQueryInstance"] = makeDropDownQueryRequired;

});

function initDropDownQuery(queryID, flowInstanceID) {

    var $query = $("#query_" + queryID);

    $query.find("select").change(
        function () {
            onChange($query.find("select"), queryID, flowInstanceID);
        }
    );

    if ($query.hasClass("hasFreeTextAlternative")) {

        var $select = $query.find("select");

        $select.change(function (e, data) {

            var $this = $(this);

            if ($this.val() == "freeTextAlternative") {
                $("#" + $this.attr("name") + "Value").removeAttr("disabled").parent().show();
            } else {
                $("#" + $this.attr("name") + "Value").attr("disabled", "disabled").parent().hide();
            }

            if (data == undefined || !data.manual) {

                if ($query.hasClass("enableAjaxPosting")) {

                    runDropDownEvaluators($this, queryID);

                }
                ;

            }
        });

        $select.trigger("change", [{manual: true}]);

    } else {

        if ($query.hasClass("enableAjaxPosting")) {

            $query.find("select").change(function () {

                runDropDownEvaluators($(this), queryID);
            });

        }

    }

}

function onChange(select, queryID, flowInstanceID) {
    var xsdElementName = select.data("elementName");

    var dataSource = null;
    eval("dataSource = dataSource" + queryID + ";");
    var id = select.val();
    var entity = null;

    for (var i = 0; i < dataSource.length; i++) {
        var e = dataSource[i];
        if (e.id == id) {
            entity = e;
            break;
        }
    }


    $("[data-dependency-source='" + xsdElementName + "']").each(
        function (index, element) {
            element = $(element);
            if (element.length != 0) {
                var value = null;
                try {
                    eval("value = entity." + element.data("dependencyField") + ";");
                    if (element.is("span")) {
                        element.html(value);
                    } else {
                        element.val(value);
                    }
                } catch (err) {
                }
            }
        }
    )

    var url = "/linkeddropdown/update/" + queryID + "/" + id + "/" + flowInstanceID;
    $.get(url);
}


function runDropDownEvaluators($this, queryID) {

    var parameters = {};

    parameters[$this.attr("name")] = $this.val();

    runQueryEvaluators(queryID, parameters);

}

function makeDropDownQueryRequired(queryID) {

    $("#query_" + queryID).find(".heading-wrapper h2").addClass("required");

}