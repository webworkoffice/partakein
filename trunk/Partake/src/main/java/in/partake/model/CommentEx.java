package in.partake.model;

import in.partake.model.dto.Comment;

public class CommentEx extends Comment {
    private EventEx event;
    private UserEx user;

    public CommentEx(Comment comment, EventEx event, UserEx user) {
        super(comment);
        this.event = event;
        this.user = user;
    }

    public EventEx getEvent() {
        return event;
    }
    
    public UserEx getUser() {
        return user;
    }
}
