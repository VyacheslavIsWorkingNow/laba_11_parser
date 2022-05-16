package com.company;

import java.util.Scanner;

/**
 * LL1 Grammar
 * <Json>::= [ <Array> ]
 * | { <Map> }
 * | NUMBER | STRING
 * | [ ] | { }
 * <Array> ::= <Json> <JTail>
 * <JTail> ::= , <Array> | ε
 * <Map>::= <Pair> <MTail>
 * <MTail> ::= , <Map> | ε
 * <Pair>::= STRING : <Json>
 *
 *
 * // <Json> ::= [ <Array> ] | NUMBER | STRING | [ ] | { }
 * // <Array> ::= <Json> <JTail>
 * // <JTail> ::= , <Array> | ε
 *
 * эта грамматика готова
 *
 * осталось дело за словарями
 *
 */


public class Main {

    private static Token sym;

    private static void expect(Tag tag) throws SyntaxError {
        if (!sym.matches(tag)) {
            sym.throwError(tag.toString() + " expected");
        }
        sym = sym.next();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        in.useDelimiter("\\Z");
        String text = in.next();

        try {
            sym = new Token(text);
            parse();
            System.out.println("success");
        }
        catch (SyntaxError e) {
            System.out.println(e.getMessage());
        }
    }

    private static void parse() throws SyntaxError {
        parseJson();
        expect(Tag.END_OF_TEXT);
    }

    private static void parseJson() throws SyntaxError {
        if (sym.matches(Tag.ARRAY)) {
            System.out.println("<Json> ::= [ <Array> ]");
            parseArray();
            expect(Tag.R_SQUARE);
        } else if (sym.matches(Tag.NUMBER)) {
            System.out.println("<Json> ::= NUMBER");
            sym = sym.next();
        } else if (sym.matches(Tag.STRING)) {
            System.out.println("<Json> ::= STRING");
            sym = sym.next();
        } else if (sym.matches(Tag.L_SQUARE)) {
            sym = sym.next();
            expect(Tag.R_SQUARE);
            System.out.println("<Json> ::= [ ]");
        } else if (sym.matches(Tag.L_CURLY_BRACKET)) {
            sym = sym.next();
            expect(Tag.R_CURLY_BRACKET);
            sym = sym.next();
            System.out.println("<Json> ::= { }");
        } else {
            sym.throwError("identifier, number or string expected");
        }
    }

    private static void parseJ_Tail() throws SyntaxError {
        if (sym.matches(Tag.COMMA) || sym.matches(Tag.ARRAY)) {
            System.out.println("<JTail> ::= , <Array>");
            parseArray();
        } else {
            System.out.println("<JTail> ::= ε");
        }
    }

    private static void parseArray() throws SyntaxError {
        if (sym.matches(Tag.ARRAY) || sym.matches(Tag.COMMA)) {
            System.out.println("<Array> ::= <Json> <JTail>");
            sym = sym.next();
            parseJson();
            parseJ_Tail();
        } else {
            sym.throwError("Json or J_Tail expected");
        }
    }
}


/*
Пример
{ "alpha": 1,
"beta": [10, "10", {}],
"gamma": { "x": [] }
}
[ rrr , [ 10 ] , 456789 ]
 */
