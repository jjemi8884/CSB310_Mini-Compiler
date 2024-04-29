import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * this class will injest a lex file and create the flattened AST tree
 */
class Parser {
    /**
     * list of tokens which the parser will iterate through
     */
    private List<Token> source;
    /**
     * used to hold the current token
     */
    private Token token;
    /**
     * this is the current position of the pointer in the source that will return the correct token
     */
    private int position;

    /**
     * Node class used to build the AST tree.
     */
    static class Node {
        /**
         * hold the type of node that will be used in the ASL tree. Pulls from a enum list NodeType.
         */
        public NodeType nt;
        /**
         * links to the left and right nodes in the tree
         */
        public Node left, right;
        /**
         * the string value for the node
         */
        public String value;


        /**
         * O(1)
         * blank constructor that will fill all values to null
         */
        Node() {
            this.nt = null;
            this.left = null;
            this.right = null;
            this.value = null;
        }//end node

        /**
         * O(1)
         * Constructor that will accept all values of nodes.
         * @param node_type get from enum NodeType
         * @param left link to left child
         * @param right link to right child
         * @param value string value of the node
         */
        Node(NodeType node_type, Node left, Node right, String value) {
            this.nt = node_type;
            this.left = left;
            this.right = right;
            this.value = value;
        }//end constructor

        /**
         * O(1)
         * static method to make a new node without a value attached.
         * @param nodetype type of node from enum list of NodeTypes
         * @param left link to left child of node
         * @param right link to the right child of node
         * @return the pointer to the newly created Node.
         */
        public static Node make_node(NodeType nodetype, Node left, Node right) {
            return new Node(nodetype, left, right, "");
        }//end make_node

        /**
         * O(1)
         * Second of three different static "make_node" which only accepts node type and a left child
         * @param nodetype type of node
         * @param left pointer to the left child
         * @return the newly created node
         */
        public static Node make_node(NodeType nodetype, Node left) {
            return new Node(nodetype, left, null, "");
        }// end make_node #2

        /**
         * O(1)
         * Static method for creating the leafs nodes of the ASL tree, this will have no children
         * @param nodetype type of node from the enum NodeType list
         * @param value string value that will be held by the node
         * @return the newly created node
         */
        public static Node make_leaf(NodeType nodetype, String value) {
            return new Node(nodetype, null, null, value);
        }//end make leaf
    }//end node class

    /**
     * Toke class used to hold data about tokens taken from multiple enums
     * Data held wil be token type, if right associate, if binary, if unary,and also the node type.
     * The token will also hold the string value of the token and position
     * as well as the line of the token itself.
     */
    static class Token {

        /**
         * Type of token from enum list TokenType
         */
        public TokenType tokentype;
        /**
         * The value of the token is holding
         */
        public String value;
        /**
         * line number token is found
         */
        public int line;
        /**
         * position in the line that the token is found (start of the token)
         */
        public int pos;


        /**
         * O(1)
         * constructor for the token
         * @param token is the TokenType class that hold token data
         * @param value String value of the token
         * @param line is the line that the token can be found in the program
         * @param pos is the position within the line that the token can be found (first char)
         */
        Token(TokenType token, String value, int line, int pos) {
            this.tokentype = token; this.value = value; this.line = line; this.pos = pos;
        }//end Token constructor

        /**
         * O(1)
         * override method for the lexical position of a token.
         * @return a sting with the position of the token
         */
        @Override
        public String toString() {
            return String.format("%5d  %5d %-15s %s", this.line, this.pos, this.tokentype, this.value);
        }//end toString
    }//end class Token

