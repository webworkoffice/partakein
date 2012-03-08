package in.partake.controller.base.permission;

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
    
    /** remove comment */
    EVENT_REMOVE_COMMENT,
    
    /** edit participants */
    EVENT_EDIT_PARTICIPANTS,
    
    /** edit questionnaire */
    EVENT_EDIT_QUESTIONNAIRE,
    
    ;
    
}
