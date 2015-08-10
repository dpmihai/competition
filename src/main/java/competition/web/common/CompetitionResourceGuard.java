package competition.web.common;

import org.apache.wicket.markup.html.SecurePackageResourceGuard;

public class CompetitionResourceGuard extends SecurePackageResourceGuard {

	/**
	 * Default constructor
	 */
	public CompetitionResourceGuard() {
		super(new SimpleCache(10));
		addPattern("+*.report");
		addPattern("+*.chart");			
	}

}
