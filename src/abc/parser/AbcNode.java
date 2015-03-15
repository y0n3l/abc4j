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
package abc.parser;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.buffers.InputBuffer.Position;
import org.parboiled.common.StringUtils;
import org.parboiled.errors.ParseError;

import abc.parser.AbcTextReplacements;

/**
 * This is a light structure to store parsed ABC informations retrieved from a
 * {@link org.parboiled.Node} structure.
 * <p>
 * A AbcNode can have child(s), one parent, can search for ancestries and
 * descendants (childs, grand-childs...)
 * <p>
 * Informations it contains:
 * <ul>
 * <li>a label (which approximatively correspond to a
 * {@link abc.parser.AbcGrammar} method
 * <li>a value
 * <li>a position in {@link abc.parser.CharStreamPosition} object
 * <li>a parent
 * <li>zero, one or more child(s)
 * <li>zero, one or more parsing errors {@link abc.parser.AbcParseError}
 * </ul>
 */
public class AbcNode extends PositionableInCharStream {

	private static final long serialVersionUID = -8874823875159174349L;

	private static final AbcTextReplacements bundle = AbcTextReplacements.getInstance();

	private List<AbcNode> childs;

	private List<AbcParseError> errors;

	private String label;

	private AbcNode parent;

	private String value;
	
	// @SuppressWarnings("unchecked")
	@SuppressWarnings("rawtypes")
	protected AbcNode(Node node, InputBuffer parseInputBuffer,
			List<ParseError> parseErrors, AbcInputBuffer abcInputBuffer) {
		super(null);
		if (node != null) {
			this.label = node.getLabel();
			this.value = parseInputBuffer.extract(node.getStartIndex(), node
					.getEndIndex());
			Position pos = parseInputBuffer.getPosition(node.getStartIndex());
			int sourceStartIndex = abcInputBuffer.getIndex(pos);
			int sourceEndIndex = sourceStartIndex + value.length();
			setCharStreamPosition(
				new CharStreamPosition(
						pos.line,
						pos.column,
						sourceStartIndex,
						sourceEndIndex
					));
			this.childs = new ArrayList<AbcNode>(node.getChildren().size());
			for (Object n : node.getChildren()) {
				AbcNode abcn = new AbcNode((Node) n, parseInputBuffer,
						parseErrors, abcInputBuffer);
				abcn.parent = this;
				childs.add(abcn);
			}
			if (!hasError()/*(childs.size() == 0)*/ && node.hasError()) {
				this.errors = new ArrayList<AbcParseError>();
				for (ParseError pe : parseErrors) {
					String peValue = pe.getInputBuffer().extract(
							pe.getStartIndex(), pe.getEndIndex());
					String peMsg = pe.getErrorMessage();
					Position pePos = pe.getInputBuffer().getPosition(pe.getStartIndex());
					int peIndex = abcInputBuffer.getIndex(pePos);
					CharStreamPosition csp = new CharStreamPosition(
							pePos.line, pePos.column, peIndex,
							peIndex + (peValue.length()>0?peValue.length():1));
					//if ((pe.getStartIndex() >= node.getStartIndex())
					//		&& (pe.getStartIndex() </*=*/ node.getEndIndex())) {
					if ((peIndex >= sourceStartIndex)
							&& ((peIndex < sourceEndIndex)
								|| (sourceStartIndex == sourceEndIndex))
						) {
						errors.add(new AbcParseError(peMsg, peValue, csp));
					}
				}
			}
		} else {
			this.label = "AbcFile-Error";
			this.value = "";
			int nbL = parseInputBuffer.getLineCount();
			for (int i = 1; i <= nbL; i++)
				this.value += parseInputBuffer.extractLine(i)+"\n";
			this.childs = new ArrayList<AbcNode>(0);
			setCharStreamPosition(new CharStreamPosition(1, 1, 0, 1));
			if (parseErrors != null) {
				this.errors = new ArrayList<AbcParseError>();
				Iterator it = parseErrors.iterator();
				while (it.hasNext()) {
					ParseError pe = (ParseError) it.next();
					errors.add(new AbcParseError(pe.getErrorMessage(), value, getCharStreamPosition()));
				}
			}
		}
	}

