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

public class AbcParseError extends PositionableInCharStream {

	private static final long serialVersionUID = 7312611622104315679L;

	private final String errorMessage;

	private final String value;
	
	protected AbcParseError(String message, String pValue, CharStreamPosition position) {
		super(position);
		this.errorMessage = message;
		this.value = pValue;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getValue() {
		return value;
	}

}
