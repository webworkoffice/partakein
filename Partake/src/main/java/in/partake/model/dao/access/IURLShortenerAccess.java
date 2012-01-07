package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;

/**
 * cache the shortened URL.
 * when using several shortening service, we can cache all the shortened URL.
 * 
 * @author shinyak
 *
 */
public interface IURLShortenerAccess extends IAccess<ShortenedURLData, ShortenedURLDataPK> {    

    /**
     * 特にどのサービスを利用しているのかを問わずに shortened URL を返す。
     * @param con
     * @param url
     * @return
     * @throws DAOException
     */
    public abstract ShortenedURLData findByURL(PartakeConnection con, String originalURL) throws DAOException;
    public abstract void removeByURL(PartakeConnection con, String originalURL) throws DAOException;
}
