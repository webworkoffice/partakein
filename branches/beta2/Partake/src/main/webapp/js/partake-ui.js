//
// partake-ui.js is a JavaScript library which implements a user interface of PARTAKE.
//

(function() {

	// Exposes PartakeUI.
	function PartakeUI() {
	}

	PartakeUI.prototype.spinner = function(targetElement) {
		var SPINNER_HEIGHT = 15, SPINNER_WIDTH = 15;
		
		var parent = $(targetElement.parentNode);
		if (!parent.hasClass('spinner-container')) {
			if (window.console)
				console.log('partakeUI.createSpinner: invalid argument.');
			return null;
		}
		
		var spinner = $(targetElement.nextElementSibling);
		if (spinner.length == 0 || !spinner.hasClass('spinner')) {
			spinner = document.createElement('img');
			spinner.className = 'spinner';
			spinner.src = "/img/spinner.gif";
			spinner = $(spinner);
			spinner.insertAfter(targetElement);
		}
		
		var top = $(targetElement).position().top + $(targetElement).outerHeight() / 2 - SPINNER_HEIGHT / 2;
		var left = $(targetElement).position().left + $(targetElement).outerWidth() / 2 - SPINNER_WIDTH / 2;
		
		spinner.css("position", "absolute");
		spinner.css("top", top);
		spinner.css("left", left);
        
        return spinner;
	};
	
	// expose partake client to global.
	createPartakeUIClient = function() {
		return new PartakeUI();
	};

})();