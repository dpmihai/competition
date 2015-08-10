package competition.web.common.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.util.visit.IVisit;

/**
 * @author Decebal Suiu
 */
public class RenderOnlyWhatMyChildrenMissedFeedbackMessageFilter implements IFeedbackMessageFilter {

	private static final long serialVersionUID = 1L;

	private List<IFeedbackMessageFilter> filters;

	public RenderOnlyWhatMyChildrenMissedFeedbackMessageFilter(Form<?> form) {
		filters = new ArrayList<IFeedbackMessageFilter>();
		form.visitChildren(FeedbackPanel.class, new IVisitor<FeedbackPanel, Void>() {

			@Override
			public void component(FeedbackPanel component, IVisit<Void> visit) {
				filters.add(component.getFilter());
			}

		});
	}

	@Override
	public boolean accept(FeedbackMessage message) {
		for (IFeedbackMessageFilter filter : filters) {
			if (filter.accept(message)) {
				return false;
			}
		}

		return true;
	}

}
