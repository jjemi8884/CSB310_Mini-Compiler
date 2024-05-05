import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.*;

public class Main {

    static final int lineSpace = 40;

    public static void main(String [] args) throws Exception {
        //get the file
        Scanner s = new Scanner(getFile());
        //lexer
        String lexSource = " ";
        while(s.hasNext()){
            lexSource += s.nextLine() + "\n";
        }//end while
        Lexer lex = new Lexer(lexSource);
        String lexOut = lex.printTokens();

        //parser

        //temp scanner
        Scanner r = new Scanner(new File("src/main/resources/fizzBuzz.lex"));


        printWelcome("Lexer complete, moving to Parser");
        Map<String, Parser.TokenType> stt = Parser.createHashMap();
        List<Parser.Token> tokenList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        updateTokenList(tokenList, stt, lexOut, r);
        Parser p = new Parser(tokenList);
        Parser.Node rootNode = p.parse();
        String parseOut = p.printAST(rootNode, sb);

    }

    /**
     * create the token list for the parser to parse through
     * @param tokenList is the list that will be updated with token types
     * @param stt is the map list that will assist in identifying tokens
     * @param lexOut is the output file from the lexer and is used to create tokens.
     * @throws Exception is if there is a not identifiable token in the list
     */
    public static void updateTokenList(List<Parser.Token> tokenList, Map<String,
            Parser.TokenType> stt, String lexOut, Scanner temp) throws Exception {
        Scanner t = temp;//new Scanner(lexOut);
        int line;
        int pos;
        String token;
        String value;
        Boolean found;

        while (t.hasNext()) {
            String str = t.nextLine();
            StringTokenizer st = new StringTokenizer(str);
            line = Integer.parseInt(st.nextToken());
            pos = Integer.parseInt(st.nextToken());
            token = st.nextToken();
            value = "";
            while (st.hasMoreTokens()) {
                value += st.nextToken() + " ";
            }
            found = false;
            if (stt.containsKey(token)) {
                found = true;
                tokenList.add(new Parser.Token(stt.get(token), value, line, pos));
            }
            if (found == false) {
                throw new Exception("Token not found: '" + token + "'");
            }
        }//end while

        int i = 1;
    }//end getTokenList

    /**
     * this method will create the list of tokens that will be then parsed by the parser.
     * @input List object that will go througt the scanner object to list each token
     */

    /**
     * O(1)
     * prints the welcomd message, and gets the file name for the file we are going to
     * be combiling
     * @return file object that can be used for parcing
     * @throws IOException incase the file name does not exist
     */
    public static File getFile() throws IOException {
        printWelcome("\tWelcome to Compiler Program\nThis program will read a" +
                        "text file in c programming and output the AST flattened tree");
        //BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter name of C File to compile within the resources folder\n" +
                "(for example \"99bottles.c\", enter here: ");
        String fileName = "count.c";//br.readLine();
        String fullFileName = ("src/main/resources/" + fileName);

        File f = new File(fullFileName);
        System.out.println("You have selected " + fullFileName + " as the file to be compiled");
        return f;
    }//end getFile()

    /**
     * the method to print to the console with a space afterwards
     * @param text the test used in the print
     */
    public static void consolePrint(String text){
        System.out.println(text);
        System.out.println();
    }

    /**
     * used to print the welcome message with some lines
     * @param text used in the welcome message
     */
    public static void printWelcome(String text){
        printLine("+");
        consolePrint(text);
        printLine("=");
    }//end printWelcome

    /**
     * O(1)
     * for printing lines
     * @param symbol is the symbol used to make the line for
     *               listed linelength.
     */
    public static void printLine(String symbol){
        for(int i = 0; i < lineSpace; i++){
            System.out.print("=");
        }//end for
        System.out.println("");
    }//end printLine
}
