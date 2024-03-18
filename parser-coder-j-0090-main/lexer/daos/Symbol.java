package lexer.daos;

public class Symbol
{
    private String name;
    // token kind of symbol
    private TokenEnum kind;

    private Symbol(String n, TokenEnum kind)
    {
        name = n;
        this.kind = kind;
    }

    private static java.util.HashMap<String,Symbol> symbols = new java.util.HashMap<String,Symbol>();

    public String toString()
    {
        return name;
    }

    public TokenEnum getKind()
    {
        return kind;
    }

    public static Symbol symbol(String newTokenString, TokenEnum kind) {
        Symbol sym = symbols.get(newTokenString);
        if (sym == null)
        {
            if (kind == TokenEnum.BogusToken)
            {
                return null;
            }
            sym = new Symbol(newTokenString, kind);
            symbols.put(newTokenString, sym);
        }

        return sym;
    }
}
