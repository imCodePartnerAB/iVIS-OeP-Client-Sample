(function (OH) {
	// TODO Add support for 4 numbers in year and a dash before checksum, also validation? optional!
	OH.setPnrInput = (function (selectors) {
        $.each(selectors, function () {
            $(this).keydown(function (event) {
                if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 || // Allow: backspace, delete, tab, escape and enter.
                    // Allow: Ctrl+A
                    (event.keyCode == 65 && event.ctrlKey) ||
                    // Allow: home, end, left, right
                    (event.keyCode >= 35 && event.keyCode <= 39)) {

                    return;
                }
                else if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105)) {
                    event.preventDefault();
                    return false;
                }
                else if ($(this).val().length < 13) {
                    return;
                }
            }).on('paste', function () {
                return false;
            });
        });
    });
})(OH);