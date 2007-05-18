package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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
	
	private int XOffset = 0;
	
	public void paint(Graphics g){
		if (tune!=null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.black);
			ScoreRenditionContext context = new ScoreRenditionContext(g2);
			Score score = tune.getScore();
			Point2D cursor = new Point(XOffset, staffLinesOffset);
			g2.setColor(Color.RED);
			g2.draw(new Line2D.Float(0, staffLinesOffset, 1500, staffLinesOffset));
			g2.setColor(Color.BLACK);
			g2.setFont(context.getFont());
			double width = 0;
			width = ClefRenderer.render(context, (Point2D)cursor.clone());
			cursor.setLocation(cursor.getX()+width, cursor.getY());
			ArrayList lessThanQuarter = new ArrayList();
			Point2D lessThanQuarterStart = null;
			int durationInGroup = 0;
			int maxDurationInGroup = 0;
			for (int i=0; i<score.size(); i++) {
				//width = 0;
				ScoreElementInterface s = (ScoreElementInterface)score.elementAt(i);
				if (s instanceof KeySignature) {
					width = KeySignatureRenderer.render(context, (Point2D)cursor.clone(), (KeySignature)s);
					int cursorNewLocationX = (int)(cursor.getX() + width + SingleNoteRenderer.getOffsetAfterNoteRendition(context));
					cursor.setLocation(cursorNewLocationX, cursor.getY());
				}
				else
					if (s instanceof TimeSignature) {
						width = TimeSignatureRenderer.render(context, (Point2D)cursor.clone(), (TimeSignature)s);
						int cursorNewLocationX = (int)(cursor.getX() + width + SingleNoteRenderer.getOffsetAfterNoteRendition(context));
						cursor.setLocation(cursorNewLocationX, cursor.getY());
						short defNoteLength = ((TimeSignature)s).getDefaultNoteLength();
						int nbOfDefaultNotesPerBeat = ((TimeSignature)s).getNumberOfDefaultNotesPerBeat(defNoteLength);
						System.out.println("default note length" + defNoteLength);
						System.out.println("nb of def not / beat " + nbOfDefaultNotesPerBeat);
						if(s.equals(TimeSignature.SIGNATURE_6_8))
							maxDurationInGroup = 3*Note.EIGHTH;
						else
							if(s.equals(TimeSignature.SIGNATURE_4_4))
								maxDurationInGroup = Note.QUARTER;
					}
					else
						if (s instanceof Note) {
							short strictDur = ((Note)s).getStrictDuration();
							if (strictDur<Note.QUARTER) {
								if (lessThanQuarter.size()==0) {
									//System.out.println("less than quarter cursor : " + cursor);
									lessThanQuarterStart = (Point2D)cursor.clone();
									context.getGraphics().setColor(Color.RED);
									context.getGraphics().drawLine((int)(lessThanQuarterStart.getX()),
											(int)lessThanQuarterStart.getY(), 
											(int)(lessThanQuarterStart.getX()),
											(int)(lessThanQuarterStart.getY()-100));
									context.getGraphics().setColor(Color.BLACK);
								}
								durationInGroup+=((Note)s).getDuration();
								System.out.println("duration in group " + durationInGroup);
								lessThanQuarter.add(s);
								if (durationInGroup>=maxDurationInGroup) {
									Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
									width = GroupOfNotesRenderer.render(context, lessThanQuarterStart, notes);
									lessThanQuarter.clear();
									durationInGroup = 0;
									//System.out.println("cursor : " + cursor);
									int cursorNewLocationX = (int)(cursor.getX() + width);
									cursor.setLocation(cursorNewLocationX, cursor.getY());
								}
							}
							else {
								/*if (lessThanQuarter.size()!=0) {
									Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
									width = GroupOfNotesRenderer.render(context, lessThanQuarterStart, notes);
									lessThanQuarter.clear();
									//System.out.println("cursor : " + cursor);
									int cursorNewLocationX = (int)(cursor.getX() + width);
									cursor.setLocation(cursorNewLocationX, cursor.getY());
								}*/
								System.out.println("Displaying single note");
								width = SingleNoteRenderer.render(context, (Point2D)cursor.clone(), (Note)s);
								int cursorNewLocationX = (int)(cursor.getX() + width + SingleNoteRenderer.getOffsetAfterNoteRendition(context));
								cursor.setLocation(cursorNewLocationX, cursor.getY());
							}
						}
						else
							if (s instanceof BarLine)
								width = BarRenderer.render(context, (Point2D)cursor.clone(), (BarLine)s);
				//System.out.println("cursor locaiton : " + cursor.getX());
			}
			if (lessThanQuarter.size()!=0) {
				Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
				width = GroupOfNotesRenderer.render(context, lessThanQuarterStart, notes);
				lessThanQuarter.clear();
				//System.out.println("cursor : " + cursor);
				int cursorNewLocationX = (int)(cursor.getX() + width);
				cursor.setLocation(cursorNewLocationX, cursor.getY());
			}
			//System.out.println("char staff woidth : " + context.getStaffCharWidth());
			int staffCharNb = (int)((cursor.getX()-XOffset)/context.getStaffCharWidth());
			//System.out.println("char staff nb : " + staffCharNb);
			char[] staffS = new char[staffCharNb];
			for (int i=0; i<staffCharNb; i++)
				staffS[i] = ScoreRenditionContext.STAFF_SIX_LINES;
			context.getGraphics().drawChars(staffS, 0, staffS.length, XOffset, staffLinesOffset);
		}
	}
	
	public void setTune(Tune theTune){
		tune = theTune;
	}
}