	/**
	 * Returns the first child having the request label, <code>null</code> if
	 * doesn't exist.
	 * <p>
	 * Can get directly a grandchild if label contains <tt>/</tt> e.g.
	 * abcHeader.getChild("FieldNumber/DIGITS") returns the grandchild "DIGITS"
	 * if exist in FieldNumber child.
	 * 
	 * @param label One of {@link AbcTokens} constants
	 */
	public AbcNode getChild(String label) {
		if (label == null || label.equals(""))
			return null;
		String[] generation = label.split("/");
		String child = generation[0];
		String grandchild = "";
		if (generation.length > 1) {
			for (int i = 1; i < generation.length; i++) {
				grandchild += (grandchild.equals("") ? "" : "/");
				grandchild += generation[i];
			}
		}
		for (AbcNode abcn : childs) {
			if (child.equals(abcn.getLabel())) {
				if (grandchild.equals(""))
					return abcn;
				else
					return abcn.getChild(grandchild);
			}
		}
		return null;
	}
	
	public List<AbcNode> getChilds() {
		return childs;
	}

	/**
	 * Returns a list of childs having the requested label
	 * <p>
	 * Can get directly grandchilds if label contains <tt>/</tt> e.g.
	 * titleFields.getChild("FieldTitle/TexText") returns all grandchilds
	 * "TexText" in all "FieldTitle" childs.
	 */
	public List<AbcNode> getChilds(String label) {
		if (label == null || label.equals(""))
			return new ArrayList<AbcNode>(0);
		String[] generation = label.split("/");
		String child = generation[0];
		String grandchild = "";
		if (generation.length > 1) {
			for (int i = 1; i < generation.length; i++) {
				grandchild += (grandchild.equals("") ? "" : "/");
				grandchild += generation[i];
			}
		}
		List<AbcNode> ret = new ArrayList<AbcNode>(childs.size());
		for (AbcNode abcn : childs) {
			if (abcn.getLabel().equals(child)) {
				if (grandchild.equals(""))
					ret.add(abcn);
				else
					ret.addAll(abcn.getChilds(grandchild));
			}
		}
		return ret;
	}

	/**
	 * Look for child, grandchild, grand-grand-child having the requested label.
	 * When such one is found, doesn't continue search into his own childs.
	 * 
	 * @param label
	 */
	public List<AbcNode> getChildsInAllGenerations(String label) {
		if (label == null || label.equals(""))
			return new ArrayList<AbcNode>(0);
		List<AbcNode> ret = new ArrayList<AbcNode>();
		for (AbcNode abcn : childs) {
			if (abcn.getLabel().equals(label)) {
				ret.add(abcn);
			} else {
				ret.addAll(abcn.getChildsInAllGenerations(label));
			}
		}
		return ret;
	}

	/**
	 * Return the deepest childs of this node. This is useful to browse easily
	 * smallest segments of parsed text, and get closer to errors.
	 * <p>
	 * e.g. node A has childs B and C.<br>
	 * B has 3 childs D, E and F.<br>
	 * A.getDeepestChilds() returns D, E, F, C in this order.
	 * <p>
	 * If it goes too deep, you can check if node is child of B or C using
	 * {@link #isChildOf(String)} or {@link #isChildOf_or_is(String)}.
	 * 
	 * @return a List of node
	 */
	public List<AbcNode> getDeepestChilds() {
		if (childs.size() == 0) {
			return new ArrayList<AbcNode>(0);
		}
		List<AbcNode> ret = new ArrayList<AbcNode>(childs.size() * 3);
		for (AbcNode child : getChilds()) {
			if (!child.hasChilds())
				ret.add(child);
			else {
				ret.addAll(child.getDeepestChilds());
			}
		}
		return ret;
	}

	/**
	 * Returns a List of errors in all childs, grand-childs...
	 */
	public List<AbcParseError> getErrors() {
		if (hasChilds()) {
			List<AbcParseError> ret = new ArrayList<AbcParseError>(0);
			for (AbcNode abcn : getChilds()) {
				ret.addAll(abcn.getErrors());
			}
			return ret;
		} else {
			return errors != null ? errors : new ArrayList<AbcParseError>(0);
		}
	}

	/**
	 * Returns the first child, <code>null</code> if no child
	 */
	public AbcNode getFirstChild() {
		if (hasChilds())
			return (AbcNode) childs.get(0);
		else
			return null;
	}

