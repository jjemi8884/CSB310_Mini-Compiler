import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Done by Mycole Brown
 * O(Test)
 * Constructor for the LexerTest class. Initializes the lexerTest's state and sets up the keyword map.
 * @param "source" the string to be tokenized by the lexer
 */
class LexerTest {
    @Test
    @DisplayName("semicolon in case switch")
    void semicolonTest() {
        // Here, I'm testing how my lexer handles a semicolon.
        String source = ";";
        Lexer l = new Lexer(source);  // I create a Lexer with the semicolon as the source.

        // I'm going to build the expected output string.
        StringBuilder sb = new StringBuilder();
        sb.append("    1      0 Semicolon      \n"); // Expecting a semicolon token at position 0.
        sb.append("    1      1 End_of_input   ");   // Also expecting an end-of-input token.

        // This is what I expect my lexer to output.
        String expected = sb.toString();
        String actual = l.printTokens(); // This is what my lexer actually outputs.

        // I'm checking that the actual output matches my expectations.
        assertEquals(expected, actual);
    }

    @Test
    void integerTest() {
        // In this test, I'm seeing if my lexer correctly handles an integer.
        String source = "42";
        Lexer l = new Lexer(source);  // Creating the lexer with '42' as the input.

        // Building the expected output.
        StringBuilder sb = new StringBuilder();
        sb.append("    1      0 Integer            42\n"); // Expecting the integer '42' as the token.
        sb.append("    1      2 End_of_input   ");          // Expecting an end-of-input following the integer.

        // What I expect versus what the lexer outputs.
        String expected = sb.toString();
        String actual = l.printTokens();

        // Here, I check that my lexer's output is as expected.
        assertEquals(expected, actual);
    }


    @Test
    void commaTest() {
        // I'm setting up a test case to check how my lexer handles a single comma.
        String source = ",";
        Lexer l = new Lexer(source);  // I create a Lexer object with the source as just a comma.

        // I'm going to build the expected output string using StringBuilder.
        StringBuilder sb = new StringBuilder();
        sb.append("    1      0 Comma          \n");  // Adding the expected token for the comma.
        sb.append("    1      1 End_of_input   ");    // Adding the end of input token.

        // This is what I expect my lexer to output after parsing the source.
        String expected = sb.toString();
        // I call printTokens to get the actual output from my lexer.
        String actual = l.printTokens();

        // Here, I'm checking if the actual output matches my expected output.
        assertEquals(expected, actual);
    }
}
