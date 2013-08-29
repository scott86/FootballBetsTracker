import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class EditTicket extends JFrame implements ActionListener {

	private BetTrackerCanvas btc;
	private BetTrackerGUI btg;
	private int week;
	private JButton remove, done;
	private JComboBox bets, games;
	private JRadioButton ncfSel, nflSel, moneySel, spreadSel, totalSel, side1, side2;
	private JTextField juice, line, wager;
	private boolean resetting;
	private Game customGame;
	
	public EditTicket(BetTrackerGUI bt, BetTrackerCanvas b, int w) {
		btc = b;
		btg = bt;
		week = w;
		resetting = false;
		customGame = null;
				
		setTitle("Week "+(week+1));
		setLocation(200,200);
		setSize(400,400);
		setLayout(new GridLayout(10,1,5,5));
		
		//removal panel
		JPanel removal = new JPanel();
		removal.setLayout(new FlowLayout());
		bets = new JComboBox();
		setBets();
		removal.add(bets);
		remove = new JButton("Remove");
		remove.setActionCommand("remove");
		remove.addActionListener(this);
		removal.add(remove);
		getContentPane().add(removal);
		
		getContentPane().add(new JPanel());
		
		JPanel ncfnfl = new JPanel();
		ncfnfl.setLayout(new GridLayout(1,2));
    	ncfSel = new JRadioButton("NCF games");
    	ncfSel.setActionCommand("ncf");
    	ncfSel.setSelected(true);
    	nflSel = new JRadioButton("NFL games");
    	nflSel.setActionCommand("nfl");
    	nflSel.setSelected(false);
    	ButtonGroup group = new ButtonGroup();
    	group.add(ncfSel);
    	group.add(nflSel);
		ncfSel.addActionListener(this);
		nflSel.addActionListener(this);
		ncfnfl.add(ncfSel);
		ncfnfl.add(nflSel);
		getContentPane().add(ncfnfl);
		try { if(!btc.ticket.ncfGamesFound) { btc.ticket.getNcfGames(); } }
		catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		try { if(!btc.ticket.nflGamesFound) { btc.ticket.getNflGames(); } }
		catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		games = new JComboBox();
		games.setActionCommand("games");
		games.addActionListener(this);
		getContentPane().add(games);
		
		//bet construction
		JPanel type = new JPanel();
		type.setLayout(new GridLayout(1,3));		
		moneySel = new JRadioButton("Money");
    	moneySel.setActionCommand("money");
    	moneySel.setSelected(false);
    	spreadSel = new JRadioButton("Spread");
    	spreadSel.setActionCommand("spread");
    	spreadSel.setSelected(true);
  		totalSel = new JRadioButton("Total");
		totalSel.setActionCommand("total");
		totalSel.setSelected(false);  	
		ButtonGroup group2 = new ButtonGroup();
		group2.add(moneySel);
    	group2.add(spreadSel);
		group2.add(totalSel);
		moneySel.addActionListener(this);
		spreadSel.addActionListener(this);
		totalSel.addActionListener(this);
		type.add(moneySel);
		type.add(spreadSel);
		type.add(totalSel);
		getContentPane().add(type);
		
		JPanel betInfo1 = new JPanel();
		betInfo1.setLayout(new GridLayout(1,4));
		side1 = new JRadioButton();
		side1.setActionCommand("side1");
		side1.setSelected(true);
		side2 = new JRadioButton();
		side2.setActionCommand("side2");
		side2.setSelected(true);
		ButtonGroup group3 = new ButtonGroup();
		group3.add(side1);
		group3.add(side2);
		betInfo1.add(side1);
		betInfo1.add(new JLabel());
		betInfo1.add(new JLabel("Line:"));
		line = new JTextField("-4.5");
		betInfo1.add(line);
		getContentPane().add(betInfo1);
		
		JPanel betInfo2 = new JPanel();
		betInfo2.setLayout(new GridLayout(1,4));
		betInfo2.add(side2);
		betInfo2.add(new JLabel());
		juice = new JTextField("-105");
		betInfo2.add(new JLabel("Juice:"));
		betInfo2.add(juice);
		getContentPane().add(betInfo2);
		
		JPanel betInfo3 = new JPanel();
		betInfo3.setLayout(new GridLayout(1,4));
		betInfo3.add(new JLabel("Wager:"));
		wager = new JTextField("10.0");
		betInfo3.add(wager);
		betInfo3.add(new JLabel());
		JButton add = new JButton("Add bet");
		add.setActionCommand("add");
		add.addActionListener(this);
		betInfo3.add(add);
		getContentPane().add(betInfo3);
		
		getContentPane().add(new JPanel());
		
		JPanel lastOne = new JPanel();
		lastOne.setLayout(new GridLayout(1,3));
		//lastOne.add(new JPanel());
		JButton update = new JButton("update");
		update.addActionListener(this);
		update.setActionCommand("update");
		lastOne.add(update);
		done = new JButton("Exit");
		done.addActionListener(this);
		done.setActionCommand("done");
		lastOne.add(done);
		lastOne.add(new JPanel());
		getContentPane().add(lastOne);
		
		setNCF();
		setGame();
	}
	
	public void setNCF() {
		resetting = true;
		games.removeAllItems();
		for( int i=0; i<btc.ticket.ncfGames.size(); i++ ) {
			games.addItem(btc.ticket.ncfGames.get(i).toString());
		}
		games.addItem("new game");
		if(games.getItemCount()>0) {
			games.setSelectedIndex(0);
			setGame();		
		}
		resetting = false;
	}
	
	public void setNFL() {
		resetting = true;
		games.removeAllItems();
		for( int i=0; i<btc.ticket.nflGames.size(); i++ ) {
			games.addItem(btc.ticket.nflGames.get(i).toString());
		}
		games.addItem("new game");
		if(games.getItemCount()>0) {
			games.setSelectedIndex(0);
			setGame();
		}
		resetting = false;
	}

	public void updateGames() {
		try { btc.ticket.getNflGames(true); }
		catch(Exception excpt) {}
		try { btc.ticket.getNcfGames(true); }
		catch(Exception excpt) {}
	}
	
	public void setBets() {
		bets.removeAllItems();
		for( int i=0; i<btc.ticket.bets.size(); i++ ) {
			bets.addItem(btc.ticket.bets.get(i).toString());
		}
		if(bets.getItemCount()>0) { bets.setSelectedIndex(bets.getItemCount()-1); }
	}
	
	public void setGame() {
		if(totalSel.isSelected()) { return; }
		System.out.println("setGame: "+games.getSelectedIndex());
		customGame = null;
		Game game = getGame();
		side1.setText(game.away);
		side2.setText(game.home);
	}
	
	public Game getGame() {
		System.out.println("getGame: "+games.getSelectedIndex());
		if(games.getSelectedItem().equals("new game")) {
			if(customGame != null) { return customGame; }
			String eid = JOptionPane.showInputDialog("espn ID:");
			String h = JOptionPane.showInputDialog("home team:");
			String a = JOptionPane.showInputDialog("away team:");
			customGame = new Game(eid,h,a);
			return customGame;
		}
		if(ncfSel.isSelected()) {
			return btc.ticket.ncfGames.get(games.getSelectedIndex());
		}
		return btc.ticket.nflGames.get(games.getSelectedIndex());
	}
	
	public int getJuice() {
		String jStr = juice.getText();
		int j = Integer.parseInt(jStr.substring(1,jStr.length()));
		if(jStr.charAt(0)=='-') { return -j; }
		return j;
	}
	
	public void addBet() {
		int type;
		if(ncfSel.isSelected()) { type = Bet.NCF; }
		else { type = Bet.NFL; }
		Game game = getGame();
		if(moneySel.isSelected()) {
			btc.ticket.addBet(new MoneyBet(type,game,side2.isSelected(),getJuice(),Double.parseDouble(wager.getText())));
		}
		else if(spreadSel.isSelected()) {
			btc.ticket.addBet(new SpreadBet(type,game,side2.isSelected(),line.getText(),getJuice(),Double.parseDouble(wager.getText())));
		}
		else {
			btc.ticket.addBet(new TotalBet(type,game,side1.isSelected(),line.getText(),getJuice(),Double.parseDouble(wager.getText())));
		}
		setBets();
	}
			
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("done")) {
			dispose();
			btc.redraw();
		}
		else if(e.getActionCommand().equals("ncf")) { setNCF(); }
		else if(e.getActionCommand().equals("nfl")) { setNFL(); }
		else if(e.getActionCommand().equals("remove")) {
			btc.ticket.removeBet(bets.getSelectedIndex());
			btg.setWeek(week);
			setBets();
		}
		else if(e.getActionCommand().equals("money")) {
			line.setEnabled(false);
			side1.setText(getGame().away);
			side2.setText(getGame().home);
		}
		else if(e.getActionCommand().equals("spread")) {
			line.setEnabled(true);
			side1.setText(getGame().away);
			side2.setText(getGame().home);
		}
		else if(e.getActionCommand().equals("total")) {
			line.setEnabled(true);
			side1.setText("Over");
			side2.setText("Under");
		}
		else if(e.getActionCommand().equals("games")) {
			if(resetting) { return; }
			System.out.println("AP: "+games.getSelectedIndex());
			setGame();
		}
		else if(e.getActionCommand().equals("add")) {
			addBet();
			btg.setWeek(week);
		}
		else if(e.getActionCommand().equals("update")) { updateGames(); }
	}
	
}
		