	/**
	 * Returns the value parsed into integer if label is DIGIT or DIGITS, else
	 * -1.
	 */
	protected int getIntValue() {
		if (label.equals(AbcTokens.DIGIT)
				|| label.equals(AbcTokens.DIGITS)) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException nfe) {
				return -1;
			}
		}
		else
			return -1;
	}

	/**
	 * Returns the node label (token name)
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get direct parent, <code>null</code> if this node is the
	 * root.
	 */
	public AbcNode getParent() {
		return parent;
	}

	/**
	 * Returns the value parsed into short if label is DIGIT or DIGITS, else -1.
	 */
	protected short getShortValue() {
		if (label.equals(AbcTokens.DIGIT)
				|| label.equals(AbcTokens.DIGITS)) {
			try {
				return Short.parseShort(value);
			} catch (NumberFormatException nfe) {
				return -1;
			}
		} else
			return -1;
	}

	/** Returns the textual value of this node, transforms all
	 * escaped chars (e.g. <TT>\'i</TT> => <TT>í</TT>)
	 */
	public String getTexTextValue() {
		String text = value;
		if ((text != null) && (text.trim().length() > 0)) {
			Enumeration<String> e = bundle.getKeys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if (text.indexOf(key) != -1)
					text = stringReplace(text, key, bundle.getString(key));
			}
		}
		return text;
	}
	  
	/** Returns the textual value of this node
	 * <p>
	 * If this node is labelled "TexText",
	 * use {@link #getTexTextValue()} which transforms all
	 * escaped chars (e.g. <TT>\'i</TT> => <TT>í</TT>)
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns a list of childs having the requested label and an non-empty
	 * value, and no error.
	 * <p>
	 * Can get directly grandchilds if label contains <tt>/</tt> e.g.
	 * titleFields.getChild("FieldTitle/TexText") returns all grandchilds
	 * "TexText" in all "FieldTitle" childs.
	 */
	public List<AbcNode> getValuedChilds(String label) {
		List<AbcNode> ret = getChilds(label);
		Iterator<AbcNode> it = ret.iterator();
		while (it.hasNext()) {
			AbcNode node = it.next();
			if (node.hasError()
				|| (node.getValue() == null)
				|| (node.getValue().length() == 0))
				it.remove();
		}
		return ret;
	}

	/**
	 * Returns true if have the request label.
	 * 
	 * Can check directly a grandchild if label contains / e.g.
	 * abcHeader.hasChild("FieldNumber/DIGITS")
	 * 
	 * @param label One of {@link AbcTokens} constants
	 * @return this is a shortcut of {@link #getChild(String)} != null
	 */
	public boolean hasChild(String label) {
		return getChild(label) != null;
	}

	public boolean hasChilds() {
		return childs.size() > 0;
	}

	public boolean hasError() {
		if (hasChilds()) {
			for (AbcNode abcn : getChilds()) {
				if (abcn.hasError())
					return true;
			}
			return false;
		} else {
			return errors != null ? (errors.size() > 0) : false;
		}
	}

	/**
	 * Check if this node has parent, i.e. is not the root of
	 * the structure
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Checks if the AbcNode is labelled with requested token
	 * <p>
	 * This is a shortcut to getLabel().equals("MyLabel")
	 * 
	 * @param label One of {@link AbcTokens} constants
	 */
	public boolean is(String label) {
		if (label != null)
			return label.equals(getLabel());
		else
			return false;
	}

	/**
	 * If has parent, checks if the parent node is labelled with
	 * requested token. i.e. checks if this node is the child,
	 * grandchild... of a node labelled <TT>label</TT>
	 * 
	 * @param label One of {@link AbcTokens} constants
	 */
	public boolean isChildOf(String label) {
		if (label == null)
			return false;
		else {
			if (hasParent()) {
				if (getParent().is(label))
					return true;
				else
					return getParent().isChildOf(label);
			} else {
				return false;
			}
		}
	}

	/**
	 * This is a shortcut for {@link #is(String)} ||
	 * {@link #isChildOf(String)}.
	 * <p>
	 * Checks if this node or one of its ancestors is labelled
	 * <TT>label</TT>
	 * @param label One of {@link AbcTokens} constants
	 */
	public boolean isChildOf_or_is(String label) {
		return is(label) || isChildOf(label);
	}
	
	/** String.replace(String arg0, String arg1) in java 1.5 */
	private String stringReplace(String text, String target, String replacement) {
		return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
				text).replaceAll(replacement);
	}

	// @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(getLabel());
		sb.append(']');
		if (hasError())
			sb.append('E');
		return StringUtils.escape(sb.toString());
	}

}
