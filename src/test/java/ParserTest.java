import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {


    @Test
    @DisplayName("Test of precedence parser")
    void testOfPrecedenceParse(){
            //create the test list for "Count = 3 * 4 + 6"
            List<Parser.Token>list=new ArrayList<>();
        list.add(new Parser.Token(Parser.TokenType.Identifier,"Count",1,1));
        list.add(new Parser.Token(Parser.TokenType.Op_assign,"=",1,2));
        list.add(new Parser.Token(Parser.TokenType.Integer,"3",1,3));
        list.add(new Parser.Token(Parser.TokenType.Op_multiply,"*",1,4));
        list.add(new Parser.Token(Parser.TokenType.Integer,"4",1,5));
        list.add(new Parser.Token(Parser.TokenType.Op_add,"+",1,6));
        list.add(new Parser.Token(Parser.TokenType.Integer,"6",1,7));
        list.add(new Parser.Token(Parser.TokenType.Semicolon,";",1,8));
        list.add(new Parser.Token(Parser.TokenType.End_of_input," ",1,9));

        Parser p=new Parser(list);
        Parser.Node root=p.parse();

        // build what the result should be in a string
        StringBuilder er=new StringBuilder();
        er.append("Sequence\n");
        er.append(";\n");
        er.append("Assign\n");
        er.append("Identifier Count\n");
        er.append("Add\n");
        er.append("Multiply\n");
        er.append("Integer 3\n");
        er.append("Integer 4\n");
        er.append("Integer 6\n");
        String expectedResult=er.toString();

        StringBuilder sb=new StringBuilder();

        String result=p.printAST(root,sb);

        //test to see if they are the same
        assertEquals(expectedResult,result);
        }//end parse test

    @Test
    @DisplayName("Test of IF statements in Parser")
    void testOfIfStatementsInParser() {
        List<Parser.Token>list=new ArrayList<>();
        list.add(new Parser.Token(Parser.TokenType.Keyword_if,"Count",1,1));
        list.add(new Parser.Token(Parser.TokenType.LeftParen,"",1,2));
        list.add(new Parser.Token(Parser.TokenType.Identifier,"n",1,3));
        list.add(new Parser.Token(Parser.TokenType.RightParen,"",1,4));
        list.add(new Parser.Token(Parser.TokenType.Keyword_print,"",1,5));
        list.add(new Parser.Token(Parser.TokenType.LeftParen,"",1,6));
        list.add(new Parser.Token(Parser.TokenType.Identifier,"n",1,7));
        list.add(new Parser.Token(Parser.TokenType.Comma," " ,1,8));
        list.add(new Parser.Token(Parser.TokenType.String,"is a prime\n ",1,9));
        list.add(new Parser.Token(Parser.TokenType.RightParen, "", 1, 10));
        list.add(new Parser.Token(Parser.TokenType.Semicolon, ";", 1, 11));
        list.add(new Parser.Token(Parser.TokenType.End_of_input, "", 1, 12));

        StringBuilder er=new StringBuilder();
        er.append("Sequence\n");
        er.append(";\n");
        er.append("If\n");
        er.append("Identifier n\n");
        er.append("If\n");
        er.append("Sequence\n");
        er.append("Sequence\n");
        er.append(";\n");
        er.append("Prti\n");
        er.append("Identifier n\n");
        er.append(";\n");
        er.append("Prts\n");
        er.append("String is a prime\n");
        er.append(" \n");
        er.append(";\n");
        er.append(";\n");

        Parser p = new Parser(list);
        Parser.Node root = p.parse();

        String expectedResult=er.toString();

        StringBuilder sb=new StringBuilder();

        String result=p.printAST(root,sb);

        //test to see if they are the same
        assertEquals(expectedResult,result);

    }

    @Test
    @DisplayName("Test of the Parser with IsPrime Lex")
   void testOfTheParserWithIsPrimeLex() throws Exception {

        //get the file
        File f = new File("src/main/resources/isPrime.lex");
        Scanner s = new Scanner(f);
        String lexOut = " ";


        Map<String, Parser.TokenType> stt = Parser.createHashMap();
        List<Parser.Token> tokenList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        Main.updateTokenList(tokenList, stt,s);
        Parser p = new Parser(tokenList);
        Parser.Node root = p.parse();
        String output = p.printAST(root, sb);
        FileWriter wf = new FileWriter("src/main/resources/isPrimeTest.par");
        Parser.outputToFile(output,wf);

        File f2 = new File("src/main/resources/CorrectIsPrime.par");
        File f3 = new File("src/main/resources/isPrimeTest.par");

        Scanner s2 = new Scanner(f2);
        StringBuilder correctOutput = new StringBuilder();
        StringBuilder actualOutput = new StringBuilder();


        while(s2.hasNext()){
            String ts = s2.nextLine();
            ts.replace("\\s", "");
            correctOutput.append(ts + "\n");
        }
        Scanner s3 = new Scanner(f3);
        while(s3.hasNext()){
            String ts = s3.nextLine();
            ts.replace("\\s", "");
            actualOutput.append(ts + "\n");
        }
        String correctOut = correctOutput.toString();
        String actualOut = actualOutput.toString();


        assertEquals(correctOut, actualOut);


    }
    

}