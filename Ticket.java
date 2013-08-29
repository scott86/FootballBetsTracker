import java.util.ArrayList;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class Ticket implements Serializable {
	
	public ArrayList<Bet> bets;
	public double ttlWagered, maxWin;
	public String ncfScorePage, nflScorePage;
	public ArrayList<Game> ncfGames, nflGames;
	public boolean ncfGamesFound, nflGamesFound;
	
	public Ticket(int wn) {
		bets = new ArrayList<Bet>();
		ncfGames = new ArrayList<Game>();
		nflGames = new ArrayList<Game>();
		ttlWagered = 0.0;
		maxWin = 0.0;
		GregorianCalendar gc = new GregorianCalendar();
		int y = gc.get(Calendar.YEAR);
		int m = gc.get(Calendar.MONTH);
		if(m<2) { y--; } // januaray, february count as last year
		ncfScorePage = "http://scores.espn.go.com/ncf/scoreboard?confId=80&seasonYear="+y+"&seasonType=2&weekNumber="+wn;
		nflScorePage = "http://scores.espn.go.com/nfl/scoreboard?seasonYear="+y+"&seasonType=2&weekNumber="+(wn-1);
		ncfGamesFound = false;
		nflGamesFound = false;
	}
	
	public void getNcfGames() throws Exception { getNcfGames(false); }
	
	public void getNcfGames(boolean match) throws Exception {
		if(!match) { ncfGames.clear(); }
		//ArrayList<String> found = Scraper.findAll(ncfScorePage,"new gameObj\\(\"[0-9]*\"");
		ArrayList<String> found = Scraper.findAll(ncfScorePage,"(\"[0-9]*\\-hTeamName\"><a title=\"([a-z]|[A-Z]|\\.| |\\-|\\&|\\(|\\))*\")|(\"[0-9]*\\-hTeamName\">([a-z]|[A-Z]|\\.| |\\-|\\&|\\(|\\))*<)");
		ArrayList<String> found2 = Scraper.findAll("(\"[0-9]*\\-aTeamName\"><a title=\"([a-z]|[A-Z]|\\.| |\\-|\\&|\\(|\\))*\")|(\"[0-9]*\\-aTeamName\">([a-z]|[A-Z]|\\.| |\\-|\\&|\\(|\\))*<)");
		String id = "";
		String home = "";
		String away = "";
		System.out.println(found.size()+" "+found2.size());
		if(found.size()!=found2.size()) { throw new Exception(); }
		for( int i=0; i<found.size(); i++ ) {
			try {
				id = found.get(i).substring(1,found.get(i).indexOf("-",0));
				if(!id.equals(found2.get(i).substring(1,found2.get(i).indexOf("-",0)))) { throw new Exception(); }
				int eq1 = found.get(i).indexOf("=");
				int eq2 = found2.get(i).indexOf("=");
				// handle the (very) atypical non-link cases... thanks, "texas A&M-commerce", whoever you are - hope UTSA beats you by 100
				if(eq1==-1) { home = found.get(i).substring(found.get(i).indexOf(">")+1,found.get(i).length()-1); }
				else { home = found.get(i).substring(eq1+2,found.get(i).length()-1); }
				if(eq2==-1) { away = found2.get(i).substring(found2.get(i).indexOf(">")+1,found2.get(i).length()-1); }
				else { away = found2.get(i).substring(eq2+2,found2.get(i).length()-1); }
				if(!match) { ncfGames.add(new Game(id,home,away)); }
				else {
					Game g;
					for( int j=0; j<ncfGames.size(); j++ ) {
						g = ncfGames.get(j);
						if(g.home.equals(home)&&g.away.equals(away)) { g.id = id; }
						else if(g.home.equals(away)&&g.away.equals(home)) { g.id = id; }
					}
				}
			}
			catch(Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}
		ncfGamesFound = true;
	}
	
	public void getNflGames() throws Exception { getNflGames(false); }
	
	public void getNflGames(boolean match) throws Exception {
		if(!match) { nflGames.clear(); }
		ArrayList<String> found = Scraper.findAll(nflScorePage,"new gameObj\\(\"[0-9]*\"");
		ArrayList<String> found2;
		String id = "";
		String home = "";
		String away = "";
		for( int i=0; i<found.size(); i++ ) {
			try {
				id = found.get(i).substring(13,found.get(i).length()-1);
				found2 = Scraper.findAll(">([A-Z]|[a-z]|4|9| )*</a><span id=\""+id+"\\-hPossession");
				if(found2.size()==1) { home = found2.get(0).substring(1,found2.get(0).indexOf("<",0)); }
				found2 = Scraper.findAll(">([A-Z]|[a-z]|4|9| )*</a><span id=\""+id+"\\-aPossession");
				if(found2.size()==1) { away = found2.get(0).substring(1,found2.get(0).indexOf("<",0)); }
				if(!match) { nflGames.add(new Game(id,home,away)); }
				else {
					Game g;
					for( int j=0; j<nflGames.size(); j++ ) {
						g = nflGames.get(j);
						if(g.home.equals(home)&&g.away.equals(away)) { g.id = id; }
						else if(g.home.equals(away)&&g.away.equals(home)) { g.id = id; }
					}
				}
			}
			catch(Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}
		nflGamesFound = true;
	}
	
	public void showGames() {
		System.out.println("NCF Games:");
		for( int i=0; i<ncfGames.size(); i++ ) { System.out.println(ncfGames.get(i).toString()); }
		System.out.println("\nNFL Games:");
		for( int i=0; i<nflGames.size(); i++ ) { System.out.println(nflGames.get(i).toString()); }
	}
		
	
	public void addBet(Bet bet) { 
		ttlWagered = ttlWagered + bet.wager;
		bet.result = Bet.WIN;
		maxWin = maxWin + bet.payout(false) - bet.wager;
		bet.result = -1;
		bets.add(bet);
	}
	
	public void removeBet(int index) { removeBet(bets.get(index)); }
	
	public void removeBet(Bet bet) {
		ttlWagered = ttlWagered - bet.wager;
		bet.result = Bet.WIN;
		maxWin = maxWin - bet.payout(false) + bet.wager;
		bets.remove(bet);
	}
	
	public double payout() { return payout(true); }
	
	public double payout(boolean includePending) {
		double cash = 0.0;
		for( int i=0; i<bets.size(); i++ ) {
			if(includePending||(bets.get(i).gameStatus!=Bet.NOT_STARTED)) {
				cash = cash + bets.get(i).payout(true);
			}
			else { cash = cash + bets.get(i).wager; }
		}
		return cash;
	}
	
	
	
	public void findScores() {
		ArrayList<Bet> fails = new ArrayList<Bet>();
		for( int i=0; i<bets.size(); i++ ) {
			Bet bet = bets.get(i);
			if(bet.gameStatus!=Bet.DONE) {
				try { bet.findScore(); }
				catch(Exception e) {
					System.out.println(e.toString());
					e.printStackTrace();
					fails.add(bet);
				}
			}
		}
		boolean skip;
		for( int i=0; i<fails.size(); i++ ) {
			skip = false;
			Bet bet = fails.get(i);
			try {
				ArrayList<String> awayFound, homeFound, finalFound, finalFound2;
				try {
					if(i==0) { finalFound = Scraper.findAll(ncfScorePage,bet.game.id+"-statusText\">[0-9]"); }
					else { finalFound = Scraper.findAll(bet.game.id+"-statusText\">[0-9]"); }
					if(finalFound.size()==1) {
						finalFound2 = Scraper.findAll(bet.game.id+"-statusText\">[0-9]+:[0-9]+ (A|P)M ET");
						if(finalFound2.size()==1) {
							bet.gameStatus = Bet.NOT_STARTED;
							skip = true;
						}
						else { bet.gameStatus = Bet.ACTIVE; }
					}
					else { bet.gameStatus = Bet.ACTIVE; }
				}
				catch(Exception exc) {
					System.out.println(exc.toString());
					exc.printStackTrace();
				}
				if(i==0) { awayFound = Scraper.findAll(ncfScorePage,bet.game.id+"-aTotal\">[0-9]+<"); }
				else { awayFound = Scraper.findAll(bet.game.id+"-aTotal\">[0-9]+<"); }
				if(awayFound.size()!=1) {
					System.out.println("found "+awayFound.size()+" "+bet.game.away+" ("+bet.game.id+")");
					throw new Exception();
				}
				homeFound = Scraper.findAll(bet.game.id+"-hTotal\">[0-9]+<");
				if(homeFound.size()!=1) { throw new Exception(); }
				bet.awayScore = Integer.parseInt(awayFound.get(0).substring(awayFound.get(0).indexOf(">")+1,awayFound.get(0).length()-1));
				bet.homeScore = Integer.parseInt(homeFound.get(0).substring(homeFound.get(0).indexOf(">")+1,homeFound.get(0).length()-1));
				bet.gameStatus = Bet.ACTIVE;
				finalFound = Scraper.findAll(bet.game.id+"-statusText\">Final");
				if(finalFound.size()==1) { bet.gameStatus = Bet.DONE; }
			}
			catch(Exception ex) {
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
		}
	}
	
}