    /**
     * enum list that holds all used tokens.Used by both Node and Token classes
     * Hold the information about the NodeType, if it is right_associate, binary token, or unary token,
     * Also has the precedence number if needed to identify the correct precedence of the token.
     * -1 precedence means that it is not a binary or unary operation.
     */
    static enum TokenType {
        End_of_input(false, false, false, -1, NodeType.nd_None),
        Op_multiply(false, true, false, 13, NodeType.nd_Mul),
        Op_divide(false, true, false, 13, NodeType.nd_Div),
        Op_mod(false, true, false, 13, NodeType.nd_Mod),
        Op_add(false, true, false, 12, NodeType.nd_Add),
        Op_subtract(false, true, false, 12, NodeType.nd_Sub),
        Op_negate(false, false, true, 14, NodeType.nd_Negate),
        Op_not(false, false, true, 14, NodeType.nd_Not),
        Op_less(false, true, false, 10, NodeType.nd_Lss),
        Op_lessequal(false, true, false, 10, NodeType.nd_Leq),
        Op_greater(false, true, false, 10, NodeType.nd_Gtr),
        Op_greaterequal(false, true, false, 10, NodeType.nd_Geq),
        Op_equal(false, true, true, 9, NodeType.nd_Eql),
        Op_notequal(false, true, false, 9, NodeType.nd_Neq),
        Op_assign(false, false, false, -1, NodeType.nd_Assign),
        Op_and(false, true, false, 5, NodeType.nd_And),
        Op_or(false, true, false, 4, NodeType.nd_Or),
        Keyword_if(false, false, false, -1, NodeType.nd_If),
        Keyword_else(false, false, false, -1, NodeType.nd_None),
        Keyword_while(false, false, false, -1, NodeType.nd_While),
        Keyword_print(false, false, false, -1, NodeType.nd_None),
        Keyword_putc(false, false, false, -1, NodeType.nd_None),
        LeftParen(false, false, false, -1, NodeType.nd_None),
        RightParen(false, false, false, -1, NodeType.nd_None),
        LeftBrace(false, false, false, -1, NodeType.nd_None),
        RightBrace(false, false, false, -1, NodeType.nd_None),
        Semicolon(false, false, false, -1, NodeType.nd_None),
        Comma(false, false, false, -1, NodeType.nd_None),
        Identifier(false, false, false, -1, NodeType.nd_Ident),
        Integer(false, false, false, -1, NodeType.nd_Integer),
        String(false, false, false, -1, NodeType.nd_String);

        /**
         * What precedence level is the token in regards to mathmatical equations
         */
        private final int precedence;

        /**
         * If the token is right_associated
         */
        private final boolean right_assoc;

        /**
         * if the token is a binary system
         */
        private final boolean is_binary;
        /**
         * if the token is a unary system
         */
        private final boolean is_unary;
        /**
         * the node type
         */
        private final NodeType node_type;

        /**
         * O(1)
         * create the token type consturctor will get its information from the enum list
         * @param right_assoc boolean value
         * @param is_binary boolean value if binary
         * @param is_unary boolean value if binary
         * @param precedence integer value of its precedence
         * @param node nodetype of the tokentype
         */
        TokenType(boolean right_assoc, boolean is_binary, boolean is_unary, int precedence, NodeType node) {
            this.right_assoc = right_assoc;
            this.is_binary = is_binary;
            this.is_unary = is_unary;
            this.precedence = precedence;
            this.node_type = node;
        }//end TokenType constructor

        /**
         * (1)
         * return the boolean value if its a right_associated
         * @return
         */
        boolean isRightAssoc() { return this.right_assoc; }//end isRightAssoc

        /**
         * O(1)
         * return boolean value if its binary token
         * @return true if token type is binary
         */
        boolean isBinary() { return this.is_binary; }//end isBinary

        /**
         * O(1)
         * return boolean value if its unary token
         * @return true if token type is a unary token
         */
        boolean isUnary() { return this.is_unary; }//end isUnary

        /**O(1)
         * method to get the precedence of the token type
         * @return integer value with a representation of the
         * precedence of the token, high the number the greater the precedence
         */
        int getPrecedence() { return this.precedence; }//end getPrecedence

        /**O(1)
         * method to get the node type of the token
         * @return return the NodeType object of the TokenType
         */
        NodeType getNodeType() { return this.node_type; }//end getNodeType
    }//end static enum TokenType

    /**O(1)
     * static enum NodeType is an enum class that will hold information about what type of
     * nodes. The only information held is the string name of the token.
     */
    static enum NodeType {
        nd_None(""), nd_Ident("Identifier"), nd_String("String"), nd_Integer("Integer"), nd_Sequence("Sequence"), nd_If("If"),
        nd_Prtc("Prtc"), nd_Prts("Prts"), nd_Prti("Prti"), nd_While("While"),
        nd_Assign("Assign"), nd_Negate("Negate"), nd_Not("Not"), nd_Mul("Multiply"), nd_Div("Divide"), nd_Mod("Mod"), nd_Add("Add"),
        nd_Sub("Subtract"), nd_Lss("Less"), nd_Leq("LessEqual"),
        nd_Gtr("Greater"), nd_Geq("GreaterEqual"), nd_Eql("Equal"), nd_Neq("NotEqual"), nd_And("And"), nd_Or("Or");

