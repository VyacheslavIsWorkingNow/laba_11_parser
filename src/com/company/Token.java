package com.company;

import java.util.Arrays;

class Token {
    private Tag tag;
    private final Position start;
    private Position follow;

    public Token(String text) throws SyntaxError {
        this(new Position(text));
    }

    private Token(Position cur) throws SyntaxError {
        start = cur.skipWhile(Character::isWhitespace);
        follow = start.skip();
        switch (start.getChar()) {
            case -1:
                tag = Tag.END_OF_TEXT;
                break;
            case '[':
                tag = Tag.L_SQUARE;
                follow = follow.skip();
                if (follow.satisfies(Character::isLetterOrDigit) || follow.getChar() == '['
                        || follow.getChar() == '{' || follow.getChar() == '"') {
                    tag = Tag.ARRAY;
                }
                break;
            case ']':
                tag = Tag.R_SQUARE;
                break;
            case '{':
                tag = Tag.L_CURLY_BRACKET;
                follow = follow.skip();
                if (follow.satisfies(Character::isLetterOrDigit) || follow.getChar() == '['
                        || follow.getChar() == '{' || follow.getChar() == '"') {
                    tag = Tag.MAP;
                }
                break;
            case '}':
                tag = Tag.R_CURLY_BRACKET;
                break;
            case ',':
                tag = Tag.COMMA;
                if (follow.satisfies(Character::isLetterOrDigit)) {
                    tag = Tag.ARRAY;
                }
                follow = follow.skipWhile(Character::isWhitespace);
                if (follow.getChar() == '"') {
                    tag = Tag.MAP;
                    follow = follow.skipWhile(Character::isWhitespace);
                    if (follow.satisfies(Character::isDigit)) {
                        tag = Tag.STRING;
                    }
                }
                break;
            case '"':
                follow = follow.skipWhile(c -> c != '"' && c != '\n' && c != -1);
                if (follow.getChar() != '"') {
                    throw new SyntaxError(follow, "newline in string literal");
                }
                follow = follow.skip();
                tag = Tag.STRING;
                break;
            case ':':
                tag = Tag.COLON;
                break;
            default:
                if (start.satisfies(Character::isLetter)) {
                    follow = follow.skipWhile(Character::isLetterOrDigit);
                    tag = Tag.STRING;
                } else if (start.satisfies(Character::isDigit)) {
                    follow = follow.skipWhile(Character::isDigit);
                    if (follow.satisfies(Character::isLetter)) {
                        throw new SyntaxError(follow, "delimiter expected");
                    }
                    tag = Tag.NUMBER;
                } else {
                    throwError("invalid character");
                }
        }
    }

    public void throwError(String msg) throws SyntaxError {
        throw new SyntaxError(start, msg);
    }

    public boolean matches(Tag... tags) {
        return Arrays.stream(tags).anyMatch(t -> tag == t);
    }

    public Token next() throws SyntaxError {
        return new Token(follow);
    }
}
