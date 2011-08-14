package in.partake.model.dao;

import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.PartakeProperties;
import in.partake.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Date;


import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * TODO: LuceneDao っておかしいよなー。あとで直す。
 * @author shinyak
 *
 */
public class LuceneDao {
	private static volatile LuceneDao instance = new LuceneDao();

	private IndexWriter indexWriter;
	private IndexReader indexReader;
	private IndexSearcher indexSearcher;
	private Analyzer analyzer;

	public static LuceneDao get() {
	    return instance;
	}

	private LuceneDao() {
		try {
		    File indexDirFile = new File(PartakeProperties.get().getLuceneIndexDirectory());
	        Directory indexDir = FSDirectory.open(indexDirFile);

	        // create index.
	        Analyzer luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_30);
            indexWriter = new IndexWriter(indexDir, luceneAnalyzer, new IndexWriter.MaxFieldLength(1024*1024));
            indexReader = indexWriter.getReader();
			indexSearcher = new IndexSearcher(indexReader);
			analyzer = new CJKAnalyzer(Version.LUCENE_30);
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
	}

	public void addDocument(Document doc) throws DAOException {
		try {
			indexWriter.addDocument(doc, analyzer);
			indexWriter.commit();
			reset();
		} catch (CorruptIndexException e) {
			throw new DAOException(e);
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	public void updateDocument(Document doc) throws DAOException {
		try {
			indexWriter.updateDocument(new Term("ID", doc.get("ID")), doc, analyzer);
			indexWriter.commit();
			reset();
		} catch (CorruptIndexException e) {
			throw new DAOException(e);
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	public void removeDocument(String id) throws DAOException {
		try {
			indexWriter.deleteDocuments(new Term("ID", id));
			indexWriter.commit();
			reset();
		} catch (CorruptIndexException e) {
			throw new DAOException(e);
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	public boolean hasDocument(String id) throws DAOException {
	    try {
	        Query query = new TermQuery(new Term("ID", id));
	        TopDocs docs = indexSearcher.search(query, 1);
	        return docs.totalHits > 0;
	    } catch (IOException e) {
	        throw new DAOException(e);
	    }
	}

	public Document getDocument(int doc) throws DAOException {
		try {
			return indexSearcher.doc(doc);
		} catch (CorruptIndexException e) {
			throw new DAOException(e);
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	public TopDocs getRecentDocuments(int n) throws DAOException {
		try {
			long current = new Date().getTime();
			Query query = new TermRangeQuery("DEADLINE-TIME", Util.getTimeString(current), Util.getTimeString(Long.MAX_VALUE), true, true);

			Sort sort = new Sort(new SortField("CREATED-AT", SortField.STRING, true));

			return indexSearcher.search(query, null, n, sort);
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	public TopDocs getRecentCategoryDocuments(String category, int maxDocument) throws DAOException, IllegalArgumentException {
		if (!EventCategory.isValidCategoryName(category)) {
			throw new IllegalArgumentException("Unknown category");
		}
		Query query = new TermQuery(new Term("CATEGORY", category));
		Sort sort = new Sort(new SortField("CREATED-AT", SortField.STRING, true));
		try {
			return indexSearcher.search(query, null, maxDocument, sort);
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	public TopDocs search(String term, String category, String sortOrder, boolean beforeDeadlineOnly, int maxDocument) throws DAOException, ParseException, IllegalArgumentException {
		try {
			Query query;
			if (StringUtils.isEmpty(term)) {
				// If the search term is not null, all events should be displayed.
				query = new MatchAllDocsQuery();
			} else {
				QueryParser partialParser = new QueryParser(Version.LUCENE_30, "CONTENT", analyzer);
				query = partialParser.parse(term);
			}

			// TODO: なんか汚い...。
			Filter filter;
			if (beforeDeadlineOnly) {
				long current = new Date().getTime();
				if (EventCategory.getAllEventCategory().equals(category) || "".equals(category)) {
					Query filterQuery = new TermRangeQuery("DEADLINE-TIME", Util.getTimeString(current), Util.getTimeString(Long.MAX_VALUE), true, true);
					filter = new QueryWrapperFilter(filterQuery);
				} else {
					if (!EventCategory.isValidCategoryName(category)) {
						throw new IllegalArgumentException("Unknown category");
					}
					BooleanQuery filterQuery = new BooleanQuery();
					filterQuery.add(new BooleanClause(new TermQuery(new Term("CATEGORY", category)), Occur.MUST));
					filterQuery.add(new BooleanClause(new TermRangeQuery("DEADLINE-TIME", Util.getTimeString(current), Util.getTimeString(Long.MAX_VALUE), true, true), Occur.MUST));
					filter = new QueryWrapperFilter(filterQuery);
				}
			} else {
				if (EventCategory.getAllEventCategory().equals(category) || "".equals(category)) {
					filter = null;
					// filter = new QueryWrapperFilter(new MatchAllDocsQuery());
				} else {
					if (!EventCategory.isValidCategoryName(category)) {
						throw new IllegalArgumentException("Unknown category");
					}
					filter = new QueryWrapperFilter(new TermQuery(new Term("CATEGORY", category)));
				}
			}

			// TODO: このへんの定数なんとかするべき。いろんなところに散らばっていて使いにくい。

			Sort sort;
			if ("score".equals(sortOrder)) {
				sort = new Sort(SortField.FIELD_SCORE, new SortField("BEGIN-TIME", SortField.STRING));
			} else if ("createdAt".equals(sortOrder)) {
				sort = new Sort(new SortField("CREATED-AT", SortField.STRING, true));
			} else if ("deadline".equals(sortOrder)) {
				sort = new Sort(new SortField("DEADLINE-TIME", SortField.STRING));
			} else if ("deadline-r".equals(sortOrder)) {
				sort = new Sort(new SortField("DEADLINE-TIME", SortField.STRING, true));
			} else if ("beginDate".equals(sortOrder)) {
				sort = new Sort(new SortField("BEGIN-TIME", SortField.STRING));
			} else if ("beginDate-r".equals(sortOrder)) {
				sort = new Sort(new SortField("BEGIN-TIME", SortField.STRING, true));
			} else {
				// 決まってない場合は、score 順にする。
				sort = new Sort(SortField.FIELD_SCORE, new SortField("BEGIN-TIME", SortField.STRING));
			}

			return indexSearcher.search(query, filter, maxDocument, sort);
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Lucene index を全て捨てる
	 * @throws DAOException
	 */
	public void truncate() throws DAOException {
	    try {
           indexWriter.deleteAll();
           indexWriter.commit();
           reset();
        } catch (CorruptIndexException e) {
            throw new DAOException(e);
        } catch (IOException e) {
            throw new DAOException(e);
        }

	}
	

	/**
     * When index is changed, reset() should be called.
     */
    private synchronized void reset() {
        try {
            IndexReader oldReader = indexReader;
            indexReader = indexWriter.getReader();
            indexSearcher = new IndexSearcher(indexReader);

            if (oldReader != indexReader) {
                oldReader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: Is this OK?
        }
    }
}
