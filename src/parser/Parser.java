package parser;

import ast.*;
import lexer.Lexer;
import lexer.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

/*
<program> ::= <Exp>

<Exp> ::= <ident> | <numeral> | '<letter>' | () | true | false | nil |
          ( <Exp> ) | <unopr> <Exp> | <Exp> <binopr> <Exp> |
          if <Exp> then <Exp> else <Exp> |
          lambda <param> . <Exp> |  <Exp> <Exp> |
          let [rec] <decs> in <Exp>

<decs>  ::= <dec>,<decs> | <dec>
<dec>   ::= <ident>=<Exp>

<param> ::= () | <ident>

<unopr> ::= hd | tl | null | not | -
<binopr> ::= and | or | = | <> | < | <= | > | >= | + | - | * | / | ::

priorities:   ::                1  cons list (right associative)
              or                2
              and               3
              = <> < <= > >=    4  scalars only
              + -               5  (binary -)
              * /               6
              application       7
              - hd tl null not  8  (unary -)
 */
public class Parser {
    private final Lexer lexer;
    private ArrayList<Token> symbols = new ArrayList<Token>(Arrays.asList(Token.WORD, Token.NUMERAL, Token.EMPTY, Token.NILSY, Token.CHAR_LITERAL, Token.TRUESY, Token.FALSESY, Token.OPEN, Token.CLOSE, Token.SQ_OPEN, Token.SQ_CLOSE, Token.CURL_OPEN, Token.CURL_CLOSE, Token.LETSY, Token.RECSY, Token.INSY, Token.COMMA, Token.COLON, Token.IFSY, Token.THENSY, Token.ELSESY, Token.LAMBDASY, Token.DOT, Token.QUOTE, Token.CONSSY, Token.ORSY, Token.ANDSY, Token.EQ, Token.NE, Token.LT, Token.GT, Token.LE, Token.GE, Token.PLUS, Token.MINUS, Token.TIMES, Token.OVER, Token.NULLSY, Token.HDSY, Token.TLSY, Token.NOTSY, Token.EOFSY));
    private ArrayList<Token> unOprs = new ArrayList<Token>(Arrays.asList(new Token[]{Token.MINUS, Token.HDSY, Token.TLSY, Token.NULLSY, Token.NOTSY}));
    private ArrayList<Token> binOprs = new ArrayList<Token>(Arrays.asList(new Token[]{Token.CONSSY, Token.ORSY, Token.ANDSY, Token.EQ, Token.NE, Token.LT, Token.LE, Token.GT, Token.GE, Token.PLUS, Token.MINUS, Token.TIMES, Token.OVER}));
    private ArrayList<Token> rightAssoc = new ArrayList<Token>(Arrays.asList(new Token[]{Token.CONSSY}));
    private ArrayList<Token> startsExp = new ArrayList<Token>(Arrays.asList(Stream.concat(Arrays.stream(unOprs.toArray()), Arrays.stream(new Token[]{Token.WORD, Token.NUMERAL, Token.EMPTY, Token.NILSY, Token.CHAR_LITERAL, Token.TRUESY, Token.FALSESY, Token.OPEN, Token.LETSY, Token.IFSY, Token.LAMBDASY}))
            .toArray(Token[]::new)));
    private HashMap<Token, Integer> oprPriority;
    private final int applicPriority = 7;

    private Token token;

    public Parser(String program) {
        this.initializeOperatorPriority();
        this.lexer = new Lexer(program);
    }

    private void initializeOperatorPriority() {
        this.oprPriority = new HashMap<>();
        for (Token symbol : symbols) {
            this.oprPriority.put(symbol, 0);
        }
        this.oprPriority.put(Token.CONSSY, 1);
        this.oprPriority.put(Token.ORSY, 2);
        this.oprPriority.put(Token.ANDSY, 3);
        for (Token symbol : new Token[]{Token.EQ, Token.NE, Token.LT, Token.GT, Token.LE, Token.GE}) {
            this.oprPriority.put(symbol, 4);
        }
        this.oprPriority.put(Token.PLUS, 5);
        this.oprPriority.put(Token.MINUS, 5);
        this.oprPriority.put(Token.TIMES, 6);
        this.oprPriority.put(Token.OVER, 6);
    }

    private void parseAndSetNextSymbol() throws Exception {
        token = this.lexer.getSymbol();
    }

    public Expression parseProgram() throws Exception {
        this.parseAndSetNextSymbol();
        // The program is parse as one big expression
        return this.parseExpression();
    }

    private Expression parseExpression() throws Exception {
        return parseExp(1);
    }

