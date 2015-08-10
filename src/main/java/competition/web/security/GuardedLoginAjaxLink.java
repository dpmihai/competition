package competition.web.security;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;

import competition.web.BaseAppSession;
import competition.web.BaseTemplatePage;
import competition.web.common.form.FormPanel;
import competition.web.competition.FullCompetitionPage;
import competition.web.security.event.LoginEvent;

/**
 * @author Decebal Suiu
 */
public class GuardedLoginAjaxLink<T> extends AjaxLink<T> {

	private static final long serialVersionUID = 1L;

	public GuardedLoginAjaxLink(final String id, final IModel<T> model) {
		super(id, model);
	}

	public GuardedLoginAjaxLink(String id) {
		super(id);
	}

	@Override
	public final void onClick(AjaxRequestTarget target) {
		if (BaseAppSession.get().isSignedIn()) {
			doSomething(target);
		} else {
			showLoginDialog(target);
		}
	}

	public void doSomething(AjaxRequestTarget target) {
	}

	private void showLoginDialog(AjaxRequestTarget target) {				
				
		ModalWindow dialog = findParent(BaseTemplatePage.class).getDialog();
		dialog.setTitle("Login");
		dialog.setAutoSize(true);

		LoginPanel loginPanel = new LoginPanel(FormPanel.CONTENT_ID) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onOk(AjaxRequestTarget target) {
				super.onOk(target);

				BaseTemplatePage page = findParent(BaseTemplatePage.class);				
				
				target.add(findParent(BaseTemplatePage.class).getHeaderPanel());
				target.add(findParent(BaseTemplatePage.class).getAdminPanel());			
				
				if (page instanceof FullCompetitionPage) {
					((FullCompetitionPage)page).refresh(target);
				}
				
				new LoginEvent(this, target).fire();
				doSomething(target);
			}

		};

		dialog.setContent(new FormPanel<Void>(dialog.getContentId(), loginPanel));
		dialog.show(target);
	}

}
