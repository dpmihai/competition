package competition.job;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.search.Search;

import competition.domain.entity.Competition;
import competition.domain.entity.Rankings;
import competition.service.BusinessService;
import competition.service.GeneralService;

public class UserRankingJob {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private GeneralService generalService;
	
	@Autowired
	private BusinessService businessService;

	public void run() {		
		long start = System.currentTimeMillis();
		log.info("*** RankingJob job started at " + new Date() + " ...");		
		computeRankings();		
		long end = System.currentTimeMillis();
		log.info("*** RankingJob job finished ... " + (end - start) / 1000 + "  sec");
	}

	private void computeRankings() {
		List<Competition> competitions = generalService.search(new Search(Competition.class));
		for (Competition competition : competitions) {
			if (competition.isActive()) {
				long start = System.currentTimeMillis();
				List<Rankings> rankings = businessService.resetUsersRanking(competition);
				businessService.setUsersRanking(rankings);
				long end = System.currentTimeMillis();
				log.info("  * competition rankings : " + competition.getName() + " took " + (end - start) / 1000 + "  sec");
			}						
		}
	}
				
    // for manual run
	
	public void setGeneralService(GeneralService generalService) {
		this.generalService = generalService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}
	

}
