package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.AccidentalType;
import abc.notation.ScoreElementInterface;
import abc.ui.swing.JScoreElement;

public class JChord extends JScoreElement {
	
	protected MultiNote multiNote = null;
	
	/** All the notes composing the chord. */
	protected Note[] m_notes = null;
	/** All the notes rendition elements that are part of the group. */
	protected JNote[] m_sNoteInstances = null;
	
	protected JNote highestElement = null;
	protected JNotePartOfGroup lowestElement = null;
	
	public JChord(MultiNote multiNote, ScoreMetrics metrics, Point2D base){
		super(metrics);
		this.multiNote = multiNote;
		m_notes = new Note[multiNote.getNotesAsVector().size()];
		multiNote.getNotesAsVector().toArray(m_notes);
		//create JNotePartOfGroup instances. Those instances should stay the same
		//when the base is changed.
		//m_sNoteInstances = new JScoreElement[m_notes.length];
		if (multiNote.hasUniqueStrictDuration()) {
			m_sNoteInstances = new JNote[m_notes.length];
			for (int i=0; i<m_notes.length; i++) {
				if (m_notes[i].equals(multiNote.getHighestNote())) {
					highestElement = createAnchorNote(m_notes[i], new Point2D.Double(), m_metrics);
					m_sNoteInstances[i] = highestElement; 
				}
				else{
					m_sNoteInstances[i] = new JChordNote(m_notes[i], new Point2D.Double(), m_metrics);
					if (m_notes[i].equals(multiNote.getLowestNote()))
						lowestElement = (JChordNote)m_sNoteInstances[i]; 
				}
				if(m_sNoteInstances[i].getWidth()>m_width)
					m_width = m_sNoteInstances[i].getWidth();
			}
		}
		else {
			//TODO support multi notes when the notes have different durations.
			m_sNoteInstances = new JNote[1];
			m_sNoteInstances[0] = new JNote(multiNote.getHighestNote(), new Point2D.Double(), m_metrics);
			m_width = m_sNoteInstances[0].getWidth();
		}
		//m_sNoteInstances[i]=n;
		setBase(base);
	}
	
	/** The anchor of the chord is the one that present the rhytm (the highest one 
	 * in case of stem up, the lowest one in case of stem down)
	 * @param note
	 * @param base
	 * @param metrics
	 * @return
	 */
	protected JNote createAnchorNote(Note note, Point2D base, ScoreMetrics metrics) {
		return new JNote(note, new Point2D.Double(), m_metrics);
	}
	
	public ScoreElementInterface getScoreElement() {
		return null;
	}
	
	Note[] getScoreElements() {
		return m_notes; 
	}
	
	/*JNotePartOfGroup[] getRenditionElements() {
		return m_sNoteInstances;
	}*/
	
	protected void onBaseChanged() {
		//m_sNoteInstances = new JNotePartOfGroup[m_notes.length];
		//Point2D currentBase =(Point2D)m_base.clone();
		//Note highestNote = Note.getHighestNote(m_notes);
		//JNotePartOfGroup sn = new JNotePartOfGroup(highestNote, m_base, m_metrics);
		//m_stemYend = sn.getStemYBegin()-m_metrics.getStemLength();
		//JNotePartOfGroup firstNote = null;
		//JNotePartOfGroup lastNote = null;
		
		//double width = 0;
		//boolean hasAccidental = multiNote.hasAccidental();
		double biggestStemX = -1;
		for (int i=0; i<m_sNoteInstances.length; i++) {
			m_sNoteInstances[i].setBase(m_base);
			if (m_sNoteInstances[i].getStemBegin().getX()>biggestStemX)
				biggestStemX = m_sNoteInstances[i].getStemBegin().getX();
			//if(m_sNoteInstances[i].getWidth()>width)
			//	width = m_sNoteInstances[i].getWidth();
		}
		//realign all stems
		for (int i=0; i<m_sNoteInstances.length; i++) {
			Point2D stemBegin = m_sNoteInstances[i].getStemBegin();
			Point2D newStemBegin = new Point2D.Double (biggestStemX, stemBegin.getY());
			m_sNoteInstances[i].setStemBegin(newStemBegin);
		}
		//m_width = width;
	}
	
	public double render(Graphics2D context){
		//super.render(context);
		//Stroke defaultStroke = context.getStroke();
		for (int i=0; i<m_sNoteInstances.length; i++) {
			JScoreElement n = m_sNoteInstances[i];
			n.render(context);
		}
		Stroke defaultStroke = context.getStroke();
		BasicStroke notesLinkStroke = m_metrics.getStemStroke();
		context.setStroke(notesLinkStroke);
		if (lowestElement!=null && multiNote.getLongestNote().getStrictDuration()<Note.WHOLE)
			context.drawLine(lowestElement.getStemX(), lowestElement.getStemYBegin(), 
					lowestElement.getStemX(), (int)(highestElement.getNotePosition().getY()-m_metrics.getNoteHeigth()/2));
		context.setStroke(defaultStroke);
		return m_width;
	}
	
	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		for (int i=0; i<m_sNoteInstances.length; i++) {
			scoreEl = m_sNoteInstances[i].getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}
}
