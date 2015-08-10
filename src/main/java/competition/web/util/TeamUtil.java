package competition.web.util;

import competition.domain.entity.Question;

public class TeamUtil {
	
	public static String getAbbreviation(String name) {
		if (Question.EMPTY.equals(name)) {
			return name;
		}
		String[] parts = name.split(" ");
		if (parts.length >= 3) {
			return parts[0].substring(0, 1).toUpperCase() + parts[1].substring(0, 1).toUpperCase() + parts[2].substring(0, 1).toUpperCase();
		} else if (parts.length == 2) {
			if ("City".equalsIgnoreCase(parts[1]) && !"Manchester".equalsIgnoreCase(parts[0])) {
				return parts[0].substring(0, 3).toUpperCase();
			}
			return parts[0].substring(0, 1).toUpperCase() + parts[1].substring(0, 2).toUpperCase();
		} else {
			if (parts[0].length() <= 3) {
				return parts[0].toUpperCase();
			} else {
				return parts[0].substring(0, 3).toUpperCase();
			}
		}
	}

}
