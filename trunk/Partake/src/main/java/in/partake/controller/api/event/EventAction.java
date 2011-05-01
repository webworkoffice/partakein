package in.partake.controller.api.event;

import in.partake.controller.api.PartakeAPIActionSupport;

public class EventAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;

    public String get() {
        throw new RuntimeException("Not implemented yet");
    }
// public String getEvent() {
//     String eventId = getParameter("eventId");
//     if (StringUtils.isEmpty(eventId)) { return INVALID; }
//     
//     UserEx loginUser = getLoginUser();
//     
//     try {
//         EventEx event = EventService.get().getEventExById(eventId);
//         if (StringUtils.isEmpty(eventId)) { return NOT_FOUND; }
//         
//         if (event.isPrivate()) {
//             // TODO: EventsController とコードが同じなので共通化するべき　
//             
//             // owner および manager は見ることが出来る。
//             String passcode = (String)session.get("event:" + eventId);
//             if (passcode == null) { passcode = getParameter("passcode"); }
//             
//             if (loginUser != null && event.hasPermission(loginUser, UserPermission.EVENT_PRIVATE_EVENT)) {
//                 // OK. You have the right to show this event.
//             } else if (StringUtils.equals(event.getPasscode(), passcode)) {
//                 // OK. The same passcode. 
//             } else {
//                 // public でなければ、passcode を入れなければ見ることが出来ない
//                 return PROHIBITED;
//             }
//         }
//         
//         String json = event.toJSON();
//         inputStream = new ByteArrayInputStream(json.getBytes("utf-8"));
//         return SUCCESS;
//         
//     } catch (DAOException e) {
//         logger.error(I18n.t(I18n.DATABASE_ERROR), e);
//         return redirectDBError();
//     } catch (UnsupportedEncodingException e) {
//         logger.error("UnsupportedEncodingException", e);
//         return redirectError("文字列のエンコード指定が誤っています。");
//     }
// }

}
