package competition.web.common.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LinkPanel extends Panel {
    
    public LinkPanel(String id, Class pageClass, PageParameters parameters) {
        super(id);
        String text = parameters.get("text").toString();
        BookmarkablePageLink link = new BookmarkablePageLink("link", pageClass, parameters);
        link.add(new Label("label", text));
        add(link);
    }
    
}    
