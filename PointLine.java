import java.io.Serializable;

public class PointLine implements Serializable {
	
	public int pts;
	public boolean half, fav;
	
	public PointLine( String points ) {
		if(points.charAt(0)=='-') { fav = true; }
		else {
			fav = false;
			if(points.charAt(0)!='+') { points = "+"+points; }
		}
		points = points.substring(1,points.length());
		if(points.length()>1) {
			if( points.charAt(points.length()-2) == '.' ) {
				if( points.charAt(points.length()-1) == '5' ) { half = true; }
				try { pts = Integer.parseInt(points.substring(0,points.indexOf("."))); }
				catch(Exception e) { pts = 0; }
			}
			else {
				try { pts = Integer.parseInt(points); }
				catch(Exception e) { pts = 0; }
			}
		}
		else {
			try { pts = Integer.parseInt(points); }
			catch(Exception e) { pts = 0; }
		}
	}

}
