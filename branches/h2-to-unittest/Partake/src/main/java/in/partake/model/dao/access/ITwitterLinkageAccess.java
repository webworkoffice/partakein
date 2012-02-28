package in.partake.model.dao.access;

import in.partake.model.dto.TwitterLinkage;

public interface ITwitterLinkageAccess extends IAccess<TwitterLinkage, String> {
    // screen name は、同じものが複数いるかもしれない。
    // TODO: あとで実装する必要がある
    // public List<TwitterLinkage> findByScreenName(PartakeConnection con, String screenName) throws DAOException;
}