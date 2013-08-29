
public class MoneyBet extends Bet {

	public boolean onHome;
	
	public MoneyBet(int t, Game g, boolean h, int l, double w) {
		super(t,g,l,w);
		onHome = h;
	}
	
	@Override
	public String toString() {
		String str = cleanCash(wager) + " (" + juice + ") on ";
		if(onHome) { str = str + game.home; }
		else { str = str + game.away; }
		if(gameStatus==Bet.NOT_STARTED) {
			if(time!=null) { return str + " to WIN: "+time; }
			return str + " to WIN: [Not Started]"; }
		str = str + " to WIN: " + game.away + " " + awayScore + ", " + game.home + " " + homeScore;
		if((gameStatus!=Bet.DONE)&&(time!=null)) { str = str + ", " + time; }
		return str;
	}
	
	public int[] getDiff() {
		int[] diff = new int[2];
		diff[1] = 0;
		if(onHome) { diff[0] = homeScore - awayScore; }
		else { diff[0] = awayScore - homeScore; }
		return diff;
	}

	@Override
	public int getResult() {
		if(homeScore>awayScore) {
			if(onHome) { return WIN; }
			return LOSS;
		}
		else if(homeScore<awayScore) {
			if(onHome) { return LOSS; }
			return WIN;
		}
		return PUSH;
	}
		
	

}
