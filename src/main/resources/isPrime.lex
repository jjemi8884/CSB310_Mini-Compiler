 4      1 Identifier      count
    4      7 Op_assign
    4      9 Integer             1
    4     10 Semicolon
    5      1 Identifier      n
    5      3 Op_assign
    5      5 Integer             1
    5      6 Semicolon
    6      1 Identifier      limit
    6      7 Op_assign
    6      9 Integer           100
    6     12 Semicolon
    7      1 Keyword_while
    7      7 LeftParen
    7      8 Identifier      n
    7     10 Op_less
    7     12 Identifier      limit
    7     17 RightParen
    7     19 LeftBrace
    8      5 Identifier      k
    8      6 Op_assign
    8      7 Integer             3
    8      8 Semicolon
    9      5 Identifier      p
    9      6 Op_assign
    9      7 Integer             1
    9      8 Semicolon
   10      5 Identifier      n
   10      6 Op_assign
   10      7 Identifier      n
   10      8 Op_add
   10      9 Integer             2
   10     10 Semicolon
   11      5 Keyword_while
   11     11 LeftParen
   11     12 LeftParen
   11     13 Identifier      k
   11     14 Op_multiply
   11     15 Identifier      k
   11     16 Op_lessequal
   11     18 Identifier      n
   11     19 RightParen
   11     21 Op_and
   11     24 LeftParen
   11     25 Identifier      p
   11     26 RightParen
   11     27 RightParen
   11     29 LeftBrace
   12      9 Identifier      p
   12     10 Op_assign
   12     11 Identifier      n
   12     12 Op_divide
   12     13 Identifier      k
   12     14 Op_multiply
   12     15 Identifier      k
   12     16 Op_notequal
   12     18 Identifier      n
   12     19 Semicolon
   13      9 Identifier      k
   13     10 Op_assign
   13     11 Identifier      k
   13     12 Op_add
   13     13 Integer             2
   13     14 Semicolon
   14      5 RightBrace
   15      5 Keyword_if
   15      8 LeftParen
   15      9 Identifier      p
   15     10 RightParen
   15     12 LeftBrace
   16      9 Keyword_print
   16     14 LeftParen
   16     15 Identifier      n
   16     16 Comma
   16     18 String          " is prime\n"
   16     31 RightParen
   16     32 Semicolon
   17      9 Identifier      count
   17     15 Op_assign
   17     17 Identifier      count
   17     23 Op_add
   17     25 Integer             1
   17     26 Semicolon
   18      5 RightBrace
   19      1 RightBrace
   20      1 Keyword_print
   20      6 LeftParen
   20      7 String          "Total primes found: "
   20     29 Comma
   20     31 Identifier      count
   20     36 Comma
   20     38 String          "\n"
   20     42 RightParen
   20     43 Semicolon
   21      1 End_of_input