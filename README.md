# programming_languages
Programs for languages, compilation and more.

The foundational work being used for study and reference: https://cs.brown.edu/courses/cs173/2012/book/

## BNF for SimpleCompiler.java


<expression> ::= <term> { ('+' | '-') <term> }
<term> ::= <factor> { ('*' | '/') <factor> }
<factor> ::= '(' <expression> ')' | <number>
<number> ::= digit { digit }
digit ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'

Here, the {} brackets denote zero or more repetitions, () brackets denote grouping, and | denotes a choice.

In plain English:

An <expression> is one or more <term>s separated by addition or subtraction operators.
A <term> is one or more <factor>s separated by multiplication or division operators.
A <factor> is either an <expression> in parentheses or a <number>.
A <number> is one or more digits.
A digit is any single digit from 0 to 9.
This language supports addition, subtraction, multiplication, and division operations, with the usual operator precedence and associativity. It also supports parenthesized expressions.


