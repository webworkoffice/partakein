
function onReady() {
    // passcode
    function checkPasscode() {
        if ($("#secret").is(":checked")) {
            $("#passcode").attr('disabled', null);
        } else {
            $("#passcode").attr('disabled', "disabled");                
        }
    }

    checkPasscode();
    $("#secret").change(checkPasscode);

    // enddate
    function checkEndDate() {
        if ($("#usesEndDate").is(":checked")) {
            $("#eyear").attr('disabled', null);
            $("#emonth").attr('disabled', null);
            $("#eday").attr('disabled', null);
            $("#ehour").attr('disabled', null);
            $("#emin").attr('disabled', null);
        } else {
            $("#eyear").attr('disabled', 'disabled');
            $("#emonth").attr('disabled', 'disabled');
            $("#eday").attr('disabled', 'disabled');
            $("#ehour").attr('disabled', 'disabled');
            $("#emin").attr('disabled', 'disabled');
        }
    }
    
    checkEndDate();
    $("#usesEndDate").change(checkEndDate);

    // 
    function checkDeadline() {
        if ($("#usesDeadline").is(":checked")) {
            $("#dyear").attr('disabled', null);
            $("#dmonth").attr('disabled', null);
            $("#dday").attr('disabled', null);
            $("#dhour").attr('disabled', null);
            $("#dmin").attr('disabled', null);
        } else {
            $("#dyear").attr('disabled', 'disabled');
            $("#dmonth").attr('disabled', 'disabled');
            $("#dday").attr('disabled', 'disabled');
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
