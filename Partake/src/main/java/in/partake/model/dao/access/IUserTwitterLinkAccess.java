package in.partake.model.dao.access;

import in.partake.model.dto.UserTwitterLink;

public interface IUserTwitterLinkAccess extends IAccess<UserTwitterLink, String> {
    // screen name は、同じものが複数いるかもしれない。
    // TODO: あとで実装する必要がある
    // public List<TwitterLinkage> findByScreenName(PartakeConnection con, String screenName) throws DAOException;
}