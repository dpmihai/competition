package competition.web.site;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.PageExpiredException;

import competition.web.BaseTemplatePage;
import competition.web.HomePage;

public class Page500 extends BaseTemplatePage {

	public Page500() {
		error("A aparut o eroare interna.");
		createComponents();
	}

	public Page500(String err) {
		createComponents();
		String localeErr = new ResourceModel(err).getObject();
		error(localeErr);
		logger.error("Error: " + localeErr);
	}

	public Page500(RuntimeException e) {
		if (e instanceof PageExpiredException) {
			throw new RestartResponseException(HomePage.class);
		}
		createComponents();
		error("A aparut o eroare interna.");
		logger.error(e.getMessage(), e);
	}

	private void createComponents() {
		add(new FeedbackPanel("feedback"));
	}
}
