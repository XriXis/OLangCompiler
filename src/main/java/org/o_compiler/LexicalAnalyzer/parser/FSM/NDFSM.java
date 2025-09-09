package org.o_compiler.LexicalAnalyzer.parser.FSM;

import org.o_compiler.Pair;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// todo: implement a determination for traversal optimization
public class NDFSM<T> implements FSM<T> {
    private final State start;
    private final Map<State, Pair<Integer, T>> finals;

    public static <T> NDFSM<T> fromRegex(String pattern, Pair<Integer, T> value) {
        return new PatternParser<>(pattern, value).parseExpr();
    }

    public static <T> NDFSM<T> fromRegexes(Map<String, Pair<Integer, T>> conf) {
        NDFSM<T> machine = null;
        for (var pattern : conf.keySet()) {
            if (machine == null) machine = fromRegex(pattern, conf.get(pattern));
            else machine = machine.union(NDFSM.fromRegex(pattern, conf.get(pattern)));
        }
        return machine;
    }

    public TraverseIterator<T> traverse() {
        return new _TraverseIterator<>(this);
    }

    Set<State> epsilonClosure(State s) {
        var res = new HashSet<State>();
        var order = new Stack<State>();
        order.add(s);
        res.add(s);
        while (!order.empty()) {
            var consideredState = order.pop();
            for (var th : consideredState.transitions) {
                if (th.o1 == null && res.add(th.o2)) order.add(th.o2);
            }
        }
        return res;
    }

    private NDFSM(State start, Map<State, Pair<Integer, T>> finals) {
        this.start = start;
        this.finals = finals;
    }

    private Pair<Integer, T> mostPriorFinalValue(){
        Pair<Integer, T> resultingRet = new Pair<>(Integer.MAX_VALUE, null);
        for (var f: finals.keySet()) {
            if (this.finals.get(f).o1 < resultingRet.o1) {
                resultingRet = this.finals.get(f);
            }
        }
        return resultingRet;
    }

    // Operations over FSMs according to TCS lecture book

    static <T> NDFSM<T> epsilon(Pair<Integer, T> value) {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition(null, s2);
        return new NDFSM<>(s1, Map.of(s2, value));
    }

    static <T> NDFSM<T> symbol(char c, Pair<Integer, T> value) {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition(ch -> ch == c, s2);
        return new NDFSM<>(s1, Map.of(s2, value));
    }

    static <T> NDFSM<T> wildcard(Pair<Integer, T> value) {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition(ch -> true, s2);
        return new NDFSM<>(s1, Map.of(s2, value));
    }

    static <T> NDFSM<T> charClass(Predicate<Character> pred, Pair<Integer, T> value) {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition(pred, s2);
        return new NDFSM<>(s1, Map.of(s2, value));
    }

    NDFSM<T> union(NDFSM<T> b) {
        State start = new State();
        Map<State, Pair<Integer, T>> finals = new HashMap<>();
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

    NDFSM<T> star() {
        State start = new State();
        State end = new State();
        start.addTransition(null, this.start);
        start.addTransition(null, end);
        for (State f : this.finals.keySet()) {
            f.addTransition(null, this.start);
            f.addTransition(null, end);
        }
        Map<State, Pair<Integer, T>> finals = new HashMap<>(this.finals);
        finals.put(end, mostPriorFinalValue());
        return new NDFSM<>(start, finals);
    }

    NDFSM<T> plus() {
        return concat(star());
    }

    NDFSM<T> optional() {
        return union(epsilon(mostPriorFinalValue()));
    }

    // --- Inner helper classes ---

    private static class State {
        static final State sink = new State();

        static {
            sink.addTransition(c -> true, sink);
        }

        List<Pair<Predicate<Character>, State>> transitions = new ArrayList<>();

        public void addTransition(Predicate<Character> pred, State target) {
            transitions.add(new Pair<>(pred, target));
        }

        public Set<State> feed(char ch) {
            return transitions.stream()
                    .filter(th -> th.o1 != null && th.o1.test(ch)) // keep only matching transitions
                    .map(th -> th.o2)                               // extract the target state
                    .collect(Collectors.toSet());
        }
    }

    public static class _TraverseIterator<T> implements TraverseIterator<T> {
        Set<State> current;
        NDFSM<T> entry;
        StringBuilder path;

        protected _TraverseIterator(NDFSM<T> related) {
            entry = related;
            current = related.epsilonClosure(entry.start);
            path = new StringBuilder();
        }

        public void feed(char ch) {
            current = current.stream()
                    .flatMap(item -> item.feed(ch).stream())
                    .flatMap(item -> entry.epsilonClosure(item).stream())
                    .collect(Collectors.toSet());
            if (!isEnd()) path.append(ch);
        }

        public boolean isEnd() {
            return current.isEmpty();
        }

        // todo: handle overlapping of the identifier and keyword
        public T result() {
            Pair<Integer, T> res = null;
            for (var item : current) {
                if (!entry.finals.containsKey(item)) {
                    continue;
                } else if (res == null){
                    res = entry.finals.get(item);
                } else if (res.o1 > entry.finals.get(item).o1 ) {
                    res = entry.finals.get(item);
                }
            }
            if (res==null) return null;
            return res.o2;
        }

        public String pathTaken() {
            return path.toString();
        }
    }
}
