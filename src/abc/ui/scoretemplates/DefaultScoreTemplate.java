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
package abc.ui.scoretemplates;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Hashtable;

import abc.ui.swing.ScoreTemplate;

/**
 * Default score template prints
 * <ul><li>At top center: title and subtitles
 * <li>At top right: composer and origin (area)
 * <li>At top left: lyricist, rhythm, group, parts order
 * <li>At bottom: source, transcriber notes, annotations and file url
 * </ul>
 * 
 * Texts styles (bold, italic) and sizes are defined.<br>
 * File url is blue (not underline).<br>
 * Main font family is Palatino Linotype, which is a bit wider,
 * easier to read (especially for lyrics). Arial and Dialog are
 * used in case of lack of Palatino.
 */
public class DefaultScoreTemplate extends ScoreTemplate implements Cloneable {

	private static final byte[] FOOTNOTES = new byte[] {
		TextFields.SOURCE,
		TextFields.TRANSCRNOTES,
		TextFields.NOTES, //=ANNOTATIONS
		TextFields.FILEURL
	};

	private static final long serialVersionUID = -556559340776215872L;

	public DefaultScoreTemplate() {
		super();
		positions();
		fonts();
	}
	
	private void fonts() {
		//All fonts are Palatino, then Arial, then Dialog, in
		//order of preference
		setDefaultTextFontFamilyName(
			new String[] {"Palatino Linotype", "Arial", "Dialog"}
		);
		//Part Labels
		setTextFontFamilyName(TextFields.PART_LABEL,
			new String[] {"Georgia", "Verdana", "Dialog"});
		//foot notes
		setTextFontFamilyName(FOOTNOTES,
			new String[] {"Arial", "Dialog"}
		);
		
		setTextStyle(
			new byte[] {TextFields.TITLE,
						TextFields.SUBTITLE,
						TextFields.COMPOSER,
						TextFields.LYRICIST,
						TextFields.RHYTHM,
						TextFields.ORIGIN},
			Font.BOLD
		);
		setTextStyle(TextFields.PARTS_ORDER, Font.ITALIC);
		setTextStyle(TextFields.PART_LABEL, Font.BOLD);
		
		setTextSize(TextFields.TITLE, 200, SizeUnit.PERCENT);
		setTextSize(TextFields.SUBTITLE, 150, SizeUnit.PERCENT);
		setTextSize(TextFields.COMPOSER, 125, SizeUnit.PERCENT);
		setTextSize(TextFields.PART_LABEL, 150, SizeUnit.PERCENT);
		setTextSize(FOOTNOTES, 10, SizeUnit.PT);
		
		Hashtable urlLink = new Hashtable();
		//urlLink.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
		urlLink.put(TextAttribute.FOREGROUND, Color.BLUE);
		setTextAttributes(TextFields.FILEURL, urlLink);
		setTextStyle(TextFields.FILEURL, Font.ITALIC);
	}
	
	private void positions() {
		setPosition(
			new byte[] { TextFields.TITLE,
						 TextFields.SUBTITLE },
			VerticalAlign.TOP,
			HorizontalAlign.CENTER
		);
		setPosition(
			new byte[] { TextFields.COMPOSER,
						 TextFields.ORIGIN },
			VerticalAlign.TOP,
			HorizontalAlign.RIGHT
		);
		setPosition(
			new byte[] { TextFields.LYRICIST,
						 TextFields.RHYTHM,
						 TextFields.GROUP,
						 TextFields.PARTS_ORDER },
			VerticalAlign.TOP,
			HorizontalAlign.LEFT
		);
		
		setPosition(
			new byte[] { TextFields.CHORDS,
						 TextFields.TEMPO,
						 TextFields.PART_LABEL },
			VerticalAlign.ABOVE_STAFF,
			HorizontalAlign.LEFT
		);
		
		setPosition(FOOTNOTES,
			VerticalAlign.BOTTOM,
			HorizontalAlign.LEFT
		);
	}
	
}
