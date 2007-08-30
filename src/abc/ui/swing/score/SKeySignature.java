package abc.ui.swing.score;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.Note;

public class SKeySignature extends SRenderer {
	KeySignature key = null;
	Point2D FPosition = null; 
	char[] Fchar = null;
	Point2D CPosition = null;
	char[] Cchar = null;
	Point2D GPosition = null;
	char[] Gchar = null;
	Point2D DPosition = null;
	char[] Dchar = null;
	Point2D APosition = null;
	char[] Achar = null;
	Point2D EPosition = null;
	char[] Echar = null;
	Point2D BPosition = null;
	char[] Bchar = null;
	
	public SKeySignature(KeySignature keyV, Point2D base, ScoreMetrics c) {
		super(base, c);
		key = keyV;
		if (keyV.hasOnlySharps()) {
			double FPositionX = base.getX();
			double FPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.f, AccidentalType.NONE))*c.getNoteHeigth();
			FPosition = new Point2D.Double(FPositionX, FPositionY);
			double CPositionX = base.getX()+c.getSharpBounds().getWidth();
			double CPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.c, AccidentalType.NONE))*c.getNoteHeigth();
			CPosition = new Point2D.Double(CPositionX, CPositionY);
			double GPositionX = base.getX()+2*c.getSharpBounds().getWidth();
			double GPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.g, AccidentalType.NONE))*c.getNoteHeigth();
			GPosition = new Point2D.Double(GPositionX, GPositionY);
			double DPositionX = base.getX()+3*c.getSharpBounds().getWidth();
			double DPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.d, AccidentalType.NONE))*c.getNoteHeigth();
			DPosition = new Point2D.Double(DPositionX, DPositionY);
			double APositionX = base.getX()+4*c.getSharpBounds().getWidth();
			double APositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.A, AccidentalType.NONE))*c.getNoteHeigth();
			APosition = new Point2D.Double(APositionX, APositionY);
			double EPositionX = base.getX()+5*c.getSharpBounds().getWidth();
			double EPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.e, AccidentalType.NONE))*c.getNoteHeigth();
			EPosition = new Point2D.Double(EPositionX, EPositionY);
			double BPositionX = base.getX()+6*c.getSharpBounds().getWidth();
			double BPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.B, AccidentalType.NONE))*c.getNoteHeigth();
			BPosition = new Point2D.Double(BPositionX, BPositionY);
		}
		else
			if (keyV.hasOnlyFlats()) {
				double BPositionX = base.getX();
				double BPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.B, AccidentalType.NONE))*c.getNoteHeigth();
				BPosition = new Point2D.Double(BPositionX, BPositionY);
				double EPositionX = base.getX()+c.getSharpBounds().getWidth();
				double EPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.e, AccidentalType.NONE))*c.getNoteHeigth();
				EPosition = new Point2D.Double(EPositionX, EPositionY);
				double APositionX = base.getX()+2*c.getSharpBounds().getWidth();
				double APositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.A, AccidentalType.NONE))*c.getNoteHeigth();
				APosition = new Point2D.Double(APositionX, APositionY);
				double DPositionX = base.getX()+3*c.getSharpBounds().getWidth();
				double DPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.d, AccidentalType.NONE))*c.getNoteHeigth();
				DPosition = new Point2D.Double(DPositionX, DPositionY);
				double GPositionX = base.getX()+4*c.getSharpBounds().getWidth();
				double GPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.g, AccidentalType.NONE))*c.getNoteHeigth();
				GPosition = new Point2D.Double(GPositionX, GPositionY);
				double CPositionX = base.getX()+5*c.getSharpBounds().getWidth();
				double CPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.d, AccidentalType.NONE))*c.getNoteHeigth();
				CPosition = new Point2D.Double(CPositionX, CPositionY);
				double FPositionX = base.getX()+6*c.getSharpBounds().getWidth();
				double FPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.f, AccidentalType.NONE))*c.getNoteHeigth();
				FPosition = new Point2D.Double(FPositionX, FPositionY);
			}
		byte[] accidentals = keyV.getAccidentals();
		for (int i=0; i<accidentals.length; i++){
			char[] chars = null;
			if (accidentals[i]==AccidentalType.SHARP) {
				chars=ScoreMetrics.SHARP;
				m_width+=c.getSharpBounds().getWidth();
			}
			else
				if (accidentals[i]==AccidentalType.FLAT){
					chars=ScoreMetrics.FLAT;
					m_width+=c.getFlatBounds().getWidth();
				}
			switch (i) {
				case 0: Cchar = chars; break;
				case 1: Dchar = chars; break;
				case 2: Echar = chars; break;
				case 3: Fchar = chars; break;
				case 4: Gchar = chars; break;
				case 5: Achar = chars; break;
				case 6: Bchar = chars; break;
			}
		}
	}
	
	public double render(Graphics2D context){
		super.render(context);
		if (Fchar!=null)
			context.drawChars(Fchar, 0, 1, (int)FPosition.getX(), (int)FPosition.getY());
		if (Cchar!=null)
			context.drawChars(Cchar, 0, 1, (int)CPosition.getX(), (int)CPosition.getY());
		if (Gchar!=null)
			context.drawChars(Gchar, 0, 1, (int)GPosition.getX(), (int)GPosition.getY());
		if (Dchar!=null)
			context.drawChars(Dchar, 0, 1, (int)DPosition.getX(), (int)DPosition.getY());
		if (Achar!=null)
			context.drawChars(Achar, 0, 1, (int)APosition.getX(), (int)APosition.getY());
		if (Echar!=null)
			context.drawChars(Echar, 0, 1, (int)EPosition.getX(), (int)EPosition.getY());
		if (Bchar!=null)
			context.drawChars(Bchar, 0, 1, (int)BPosition.getX(), (int)BPosition.getY());
		return m_width;
	}
	
	
}
