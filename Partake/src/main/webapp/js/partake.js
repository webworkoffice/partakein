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

        getTickets: function(offset, limit) {
            var arg = {
                offset: offset,
                limit: limit
            };

            return $.post('/api/account/tickets', arg);
        },

        getImages: function(offset, limit) {
            var arg = {
                offset: offset,
                limit: limit
            };

            return $.post('/api/account/images', arg);
        },

        getMessages: function(offset, limit) {
            var arg = {
                offset: offset,
                limit: limit
            };

            return $.post('/api/account/messages', arg);
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

        create: function(title, beginDate, endDate) {
            var arg = {
                sessionToken: partake.sessionToken,
                title: title,
                beginDate: beginDate,
                endDate: endDate,
            };
            return $.post('/api/event/create', arg);
        },

        modify: function(eventId, params) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId,
            };
            for (var s in params)
                arg[s] = params[s];

            return $.post('/api/event/modify', arg);
        },

        publish: function(eventId) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId,
            };

            return $.post('/api/event/publish', arg);
        },

        remove: function(eventId) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId
            };

            return $.post('/api/event/remove', arg);
        },

        simpleSearch: function(query, offset, limit) {
            var arg = {
                query: query,
                offset: offset,
                limit: limit
            };

            return $.post('/api/event/search', arg);
        },

        search: function(query, category, sortOrder, beforeDeadlineOnly, maxNum) {
            var arg = {
                query: query,
                category: category,
                sortOrder: sortOrder,
                beforeDeadlineOnly: beforeDeadlineOnly,
                maxNum: maxNum
            };

            return $.post('/api/event/search', arg);
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

        modifyEnquete: function(eventId, questions, types, options) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId,
                questions: questions,
                types: types,
                options: options
            };

            return $.post('/api/event/modifyEnquete', arg);
        },

        apply: function(ticketId, status, comment) {
            var arg = {
                    sessionToken: partake.sessionToken,
                    ticketId: ticketId,
                    status: status,
                    comment: comment
                };

                return $.post('/api/event/apply', arg);
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
    // User

    Partake.prototype.user = {
        partake: this,

        getEvents: function(userId, queryType, offset, limit) {
            var arg = {
                sessionToken: partake.sessionToken,
                userId: userId,
                queryType: queryType,
                offset: offset,
                limit: limit
            };

            return $.post('/api/user/events', arg);
        },

        getTickets: function(userId, offset, limit) {
            var arg = {
                sessionToken: partake.sessionToken,
                userId: userId,
                offset: offset,
                limit: limit
            };

            return $.post('/api/user/tickets', arg);
        }
    };

    // ----------------------------------------------------------------------
    // Message

    Partake.prototype.message = {
        partake: this,

        sendMessage: function(eventId, subject, body) {
            var arg = {
                sessionToken: partake.sessionToken,
                eventId: eventId,
                subject: subject,
                body: body
            };

            return $.post('/api/event/sendMessage', arg);
        }
    };

    Partake.prototype.defaultFailHandler = function (xhr) {
        try {
            var json = $.parseJSON(xhr.responseText);
            alert(json.reason);
        } catch (e) {
            alert('レスポンスが JSON 形式ではありません。');
        }
    };

    // ----------------------------------------------------------------------

    // expose partake client to global.
    createPartakeClient = function(sessionToken) {
        return new Partake(sessionToken);
    };
})();



