/**
 * 
 */
package abc.ui.scoretemplates;

/**
 * @author Administrateur
 *
 */
public abstract class TextFields {
	
	/** @see #ORIGIN */
	public static final byte AREA = 9;
	/** Book (e.g. B:O'Neills) */
	public static final byte BOOK = 2;
	/** Composer (e.g. C:Robert Jones, C:Trad.) */
	public static final byte COMPOSER = 3;
	/** Discography (e.g. D:Chieftains IV) */
	public static final byte DISCOGRAPHY = 4;
	//public static final byte ELEMSKIP = 5;
	/** Group (e.g. G:flute) */
	public static final byte GROUP = 5;
	/** History (e.g. H:This tune said to...) */
	public static final byte HISTORY = 6;
	/** Informations or instructions */
	public static final byte INFORMATIONS = 7; //or instruction
	/** Notes, Annotations (e.g. N:see also O'Neills - 234) */
	public static final byte NOTES = 8;
	/** Notes, Annotations (e.g. N:see also O'Neills - 234) */
	public static final byte ANNOTATIONS = 8;
	/** Origin and area (e.g. O:UK, Yorkshire, Bradford) */
	public static final byte ORIGIN = 9;
	/** Rhythm (e.g. R:R, R:reel) */
	public static final byte RHYTHM = 10;
	/** Source (e.g. S:collected in Brittany) */
	public static final byte SOURCE = 11;
	/** Title(s) (e.g. T:Paddy O'Rafferty) */
	public static final byte TITLE = 12;
	public static final byte SUBTITLE = 13;
	/** Transcriber and notes (e.g. Z:John Smith, j.s@aol.com) */
	public static final byte TRANSCRNOTES = 14;
	/** Words (lyrics after the tune) */
	public static final byte WORDS = 15;
	/** File URL (e.g. F:http://a.b.c/file.abc) */
	public static final byte FILEURL = 16;
	/** Lyrics' author (e.g. A:Walter Raleigh) */
	public static final byte LYRICIST = 17; //v2.0 A: field
	/** Chord names over the staff line */
	public static final byte CHORDS = 18;
	/** Lyrics under the staff line, aligned on notes */
	public static final byte LYRICS = 19;
	/** Part labels in frames over staff line */
	public static final byte PART_LABEL = 20;
	/** Part order in header */
	public static final byte PARTS_ORDER = 21;
	/** Tempo */
	public static final byte TEMPO = 22;
	
	public static String toString(byte b) {
		switch (b) {
		case ANNOTATIONS: return "annotations";
		//case AREA: cf ORIGIN
		case BOOK: return "book";
		case CHORDS: return "chords";
		case COMPOSER: return "composer";
		case DISCOGRAPHY: return "discography";
		case FILEURL: return "file url";
		case GROUP: return "group";
		case HISTORY: return "history";
		case INFORMATIONS: return "informations";
		case LYRICIST: return "lyricist";
		case LYRICS: return "lyrics";
		//case NOTES: cf ANNOTATIONS
		case ORIGIN: return "origin";
		case PART_LABEL: return "part label";
		case PARTS_ORDER: return "parts order";
		case RHYTHM: return "rhythm";
		case SOURCE: return "source";
		case SUBTITLE: return "subtitle";
		case TEMPO: return "tempo";
		case TITLE: return "title";
		case TRANSCRNOTES: return "transcriber notes";
		case WORDS: return "words";
		default: return "<unknown text field>";
		}
	}
}
