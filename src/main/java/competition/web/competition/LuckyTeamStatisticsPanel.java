package competition.web.competition;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.HashMap;

import competition.domain.entity.Competition;
import competition.domain.entity.User;
import competition.web.CompetitionConfiguration;
import competition.web.common.DocumentInlineFrame;
import competition.web.util.ConnectionUtil;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.util.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.FluentReportRunner;

public class LuckyTeamStatisticsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(AverageStatisticsPanel.class);
	
	private Competition competition;
	private String user;
	
	public LuckyTeamStatisticsPanel(String id, IModel<Competition> competition, IModel<String> user) {
		super(id);		
		this.competition = competition.getObject();
		this.user = user.getObject();
		add(new DocumentInlineFrame("luckyStatistics", new LuckyTeamResource()));
	}		
	
	public void setUser(String user) {
		this.user = user;
	}

	private Integer getCompetitionId() {
		Integer competitionId = 0;
		if (competition != null) {
			competitionId = competition.getId();			
		} 
		return competitionId;
	}	

	class LuckyTeamResource extends ByteArrayResource {
				
		private static final long serialVersionUID = -6307719094949487807L;
		private byte[] data;
		private String userName;

		public LuckyTeamResource() {
			super("text/html");					
		}
		
		@Override
		protected byte[] getData(final Attributes attributes) {									
			data = generateReport();							 	
			return data;
		}				
		
		@Override
		protected void configureResponse(ResourceResponse response, Attributes attributes) {
			response.setCacheDuration(Duration.NONE); 
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
		
		private byte[] generateReport() {			
			HashMap<String, Object> pValues = new HashMap<String, Object>();
			pValues.put("Competition", getCompetitionId());
			pValues.put("User", user);
			ByteArrayOutputStream output;
			Connection connection = null;
			Report report = CompetitionConfiguration.get().getLuckyTeamReport();
			try {
				output = new ByteArrayOutputStream();
				connection = ConnectionUtil.createConnection();
				FluentReportRunner.report(report).
					connectTo(connection).
					withQueryTimeout(60).
					withParameterValues(pValues).
					formatAs(ReportRunner.HTML_FORMAT).
					run(output);				
				return output.toByteArray();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				return new byte[0];
			} finally {
				ConnectionUtil.closeConnection(connection);
			}
		}
	}
		
}
