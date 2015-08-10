package competition.web;

import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.LoadReportException;
import ro.nextreports.engine.util.ReportUtil;

public class CompetitionConfiguration {

	private static CompetitionConfiguration singleton = new CompetitionConfiguration();
	
	private Configuration configuration;

	private CompetitionConfiguration() {
		// prevent instantiation from outside
	}
	
	public static CompetitionConfiguration get() {
		return singleton;
	}	
	
	// used by StorageUpdate9 to put existing properties inside JCR
    public Configuration getConfiguration() {
		if (configuration == null) {
			try {
				configuration = new PropertiesConfiguration(getClass().getResource("/competition.properties"));
			} catch (ConfigurationException e) {
				throw new RuntimeException(e);
			}
		}		
		return configuration;
	}
    
    public String getProxyHost() {
    	return getConfiguration().getString("proxy.host");
    }
    
    public Integer getProxyPort() {
    	String portS = getConfiguration().getString("proxy.port");
    	try {
    		Integer port = Integer.parseInt(portS);
    		return port;
    	} catch (NumberFormatException ex) {
    		return 0;
    	}
    }
    
    public String getWebBaseUrl() {
    	return getConfiguration().getString("web.baseUrl");
    }
    
    public Report getReport() {
    	InputStream is = getClass().getResourceAsStream("/Full.report");
    	try {
			return ReportUtil.loadReport(is);
		} catch (LoadReportException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public Report getAverageStatisticsReport() {
    	InputStream is = getClass().getResourceAsStream("/AveragePerStage.report");
    	try {
			return ReportUtil.loadReport(is);
		} catch (LoadReportException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public Report getLuckyTeamReport() {
    	InputStream is = getClass().getResourceAsStream("/LuckyTeam.report");
    	try {
			return ReportUtil.loadReport(is);
		} catch (LoadReportException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public Report getCompetitionReport() {
    	InputStream is = getClass().getResourceAsStream("/Competition.report");
    	try {
			return ReportUtil.loadReport(is);
		} catch (LoadReportException e) {
			e.printStackTrace();
			return null;
		}
    }
			            
}

