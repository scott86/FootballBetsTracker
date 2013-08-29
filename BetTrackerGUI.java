import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class BetTrackerGUI extends JFrame implements Serializable {

	public int WEEK;
	public static final int CURR_WEEK = 1;

	public ArrayList<Ticket> tickets;

	public BetTrackerGUI(ArrayList<Ticket> t) { this(t,1); }

	public BetTrackerGUI(ArrayList<Ticket> t, int wk) {
		tickets = t;
		WEEK = wk;
		setTitle("Ticket for week "+WEEK);
      setSize(600,145+(tickets.get(WEEK-1).bets.size()*BetTrackerCanvas.BET_HEIGHT));
      setLocation(100,100);
	}

	public void setWeek(int w) {
		setTitle("Ticket for week "+(w+1));
		setSize(getBounds().width,145+(tickets.get(w).bets.size()*BetTrackerCanvas.BET_HEIGHT));
	}

	public static void saveTicket(Ticket t, int wk) {
	  FileOutputStream fos = null;
	  ObjectOutputStream out = null;
	  try {
		  fos = new FileOutputStream("ticket"+wk+".ser");
		  out = new ObjectOutputStream(fos);
		  out.writeObject(t);
		  out.close();
	  }
	  catch(IOException e) { e.printStackTrace(); }
  }

  public static Ticket loadTicket(int wk) throws Exception {
	  FileInputStream fis = null;
	  ObjectInputStream in = null;
	  Ticket uc = null;
	  try {
		  fis = new FileInputStream("ticket"+wk+".ser");
		  in = new ObjectInputStream(fis);
		  uc = (Ticket)in.readObject();
		  in.close();
	  }
	  catch(Exception e) {
	  	e.printStackTrace();
		throw new Exception();
	  }
	  return uc;
  }

  public static void replace(ArrayList<Ticket> ticks, int wk, String[] bad, String[] good) {
  		Game g;
		for( int i=0; i<ticks.get(wk).ncfGames.size(); i++ ) {
			g = ticks.get(wk).ncfGames.get(i);
			for( int j=0; j<bad.length; j++ ) {
				if(g.id.equals(bad[j])) {
					g.id = good[j];
				}
			}
		}
	}

	public static void main(String[] args) {

		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		for( int i=0; i<18; i++ ) {
			try { tickets.add(loadTicket(i+1)); }
			catch(Exception e) {
				/*if(i==0) {
					Ticket t = new Ticket(1);
					t.addBet(new SpreadBet(Bet.NCF,new Game("302470127","Mich St.","W Mich"),false,"+24",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302470130","Michigan","UCONN"),false,"+3",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302472306","K-State","UCLA"),true,"-1.5",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302470097","L'Ville","UK"),true,"+3",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302470197","OK State","Wash St."),false,"+17",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302470158","Nebraska","W Kent"),false,"+37.5",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302470238","Vandy","Northwestern"),false,"-3.5",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302482641","Texas Tech","SMU"),false,"+14",-115,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302490120","Maryland","Navy"),true,"+6",-105,10.0));
					t.addBet(new SpreadBet(Bet.NCF,new Game("302490259","VaTech","Boise St."),true,"+2",-105,10.0));
					tickets.add(t);
				}
				else if(i==1) {
					Ticket t2 = new Ticket(2);
					t2.addBet(new MoneyBet(Bet.NFL,new Game("300909018","Saints","Vikings"),true,-230,230.0));
					t2.addBet(new MoneyBet(Bet.NFL,new Game("300909018","Saints","Vikings"),true,-240,24.0));
					tickets.add(t2);
				}
				else { tickets.add(new Ticket(i+1)); }*/
				tickets.add(new Ticket(i+1));
			}
		}

		//tickets.get(2).addBet(new MoneyBet(Bet.NCF,new Game("302610142","Missouri","San Diego St"),true,-600,60.0));

		for( int i=0; i<18; i++ ) {
			try { if(!tickets.get(i).ncfGamesFound) { tickets.get(i).getNcfGames(); } }
			catch(Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
			try { if(!tickets.get(i).nflGamesFound) { tickets.get(i).getNflGames(); } }
			catch(Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
			//tickets.get(i).showGames();
		}

		//String[] bad = {"303232294","303232305","303232132","303230130","303230245","303230183"};
		//String[] good = {"303242294","303242305","303242132","303240130","303240245","303240183"};
		//replace(tickets,CURR_WEEK-1,bad,good);

		int wk = 0;
		try { wk = Integer.parseInt(args[0]) + 1; }
		catch(Exception e) {

			// now
			GregorianCalendar gc = new GregorianCalendar();

			// 1st dividing time
			GregorianCalendar dt = new GregorianCalendar(2013,8,4,1,0,0);

			// increment dividing time until it's after now
			wk = 1;
			while(dt.before(gc)) {
				dt.add(Calendar.DAY_OF_MONTH,7);
				wk++;
			}

			/*
			int d = gc.get(Calendar.DATE);
			int m = gc.get(Calendar.MONTH)+1;
			if(m==8) { wk = 1; } // aug.
			else if(m==9) { // sept.
				if(d<5) { wk = 1; }
				else if(d<12) { wk = 2; }
				else if(d<19) { wk = 3; }
				else if(d<26) { wk = 4; }
				else { wk = 5; }
			}
			else if(m==10) { // oct.
				if(d<3) { wk = 5; }
				else if(d<10) { wk = 6; }
				else if(d<17) { wk = 7; }
				else if(d<24) { wk = 8; }
				else if(d<31) { wk = 9; }
				else { wk = 10; }
			}
			else if(m==11) { // nov.
				if(d<7) { wk = 10; }
				else if(d<14) { wk = 11; }
				else if(d<21) { wk = 12; }
				else if(d<28) { wk = 13; }
				else { wk = 14; }
			}
			else if(m==12) { // dec.
				if(d<5) { wk = 14; }
				else if(d<12) { wk = 15; }
				else if(d<19) { wk = 16; }
				else if(d<26) { wk = 17; }
				else { wk = 18; }
			}
			else { // gotta be jan.
				if(d<2) { wk = 18; }
				else if(d<9) { wk = 19; } // wildcard rd?
				else if(d<16) { wk = 20; } // div. rd?
				else { wk = 21; } // conf. champs?
			}
			*/

		}

		 BetTrackerGUI myFrame = new BetTrackerGUI(tickets,wk);
	    myFrame.setLayout(new BorderLayout());
	    BetTrackerCanvas myCanvas = new BetTrackerCanvas(myFrame.tickets.get(myFrame.WEEK-1));
	    myFrame.add( myCanvas, BorderLayout.CENTER );
		 GuiPanel top = new GuiPanel(myFrame.tickets,myFrame.WEEK-1,myCanvas,myFrame);
		 myFrame.add(top,BorderLayout.NORTH);
	    myFrame.addWindowListener( new WindowAdapter() {
	      public void windowClosing( WindowEvent we ) {
	        System.exit( 0 );
	      }
	    } );

	/*
	try {
		Game g;
		for( int j=0; j<myFrame.tickets.get(2).ncfGames.size(); j++ ) {
			g = myFrame.tickets.get(2).ncfGames.get(j);
			if(g.id.equals("312602459")) {
				//String temp = g.home;
				//g.home = g.away;
				//g.away = temp;
				SpreadBet sb;
				for(int k=0; k<myFrame.tickets.get(2).bets.size(); k++) {
					try {
						sb = (SpreadBet)myFrame.tickets.get(2).bets.get(k);
						if(sb.game.equals(g)) { sb.onHome = !sb.onHome; }
					}
					catch(Exception ykt) {}
				}
			}
		}
	}
	catch(Exception excizzie) {}
	*/

	    myFrame.setVisible(true);
	    myCanvas.scoreChecker = new ScoreMonitor(myCanvas);
		(new Thread(myCanvas.scoreChecker)).start();
		(new Thread(new GuiUpdater(myCanvas))).start();

		/*
		String page = "http://espn.go.com/ncf/scoreboard?confId=80&seasonYear=2010&seasonType=2&weekNumber=1";
		String espnID = "302470036";
		String espnID2 = "302470153";
		String match = espnID+"-aTotal\">[0-9]+<";
		String match2 = espnID+"-hTotal\">[0-9]+<";
		String match3 = espnID2+"-aTotal\">[0-9]+<";
		String match4 = espnID2+"-hTotal\">[0-9]+<";
		try {
			ArrayList<String> found = Scraper.findAll(page, match);
			for( int i=0; i<found.size(); i++ ) { System.out.println(found.get(i)); }
			found = Scraper.findAll(match2);
			for( int i=0; i<found.size(); i++ ) { System.out.println(found.get(i)); }
			found = Scraper.findAll(match3);
			for( int i=0; i<found.size(); i++ ) { System.out.println(found.get(i)); }
			found = Scraper.findAll(match4);
			for( int i=0; i<found.size(); i++ ) { System.out.println(found.get(i)); }
		}
		catch(Exception e) { System.out.println(e.toString()); }
		*/
	}

}
