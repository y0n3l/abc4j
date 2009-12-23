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

import java.awt.Font;
import java.awt.Graphics2D;

import abc.notation.MusicElement;

/**
 * TODO doc
 */
public class JText extends JScoreElementAbstract {

	protected static final short ALIGN_CENTER = 1;

	protected static final short ALIGN_LEFT = 0;

	protected static final short ALIGN_RIGHT = 2;

	private short m_fontType;

	private String m_text = null;

	private short m_textAlign;

	/**
	 * Constructor
	 * 
	 * @param mtrx
	 *            The score metrics needed
	 * @param text
	 *            The text
	 * @param fontType
	 *            One of {@link ScoreMetrics#FONT_TITLE},
	 *            {@link ScoreMetrics#FONT_SUBTITLE},
	 *            {@link ScoreMetrics#FONT_COMPOSER},
	 *            {@link ScoreMetrics#FONT_ANNOTATION},
	 *            {@link ScoreMetrics#FONT_PART_LABEL},
	 *            {@link ScoreMetrics#FONT_CHORDS},
	 *            {@link ScoreMetrics#FONT_LYRICS}...
	 */
	protected JText(ScoreMetrics mtrx, String text, short fontType) {
		this(mtrx, text, fontType, ALIGN_LEFT);
	}

	/**
	 * Constructor
	 * 
	 * @param mtrx
	 *            The score metrics needed
	 * @param text
	 *            The text
	 * @param fontType
	 *            One of {@link ScoreMetrics#FONT_TITLE},
	 *            {@link ScoreMetrics#FONT_SUBTITLE},
	 *            {@link ScoreMetrics#FONT_COMPOSER},
	 *            {@link ScoreMetrics#FONT_ANNOTATION},
	 *            {@link ScoreMetrics#FONT_PART_LABEL},
	 *            {@link ScoreMetrics#FONT_CHORDS},
	 *            {@link ScoreMetrics#FONT_LYRICS}...
	 * @param textAlign
	 *            {@link #ALIGN_LEFT}, {@link #ALIGN_CENTER},
	 *            {@link #ALIGN_RIGHT}.
	 */
	protected JText(ScoreMetrics mtrx, String text, short fontType,
			short textAlign) {
		super(mtrx);
		this.m_text = text;
		this.m_fontType = fontType;
		this.m_textAlign = textAlign;
	}

	/**
	 * Returns the height of this score element.
	 * 
	 * @return The height of this score element.
	 */
	public double getHeight() {
		return (double) getMetrics().getTextFontHeight(m_fontType);
	}

	/**
	 * Returns the tune's music element represented by this graphical score
	 * element.
	 * 
	 * @return The tune's music element represented by this graphical score
	 *         element. <TT>null</TT> if this graphical score element is not
	 *         related to any music element.
	 * @see MusicElement
	 */
	public MusicElement getMusicElement() {
		return null;
	}

	public String getText() {
		return m_text;
	}

	/** Returns the alignment */
	public short getAlignment() {
		return m_textAlign;
	}

	/**
	 * Returns the width of this score element.
	 * 
	 * @return The width of this score element.
	 */
	public double getWidth() {
		return (double) getMetrics().getTextFontWidth(m_fontType, getText());
	}

	/** Callback invoked when the base has changed for this object. */
	protected void onBaseChanged() {
		// does nothing
	}

	/**
	 * Renders this Score element to the given graphic context.
	 * 
	 * @param g2
	 */
	public double render(Graphics2D g2) {
		Font previousFont = g2.getFont();
		g2.setFont(getMetrics().getTextFont(m_fontType));
		g2
				.drawString(getText(), (int) getBase().getX(), (int) getBase()
						.getY());
		g2.setFont(previousFont);
		return getWidth();
	}

	/**
	 * Sets the text alignment
	 * 
	 * @param textAlign
	 *            {@link #ALIGN_LEFT}, {@link #ALIGN_CENTER},
	 *            {@link #ALIGN_RIGHT}.
	 */
	public void setAlignment(short textAlign) {
		this.m_textAlign = textAlign;
	}

}
