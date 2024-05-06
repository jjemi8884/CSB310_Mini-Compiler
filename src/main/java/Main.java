import java.io.*;
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
        //Scanner r = new Scanner(new File("src/main/resources/fizzBuzz.lex"));


        printWelcome("Lexer complete, moving to Parser");
        Map<String, Parser.TokenType> stt = Parser.createHashMap();
        List<Parser.Token> tokenList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        updateTokenList(tokenList, stt, lexOut );
        Parser p = new Parser(tokenList);
        Parser.Node rootNode = p.parse();
        String parseOut = p.printAST(rootNode, sb);

    }

    /**
     * create the token list for the parser to parse through this is to allow the
     * test to all the other class
     * @param tokenList is the list that will be updated with token types
     * @param stt is the map list that will assist in identifying tokens
     * @param lexOut is the output file from the lexer and is used to create tokens.
     * @throws Exception is if there is a not identifiable token in the list
     */
    public static void updateTokenList(List<Parser.Token> tokenList, Map<String,
            Parser.TokenType> stt, String lexOut) throws Exception {
        Scanner t = new Scanner(lexOut);
        updateTokenList(tokenList,stt,t);

    }
    /**
     * create the token list for the parser to parse through
     * @param tokenList is the list that will be updated with token types
     * @param stt is the map list that will assist in identifying tokens
     * @param t is the Scanner that will be passes in
     * @throws Exception is if there is a not identifiable token in the list
     */
    public static void updateTokenList(List<Parser.Token> tokenList, Map<String,
            Parser.TokenType> stt, Scanner t) throws Exception {

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
                        "text \nfile in c programming and output the AST flattened tree");
        BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
        List<String> cList = new ArrayList<>();
        updateCList(cList);
        String fullFileName = getChoice(br, cList);
        File f = new File(fullFileName);
        System.out.println("You have selected " + fullFileName + " as the file to be compiled");
        return f;
    }//end getFile()

    /**
     * o(1)
     * will find all .c files in the resources folder that can be lexed and parsed
     * @param cList the list to update with c programs
     * @throws FileNotFoundException yes
     */
    public static void updateCList(List cList) throws FileNotFoundException {
        File f1 = new File("src/main/resources/");
        File [] fileList = f1.listFiles();
        for(int i = 0; i < fileList.length; i++){
            String fileName = fileList[i].getName();
            char lastChar = fileName.charAt(fileName.length()-1);
            if(lastChar == 'c'){
                cList.add(fileName);
            }//if
        }//end while
    }//end updateCList

    /**
     * THis will allow us to choose some our listed c programs for compiling or select a new program
     * @param br BufferedReader Object
     * @return our integer of choice.
     */
    public static String getChoice(BufferedReader br, List<String> cList) throws IOException {
        printLine("-");
        System.out.print("Enter the number of what file you want to compile\n");
        printLine("-");
        int num = 1;
        System.out.println("0: manually enter path and file name");
        for(String fileName : cList){
            System.out.print(num + ": " + fileName + "\n");
            num++;
        }//end for
        printLine("-");
        System.out.print("Enter Number: ");
        boolean done = false;
        String a = br.readLine();
        int i = Integer.parseInt(a);
        String fullFileName = "";
        if(i == 0){
            System.out.print("Enter Path and name of file: ");
            fullFileName = br.readLine();

        }else {
            i--;
            fullFileName = "src/main/resources/" + cList.get(i);
        }

        return fullFileName;
    }

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
