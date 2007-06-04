package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import abc.notation.BarLine;
import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.RepeatBarLine;
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
	
	//private int staffLineNb = 0;
	
	public void paint(Graphics g){
		if (tune!=null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.black);
			ScoreRenditionContext context = new ScoreRenditionContext(g2);
			Score score = tune.getScore();
			staffLinesOffset = (int)(context.getStaffCharBounds().getHeight()*2.5);
			Point2D cursor = new Point(XOffset, 0);
			g2.setFont(context.getFont());
			double width = 0;
			ArrayList lessThanQuarter = new ArrayList();
			Point2D lessThanQuarterStart = null;
			int durationInGroup = 0;
			int maxDurationInGroup = Note.QUARTER;
			int durationInCurrentMeasure = 0;
			KeySignature currentKey = tune.getKey();
			boolean currentStaffLineInitialized = false;
			int staffLineNb = 0;
			for (int i=0; i<score.size(); i++) {
				ScoreElementInterface s = (ScoreElementInterface)score.elementAt(i);
				if (
						//detects the end of a group.
						(!(s instanceof Note)  
						|| (s instanceof Note && ((Note)s).isRest()) 
						|| (s instanceof Note && ((Note)s).getStrictDuration()>=Note.QUARTER) )
						&& lessThanQuarter.size()!=0)
					{
					if (!currentStaffLineInitialized) {initNewStaffLine(currentKey, null, cursor, context);currentStaffLineInitialized = true;}
					renderNotesInGroup(lessThanQuarter, context, lessThanQuarterStart, cursor);
					lessThanQuarter.clear();
					durationInGroup = 0;
				}
				if (s instanceof KeySignature) {
					currentKey = (KeySignature)s;
					//SKeySignature sk = new SKeySignature(currentKey, cursor, context);
					//width = sk.render(context, cursor);
					//int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
					//cursor.setLocation(cursorNewLocationX, cursor.getY());
				}
				else
					if (s instanceof TimeSignature) {
						if (!currentStaffLineInitialized) {initNewStaffLine(currentKey, (TimeSignature)s, cursor, context);currentStaffLineInitialized = true;}
						//width = TimeSignatureRenderer.render(context, (Point2D)cursor.clone(), (TimeSignature)s);
						//int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
						//cursor.setLocation(cursorNewLocationX, cursor.getY());
						if(s.equals(TimeSignature.SIGNATURE_4_4)) maxDurationInGroup = Note.QUARTER;
						else if(((TimeSignature)s).getDenominator()==8) maxDurationInGroup = 3*Note.EIGHTH;
							else if(((TimeSignature)s).getDenominator()==4) maxDurationInGroup = Note.QUARTER;
							/*else if(s.equals(TimeSignature.SIGNATURE_3_4)) maxDurationInGroup = Note.QUARTER;*/
					}
					else
						if (s instanceof Note) {
							Note note = ((Note)s);
							short strictDur = note.getStrictDuration();
							if (strictDur<Note.QUARTER && !note.isRest() && 
									(
											(lessThanQuarter.size()==0&&durationInCurrentMeasure%maxDurationInGroup==0))
											|| lessThanQuarter.size()>0) {
								if (lessThanQuarter.size()==0) {
									lessThanQuarterStart = (Point2D)cursor.clone();
								}
								durationInGroup+=((Note)s).getDuration();
								//System.out.println("duration in group " + durationInGroup);
								lessThanQuarter.add(s);
								if (durationInGroup>=maxDurationInGroup) {
									if (!currentStaffLineInitialized) {initNewStaffLine(currentKey, null, cursor, context);currentStaffLineInitialized = true;}
									renderNotesInGroup(lessThanQuarter, context, lessThanQuarterStart, cursor);
									lessThanQuarter.clear();
									durationInGroup = 0;
								}
							}
							else {
								if (!currentStaffLineInitialized) {initNewStaffLine(currentKey, null, cursor, context);currentStaffLineInitialized = true;}
								SNote noteR = new SNote((Note)s, cursor, context);
								width = noteR.render(context, cursor);//SingleNoteRenderer.render(context, (Point2D)cursor.clone(), (Note)s);
								int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
								cursor.setLocation(cursorNewLocationX, cursor.getY());
							}
							durationInCurrentMeasure+=note.getDuration();
							//System.out.println("duration in current measure " + durationInCurrentMeasure);
						}
						else
							if (s instanceof RepeatBarLine) {
								if (!currentStaffLineInitialized) {initNewStaffLine(currentKey, null, cursor, context);currentStaffLineInitialized = true;}
								SRepeatBar bar = new SRepeatBar((RepeatBarLine)s, cursor, context);
								width = bar.render(context, cursor);
								int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
								cursor.setLocation(cursorNewLocationX, cursor.getY());
								durationInCurrentMeasure=0;
							}
							else
							if (s instanceof BarLine) {
								if (!currentStaffLineInitialized) {initNewStaffLine(currentKey, null, cursor, context);currentStaffLineInitialized = true;}
								SBar bar = new SBar((BarLine)s, cursor, context);
								width = bar.render(context, cursor);
								int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
								cursor.setLocation(cursorNewLocationX, cursor.getY());
								durationInCurrentMeasure=0;
							}
							else
								if (s instanceof StaffEndOfLine) {
									renderStaffLines(cursor, context);
									if (cursor.getX()>componentWidth)
										componentWidth = (int)cursor.getX(); 
									/*cursor.setLocation(0, cursor.getY()+staffLinesOffset);*/
									//initNewStaffLine(currentKey, cursor, context);
									currentStaffLineInitialized = false;
								}
				//System.out.println("cursor locaiton : " + cursor.getX());
			}
			if (lessThanQuarter.size()!=0) {
				if (!currentStaffLineInitialized) {initNewStaffLine(currentKey, null, cursor, context);currentStaffLineInitialized = true;}
				renderNotesInGroup(lessThanQuarter, context, lessThanQuarterStart, cursor);
				lessThanQuarter.clear();
				durationInGroup = 0;
			}
			//System.out.println("char staff woidth : " + context.getStaffCharWidth());
			//renderStaffLines(cursor, context);
			setPreferredSize(new Dimension(componentWidth, (int)cursor.getY()));
			repaint();
		}
	}
	
	private void renderNotesInGroup(ArrayList lessThanQuarter, ScoreRenditionContext context, Point2D start, Point2D cursor){
		Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
		int width = (int)GroupOfNotesRenderer.render(context, start, notes);
		//System.out.println("width of group is " + width);
		int cursorNewLocationX = (int)(cursor.getX() + width);
		cursor.setLocation(cursorNewLocationX, cursor.getY());
		cursorNewLocationX = (int)(cursor.getX() + context.getNotesSpacing());
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
	
	private void initNewStaffLine(KeySignature key, TimeSignature ts, Point2D cursor, ScoreRenditionContext context){
		cursor.setLocation(0, cursor.getY()+staffLinesOffset);
		int width = (int)ClefRenderer.render(context, cursor);
		cursor.setLocation(cursor.getX()+width, cursor.getY());
		if (ts!=null) {
			STimeSignature sig = new STimeSignature(ts, cursor, context);
			width = (int)sig.render(context, cursor);
			int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
		SKeySignature sk = new SKeySignature(key, cursor, context);
		width = sk.render(context, cursor);
		int cursorNewLocationX = (int)(cursor.getX() + width + context.getNotesSpacing());
		cursor.setLocation(cursorNewLocationX, cursor.getY());
	}
	
	public void setTune(Tune theTune){
		tune = theTune;
		repaint();
	}
}
