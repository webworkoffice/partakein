
function onReady() {


    // 
    function checkDeadline() {
        if ($("#usesDeadline").is(":checked")) {
            $("#ddate").attr('disabled', null);
            $("#dhour").attr('disabled', null);
            $("#dmin").attr('disabled', null);
        } else {
            $("#ddate").attr('disabled', 'disabled');
            $("#dhour").attr('disabled', 'disabled');
            $("#dmin").attr('disabled', 'disabled');
        }
    }

    checkDeadline();
    $("#usesDeadline").change(checkDeadline);
}

function codePointCount(str) {
	return !str ? 0 : str.replace(/[\uD800-\uDBFF][\uDC00-\uDFFF]/g, '*').length;
}

jQuery(onReady);
// $(document).ready(onReady);
