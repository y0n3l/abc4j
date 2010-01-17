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
package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class NthRepeatDefinition extends AutomataDefinition
{
    public NthRepeatDefinition()
    {
        State state = new State(AbcTokenType.UNKNOWN, false);
        getStartingState().addTransition(new Transition(state,'['));
        State state1 = new State(AbcTokenType.NTH_REPEAT, true);
        char[] chars = {'1', '2'};
        Transition trans = new Transition(state1,chars);
        state.addTransition(trans);

        State state2 = new State(AbcTokenType.UNKNOWN, false);
        getStartingState().addTransition(new Transition(state2, '|'));
        State state3 = new State(AbcTokenType.NTH_REPEAT, true);
        state2.addTransition(new Transition(state3,'1'));


        State state4 = new State(AbcTokenType.UNKNOWN, false);
        State state5 = new State(AbcTokenType.UNKNOWN, false);
        State state6 = new State(AbcTokenType.NTH_REPEAT, true);
        getStartingState().addTransition(new Transition(state4,':'));
        state4.addTransition(new Transition(state5,'|'));
        state5.addTransition(new Transition(state6,'2'));

    }
}

