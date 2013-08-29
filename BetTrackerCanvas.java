import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.net.URL;

public class BetTrackerCanvas extends Canvas implements MouseListener {
	
	public static final int BET_HEIGHT = 20;
	public static final int MAX_PTS = 50;
	
	public Ticket ticket;
	public boolean loading;
	public ArrayList<Bet> finished, active, pending, sorted;
	private int diffTotal, losers, winners;
	public ScoreMonitor scoreChecker;

	public boolean scraperCheckedIn;
	
	//for playing sounds
	AudioInputStream stream;      
	AudioFormat format;      
	DataLine.Info info;      
	Clip clip;
	
	public BetTrackerCanvas(Ticket t) {
		ticket = t;
		setBackground(Color.lightGray);
		loading = false;
		finished = new ArrayList<Bet>();
		active = new ArrayList<Bet>();
		pending = new ArrayList<Bet>();
		sorted = new ArrayList<Bet>();
		diffTotal = 0;
		losers = 0;
		winners = 0;
		scraperCheckedIn = false;
	}
	
	public void refreshScoreChecker() {
		//scoreChecker.destroy();
		scoreChecker.stop = true;
		scoreChecker = new ScoreMonitor(this);
		(new Thread(scoreChecker)).start();
	}
	
	public void mouseReleased( MouseEvent e ) {
		System.out.println("you clicked it!");
		redraw();
	}
	
	public void mouseExited( MouseEvent e ) {
    ;
  	}	
	public void mouseEntered( MouseEvent e ) {
    ;
  	}
	public void mousePressed( MouseEvent e ) {
	 ;
	}
	public void mouseClicked( MouseEvent e ) {
	 ;
	}
	
	public void loading() {
		loading = !loading;
		if(loading) {
			getGraphics().clearRect(0,0,this.getBounds().width,this.getBounds().height);
			getGraphics().drawString("Loading week...",50,50);
		}
	}
	
	public void redraw(Ticket t) {
		ticket = t;
		redraw();
	}
	
