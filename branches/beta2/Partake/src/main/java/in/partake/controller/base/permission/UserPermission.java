package in.partake.controller.base.permission;

// TODO: Move these.
@Deprecated
public enum UserPermission {
    /** can edit the event */
    EVENT_EDIT,

    /** can remove the event */
    EVENT_REMOVE,

    /** can retrieve the participation list */
    EVENT_PARTICIPATION_LIST,
    
    /** show the event even if the event is private. */
    EVENT_PRIVATE_EVENT,
    
    /** can send message to participants */
    EVENT_SEND_MESSAGE,
    
    /** edit participants */
    EVENT_EDIT_PARTICIPANTS,
    
    ;
    
}
