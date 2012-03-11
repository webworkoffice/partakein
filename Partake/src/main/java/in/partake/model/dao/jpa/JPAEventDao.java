
package in.partake.model.dao.jpa;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

class JPAEventDao extends JPADao<Event> implements IEventAccess {
	// FIXME OpenJPAがnullなフィールドをmergeしないので回避策としてヌルオブジェクトを入れている
	// see http://code.google.com/p/partakein/issues/detail?id=208
	private static final String NULL_STRING = String.format("_NULL(%s)_", JPAEventDao.class.getName());
	private static final Date NULL_DATE = new Date(0);

	@Override
	public String getFreshId(PartakeConnection con) throws DAOException {
		return getFreshIdImpl(con, Event.class);
	}

	@Override
	public Event find(PartakeConnection con, String eventId) throws DAOException {
		Event event = decode(findImpl(con, eventId, Event.class));
		return event == null ? null : event.freeze();
	}

	@Override
	public void put(PartakeConnection con, Event embryo) throws DAOException {
		putImpl(con, encode(embryo), Event.class);
	}

	@Override
	public void remove(PartakeConnection con, String eventId) throws DAOException {
		removeImpl(con, eventId, Event.class);
	}

	@Override
	public DataIterator<Event> getIterator(PartakeConnection con) throws DAOException {
		EntityManager em = getEntityManager(con);
		Query q = em.createQuery("SELECT event FROM Events event");

		@SuppressWarnings("unchecked")
		List<Event> events = q.getResultList();

		return new Issue153IteratorWrapper(new JPAPartakeModelDataIterator<Event>(em, events, Event.class, false));
	}

	@Override
	public List<Event> findByOwnerId(PartakeConnection con, String userId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT event FROM Events event WHERE event.ownerId = :userId");
        q.setParameter("userId", userId);

        List<Event> events = new ArrayList<Event>();
        @SuppressWarnings("unchecked")
        List<Event> storedList = (List<Event>) q.getResultList();
        for (Event source : storedList) {
            Event event = decode(source);
            events.add(event == null ? null : event.freeze());
        }

        return events;
	}
	
	@Override
	public void truncate(PartakeConnection con) throws DAOException {
		EntityManager em = getEntityManager(con);
		Query q = em.createQuery("DELETE FROM Events");
		q.executeUpdate();
	}

	@Override
	public boolean isRemoved(PartakeConnection con, String eventId) throws DAOException {
		return false;
		// TODO:
		//	    throw new RuntimeException("Not implemented yet");
	}

	@Override
	public List<Event> findByScreenName(PartakeConnection con, String screenName) throws DAOException {
		// TODO: screenName に % が入っていたら検索が重たくなるかもしれない。たいして影響でないと思うけど。
		// TODO: でもちゃんと取り除いてあげる必要がある。

		EntityManager em = getEntityManager(con);
		TypedQuery<Event> q = em.createQuery("SELECT event FROM Events event WHERE event.managerScreenNames LIKE :queryName", Event.class);
		q.setParameter("queryName", "%" + screenName + "%");

		// 自分が "A" なら "BAC" とかもひっかかってくるので、取り除く。
		List<Event> events = q.getResultList();
		List<Event> result = new ArrayList<Event>();

		for (Event event : events) {
			if (event == null) { continue; }
			if (event.isManager(screenName)) {
				result.add(event.freeze());
			}
		}

		return result;
	}

	/***
	 * ImageIdにnullが入っていた場合、それを空文字として永続化する。
	 * endDateおよびdeadlineにnullが入っていた場合、それをnew Date(0)として永続化する。
	 * @see http://code.google.com/p/partakein/issues/detail?id=153
	 * @see http://code.google.com/p/partakein/issues/detail?id=204
	 */
	private static Event encode(Event source) {
		if (source == null) { return null; }
		Event event = source.copy();
		if (event.getBackImageId() == null) {
			event.setBackImageId(NULL_STRING);
		}
		if (event.getForeImageId() == null) {
			event.setForeImageId(NULL_STRING);
		}
		if (event.getEndDate() == null) {
			event.setEndDate((Date)NULL_DATE.clone());
		}
		if (event.getDeadline() == null) {
			event.setDeadline((Date)NULL_DATE.clone());
		}
		return event;
	}

	/***
	 * ImageIdに空文字が入っていた場合、それをnullとして扱う。
	 * endDateおよびdeadlineがbeginDateと同じだった場合、それをnullとして扱う。
	 * @see http://code.google.com/p/partakein/issues/detail?id=153
	 * @see http://code.google.com/p/partakein/issues/detail?id=204
	 */
	private static Event decode(Event persisted) {
		if (persisted == null) { return null; }
		Event event = persisted.copy();
		if (NULL_STRING.equals(event.getForeImageId())) {
			event.setForeImageId(null);
		}
		if (NULL_STRING.equals(event.getBackImageId())) {
			event.setBackImageId(null);
		}
		if (NULL_DATE.equals(event.getEndDate())) {
			event.setEndDate(null);
		}
		if (NULL_DATE.equals(event.getDeadline())) {
			event.setDeadline(null);
		}
		return event;
	}

	/**
	 * @see http://code.google.com/p/partakein/issues/detail?id=153
	 */
	private static class Issue153IteratorWrapper extends DataIterator<Event> {
		private final DataIterator<Event> inner;

		Issue153IteratorWrapper(DataIterator<Event> inner) {
			this.inner = inner;
		}

		@Override
		public boolean hasNext() throws DAOException {
			return inner.hasNext();
		}

		@Override
		public Event next() throws DAOException {
			return JPAEventDao.decode(inner.next());
		}

		@Override
		public void remove() throws DAOException, UnsupportedOperationException {
			inner.remove();
		}

		@Override
		public void update(Event t) throws DAOException,
		UnsupportedOperationException {
			inner.update(t);
		}
		
		@Override
		public void close() {
		}
	}
	
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return countImpl(con, "Events");
    }
    
    @Override
    public int countEventsByScreenName(PartakeConnection con, String screenName, EventFindCriteria criteria) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public List<Event> findByOwnerId(PartakeConnection con, String userId, EventFindCriteria criteria, int offset, int limit) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public List<Event> findByScreenName(PartakeConnection con, String screenName, EventFindCriteria criteria, int offset, int limit) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public int countEventsByOwnerId(PartakeConnection con, String userId, EventFindCriteria criteria) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
}
