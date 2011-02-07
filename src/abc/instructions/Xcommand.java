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
package abc.instructions;

import abc.notation.Instruction;
import abc.notation.MusicElement;

/**
 * A Xcommand is a meta-comment (starting by %% in ABC format),
 * which give instructions for softwares which understand them,
 * about MIDI, printing...
 * 
 * e.g. %%MIDI progam 40
 */
//TODO create child class for specific (%%MIDI...)
public class Xcommand extends MusicElement implements Instruction, Cloneable {

	private static final long serialVersionUID = 2978365502429619781L;

	private String command;
	
	public Xcommand(String xcommand) {
		this.command = xcommand;
	}
	
	public String getCommand() {
		return command;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
