import java.util.ArrayList;
import java.util.List;

public class SimpleCompiler {
    public enum TokenType {
        NUMBER, PLUS, MINUS, MULTIPLY, DIVIDE, LEFT_PAREN, RIGHT_PAREN, EOF
    }

    public static class Token {
        private final TokenType type;
        private final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public TokenType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    public abstract static class Expr {
        public abstract long evaluate();
    }

    public static class BinaryExpr extends Expr {
        private final Expr left;
        private final Expr right;
        private final Token op;

        public BinaryExpr(Expr left, Expr right, Token op) {
            this.left = left;
            this.right = right;
            this.op = op;
        }

        @Override
        public long evaluate() {
            long leftValue = left.evaluate();
            long rightValue = right.evaluate();

            return switch (op.getType()) {
                case PLUS -> leftValue + rightValue;
                case MINUS -> leftValue - rightValue;
                case MULTIPLY -> leftValue * rightValue;
                case DIVIDE -> leftValue / rightValue;
                default -> throw new AssertionError("Unknown operator: " + op.getType());
            };
        }
    }

    public static class NumberExpr extends Expr {
        private final Token value;

        public NumberExpr(Token value) {
            this.value = value;
        }

        @Override
        public long evaluate() {
            return Long.parseLong(value.getValue());
        }
    }

    public static class Parser {
        private Token currentToken;
        private final List<Token> tokens;
        private int pos = 0;

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
            currentToken = tokens.get(pos);
        }

        public Expr parse() {
            return expression();
        }

        private Expr expression() {
            Expr result = term();

            while (true) {
                if (match(TokenType.PLUS)) {
                    result = new BinaryExpr(result, term(), new Token(TokenType.PLUS, "+"));
                } else if (match(TokenType.MINUS)) {
                    result = new BinaryExpr(result, term(), new Token(TokenType.MINUS, "-"));
                } else {
                    return result;
                }
            }
        }

        private Expr term() {
            Expr result = factor();

            while (true) {
                if (match(TokenType.MULTIPLY)) {
                    result = new BinaryExpr(result, factor(), new Token(TokenType.MULTIPLY, "*"));
                } else if (match(TokenType.DIVIDE)) {
                    result = new BinaryExpr(result, factor(), new Token(TokenType.DIVIDE, "/"));
                } else {
                    return result;
                }
            }
        }

        private Expr factor() {
            if (match(TokenType.LEFT_PAREN)) {
                Expr result = expression();
                if (!match(TokenType.RIGHT_PAREN)) {
                    throw new RuntimeException("Missing right parenthesis");
                }
                return result;
            } else if (currentToken.getType() == TokenType.NUMBER) {
                return new NumberExpr(consume());
            } else {
                throw new RuntimeException("Unexpected token: " + currentToken.getValue());
            }
        }

        private Token consume() {
            Token result = currentToken;
            advance();
            return result;
        }

        private boolean match(TokenType type) {
            if (currentToken.getType() == type) {
                advance();
                return true;
            } else {
                return false;
            }
        }

        private Token advance() {
            if (++pos < tokens.size()) {
                return currentToken = tokens.get(pos);
            } else {
                return currentToken = new Token(TokenType.EOF, null);
            }
        }
    }

    public static class Lexer {
        private final String input;
        private int pos = 0;
        private char currentChar;

        public Lexer(String input) {
            this.input = input;
            currentChar = input.charAt(pos);
        }

        public List<Token> tokenize() {
            List<Token> tokens = new ArrayList<>();

            while (pos < input.length()) {
                if (Character.isWhitespace(currentChar)) {
                    advance();
                } else if (Character.isDigit(currentChar)) {
                    StringBuilder numberBuilder = new StringBuilder();
                    while (pos < input.length() && Character.isDigit(currentChar)) {
                        numberBuilder.append(currentChar);
                        advance();
                    }
                    tokens.add(new Token(TokenType.NUMBER, numberBuilder.toString()));
                } else if (currentChar == '+') {
                    tokens.add(new Token(TokenType.PLUS, String.valueOf(currentChar)));
                    advance();
                } else if (currentChar == '-') {
                    tokens.add(new Token(TokenType.MINUS, String.valueOf(currentChar)));
                    advance();
                } else if (currentChar == '*') {
                    tokens.add(new Token(TokenType.MULTIPLY, String.valueOf(currentChar)));
                    advance();
                } else if (currentChar == '/') {
                    tokens.add(new Token(TokenType.DIVIDE, String.valueOf(currentChar)));
                    advance();
                } else if (currentChar == '(') {
                    tokens.add(new Token(TokenType.LEFT_PAREN, String.valueOf(currentChar)));
                    advance();
                } else if (currentChar == ')') {
                    tokens.add(new Token(TokenType.RIGHT_PAREN, String.valueOf(currentChar)));
                    advance();
                } else {
                    throw new RuntimeException("Invalid character: " + currentChar);
                }
            }
            tokens.add(new Token(TokenType.EOF, null));
            return tokens;
        }

        private void advance() {
            currentChar = (++pos < input.length()) ? input.charAt(pos) : '\0';
        }
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer("(3 + 5) * (2 - 1)");
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Expr expr = parser.parse();

        System.out.println(expr.evaluate());
    }
}
