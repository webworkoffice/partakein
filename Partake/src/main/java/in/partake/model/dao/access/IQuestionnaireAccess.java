package in.partake.model.dao.access;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Questionnaire;

public interface IQuestionnaireAccess extends IAccess<Questionnaire, String> {

    public String getFreshId(PartakeConnection con) throws DAOException;
    
    /**
     * event id を指定してアンケートを取得する。アンケートは順番通りに並んでいる。(同じ順番があった場合は id の順に並ぶ。)
     * @param con
     * @param eventId
     * @return
     * @throws DAOException
     */
    public List<Questionnaire> findQuestionnairesByEventId(PartakeConnection con, String eventId) throws DAOException;
    
    /**
     * event id に関連するアンケートを全て消去する。
     * @param con
     * @param eventId
     * @throws DAOException
     */
    public void removeByEventId(PartakeConnection con, String eventId) throws DAOException;
}
