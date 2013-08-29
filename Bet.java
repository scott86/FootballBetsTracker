import java.util.ArrayList;
import java.io.Serializable;

public abstract class Bet implements Serializable {

	public static final int NCF = 0;
	public static final int NFL = 1;

	public static final int WIN = 0;
	public static final int LOSS = 1;
	public static final int PUSH = 2;
	
	public static final int NOT_STARTED = 0;
	public static final int ACTIVE = 1;
	public static final int DONE = 2;
	
	public Game game;
	public String time;
	public int juice, homeScore, awayScore, result, gameStatus, type;
	public double wager;
	
	public Bet(int t, Game g, int j, double w) {
		type = t;
		game = g;
		juice = j;
		wager = w;
		homeScore = 0;
		awayScore = 0;
		gameStatus = NOT_STARTED;
		result = -1;
	}
			
	public double payout(boolean update) {
		if(update) { result = getResult(); }
		if( result == WIN ) {
			if( juice > 0 ) { return wager + (wager*(juice/100.0)); }
			else { return wager + (wager*(-100.0/juice)); }
		}
		if( result == LOSS ) { return 0.0; }
		return wager;
	}
	
	public static String cleanCash(double cash) {
		boolean neg = cash < 0.0;
		if(neg) { cash = cash*-1.0; }
		int dollars = (int)cash;
		cash = cash - (double)dollars;
		int cents = (int)(cash*100.0);
		cash = cash - ((double)cents)/100.0;
		if( cash >= 0.005 ) { cents++; }
		if( cents == 100 ) {
			dollars++;
			cents = 0;
		}
		if(neg) { return "-$"+dollars+"."+cents; }
		return "$"+dollars+"."+cents;
	}
	
	public abstract int getResult();
	
	public abstract int[] getDiff();
	
	public abstract String toString();
	
	public void findScore() throws Exception {
		if(type==NCF) {
			//System.out.println("finding NCF "+game.id);
			ArrayList<String> found, found2;
			try {
				found = Scraper.findAll("http://scores.espn.go.com/ncf/boxscore?gameId="+game.id,"game-state\">[0-9][0-9]?:[0-9][0-9] (((P|A)M ET)|(((1st|2nd|3rd|4th) Qtr)|Halftime|[1-9]OT))<");
				if(found.size()==1) {
					time = found.get(0).substring(found.get(0).indexOf(">")+1,found.get(0).length()-1);
					System.out.println("time: "+time);
					found2 = Scraper.findAll("game-state\">[0-9]+:[0-9]+ (A|P)M ET");
					if(found2.size()==1) {
						gameStatus = NOT_STARTED;
						return;
					}
					else { gameStatus = ACTIVE; }
				}
				else { gameStatus = ACTIVE; }
				found = Scraper.findAll("<span>[0-9]+<");
			}
			catch(Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
				found = Scraper.findAll("http://scores.espn.go.com/ncf/boxscore?gameId="+game.id,"<span>[0-9]+<");
			}
			for( int i=0; i<found.size(); i++ ) { System.out.println(found.get(i)); }
			if(found.size()<2) { System.out.println(game.id); throw new Exception(); }
			awayScore = Integer.parseInt(found.get(0).substring(6,found.get(0).length()-1));
			homeScore = Integer.parseInt(found.get(1).substring(6,found.get(1).length()-1));
			try {
				found = Scraper.findAll("game-state\">Final");
				if(found.size()==1) { gameStatus = DONE; }
			}
			catch(Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}
		else if(type==NFL) {
			//System.out.println("finding NFL "+game.id);
			//find nfl score
			ArrayList<String> found = Scraper.findAll("http://scores.espn.go.com/nfl/boxscore?gameId="+game.id,"gameStatusBarText\">[0-9][0-9]?:[0-9][0-9] (A|P)M [A-Z][A-Z]?T<");
			if(found.size()==1) {
				gameStatus = NOT_STARTED;
				time = found.get(0).substring(19,found.get(0).length()-1);
				return;
			}
			found = Scraper.findAll(game.id+"\\-awayScore\">[0-9]*<");
			ArrayList<String> found2 = Scraper.findAll(game.id+"\\-homeScore\">[0-9]*<");
			if((found.size()==1)&&(found2.size()==1)) {
				homeScore = Integer.parseInt(found2.get(0).substring(game.id.length()+12,found2.get(0).length()-1));
				awayScore = Integer.parseInt(found.get(0).substring(game.id.length()+12,found.get(0).length()-1));
				found = Scraper.findAll("gameStatusBarText\">[^<]{1,100}<");
				if(found.size()==1) {
					time = found.get(0).substring(19,found.get(0).length()-1);
					gameStatus = ACTIVE;
					if(time.length()>=5) {
						if(time.substring(0,5).equals("Final")) { gameStatus = DONE; }
					} 
				}
			}
		}
	}
	
}
