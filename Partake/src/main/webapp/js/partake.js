// partake の基本 API を便利に使うための javascript library です。jQuery に依存します。
// $partake のみが公開されます。

// *** UNDER IMPLEMENTATION ***

function $partake() {
};

/**
 * @param {!String} userId
 * @param {!String} eventId
 * @param {!String} status
 * 
 * for example
 * $partake.changeAttendance(...).success(function(json) {...} ).error(function(json) {...});
 */
$partke.changeAttendance = function(userId, eventId, status) {
	var arg = {
		userId: userId,
		eventId: eventId,
		status: status
	};
	
	return $.post('/api/attendance/change', arg);
}; 
