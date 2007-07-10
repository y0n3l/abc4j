package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import abc.notation.BarLine;
import abc.notation.KeySignature;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.RepeatBarLine;
import abc.notation.ScoreElementInterface;
import abc.notation.StaffEndOfLine;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tune.Score;

public class JScoreComponent extends JComponent {
	
	private Tune m_tune = null; 
	
	private int staffLinesOffset = -1;
	
	private int componentWidth = 0; 
	
	private int XOffset = 0;
	
	protected ScoreMetrics m_metrics = null;
	
	protected BufferedImage m_bufferedImage = new BufferedImage(3000, 3000, BufferedImage.TYPE_BYTE_GRAY);;
	
	protected Graphics2D m_gfx2 = m_bufferedImage.createGraphics();
	
	public void paint(Graphics g){
		if(m_tune!=null)
			((Graphics2D)g).drawImage(m_bufferedImage, 0, 0, null);
	}
	
	public void writeScoreTo(Tune tune, File file) throws IOException {
		BufferedImage score = new BufferedImage(3000, 3000, BufferedImage.TYPE_BYTE_GRAY);
		Dimension dim = drawIn(tune, score.getGraphics());
		System.out.println("dimension : " + dim);
		BufferedImage scoreResized = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		scoreResized.getGraphics().drawImage(score, 0, 0, null);
		ImageIO.write(scoreResized,"jpg",file);
	}
	
	public Dimension drawIn(Tune tune, Graphics g){
		System.out.println("Calculating score");
		if (tune!=null) {
			if (m_metrics==null)
				m_metrics = new ScoreMetrics((Graphics2D)g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, 3000, 3000);
			g2.setColor(Color.BLACK);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//g2.setColor(Color.black);
			//ScoreMetrics context = new ScoreMetrics(g2);
			Score score = tune.getScore();
			staffLinesOffset = (int)(m_metrics.getStaffCharBounds().getHeight()*2.5);
			Point2D cursor = new Point(XOffset, 0);
			g2.setFont(m_metrics.getFont());
			double width = 0;
			ArrayList lessThanQuarter = new ArrayList();
			//Point2D lessThanQuarterStart = null;
			int durationInGroup = 0;
			int maxDurationInGroup = Note.QUARTER;
			int durationInCurrentMeasure = 0;
			KeySignature currentKey = tune.getKey();
			boolean isPartOfTuplet = false;
			//TimeSignature currentTimeSignature = null;
			boolean currentStaffLineInitialized = false;
			int staffLineNb = 0;
			for (int i=0; i<score.size(); i++) {
				ScoreElementInterface s = (ScoreElementInterface)score.elementAt(i);
				if (
						//detects the end of a group.
						(!(s instanceof Note)  
						|| (s instanceof Note && ((Note)s).isRest()) 
						//if we were in a tuplet and the current note isn't part of tuplet anymore.
						|| (s instanceof NoteAbstract && isPartOfTuplet && (!((NoteAbstract)s).isPartOfTuplet()))
						|| (s instanceof Note && ((Note)s).getStrictDuration()>=Note.QUARTER) 
						|| (durationInCurrentMeasure!=0 && durationInCurrentMeasure%maxDurationInGroup==0))
						&& lessThanQuarter.size()!=0)
					{
					if (!currentStaffLineInitialized) {initNewStaffLine(g, currentKey, null, cursor, m_metrics);currentStaffLineInitialized = true;}
					renderNotesInGroup(g2, lessThanQuarter, cursor);
					lessThanQuarter.clear();
					durationInGroup = 0;
				}
				if (s instanceof KeySignature) 
					currentKey = (KeySignature)s;
				else
					if (s instanceof TimeSignature) {
						if (!currentStaffLineInitialized) {initNewStaffLine(g, currentKey, (TimeSignature)s, cursor, m_metrics);currentStaffLineInitialized = true;}
						//width = TimeSignatureRenderer.render(m_metrics, (Point2D)cursor.clone(), (TimeSignature)s);
						//int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
						//cursor.setLocation(cursorNewLocationX, cursor.getY());
						if(s.equals(TimeSignature.SIGNATURE_4_4)) maxDurationInGroup = 2*Note.QUARTER;
						else if(((TimeSignature)s).getDenominator()==8) maxDurationInGroup = 3*Note.EIGHTH;
							else if(((TimeSignature)s).getDenominator()==4) maxDurationInGroup = Note.QUARTER;
							/*else if(s.equals(TimeSignature.SIGNATURE_3_4)) maxDurationInGroup = Note.QUARTER;*/
					}
					else
						if (s instanceof NoteAbstract) {
							Note note = null;
							if (s instanceof MultiNote)
								note = ((MultiNote)s).getShortestNote();
							else
								note = ((Note)s);
							short strictDur = note.getStrictDuration();
							isPartOfTuplet = note.isPartOfTuplet();
							// checks if this note should be part of a group.
							if (strictDur<Note.QUARTER && !note.isRest()) {
								durationInGroup+=(note).getDuration();
								//System.out.println("duration in group " + durationInGroup);
								lessThanQuarter.add(note);
								if (durationInGroup>=maxDurationInGroup) {
									if (!currentStaffLineInitialized) {initNewStaffLine(g, currentKey, null, cursor, m_metrics);currentStaffLineInitialized = true;}
									renderNotesInGroup(g2, lessThanQuarter, cursor);
									lessThanQuarter.clear();
									durationInGroup = 0;
								}
							}
							else {
								if (!currentStaffLineInitialized) {initNewStaffLine(g, currentKey, null, cursor, m_metrics);currentStaffLineInitialized = true;}
								SNote noteR = new SNote(note, cursor, m_metrics);
								width = noteR.render(g2);//SingleNoteRenderer.render(m_metrics, (Point2D)cursor.clone(), (Note)s);
								int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
								cursor.setLocation(cursorNewLocationX, cursor.getY());
							}
							durationInCurrentMeasure+=note.getDuration();
							//System.out.println("duration in current measure " + durationInCurrentMeasure);
						}
						else
							if (s instanceof RepeatBarLine) {
								if (!currentStaffLineInitialized) {initNewStaffLine(g, currentKey, null, cursor, m_metrics);currentStaffLineInitialized = true;}
								SRepeatBar bar = new SRepeatBar((RepeatBarLine)s, cursor, m_metrics);
								width = bar.render(g);
								int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
								cursor.setLocation(cursorNewLocationX, cursor.getY());
								durationInCurrentMeasure=0;
							}
							else
							if (s instanceof BarLine) {
								if (!currentStaffLineInitialized) {initNewStaffLine(g, currentKey, null, cursor, m_metrics);currentStaffLineInitialized = true;}
								SBar bar = new SBar((BarLine)s, cursor, m_metrics);
								width = bar.render(g);
								int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
								cursor.setLocation(cursorNewLocationX, cursor.getY());
								durationInCurrentMeasure=0;
							}
							else
								if (s instanceof StaffEndOfLine) {
									renderStaffLines(g2, cursor);
									staffLineNb++;
									if (cursor.getX()>componentWidth)
										componentWidth = (int)cursor.getX(); 
									/*cursor.setLocation(0, cursor.getY()+staffLinesOffset);*/
									//initNewStaffLine(currentKey, cursor, m_metrics);
									currentStaffLineInitialized = false;
								}
				//System.out.println("cursor locaiton : " + cursor.getX());
			}
			if (lessThanQuarter.size()!=0) {
				if (!currentStaffLineInitialized) {initNewStaffLine(g, currentKey, null, cursor, m_metrics);currentStaffLineInitialized = true;}
				renderNotesInGroup(g2, lessThanQuarter, cursor);
				lessThanQuarter.clear();
				durationInGroup = 0;
			}
			//System.out.println("char staff woidth : " + m_metrics.getStaffCharWidth());
			renderStaffLines(g2, cursor);
			if (cursor.getX()>componentWidth)
				componentWidth = (int)cursor.getX();
			return new Dimension((int)(componentWidth+m_metrics.getStaffCharBounds().getWidth()), (int)(cursor.getY()+m_metrics.getStaffCharBounds().getHeight()));
			//repaint();
		}
		else
			return null;
	}
	
