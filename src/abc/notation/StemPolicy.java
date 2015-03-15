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
package abc.notation;

/**
 * Only three constants for stem policies
 */
public abstract class StemPolicy {

	/**
	 * This policy tells to draw stems depending on note height:
	 * <ul>
	 * <li>stems up if note is lower than staff middle
	 * <li>stems down if note is higher than staff middle
	 * </ul>
	 */
	public final static byte STEMS_AUTO = 0;

	/** This policy tells to draw all stems up */
	public final static byte STEMS_DOWN = 2;

	/** This policy tells to draw all stems up */
	public final static byte STEMS_UP = 1;

	/**
	 * Ensure the given policy is correct. If not, returns {@link #STEMS_AUTO}
	 * 
	 * @param policy
	 */
	public static byte ensureCorrectPolicy(byte policy) {
		if ((policy != STEMS_UP) && (policy != STEMS_DOWN))
			return STEMS_AUTO;
		else
			return policy;
	}

}
