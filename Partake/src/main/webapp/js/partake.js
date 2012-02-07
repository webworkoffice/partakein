// partake の基本 API を便利に使うための javascript library です。jQuery に依存します。

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
	
	// ----------------------------------------------------------------------

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


 
