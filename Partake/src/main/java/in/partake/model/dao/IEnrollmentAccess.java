package in.partake.model.dao;

import in.partake.model.dto.Enrollment;
import in.partake.model.dto.pk.EnrollmentPK;

import java.util.List;

public interface IEnrollmentAccess extends IAccess<Enrollment, EnrollmentPK> {
    /** enrollment の一覧を取得。順不同。 */
    public List<Enrollment> findByEventId(PartakeConnection con, String eventId) throws DAOException;
    /** enrollment の一覧を取得。順不同。 */
    public List<Enrollment> findByUserId(PartakeConnection con, String userId) throws DAOException;
}
