package abc.ui.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.Vector;

import abc.notation.MultiNote;
import abc.notation.MusicElement;
import abc.notation.Note;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

/** This class is in charge of rendering a chord. */
class JChord extends JScoreElementAbstract {
	/** the multi this JChord is the graphic representation of. */ 
	protected MultiNote multiNote = null;
	/** All the notes composing the chord. */
	protected Note[] m_notes = null;
	/** All the notes rendition elements that are part of the group. */
	protected JNote[] m_sNoteInstances = null;
	
	protected JNote anchor = null;
	/** <TT>true</TT> if the stem is up for this chord, <TT>false</TT> otherwise. */
	protected boolean isStemUp = true;
	/** When multi notes are made of notes with different durations, such chord 
	 * is decomposed into chords with same strict duration for normalization,
	 * and to ease the rendition : the rendition is made on set of notes with the same
	 * strict duration.
	 * Such normalized chords are ordered with ascend strict durations in this array. */  
	protected JChord[] m_normalizedChords = null;
	
	public JChord(MultiNote multiNote, ScoreMetrics metrics, Point2D base){
		super(metrics);
		this.multiNote = multiNote;
		m_notes = multiNote.toArray();
		//create JNotePartOfGroup instances. Those instances stay the same when the base is changed.
		//The width of the chord is the width of the largest note.
		if (multiNote.hasUniqueStrictDuration()) {
			m_sNoteInstances = new JNote[m_notes.length];
			for (int i=0; i<m_notes.length; i++) {
				m_sNoteInstances[i] = new JChordNote(m_notes[i], new Point2D.Double(), m_metrics);
				if(m_sNoteInstances[i].getWidth()>m_width)
					m_width = m_sNoteInstances[i].getWidth();
			}
			//inits stem direction
			setStemUp(isStemUp);
		}
		else {
			m_sNoteInstances = new JNote[1];
			m_sNoteInstances[0] = new JNote(multiNote.getHighestNote(), new Point2D.Double(), m_metrics);
			m_width = m_sNoteInstances[0].getWidth();
			Hashtable h = multiNote.splitWithSameStrictDuration();
			short[] durations = multiNote.getStrictDurations();
			if (durations.length>2)
				System.err.println("abc4j - warning : chords with more than 2 differents strict duration aren't supported : only 2 smaller durations are taken into account");
			m_normalizedChords = new JChord[2]; 
			MultiNote fastest = new MultiNote((Vector)h.get(new Short(durations[0])));
			JChord jChord = createNormalizedChord(fastest, metrics, base);
			m_normalizedChords[0] = jChord;
			m_normalizedChords[0].setStemUp(true);
			MultiNote slowest = new MultiNote((Vector)h.get(new Short(durations[1])));
			jChord = createNormalizedChord(slowest, metrics, base);
			m_normalizedChords[1] = jChord;
			m_normalizedChords[1].setStemUp(false);
		}
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
		JNote jNote = new JNote(note, new Point2D.Double(), m_metrics);
		//note.set
		return jNote;
	}
	
	/** Invoked when a multi note is decomposed into multi notes with same strict 
	 * duration. */
	protected JChord createNormalizedChord(MultiNote mNote, ScoreMetrics mtrx, Point2D base) {
		return new JChord(mNote, mtrx, base);
	}
	
	public MusicElement getMusicElement() {
		return multiNote;
	}
	
	public JNote[] getScoreElements() {
		return m_sNoteInstances; 
	}
	
	/** Sets the staff line this chord belongs to. */
	public void setStaffLine(JStaffLine staffLine) {
		for (int i=0; i<m_sNoteInstances.length; i++)
			m_sNoteInstances[i].setStaffLine(staffLine);
		super.setStaffLine(staffLine);
	}
	
	/*JNotePartOfGroup[] getRenditionElements() {
		return m_sNoteInstances;
	}*/
	
	/** Sets the base of this chord. */
	public void setBase(Point2D base) {
		if (m_normalizedChords!=null)
			for (int i=0; i<m_normalizedChords.length; i++)
				m_normalizedChords[i].setBase(base);
		super.setBase(base);
	}
	
