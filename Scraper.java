import java.util.ArrayList;
import java.util.regex.*;
import java.io.*;
import java.net.*;

public class Scraper {

	public static final String mlbDotComCityRegex = "((LA Dodgers)|(Detroit)|(Chi Cubs)|(Texas)|(Boston)|(Philadelphia)|(Tampa Bay)|(Houston)|(Cincinnati)|(Cleveland)|(Atlanta)|(Pittsburgh)|(Minnesota)|(Milwaukee)|(Colorado)|(Kansas City)|(Washington)|(Baltimore)|(Oakland)|(San Francisco)|(LA Angels)|(St. Louis)|(Chi White Sox)|(Florida)|(San Diego)|(Seattle)|(Arizona)|(Toronto)|(NY Mets)|(NY Yankees))";
	public static final String mlbDotComTeamRegex = "((Dodgers)|(Tigers)|(Cubs)|(Rangers)|(Red Sox)|(Phillies)|(Rays)|(Astros)|(Reds)|(Indians)|(Braves)|(Pirates)|(Twins)|(Brewers)|(Rockies)|(Royals)|(Nationals)|(Orioles)|(Athletics)|(Giants)|(Angels)|(Cardinals)|(White Sox)|(Marlins)|(Padres)|(Mariners)|(D-backs)|(Blue Jays)|(Mets)|(Yankees))";
	public static final String[] mlbDotComCriteria = {"\"home_team_name\":\""+Scraper.mlbDotComTeamRegex+"\"","\"away_team_name\":\""+Scraper.mlbDotComTeamRegex+"\"","\"r\":\\{\"home\":\"[0-9]+\",\"away\":\"[0-9]+\""};
	public static final String espnCriteria = "[0-9]{9}\">(at )?[A-Z]([A-Z]|[a-z]| |\\.)* [0-9]+, (at )?[A-Z]([A-Z]|[a-z]| |\\.)* [0-9]+";
	
	public static String wholePage = "";
	
	public static void main( String[] args ) {
		try {
			String criteria = "-hTeamName\"><a title=\"([a-z]|[A-Z]|\\.| |\\-|\\&|\\(|\\))*\"";
			//ArrayList<String> scores = findAll("http://espn.go.com/ncf/scoreboard?confId=80&seasonYear=2010&seasonType=2&weekNumber=1",criteria);
			ArrayList<String> scores = Scraper.findAll("http://scores.espn.go.com/ncf/scoreboard?confId=80&seasonYear=2010&seasonType=2&weekNumber=2","new gameObj\\(\"[0-9]*\"");
			for( int i=0; i<scores.size(); i++ ) {
				System.out.println(scores.get(i));
			}
			System.out.println(scores.size());
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String[][] findAll( String page, String[] matches ) throws Exception {
		System.out.println("Requested page: "+page);
		String[][] result;
		ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
		for( int i=0; i<matches.length; i++ ) {
			temp.add( findAll(page,matches[i]) );
		}
		if(temp.size()<1) { throw new Exception(); }
		int numFound = temp.get(0).size();
		for( int i=1; i<temp.size(); i++ ) {
			if(temp.get(i).size()!=numFound) {
				System.out.println("data mismatch!  dim 0 size = "+numFound+", dim "+i+" = "+temp.get(i).size());
				for( int j=0; j<temp.get(0).size(); j++ ) {
					System.out.println(temp.get(0).get(j));
				}
				for( int k=0; k<temp.get(i).size(); k++ ) {
					System.out.println(temp.get(i).get(k));
				}
				throw new Exception();
			}
		}
		result = new String[numFound][temp.size()];
		for( int i=0; i<numFound; i++ ) {
			for( int j=0; j<temp.size(); j++ ) {
				result[i][j] = temp.get(j).get(i);
			}
		}
		return result;
	}
	
	public static ArrayList<String> findAll( String page, String match) throws Exception {
		//System.out.println("Requested page: "+page);
		wholePage = "";
		URL url = new URL(page);
		InputStream is = url.openStream();
		DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
		String line = "line";
		while((line = dis.readLine()) != null) { wholePage = wholePage + line; }
		dis.close();
		is.close();
		return findAll(match); }

	public static ArrayList<String> findAll( String match ) throws Exception {

		ArrayList<String> matches = new ArrayList<String>();
		Pattern pattern = Pattern.compile(match);
		Matcher matcher = pattern.matcher(wholePage);

		while(matcher.find()) { matches.add(matcher.group()); }

		return matches;

	}

	public static String convertMlbDotComName(String raw) {
		if(raw.equals("D-backs")) { return "Diamondbacks"; }
		return raw;
	}

}