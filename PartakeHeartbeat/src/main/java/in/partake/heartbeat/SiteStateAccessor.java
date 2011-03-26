package in.partake.heartbeat;

import java.util.Date;
import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/**
 * <p>監視対象サイトの状態をデータストアに出し入れするためのクラス。</p>
 * @author skypencil(@eller86)
 */
class SiteStateAccessor {
	private static final String KIND_NAME = "SiteState";
	private static final String PROPNAME_IS_ALIVE = "isAlive";
	private static final String PROPNAME_TIMESTAMP = "timestamp";

	private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	/**
	 * @return 前回確認時に正常に動作していたならばtrue
	 */
	boolean loadPreviousState() {
		final Query query = new Query(KIND_NAME).addSort(PROPNAME_TIMESTAMP, SortDirection.DESCENDING);
		final Iterator<Entity> results = datastore.prepare(query).asIterator();
		final Boolean prop;

		if (results.hasNext()) {
			final Entity lastState = results.next();
			prop = (Boolean) lastState.getProperty(PROPNAME_IS_ALIVE);
		} else {
			// 初回確認なので、前回正常だったものと仮定する
			prop = Boolean.TRUE;
		}

		return prop.booleanValue();
	}

	/**
	 * @param siteIsAlive 監視対象サイトが正常に動作しているならばtrue
	 */
	void storeSiteState(boolean siteIsAlive) {
		final Entity state = new Entity(KIND_NAME);
		state.setUnindexedProperty(PROPNAME_IS_ALIVE, Boolean.valueOf(siteIsAlive));
		state.setProperty(PROPNAME_TIMESTAMP, new Date());
		datastore.put(state);
	}
}
