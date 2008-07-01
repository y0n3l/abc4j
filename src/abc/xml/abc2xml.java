package abc.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import abc.notation.AccidentalType;
import abc.notation.BarLine;
import abc.notation.KeySignature;
//import abc.notation.Measure;
import abc.notation.MultiNote;
import abc.notation.MusicElement;
import abc.notation.Note;
import abc.notation.RepeatBarLine;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tune.Music;

public class abc2xml {
	
	protected static final String SCORE_PARTWISE_TAG = "score-partwise";
	protected static final String PART_LIST_TAG = "part-list";
	protected static final String SCORE_PART_TAG = "score-part";
	protected static final String PART_NAME_TAG = "part-name";
	protected static final String PART_TAG = "part";
	protected static final String MEASURE_TAG = "measure";
	protected static final String ATTRIBUTES_TAG = "attributes";
	protected static final String DIVISIONS_TAG = "divisions";
	protected static final String CLEF_TAG = "clef";
	protected static final String SIGN_TAG = "sign";
	protected static final String LINE_TAG = "line";
	//protected static final String DIVISION_TAG = "division";
		
	protected static final String NOTE_TAG = "note";
	protected static final String CHORD_TAG = "chord";
	protected static final String PITCH_TAG = "pitch";
	protected static final String REST_TAG = "rest";
	protected static final String STEP_TAG = "step";
	protected static final String OCTAVE_TAG = "octave";
	protected static final String DURATION_TAG = "duration";
	protected static final String DOTS_TAG = "dots";
	protected static final String TYPE_TAG = "type";
	protected static final String KEY_TAG = "key";
	protected static final String FIFTHS_TAG = "fifths";
	protected static final String TIME_TAG = "time";
	protected static final String BEATS_TAG = "beats";
	protected static final String BEAT_TYPE_TAG = "beat-type";
	protected static final String BAR_LINE_TAG = "barline";
	protected static final String REPEAT_TAG = "repeat";
	protected static final String ACCIDENTAL_TAG = "accidental";
	
	protected static final String BEAM_TAG = "beam";
	
	protected static final String ID_ATTRIBUTE = "id";
	protected static final String NUMBER_ATTRIBUTE = "number";
	protected static final String LOCATION_ATTRIBUTE = "location";
	protected static final String DIRECTION_ATTRIBUTE = "direction";
	
	protected static final int DIVISIONS_PER_QUARTER_NOTE = 32;

	/** C major */
	protected static final byte[] KEY_NO_ACCIDENTAL = 
		{AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, 
		AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL };
	/** G major */
	protected static final byte[] KEY_SHARP_1ST = 
		{AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.SHARP,	
		AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL };
	/** D major */
	protected static final byte[] KEY_SHARP_2ND = 
		{AccidentalType.SHARP, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.SHARP,	
		AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL };
	/** A major */
	protected static final byte[] KEY_SHARP_3RD = 
		{AccidentalType.SHARP, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.SHARP,	
		AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL };
	/** E major */
	protected static final byte[] KEY_SHARP_4TH = 
		{AccidentalType.SHARP, AccidentalType.SHARP, AccidentalType.NATURAL, AccidentalType.SHARP,	
		AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL };
	/** B major */
	protected static final byte[] KEY_SHARP_5TH = 
		{AccidentalType.SHARP, AccidentalType.SHARP, AccidentalType.NATURAL, AccidentalType.SHARP,	
		AccidentalType.SHARP,	AccidentalType.SHARP, AccidentalType.NATURAL };
	/** F# major */
	protected static final byte[] KEY_SHARP_6TH = 
		{AccidentalType.SHARP, AccidentalType.SHARP, AccidentalType.SHARP, AccidentalType.SHARP,	
		AccidentalType.SHARP,	AccidentalType.SHARP, AccidentalType.NATURAL };
	/** C# major */
	protected static final byte[] KEY_SHARP_7TH = 
		{AccidentalType.SHARP, AccidentalType.SHARP, AccidentalType.SHARP, AccidentalType.SHARP,	
		AccidentalType.SHARP,	AccidentalType.SHARP, AccidentalType.SHARP };

	
	/** F major */
	protected static final byte[] KEY_FLAT_1ST = 
		{AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL,	
		AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.FLAT};
	/** Bb major */
	protected static final byte[] KEY_FLAT_2ND = 
		{AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.FLAT, AccidentalType.NATURAL,	
		AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.FLAT};
	/** Eb major */
	protected static final byte[] KEY_FLAT_3RD = 
		{AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.FLAT, AccidentalType.NATURAL,	
		AccidentalType.NATURAL, AccidentalType.FLAT, AccidentalType.FLAT};
	/** Ab major */
	protected static final byte[] KEY_FLAT_4TH = 
		{AccidentalType.NATURAL, 	AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.NATURAL,	
		AccidentalType.NATURAL, AccidentalType.FLAT, AccidentalType.FLAT};
	/** Db major */
	protected static final byte[] KEY_FLAT_5TH = 
		{AccidentalType.NATURAL, 	AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.NATURAL,	
		AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.FLAT};
	/** Gb major */
	protected static final byte[] KEY_FLAT_6TH = 
		{AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.NATURAL,	
		AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.FLAT};
	/** Cb major */
	protected static final byte[] KEY_FLAT_7TH = 
		{AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.FLAT,	
		AccidentalType.FLAT, AccidentalType.FLAT, AccidentalType.FLAT};
	
