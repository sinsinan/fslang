package lexer;

/*
https://users.monash.edu.au/~lloyd/tildeFP/Lambda/Ch/04.Pars.shtml
                                                           {lexical items}
symbol = (word, numeral, empty{ () }, nilsy, charliteral, truesy, falsesy,
          open, close, sqopen, sqclose, curlopen, curlclose,
          letsy, recsy, insy, comma, colon,
          ifsy, thensy, elsesy, lambdasy, dot,
          quote,
          conssy,
          orsy, andsy,
          eq, ne, lt, le, gt, ge,
          plus, minus, times, over,
          nullsy, hdsy, tlsy, notsy,
          eofsy
         );

{\fB Lexical Types. \fP}
 */

public enum Token {
    WORD,
    NUMERAL,
    EMPTY,
    NILSY,
    CHAR_LITERAL,
    TRUESY,
    FALSESY,
    OPEN,
    CLOSE,
    SQ_OPEN,
    SQ_CLOSE,
    CURL_OPEN,
    CURL_CLOSE,
    LETSY,
    RECSY,
    INSY,
    COMMA,
    COLON,
    IFSY,
    THENSY,
    ELSESY,
    LAMBDASY,
    DOT,
    QUOTE,
    CONSSY,
    ORSY,
    ANDSY,
    EQ,
    NE,
    LT,
    GT,
    LE,
    GE,
    PLUS,
    MINUS,
    TIMES,
    OVER,
    NULLSY,
    HDSY,
    TLSY,
    NOTSY,
    EOFSY
}
