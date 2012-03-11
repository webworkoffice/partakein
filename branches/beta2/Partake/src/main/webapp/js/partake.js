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
		partake: this,
		
		// Gets events of account. 
		getEvents: function(queryType, offset, limit) {
			var arg = {
				queryType: queryType,
				offset: offset,
				limit: limit
			};
				
			return $.post('/api/account/events', arg);
		},
		
		getEnrollments: function(offset, limit) {
			var arg = {
				offset: offset,
				limit: limit
			};

			return $.post('/api/account/enrollments', arg);			
		},
		
		getImages: function(offset, limit) {
			var arg = {
				offset: offset,
				limit: limit
			};
			
			return $.post('/api/account/images', arg);
		},
		
		setPreference: function(receivingTwitterMessage, profilePublic, tweetingAttendanceAutomatically) {
			var arg = {
				sessionToken: partake.sessionToken,
				receivingTwitterMessage: receivingTwitterMessage,
				profilePublic: profilePublic,
				tweetingAttendanceAutomatically: tweetingAttendanceAutomatically
			};
			
			return $.post('/api/account/setPreference', arg);
		},
		
		removeOpenID: function(identifier) {
			var arg = {
				sessionToken: partake.sessionToken, 
				identifier: identifier
			};
			
			return $.post('/api/account/removeOpenID', arg);
		},
		
		revokeCalendar: function() {
			var arg = {
				sessionToken: partake.sessionToken			
			};
			
			return $.post('/api/account/revokeCalendar', arg);
		}
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

		modify: function(eventId, eventArgs) {
			var arg = {
				sessionToken: partake.sessionToken,
				eventId: eventId
			};
			for (var s in eventArgs)
				arg[s] = eventArgs[s];
			return $.post('/api/event/modify', arg);
		},

		remove: function(eventId) {
			var arg = {
				sessionToken: partake.sessionToken,
				eventId: eventId
			};
			
			return $.post('/api/event/remove', arg);
		},
		
		postComment: function(eventId, comment) {
			var arg = {
				sessionToken: partake.sessionToken,
				eventId: eventId,
				comment: comment
			};
			
			return $.post('/api/event/postComment', arg);
		},
		
		removeComment: function(commentId) {
			var arg = {
				sessionToken: partake.sessionToken,
				commentId: commentId,
			};
			
			return $.post('/api/event/removeComment', arg);
		},
		
		enroll: function(eventId, status, comment) {
			var arg = {
					sessionToken: partake.sessionToken,
					eventId: eventId,
					status: status,
					comment: comment
				};
				
				return $.post('/api/event/enroll', arg);
		},
		
		changeEnrollmentComment: function(eventId, comment) {
			var arg = {
				sessionToken: partake.sessionToken,
				eventId: eventId,
				comment: comment
			};
			
			return $.post('/api/event/enroll/changeComment', arg);
		},
		
		makeAttendantVIP: function(userId, eventId, vip) {
			var arg = {
				sessionToken: partake.sessionToken,
				userId: userId,
				eventId: eventId,
				vip: vip
			};
			
			return $.post('/api/event/makeAttendantVIP', arg);
		},
			
		removeAttendant: function(userId, eventId) {
			var arg = {
				sessionToken: partake.sessionToken,
				userId: userId,
				eventId: eventId
			};
			
			return $.post('/api/event/removeAttendant', arg);
		},
		
		changeAttendance: function(userId, eventId, status) {
			var arg = {
				sessionToken: partake.sessionToken,
				userId: userId,
				eventId: eventId,
				status: status
			};
			
			return $.post('/api/event/attend', arg);
		}
	};

	// ----------------------------------------------------------------------
	// Message
	
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


 
