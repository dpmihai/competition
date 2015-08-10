package competition.web.competition;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import competition.domain.entity.Competition;
import competition.web.CompetitionConfiguration;
import competition.web.common.DocumentInlineFrame;
import competition.web.util.ConnectionUtil;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.AbstractResource.ResourceResponse;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.FluentReportRunner;

public class AverageStatisticsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(AverageStatisticsPanel.class);
	
	private Competition competition;
	
	// cache for average statistics per competition
	// in GamesPanel addEdit(game) we do a resetAverageStatistics
	private static Map<String, byte[]> averageStats = new HashMap<String, byte[]>(); 

	public AverageStatisticsPanel(String id, IModel<Competition> competition) {
		super(id);		
		this.competition = competition.getObject();
		setRenderBodyOnly(true);
		add(new DocumentInlineFrame("averageStatistics", new AverageReportResource()));
	}
	
	private String getCompetitionName() {
		String competitionName = "";
		if (competition != null) {
			competitionName = competition.getName();			
		} 
		return competitionName;
	}	

	class AverageReportResource extends ByteArrayResource {
				
		private static final long serialVersionUID = -6307719094949487807L;
		private byte[] data;
		private String userName;

		public AverageReportResource() {
			super("text/html");					
		}
		
		@Override
		protected byte[] getData(final Attributes attributes) {			
			data = averageStats.get(getCompetitionName());			
			if (data == null) {					
				data = generateReport();				
				averageStats.put(getCompetitionName(), data);				
			} 	
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
			pValues.put("Competition", getCompetitionName());
			ByteArrayOutputStream output;
			Connection connection = null;
			Report report = CompetitionConfiguration.get().getAverageStatisticsReport();
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
	
	public static void resetAverageStatistics(String competitionName) {
		averageStats.remove(competitionName);
	}
}
