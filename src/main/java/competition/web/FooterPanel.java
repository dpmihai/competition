package competition.web;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Decebal Suiu
 */
public class FooterPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public FooterPanel(String id) {
	super(id);

        add(new BookmarkablePageLink<Void>("guideLines", GuideLinesPage.class));
    }

}
