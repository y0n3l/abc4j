package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Dimension;
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
import abc.notation.StaffEndOfLine;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tune.Score;

public class JScoreComponent extends JComponent {
	
	private Tune tune = null; 
	
	private int staffLinesOffset = -1;
	
	private int componentWidth = 0; 
	
	private int XOffset = 0;
	
	public void paint(Graphics g){
		if (tune!=null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.black);
			ScoreRenditionContext context = new ScoreRenditionContext(g2);
			Score score = tune.getScore();
			staffLinesOffset = (int)(context.getStaffCharBounds().getHeight()*2.5);
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
			KeySignature currentKey = null;
			for (int i=0; i<score.size(); i++) {
				ScoreElementInterface s = (ScoreElementInterface)score.elementAt(i);
				if (
						(!(s instanceof Note)  
						|| (s instanceof Note && ((Note)s).isRest()) 
						|| (s instanceof Note && ((Note)s).getStrictDuration()>=Note.QUARTER) )
						&& lessThanQuarter.size()!=0)
					{
					renderNotesInGroup(lessThanQuarter, context, lessThanQuarterStart, cursor);
					lessThanQuarter.clear();
					durationInGroup = 0;
				}
				if (s instanceof KeySignature) {
					currentKey = (KeySignature)s;
					SKeySignature sk = new SKeySignature(currentKey, cursor, context);
					width = sk.render(context, cursor);
					int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
					cursor.setLocation(cursorNewLocationX, cursor.getY());
				}
				else
					if (s instanceof TimeSignature) {
						width = TimeSignatureRenderer.render(context, (Point2D)cursor.clone(), (TimeSignature)s);
						int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
						cursor.setLocation(cursorNewLocationX, cursor.getY());
						//short defNoteLength = ((TimeSignature)s).getDefaultNoteLength();
						//int nbOfDefaultNotesPerBeat = ((TimeSignature)s).getNumberOfDefaultNotesPerBeat(defNoteLength);
						//System.out.println("default note length" + defNoteLength);
						//System.out.println("nb of def not / beat " + nbOfDefaultNotesPerBeat);
						if(s.equals(TimeSignature.SIGNATURE_4_4))
							maxDurationInGroup = 2*Note.QUARTER;
						else
							if(((TimeSignature)s).getDenominator()==8)
								maxDurationInGroup = 3*Note.EIGHTH;
							else
								if(((TimeSignature)s).getDenominator()==4)
									maxDurationInGroup = Note.QUARTER;
							/*else
								if(s.equals(TimeSignature.SIGNATURE_3_4))
									maxDurationInGroup = Note.QUARTER;*/
					}
					else
						if (s instanceof Note) {
							Note note = ((Note)s);
							short strictDur = note.getStrictDuration();
							if (strictDur<Note.QUARTER && !note.isRest()) {
								if (lessThanQuarter.size()==0) {
									lessThanQuarterStart = (Point2D)cursor.clone();
								}
								durationInGroup+=((Note)s).getDuration();
								//System.out.println("duration in group " + durationInGroup);
								lessThanQuarter.add(s);
								if (durationInGroup>=maxDurationInGroup) {
									renderNotesInGroup(lessThanQuarter, context, lessThanQuarterStart, cursor);
									lessThanQuarter.clear();
									durationInGroup = 0;
									/*Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
									width = GroupOfNotesRenderer.render(context, lessThanQuarterStart, notes);
									lessThanQuarter.clear();
									durationInGroup = 0;
									//System.out.println("cursor : " + cursor);
									int cursorNewLocationX = (int)(cursor.getX() + width);
									cursor.setLocation(cursorNewLocationX, cursor.getY());
									cursorNewLocationX = (int)(cursor.getX() + context.getNotesSpacing()/2);
									cursor.setLocation(cursorNewLocationX, cursor.getY());*/
								}
							}
							else {
								/*if (lessThanQuarter.size()!=0) {
									renderNotesInGroup(lessThanQuarter, context, lessThanQuarterStart, cursor);
									lessThanQuarter.clear();
									durationInGroup = 0;
								}*/
								SNote noteR = new SNote((Note)s, cursor, context);
								width = noteR.render(context, cursor);//SingleNoteRenderer.render(context, (Point2D)cursor.clone(), (Note)s);
								int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
								cursor.setLocation(cursorNewLocationX, cursor.getY());
							}
						}
						else
							if (s instanceof BarLine) {
								int cursorNewLocationX = (int)(cursor.getX() + context.getNotesSpacing()/2);
								cursor.setLocation(cursorNewLocationX, cursor.getY());
								width = BarRenderer.render(context, (Point2D)cursor.clone(), (BarLine)s);
								//int cursorNewLocationX = (int)(cursor.getX() + width );
								//cursor.setLocation(cursorNewLocationX, cursor.getY());
								cursorNewLocationX = (int)(cursor.getX() + context.getNotesSpacing()/2);
								cursor.setLocation(cursorNewLocationX, cursor.getY());
							}
							else
								if (s instanceof StaffEndOfLine) {
									renderStaffLines(cursor, context);
									if (cursor.getX()>componentWidth)
										componentWidth = (int)cursor.getX(); 
									cursor.setLocation(0, cursor.getY()+staffLinesOffset);
									initNewStaffLine(currentKey, cursor, context);
								}
				//System.out.println("cursor locaiton : " + cursor.getX());
			}
			if (lessThanQuarter.size()!=0) {
				/*Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
				width = GroupOfNotesRenderer.render(context, lessThanQuarterStart, notes);
				lessThanQuarter.clear();
				//System.out.println("cursor : " + cursor);
				int cursorNewLocationX = (int)(cursor.getX() + width);
				cursor.setLocation(cursorNewLocationX, cursor.getY());*/
				renderNotesInGroup(lessThanQuarter, context, lessThanQuarterStart, cursor);
				lessThanQuarter.clear();
				durationInGroup = 0;
			}
			//System.out.println("char staff woidth : " + context.getStaffCharWidth());
			renderStaffLines(cursor, context);
			/*int staffCharNb = (int)((cursor.getX()-XOffset)/context.getStaffCharWidth());
			//System.out.println("char staff nb : " + staffCharNb);
			char[] staffS = new char[staffCharNb];
			for (int i=0; i<staffCharNb; i++)
				staffS[i] = ScoreRenditionContext.STAFF_SIX_LINES;
			context.getGraphics().drawChars(staffS, 0, staffS.length, XOffset, staffLinesOffset);*/
			setPreferredSize(new Dimension(componentWidth, (int)cursor.getY()));
		}
	}
	
	private void renderNotesInGroup(ArrayList lessThanQuarter, ScoreRenditionContext context, Point2D start, Point2D cursor){
		Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
		int width = (int)GroupOfNotesRenderer.render(context, start, notes);
		//System.out.println("width of group is " + width);
		int cursorNewLocationX = (int)(cursor.getX() + width);
		cursor.setLocation(cursorNewLocationX, cursor.getY());
		cursorNewLocationX = (int)(cursor.getX() + context.getNotesSpacing()/2);
		cursor.setLocation(cursorNewLocationX, cursor.getY());
	}
	
	private void renderStaffLines(Point2D cursor, ScoreRenditionContext context){
		int staffCharNb = (int)((cursor.getX()-XOffset)/context.getStaffCharWidth());
		//System.out.println("char staff nb : " + staffCharNb);
		char[] staffS = new char[staffCharNb+1];
		for (int i=0; i<staffCharNb+1; i++)
			staffS[i] = ScoreRenditionContext.STAFF_SIX_LINES;
		context.getGraphics().drawChars(staffS, 0, staffS.length, XOffset, (int)cursor.getY());
	}
	
	private void initNewStaffLine(KeySignature key, Point2D cursor, ScoreRenditionContext context){
		int width = (int)ClefRenderer.render(context, cursor);
		cursor.setLocation(cursor.getX()+width, cursor.getY());
	}
	
	public void setTune(Tune theTune){
		tune = theTune;
		repaint();
	}
}
