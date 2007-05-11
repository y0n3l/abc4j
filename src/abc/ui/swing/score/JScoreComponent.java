package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import abc.notation.BarLine;
import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.ScoreElementInterface;
import abc.notation.TimeSignature;
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
			double width = 0;
			width = ClefRenderer.render(context, (Point2D)cursor.clone());
			cursor.setLocation(cursor.getX()+width, cursor.getY());
			for (int i=0; i<score.size(); i++) {
				width = 0;
				ScoreElementInterface s = (ScoreElementInterface)score.elementAt(i);
				if (s instanceof KeySignature)
					width = KeySignatureRenderer.render(context, (Point2D)cursor.clone(), (KeySignature)s);
				else
					if (s instanceof TimeSignature)
						width = TimeSignatureRenderer.render(context, (Point2D)cursor.clone(), (TimeSignature)s);
					else
						if (s instanceof Note)
							width = SingleNoteRenderer.render(context, (Point2D)cursor.clone(), (Note)s);
						else
							if (s instanceof BarLine)
								width = BarRenderer.render(context, (Point2D)cursor.clone(), (BarLine)s);
				cursor.setLocation(cursor.getX()+width+context.getNoteWidth(), cursor.getY());
				//System.out.println("cursor locaiton : " + cursor.getX());
			}
		}
	}
	
	public void setTune(Tune theTune){
		tune = theTune;
	}
}
