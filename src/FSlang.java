import ast.Expression;
import executor.Executor;
import parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;

public class FSlang {
    public static void main(String[] args) throws Exception {
        String content = Files.readString(Path.of("HelloWorld.sin"));
        Parser p = new Parser(content);
        Expression program = p.parseProgram();
        Executor e = new Executor();
        e.execute(program);
    }
}
