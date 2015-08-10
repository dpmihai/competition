package competition.domain.entity;

import java.util.Date;

public class JsonGame {
	
	private String hosts;
	private String guests;
	private String status;
	private int hostsScore;
	private int guestsScore;
	private Date date;
		
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public String getGuests() {
		return guests;
	}
	public void setGuests(String guests) {
		this.guests = guests;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getHostsScore() {
		return hostsScore;
	}
	public void setHostsScore(int hostsScore) {
		this.hostsScore = hostsScore;
	}
	public int getGuestsScore() {
		return guestsScore;
	}
	public void setGuestsScore(int guestsScore) {
		this.guestsScore = guestsScore;
	}
			
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String toPrettyString() {
		return "[ " + hosts + " - " + guests + "   " + hostsScore + " : " + guestsScore + " ]";
	}
	
	@Override
	public String toString() {
		return "JsonGame [hosts=" + hosts + ", guests=" + guests + ", status=" + status + ", hostsScore=" + hostsScore
				+ ", guestsScore=" + guestsScore + "]";
	}


}
