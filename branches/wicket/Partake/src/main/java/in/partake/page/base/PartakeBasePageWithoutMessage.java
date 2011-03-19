package in.partake.page.base;

import org.apache.wicket.markup.html.basic.Label;

import in.partake.application.PartakeSession;

public class PartakeBasePageWithoutMessage extends PartakePage {
    private static final long serialVersionUID = 1L;

    public PartakeBasePageWithoutMessage() {
        header();
    }

    private void header() {
        add(new Label("partake.title", getTitle()));
        String url = ""; 

        PartakeSession session = PartakeSession.get();
        if (session.isLoggedIn()) {
            add(new LoggedInHeaderPanel("header"));
        } else {
            add(new NotLoggedInHeaderPanel("header", url));
        }
    }
    
    /**
     * タイトルを設定する
     * @return
     */
    protected String getTitle() {
        return "[PARTAKE]";
    }
}
