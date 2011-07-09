// partake の基本 API を便利に使うための javascript library です。jQuery に依存します。

// *** UNDER IMPLEMENTATION ***

(function() {
	/**
	 * @param {!String} sessionToken
	 * @returns {createPartakeClient}
	 */
	function $partake(sessionToken) {
		this.sessionToken = sessionToken;
	}

	/**
	 * @param {!String} userId
	 * @param {!String} eventId
	 * @param {!String} status
	 * 
	 * for example
	 * $partake.changeAttendance(...).success(function(json) {...} ).error(function(json) {...});
	 */
	$partake.prototype.changeAttendance = function(userId, eventId, status) {
		var arg = {
			userId: userId,
			eventId: eventId,
			status: status,
			sessionToken: this.sessionToken 
		};
		
		return $.post('/api/attendance/change', arg);
	};
	
	// expose partake client to global.
	createPartakeClient = function(sessionToken) {
		return new $partake(sessionToken);
	};
})();


 
