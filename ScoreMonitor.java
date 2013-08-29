public class ScoreMonitor implements Runnable {

	BetTrackerCanvas canvas;
	public boolean stop;
	
	public ScoreMonitor(BetTrackerCanvas btc) {
		canvas = btc;
		stop = false;
	}
	
	public void run() {
		while(!stop) {
			canvas.ticket.findScores();
			System.out.println("score-search completed.");
			//canvas.redraw();
			try { Thread.sleep(10000); }
	    		catch(Exception e) { System.out.println(e.toString()); }
			canvas.scraperCheckedIn = true;
		}
	}
	
}
			