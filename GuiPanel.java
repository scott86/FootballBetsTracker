import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.net.URL;

class GuiPanel extends JPanel implements ActionListener {

	ArrayList<Ticket> tickets;
	int week;
	private JButton prev, next, bets, save, gd, ks;
	private BetTrackerCanvas btc;
	private BetTrackerGUI btg;
	
	//for playing sounds
	AudioInputStream stream;      
	AudioFormat format;      
	DataLine.Info info;      
	Clip clip;
	
	public GuiPanel(ArrayList<Ticket> t, int w, BetTrackerCanvas b, BetTrackerGUI g) {
		tickets = t;
		week = w;
		btc = b;
		btg = g;
		
		setLayout(new GridLayout(1,6));
		prev = new JButton("prev. week");
		prev.addActionListener(this);
		prev.setActionCommand("p");
		add(prev);
		next = new JButton("next week");
		next.addActionListener(this);
		next.setActionCommand("n");
		bets = new JButton("bets...");
		bets.addActionListener(this);
		bets.setActionCommand("b");
		add(bets);
		save = new JButton("save");
		save.addActionListener(this);
		save.setActionCommand("s");
		add(save);
		gd = new JButton("goddamnit");
		gd.addActionListener(this);
		gd.setActionCommand("g");
		add(gd);
		ks = new JButton("kickstart");
		ks.addActionListener(this);
		ks.setActionCommand("k");
		add(ks);
		add(next);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="p") {
			week = week - 1;
			if(week==-1) { week = tickets.size() - 1; }
			btg.setWeek(week);
			btc.loading();
			tickets.get(week).findScores();
			btc.loading();
			btc.redraw(tickets.get(week));
		}
		else if(e.getActionCommand()=="n") {
			week = (week+1) % tickets.size();
			btg.setWeek(week);
			btc.loading();
			tickets.get(week).findScores();
			btc.loading();
			btc.redraw(tickets.get(week));
		}
		else if(e.getActionCommand()=="s") {
			for( int i=0; i<tickets.size(); i++ ) {
				BetTrackerGUI.saveTicket(tickets.get(i),i+1);
			}
		}
		else if(e.getActionCommand()=="b") {
			EditTicket et = new EditTicket(btg,btc,week);
			//System.out.println(btc.ticket.ncfGamesFound);
			//System.out.println(btc.ticket.nflGamesFound);
			et.setVisible(true);
		}
		else if(e.getActionCommand()=="g") {
			URL url=null;
				try{    
					url = getClass().getClassLoader().getResource("goddamnit.wav"); 
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
		else if(e.getActionCommand()=="k") { btc.refreshScoreChecker(); }
			
	}
	
}