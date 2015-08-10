package competition.web.common.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import competition.web.common.panel.GenericPanel;

/**
 * @author Decebal Suiu
 */
public class FormContentPanel<T> extends GenericPanel<T> {

	private static final long serialVersionUID = 1L;

	public FormContentPanel() {
		super(FormPanel.CONTENT_ID);
		setRenderBodyOnly(true);
	}

	public FormContentPanel(String id) {
		super(id);
		setRenderBodyOnly(true);
	}

	public FormContentPanel(String id, IModel<T> model) {
		super(id, model);
		setRenderBodyOnly(true);
	}

	public void onOk(AjaxRequestTarget target) {
		ModalWindow.closeCurrent(target);
	}

	public void onCancel(AjaxRequestTarget target) {
		ModalWindow.closeCurrent(target);
	}

	public void onApply(AjaxRequestTarget target) {
		ModalWindow.closeCurrent(target);
	}

	@SuppressWarnings("unchecked")
	public Form<T> getForm() {
		return findParent(FormPanel.class).getForm();
	}

	public FeedbackPanel getFeadbackPanel() {
		return findParent(FormPanel.class).getFeedbackPanel();
	}

	public void addWithFeedback(FormComponent<?> component) {
		String feedbackId = component.getId() + "Feedback";
		addWithFeedback(component, feedbackId);
	}

	public void addWithFeedback(FormComponent<?> component, String feedbackId) {
		add(component);
		add(new FeedbackPanel(feedbackId, new ComponentFeedbackMessageFilter(component)).setOutputMarkupId(true));
	}

}
