package abc.ui.swing;

import java.awt.geom.Point2D;

import abc.notation.MultiNote;
import abc.notation.Note;

class JChordPartOfGroup extends JChord implements JGroupableNote {

	public JChordPartOfGroup(MultiNote multiNote, ScoreMetrics metrics, Point2D base){
		super(multiNote, metrics, base);
	}
	
	protected JNote createAnchorNote(Note note, Point2D base, ScoreMetrics metrics) {
		return new JNotePartOfGroup(note, new Point2D.Double(), m_metrics);
	}
	
	protected JChord createComplexChord(MultiNote mNote, ScoreMetrics mtrx, Point2D base) {
		
		return new JChord(mNote, mtrx, base);
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
