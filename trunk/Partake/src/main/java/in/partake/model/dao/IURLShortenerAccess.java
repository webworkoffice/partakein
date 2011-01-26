package in.partake.model.dao;

/**
 * cache the shortened URL.
 * when using several shortening service, we can cache all the shortened URL.
 * 
 * @author shinyak
 *
 */
public interface IURLShortenerAccess {
    public abstract void addShortenedURL(PartakeConnection con, String originalURL, String serviceType, String shortenedURL) throws DAOException;
    public abstract String getShortenedURL(PartakeConnection con, String originalURL, String serviceType) throws DAOException;
    
    /**
     * 特にどのサービスを利用しているのかを問わずに shortened URL を返す。
     * @param con
     * @param url
     * @return
     * @throws DAOException
     */
    public abstract String getShortenedURL(PartakeConnection con, String originalURL) throws DAOException;
    
    /** Use ONLY in unit tests.*/
    public abstract void truncate(PartakeConnection con) throws DAOException;

}
