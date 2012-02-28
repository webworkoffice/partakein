//
// partake.js is a JavaScript library which calls Partake API. 
// partake.js depends on the latest jQuery.
//

// *** UNDER IMPLEMENTATION ***

(function() {
	/**
	 * @param {!String} sessionToken
	 * @returns {createPartakeClient}
	 */
	function Partake(sessionToken) {
		this.sessionToken = sessionToken;
	}

	// ----------------------------------------------------------------------
	// Account
	
	/**
	 * Removes OpenID.
	 * Usage: 
	 *   partake.removeOpenID(...).success(function(json) { ... }).error(function(json) { ... });
	 */
	Partake.prototype.removeOpenID = function(identifier) {
		var arg = {
			sessionToken: this.sessionToken, 
			identifier: identifier
		};
		
		return $.post('/api/account/removeOpenID', arg);
	};

	Partake.prototype.setPreference = function(receivingTwitterMessage, profilePublic, tweetingAttendanceAutomatically) {
		var arg = {
			sessionToken: this.sessionToken,
			receivingTwitterMessage: receivingTwitterMessage,
			profilePublic: profilePublic,
			tweetingAttendanceAutomatically: tweetingAttendanceAutomatically
		};
		
		return $.post('/api/account/setPreference', arg);
	};
	
	Partake.prototype.revokeCalendar = function() {
		var arg = {
			sessionToken: this.sessionToken			
		};
		
		return $.post('/api/account/revokeCalendar', arg);
	};
	
	// ----------------------------------------------------------------------
	// Event
	
	/**
	 * @param {!String} userId
	 * @param {!String} eventId
	 * @param {!String} status
	 * 
	 * for example
	 * $partake.changeAttendance(...).success(function(json) {...} ).error(function(json) {...});
	 */
	Partake.prototype.changeAttendance = function(userId, eventId, status) {
		var arg = {
			sessionToken: this.sessionToken,
			userId: userId,
			eventId: eventId,
			status: status
		};
		
		return $.post('/api/event/attend', arg);
	};
	
	// ----------------------------------------------------------------------

	// expose partake client to global.
	createPartakeClient = function(sessionToken) {
		return new Partake(sessionToken);
	};
})();


 