	private void renderNotesInGroup(Graphics2D context, ArrayList lessThanQuarter, Point2D cursor){
		Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
		int width = 0;
		if (notes.length==1) {
			SNote sn = new SNote(notes[0], cursor, m_metrics);
			width = sn.render(context);
		}
		else {
			GroupOfNotesRenderer rend = new GroupOfNotesRenderer(m_metrics, cursor, notes);
			width = (int)rend.render(context);
		}
		//System.out.println("width of group is " + width);
		int cursorNewLocationX = (int)(cursor.getX() + width);
		cursor.setLocation(cursorNewLocationX, cursor.getY());
		cursorNewLocationX = (int)(cursor.getX() + m_metrics.getNotesSpacing());
		cursor.setLocation(cursorNewLocationX, cursor.getY());
	}
	
	private void renderStaffLines(Graphics2D context, Point2D cursor){
		int staffCharNb = (int)((cursor.getX()-XOffset)/m_metrics.getStaffCharWidth());
		//System.out.println("char staff nb : " + staffCharNb);
		char[] staffS = new char[staffCharNb+1];
		for (int i=0; i<staffCharNb+1; i++)
			staffS[i] = ScoreMetrics.STAFF_SIX_LINES;
		context.drawChars(staffS, 0, staffS.length, XOffset, (int)cursor.getY());
	}
	
	private void initNewStaffLine(Graphics context, KeySignature key, TimeSignature ts, Point2D cursor, ScoreMetrics metrix){
		cursor.setLocation(0, cursor.getY()+staffLinesOffset);
		SClef clef = new SClef(cursor, metrix);
		int width = clef.render(context);
		cursor.setLocation(cursor.getX()+width, cursor.getY());
		if (ts!=null) {
			STimeSignature sig = new STimeSignature(ts, cursor, metrix);
			width = (int)sig.render(context);
			int cursorNewLocationX = (int)(cursor.getX() + width + metrix.getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
		if (key!=null) {
			SKeySignature sk = new SKeySignature(key, cursor, metrix);
			width = sk.render(context);
			int cursorNewLocationX = (int)(cursor.getX() + width + metrix.getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
	}
	
	public void setTune(Tune theTune){
		m_tune = theTune;
		Dimension dim = drawIn(m_tune, m_gfx2);
		setPreferredSize(dim);
		setSize(dim);
		repaint();
	}
	
}