	/** Invoked when this chord base has changed. */
	protected void onBaseChanged() {
		if (m_normalizedChords==null) {
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
		}
		else
			for (int i=0; i<m_normalizedChords.length; i++)
				m_normalizedChords[i].onBaseChanged();
		//m_width = width;
	}
	
	public double render(Graphics2D context){
		//super.render(context);
		//Stroke defaultStroke = context.getStroke();
		//JNote lowestElement = m_sNoteInstances[0];
		//JNote highestElement = m_sNoteInstances[m_sNoteInstances.length-1];
		if (m_normalizedChords==null){
			for (int i=0; i<m_sNoteInstances.length; i++) {
				JNote n = m_sNoteInstances[i];
				n.render(context);
				//System.out.println("note : "+ n + " stem up " + n.isStemUp);
			}
			Stroke defaultStroke = context.getStroke();
			BasicStroke notesLinkStroke = m_metrics.getStemStroke();
			context.setStroke(notesLinkStroke);
			/*if (isStemUp) {*/
				if (multiNote.getLongestNote().getStrictDuration()<Note.WHOLE)
					context.drawLine((int)m_sNoteInstances[0].getStemBegin().getX(),
							(int)m_sNoteInstances[0].getStemBegin().getY(),
							(int)m_sNoteInstances[m_sNoteInstances.length-1].getStemBegin().getX(),
							(int)m_sNoteInstances[m_sNoteInstances.length-1].getStemBegin().getY());
						/*((JNotePartOfGroup)lowestElement).getStemYBegin(), 
						((JNotePartOfGroup)lowestElement).getStemX(), 
						(int)(highestElement.getNotePosition().getY()-m_metrics.getNoteHeigth()/2));
			}
			else
				context.drawLine(((JNotePartOfGroup)highestElement).getStemX(), 
						((JNotePartOfGroup)highestElement).getStemYBegin(), 
						((JNotePartOfGroup)highestElement).getStemX(), 
						(int)(lowestElement.getNotePosition().getY()-m_metrics.getNoteHeigth()/2));*/
			context.setStroke(defaultStroke);
		}
		else
			for (int i=0; i<m_normalizedChords.length; i++)
				m_normalizedChords[i].render(context); 
		return m_width;
	}
	
	public void setStemUp(boolean isUp) {
		isStemUp = isUp;
		if (isUp) {
			m_sNoteInstances[0] = new JChordNote(m_notes[0], m_sNoteInstances[0].getBase(), m_metrics);
			JNote highestJNote = m_sNoteInstances[m_sNoteInstances.length-1];
			Note highestNote = (Note)highestJNote.getMusicElement();
			//When the stem is up, the anchor is the highest note.
			anchor = createAnchorNote(highestNote, highestJNote.getBase(), m_metrics);
			m_sNoteInstances[m_sNoteInstances.length-1] = anchor;
			for (int i=0; i<m_sNoteInstances.length; i++)
				m_sNoteInstances[i].setStemUp(true);
		}
		else {
			//Replace the existing highest note 
			m_sNoteInstances[m_sNoteInstances.length-1] = new JChordNote(m_notes[m_notes.length-1], 
					m_sNoteInstances[m_sNoteInstances.length-1].getBase(), m_metrics);
			JNote lowestJNote = m_sNoteInstances[0];
			Note lowestNote = (Note)lowestJNote.getMusicElement();
			// Replace the existing lowest note
			//When the stem is down, the anchor is the lowest note.
			anchor = createAnchorNote(lowestNote, lowestJNote.getBase(), m_metrics);
			m_sNoteInstances[0] = anchor;
			//m_sNoteInstances[0].setStemUp(false);
			//Apply the stem direction to the rest of the notes composing the chord.
			for (int i=0; i<m_sNoteInstances.length; i++)
				m_sNoteInstances[i].setStemUp(false);
			
		}
	}
	
	public JScoreElementAbstract getScoreElementAt(Point point) {
		JScoreElementAbstract scoreEl = null;
		for (int i=0; i<m_sNoteInstances.length; i++) {
			scoreEl = m_sNoteInstances[i].getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}
}
