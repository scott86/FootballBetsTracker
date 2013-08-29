
public class SpreadBet extends Bet {
	
	public boolean onHome;
	public PointLine ptLine;
	private int ptDiff;
	
	public SpreadBet(int t, Game g, boolean h, String points, int l, double w) {
		super(t,g,l,w);
		onHome = h;
		ptLine = new PointLine(points);
		ptDiff = 0;
	}

	@Override
	public int getResult() {
		int goodPts, badPts;
		if(onHome) {
			goodPts = homeScore;
			badPts = awayScore;
		}
		else {
			goodPts = awayScore;
			badPts = homeScore;
		}
		ptDiff = goodPts - badPts;
		if(ptLine.fav) { goodPts = goodPts - ptLine.pts; }
		else { goodPts = goodPts + ptLine.pts; }
		if(goodPts>badPts) { return WIN; }
		else if(goodPts<badPts) { return LOSS; }
		else if(ptLine.half) {
			if(ptLine.fav){ return LOSS; }
			return WIN;
		}
		return PUSH;
	}
	
	@Override
	public int[] getDiff() {
		int[] diff = new int[2];
		getResult();
		if(ptLine.fav) { diff[0] = ptDiff - ptLine.pts; }
		else { diff[0] = ptDiff + ptLine.pts; }
		if(ptLine.half) {
			if(ptLine.fav) { diff[1] = -1; }
			else { diff[1] = 1; }
		}
		else { diff[1] = 0; }
		return diff;
	}

	@Override
	public String toString() {
		String str = cleanCash(wager) + " (" + juice + ") on ";
		if(onHome) { str = str + game.home; }
		else { str = str + game.away; }
		str = str + " to COVER ";
		if(ptLine.fav) { str = str + "-"; }
		else { str = str + "+"; }
		str = str + ptLine.pts;
		if(ptLine.half){ str = str + ".5"; }
		if(gameStatus==Bet.NOT_STARTED) {
			if(time!=null) { return str + ": "+time; }
			return str + ": [Not Started]"; }
		str = str + ": " + game.away + " " + awayScore + ", " + game.home + " " + homeScore;
		if((gameStatus!=Bet.DONE)&&(time!=null)) { str = str + ", " + time; }
		return str;
	}

}