	public void redraw() {
		if(loading) {
			getGraphics().clearRect(0,0,this.getBounds().width,this.getBounds().height);
			getGraphics().drawString("Loading week...",50,50);
			return;
		}
		Graphics g = getGraphics();
		g.clearRect(0, 0, this.getBounds().width, this.getBounds().height);
		
		finished.clear();
		active.clear();
		pending.clear();
		boolean newLoss = false;
		boolean newWin = false;
		for( int i=0; i<ticket.bets.size(); i++ ) {
			Bet b = ticket.bets.get(i);
			if(b.gameStatus==Bet.DONE) {
				finished.add(b);
				//if(!b.doneAck) {
				//	b.doneAck = true;
				//	if(b.result==Bet.LOSS) { newLoss = true; }
				//	else if(b.result==Bet.WIN) { newWin = true; }
				//}
			}
			else if(ticket.bets.get(i).gameStatus==Bet.ACTIVE) { active.add(ticket.bets.get(i)); }
			else { pending.add(ticket.bets.get(i)); }
		}
		sorted.clear();
		sorted.addAll(finished);
		sorted.addAll(active);
		sorted.addAll(pending);
		int[] stringWidths = new int[sorted.size()];
		int maxWidth = 0;
		for( int i=0; i<sorted.size(); i++ ) {
			stringWidths[i] = g.getFontMetrics().stringWidth(sorted.get(i).toString());
			if(stringWidths[i]>maxWidth) { maxWidth = stringWidths[i]; }
		}
		int leftBar = ((this.getBounds().width-maxWidth)/2)-1;
		int rightBar = ((this.getBounds().width+maxWidth)/2)+1;
		int lastDiffTotal = diffTotal;
		diffTotal = 0;
		int lastWinnerTotal = winners;
		winners = 0;
		int lastLoserTotal = losers;
		losers = 0;
		for( int i=0; i<sorted.size(); i++ ) {
			Bet bet = sorted.get(i);
			g.setColor(Color.black);
			g.drawString(bet.toString(), ((this.getBounds().width-stringWidths[i])/2), 20+(i*BET_HEIGHT));
			if(bet.gameStatus!=Bet.NOT_STARTED) {
				if(bet.gameStatus==Bet.DONE) {
					if(bet.result==Bet.WIN) { winners++; }
					else if(bet.result==Bet.LOSS) { losers++; }
				}
				int meter = 0;
				int[] diff = bet.getDiff();
				//System.out.println(diff[0]+", "+diff[1]);
				meter = (diff[0]*2) + diff[1];
				diffTotal = diffTotal + meter;
				if(meter < 0) { g.setColor(Color.red); }
				else if(meter > 0) { g.setColor(Color.green); }
				meter = (int)((((double)meter)/((double)(MAX_PTS*2)))*((double)leftBar));
				if((diff[0]<0)||((diff[0]==0)&&(diff[1]<0))) {
					//g.setColor(Color.red);
					g.fillRect(leftBar+meter, 10+(i*BET_HEIGHT), -meter, 10);
					String diffStr = ""+(0-diff[0]);
					if(diff[1]<0) { diffStr = diffStr + ".5"; }
					else if(diff[1]>0) { diffStr = (diff[0]+1) + ".5"; }
					int diffWidth = g.getFontMetrics().stringWidth(diffStr);
					g.drawString(diffStr,leftBar+meter-diffWidth-1,20+(i*BET_HEIGHT));
				}
				else if((diff[0]>0)||((diff[0]==0)&&(diff[1]>0))) {
					//g.setColor(Color.green);
					g.fillRect(rightBar+1, 10+(i*BET_HEIGHT), meter+1, 10);
					String diffStr = ""+diff[0];
					if(diff[1]<0) { diffStr = (diff[0]-1) + ".5"; }
					else if(diff[1]>0) { diffStr = diffStr + ".5"; }
					int diffWidth = g.getFontMetrics().stringWidth(diffStr);
					g.drawString(diffStr,rightBar+meter+3,20+(i*BET_HEIGHT));
				}
				if(bet.gameStatus==Bet.DONE) { g.drawRect(((this.getBounds().width-stringWidths[i])/2)-1, 9+(i*BET_HEIGHT), stringWidths[i]+2, 12); }
			}
		}
		g.setColor(Color.black);
		g.drawLine(leftBar, 10, leftBar, 3+(ticket.bets.size()*BET_HEIGHT));
		g.drawLine(rightBar, 10, rightBar, 3+(ticket.bets.size()*BET_HEIGHT));
		double zeroSum = ticket.ttlWagered / (ticket.ttlWagered+ticket.maxWin);
		System.out.println(ticket.ttlWagered+", "+ticket.maxWin);
		int midAt = 10+((int)(((double)(this.getBounds().width-20))*zeroSum));
		double net = (ticket.payout(false)-ticket.ttlWagered) / (ticket.ttlWagered+ticket.maxWin);
		int meter = (int)(((double)(this.getBounds().width-20))*net);
		//System.out.println(net+", "+meter);
		String currNet = Bet.cleanCash(ticket.payout(false)-ticket.ttlWagered);
		if(meter<0) {
			g.setColor(Color.red);
			g.fillRect(midAt+meter, this.getBounds().height-40, -meter, 15);
		}
		else if(meter>0) {
			g.setColor(Color.green);
			g.fillRect(midAt, this.getBounds().height-40, meter, 15);
		}
		g.drawString(currNet, (midAt+meter)-(g.getFontMetrics().stringWidth(currNet)/2), this.getBounds().height-47);
		g.setColor(Color.black);
		g.drawLine(midAt, this.getBounds().height-47, midAt, this.getBounds().height-18);
		g.drawRect(10, this.getBounds().height-45, this.getBounds().width-20, 25);
		g.drawString("-"+Bet.cleanCash(ticket.ttlWagered),0,this.getBounds().height-1);
		g.drawString("+"+Bet.cleanCash(ticket.maxWin),this.getBounds().width-g.getFontMetrics().stringWidth("+"+Bet.cleanCash(ticket.maxWin)),this.getBounds().height-1);
		
		// berman / mega shark / hank / sheen
		int wl = (winners-lastWinnerTotal) - (losers-lastLoserTotal);
		if(wl>0) { newWin = true; }
		else if(wl<0) { newLoss = true; }
		if ( (diffTotal != lastDiffTotal) || newWin || newLoss ) {
			URL url=null;
				try{
					if(newLoss) { url = getClass().getClassLoader().getResource("hank.wav"); }
					else if(newWin) { url = getClass().getClassLoader().getResource("winning.wav"); }
					else if(diffTotal < lastDiffTotal) { url = getClass().getClassLoader().getResource("goddamnit.wav"); }
					else { url = getClass().getClassLoader().getResource("holy_shit.wav"); }
					stream = AudioSystem.getAudioInputStream(url);      
					format = stream.getFormat();      
					info = new DataLine.Info(Clip.class, stream.getFormat());      
					clip = (Clip) AudioSystem.getLine(info);         
		 			clip.open(stream);      
		 			clip.start();
				} catch (Exception ex)
				{      
	 				ex.printStackTrace();    
				} 
		}
	}

}
