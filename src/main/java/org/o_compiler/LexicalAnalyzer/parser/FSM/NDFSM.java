package org.o_compiler.LexicalAnalyzer.parser.FSM;

import java.util.*;
import java.util.function.Predicate;

public class NDFSM<T> {
    private final State start;
    private final Map<State, T> finals;

    public static <T> NDFSM<T> fromRegex(String pattern, T value) {
        return new PatternParser<>(pattern, value).parseExpr();
    }


    private NDFSM(State start, Map<State, T> finals) {
        this.start = start;
        this.finals = finals;
    }

    // Operations over FSMs according to TCS lecture book

    static <T> NDFSM<T> epsilon(T value) {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition(null, s2);
        return new NDFSM<>(s1, Map.of(s2, value));
    }

    static <T> NDFSM<T> symbol(char c, T value) {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition(ch -> ch == c, s2);
        return new NDFSM<>(s1, Map.of(s2, value));
    }

    static <T> NDFSM<T> charClass(Predicate<Character> pred, T value) {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition(pred, s2);
        return new NDFSM<>(s1, Map.of(s2, value));
    }

    NDFSM<T> union(NDFSM<T> b) {
        State start = new State();
        Map<State, T> finals = new HashMap<>();
        start.addTransition(null, this.start);
        start.addTransition(null, b.start);
        finals.putAll(this.finals);
        finals.putAll(b.finals);
        return new NDFSM<>(start, finals);
    }

    NDFSM<T> concat(NDFSM<T> b) {
        for (State f : this.finals.keySet()) {
            f.addTransition(null, b.start);
        }
        return new NDFSM<>(this.start, b.finals);
    }

    NDFSM<T> star(T value) {
        State start = new State();
        State end = new State();
        start.addTransition(null, this.start);
        start.addTransition(null, end);
        for (State f : this.finals.keySet()) {
            f.addTransition(null, this.start);
            f.addTransition(null, end);
        }
        // keep finals from a and add the new end state with the propagated value
        Map<State, T> finals = new HashMap<>(this.finals);
        finals.put(end, value);
        return new NDFSM<>(start, finals);
    }

    NDFSM<T> plus(T value) {
        return concat(star(value));
    }

    NDFSM<T> optional(T value) {
        return union(epsilon(value));
    }

    // --- Inner helper classes ---

    private static class State {
        List<Pair<Predicate<Character>, State>> transitions = new ArrayList<>();

        public void addTransition(Predicate<Character> pred, State target) {
            transitions.add(new Pair<>(pred, target));
        }
    }

    private record Pair<T1, T2>(T1 o1, T2 o2) { }
}
