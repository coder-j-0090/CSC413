package parser;

import java.util.EnumSet;
import java.util.List;

import ast.AST;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TreeVisitor;
import exceptions.LexicalException;
import exceptions.SyntaxErrorException;
import lexer.ILexer;
import lexer.Lexer;
import lexer.daos.Token;
import lexer.daos.TokenKind;
import visitor.PrintVisitor;
import lexer.daos.TokenEnum;

public class Parser {
  private Token currentToken;
  private ILexer lexer;
  private Lexer lex;

  private EnumSet<TokenEnum> relationalOperators = EnumSet.of(
          TokenEnum.Equal,
          TokenEnum.NotEqual,
          TokenEnum.Less,
          TokenEnum.LessEqual);
  private EnumSet<TokenEnum> additionOperators = EnumSet.of(
          TokenEnum.Plus,
          TokenEnum.Minus,
          TokenEnum.Or);
  private EnumSet<TokenEnum> multiplicationOperators = EnumSet.of(
          TokenEnum.Multiply,
          TokenEnum.Divide,
          TokenEnum.And);

  public Parser(String sourceProgramPath) throws Exception
  {
    try
    {
        lex = new Lexer(sourceProgramPath);
        scan();
    }catch(Exception e)
    {
        System.out.println("*********exception********"+e.toString);
        throw e;
    };
  }

  public Parser(ILexer lexer) throws LexicalException {
    this.lexer = lexer;
    scan();
  }

  public Lexer getLex()
  {
      return lex;
  }

  private void expect(TokenEnum expected) throws SyntaxErrorException, LexicalException {
    if (this.currentToken.getTokenKind() == expected) {
      scan();
    } else {
      error(currentToken.getTokenKind(), expected);
    }
  }

  private void error(TokenKind actual, TokenKind... expected) throws SyntaxErrorException {
    throw new SyntaxErrorException(actual, expected);
  }

  private void scan() throws LexicalException {
    this.currentToken = lexer.nextToken();
  }

  private boolean match(TokenKind... kinds) {
    for (TokenKind tokenKind : kinds) {
      if (this.currentToken.getTokenKind() == tokenKind) {
        return true;
      }
    }

    return false;
  }

  public AST execute() throws SyntaxErrorException, LexicalException {
    try
    {
      return program();
    }catch(SyntaxError e)
    {
        e.print();
        throw e;
    }
  }

  private AST program() throws SyntaxErrorException, LexicalException {
    AST node = new ProgramTree();

    expect(TokenKind.Program);
    node.addChild(block());

    return node;
  }


  private AST block() throws SyntaxErrorException, LexicalException
  {

  }


  private boolean startingDeclaration() {
    return match(
        TokenKind.IntType,
        TokenKind.BooleanType);
  }

  private boolean startingStatement() {
    return match(
        TokenKind.If,
        TokenKind.While,
        TokenKind.Return,
        TokenKind.LeftBrace,
        TokenKind.Identifier);
  }


  private AST declaration() throws SyntaxErrorException, LexicalException {
    AST type = type();
    AST name = name();

    if (match(TokenKind.LeftParen)) {
      AST function = new FunctionDeclarationTree().addChild(type).addChild(name);

      function.addChild(formals());

      return function.addChild(block());
    } else {
      return new DeclarationTree().addChild(type).addChild(name);
    }
  }

  private AST type() throws LexicalException, SyntaxErrorException {
    AST node = null;

    if (match(TokenKind.IntType)) {
      node = new IntTypeTree();
    } else if (match(TokenKind.BooleanType)) {
      node = new BoolTypeTree();
    } else {
      error(currentToken.getTokenKind(), TokenKind.IntType, TokenKind.BooleanType);
    }

    scan();
    return node;
  }

  private AST name() throws LexicalException, SyntaxErrorException {
    AST node = null;

    if (match(TokenKind.Identifier)) {
      node = new IdentifierTree(currentToken);

      expect(TokenKind.Identifier);
    }

    return node;
  }

  private AST formals() throws SyntaxErrorException, LexicalException {
    AST node = new FormalsTree();

    expect(TokenKind.LeftParen);

    if (!match(TokenKind.RightParen)) {
      node.addChild(declaration());
    }
    while (!match(TokenKind.RightParen)) {
      expect(TokenKind.Comma);
      node.addChild(declaration());
    }

    expect(TokenKind.RightParen);

    return node;
  }

  private AST statement() throws SyntaxErrorException, LexicalException {

    switch (currentToken.getTokenKind()) {
      case If:
        return ifStatement();
      case While:
        return whileStatement();
      case Return:
        return returnStatement();
      case LeftBrace:
        return block();
      case Identifier:
        return assignStatement();
      default:
        error(
            currentToken.getTokenKind(),
            TokenKind.If,
            TokenKind.While,
            TokenKind.Return,
            TokenKind.LeftBrace,
            TokenKind.Identifier);
        return null;
    }
  }

