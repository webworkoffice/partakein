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
	
	Partake.prototype.account = {
		// Gets events of account. 
		getEvents: function(queryType, finished, offset, limit) {
			var arg = {
				queryType: queryType,
				finished: finished,
				offset: offset,
				limit: limit
			};
			
			return $.post('/api/account/events', arg);
		}
	};
	
	// TODO: All methods should be moved to the account object.
	
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
	
	Partake.prototype.event = {
		partake: this,
		
		create: function(eventArgs) {
			var arg = {
				sessionToken: partake.sessionToken	
			};
			for (var s in eventArgs)
				arg[s] = eventArgs[s];
			return $.post('/api/event/create', arg);
		},
		
		remove: function(eventId) {
			var arg = {
				sessionToken: partake.sessionToken,
				eventId: eventId
			};
			
			return $.post('/api/event/remove', arg);
		},
		
		removeComment: function(commentId, eventId) {
			var arg = {
				sessionToken: partake.sessionToken,
				commentId: commentId,
				eventId: eventId,
			};
			
			return $.post('/api/event/removeComment', arg);
		}
	};
	
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
	// Event
	
	Partake.prototype.message = {
		partake: this,
		
		sendMessage: function(eventId, message) {
			var arg = {
				sessionToken: partake.sessionToken,
				eventId: eventId,
				message: message
			};
			
			return $.post('/api/event/sendMessage', arg);
		}
	};
	
	// ----------------------------------------------------------------------

	// expose partake client to global.
	createPartakeClient = function(sessionToken) {
		return new Partake(sessionToken);
	};
})();


 
