package lexer;

import lexer.daos.Symbol;
import lexer.daos.Token;
import lexer.daos.TokenEnum;
import lexer.daos.TokenKind;

public class Lexer {
    private boolean atEOF = false;
    // next character to process
    private char ch;
    private SourceReader source;

    // positions in line of current token
    private int startPosition;
    private int endPosition;
    private int lineNumber;
    private String sourceFile;

    /**
     *  Lexer constructor
     * @param sourceFile is the name of the File to read the program source from
     */

    public Lexer(String sourceFile)
    {
        this.sourceFile = sourceFile;
    }
    public Lexer(String sourceFile, boolean read) throws Exception
    {
        new TokenKind();
        source = new TokenKind(sourceFile);
        if(read) {
            ch = source.read();
        }
    }

    public Token newIdToken(String id, int startPosition, int endPosition, int lineNumber, TokenEnum kind) {
        return new Token(
                startPosition,
                endPosition,
                lineNumber,
                Symbol.symbol(id, kind)
        );
    }

    public Token newToken(String number, int startPosition, int endPosition, int lineNumber, TokenKind kind) {
        return new Token(
                startPosition,
                endPosition,
                lineNumber,
                Symbol.symbol(number, kind)
        );
    }

    public Token makeToken(String s, int startPosition, int endPosition, int lineNumber) {
        // filter comments
        if(s.equals("//")) {
            try {
                int oldLine = source.getLineno();

                do {
                    ch = source.read();
                } while(oldLine == source.getLineno());
            } catch (Exception e) {
                atEOF = true;
            }

            return nextToken();
        }

        // ensure it's a valid token
        Symbol sym = Symbol.symbol(s, Tokens.BogusToken);

        if( sym == null ) {
            System.out.println("******** illegal character: " + s);
            atEOF = true;
            return nextToken();
        }

        return new Token(startPosition, endPosition, lineNumber, sym);
    }

    /**
     *  @return the next Token found in the source file
     */
    public Token nextToken() {
        // ch is always the next char to process
        if(atEOF) {
            if(source != null) {
                source.close();
                source = null;
            }

            return null;
        }

        try {
            // scan past whitespace
            while(Character.isWhitespace(ch)) {
                ch = source.read();
            }
        } catch(Exception e) {
            atEOF = true;
            return nextToken();
        }

        startPosition = source.getPosition();
        endPosition = startPosition - 1;
        lineNumber = source.getLineno();

        if(Character.isJavaIdentifierStart(ch)) {
            // return tokens for ids and reserved words
            String id = "";

            try {
                do {
                    endPosition++;
                    id += ch;
                    ch = source.read();
                } while(Character.isJavaIdentifierPart(ch));
            } catch(Exception e) {
                atEOF = true;
            }

            TokenEnum kind = TokenEnum.Identifier;
            if(id.equals("number")) {
                kind = TokenEnum.Number;
            } else if(id.equals("scientific")) {
                kind = TokenEnum.Scientific;
            }

            return newIdToken(id, startPosition, endPosition, lineNumber, TokenEnum.Identifier);
        }

        if(Character.isDigit(ch)) {
            // return number tokens

            String number = "";
            TokenEnum token = TokenEnum.INTeger;

            try {
                do {
                    endPosition++;
                    number += ch;
                    ch = source.read();
                } while(Character.isDigit(ch));
            } catch(Exception e) {
                atEOF = true;
            }

            try {
                if('.' == ch) {
                    endPosition++;
                    number += ch;
                    ch = source.read();
                    do {
                        endPosition++;
                        number += ch;
                        ch = source.read();
                    } while(Character.isDigit(ch));

                    if(isNumberLit(number)) {
                        token = TokenEnum.NumberLit;
                    }
                }
            } catch(Exception e) {
                atEOF = true;
            }

            try {
                if('e' == ch) {
                    endPosition++;
                    number += ch;
                    ch = source.read();

                    if('+' == ch || '-' == ch) {
                        endPosition++;
                        number += ch;
                        ch = source.read();
                    }

                    do {
                        endPosition++;
                        number += ch;
                        ch = source.read();
                    } while(Character.isDigit(ch));

                    if(isScientificLit(number)) {
                        token = TokenEnum.ScientificLit;
                    }
                }
            } catch(Exception e) {
                atEOF = true;
            }

            return newToken(number, startPosition, endPosition, lineNumber, token);
        }

        // At this point the only tokens to check for are one or two
        // characters; we must also check for comments that begin with
        // 2 slashes
        String charOld = "" + ch;
        String op = charOld;
        Symbol sym;
        try {
            endPosition++;
            ch = source.read();
            op += ch;

            // check if valid 2 char operator; if it's not in the symbol
            // table then don't insert it since we really have a one char
            // token
            sym = Symbol.symbol(op, TokenEnum.BogusToken);
            if (sym == null) {
                // it must be a one char token
                return makeToken(charOld, startPosition, endPosition, lineNumber);
            }

            endPosition++;
            ch = source.read();

            return makeToken(op, startPosition, endPosition, lineNumber);
        } catch( Exception e ) { /* no-op */ }

        atEOF = true;
        if(startPosition == endPosition) {
            op = charOld;
        }

        return makeToken(op, startPosition, endPosition, lineNumber);
    }

    private boolean isNumberLit(String number) {
        return number.matches("^\\d*\\.\\d+|\\d+\\.\\d*$");
    }

    private boolean isScientificLit(String number) {
        return number.matches("^[+-]?(\\d+\\.\\d+|\\d+\\.|\\.\\d+|\\d+)([eE][+-]?\\d+)?$");
    }

    public static void main(String args[]) {
        Token token;
        String filename;
        Lexer lex = null;

        if(args.length != 1) {
            System.out.println("usage: java lexer.Lexer filename.x");
        } else {
            filename = args[0];

            try {
                lex = new Lexer(filename, true);

                while(!lex.atEOF) {
                    token = lex.nextToken();
                    token.print();
                }
            } catch (Exception e) {

            }

            try {
                lex = new Lexer(filename, false);
                lex.source.printFile();
            } catch (Exception e) {

            }
        }
    }
}
