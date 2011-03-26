package in.partake.heartbeat;

import java.util.Date;
import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

class SiteStateAccessor {
	private static final String KIND_NAME = "SiteState";
	private static final String PROPNAME_IS_ALIVE = "isAlive";
	private static final String PROPNAME_TIMESTAMP = "timestamp";

	private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	boolean loadPrevState() {
		final Query query = new Query(KIND_NAME).addSort(PROPNAME_TIMESTAMP, SortDirection.DESCENDING);
		final Iterator<Entity> results = datastore.prepare(query).asIterator();
		final Boolean prop;

		if (results.hasNext()) {
			final Entity lastState = results.next();
			prop = (Boolean) lastState.getProperty(PROPNAME_IS_ALIVE);
		} else {
			prop = Boolean.TRUE;
		}

		return prop.booleanValue();
	}

	void storeSiteState(boolean siteIsAlive) {
		final Entity state = new Entity(KIND_NAME);
		state.setUnindexedProperty(PROPNAME_IS_ALIVE, Boolean.valueOf(siteIsAlive));
		state.setProperty(PROPNAME_TIMESTAMP, new Date());
		datastore.put(state);
	}
}