    private Expression parseExp(Integer priority) throws Exception {
        Expression e = null, a;
        if (priority < this.applicPriority) {
            e = parseExp(priority + 1);
            if ((binOprs.contains(this.token) || rightAssoc.contains(this.token)) && this.oprPriority.get(this.token) == priority) {
                a = e;
                Token binOpr = this.token;
                this.parseAndSetNextSymbol();
                e = new BinExp(binOpr, a, this.parseExp(priority));
            } else {
                while (binOprs.contains(this.token) && !rightAssoc.contains(token) && this.oprPriority.get(this.token) == priority) {
                    a = e;
                    Token binOpr = this.token;
                    this.parseAndSetNextSymbol();
                    e = new BinExp(binOpr, a, this.parseExp(priority + 1));
                }
            }
        } else if (priority == this.applicPriority) {
            e = parseExp(priority + 1);
            while (startsExp.contains(this.token) && !binOprs.contains(this.token)) {
                a = e;
                e = new Application(a, this.parseExp(priority + 1));
            }
        } else if (unOprs.contains(this.token)) {
            Token unaryOpr = this.token;
            parseAndSetNextSymbol();
            e = new UnExp(unaryOpr, parseExp(priority));
        } else if (startsExp.contains(this.token)) {
            switch (this.token) {
                case WORD:
                    e = new Ident(this.lexer.getVariableName());
                    parseAndSetNextSymbol();
                    break;
                case NUMERAL:
                    e = new NumCon(this.lexer.getNumber());
                    parseAndSetNextSymbol();
                    break;
                case CHAR_LITERAL:
                    e = new CharCon(this.lexer.getQuotedString().charAt(0));
                    parseAndSetNextSymbol();
                    break;
                case EMPTY:
                    parseAndSetNextSymbol();
                    e = new EmptyCon();
                    break;
                case NILSY:
                    parseAndSetNextSymbol();
                    e = new NilCon();
                    break;
                case TRUESY:
                    e = new BoolCon(true);
                    parseAndSetNextSymbol();
                    break;
                case FALSESY:
                    e = new BoolCon(false);
                    parseAndSetNextSymbol();
                    break;
                case OPEN:
                    parseAndSetNextSymbol();
                    e = this.parseExpression();
                    checkTokenAndThrowError(Token.CLOSE, ") expected");
                    parseAndSetNextSymbol();
                    break;
                case LETSY:
                    parseAndSetNextSymbol();
                    e = this.parseParameterDeclarations();
                    break;
                case IFSY:
                    parseAndSetNextSymbol();
                    Expression e1 = this.parseExpression();
                    this.checkTokenAndThrowError(Token.THENSY, "no then   ");
                    parseAndSetNextSymbol();
                    Expression e2 = this.parseExpression();
                    this.checkTokenAndThrowError(Token.ELSESY, "no else   ");
                    parseAndSetNextSymbol();
                    Expression e3 = this.parseExpression();
                    e = new IfExp(e1, e2, e3);
                    break;
                case LAMBDASY:
                    parseAndSetNextSymbol();
                    Expression fParam = this.parseFunctionParameter();
                    parseAndSetNextSymbol();
                    checkTokenAndThrowError(Token.DOT, ". expected");
                    parseAndSetNextSymbol();
                    e = new LambdaExp(fParam, this.parseExpression());
                    break;

            }
        } else {
            this.error("bad operand");
        }
        return e;
    }

    private void checkTokenAndThrowError(Token s, String m) throws Exception {
        if (this.token != s) {
            this.error(m);
        }
    }

    private void error(String m) throws Exception {
        throw new Exception("error: " + m + "\tline no:" + this.lexer.getLineNumber() + "\t token:" + this.token + "\n program left to parse:\n" + this.lexer.getNonParsedExp());
    }

    private Expression parseParameterDeclarations() throws Exception {
        Boolean isRec = isToken(Token.RECSY);
        if (isRec) {
            parseAndSetNextSymbol();
        }
        Expression pDecs = this.parseParameterDeclarationList(isRec);
        if (!isToken(Token.INSY)){
            this.parseAndSetNextSymbol();
        }
        this.checkTokenAndThrowError(Token.INSY, "in expected");
        this.parseAndSetNextSymbol();
        return new Block(pDecs, this.parseExpression());
    }

    private Boolean isToken(Token s) {
        return s == this.token;
    }

    private Expression parseParameterDeclarationList(Boolean isRec) throws Exception {
        Expression d = parameterDeclaration();
        if (this.isToken(Token.COMMA)) {
            parseAndSetNextSymbol();
            return this.constructParameterDeclarationList(isRec, d, parseParameterDeclarationList(isRec));
        } else {
            return this.constructParameterDeclarationList(isRec, d, null);
        }
    }

    private Expression parameterDeclaration() throws Exception {
        Expression d = null;
        if (this.token == Token.WORD) {
            String name = this.lexer.getVariableName();
            parseAndSetNextSymbol();
            this.checkTokenAndThrowError(Token.EQ, "= expected");
            parseAndSetNextSymbol();
            d = new Decln(name, parseExpression());
        } else {
            this.error("dec, no id");
        }
        return d;
    }

    private Expression constructParameterDeclarationList(Boolean isRec, Expression h, Expression t) {
        return new DecList(isRec, h, t);
    }

    private Expression parseFunctionParameter() throws Exception {
        Expression functionParameter = null;
        if (this.token == Token.WORD) {
            functionParameter = new Ident(this.lexer.getVariableName());
        } else if (this.token == Token.EMPTY) {
            functionParameter = new EmptyCon();
        } else {
            this.error("f param   ");
        }
        return functionParameter;
    }
}
