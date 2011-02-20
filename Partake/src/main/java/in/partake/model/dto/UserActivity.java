package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.openjpa.persistence.jdbc.Index;

public class UserActivity extends PartakeModel<UserActivity> {
    @Id
    private String id;
    @Column @Index
    private String userId;
    @Column(length = 2000)
    private String content;
    @Column
    private Date createdAt;
    
    @Override
    public UserActivity copy() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Object getPrimaryKey() {
        // TODO Auto-generated method stub
        return null;
    }
}
