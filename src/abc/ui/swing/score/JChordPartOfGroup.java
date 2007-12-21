package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.MultiNote;
import abc.notation.Note;

public class JChordPartOfGroup extends JChord {

	public JChordPartOfGroup(MultiNote multiNote, ScoreMetrics metrics, Point2D base){
		super(multiNote, metrics, base);
	}
	
	protected JNote createAnchorNote(Note note, Point2D base, ScoreMetrics metrics) {
		return new JNotePartOfGroup(note, new Point2D.Double(), m_metrics);
	}
}
