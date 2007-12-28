package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.MultiNote;
import abc.notation.Note;

public class JChordPartOfGroup extends JChord implements GroupableNote {

	public JChordPartOfGroup(MultiNote multiNote, ScoreMetrics metrics, Point2D base){
		super(multiNote, metrics, base);
	}
	
	protected JNote createAnchorNote(Note note, Point2D base, ScoreMetrics metrics) {
		return new JNotePartOfGroup(note, new Point2D.Double(), m_metrics);
	}
	
	public int getStemX() {
		return ((JNotePartOfGroup)anchor).getStemX();
	}

	public int getStemYBegin() {
		return ((JNotePartOfGroup)anchor).getStemYBegin();
	}
	
	public void setStemYEnd(int value) {
		((JNotePartOfGroup)anchor).setStemYEnd(value);
	}
	
	public int getStemYEnd() {
		return ((JNotePartOfGroup)anchor).getStemYEnd();
	}
	
	public Point2D getStemBegin() {
		return ((JNotePartOfGroup)anchor).getStemBegin();
	}
	
	public JNotePartOfGroup getReferenceNoteForGroup () {
		return (JNotePartOfGroup)anchor;
	}
}
