package in.partake.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.page.base.PartakeBasePage;
import in.partake.util.KeyValuePair;


public class FeedListPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    
    public FeedListPage() {

        add(new ListView<KeyValuePair>("feeds", EventCategory.CATEGORIES) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<KeyValuePair> item) {
                ExternalLink link = new ExternalLink("key", "/feed/category/" + item.getModelObject().getKey());
                item.add(link);
                link.add(new Label("value", item.getModelObject().getValue()));
            }
        });

        add(new ListView<KeyValuePair>("calendars", EventCategory.CATEGORIES) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<KeyValuePair> item) {
                ExternalLink link = new ExternalLink("key", "/calendars/category/" + item.getModelObject().getKey());
                item.add(link);
                link.add(new Label("value", item.getModelObject().getValue()));
            }
        });
    }
}
