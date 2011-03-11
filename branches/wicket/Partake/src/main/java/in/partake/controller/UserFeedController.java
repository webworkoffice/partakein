package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.I18n;
import in.partake.service.UserService;

import org.apache.log4j.Logger;

public class UserFeedController extends PartakeActionSupport {
    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserFeedController.class);

    public String feed() {
        try {
            String feedId = getParameter("feedId");
            if (feedId == null) { return ERROR; }
            
            // NOTE: feedId と calendarId は同じにしてある。
            UserEx user = UserService.get().getUserFromCalendarId(feedId);
            if (user == null) { return ERROR; }
            
            // 次のようなデータを用意する予定。
            //      1. 自分が参加しているイベントに変更があれば、RSS に変更されたという情報を追加。
            //      2. 自分が管理しているイベントに参加者の変化があれば、RSS に変化を追加
            
            throw new RuntimeException("Not implemented yet");
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            return ERROR;
        }
    }
}
