package in.partake.page.event;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.page.base.PartakeBasePage;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.util.KeyValuePair;
import in.partake.wicket.component.InvisibleComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class EventSearchPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EventSearchPage.class);
    private static final List<KeyValuePair> SORTORDERS = Collections.unmodifiableList(Arrays.asList(
            new KeyValuePair("score", "マッチ度順"),
            new KeyValuePair("createdAt", "新着順"),
            new KeyValuePair("deadline", "締め切りの早い順"),
            new KeyValuePair("deadline-r", "締め切りの遅い順"),
            new KeyValuePair("beginDate", "開始日時の早い順"),
            new KeyValuePair("beginDate-r", "開始日時の遅い順")
    ));
    private static final List<KeyValuePair> CATEGORIES_FOR_SEARCH;
    static {
        List<KeyValuePair> categories = new ArrayList<KeyValuePair>();
        categories.add(new KeyValuePair("all", "全て"));
        categories.addAll(EventCategory.CATEGORIES);
        CATEGORIES_FOR_SEARCH = Collections.unmodifiableList(categories);
    }
    
    public EventSearchPage() {
        super("イベント検索 - [PARTAKE]");
        try {
            List<Event> events = EventService.get().getRecentEvents(5); // TODO: MAGIC NUMBER! 5
            
            createForm("", CATEGORIES_FOR_SEARCH.get(0), SORTORDERS.get(0), true);
            renderSearchResult(events, "最近登録された締め切り前のイベント");
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
    }
    
    public EventSearchPage(PageParameters params) {
        super("イベント検索 - [PARTAKE]");
        
        String searchTerm = params.get("searchTerm").toOptionalString();
        if (searchTerm == null) { searchTerm = ""; }
        else { searchTerm = searchTerm.trim(); }
        
        KeyValuePair category = findKeyValuePair(params.get("category").toOptionalString(), CATEGORIES_FOR_SEARCH, CATEGORIES_FOR_SEARCH.get(0));
        KeyValuePair sortOrder = findKeyValuePair(params.get("sortOrder").toOptionalString(), SORTORDERS, SORTORDERS.get(0));        
        
        String deadline = params.get("deadline").toOptionalString();
        boolean deadlineOnly = !"false".equals(deadline);
        
        try {
            List<Event> events = EventService.get().search(searchTerm, category.getKey(), sortOrder.getKey(), deadlineOnly, 50);
            
            createForm(searchTerm, category, sortOrder, deadlineOnly);
            renderSearchResult(events, "検索結果");
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        } catch (ParseException e) {
            // TODO: message を追加するべき
            throw new RestartResponseException(EventSearchPage.class);
        }
    }
    
    
    private void createForm(String searchTerm, KeyValuePair category, KeyValuePair sortOrder, boolean deadlineOnly) throws DAOException {
        ChoiceRenderer<KeyValuePair> choiceRenderer = new ChoiceRenderer<KeyValuePair>("value", "key");
        
        final TextField<String> searchTermField = new TextField<String>("search.term", new Model<String>(searchTerm)); 
        final DropDownChoice<KeyValuePair> searchCategoryChoice = new DropDownChoice<KeyValuePair>("search.category", new Model<KeyValuePair>(category), CATEGORIES_FOR_SEARCH, choiceRenderer);
        final DropDownChoice<KeyValuePair> searchOrderChoice = new DropDownChoice<KeyValuePair>("search.order", new Model<KeyValuePair>(sortOrder), SORTORDERS, choiceRenderer);
        final CheckBox searchDeadlineOnlyCheckBox = new CheckBox("search.beforeDeadlineOnly", new Model<Boolean>(deadlineOnly));
        
        Form form = new Form("search") {
            @Override
            protected void onSubmit() {
                String searchTerm = searchTermField.getDefaultModelObjectAsString();
                if (!StringUtils.isEmpty(searchTerm)) {
                    searchTerm.trim();
                }
                String category = CATEGORIES_FOR_SEARCH.get(0).getKey();
                {
                    KeyValuePair kvp = (KeyValuePair) searchCategoryChoice.getDefaultModelObject();
                    if (kvp != null) {
                        category = kvp.getKey();
                    }
                }
                String sortOrder = SORTORDERS.get(0).getKey();
                {
                    KeyValuePair kvp = (KeyValuePair) searchOrderChoice.getDefaultModelObject();
                    if (kvp != null) {
                        sortOrder = kvp.getKey();
                    }
                }
                boolean beforeDeadlineOnly = (Boolean) searchDeadlineOnlyCheckBox.getDefaultModelObject();
                
                PageParameters params = new PageParameters();
                params.add("searchTerm", searchTerm);
                params.add("category", category);
                params.add("sortOrder", sortOrder);
                params.add("deadline", beforeDeadlineOnly ? "true" : "false");
                
                setResponsePage(EventSearchPage.class, params);
            }  
        };
        
        {
            form.add(searchTermField);
            form.add(searchCategoryChoice);
            form.add(searchOrderChoice);
            form.add(searchDeadlineOnlyCheckBox);
            
            ImageButton submitButton = new ImageButton("search.submit", "/images/dummy");
            submitButton.add(new SimpleAttributeModifier("src", "/images/btn-search.png"));
            form.add(submitButton);
        }
        add(form);
    }
    
    private void renderSearchResult(List<Event> events, String title) throws DAOException {
        add(new Label("searchResult.title", title));
        if (CollectionUtils.isEmpty(events)) {
            add(new InvisibleComponent("searchResult"));
            add(new Label("noSearchResult", "検索結果がありませんでした。"));            
        } else {
            RepeatingView eventView = new RepeatingView("searchResult");
            for (Event event : events) {
                if (event == null) { continue; }
                eventView.add(new EventMiniPanel(eventView.newChildId(), event));
            }
            add(eventView);
            add(new InvisibleComponent("noSearchResult"));
        }
    }
    
    // ----------------------------------------------------------------------
    
    private KeyValuePair findKeyValuePair(String key, List<KeyValuePair> keyValuePairs, KeyValuePair defaultValue) {
        for (KeyValuePair kvp : keyValuePairs) {
            if (kvp.getKey().equals(key)) {
                return kvp;
            }
        }

        return defaultValue;
    }
    
}
