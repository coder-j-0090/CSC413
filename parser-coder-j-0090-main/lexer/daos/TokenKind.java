package lexer.daos;

public class TokenKind
{
    private String check;
    public static java.util.HashMap<TokenEnum,Symbol> tokens = new java.util.HashMap<TokenEnum,Symbol>();
    public TokenKind() {
        tokens.put(TokenEnum.Program, Symbol.symbol("program",TokenEnum.Program));
        tokens.put(TokenEnum.Int, Symbol.symbol("int",TokenEnum.Int));
        tokens.put(TokenEnum.BOOLean, Symbol.symbol("boolean",TokenEnum.BOOLean));
        tokens.put(TokenEnum.If, Symbol.symbol("if",TokenEnum.If));
        tokens.put(TokenEnum.Then, Symbol.symbol("then",TokenEnum.Then));
        tokens.put(TokenEnum.Else, Symbol.symbol("else",TokenEnum.Else));
        tokens.put(TokenEnum.While, Symbol.symbol("while",TokenEnum.While));
        tokens.put(TokenEnum.Function, Symbol.symbol("function",TokenEnum.Function));
        tokens.put(TokenEnum.Return, Symbol.symbol("return",TokenEnum.Return));
        tokens.put(TokenEnum.Identifier, Symbol.symbol("<id>",TokenEnum.Identifier));
        tokens.put(TokenEnum.INTeger, Symbol.symbol("<int>",TokenEnum.INTeger));
        tokens.put(TokenEnum.LeftBrace, Symbol.symbol("{",TokenEnum.LeftBrace));
        tokens.put(TokenEnum.RightBrace, Symbol.symbol("}",TokenEnum.RightBrace));
        tokens.put(TokenEnum.LeftParen, Symbol.symbol("(",TokenEnum.LeftParen));
        tokens.put(TokenEnum.RightParen, Symbol.symbol(")",TokenEnum.RightParen));
        tokens.put(TokenEnum.Comma, Symbol.symbol(",",TokenEnum.Comma));
        tokens.put(TokenEnum.Assign, Symbol.symbol("=",TokenEnum.Assign));
        tokens.put(TokenEnum.Equal, Symbol.symbol("==",TokenEnum.Equal));
        tokens.put(TokenEnum.NotEqual, Symbol.symbol("!=",TokenEnum.NotEqual));
        tokens.put(TokenEnum.Less, Symbol.symbol("<",TokenEnum.Less));
        tokens.put(TokenEnum.LessEqual, Symbol.symbol("<=",TokenEnum.LessEqual));
        tokens.put(TokenEnum.Plus, Symbol.symbol("+",TokenEnum.Plus));
        tokens.put(TokenEnum.Minus, Symbol.symbol("-",TokenEnum.Minus));
        tokens.put(TokenEnum.Or, Symbol.symbol("|",TokenEnum.Or));
        tokens.put(TokenEnum.And, Symbol.symbol("&",TokenEnum.And));
        tokens.put(TokenEnum.Multiply, Symbol.symbol("*",TokenEnum.Multiply));
        tokens.put(TokenEnum.Divide, Symbol.symbol("/",TokenEnum.Divide));
        tokens.put(TokenEnum.Comment, Symbol.symbol("//",TokenEnum.Comment));
        tokens.put(TokenEnum.Greater, Symbol.symbol(">",TokenEnum.Greater));
        tokens.put(TokenEnum.GreaterEqual, Symbol.symbol(">=",TokenEnum.GreaterEqual));
        tokens.put(TokenEnum.SortaClose, Symbol.symbol("<>",TokenEnum.SortaClose));
        tokens.put(TokenEnum.NumberLit, Symbol.symbol("<d+.d+>",TokenEnum.NumberLit));
        tokens.put(TokenEnum.ScientificLit, Symbol.symbol("<d.dd?[Ee][+-]d+>",TokenEnum.ScientificLit));
        tokens.put(TokenEnum.Number, Symbol.symbol("number",TokenEnum.Number));
        tokens.put(TokenEnum.Scientific, Symbol.symbol("scientific",TokenEnum.Scientific));
    }
    public TokenKind(String check)
    {
        for(TokenEnum te : tokens.keySet())
        {
            if(te.equals(check))
            {
                this.check = check;
            }
        }
    }
}
