package in.partake.model.dto;

import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;

/**
 * Message を、「他に人に送る」ことを表現するクラス。
 * 
 * @author shinyak
 *
 */
@Entity(name = "Envelopes")
public class Envelope extends PartakeModel<Envelope> {
    @Id
    private String envelopeId;
    @Column
    private String senderId;
    @Column
    private String receiverId;
    @Column
    private String messageId;
    @Column
    private Date deadline;
    @Column
    private int numTried;
    @Column
    private Date lastTriedAt;
    @Column
    private Date tryAfter;
    @Column
    private DirectMessagePostingType postingType;
    @Column
    private Date createdAt;
    
    public Envelope() {
        // do nothing
    }
    
    public Envelope(String id, String senderId, String receiverId, String messageId, Date deadline, int numTried, Date lastTriedAt, Date tryAfter, DirectMessagePostingType postingType, Date createdAt) {
        this.envelopeId = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageId = messageId;
        this.deadline = deadline != null ? (Date) deadline.clone() : null;
        this.numTried = numTried;
        this.lastTriedAt = lastTriedAt != null ? (Date) lastTriedAt.clone() : null;
        this.tryAfter = tryAfter != null ? (Date) tryAfter.clone() : null;
        this.postingType = postingType;
        this.createdAt = createdAt != null ? (Date) createdAt.clone() : null;
    }
    
    public Envelope(Envelope envelope) {
        this.envelopeId = envelope.envelopeId;
        this.senderId = envelope.senderId;
        this.receiverId = envelope.receiverId;
        this.messageId = envelope.messageId;
        this.deadline = envelope.getDeadline() != null ? (Date) envelope.getDeadline().clone() : null;
        this.numTried = envelope.numTried;
        this.lastTriedAt = envelope.lastTriedAt != null ? (Date) envelope.lastTriedAt.clone() : null;
        this.tryAfter = envelope.tryAfter != null ? (Date) envelope.tryAfter.clone() : null;
        this.postingType = envelope.postingType;
        this.createdAt = envelope.createdAt != null ? (Date) envelope.createdAt.clone() : null;
    }
    
    @Override
    public Object getPrimaryKey() {
        return envelopeId;
    }
    
    @Override
    public Envelope copy() {
        return new Envelope(this);
    }
    
    // ----------------------------------------------------------------------
    // equals method
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Envelope)) { return false; }
        
        Envelope lhs = this;
        Envelope rhs = (Envelope) obj;
        
        if (!ObjectUtils.equals(lhs.envelopeId,  rhs.envelopeId))  { return false; }
        if (!ObjectUtils.equals(lhs.senderId,    rhs.senderId))    { return false; }
        if (!ObjectUtils.equals(lhs.receiverId,  rhs.receiverId))  { return false; }
        if (!ObjectUtils.equals(lhs.messageId,   rhs.messageId))   { return false; }
        if (!ObjectUtils.equals(lhs.deadline,    rhs.deadline))    { return false; }
        if (!ObjectUtils.equals(lhs.numTried,    rhs.numTried))    { return false; }
        if (!ObjectUtils.equals(lhs.lastTriedAt, rhs.lastTriedAt)) { return false; }
        if (!ObjectUtils.equals(lhs.tryAfter,    rhs.tryAfter))    { return false; }
        if (!ObjectUtils.equals(lhs.postingType, rhs.postingType)) { return false; }
        if (!ObjectUtils.equals(lhs.createdAt,   rhs.createdAt))   { return false; }
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(envelopeId);
        code = code * 37 + ObjectUtils.hashCode(senderId);
        code = code * 37 + ObjectUtils.hashCode(receiverId);
        code = code * 37 + ObjectUtils.hashCode(messageId);
        code = code * 37 + ObjectUtils.hashCode(deadline);
        code = code * 37 + ObjectUtils.hashCode(numTried);
        code = code * 37 + ObjectUtils.hashCode(lastTriedAt);
        code = code * 37 + ObjectUtils.hashCode(tryAfter);
        code = code * 37 + ObjectUtils.hashCode(postingType);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        
        return code;
    }

    
    // ----------------------------------------------------------------------
    // accessors
    
    public String getEnvelopeId() { return envelopeId; }
    public String getSenderId()   { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessageId()  { return messageId; }
    public Date getDeadline() { return deadline; }
    public int getNumTried() { return numTried; }
    public Date getLastTriedAt() { return lastTriedAt; }
    public Date getTryAfter() { return tryAfter; }
    public DirectMessagePostingType getPostingType() { return postingType; }
    public Date getCreatedAt() { return createdAt; }
    
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
        this.lastTriedAt = lastTriedAt != null ? (Date) lastTriedAt.clone() : null;
    }
    public void setTryAfter(Date tryAfter) {
        this.tryAfter = tryAfter != null ? (Date) tryAfter.clone() : null;
    }
    public void setPostingType(DirectMessagePostingType postingType) {
        this.postingType = postingType;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt != null ? (Date) createdAt.clone() : null;
    }
    
    public void updateForSendingFailure() {
        this.numTried += 1;
        this.lastTriedAt = new Date();
    }
    
}
