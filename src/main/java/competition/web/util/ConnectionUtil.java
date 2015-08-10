package competition.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.web.CompetitionConfiguration;

public class ConnectionUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionUtil.class);

	public static Connection createConnection() {
		
	    Configuration cc = CompetitionConfiguration.get().getConfiguration();		
		String driver = cc.getString("jdbc.driver");
		String url = cc.getString("jdbc.url");		
		try {
			Class.forName(driver);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return null;
		}

		try {
			return DriverManager.getConnection(url, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return null;
		}

	}
	
	public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
                LOG.error(e.getMessage(), e);
            }
        }
    }

}
