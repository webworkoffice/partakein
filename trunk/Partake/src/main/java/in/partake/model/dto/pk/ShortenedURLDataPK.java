package in.partake.model.dto.pk;

import javax.persistence.Id;

import org.apache.commons.lang.xwork.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;

public class ShortenedURLDataPK {
    @Id @Index
    private String originalURL;

    @Id 
    private String serviceType;
    
    // ----------------------------------------------------------------------
    // constructors
    
    public ShortenedURLDataPK() {
        
    }
    
    public ShortenedURLDataPK(String originalURL, String serviceType) {
        this.originalURL = originalURL;
        this.serviceType = serviceType;
    }
    
    public ShortenedURLDataPK(ShortenedURLDataPK shortenedURL) {
        this.originalURL = shortenedURL.originalURL;
        this.serviceType = shortenedURL.serviceType;
    }
    
    // ----------------------------------------------------------------------
    // equals method
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShortenedURLDataPK)) { return false; }
        
        ShortenedURLDataPK lhs = this;
        ShortenedURLDataPK rhs = (ShortenedURLDataPK) obj;
        
        if (!ObjectUtils.equals(lhs.originalURL, rhs.originalURL)) { return false; }
        if (!ObjectUtils.equals(lhs.serviceType, rhs.serviceType)) { return false; }
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(originalURL);
        code = code * 37 + ObjectUtils.hashCode(serviceType);
        
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

    public void setOriginalURL(String originalURL) {
        this.originalURL = originalURL;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
