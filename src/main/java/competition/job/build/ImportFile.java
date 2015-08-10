package competition.job.build;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.Team;

/**
 * Import file format for a competition
 * 
 * <comp-name>;<comp-image>;<comp-rss>
 * <teams-number>
 * <team>;<team-logo>
 * .....
 * <stage-name>;<stage-date>;<stage-games-number>
 * <host-team-name> v <guest-team-name>
 * .....
 * 
 * Fixtures for premier-league: http://www.premierleague.com/en-gb/news/news/barclays-premier-league-2012-13-fixtures-day.html
 * 
 * @author Mihai Dinca-Panaitescu
 *
 */
public class ImportFile {
	
	
	public static ImportData importCompetition(InputStream is)  {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		ImportData importData = new ImportData();
		try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new InputStreamReader(is));
            try {
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                int lineNo = 1;
                int teamNo = 0;
                List<Team> teams = new ArrayList<Team>();
                Map<Stage, List<Game>> stages = new LinkedHashMap<Stage, List<Game>>();
                Stage stage = null;
                while ((line = input.readLine()) != null) {
                    if (lineNo == 1) {
                    	// competition
                    	String[] array = line.split(";");
                    	if (array.length != 3) {
                    		throw new IllegalArgumentException("Invalid import file format: first line.");
                    	}
                    	Competition competition = new Competition();
                    	competition.setName(array[0]);
                    	competition.setImageFile(array[1]);
                    	competition.setRss(array[2]);
                    	importData.setCompetition(competition);
                    } else if (lineNo == 2) {
                    	// teams number
                    	try {
                    		teamNo = Integer.parseInt(line);                    		
                    	} catch (NumberFormatException ex) {
                    		throw new IllegalArgumentException("Invalid import file format: second line is not a number.");
                    	}
                    } else if ((lineNo >= 2) && (lineNo <= teamNo + 2)) {
                    	// teams
                    	String[] array = line.split(";");
                    	if (array.length != 3) {
                    		throw new IllegalArgumentException("Invalid import file format: line " + lineNo + " team.");
                    	}
                    	Team team = new Team();
                    	team.setId(lineNo-2); // id to use for teams -> must be set to null before insert team!
                    	team.setName(array[0]);
                    	team.setAbbreviation(array[1]);
                    	team.setAvatarFile(array[2]);
                    	teams.add(team);
                    } else {
                    	// stages with games                    	
                    	String[] array = line.split(";");
                    	if (array.length == 2) {
                    		// stage
                    		stage = new Stage();
                    		stage.setName(array[0]);
                    		try {
								stage.setFixtureDate(sdf.parse(array[1]));
							} catch (ParseException e) {
								throw new IllegalArgumentException("Invalid import file format: line " + lineNo + " : invalid date format = " + array[1]);
							}
                    		stages.put(stage, new ArrayList<Game>());
                    	} else {
                    		// game
                    		String[] opponents = line.split("\\s+v\\s+");
                    		Game game = new Game();
                    		game.setFixtureDate(stage.getFixtureDate());
                    		game.setHostsId(getTeamId(opponents[0], teams));
                    		game.setGuestsId(getTeamId(opponents[1], teams));
                    		stages.get(stage).add(game);
                    	}
                    }
                    lineNo++;
                }
                importData.setTeams(teams);
                importData.setStages(stages);
            }
            finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		return importData;
		
	}
	
	private static Integer getTeamId(String teamName, List<Team> teams) {
		for (Team team : teams) {
			if (teamName.equals(team.getName())) {
				return team.getId();
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		ImportData data;
		try {
			data = importCompetition(new FileInputStream("D:/Public/competition/premier-league-2012-2013-import.txt"));
			System.out.println(data.getCompetition());
			System.out.println(data.getTeams());
			for (Stage stage : data.getStages().keySet()) {
				System.out.println("---------------------");
				System.out.println(stage);
				System.out.println(data.getStages().get(stage));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
