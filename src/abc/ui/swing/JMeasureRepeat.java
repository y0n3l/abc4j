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
import java.awt.geom.Rectangle2D;

import abc.notation.BarLine;
import abc.notation.Decoration;
import abc.notation.MeasureRepeat;
import abc.notation.MusicElement;
import abc.notation.Spacer;

public class JMeasureRepeat extends JScoreElementAbstract {
	
	private MeasureRepeat m_measureRepeat = null;
	
	public JMeasureRepeat(ScoreMetrics metrics, Point2D base, MeasureRepeat mr) {
		super(metrics);
		m_measureRepeat = mr;
		setBase(base);
	}

	public Rectangle2D getBoundingBox() {
		Rectangle2D bb = new Rectangle2D.Double(getBase().getX(), getBase()
				.getY()
				- getHeight(), getWidth(), getHeight());
		return bb;
	}

	private int getHeight() {
		return (int) getMetrics().getStaffCharBounds().getHeight();
	}

	public double getWidth() {
		return getMetrics().getBounds(
			getMusicalFont().getDecoration(
				m_measureRepeat.getNumberOfMeasure()==1
					?Decoration.REPEAT_LAST_BAR
					:Decoration.REPEAT_LAST_TWO_BARS
			)
		).getWidth() * 3;
	}

	public MusicElement getMusicElement() {
		return m_measureRepeat;
	}

	protected void onBaseChanged() {
	}

	public double render(Graphics2D context){
		super.render(context);
		if (m_measureRepeat.getNumberOfMeasure() == 1) {
			float ratio = (float) (getWidth() / getMetrics().getNoteWidth());
			Spacer spacer = new Spacer(ratio);
			spacer.setDecorations(new Decoration[]
					{ new Decoration(Decoration.REPEAT_LAST_BAR) });
			spacer.setDynamic(m_measureRepeat.getDynamic());
			spacer.setAnnotations(m_measureRepeat.getAnnotations());
			spacer.setChord(m_measureRepeat.getChord());
			JSpacer jspacer = new JSpacer(getMetrics(), getBase(), spacer);
			jspacer.setStaffLine(getStaffLine());
			jspacer.render(context);
		} else { //2
			float ratio = (float) ((getWidth()/2)
					/ getMetrics().getNoteWidth());
			JSpacer jspacer = new JSpacer(getMetrics(), getBase(), new Spacer(ratio));
			jspacer.setStaffLine(getStaffLine());
			double w = jspacer.render(context);
			setBase(new Point2D.Double(getBase().getX()+w,
					getBase().getY()));
			BarLine barline = new BarLine(BarLine.SIMPLE);
			barline.setDecorations(new Decoration[]
					{ new Decoration(Decoration.REPEAT_LAST_TWO_BARS) });
			barline.setDynamic(m_measureRepeat.getDynamic());
			barline.setAnnotations(m_measureRepeat.getAnnotations());
			barline.setChord(m_measureRepeat.getChord());
			JBar jbar = new JBar(barline, getBase(), getMetrics());
			jbar.setStaffLine(getStaffLine());
			jbar.render(context);
		}
		return getWidth();
	}
	
}
