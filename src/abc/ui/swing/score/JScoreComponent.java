package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.ScoreElementInterface;
import abc.notation.Tune;
import abc.notation.Tune.Score;

public class JScoreComponent extends JComponent {
	
	private Tune tune = null; 
	
	private int staffLinesOffset = 250;
	
	public void paint(Graphics g){
		if (tune!=null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.black);
			ScoreRenditionContext context = new ScoreRenditionContext(g2);
			Score score = tune.getScore();
			Point2D cursor = new Point(20, staffLinesOffset);
			g2.setColor(Color.RED);
			g2.draw(new Line2D.Float(0, staffLinesOffset, 1500, staffLinesOffset));
			g2.setColor(Color.BLACK);
			g2.setFont(context.getFont());
			for (int i=0; i<score.size(); i++) {
				ScoreElementInterface s = (ScoreElementInterface)score.elementAt(i);
				if (s instanceof KeySignature)
					ClefRenderer.render(context, cursor, (KeySignature)s);
				else
					if (s instanceof Note)
						SingleNoteRenderer.render(context, cursor, (Note)s);
				cursor.setLocation(cursor.getX()+context.getNoteWidth()*2, cursor.getY());
			}
		}
	}
	
	public void setTune(Tune theTune){
		tune = theTune;
	}
}
