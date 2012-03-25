package in.partake.service.impl;

import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.model.dto.Event;
import in.partake.service.EventSearchServiceException;
import in.partake.service.IEventSearchService;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

class EventSearchService implements IEventSearchService {

    public EventSearchService() {
    }

    @Override
    public void create(Event event) throws EventSearchServiceException {
        Document doc = makeDocument(event.getId(), event);
        LuceneService.get().addDocument(doc);
    }

    @Override
    public boolean hasIndexed(String eventId) throws EventSearchServiceException {
        return LuceneService.get().hasDocument(eventId);
    }

    @Override
    public void remove(String eventId) throws EventSearchServiceException{
        LuceneService.get().removeDocument(eventId);
    }

    @Override
    public void update(Event event) throws EventSearchServiceException {
        Document doc = makeDocument(event.getId(), event);
        LuceneService.get().updateDocument(doc);
    }

    @Override
    public void truncate() throws EventSearchServiceException {
        LuceneService.get().truncate();
    }

    @Override
    public List<String> getUpcomingByCategory(String category, int maxDocument) throws EventSearchServiceException {
        try {
            TopDocs docs = LuceneService.get().search("", category, "beginDate", true, maxDocument);
            return convertToIds(docs);
        } catch (ParseException e) {
            assert false;
            throw new EventSearchServiceException(e);
        }
    }
    
    @Override
    public List<String> getRecent(int maxDocument) throws EventSearchServiceException {
        TopDocs docs = LuceneService.get().getRecentDocuments(maxDocument);
        return convertToIds(docs);
    }
    
    @Override
    public List<String> getRecentByCategory(String category, int maxDocument) throws EventSearchServiceException {
        TopDocs docs = LuceneService.get().getRecentCategoryDocuments(category, maxDocument);
        return convertToIds(docs);
    }
    
    @Override
    public List<String> search(String term, String category, String sortOrder, boolean beforeDeadlineOnly, int maxDocument) throws EventSearchServiceException {
        try {
            TopDocs docs = LuceneService.get().search(term, category, sortOrder, beforeDeadlineOnly, maxDocument);
            return convertToIds(docs);
        } catch (ParseException e) {
            assert false;
            throw new EventSearchServiceException(e);
        }
    }
    
    private List<String> convertToIds(TopDocs docs) throws EventSearchServiceException {
        List<String> eventIds = new ArrayList<String>();
        
        for (ScoreDoc doc : docs.scoreDocs) {
            Document document = LuceneService.get().getDocument(doc.doc);
            String id = document.get("ID");
            if (id == null)
                continue;
            
            eventIds.add(id);
        }
        
        return eventIds;
    }
    /**
     * create a lucene document from eventId and event.
     */
    private Document makeDocument(String eventId, Event eventEmbryo) {
        StringBuilder builder = new StringBuilder();
        builder.append(eventEmbryo.getTitle()).append(" ");
        builder.append(eventEmbryo.getSummary()).append(" ");
        builder.append(eventEmbryo.getAddress()).append(" ");
        builder.append(eventEmbryo.getPlace()).append(" ");
        builder.append(Util.removeTags(eventEmbryo.getDescription()));

        long beginTime = eventEmbryo.getBeginDate().getTime();
        long deadlineTime = eventEmbryo.getDeadline() != null ? eventEmbryo.getDeadline().getTime() : beginTime;
        Document doc = new Document();
        doc.add(new Field("ID", eventId, Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field("CATEGORY", eventEmbryo.getCategory(), Store.NO, Index.NOT_ANALYZED, TermVector.WITH_POSITIONS));
        doc.add(new Field("CREATED-AT", TimeUtil.getTimeString(eventEmbryo.getCreatedAt().getTime()), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field("BEGIN-TIME", TimeUtil.getTimeString(beginTime), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field("DEADLINE-TIME", TimeUtil.getTimeString(deadlineTime), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field("TITLE", eventEmbryo.getTitle(), Store.NO, Index.ANALYZED, TermVector.WITH_POSITIONS));
        doc.add(new Field("CONTENT", builder.toString(), Store.NO, Index.ANALYZED, TermVector.WITH_POSITIONS));

        return doc;
    }

}
