package competition.misc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import competition.domain.entity.JsonGame;

public class JsonTest {
	
	public static void main(String[] args) {
		
		try {
			InputStream is = new FileInputStream("D:/Public/competition-1.6/sample-json.json");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			StringBuilder sb = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			String data = sb.toString();						
			System.out.println(data);
			
			List<JsonGame> result = new ArrayList<JsonGame>();
			createGamesFromJson(result, data);
			System.out.println("**** games = " + result.size());
			for (JsonGame game : result) {
				System.out.println(game);
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}			
	
	public static void createGamesFromJson(List<JsonGame> result, String data) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(data);
		for (Object o : a) {
			JsonGame game = new JsonGame();
			JSONObject obj = (JSONObject) o;

			String homeTeam = (String) obj.get("home");
			game.setHosts(homeTeam);

			String awayTeam = (String) obj.get("away");
			game.setGuests(awayTeam);

			String status = (String) obj.get("status");
			game.setStatus(status);

			JSONArray fulltime = (JSONArray) obj.get("fulltime");

			for (int i=0, size=fulltime.size(); i<size; i++) {
				if (i == 0) {
					game.setHostsScore(((Long)fulltime.get(i)).intValue());
				} else {
					game.setGuestsScore(((Long)fulltime.get(i)).intValue());
				}
			}
			result.add(game);
		}
	}

}