        /**
         * only value within the NodeType is the string name
         */
        private final String name;

        /**
         * O(1)
         * constructor for the NodeType enum class
         * @param name of the token
         */
        NodeType(String name) {
            this.name = name;
        }//end constructor

        /**
         * override method for the node class, this will have the name value of the
         * node returned
         * @return string of the name of the node
         */
        @Override
        public String toString() { return this.name; }
    }//end NodeType Static enum class

    /**
     * O(1)
     * This will print out any errors within the syntax of the supplied
     * lexiconical input. NOTE: this is for syntax errors found on the
     * inputted data, not for error within this parser.
     * @param line line the error is found
     * @param pos position of the error
     * @param msg string message that can be relayed to the programmer
     */
    static void error(int line, int pos, String msg) {
        if (line > 0 && pos > 0) {
            System.out.printf("%s in line %d, pos %d\n", msg, line, pos);
        } else {
            System.out.println(msg);
        }
        System.exit(1);
    }
    Parser(List<Token> source) {
        this.source = source;
        this.token = null;
        this.position = 0;

    }//end error

    /**
     * O(1)
     * Method to get the next token from the global source list
     * it will also advance the global position counter
     * @return the global token but will also update the global token return is not
     * really needed.
     */
    Token getNextToken() {
        this.token = this.source.get(this.position++);
        return this.token;
    }//end getNextToken

    /**
     * O(n) where n = the number of tokens in the lexical analysis.
     * The method "While" and "if" will recurisvally call upon itself until
     * all statements are listed.
     *
     * STATEMENT class is one of two main classes that are used to translate the lexicon input into an
     * AST tree. Tokens handled here are the semicolon, identifier (assign operator), while,
     * left bracket, if, else, print, parentheses, the right bracket, and end of file
     * This method also holds a while loop.
     * @return a Node object that will be used in the tree to build the flattened AST tree.
     */
    Node stmt() {
        // this one handles TokenTypes such as Keyword_if, Keyword_else, nd_If, Keyword_print, etc.
        // also handles while, end of file, braces

        Node s, s1, t = null, e, v;
        if(this.token.tokentype == TokenType.Semicolon){
            getNextToken();
        } else if(this.token.tokentype == TokenType.Identifier){
            v = Node.make_leaf(this.token.tokentype.node_type, this.token.value);
            getNextToken();
            expect("Op_assign", TokenType.Op_assign);
            NodeType n = this.token.tokentype.node_type;
            getNextToken();
            t = Node.make_node(n, v, expr(0));
            expect(";",TokenType.Semicolon);
            getNextToken();

        }else if(this.token.tokentype == TokenType.Keyword_while) {
            getNextToken();
            e = paren_expr();
            t = Node.make_node(NodeType.nd_While,e, stmt());


        }else if(this.token.tokentype == TokenType.LeftBrace){
            getNextToken();
            while(this.token.tokentype != TokenType.RightBrace &&
                    this.token.tokentype != TokenType.End_of_input) {
                t = Node.make_node(NodeType.nd_Sequence, t, stmt());
            }//end while
            getNextToken();


        }else if (this.token.tokentype == TokenType.Keyword_if) {
            getNextToken();
            e = paren_expr();
            s = stmt();
            Node el= stmt();
            if(this.token.tokentype == TokenType.Keyword_else){
                el = stmt();
            }//end if
            //Going to make the inner if statement the true
            //statement and the else statement he left node.
            t = Node.make_node(NodeType.nd_If, e, Node.make_node(NodeType.nd_If, s, el));

        }else if (this.token.tokentype == TokenType.Keyword_print) {
            getNextToken();
            expect("(", TokenType.LeftParen);
            boolean done = false;
            while(!done){

                getNextToken();
                Node it = null;
                if(this.token.tokentype == TokenType.String){
                    it = Node.make_node(NodeType.nd_Prts,
                            Node.make_leaf(NodeType.nd_String, this.token.value));
                    getNextToken();
                }else{
                    it = Node.make_node(NodeType.nd_Prti, expr(0));
                }

                if(this.token.tokentype != TokenType.Comma){
                    done = true;
                }
                //assume that its eather a string, integer, or expr
                t = Node.make_node(NodeType.nd_Sequence, t, it);
            }

            expect(")", TokenType.RightParen);
            getNextToken();
            expect(";", TokenType.Semicolon);
            getNextToken();


        }else if (this.token.tokentype == TokenType.Keyword_putc) {
            t = paren_expr();
            getNextToken();
            expect(";", TokenType.Semicolon);
            getNextToken();

        }else if(this.token.tokentype == TokenType.RightBrace){
            getNextToken();
        }else if(this.token.tokentype == TokenType.End_of_input){
            return t;
        }


        else{
            System.out.println("Syntax error: " + this.token.tokentype + " is not in the <stmt> EBNF");
        }//end if statments


        return t;
    }
    Node paren_expr() {
        expect("paren_expr", TokenType.LeftParen);
        getNextToken();
        Node node = expr(0);
        expect("paren_expr", TokenType.RightParen);
        getNextToken();
        return node;
    }