	/**
	 * Writes the specified tune to the specified file as MusicXML.
	 * @param file A file.
	 * @param tune A tune.
	 * @throws IOException Thrown if the file cannot be created.
	 */
	public static void writeAsMusicXML(Tune tune, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		Document doc = createMusicXmlDOM(tune);
		//dumpDOM(doc);
		writeAsMusicXML(doc, writer);
		writer.flush();
		writer.close();
	}
	
	/**
	 * Writes the specified Node to the given writer.
	 * @param node A DOM node.
	 * @param writer A stream writer.
	 * @throws IOException Thrown if the file cannot be created.
	 */
	public static void writeAsMusicXML(Node node, BufferedWriter writer) throws IOException {
		/*writer.write("<"+node.getNodeName());
		NamedNodeMap attr = node.getAttributes();
		if (attr!=null)
			for (int i=0; i<attr.getLength(); i++)
				writer.write(" " + attr.item(i).getNodeName() + "=" + attr.item(i).getNodeValue());
		writer.write(">");
		writer.newLine();
		NodeList nlist =  node.getChildNodes();
		for (int i=0; i<nlist.getLength(); i++)
			writeAsMusicXML(writer, nlist.item(i));
		writer.write("</"+node.getNodeName()+">");
		writer.newLine();*/
		try {
	    TransformerFactory transfac = TransformerFactory.newInstance();
	    Transformer trans = transfac.newTransformer();
	    //trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    trans.setOutputProperty(OutputKeys.INDENT, "yes");
	    trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "test 2 doctype");

	    //create string from xml tree
	    //StringWriter sw = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    DOMSource source = new DOMSource(node);
	    
	    trans.transform(source, result);
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    //String xmlString = sw.toString();
	//}
	}
	
	
	/** Creates a DOM representation (fullfilling musicXML dtd) of the specified
	 * tune. 
	 * @param tune A tune.
	 * @return A DOM representation (fullfilling musicXML dtd) of the specified
	 * tune. */
	public static Document createMusicXmlDOM(Tune tune) {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement(SCORE_PARTWISE_TAG);
			doc.appendChild(root);
			doc.setXmlVersion("1.0");
			Element partListEl = doc.createElement(PART_LIST_TAG);
			Element scorePartEl = doc.createElement(SCORE_PART_TAG);
			scorePartEl.setAttribute(ID_ATTRIBUTE, "1");
			Element partNameEl = doc.createElement(PART_NAME_TAG);
			
			Element partEl = doc.createElement(PART_TAG);
			partEl.setAttribute(ID_ATTRIBUTE, "1");

			partNameEl.appendChild(doc.createTextNode(tune.getTitles()[0]));
			scorePartEl.appendChild(partNameEl);
			partListEl.appendChild(scorePartEl);
			root.appendChild(partListEl);
			
			root.appendChild(partEl);
			
			//Music music = tune.getMusic();
			convert(doc, tune.getMusic(), partEl);
			
			/*int measureNb = tune.getMusic().countMeasures();
			for (int i=1; i<=measureNb; i++) {
				Measure meas = tune.getMusic().getMeasure(i);
				partEl.appendChild(convert(doc, meas, i));
			}*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	protected static void appendTo(Element measureElement, TimeSignature time, Document context) {
		Element attributeEl = (Element)measureElement.getElementsByTagName(ATTRIBUTES_TAG).item(0);
		if (attributeEl==null) {
			/*attributeEl = context.createElement(ATTRIBUTES_TAG);
			Element divisionEl = context.createElement(DIVISIONS_TAG);
			divisionEl.appendChild(context.createTextNode(new Integer(DIVISIONS_PER_QUARTER_NOTE).toString()));
			attributeEl.appendChild(divisionEl);
			measureElement.appendChild(attributeEl);
			Element clefEl = context.createElement(CLEF_TAG);
			Element signEl = context.createElement(SIGN_TAG);
			Element linEl = context.createElement(LINE_TAG);
			clefEl.appendChild(signEl);
			clefEl.appendChild(linEl);
			attributeEl.appendChild(clefEl);
			measureElement.insertBefore(attributeEl, measureElement.getFirstChild());*/
			attributeEl = createMeasureGeneralAttributes(context);
			measureElement.insertBefore(attributeEl, measureElement.getFirstChild());
		}
		attributeEl.appendChild(convert(context, time));
	}
	
	protected static void appendTo(Element measureElement, KeySignature key, Document context) {
		Element attributeEl = (Element)measureElement.getElementsByTagName(ATTRIBUTES_TAG).item(0);
		if (attributeEl==null) {
			attributeEl = createMeasureGeneralAttributes(context);
			measureElement.insertBefore(attributeEl, measureElement.getFirstChild());
		}
		attributeEl.appendChild(convert(context,key));
	}
	
	protected static Element createMeasureGeneralAttributes(Document context) {
		Element attributeEl = context.createElement(ATTRIBUTES_TAG);
		Element divisionEl = context.createElement(DIVISIONS_TAG);
		divisionEl.appendChild(context.createTextNode(new Integer(DIVISIONS_PER_QUARTER_NOTE).toString()));
		attributeEl.appendChild(divisionEl);
		Element clefEl = context.createElement(CLEF_TAG);
		Element signEl = context.createElement(SIGN_TAG);
		Element linEl = context.createElement(LINE_TAG);
		clefEl.appendChild(signEl);
		clefEl.appendChild(linEl);
		attributeEl.appendChild(clefEl);
		return attributeEl;
	}
	
	protected static void convert (Document doc, Music music, Element musicElement) {
		int measureNb = 1;
		Element currentMeasureEl = doc.createElement(MEASURE_TAG);
		int addedMusicElement = 0;
		musicElement.appendChild(currentMeasureEl);
		//Element lastShorterThanQuarterNote = null;
		//KeySignature key = null;
		//TimeSignature time = null;
		currentMeasureEl.setAttribute(NUMBER_ATTRIBUTE, new Integer(measureNb).toString());
		for (int i=0; i<music.size(); i++) {
			MusicElement current = (MusicElement) music.elementAt(i);
			if(music.elementAt(i) instanceof Note) {
					Note note = (Note)music.elementAt(i);
					Element noteElement = convert(doc, note);
					/*if (note.getStrictDuration()<Note.QUARTER) {
						if (lastShorterThanQuarterNote==null) {
							//this is the first note of a group
							lastShorterThanQuarterNote = noteElement;
							Element beamNode = doc.createElement(BEAM_TAG);
							beamNode.setAttribute(NUMBER_ATTRIBUTE, "1");
							Node text = doc.createTextNode("begin");
							beamNode.appendChild(text);
							noteElement.appendChild(beamNode);
						}
						else {
							//this is part of a previously created beam 
							lastShorterThanQuarterNote = noteElement;
							Element beamNode = doc.createElement(BEAM_TAG);
							beamNode.setAttribute(NUMBER_ATTRIBUTE, "1");
							//noteElement.appendChild(lastShorterThanQuarterNote);
							Node text = doc.createTextNode("continue");
							beamNode.appendChild(text);
							noteElement.appendChild(beamNode);
						}
					}*/
					currentMeasureEl.appendChild(noteElement);
					addedMusicElement++;
				}
				else
					if(music.elementAt(i) instanceof MultiNote) {
						Node[] nodes = convert(doc, (MultiNote)music.elementAt(i));
						for (int j=0; j<nodes.length; j++)
							currentMeasureEl.appendChild(nodes[j]);
						addedMusicElement++;
					}
					else
						if(music.elementAt(i) instanceof TimeSignature) {
							//time = (TimeSignature)music.elementAt(i);
							appendTo(currentMeasureEl, (TimeSignature)music.elementAt(i), doc);
						}
						else
							if(music.elementAt(i) instanceof KeySignature) {
								//key = (KeySignature)music.elementAt(i);
								appendTo(currentMeasureEl, (KeySignature)music.elementAt(i), doc);
							}
							else {
								if(music.elementAt(i) instanceof BarLine) { 
									if (((BarLine)music.elementAt(i)).getType()==BarLine.REPEAT_OPEN) {
										/*<barline location="right">
							        	<repeat direction="backward"/>
							      		</barline>*/
										Element barLineNode = doc.createElement(BAR_LINE_TAG);
										barLineNode.setAttribute(LOCATION_ATTRIBUTE, "left");
										Element repeatNode =  doc.createElement(REPEAT_TAG);
										repeatNode.setAttribute(DIRECTION_ATTRIBUTE, "forward");
										barLineNode.appendChild(repeatNode);
										currentMeasureEl.appendChild(barLineNode);
									}
									else
										if (((BarLine)music.elementAt(i)).getType()==BarLine.REPEAT_CLOSE) {
											/*<barline location="right">
								        	<repeat direction="backward"/>
								      		</barline>*/
											Element barLineNode = doc.createElement(BAR_LINE_TAG);
											barLineNode.setAttribute(LOCATION_ATTRIBUTE, "right");
											Element repeatNode =  doc.createElement(REPEAT_TAG);
											repeatNode.setAttribute(DIRECTION_ATTRIBUTE, "backward");
											barLineNode.appendChild(repeatNode);
											currentMeasureEl.appendChild(barLineNode);
										}
								}
								//a bar line has been detected , do we create a new measure ?
								if (music.elementAt(i) instanceof BarLine && addedMusicElement>0) {
									currentMeasureEl = doc.createElement(MEASURE_TAG);
									measureNb++;
									currentMeasureEl.setAttribute(NUMBER_ATTRIBUTE, new Integer(measureNb).toString());
									musicElement.appendChild(currentMeasureEl);
									addedMusicElement=0;
								}
							}
							//else
								//if (mEl[i] instanceof NotesSeparator)
								//Node lastBeam =lastShorterThanQuarterNote.getElementsByTagName(BEAM_TAG).item(0); 
							
			}
	}
	
	
	protected static Node convert(Document doc, KeySignature signature) {
		Element keyEl = doc.createElement(KEY_TAG);
		Element fifthEl = doc.createElement(FIFTHS_TAG);
		byte[] acc = signature.getAccidentals();
		if (Arrays.equals(acc, KEY_NO_ACCIDENTAL))
			fifthEl.appendChild(doc.createTextNode("0"));
		else
			if (signature.hasOnlySharps()) {
				if (Arrays.equals(acc, KEY_SHARP_1ST))
					fifthEl.appendChild(doc.createTextNode("1"));
				else
					if (Arrays.equals(acc, KEY_SHARP_2ND))
						fifthEl.appendChild(doc.createTextNode("2"));
					else
						if (Arrays.equals(acc, KEY_SHARP_3RD))
							fifthEl.appendChild(doc.createTextNode("3"));
						else
							if (Arrays.equals(acc, KEY_SHARP_4TH))
								fifthEl.appendChild(doc.createTextNode("4"));
							else
								if (Arrays.equals(acc, KEY_SHARP_5TH))
									fifthEl.appendChild(doc.createTextNode("5"));
								else
									if (Arrays.equals(acc, KEY_SHARP_6TH))
										fifthEl.appendChild(doc.createTextNode("6"));
									else
										if (Arrays.equals(acc, KEY_SHARP_7TH))
											fifthEl.appendChild(doc.createTextNode("7"));
			}
			else {
				if (Arrays.equals(acc, KEY_FLAT_1ST))
					fifthEl.appendChild(doc.createTextNode("-1"));
				else
					if (Arrays.equals(acc, KEY_FLAT_2ND))
						fifthEl.appendChild(doc.createTextNode("-2"));
					else
						if (Arrays.equals(acc, KEY_FLAT_3RD))
							fifthEl.appendChild(doc.createTextNode("-3"));
						else
							if (Arrays.equals(acc, KEY_FLAT_4TH))
								fifthEl.appendChild(doc.createTextNode("-4"));
							else
								if (Arrays.equals(acc, KEY_FLAT_5TH))
									fifthEl.appendChild(doc.createTextNode("-5"));
								else
									if (Arrays.equals(acc, KEY_FLAT_6TH))
										fifthEl.appendChild(doc.createTextNode("-6"));
									else
										if (Arrays.equals(acc, KEY_FLAT_7TH))
											fifthEl.appendChild(doc.createTextNode("-7"));
			}
		keyEl.appendChild(fifthEl);
		return keyEl;
	}
	
	protected static Node convert(Document doc, TimeSignature signature) {
		Element timeEl = doc.createElement(TIME_TAG);
		Element beatsEl = doc.createElement(BEATS_TAG);
		Element beatTypeEl = doc.createElement(BEAT_TYPE_TAG);
		beatsEl.appendChild(doc.createTextNode(Integer.toString(signature.getNumerator())));
		beatTypeEl.appendChild(doc.createTextNode(Integer.toString(signature.getDenominator())));
		timeEl.appendChild(beatsEl);
		timeEl.appendChild(beatTypeEl);
		return timeEl;
	}
	
	/*
	protected static Node convert(Document doc, Measure measure, int measureNb) {
		MusicElement[] mEl = measure.getElements();
		Element measureEl = doc.createElement(MEASURE_TAG);
		//Element lastShorterThanQuarterNote = null;
		KeySignature key = null;
		TimeSignature time = null;
		measureEl.setAttribute(NUMBER_ATTRIBUTE, new Integer(measureNb).toString());
		//Node firstNoteNode = null;
		for (int i=0; i<mEl.length; i++) {
			if(mEl[i] instanceof Note) {
				Note note = (Note)mEl[i];
				Element noteElement = convert(doc, note);
				measureEl.appendChild(noteElement);
			}
			else
				if(mEl[i] instanceof MultiNote) {
					Node[] nodes = convert(doc, (MultiNote)mEl[i]);
					for (int j=0; j<nodes.length; j++)
						measureEl.appendChild(nodes[j]);
				}
				else
					if(mEl[i] instanceof TimeSignature)
						time = (TimeSignature)mEl[i];
					else
						if(mEl[i] instanceof KeySignature)
							key = (KeySignature)mEl[i];
						else
							if(mEl[i] instanceof RepeatBarLine) { 
								if (((RepeatBarLine)mEl[i]).getType()==BarLine.REPEAT_OPEN) {
									//<barline location="right">
						        	//<repeat direction="backward"/>
						      		//</barline>
									Element barLineNode = doc.createElement(BAR_LINE_TAG);
									barLineNode.setAttribute(LOCATION_ATTRIBUTE, "left");
									Element repeatNode =  doc.createElement(REPEAT_TAG);
									repeatNode.setAttribute(DIRECTION_ATTRIBUTE, "forward");
									barLineNode.appendChild(repeatNode);
									measureEl.appendChild(barLineNode);
								}
								else
									if (((RepeatBarLine)mEl[i]).getType()==BarLine.REPEAT_CLOSE) {
										//<barline location="right">
							        	//<repeat direction="backward"/>
							      		//</barline>
										Element barLineNode = doc.createElement(BAR_LINE_TAG);
										barLineNode.setAttribute(LOCATION_ATTRIBUTE, "right");
										Element repeatNode =  doc.createElement(REPEAT_TAG);
										repeatNode.setAttribute(DIRECTION_ATTRIBUTE, "backward");
										barLineNode.appendChild(repeatNode);
										measureEl.appendChild(barLineNode);
									}
							}
						//else
							//if (mEl[i] instanceof NotesSeparator)
							//Node lastBeam =lastShorterThanQuarterNote.getElementsByTagName(BEAM_TAG).item(0); 
						
		}
		if (time!=null || key!=null) {
			Element attributeEl = doc.createElement(ATTRIBUTES_TAG);
			Element divisionEl = doc.createElement(DIVISIONS_TAG);
			divisionEl.appendChild(doc.createTextNode(new Integer(DIVISIONS_PER_QUARTER_NOTE).toString()));
			attributeEl.appendChild(divisionEl);
			if (key!=null)
				attributeEl.appendChild(convert(doc, key));
			if (time!=null)
				attributeEl.appendChild(convert(doc, time));
			Element clefEl = doc.createElement(CLEF_TAG);
			Element signEl = doc.createElement(SIGN_TAG);
			Element linEl = doc.createElement(LINE_TAG);
			clefEl.appendChild(signEl);
			clefEl.appendChild(linEl);
			attributeEl.appendChild(clefEl);
			measureEl.insertBefore(attributeEl, measureEl.getFirstChild());
		}
		return measureEl;
			
	}*/
	
	protected static Node[] convert(Document doc, MultiNote chord) {
		Vector notes = chord.getNotesAsVector();
		Node[] nodes = new Node[notes.size()];
		for (int i=0; i<notes.size(); i++) {
			nodes[i] = convert(doc, (Note)notes.elementAt(i));
			if (i!=0)
				nodes[i].insertBefore(doc.createElement(CHORD_TAG), nodes[i].getFirstChild());
		}
		return nodes;
		
	}
	
	
	protected static Element convert(Document doc, Note note) {
		Element noteEl = doc.createElement(NOTE_TAG);
		Element durationEl = doc.createElement(DURATION_TAG);
		
		String stepValue = null;
		byte strictHeight = note.getStrictHeight();
		int octave = note.getOctaveTransposition();
		if (note.isRest()){
			Element rest = doc.createElement(REST_TAG);
			noteEl.appendChild(rest);
		}
		else {
			switch (strictHeight) {
				case Note.C : stepValue ="C"; break; 
				case Note.D : stepValue ="D"; break;
				case Note.E : stepValue ="E"; break;
				case Note.F : stepValue ="F"; break;
				case Note.G : stepValue ="G"; break;
				case Note.A : stepValue ="A"; break;
				case Note.B : stepValue ="B"; break;
			}
			octave=octave+4;
			String octaveValue = new Integer(octave).toString();
			//Element typeEl = doc.createElement(TYPE_TAG);
			//typeEl.appendChild(doc.createTextNode("quarter"));
			Element pitchEl = doc.createElement(PITCH_TAG);
			Element stepEl = doc.createElement(STEP_TAG);
			stepEl.appendChild(doc.createTextNode(stepValue));
			Element octaveEl = doc.createElement(OCTAVE_TAG);
			octaveEl.appendChild(doc.createTextNode(octaveValue));
		
			pitchEl.appendChild(stepEl);
			pitchEl.appendChild(octaveEl);
		
			noteEl.appendChild(pitchEl);
			
			if (note.countDots()>=1) {
				Node dot = doc.createElement(DOTS_TAG);
				noteEl.appendChild(dot);
			}
			Node type = doc.createElement(TYPE_TAG);
			Node typeValue = null;
			switch (note.getStrictDuration()) {
				case Note.SIXTY_FOURTH : typeValue = doc.createTextNode("64th"); break; 
				case Note.THIRTY_SECOND : typeValue = doc.createTextNode("32nd"); break;
				case Note.SIXTEENTH : typeValue = doc.createTextNode("16th"); break;
				case Note.EIGHTH : typeValue = doc.createTextNode("eighth"); break;
				case Note.QUARTER : typeValue = doc.createTextNode("quarter"); break;
				case Note.HALF : typeValue = doc.createTextNode("half"); break;
				case Note.WHOLE : typeValue = doc.createTextNode("whole"); break;
			}
			if (typeValue!=null) {
				type.appendChild(typeValue);
				noteEl.appendChild(type);
			}
			if (note.hasAccidental()) {
				Node acc = doc.createElement(ACCIDENTAL_TAG);
				Node accValue = null;
				switch (note.getAccidental()) {
					case AccidentalType.FLAT : accValue = doc.createTextNode("flat"); break; 
					case AccidentalType.NATURAL : accValue = doc.createTextNode("natural"); break;
					case AccidentalType.SHARP : accValue = doc.createTextNode("sharp"); break;
				}
				acc.appendChild(accValue);
				noteEl.appendChild(acc);
			}
		}
		int relDuration = note.getDuration()*DIVISIONS_PER_QUARTER_NOTE/Note.QUARTER;
		durationEl.appendChild(doc.createTextNode(new Integer(relDuration).toString()));
		noteEl.appendChild(durationEl);
		//noteEl.appendChild(typeEl);
		
		return noteEl;
		
	}
	
	
	protected static void dumpDOM(Document doc) {
		try {
		    TransformerFactory transfac = TransformerFactory.newInstance();
		    Transformer trans = transfac.newTransformer();
		    //trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    trans.setOutputProperty(OutputKeys.INDENT, "yes");
		    trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "test 2 doctype");

		    //create string from xml tree
		    //StringWriter sw = new StringWriter();
		    StreamResult result = new StreamResult(System.out);
		    DOMSource source = new DOMSource(doc);
		    trans.transform(source, result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
/////////////////
    //Output the XML

    //set up a transformer
    TransformerFactory transfac = TransformerFactory.newInstance();
    Transformer trans = transfac.newTransformer();
    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    trans.setOutputProperty(OutputKeys.INDENT, "yes");

    //create string from xml tree
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(doc);
    trans.transform(source, result);
    String xmlString = sw.toString();*/
}

