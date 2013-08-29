
public class TotalBet extends Bet {

	public boolean onOver;
	public PointLine totLine;
	
	public TotalBet(int t, Game g, boolean over, String points, int l, double w) {
		super(t,g,l,w);
		onOver = over;
		totLine = new PointLine(points);
	}
	
	@Override
	public int getResult() {
		int status = 0; //0=under,1=over
		if((homeScore+awayScore)>totLine.pts) { status = 1; }
		else if((homeScore+awayScore)==totLine.pts) {
			if(!totLine.half) { return PUSH; }
		}
		if(onOver) {
			if(status==0) { return LOSS; }
			return WIN;
		}
		else {
			if(status==0) { return WIN; }
			return LOSS;
		}
	}

	@Override
	public String toString() {
		String str = cleanCash(wager) + " (" + juice + "( on " + game.away + "-" + game.home;
		if(onOver) { str = str + " OVER "; }
		else { str = str + " UNDER "; }
		str = str + totLine.pts;
		if(totLine.half){ str = str + ".5"; }
		if(gameStatus==Bet.NOT_STARTED) {
			if(time!=null) { return str + ": "+time; }
			return str + ": [Not Started]"; }
		str = str + ": " + game.away + " " + awayScore + ", " + game.home + " " + homeScore;
		if((gameStatus!=Bet.DONE)&&(time!=null)) { str = str + ", " + time; }
		return str;
	}

	@Override
	public int[] getDiff() {
		int[] diff = new int[2];
		diff[1] = 0;
		if(onOver) {
			diff[0] = (homeScore+awayScore) - totLine.pts;
			if(totLine.half) { diff[1] = -1; }
		}
		else {
			diff[0] = totLine.pts - (homeScore+awayScore);
			if(totLine.half) { diff[1] = 1; }
		}
		return diff;
	}

}
