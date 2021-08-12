package lexer;

public class Lexer {
    int lineNumber;
    String exp;
    int length;
    int index;
    double number;
    KeyWordTable keyWordTable;
    String variableName;
    String quotedString;

    public Lexer(String exp) {
        this.exp = exp;
        this.length = exp.length();
        this.index = 0;
        this.keyWordTable = new KeyWordTable();
        this.lineNumber = 1;
    }

    public Token getSymbol() throws Exception {
        if (this.index >= this.length) {
            return Token.EOFSY;
        }
        Token tok = null;
        switch (this.exp.charAt(this.index)) {
            // Handling comments
            case '{':
                while (this.exp.charAt(this.index) != '}') {
                    this.index++;
                }
                this.index++;
                tok = this.getSymbol();
                break;
            case '\n':
                this.lineNumber+=1;
            case '\t':
            case ' ':
                this.index++;
                tok = this.getSymbol();
                break;
            case ',':
                this.index++;
                tok = Token.COMMA;
                break;
//            case ';':
//                this.index++;
//                tok = Symbol.SEMI;
//                break;
            case '+':
                this.index++;
                tok = Token.PLUS;
                break;
            case '-':
                this.index++;
                tok = Token.MINUS;
                break;
            case '*':
                this.index++;
                tok = Token.TIMES;
                break;
            case '/':
                this.index++;
                tok = Token.OVER;
                break;
            case '(':
                this.index++;
                tok = Token.OPEN;
                if (this.index < this.length && this.exp.charAt(this.index) == ')') {
                    this.index++;
                    tok = Token.EMPTY;
                }
                break;
            case ')':
                this.index++;
                tok = Token.CLOSE;
                break;
//            case '{':
//                this.index++;
//                tok = Token.CURL_OPEN;
//                break;
//            case '}':
//                this.index++;
//                tok = Token.CURL_CLOSE;
//                break;
            case '[':
                this.index++;
                tok = Token.SQ_OPEN;
                break;
            case ']':
                this.index++;
                tok = Token.SQ_CLOSE;
                break;

            case '.':
                this.index++;
                tok = Token.DOT;
                break;
            case '"':
                this.index++;
                tok = Token.QUOTE;
                break;
            case '=':
                this.index++;
                tok = Token.EQ;
//                if (this.index < this.length && this.exp.charAt(this.index) == '=') {
//                    this.index++;
//                    tok = Token.EQ;
//                }
                break;
            case '<':
                tok = Token.LT;
                this.index++;
                if (this.index < this.length) {
                    switch (this.exp.charAt(this.index)) {
                        case '>':
                            this.index++;
                            tok = Token.NE;
                            break;
                        case '=':
                            this.index++;
                            tok = Token.LE;
                            break;
                    }
                }
                break;

            case '>':
                tok = Token.GT;
                this.index++;
                if (this.index < this.length && this.exp.charAt(this.index) == '=') {
                    this.index++;
                    tok = Token.GE;
                    break;
                }
                break;
            case ':':
                tok = Token.COLON;
                if ((this.index + 1) < this.length && this.exp.charAt(this.index + 1) == ':') {
                    this.index += 2;
                    tok = Token.CONSSY;
                    break;
                }
                break;
//            case '|':
//                if ((this.index + 1) < this.length && this.exp.charAt(this.index + 1) == '|') {
//                    this.index += 2;
//                    tok = Token.OR;
//                    break;
//                }
//            case '!':
//                this.index++;
//                tok = Token.NOT;
//                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                tok = this.getDoubleFromString();
                break;
            case '\'':
                this.parseCharacterLiteral();
                tok = Token.CHAR_LITERAL;
                break;
            default:
                tok = this.getKeyWordSymbol();
                if (tok != Token.EOFSY) {
                    break;
                }
                tok = this.getVariableSymbol();
                if (tok != Token.EOFSY) {
                    break;
                }
                System.out.println(String.format("Invalid character %c found at index %d in expression %s", this.exp.charAt(this.index), this.index, this.exp));
                throw new Exception("Invalid character found");
        }
        return tok;
    }

    public double getNumber() {
        return this.number;
    }

    private Token getDoubleFromString() {
        String numberStr = "";
        while (this.index < this.length && (this.exp.charAt(this.index) == '0' || this.exp.charAt(this.index) == '1' || this.exp.charAt(this.index) == '2' || this.exp.charAt(this.index) == '3' || this.exp.charAt(this.index) == '4' || this.exp.charAt(this.index) == '5' || this.exp.charAt(this.index) == '6' || this.exp.charAt(this.index) == '7' || this.exp.charAt(this.index) == '8' || this.exp.charAt(this.index) == '9')) {
            numberStr += this.exp.charAt(this.index);
            this.index++;
        }
        this.number = Double.parseDouble(numberStr);
        return Token.NUMERAL;
    }

    private Token getKeyWordSymbol() {
        String possibleKeyWord = "";
        int tempIndex = this.index;
        Token tokenFromKeyWord = this.keyWordTable.getSymbolFromKeyWord(possibleKeyWord);
        while (this.keyWordTable.getKeywordMatchCount(possibleKeyWord) > 0) {
            if (this.keyWordTable.getSymbolFromKeyWord(possibleKeyWord) != Token.EOFSY) {
                tokenFromKeyWord = this.keyWordTable.getSymbolFromKeyWord(possibleKeyWord);
                this.index = tempIndex;
            }
            possibleKeyWord += this.exp.charAt(tempIndex);
            tempIndex++;
        }
        return tokenFromKeyWord;
    }

    private Token getVariableSymbol() {
        if (Character.isLetter(this.exp.charAt(this.index))) {
            this.variableName = "";
            while (this.index < this.length && (Character.isAlphabetic(this.exp.charAt(this.index)) || Character.isDigit(this.exp.charAt(this.index)) || this.exp.charAt(this.index) == '_')) {
                this.variableName += this.exp.charAt(this.index);
                this.index++;
            }
            return Token.WORD;

        } else {
            return Token.EOFSY;
        }
    }

    private void parseCharacterLiteral() throws Exception {
        this.quotedString = "";
        int quoteCount = 0;
        if (this.exp.charAt(this.index) == '\'') {
            while ((this.index < this.length) && quoteCount < 2) {
                if (this.exp.charAt(this.index) == '\'') {
                    quoteCount++;
                } else {
                    this.quotedString += this.exp.charAt(this.index);
                }
                this.index++;
            }
        }
        if (quoteCount != 2) {
            throw new Exception("Quoted string not ended");
        }
        if (this.quotedString.length() != 1) {
            throw new Exception("Quoted string should be 1, got "+ this.quotedString.length());
        }
    }

    private void parseQuotedString() throws Exception {
        this.quotedString = "";
        int quoteCount = 0;
        if (this.exp.charAt(this.index) == '"') {
            while ((this.index < this.length) && quoteCount < 2) {
                if (this.exp.charAt(this.index) == '"') {
                    quoteCount++;
                } else {
                    this.quotedString += this.exp.charAt(this.index);
                }
                this.index++;
            }
        }
        if (quoteCount != 2) {
            throw new Exception("Quoted string not ended");
        }
    }

    public String getVariableName() {
        return this.variableName;
    }

    public String getQuotedString() {
        return this.quotedString;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getNonParsedExp() {
        return this.exp.substring(this.index);
    }
}
