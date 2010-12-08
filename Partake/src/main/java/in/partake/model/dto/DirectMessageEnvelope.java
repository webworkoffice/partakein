package in.partake.model.dto;

import java.util.Date;

public class DirectMessageEnvelope {
    private String envelopeId;
    private String senderId;
    private String receiverId;
    private String messageId;
    private Date deadline;
    private int numTried;
    private Date lastTriedAt;
    private Date tryAfter;
    private DirectMessagePostingType postingType;
        
    public String getEnvelopeId() { return envelopeId; }
    public String getSenderId()   { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessageId()  { return messageId; }
    public Date getDeadline() { return deadline; }
    public int getNumTried() { return numTried; }
    public Date getLastTriedAt() { return lastTriedAt; }
    public Date getTryAfter() { return tryAfter; }
    public DirectMessagePostingType getPostingType() { return postingType; }
    
    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    public void setNumTried(int numTried) {
        this.numTried = numTried;
    }
    public void setLastTriedAt(Date lastTriedAt) {
        this.lastTriedAt = lastTriedAt;
    }
    public void setTryAfter(Date tryAfter) {
        this.tryAfter = tryAfter;
    }
    public void setPostingType(DirectMessagePostingType postingType) {
        this.postingType = postingType;
    }
    
    public void updateForSendingFailure() {
        this.numTried += 1;
        this.lastTriedAt = new Date();
    }
    
}
