/**
 * 
 */
package abc.ui.scoretemplates;

/**
 * @author Administrateur
 *
 */
public abstract class HorizontalAlign {
	
	public static final byte LEFT = 1;
	public static final byte CENTER = 2;
	public static final byte RIGHT = 3;
	public static final byte STAFF_LEFT = 4;
	public static final byte STAFF_RIGHT = 5;
	public static final byte LEFT_TAB = 6;

	public static String toString(byte h) {
		switch (h) {
		case LEFT: return "left";
		case CENTER: return "center";
		case RIGHT: return "right";
		case STAFF_LEFT: return "staff left";
		case STAFF_RIGHT: return "staff right";
		case LEFT_TAB: return "left tab";
		default: return "<unknown horizontal align>";
		}
	}
	
	public static byte toHorizontalAlign(String s) {
		s = s.trim().toLowerCase();
		if (s.equals("left")) return LEFT;
		else if (s.equals("center")) return CENTER;
		else if (s.equals("right")) return RIGHT;
		else if (s.equals("staff left")) return STAFF_LEFT;
		else if (s.equals("staff right")) return STAFF_RIGHT;
		else if (s.equals("left tab")) return LEFT_TAB;
		else throw new RuntimeException("unknown horizontal align: "+s);
	}

}
