package in.partake.model.dto;

import in.partake.model.dto.pk.ShortenedURLDataPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.apache.commons.lang.xwork.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;

@IdClass(ShortenedURLDataPK.class)
@Entity(name = "ShortenedURLData")
public class ShortenedURLData extends PartakeModel<ShortenedURLData> {
    @Id @Index
    private String originalURL;

    @Id 
    private String serviceType;

    @Column
    private String shortenedURL;
    
    // ----------------------------------------------------------------------
    // constructors
    
    public ShortenedURLData() {
        
    }
    
    public ShortenedURLData(String originalURL, String serviceType, String shortenedURL) {
        this.originalURL = originalURL;
        this.serviceType = serviceType;
        this.shortenedURL = shortenedURL;
    }
    
    public ShortenedURLData(ShortenedURLData shortenedURL) {
        this.originalURL = shortenedURL.originalURL;
        this.serviceType = shortenedURL.serviceType;
        this.shortenedURL = shortenedURL.shortenedURL;
    }
    
    @Override
    public Object getPrimaryKey() {
        return new ShortenedURLDataPK(originalURL, serviceType);
    }
    
    @Override
    public ShortenedURLData copy() {
        return new ShortenedURLData(this);
    }
    
    // ----------------------------------------------------------------------
    // equals method
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShortenedURLData)) { return false; }
        
        ShortenedURLData lhs = this;
        ShortenedURLData rhs = (ShortenedURLData) obj;
        
        if (!ObjectUtils.equals(lhs.originalURL, rhs.originalURL)) { return false; }
        if (!ObjectUtils.equals(lhs.serviceType, rhs.serviceType)) { return false; }
        if (!ObjectUtils.equals(lhs.shortenedURL, rhs.shortenedURL)) { return false; }
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(originalURL);
        code = code * 37 + ObjectUtils.hashCode(serviceType);
        code = code * 37 + ObjectUtils.hashCode(shortenedURL);
        
        return code;
    }

    // ----------------------------------------------------------------------
    // accessor methods

    public String getOriginalURL() {
        return originalURL;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getShortenedURL() {
        return shortenedURL;
    }

    public void setOriginalURL(String originalURL) {
        checkFrozen();
        this.originalURL = originalURL;
    }

    public void setServiceType(String serviceType) {
        checkFrozen();
        this.serviceType = serviceType;
    }

    public void setShortenedURL(String shortenedURL) {
        checkFrozen();
        this.shortenedURL = shortenedURL;
    }
}