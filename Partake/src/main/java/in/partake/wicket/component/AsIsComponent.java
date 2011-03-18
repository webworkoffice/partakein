package in.partake.wicket.component;

import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * HTML テンプレートをそのまま出す場合に用いる。
 * @author shinyak
 *
 */
public class AsIsComponent extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    public AsIsComponent(String id) {
        super(id);
    }
}