  private AST ifStatement() throws SyntaxErrorException, LexicalException {
    AST node = new IfTree();

    expect(TokenKind.If);
    node.addChild(expression());
    expect(TokenKind.Then);
    node.addChild(block());
    expect(TokenKind.Else);
    node.addChild(block());

    return node;
  }

  private AST whileStatement() throws SyntaxErrorException, LexicalException {
    AST node = new WhileTree();

    expect(TokenKind.While);
    node.addChild(expression()).addChild(block());

    return node;
  }

  private AST returnStatement() throws SyntaxErrorException, LexicalException {
    AST node = new ReturnTree();

    expect(TokenKind.Return);

    node.addChild(expression());
    return node;
  }

  private AST assignStatement() throws SyntaxErrorException, LexicalException {
    AST node = new AssignmentTree();

    node.addChild(name());
    expect(TokenKind.Assign);
    node.addChild(expression());

    return node;
  }

  private AST expression() throws LexicalException, SyntaxErrorException {
    AST tree, child = simpleExpression();

    tree = getRelopTree();
    if (tree == null) {
      return child;
    }

    tree.addChild(child);
    tree.addChild(simpleExpression());

    return tree;
  }

  private AST getRelopTree() throws LexicalException {
    if (relationalOperators.contains(currentToken.getTokenKind())) {
      AST tree = new RelOpTree(currentToken);
      scan();

      return tree;
    } else {
      return null;
    }
  }

  /**
   * SE → T
   * SE → SE '+' T
   * SE → SE '-' T
   * SE → SE '|' T
   */
  private AST simpleExpression() throws LexicalException, SyntaxErrorException {
    AST tree, child = term();

    while ((tree = getAddOpTree()) != null) {
      tree.addChild(child);
      tree.addChild(term());

      child = tree;
    }

    return child;
  }

  private AST getAddOpTree() throws LexicalException {
    if (additionOperators.contains(currentToken.getTokenKind())) {
      AST tree = new AddOpTree(currentToken);
      scan();

      return tree;
    } else {
      return null;
    }
  }

  /**
   * T → F
   * T → T '*' F
   * T → T '/' F
   * T → T '&' F
   */
  private AST term() throws SyntaxErrorException, LexicalException {
    AST tree, child = factor();

    while ((tree = getMultOpTree()) != null) {
      tree.addChild(child);
      tree.addChild(factor());

      child = tree;
    }

    return child;
  }

  private AST getMultOpTree() throws LexicalException {
    if (multiplicationOperators.contains(currentToken.getTokenKind())) {
      AST tree = new MultOpTree(currentToken);
      scan();

      return tree;
    } else {
      return null;
    }
  }

  /**
   * F → '(' E ')'
   * F → <int>
   * F → NAME
   * F → NAME '(' E_LIST ')'
   */
  private AST factor() throws SyntaxErrorException, LexicalException {
    switch (currentToken.getTokenKind()) {
      case LeftParen: {
        expect(TokenKind.LeftParen);
        AST node = expression();
        expect(TokenKind.RightParen);

        return node;
      }
      case IntLit: {
        AST node = new IntTree(currentToken);
        expect(TokenKind.IntLit);

        return node;
      }
      case Identifier: {
        AST node = new IdentifierTree(currentToken);
        expect(TokenKind.Identifier);

        if (match(TokenKind.LeftParen)) {
          node = new CallTree().addChild(node);
          node.addChild(actualArguments());
        }

        return node;
      }
      default:
        error(
            currentToken.getTokenKind(),
            TokenKind.LeftParen);
        return null;
    }
  }

  /**
   * ACTUAL_ARGUMENTS → 𝜀
   * ACTUAL_ARGUMENTS → E (',' E)*
   */
  private AST actualArguments() throws SyntaxErrorException, LexicalException {
    AST node = new ActualArgumentsTree();

    expect(TokenKind.LeftParen);

    if (!match(TokenKind.RightParen)) {
      node.addChild(expression());
    }
    while (match(TokenKind.Comma) && !match(TokenKind.RightParen)) {
      expect(TokenKind.Comma);
      node.addChild(expression());
    }

    expect(TokenKind.RightParen);

    return node;
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java parser.Parser <filepath>");
      System.exit(1);
    }

    try {
      ILexer lexer = new Lexer(args[0]);
      Parser parser = new Parser(lexer);

      AST ast = parser.execute();

      System.out.println(lexer);
      System.out.println();

      PrintVisitor printVisitor = new PrintVisitor();
      printVisitor.visitProgramTree(ast);

    } catch (LexicalException e) {
      e.printStackTrace();
    } catch (SyntaxErrorException e) {
      e.printStackTrace();
    }

  }
}