    /**
     * O(n) where n equals the number of tokens or line supplied by the lexical analyzer
     * EXPRESSION is the second class that will build the AST flattened tree using recursion.
     * It looks for Binary, Unary, Identifier, Integer, object. Binary and unary tokens are
     * all the arithmetic operations including (><==!+-/*), Unary operation is the not operation.
     * @param p is the presedence of the operation.
     * @return the Node that will be used in building the AST flattened tree.
     */
    Node expr(int p) {
        // create nodes for token types such as LeftParen, Op_add, Op_subtract, etc.
        // be very careful here and be aware of the precendence rules for the AST tree

        Node result = null, e, v;
        if(this.token.tokentype.is_binary){
            NodeType n = this.token.tokentype.node_type;
            getNextToken(); //set up right child
            result = Node.make_node(n, null, expr(0));

        }else if(this.token.tokentype == TokenType.Integer){
            v = Node.make_leaf(NodeType.nd_Integer, this.token.value);
            getNextToken();
            if(this.token.tokentype == TokenType.Semicolon || this.token.tokentype == TokenType.RightParen){
                result = v;
            }else{
                result = expr(this.token.tokentype.precedence);
                result.left = v;
            }

        } else if(this.token.tokentype == TokenType.Identifier){
            v = Node.make_leaf(NodeType.nd_Ident, this.token.value);
            getNextToken();
            if(this.token.tokentype == TokenType.Semicolon || this.token.tokentype == TokenType.RightParen
                               || this.token.tokentype == TokenType.Comma){
                result = v;
            }else{
                result = expr(this.token.tokentype.precedence);
                result.left = v;
            }

        } else if(this.token.tokentype.is_unary){
            Token temp = this.token;
            getNextToken();
            result = Node.make_node(temp.tokentype.node_type, expr(0),null);
        } else if(this.token.tokentype == TokenType.LeftParen){

        }

        //check for precedence
        while(this.token.tokentype.is_binary && this.token.tokentype.precedence >= p){
            Token temp = this.token;
            getNextToken();
            int q = this.token.tokentype.precedence;
            if (temp.tokentype.isRightAssoc()){
                q += 1;
                result = Node.make_node(temp.tokentype.node_type, result, expr(q));
            }//end if
        }// end while
        return result;
    }

    /**
     * O(1)
     * method for checking that a certain token is where it should be.
     * If the token is incorrect, then it will output an error message on what token was
     * not correct and also the line and position of the bad token.
     * @param msg the input message on what the token should be
     * @param s the token type that should be supplied
     */
    void expect(String msg, TokenType s) {
        if (this.token.tokentype == s) {
            //getNextToken();
            return;
        }
        error(this.token.line, this.token.pos, msg + ": Expecting '" + s + "', found: '" + this.token.tokentype + "'");
    }

    /**
     * O(nLogn)
     * due to recursive nature and the building of the AST tree, the number of levels depends upon the
     * number of statements and sequences that are within the supplied Lexical analysis.
     * @return the root node of the AST tree that then can be flattened.
     */
    Node parse() {
        Node t = null;
        getNextToken();
        while (this.token.tokentype != TokenType.End_of_input) {
            t = Node.make_node(NodeType.nd_Sequence, t, stmt());
        }
        return t;
    }

    /**
     * O(n log n) by parsing through the supplied AST tree.
     * @param t
     * @param sb
     * @return
     */
    String printAST(Node t, StringBuilder sb) {
        int i = 0;
        if (t == null) {
            sb.append(";");
            sb.append("\n");
            System.out.println(";");
        } else {
            sb.append(t.nt);
            System.out.printf("%-14s", t.nt);
            if (t.nt == NodeType.nd_Ident || t.nt == NodeType.nd_Integer || t.nt == NodeType.nd_String) {
                sb.append(" " + t.value);
                sb.append("\n");
                System.out.println(" " + t.value);
            } else {
                sb.append("\n");
                System.out.println();
                printAST(t.left, sb);
                printAST(t.right, sb);
            }

        }
        return sb.toString();
    }

