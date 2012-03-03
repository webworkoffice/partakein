package in.partake.controller.api.event;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

public class ModifyAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        throw new RuntimeException("Not implemented yet");
//      UserEx user = getLoginUser();
//      if (user == null) { return LOGIN; }
//
//      EventEx event;
//      if (eventId == null) { return ERROR; }
//      event = EventService.get().getEventExById(eventId);
//      if (event == null) { return ERROR; }
//
//      // check permission
//      if (!event.hasPermission(user, UserPermission.EVENT_EDIT)) { return PROHIBITED; }
//
//      GregorianCalendar calendar = new GregorianCalendar();
//      calendar.set(syear, smonth - 1, sday, shour, smin, 0);
//      calendar.set(Calendar.MILLISECOND, 0);
//      Date beginDate = calendar.getTime();
//
//      Date endDate;
//      if (usesEndDate) {
//          calendar.set(eyear, emonth - 1, eday, ehour, emin, 0);
//          calendar.set(Calendar.MILLISECOND, 0);
//          endDate = calendar.getTime();
//      } else {
//          endDate = null;
//      }
//
//      Date deadline;
//      if (usesDeadline) {
//          calendar.set(dyear, dmonth - 1, dday, dhour, dmin, 0);
//          calendar.set(Calendar.MILLISECOND, 0);
//          deadline = calendar.getTime();
//      } else {
//          deadline = null;
//      }
//
//          Date now = new Date();
//          BinaryData foreImageEmbryo = createBinaryDataEmbryo(foreImage, foreImageContentType);
//          BinaryData backImageEmbryo = createBinaryDataEmbryo(backImage, backImageContentType);
//          // TODO: preview
//          Event eventEmbryo = new Event(
//                  shortId, title, summary, category, deadline, beginDate, endDate, capacity, url, place, address, description,
//                  hashTag, event.getOwnerId(), managers,
//                  secret, passcode, false, false, event.getCreatedAt(), now
//                  );
//
//          // TODO: これはひどい
//          // related event を登録
//          List<EventRelation> eventRelations = new ArrayList<EventRelation>();
//          Set<EventRelationPK> eventRelationPKs = new HashSet<EventRelationPK>();
//          if (!StringUtils.isEmpty(relatedEventID1) && !eventRelationPKs.contains(new EventRelationPK(event.getId(), Util.removeURLFragment(relatedEventID1)))) {
//              eventRelations.add(new EventRelation(event.getId(), Util.removeURLFragment(relatedEventID1), relatedEventRequired1, relatedEventPriority1));
//              eventRelationPKs.add(new EventRelationPK(event.getId(), Util.removeURLFragment(relatedEventID1)));
//          }
//          if (!StringUtils.isEmpty(relatedEventID2) && !eventRelationPKs.contains(new EventRelationPK(event.getId(), Util.removeURLFragment(relatedEventID2)))) {
//              eventRelations.add(new EventRelation(event.getId(), Util.removeURLFragment(relatedEventID2), relatedEventRequired2, relatedEventPriority2));
//              eventRelationPKs.add(new EventRelationPK(event.getId(), Util.removeURLFragment(relatedEventID1)));
//          }
//          if (!StringUtils.isEmpty(relatedEventID3) && !eventRelationPKs.contains(new EventRelationPK(event.getId(), Util.removeURLFragment(relatedEventID3)))) {
//              eventRelations.add(new EventRelation(event.getId(), Util.removeURLFragment(relatedEventID3), relatedEventRequired3, relatedEventPriority3));
//              eventRelationPKs.add(new EventRelationPK(event.getId(), Util.removeURLFragment(relatedEventID1)));
//          }
//
//          EventService.get().update(event, eventEmbryo,
//                  foreImageEmbryo != null || removingForeImage, foreImageEmbryo,
//                  backImageEmbryo != null || removingBackImage, backImageEmbryo);
//          EventService.get().setEventRelations(event.getId(), eventRelations);
//
//          addActionMessage("イベント情報が変更されました。");
//          this.eventId = event.getId();
//          return SUCCESS;        
    }
}
