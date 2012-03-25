package in.partake.model;

import in.partake.model.dto.Comment;

/**
 * Comment with related data.
 * @author shinyak
 *
 */
public class CommentEx extends Comment {
    private UserEx user;

    public CommentEx(Comment comment, UserEx user) {
        super(comment);
        this.user = user;
    }

    public UserEx getUser() {
        return user;
    }
}
