// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.ui.swing;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.MusicElement;

/** This class is in charge of rendering a key signature. */
class JKeySignature extends JScoreElementAbstract {
/* TODO: test every keys, TJM "Gracings" branch changes not included */
	KeySignature key = null;
	KeySignature previous_key = null;

	private double m_width = -1;
	private ArrayList chars = new ArrayList(); //ArrayList of char[]
	private ArrayList positions = new ArrayList(); //ArrayList of Point2D

	public JKeySignature(KeySignature keyV, Point2D base, ScoreMetrics c) {
		this(keyV, null, base, c);
	}
	
	public JKeySignature(KeySignature keyV, KeySignature keyPrevious,
			Point2D base, ScoreMetrics c) {
		super(c);
		key = keyV;
		previous_key = keyPrevious;
		setBase(base);
	}

	public MusicElement getMusicElement() {
		return key;
	}
	
	public double getWidth() {
		return m_width; //suppose it has been calculated
	}

	protected void onBaseChanged() {
		ScoreMetrics c = getMetrics();
		Point2D m_base = getBase();
		// easier to read, and may speed up a little :)
		double noteHeight = c.getNoteHeight();
		double baseX = m_base.getX();
		double baseY = m_base.getY() - noteHeight/2;
		
		//Calculate vertical position for # and b
		double FsharpY = baseY - JNote.getOffset(new Note(Note.f)) * noteHeight;
		double CsharpY = baseY - JNote.getOffset(new Note(Note.c)) * noteHeight;
		double GsharpY = baseY - JNote.getOffset(new Note(Note.g)) * noteHeight;
		double DsharpY = baseY - JNote.getOffset(new Note(Note.d)) * noteHeight;
		double AsharpY = baseY - JNote.getOffset(new Note(Note.A)) * noteHeight;
		double EsharpY = baseY - JNote.getOffset(new Note(Note.e)) * noteHeight;
		double BsharpY = baseY - JNote.getOffset(new Note(Note.B)) * noteHeight;
		
		double BflatY = BsharpY;//baseY - JNote.getOffset(new Note(Note.B)) * noteHeight;
		double EflatY = EsharpY;//baseY - JNote.getOffset(new Note(Note.e)) * noteHeight;
		double AflatY = AsharpY;//baseY - JNote.getOffset(new Note(Note.A)) * noteHeight;
		double DflatY = DsharpY;//baseY - JNote.getOffset(new Note(Note.d)) * noteHeight;
		double GflatY = baseY - JNote.getOffset(new Note(Note.G)) * noteHeight;
		double CflatY = CsharpY;//baseY - JNote.getOffset(new Note(Note.c)) * noteHeight;
		double FflatY = baseY - JNote.getOffset(new Note(Note.F)) * noteHeight;
		
		//0=C, 1=D..., 6=B
		int[] sharpOrder = new int[] {3,0,4,1,5,2,6};
		int[] flatOrder = new int[] {6,2,5,1,4,0,3};
		double[] sharpYs = new double[] {FsharpY,CsharpY,GsharpY,DsharpY,AsharpY,EsharpY,BsharpY};
		double[] flatYs = new double[] {BflatY,EflatY,AflatY,DflatY,GflatY,CflatY,FflatY};
		
		byte firstAccidental, secondAccidental;

		//key.hasOnlyFlat/Sharp are bogus
		//should be rename key.isFlat/SharpDominant or something like that
		//e.g. K:Dm ^c (D minor with all C sharp, can appear in oriental scales)
		if (key.hasOnlySharps()) {
			firstAccidental = AccidentalType.SHARP;
			secondAccidental = AccidentalType.FLAT;
		} else /*if (key.hasOnlyFlats())*/ {
			firstAccidental = AccidentalType.FLAT;
			secondAccidental = AccidentalType.SHARP;
		}
		
		byte[] accidentals = key.getAccidentals();
		int cpt = 0;
		
		if ((previous_key != null) && !previous_key.equals(key)) {
			//Before switching to a new key, maybe we need to
			//print naturals
			byte accidental = previous_key.hasOnlySharps()
					?AccidentalType.SHARP:AccidentalType.FLAT;
			int[] order = (accidental == AccidentalType.FLAT)
					?flatOrder:sharpOrder;
			char[] glyph = ScoreMetrics.NATURAL;
			double glyphWidth = getMetrics().getBounds(glyph).getWidth();
			double[] Ys = (accidental==AccidentalType.FLAT)
					?flatYs:sharpYs;
			byte[] previous_accidentals = previous_key.getAccidentals();
			for (int i = 0; i < order.length; i++) {
				if ((previous_accidentals[order[i]] != AccidentalType.NATURAL)
					&& (accidentals[order[i]] != previous_accidentals[order[i]])) {
					chars.add(cpt, glyph);
					positions.add(cpt, new Point2D.Double(baseX, Ys[i]));
					baseX += glyphWidth;
					m_width += glyphWidth;
					cpt++;
				}
			}
			if (cpt > 0) { //there are some naturals, so a little space
				baseX += glyphWidth;
				m_width += glyphWidth;
			}
		}
		
		for (int twoPasses = 1; twoPasses <= 2; twoPasses++) {
			byte accidental = twoPasses==1?firstAccidental:secondAccidental;
			int[] order = (accidental == AccidentalType.FLAT)
						?flatOrder:sharpOrder;
			char[] glyph = getMetrics().getAccidentalGlyph(accidental);
			double glyphWidth = getMetrics().getBounds(glyph).getWidth();
			double[] Ys = (accidental==AccidentalType.FLAT)
						?flatYs:sharpYs;
			if (twoPasses == 2 && key.hasSharpsAndFlats()) {
				//A little space when changing accidental
				baseX += glyphWidth;
				m_width += glyphWidth;
			}
			for (int i = 0; i < order.length; i++) {
				if (accidentals[order[i]] == accidental) {
					chars.add(cpt, glyph);
					positions.add(cpt, new Point2D.Double(baseX, Ys[i]));
					baseX += glyphWidth;
					m_width += glyphWidth;
					cpt++;
				}
			}
		}
	}

	public double render(Graphics2D context){
		super.render(context);
		for (int i = 0, j = chars.size(); i < j; i++) {
			if (chars.get(i)!=null) {
				Point2D p = (Point2D) positions.get(i);
				context.drawChars((char[]) chars.get(i), 0, 1,
						(int)p.getX(), (int)p.getY());
			}
		}
		return getWidth();
	}


}
