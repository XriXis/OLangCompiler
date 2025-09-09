package org.o_compiler.LexicalAnalyzer.parser.FSM;

import java.util.*;

public class DFSM<T> {
    private final DState start;
    private final Set<DState> states;

    public DFSM(DState start, Set<DState> states) {
        this.start = start;
        this.states = states;
    }

    public DState getStart() { return start; }
    public Set<DState> getStates() { return states; }

    // Deterministic state
    public class DState {
        private final Map<Character, DState> transitions = new HashMap<>();
        private final T value; // non-null if final

        DState(T value) {
            this.value = value;
        }

        public void addTransition(char c, DState target) {
            transitions.put(c, target);
        }

        public DState next(char c) {
            return transitions.get(c);
        }

        public boolean isFinal() {
            return value != null;
        }

        public T getValue() {
            return value;
        }
    }
}
