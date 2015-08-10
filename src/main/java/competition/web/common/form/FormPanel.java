package competition.web.common.form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import competition.web.common.panel.GenericPanel;

/**
 * @author Decebal Suiu
 */
public class FormPanel<T> extends GenericPanel<T> {

	public static final String CONTENT_ID = "contentPanel";

	private static final long serialVersionUID = 1L;

	protected Form<T> form;
	protected FeedbackPanel feedbackPanel;

	private FormContentPanel<T> contentPanel;

	private AjaxLink<Void> cancelButton;
	private AjaxSubmitLink applyButton;
	private AjaxSubmitLink okButton;

	public FormPanel(String id, FormContentPanel<T> contentPanel) {
		super(id);

		contentPanel.setOutputMarkupId(true);

		this.contentPanel = contentPanel;

		form = new Form<T>("form", contentPanel.getModel());
		form.add(contentPanel);
		add(form);

		feedbackPanel = new FeedbackPanel("feedback");
		feedbackPanel.setFilter(new RenderOnlyWhatMyChildrenMissedFeedbackMessageFilter(form));
		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);

		cancelButton = createCancelButton();
		form.add(cancelButton);

		applyButton = createApplyButton();
		applyButton.setVisible(false);
		form.add(applyButton);

		okButton = createOkButton();
		form.add(okButton);
	}

	public Form<T> getForm() {
		return form;
	}

	public FeedbackPanel getFeedbackPanel() {
		return feedbackPanel;
	}

	public AjaxSubmitLink getOkButton() {
		return okButton;
	}

	public AjaxLink<Void> getCancelButton() {
		return cancelButton;
	}

	public AjaxSubmitLink getApplyButton() {
		return applyButton;
	}

	public void setOkButtonValue(String value) {
		setButtonValue(okButton, value);
	}

	public void setCancelButtonValue(String value) {
		setButtonValue(cancelButton, value);
	}

	public void setApplyButtonValue(String value) {
		setButtonValue(applyButton, value);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		visitChildren(new DefaultFocusFormVisitor());
	}

	private AjaxLink<Void> createCancelButton() {
		return new AjaxLink<Void>("cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}

		};
	}

	private AjaxSubmitLink createApplyButton() {
		return new AjaxSubmitLink("apply") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onApply(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
			}

		};
	}

	private AjaxSubmitLink createOkButton() {
		return new AjaxSubmitLink("ok") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onOk(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);				
			}

		};
	}

	protected void onOk(AjaxRequestTarget target) {
		contentPanel.onOk(target);
	}

	protected void onCancel(AjaxRequestTarget target) {
		setVisible(false);
		target.add(this);

		form.clearInput();
		target.add(form);

		contentPanel.onCancel(target);
	}

	protected void onApply(AjaxRequestTarget target) {
		contentPanel.onApply(target);
	}

	private void setButtonValue(AbstractLink button, String value) {
		button.add(new AttributeModifier("value", Model.of(value)));
	}

}
