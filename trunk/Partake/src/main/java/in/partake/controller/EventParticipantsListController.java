package in.partake.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

import in.partake.model.EventEx;
import in.partake.model.ParticipationEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Participation;
import in.partake.model.dto.ParticipationStatus;
import in.partake.model.dto.UserPermission;
import in.partake.service.EventService;
import in.partake.service.UserService;

public class EventParticipantsListController extends PartakeActionSupport {
    private String contentType = null;
    private ByteArrayInputStream inputStream = null;
    
    public String show() {
        String eventId = getParameter("eventId");
        if (eventId == null) { return NOT_FOUND; }
        
        try {
            EventEx event = EventService.get().getEventExById(eventId); 
            if (event == null) { return NOT_FOUND; }
    
            // Only owner can retrieve the participants list.
            if (!event.hasPermission(getLoginUser(), UserPermission.EVENT_PARTICIPATION_LIST)) {
                addActionError("イベント参加者の取得権限がありません。");
                return PROHIBITED;
            }
            
            List<ParticipationEx> participations = EventService.get().getParticipationEx(eventId);
            ParticipationList list = event.calculateParticipationList(participations);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos));
            
            for (Participation participation : list.getEnrolledParticipations()) {
                UserEx user = UserService.get().getUserExById(participation.getUserId());
                
                String[] lst = new String[4];
                lst[0] = user.getTwitterLinkage().getScreenName();
                if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) {
                    lst[1] = "参加";
                } else if (ParticipationStatus.RESERVED.equals(participation.getStatus())) {
                    lst[1] = "仮参加";
                } else {
                    lst[1] = "(状態不明...)";
                    System.out.println("SHOULD NOT HAPPEN : EventParticipantsListController#show()");
                }
                lst[2] = participation.getComment();
                lst[3] = participation.getModifiedAt().toString();                
                writer.writeNext(lst);
            }
            
            for (Participation participation : list.getSpareParticipations()) {
                UserEx user = UserService.get().getUserExById(participation.getUserId());
                
                String[] lst = new String[4];
                lst[0] = user.getTwitterLinkage().getScreenName();
                if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) {
                    lst[1] = "補欠 (参加)";
                } else if (ParticipationStatus.RESERVED.equals(participation.getStatus())) {
                    lst[1] = "補欠 (仮参加)";
                } else {
                    lst[1] = "補欠 (状態不明...)";
                    System.out.println("SHOULD NOT HAPPEN : EventParticipantsListController#show()");
                }
                lst[2] = participation.getComment();
                lst[3] = participation.getModifiedAt().toString();                
                writer.writeNext(lst);
            }

            writer.flush();
            writer.close();

            this.contentType = "text/csv";
            this.inputStream = new ByteArrayInputStream(baos.toByteArray());
            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR;            
        } catch (DAOException e) {
            e.printStackTrace();
            return ERROR;
        }
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public ByteArrayInputStream getInputStream() {
        return this.inputStream;
    }
}
