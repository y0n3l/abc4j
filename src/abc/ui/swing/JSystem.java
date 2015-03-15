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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.TreeMap;

import abc.notation.MusicElement;
import abc.notation.PositionableInTime;

/**
 * A system contains one or several {@link abc.ui.swing.JStaffLine staves}, one
 * per {@link abc.notation.Voice}.
 * 
 * This class ensure measure alignment and justification.
 */
class JSystem extends JScoreElementAbstract {

	private Engraver m_engraver;
	
	private boolean voicesAligned = false;

	/** The staff lines drawings. */
	private ArrayList<JStaffLine> m_staffLines = null;

	protected JSystem(Point2D base, ScoreMetrics c, Engraver e, int nbOfVoices) {
		super(base, c);
		m_engraver = e;
		m_staffLines = new ArrayList<JStaffLine>(nbOfVoices);
	}

	protected double getBottomY() {
		if (m_staffLines.size() > 0) {
			return ((JStaffLine)m_staffLines.get(m_staffLines.size()-1))
				.getBottomY();
		} else
			return getBase().getY();
	}

	public MusicElement getMusicElement() {
		return null;
	}

	/** <B>This is not applicable for a system!</B> returns null */
	public JStaffLine getStaffLine() {
		return null;
	}
	
	/** Returns the request staff line, null if not present */
	protected JStaffLine getStaffLine(String voiceId) {
		for (JStaffLine jsl : m_staffLines) {
			if (jsl.getVoiceId().equals(voiceId))
				return jsl;
		}
		return null;
	}

	/** Returns a ArrayList of JStaffLine */
	protected ArrayList<JStaffLine> getStaffLines() {
		return m_staffLines;
	}

	public double getWidth() {
		if (!voicesAligned)
			alignVoices();
		double max = 0;
		for (JStaffLine staff : getStaffLines()) {
			max = Math.max(max, staff.getWidth());
		}
		return max;
	}
	
	public void scaleToWidth(double newWidth) {
		alignVoices();
		if (getStaffLines().size() == 1) {
			((JStaffLine) getStaffLines().get(0)).scaleToWidth(newWidth);
		} else {
			for (JStaffLine jsl : getStaffLines()) {
				jsl.scaleToWidth(newWidth);
			}
		}
	}
	
	protected JStaffLine lastStaffLine() {
		if (m_staffLines.isEmpty())
			return null;
		else
			return (JStaffLine) m_staffLines.get(m_staffLines.size() - 1);
	}
	
	private void alignVoices() {
		voicesAligned = true;
		if (getStaffLines().size() <= 1) {
			return;
		}
		// For each position in time bar/units, store the max X position
		TreeMap<Long, Double> timeToXall = new TreeMap<Long, Double>();
		// Store times pos' that are common to at least 2 elements
		ArrayList<Long> timeToXcommon = new ArrayList<Long>();
		for (JStaffLine staffLine : getStaffLines()) {
			for (JScoreElementAbstract element : staffLine.getStaffElements()) {
				JScoreElementAbstract[] subElements;
				if (element instanceof JGroupOfNotes)
					subElements = ((JGroupOfNotes) element).getRenditionElements();
				else
					subElements = new JScoreElementAbstract[] { element };
				for (int iSubElmt = 0; iSubElmt < subElements.length; iSubElmt++) {
					JScoreElementAbstract subElement = subElements[iSubElmt];
					MusicElement musicElmt = subElement.getMusicElement();
					if (musicElmt != null
							&& musicElmt instanceof PositionableInTime) {
						PositionableInTime pit = (PositionableInTime) musicElmt;
						Long key = new Long((long) pit.getPositionInBars()
								* 1000000 + pit.getPositionInUnits());
						double x = subElement.getBase().getX();
						if (timeToXall.containsKey(key)) {
							timeToXcommon.add(key);
							double storedX = ((Double) timeToXall.get(key))
									.doubleValue();
							if (x > storedX) {
								double diffX = x - storedX;
								// shift all time > key by diffX
								for (Long key2 : timeToXall.keySet()) {
									if (key2.longValue() > key.longValue()) {
										timeToXall.put(key2, new Double(
												((Double) timeToXall.get(key))
														.doubleValue()
														+ diffX));
									}
								}
								timeToXall.put(key, new Double(x));
							}
						} else {
							timeToXall.put(key, new Double(x));
						}
					}
				}
			}
		}
		// Remove time pos used by only one element
		ArrayList<Long> timeToRemove = new ArrayList<Long>();
		for (Long key : timeToXall.keySet()) {
			if (!timeToXcommon.contains(key))
				timeToRemove.add(key);
		}
		for (Long key : timeToRemove) {
			timeToXall.remove(key);
		}
		timeToXcommon.clear();
		timeToXcommon = null;
		timeToRemove.clear();
		timeToRemove = null;
		

		// here we have timeToXall containing time -> max(x) for all
		// time position having at least 2 element
		// For each voice, realign on x, or scale elements between 2 known x
		
		ArrayList<JScoreElementAbstract> elementsToScale = new ArrayList<JScoreElementAbstract>();
		for (JStaffLine staffLine : getStaffLines()) {
			//Before the first known x, don't move anything
			boolean dontMoveFirstElements = true;
			for (JScoreElementAbstract element : staffLine.getStaffElements()) {
				JScoreElementAbstract[] subElements;
				if (element instanceof JGroupOfNotes)
					subElements = ((JGroupOfNotes) element).getRenditionElements();
				else
					subElements = new JScoreElementAbstract[] { element };
				for (int iSubElmt = 0; iSubElmt < subElements.length; iSubElmt++) {
					JScoreElementAbstract subElement = subElements[iSubElmt];
					MusicElement musicElmt = subElement.getMusicElement();
					if (musicElmt != null
							&& musicElmt instanceof PositionableInTime) {
						PositionableInTime pit = (PositionableInTime) musicElmt;
						Long key = new Long((long) pit.getPositionInBars()
								* 1000000 + pit.getPositionInUnits());
						if (timeToXall.containsKey(key)) {
							double x = ((Double) timeToXall.get(key)).doubleValue();
							subElement.setBase(new Point2D.Double(x, element.getBase().getY()));
							subElement.setColor(Color.BLUE);
							if (elementsToScale.size() > 0) {
								//TODO scale element, except first which X is correct
								boolean bFirst = true;
								for (JScoreElementAbstract jsea : elementsToScale) {
									if (!bFirst) {
										jsea.setColor(Color.RED);
									}
									bFirst = false;
								}
								elementsToScale.clear();
							}
							dontMoveFirstElements = false;
						} else {
							if (!dontMoveFirstElements)
								elementsToScale.add(element);
						}
					}
					if (!dontMoveFirstElements)
						elementsToScale.add(subElement);	
				}
			}
		}
		
	}
	
	public double render(Graphics2D g2) {
		if (!voicesAligned)
			alignVoices();
		for (JStaffLine jsl : getStaffLines()) {
			jsl.render(g2);
		}
		return getWidth();
	}

	protected boolean isEmpty() {
		return m_staffLines.isEmpty();
	}

	protected void onBaseChanged() {
		voicesAligned = false;
	}

}
