package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.service.UserService;

import org.apache.log4j.Logger;

/**
 * User に関連する RSS を作成して返す。
 * 
 * 次のようなデータを用意する予定。
 *  1. 自分が参加しているイベントに変更があれば、RSS に変更されたという情報を追加。
 *  2. 自分が管理しているイベントに参加者の変化があれば、RSS に変化を追加
 *  
 * @author shinyak
 *
 */
public class UserFeedController extends PartakeActionSupport {
    /** */
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(UserFeedController.class);

    public String feed() throws DAOException {
        String feedId = getParameter("feedId");
        if (feedId == null) { return INVALID; }
        
        // NOTE: feedId と calendarId は同じにしてある。
        UserEx user = UserService.get().getUserFromCalendarId(feedId);
        if (user == null) { return INVALID; }
        
        throw new RuntimeException("Not implemented yet");
    }
}
