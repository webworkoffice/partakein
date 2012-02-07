
function onReady() {
    var dialogIDs = ["signin-dialog", "enroll-form", "reserve-form", "cancel-form",
	                   "event-delete-form", 
	                   "message-form", "twitter-promotion-form",
	                   "change-comment-form", "reminder-reset-form"];

    var defaultDialogOptions = {
		autoOpen: false,
		draggable: false,
		modal: true,
		resizable: true				    		
    };
    
    var dialogOptions = {
    		"signin-dialog": { width: 500, resizable: false },
    		"enroll-form": { width: 350, resizable: false },
    		"reserve-form": { width: 500, resizable: false },
    		"cancel-form": { width: 350, resizable: false },
    		"change-comment-form": { width: 350, resiable: false }
    };
    
    function merge(option, mergingOption) {
    	if (!mergingOption) { return; }
    	for (var s in mergingOption) {
    		option[s] = mergingOption[s];
    	}
    }
    
	for (var i = 0; i < dialogIDs.length; ++i) {
		var option = {};
		merge(option, defaultDialogOptions);
		merge(option, dialogOptions[dialogIDs[i]]);
		
		$("#" + dialogIDs[i]).dialog(option);		
		$("#open-" + dialogIDs[i]).click((function(i) {
			return function(event) {
				$("#" + dialogIDs[i]).dialog('open');
			};
		})(i));
	}

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
