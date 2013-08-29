import java.io.Serializable;

class Game implements Serializable {

	public String id, home, away;
	
	public Game(String eID, String h, String a) {
		id = eID;
		home = h;
		away = a;
	}
	
	public String toString() { return away+" @ "+home; }
	
}