    static void outputToFile(String result) {
        try {
            FileWriter myWriter = new FileWriter("src/main/resources/test2.par");
            myWriter.write(result);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Map<String, TokenType> createHashMap(){
        Map<String, TokenType> str_to_tokens = new HashMap<>();

        //add tokens to map
        str_to_tokens.put("End_of_input", TokenType.End_of_input);
        str_to_tokens.put("Op_multiply", TokenType.Op_multiply);
        str_to_tokens.put("Op_divide", TokenType.Op_divide);
        str_to_tokens.put("Op_mod", TokenType.Op_mod);
        str_to_tokens.put("Op_add", TokenType.Op_add);
        str_to_tokens.put("Op_subtract", TokenType.Op_subtract);
        str_to_tokens.put("Op_negate", TokenType.Op_negate);
        str_to_tokens.put("Op_not", TokenType.Op_not);
        str_to_tokens.put("Op_less", TokenType.Op_less);
        str_to_tokens.put("Op_lessequal", TokenType.Op_lessequal);
        str_to_tokens.put("Op_greater", TokenType.Op_greater);
        str_to_tokens.put("Op_greaterequal", TokenType.Op_greaterequal);
        str_to_tokens.put("Op_equal", TokenType.Op_equal);
        str_to_tokens.put("Op_notequal", TokenType.Op_notequal);
        str_to_tokens.put("Op_assign", TokenType.Op_assign);
        str_to_tokens.put("Op_and", TokenType.Op_and);
        str_to_tokens.put("Op_or", TokenType.Op_or);
        str_to_tokens.put("Keyword_if", TokenType.Keyword_if);
        str_to_tokens.put("Keyword_else", TokenType.Keyword_else);
        str_to_tokens.put("Keyword_while", TokenType.Keyword_while);
        str_to_tokens.put("Keyword_print", TokenType.Keyword_print);
        str_to_tokens.put("Keyword_putc", TokenType.Keyword_putc);
        str_to_tokens.put("LeftParen", TokenType.LeftParen);
        str_to_tokens.put("RightParen", TokenType.RightParen);
        str_to_tokens.put("LeftBrace", TokenType.LeftBrace);
        str_to_tokens.put("RightBrace", TokenType.RightBrace);
        str_to_tokens.put("Semicolon", TokenType.Semicolon);
        str_to_tokens.put("Comma", TokenType.Comma);
        str_to_tokens.put("Identifier", TokenType.Identifier);
        str_to_tokens.put("Integer", TokenType.Integer);
        str_to_tokens.put("String", TokenType.String);

        return str_to_tokens;
    }//end createMap


    public static void main(String[] args) {
        if (1==1) {
            try {
                String value, token;
                String result = " ";
                StringBuilder sb = new StringBuilder();
                int line, pos;
                Token t;
                boolean found;
                List<Token> list = new ArrayList<>();
                Map<String, TokenType> str_to_tokens =  createHashMap();

                Scanner s = new Scanner(new File("src/main/resources/count.lex"));
                String source = " ";
                while (s.hasNext()) {
                    String str = s.nextLine();
                    StringTokenizer st = new StringTokenizer(str);
                    line = Integer.parseInt(st.nextToken());
                    pos = Integer.parseInt(st.nextToken());
                    token = st.nextToken();
                    value = "";
                    while (st.hasMoreTokens()) {
                        value += st.nextToken() + " ";
                    }
                    found = false;
                    if (str_to_tokens.containsKey(token)) {
                        found = true;
                        list.add(new Token(str_to_tokens.get(token), value, line, pos));
                    }
                    if (found == false) {
                        throw new Exception("Token not found: '" + token + "'");
                    }
                }
                Parser p = new Parser(list);
                Node parceNode = p.parse();
                result = p.printAST(parceNode, sb);
                outputToFile(result);
            } catch (FileNotFoundException e) {
                error(-1, -1, "Exception: " + e.getMessage());
            } catch (Exception e) {
                error(-1, -1, "Exception: " + e.getMessage());
            }
        } else {
            error(-1, -1, "No args");
        }
    }
}