package lexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyWordTable {
    private static Map<String, Token> keyWorkMap;
    private Set<String> keyWordSet;

    public KeyWordTable() {
        this.keyWorkMap = new HashMap<>();
        this.keyWorkMap.put("nil", Token.NILSY);
        this.keyWorkMap.put("true", Token.TRUESY);
        this.keyWorkMap.put("false", Token.FALSESY);
        this.keyWorkMap.put("let", Token.LETSY);
        this.keyWorkMap.put("in", Token.INSY);
        this.keyWorkMap.put("if", Token.IFSY);
        this.keyWorkMap.put("then", Token.THENSY);
        this.keyWorkMap.put("else", Token.ELSESY);
        this.keyWorkMap.put("lambda", Token.LAMBDASY);
        this.keyWorkMap.put("or", Token.ORSY);
        this.keyWorkMap.put("and", Token.ANDSY);
        this.keyWorkMap.put("null", Token.NULLSY);
        this.keyWorkMap.put("hd", Token.HDSY);
        this.keyWorkMap.put("tl", Token.TLSY);
        this.keyWorkMap.put("not", Token.NOTSY);
        this.keyWorkMap.put("rec", Token.RECSY);
        this.keyWordSet = this.keyWorkMap.keySet();
    }

    public Token getSymbolFromKeyWord(String keyWord) {
        return this.keyWorkMap.getOrDefault(keyWord, Token.EOFSY);
    }

    public int getKeywordMatchCount(String inputKeyWord) {
        int count = 0;
        for (String keyWord : this.keyWordSet) {
            if (keyWord.startsWith(inputKeyWord) || keyWord.equals(inputKeyWord)) {
                count++;
            }
        }
        return count;
    }
}
