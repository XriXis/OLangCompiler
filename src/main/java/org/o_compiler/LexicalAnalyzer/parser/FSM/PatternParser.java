package org.o_compiler.LexicalAnalyzer.parser.FSM;

import java.util.*;
import java.util.function.Predicate;

class PatternParser<T> {
    final String pattern;
    final T value;  // the return value for accepting states

    public PatternParser(String pattern, T value) {
        this.pattern = pattern;
        this.value = value;
    }

    public NDFSM<T> parseExpr() {
        return parseExpr(0, pattern.length());
    }

    // Grammar: expr = term ('|' term)*
    // term = factor+
    // factor = base ('*' | '+' | '?')?
    // base = '(' expr ')' | char | '[' (char+ | char'-'char) ']'
    private NDFSM<T> parseExpr(int l, int r) {
        int level = 0, lastOr = -1;
        for (int i = l; i < r; i++) {
            char c = pattern.charAt(i);
            if (c == '(') level++;
            else if (c == ')') level--;
            else if (c == '|' && level == 0) lastOr = i;
        }
        if (lastOr != -1) {
            NDFSM<T> left = parseExpr(l, lastOr);
            NDFSM<T> right = parseExpr(lastOr + 1, r);
            return left.union(right);
        }
        return parseTerm(l, r);
    }

    private int nextIndex;
    private NDFSM<T> parseTerm(int l, int r) {
        NDFSM<T> result = null;
        for (int i = l; i < r; ) {
            NDFSM<T> f = parseFactor(i, r);
            i = nextIndex; // advanced by parseFactor
            result = (result == null) ? f : result.concat(f);
        }
        return result;
    }

    private NDFSM<T> parseFactor(int l, int r) {
        NDFSM<T> base;
        char c = pattern.charAt(l);

        if (c == '(') {
            int match = findClosingParen(l, r);
            base = parseExpr(l + 1, match);
            nextIndex = match + 1;

        } else if (c == '[') {
            int match = findClosingBracket(l, r);
            Predicate<Character> pred = parseCharClass(l + 1, match);
            base = NDFSM.charClass(pred, value);
            nextIndex = match + 1;

        } else {
            base = NDFSM.symbol(c, value);
            nextIndex = l + 1;
        }

        if (nextIndex < r) {
            char op = pattern.charAt(nextIndex);
            if (op == '*') { base = base.star(value); nextIndex++; }
            else if (op == '+') { base = base.plus(value); nextIndex++; }
            else if (op == '?') { base = base.optional(value); nextIndex++; }
        }
        return base;
    }

    private int findClosingParen(int l, int r) {
        int depth = 0;
        for (int i = l; i < r; i++) {
            if (pattern.charAt(i) == '(') depth++;
            else if (pattern.charAt(i) == ')') {
                depth--;
                if (depth == 0) return i;
            }
        }
        throw new IllegalArgumentException("Unbalanced parentheses in pattern");
    }

    private int findClosingBracket(int l, int r) {
        for (int i = l + 1; i < r; i++) {
            if (pattern.charAt(i) == ']') return i;
        }
        throw new IllegalArgumentException("Unclosed [..] character class");
    }

    private Predicate<Character> parseCharClass(int l, int r) {
        // parses between '[' and ']'
        List<Predicate<Character>> parts = new ArrayList<>();
        for (int i = l; i < r; i++) {
            char c = pattern.charAt(i);
            if (i + 2 < r && pattern.charAt(i + 1) == '-') {
                char end = pattern.charAt(i + 2);
                parts.add(ch -> ch >= c && ch <= end);
                i += 2; // skip the range
            } else {
                parts.add(ch -> ch == c);
            }
        }
        // merge all into one predicate
        return ch -> parts.stream().anyMatch(p -> p.test(ch));
    }
}
