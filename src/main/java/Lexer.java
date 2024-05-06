import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Lexer {
    private int line;
    private int pos;
    private int position;
    private char chr;
    private String s;

    // I'm using a map to store keywords and their corresponding token types.
    Map<String, TokenType> keywords = new HashMap<>();

    // A nested Token class to hold the details of each token.
    static class Token {
        public TokenType tokentype;
        public String value;
        public int line;
        public int pos;
        Token(TokenType token, String value, int line, int pos) {
            this.tokentype = token; this.value = value; this.line = line; this.pos = pos;
        }

        // Custom toString to format the token information.
        @Override
        public String toString() {
            String result = String.format("%5d  %5d %-15s", this.line, this.pos, this.tokentype);
            switch (this.tokentype) {
                case Integer:
                    result += String.format("  %4s", value);
                    break;
                case Identifier:
                    result += String.format(" %s", value);
                    break;
                case String:
                    result += String.format(" \"%s\"", value);
                    break;
            }
            return result;
        }
    }

    // Enum to define all possible token types.
    static enum TokenType {
        // List of all token types, including operators, keywords, and punctuation.
        End_of_input, Op_multiply,  Op_divide, Op_mod, Op_add, Op_subtract,
        Op_negate, Op_not, Op_less, Op_lessequal, Op_greater, Op_greaterequal,
        Op_equal, Op_notequal, Op_assign, Op_and, Op_or, Keyword_if,
        Keyword_else, Keyword_while, Keyword_print, Keyword_putc, LeftParen, RightParen,
        LeftBrace, RightBrace, Semicolon, Comma, Identifier, Integer, String
    }

    // A method to handle errors and exit the program.
    static void error(int line, int pos, String msg) {
        if (line > 0 && pos > 0) {
            System.out.printf("%s in line %d, pos %d\n", msg, line, pos);
        } else {
            System.out.println(msg);
        }
        System.exit(1);
    }


    // Constructor for the Lexer. Initializes everything and sets up keywords.
    Lexer(String source) {
        this.line = 1; // Start counting lines from 1.
        this.pos = 0; // Position starts at 0.
        this.position = 0; // Same with the character position.
        this.s = source; // The string to tokenize.
        this.chr = this.s.charAt(0); // Start by looking at the first character.
        // Adding some predefined keywords to the map.
        this.keywords.put("if", TokenType.Keyword_if);
        this.keywords.put("else", TokenType.Keyword_else);
        this.keywords.put("print", TokenType.Keyword_print);
        this.keywords.put("putc", TokenType.Keyword_putc);
        this.keywords.put("while", TokenType.Keyword_while);
    }

    // Method to handle following characters for two-character tokens.
    Token follow(char expect, TokenType ifyes, TokenType ifno, int line, int pos) {
        if (getNextChar() == expect) {
            getNextChar();
            return new Token(ifyes, "", line, pos);
        }
        if (ifno == TokenType.End_of_input) {
            error(line, pos, String.format("follow: unrecognized character: (%d) '%c'", (int)this.chr, this.chr));
        }
        return new Token(ifno, "", line, pos);
    }

    // Method to parse character literals.
    Token char_lit(int line, int pos) {
        char c = getNextChar(); // skip opening quote
        int n = (int)c;
        if (c == '\\') {
            c = getNextChar();
            if (c == 'n') n = 10; // Handling newline escape.
            else if (c == '\\') n = '\\'; // Handling backslash escape.
            else error(line, pos, String.format("unrecognized escape sequence \\%c", c));
        }
        if (getNextChar() != '\'') {
            error(line, pos, "multi-character constant");
        }
        getNextChar();
        return new Token(TokenType.Integer, "" + n, line, pos);
    }

    // Method to parse string literals.
    Token string_lit(char start, int line, int pos) {
        String result = "";
        // I loop through each character until I reach the closing quote.
        while (getNextChar() != start) {
            if (this.chr == '\\') {
                // Escaping sequences if I encounter a backslash.
                getNextChar();
                if (this.chr == 'n') {
                    result += "\\n"; // Handle newline
                } else if (this.chr == '\\') {
                    result += "\\"; // Handle backslash
                } else if (this.chr == '"') {
                    result += "\""; // Handle quote
                } else {
                    // If it's an unrecognized escape sequence, I report an error.
                    error(line, pos, String.format("unrecognized escape sequence \\%c", this.chr));
                }
            } else if (this.chr == '\u0000') {
                // If I reach the end of file without closing quote, it's an error.
                error(line, pos, "EOF while scanning string literal");
            } else {
                // Otherwise, I just add the character to my result.
                result += this.chr;
            }
        }
        getNextChar(); // I move past the closing quote.
        return new Token(TokenType.String, result, line, pos); // And return a new string token.
    }

    Token div_or_comment(int line, int pos) {
        // I check if the next character is not '*', it's a division operation.
        if (getNextChar() != '*') {
            return new Token(TokenType.Op_divide, "", line, pos);
        }
        getNextChar(); // Skip the '*'
        while (true) {
            if (this.chr == '\u0000') {
                // If I hit EOF inside a comment block, that's an error.
                error(line, pos, "EOF in comment");
            } else if (this.chr == '*') {
                // If the next char after '*' is '/', I end the comment.
                if (getNextChar() == '/') {
                    getNextChar(); // Skip the '/'
                    return getToken(); // And get the next token.
                }
            } else {
                // Otherwise, I just keep scanning.
                getNextChar();
            }
        }
    }

    Token identifier_or_integer(int line, int pos) {
        boolean is_number = true;
        String text = "";
        // I collect alphanumeric characters and underscores.
        while (Character.isAlphabetic(this.chr) || this.chr == '_' || Character.isDigit(this.chr)) {
            text += this.chr;
            if (!Character.isDigit(this.chr)) is_number = false; // If any character is not a digit, it's not a number.
            getNextChar(); // Move to the next character.
        }

        if (text.isEmpty()) {
            // If I didn't collect any characters, that's an error.
            error(line, pos, String.format("unrecognized character: (%d) '%c'", (int)this.chr, this.chr));
        } else if (is_number) {
            // If it's all digits, return it as an integer token.
            return new Token(TokenType.Integer, text, line, pos);
        } else if (this.keywords.containsKey(text)) {
            // If it's a keyword, return the appropriate keyword token.
            return new Token(this.keywords.get(text), "", line, pos);
        } else {
            // Otherwise, it's an identifier.
            return new Token(TokenType.Identifier, text, line, pos);
        }
        return null; // This return shouldn't actually be reached because of the earlier returns.
    }

    Token getToken() {
        int line, pos;
        // I skip any whitespace characters to get to the first significant character.
        while (Character.isWhitespace(this.chr)) {
            getNextChar();
        }
        line = this.line;
        pos = this.pos;

        // I switch based on the character to determine what token to create.
        switch (this.chr) {
            case '\u0000': return new Token(TokenType.End_of_input, "", this.line, this.pos);
            case '/': return div_or_comment(line, pos); // Handling division or comments.
            case '\'': return char_lit(line, pos); // Handling character literals.
            case '"': return string_lit(this.chr, line, pos); // Handling string literals.
            case '(': getNextChar(); return new Token(TokenType.LeftParen, "", line, pos); // Handling left parenthesis.
            case ')': getNextChar(); return new Token(TokenType.RightParen, "", line, pos); // Handling right parenthesis.
            case '{': getNextChar(); return new Token(TokenType.LeftBrace, "", line, pos); // Handling left brace.
            case '}': getNextChar(); return new Token(TokenType.RightBrace, "", line, pos); // Handling right brace.
            case ';': getNextChar(); return new Token(TokenType.Semicolon, "", line, pos); // Handling semicolon.
            case ',': getNextChar(); return new Token(TokenType.Comma, "", line, pos); // Handling comma.
            case '+': getNextChar(); return new Token(TokenType.Op_add, "", line, pos); // Handling addition.
            case '-': getNextChar(); return new Token(TokenType.Op_subtract, "", line, pos); // Handling subtraction.
            case '*': getNextChar(); return new Token(TokenType.Op_multiply, "", line, pos); // Handling multiplication.
            case '%': getNextChar(); return new Token(TokenType.Op_mod, "", line, pos); // Handling modulus.
            case '&': return follow('&', TokenType.Op_and, TokenType.End_of_input, line, pos); // Handling AND operation.
            case '|': return follow('|', TokenType.Op_or, TokenType.End_of_input, line, pos); // Handling OR operation.
            case '=': return follow('=', TokenType.Op_equal, TokenType.Op_assign, line, pos); // Handling equal or assignment.
            case '!': return follow('=', TokenType.Op_notequal, TokenType.Op_not, line, pos); // Handling not equal.
            case '<': return follow('=', TokenType.Op_lessequal, TokenType.Op_less, line, pos); // Handling less or less equal.
            case '>': return follow('=', TokenType.Op_greaterequal, TokenType.Op_greater, line, pos); // Handling greater or greater equal.
            default: return identifier_or_integer(line, pos); // Handling identifiers or integers.
        }
    }

    char getNextChar() {
        this.pos++;
        this.position++;
        if (this.position >= this.s.length()) {
            this.chr = '\u0000'; // If I reach the end of the string, I return null character.
            return this.chr;
        }
        this.chr = this.s.charAt(this.position);
        if (this.chr == '\n') {
            this.line++; // Increment line number on newline.
            this.pos = 0; // Reset position for the new line.
        }
        return this.chr;
    }

    String printTokens() {
        Token t;
        StringBuilder sb = new StringBuilder();
        // I print out all tokens until the end of input token.
        while ((t = getToken()).tokentype != TokenType.End_of_input) {
            sb.append(t);
            sb.append("\n");
            System.out.println(t);
        }
        sb.append(t);
        System.out.println(t); // Printing the last token
        return sb.toString();
    }

    static void outputToFile(String result) {
        try {
            // I'm creating a FileWriter to write to a file.
            FileWriter myWriter = new FileWriter("src/main/resources/countTest.lex");
            myWriter.write(result); // Here, I write the result to the file.
            myWriter.close(); // Always good practice to close the file when I'm done.
            System.out.println("Successfully wrote to the file."); // I print to console to confirm it worked.
        } catch (IOException e) {
            // If there's an issue, I throw a runtime exception with the caught exception as its cause.
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (1 == 1) { // This condition is always true, used here just as a placeholder.
            try {
                // I'm setting up to read from a file.
                File f = new File("src/main/resources/count.c");
                Scanner s = new Scanner(f);
                String source = " ";
                String result = " ";
                // I read through the file, appending each line to 'source'.
                while (s.hasNext()) {
                    source += s.nextLine() + "\n";
                }
                Lexer l = new Lexer(source); // I create a Lexer with the file's contents.
                result = l.printTokens(); // I tokenize the contents and store the results.

                outputToFile(result); // I call my method to write the results to a file.

            } catch (FileNotFoundException e) {
                // If the file isn't found, I handle the error with a custom message.
                error(-1, -1, "Exception: " + e.getMessage());
            }
        } else {
            // This condition is never true; it's just a placeholder.
            error(-1, -1, "No args");
        }
    }

}










