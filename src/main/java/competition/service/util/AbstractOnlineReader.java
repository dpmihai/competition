package competition.service.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import competition.domain.entity.JsonGame;
import competition.web.CompetitionConfiguration;

public abstract class AbstractOnlineReader implements OnlineReader {
	
	protected abstract String getURL();
	private boolean debug = false;
	
	protected abstract void createGamesFromHtml(List<JsonGame> result, String html, Date startDate, Date endDate);

	@Override
	public List<JsonGame> readOnlineGames(Date start, Date end) {
		List<JsonGame> result = new ArrayList<JsonGame>();
		try {
			URL url = new URL(getURL());
			if (url != null) {
				String proxyHost = getProxyHost();
				Integer proxyPort = getProxyPort();
				//System.out.println("**** proxyHost = " + proxyHost +  " port="+proxyPort);
				URLConnection connection ;
				if ((proxyHost == null) || proxyHost.trim().isEmpty()) {
					connection = url.openConnection();
				} else {					
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
					connection = url.openConnection(proxy);
				}
				if (connection != null) {
					InputStream stream = connection.getInputStream();
					if (stream != null) {
						BufferedReader br = new BufferedReader(new InputStreamReader(stream));
						String inputLine;
						StringBuilder sb = new StringBuilder();
						while ((inputLine = br.readLine()) != null) {
							sb.append(inputLine);
						}
						String html = sb.toString();
						createGamesFromHtml(result, html, start, end);						
					}
				}
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String getProxyHost() {
		return CompetitionConfiguration.get().getProxyHost();
	}
	
	public Integer getProxyPort() {
		return CompetitionConfiguration.get().getProxyPort();
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	protected void print(String message) {
		if (debug) {
			System.out.println(message);
		}
	}

}
