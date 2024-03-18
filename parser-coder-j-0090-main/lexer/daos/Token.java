package lexer.daos;

public class Token
{
    private int leftPosition;
    private int rightPosition;
    private int lineNumber;
    private Symbol symbol;

    public Token(int leftPosition, int rightPosition, int lineNumber, Symbol sym)
    {
        this.leftPosition = leftPosition;
        this.rightPosition = rightPosition;
        this.lineNumber = lineNumber;
        this.symbol = sym;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void print() {
        String tokenString;
        if ((getKind() == TokenEnum.Identifier) || (getKind() == TokenEnum.INTeger) ||
                (getKind() == TokenEnum.NumberLit) || (getKind() == TokenEnum.ScientificLit)) {
            tokenString = toString();
        } else {
            tokenString = TokenKind.tokens.get(getKind()).toString();
        }

        System.out.format("%-11s left: %-8d right: %-8d line: %-8d %s\n",
                tokenString, getLeftPosition(), getRightPosition(),
                getLineNumber(), getKind());

        return;
    }

    public String toString() {
        return symbol.toString();
    }

    public int getLeftPosition() {
        return leftPosition;
    }

    public int getRightPosition() {
        return rightPosition;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /**
     *  @return the integer that represents the kind of symbol we have which
     *  is actually the type of token associated with the symbol
     */
    public TokenEnum getKind() {
        return symbol.getKind();
    }

    public TokenEnum getTokenKind()
    {
        return ;
    }


}
