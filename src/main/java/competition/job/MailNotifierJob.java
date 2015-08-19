package competition.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.MailData;
import competition.domain.entity.Stage;
import competition.domain.entity.User;
import competition.domain.entity.UserScore;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.CompetitionConfiguration;
import competition.web.util.DateUtil;

public class MailNotifierJob {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private long start;
	
	@Autowired
	private GeneralService generalService;
	
	@Autowired
	private BusinessService businessService;
	
	 @Autowired
	 private MailSender mailSender;
	
	public void run() {
		start = System.currentTimeMillis();
		LOG.info("*** MailNotifierJob job started at " +  new Date() + " ...");
		email();		
	}
	
	
	private void email() {		
		List<MailData> mailData = createMails();		
		sendMails(mailData);
	}
	
	public List<MailData> createMails() {
		List<Competition> competitions = businessService.getCompetitions();
		List<MailData> mailData = new ArrayList<MailData>();
		for (Competition c : competitions) {			
			if (!c.isActive()) {
				continue;
			}
			List<User> registeredUsers = businessService.getRegisteredUsers(c.getId());
			if (registeredUsers.size() == 0) {
				continue;
			}
			// there may be another stage starting just after the current one
			// so we must test two stages for completition
			Stage currentStage = businessService.getCurrentStage(c.getId());
			updateMailData(c, currentStage, registeredUsers, mailData);
			
			Stage nextStage = businessService.getNextStage(currentStage);
			updateMailData(c, nextStage, registeredUsers, mailData);
		}
		System.out.println("------------ Mail Data -----------------");
		System.out.println(mailData);
		System.out.println("----------------------------------------");
		return mailData;
	}
	
	private void updateMailData(Competition c, Stage stage, List<User> registeredUsers, List<MailData> mailData) {
		if (stage == null) {
			return;
		}
		
		int daysToStage = DateUtil.getNumberOfDays(new Date(), stage.getFixtureDate());
		if ((daysToStage <= 1) || (daysToStage > 5)) {
			return;
		}
		
		List<Game> games = businessService.getGames(stage.getId());
		List<Integer> gamesIds = new ArrayList<Integer>();
		for (Game game : games) {			
			gamesIds.add(game.getId());
		}
					
		for (User user : registeredUsers) {			
			if (needCompletition(gamesIds, user.getUsername())) {
				MailData md = new MailData();
				md.setUser(user);
				md.setCompetitionName(c.getName());
				md.setStageName(stage.getName());
				md.setDaysToStage(daysToStage-1);
				mailData.add(md);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean needCompletition(List<Integer> gamesIds, String username) {
		// stage games were not yet entered
		if (gamesIds.size() == 0) {
			LOG.info("*** No mail to send because there is no game entered yet in current stage.");
			return false;
		}
		List<UserScore> scores = generalService.search(
				new Search(UserScore.class).
				addFilter(Filter.equal("username", username)).
				addFilter(Filter.in("gameId", gamesIds)));
		if (scores.size() == 0) {
			return true;
		}
		for (UserScore score : scores) {
			if ((score.getHostsScore() == null) || (score.getGuestsScore() == null)) {
				return true;
			}
		}
		return false;		
	}
	
	@Async
	private void sendMails(List<MailData> mailData) {
		if (mailData.size() == 0) {
			long end = System.currentTimeMillis();
			LOG.info("*** MailNotifierJob job finished ... " + (end-start)/1000 + "  sec : No mails to send");
			return;
		}
		Map<User, List<MailData>> map = createMap(mailData);
		for (User user : map.keySet()) {
			List<MailData> userMailData = map.get(user);
			SimpleMailMessage mail = createMailMessage(user, userMailData);
			LOG.info("Try to send mail to user " + user.getUsername() + " subject="+mail.getSubject() + "  content="+mail.getText());
			try {
				mailSender.send(mail);
				LOG.info("*** Successfuly send mail to user " + user.getUsername());
			} catch (MailException ex) {
				LOG.info("*** Failed to send mail to user " + user.getUsername());
			}
		}	
		long end = System.currentTimeMillis();
		LOG.info("*** MailNotifierJob job finished ... " + (end-start)/1000 + "  sec");
	}
	
	private Map<User, List<MailData>> createMap(List<MailData> mailData) {
		Map<User, List<MailData>> map = new HashMap<User, List<MailData>> ();
		for (MailData md : mailData) {
			if (map.get(md.getUser()) == null) {
				map.put(md.getUser(), new ArrayList<MailData>());
			}
			map.get(md.getUser()).add(md);
		}
		return map;
	}
			
	private SimpleMailMessage createMailMessage(User user, List<MailData> mailData) {	
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom("dpmihai@gmail.com");
		String[] tos = new String[] {user.getEmail(), "dpmihai@yahoo.com"};
		mailMessage.setTo(tos);
		mailMessage.setSubject(createMailSubject(mailData));
		mailMessage.setText(createMailMessage(mailData));
		return mailMessage;
	}
	
	private String createMailSubject(List<MailData> mailData) {
		StringBuilder sb = new StringBuilder(mailData.get(0).getUser().getUsername().toUpperCase()).append(" : ");
		int count = 0;
		for (MailData md : mailData) {
			if (count > 0) {
				sb.append(" / ");
			}
			sb.append(md.getCompetitionName()).append(" ").append(md.getStageName());			    
			count++;   
		}
		return sb.toString();
	}
	
	// create message for one user
	private String createMailMessage(List<MailData> mailData) {
		StringBuilder sb = new StringBuilder();
		for (MailData md : mailData) {
			sb.append(md.getUser().getUsername().toUpperCase()).
			   append(" trebuie sa completezi ").
			   append(md.getCompetitionName()).append(" ").
			   append(md.getStageName());
			if ( md.getDaysToStage() > 1) {
			   sb.append(". Au mai ramas " + md.getDaysToStage() + " zile.\r\n");
			} else {
				sb.append(". A mai ramas o zi.\r\n");
			}
		}
		
		sb.append("\r\nAcest mail a fost generat automat de site-ul Competition :" + CompetitionConfiguration.get().getWebBaseUrl() + "/competition/\r\n");
		return sb.toString();
	}


	// needed for web services
	public void setGeneralService(GeneralService generalService) {
		this.generalService = generalService;
	}

	// needed for web services
	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

}
