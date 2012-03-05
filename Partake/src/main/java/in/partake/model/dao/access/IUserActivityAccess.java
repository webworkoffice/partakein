package in.partake.model.dao.access;

import java.util.Date;
import java.util.List;

import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserActivity;

// TODO: Who uses this?
public interface IUserActivityAccess extends IAccess<UserActivity, String> {

    /**
     * userId に関連する activity を、最大 maxNum 個取得する。
     *  
     * @param con
     * @param userId
     * @param maxNum
     * @return
     */
    public List<UserActivity> getActivitiesByUserId(PartakeConnection con, String userId, int maxNum);
    
    /**
     * dateBefore より前の Activity を全て消去する。     
     * @param con
     * @param dateBefore
     */
    public void removeOldActivities(PartakeConnection con, Date dateBefore);
}
