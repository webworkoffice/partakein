package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.jdbc.Index;

/**
 * UserActivity は、user に関連する feed が入る。
 * 次のものが入る。... あと何が必要かなあ？
 * <ul>
 *    <li>そのユーザーが owner になっているイベントの参加情報 (コメント、参加者) </li>
 * </ul>
 * @author shinyak
 *
 */
public class UserActivity extends PartakeModel<UserActivity> {
    @Id
    private String id;
    @Column @Index
    private String userId;
    @Column @Lob 
    private String content;
    @Column @Temporal(TemporalType.TIMESTAMP)
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
