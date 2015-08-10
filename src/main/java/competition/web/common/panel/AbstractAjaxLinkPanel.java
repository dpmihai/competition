package competition.web.common.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import competition.web.common.ConfirmAjaxCallListener;

/**
 * @author Decebal Suiu
 */
public abstract class AbstractAjaxLinkPanel<T> extends GenericPanel<T> {

	private static final long serialVersionUID = 116634505227682256L;

	public static final String LINK_ID = "link";
	public static final String LABEL_ID = "label";

	protected AjaxLink<T> link;
	protected Label label;
	private String showMessage;

	public AbstractAjaxLinkPanel(String id) {
		this(id, null);
	}

	public AbstractAjaxLinkPanel(String id, IModel<T> model) {
		this(id, model, null);
	}

	public AbstractAjaxLinkPanel(String id, IModel<T> model, String showMessage) {
		super(id, model);
		this.showMessage = showMessage;
		link = createLink(LINK_ID);
		label = new Label(LABEL_ID, getDisplayString());
		link.add(label);
		add(link);
	}

	public abstract String getDisplayString();

	public void onClick(AjaxRequestTarget target) {
	}

	protected AjaxLink<T> createLink(String id) {
		return new AjaxLink<T>(id) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				AbstractAjaxLinkPanel.this.onClick(target);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmAjaxCallListener(showMessage));
			}

		};
	}

}
