package competition.web.common.panel;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class AbstractImageLinkPanel<T extends Serializable, C extends Page> extends GenericPanel<T> {
	
	private static final long serialVersionUID = 6355481745861761329L;
	
	public AbstractImageLinkPanel(String id, final Class<C> pageClass, Model<T> model) {
		super(id, model);		
		BookmarkablePageLink<T> link = new BookmarkablePageLink<T>("link", pageClass, createPageParameters());
//		Link<T> link = new Link<T>("link", model) {
//			
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void onClick() {
//				setResponsePage(pageClass, createPageParameters());				
//			}
//			
//		};
		link.add(getImage());
		add(link);
		addComponents();
	}

	public abstract String getImageName();
	
	public abstract PageParameters createPageParameters();

	protected Component getImage() {
		return new ContextImage("image", getImageName());
	}
	
	protected void addComponents() {		
	}

}
