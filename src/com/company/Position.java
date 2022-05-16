package com.company;

import java.util.function.IntPredicate;

class Position {
    private final String text;
    private final int index;
    private int line;
    private final int col;

    public Position(String text) {
        this(text, 0, 1, 1);
    }

    private Position(String text, int index, int line, int col) {
        this.text = text;
        this.index = index;
        this.line = line;
        this.col = col;
    }

    public int getChar() {
        return index < text.length() ? text.codePointAt(index) : -1;
    }

    public boolean satisfies(IntPredicate p) {
        return p.test(getChar());
    }

    public Position skip() {
        int c = getChar();
        return switch (c) {
            case -1 -> this;
            case '\n' -> new Position(text, index + 1, line + 1, 1);
            default -> new Position(text, index + (c > 0xFFFF ? 2 : 1), line, col + 1);
        };
    }

    public Position skipWhile(IntPredicate p) {
        Position pos = this;
        while (pos.satisfies(p)) pos = pos.skip();
        return pos;
    }

    public String toString() {
        return String.format("(%d, %d)", line, col);
    }
}

class SyntaxError extends Exception {
    public SyntaxError(Position pos, String msg) {
        super(String.format("Syntax error at %s: %s", pos.toString(), msg));
    }
}

enum Tag {
    JSON,
    ARRAY,
    NUMBER,
    STRING,
    L_SQUARE,
    R_SQUARE,
    END_OF_TEXT,
    L_CURLY_BRACKET,
    R_CURLY_BRACKET,
    J_TAIL,
    COMMA,
    ;

    public String toString() {
        switch (this) {
            case JSON: return "identifier";
            case ARRAY: return "array";
            case NUMBER: return "number";
            case STRING: return "string";
            case L_SQUARE: return "'['";
            case R_SQUARE: return "']'";
            case L_CURLY_BRACKET: return "'{'";
            case R_CURLY_BRACKET: return "'}'";
            case J_TAIL: return "part_array";
            case COMMA: return "','";
            case END_OF_TEXT: return "end of text";
        }
        throw new RuntimeException("unreachable code");
    }
}