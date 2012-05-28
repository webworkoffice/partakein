package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EnqueteAnswerType;
import in.partake.model.dto.auxiliary.EnqueteQuestion;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

public class ModifyEnqueteAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        String[] questions = getParameters("questions[]");
        String[] types = getParameters("types[]");
        String[] options = getParameters("options[]");

        if (questions == null)
            return renderInvalid(UserErrorCode.MISSING_ENQUETE_QUESTION);
        if (types == null)
            return renderInvalid(UserErrorCode.MISSING_ENQUETE_TYPE);
        if (options == null)
            return renderInvalid(UserErrorCode.MISSING_ENQUETE_OPTION);
        if (questions.length != types.length || questions.length != options.length)
            return renderInvalid(UserErrorCode.INVALID_ENQUETE_PARAMS);

        new ModifyEnqueteTransaction(user, eventId, questions, types, options).execute();
        return renderOK();
    }
}

class ModifyEnqueteTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private String[] questions;
    private String[] types;
    private String[] options;

    public ModifyEnqueteTransaction(UserEx user, String eventId, String[] questions, String[] types, String[] options) {
        this.user = user;
        this.eventId = eventId;
        this.questions = questions;
        this.types = types;
        this.options = options;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);
        if (!event.isDraft())
            throw new PartakeException(UserErrorCode.INVALID_ENQUETE_PUBLISHED);

        List<EnqueteQuestion> enquetes = new ArrayList<EnqueteQuestion>();
        for (int i = 0; i < questions.length; ++i) {
            List<String> optionValues = new ArrayList<String>();
            JSONArray array = JSONArray.fromObject(options[i]);
            for (int j = 0; j < array.size(); ++j)
                optionValues.add(array.getString(j));

            EnqueteQuestion question = new EnqueteQuestion(
                    questions[i], EnqueteAnswerType.safeValueOf(types[i]), optionValues);
            enquetes.add(question);
        }

        Event copied = new Event(event);
        copied.setEnquetes(enquetes);
        daos.getEventAccess().put(con, copied);

        return null;
    }
}
