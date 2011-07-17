package in.partake.controller;

import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Questionnaire;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.Constants;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;

import java.util.Collections;
import java.util.List;

public class QuestionnaireController extends PartakeActionSupport {
    // private static final Logger logger = Logger.getLogger(QuestionnaireController.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * アンケート編集画面を表示する。
     */
    public String edit() throws PartakeResultException, DAOException {
        UserEx user = ensureLogin();

        String eventId = getParameter("eventId");
        if (eventId == null) { return renderInvalid(UserErrorCode.MISSING_EVENT_ID); }

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return renderInvalid(UserErrorCode.INVALID_EVENT_ID); }

        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_QUESTIONNAIRE)) { return renderForbidden(); }

        // 権限チェックは終了。

        // 現在のアンケートを取得し、attribute に保持。
        List<Questionnaire> questionnaires = EventService.get().findQuestionnairesByEventId(eventId);
        if (questionnaires == null) { questionnaires = Collections.emptyList(); }

        attributes.put(Constants.ATTR_QUESTIONNAIRES, questionnaires);

        return SUCCESS;
    }

    /**
     * アンケートを保存する。
     * @return 成功すれば "success" を、データに不整合があれば "input" を返す。 
     */
    public String commit() {
        throw new RuntimeException("not implemented yet");
    }
}
