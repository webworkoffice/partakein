package in.partake.model.dto;

public enum UserPermission {
    /** can edit the event */
    EVENT_EDIT,

    /** can remove the event */
    EVENT_REMOVE,

    /** can create/remove a subevent */
    EVENT_MAKE_SUBEVENT,
    
    /** can edit a sub event */
    EVENT_EDIT_SUBEVENT,
    
    /** can retrieve the participation list */
    EVENT_PARTICIPATION_LIST,
    
    /** show the event even if the event is private. */
    EVENT_PRIVATE_EVENT,
    
    /** can promote the event. */
    EVENT_PROMOTE,
    
    /** can send message to participants */
    EVENT_SEND_MESSAGE,
    
    /** remove comment */
    EVENT_REMOVE_COMMENT;
}
