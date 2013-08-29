public class GuiUpdater implements Runnable {

	BetTrackerCanvas canvas;
	
	public GuiUpdater(BetTrackerCanvas btc) {
		canvas = btc;
	}
	
	public void run() {
		int scraperMIA = 0;
		while(true) {
			//canvas.ticket.findScores();
			canvas.redraw();
			try { Thread.sleep(10000); }
	    		catch(Exception e) { System.out.println(e.toString()); }
			if(canvas.scraperCheckedIn) {
				canvas.scraperCheckedIn = false;
				scraperMIA = 0;
			}
			else {
				scraperMIA++;
				if(scraperMIA>=15) {
					canvas.refreshScoreChecker();
					scraperMIA = 0;
				}
			}
		}
	}
	
}
		