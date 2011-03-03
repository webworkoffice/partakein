package in.partake.controller.action;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import in.partake.controller.PartakeActionSupport;
import in.partake.controller.PartakeInvalidResultException;
import in.partake.controller.PartakeResultException;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Questionnaire;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.Constants;
import in.partake.resource.I18n;
import in.partake.service.EventService;

public class QuestionnaireController extends PartakeActionSupport {
    private static final Logger logger = Logger.getLogger(QuestionnaireController.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * アンケート編集画面を表示する。
     */
    public String edit() throws PartakeResultException {
        UserEx user = ensureLogin();
        
        String eventId = getParameter("eventId");
        if (StringUtils.isBlank(eventId)) { throw new PartakeInvalidResultException("eventId が空です。"); }
        
        try {
            EventEx event = EventService.get().getEventExById(eventId);
            if (event == null) { throw new PartakeInvalidResultException("無効な eventId が渡されました。"); }
            if (!event.hasPermission(user, UserPermission.EVENT_EDIT_QUESTIONNAIRE)) { return PROHIBITED; }

            // 権限チェックは終了。
            
            // 現在のアンケートを取得し、attribute に保持。
            List<Questionnaire> questionnaires = EventService.get().findQuestionnairesByEventId(eventId);
            if (questionnaires == null) { questionnaires = Collections.emptyList(); }
            
            attributes.put(Constants.ATTR_QUESTIONNAIRES, questionnaires);
            
            return SUCCESS;
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            return ERROR;
        }
    }
    
    /**
     * アンケートを保存する。
     * @return 成功すれば "success" を、データに不整合があれば "input" を返す。 
     */
    public String commit() {
        throw new RuntimeException("not implemented yet");
    }
}
