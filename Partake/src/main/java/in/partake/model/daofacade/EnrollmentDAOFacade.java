package in.partake.model.daofacade;

import in.partake.base.PartakeRuntimeException;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationList;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.deprecated.DeprecatedPartakeDAOFacadeUtils;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.CalculatedEnrollmentStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.resource.ServerErrorCode;
import in.partake.service.DBService;

import java.util.List;

public class EnrollmentDAOFacade {

    public static List<EnrollmentEx> getEnrollmentExs(PartakeConnection con, String eventId) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getEnrollmentExs(con, DBService.getFactory(), eventId);
    }

    /** 参加ステータスを表示します */
    public static CalculatedEnrollmentStatus calculateEnrollmentStatus(PartakeConnection con, String userId, Event event) throws DAOException {
        ParticipationStatus status = getParticipationStatus(con, userId, event.getId());

        if (status == null)
            return CalculatedEnrollmentStatus.NOT_ENROLLED;

        switch (status) {
        case ENROLLED: {
            int order = getOrderOfEnrolledEvent(con, event.getId(), userId);
            if (order <= event.getCapacity() || event.getCapacity() == 0)
                return CalculatedEnrollmentStatus.ENROLLED;
            else
                return CalculatedEnrollmentStatus.ENROLLED_ON_WAITING_LIST;
        }
        case RESERVED: {
            int order = getOrderOfEnrolledEvent(con, event.getId(), userId);
            if (order <= event.getCapacity() || event.getCapacity() == 0)
                return CalculatedEnrollmentStatus.RESERVED;
            else
                return CalculatedEnrollmentStatus.RESERVED_ON_WAITING_LIST;
        }
        case NOT_ENROLLED:
            return CalculatedEnrollmentStatus.NOT_ENROLLED;
        case CANCELLED:
            return CalculatedEnrollmentStatus.CANCELLED;
        }

        assert false;
        throw new PartakeRuntimeException(ServerErrorCode.LOGIC_ERROR);
    }   

    public static ParticipationStatus getParticipationStatus(PartakeConnection con, String userId, String eventId) throws DAOException {
        Enrollment enrollment = DBService.getFactory().getEnrollmentAccess().find(con, new EnrollmentPK(userId, eventId));

        if (enrollment == null)
            return ParticipationStatus.NOT_ENROLLED;
        return enrollment.getStatus();
    }

    /**
     * event の参加順位(何番目に参加したか)を返します。
     */
    public static int getOrderOfEnrolledEvent(PartakeConnection con, String eventId, String userId) throws DAOException {
        List<EnrollmentEx> enrollments = getEnrollmentExs(con, eventId);
        EventEx event = EventDAOFacade.getEventEx(con, eventId);
        ParticipationList list = event.calculateParticipationList(enrollments);

        int result = 0;
        for (Enrollment e : list.getEnrolledParticipations()) {
            ++result;
            if (userId.equals(e.getUserId())) { return result; }
        }
        for (Enrollment e : list.getSpareParticipations()) {
            ++result;
            if (userId.equals(e.getUserId())) { return result; }
        }

        return -1;
    }
}
