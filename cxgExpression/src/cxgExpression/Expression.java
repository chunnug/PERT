package cxgExpression;
//CXG230014



import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
      
/** Class to store a node of expression tree
  For each internal node, element contains a binary operator
  List of operators: +|*|-|/|%|^
  Other tokens: (|)
  Each leaf node contains an operand (long integer)
*/

public class Expression {
  public enum TokenType {  // NIL is a special token that can be used to mark bottom of stack
	PLUS, TIMES, MINUS, DIV, MOD, POWER, OPEN, CLOSE, NIL, NUMBER
  }
 
  public static class Token {
	TokenType token;
	int priority; // for precedence of operator
	Long number;  // used to store number of token = NUMBER
	String string; //stores actual token string

	Token(TokenType op, int pri, String tok) {
	    token = op;
	    priority = pri;
	    number = null;
	    string = tok; 
	}

	// Constructor for number.  To be called when other options have been exhausted.
	Token(String tok) {
	    token = TokenType.NUMBER;
	    number = Long.parseLong(tok);
	    string = tok;
	}
	
	boolean isOperand() { return token == TokenType.NUMBER; }
      //if the token is an operand, we receive it's numerical value
	public long getValue() {
	    return isOperand() ? number : 0;
	}

      //return string representation of the token
	public String toString() { return string; }
  }
//stores the token associated with a node in expression tree
  Token element;
  //left and right subtrees
  Expression left, right;

  // Create token corresponding to a string
  // tok is "+" | "*" | "-" | "/" | "%" | "^" | "(" | ")"| NUMBER
  // NUMBER is either "0" or "[-]?[1-9][0-9]*
  
/**
* Method will convert a String to a Token based on a switch case
* The token is given a priority based on order of precedence for operators
* 
* @param tok, the element to be turned into a token object
* @return result, which is the token object representing the passed String
*/
  static Token getToken(String tok) {
  Token result;
  switch (tok) {
      case "+":
          result = new Token(TokenType.PLUS, 1, tok); 
          break;
      case "-":
          result = new Token(TokenType.MINUS, 1, tok); // same priority as -, must compare depending on stack
          break;
      case "*":
          result = new Token(TokenType.TIMES, 2, tok);
          break;
      case "/":
          result = new Token(TokenType.DIV, 2, tok);
          break;
      case "%":
          result = new Token(TokenType.MOD, 2, tok);
          break;
      case "^":
          result = new Token(TokenType.POWER, 3, tok);
          break;
      case "(":
          result = new Token(TokenType.OPEN, 4, tok);
          break;
      case ")":
          result = new Token(TokenType.CLOSE, 4, tok);
          break;
      default:
          // If it's not an operator, assume it's a number
          result = new Token(tok);
          //get the numerical value by parsing 
          result.number = Long.parseLong(tok);
          break;
  }
  return result;
}

  
  private Expression() {
	element = null;
  }
  
  private Expression(Token oper, Expression left, Expression right) {
	this.element = oper;
	this.left = left;
	this.right = right;
  }

  private Expression(Token num) {
	this.element = num;
	this.left = null;
	this.right = null;
  }
  
  /**
   * Given a list of tokens that corresponds to an infix expression,
   * this method will turn the input into an expression Tree
   * 
   * @param exp, a list of tokens that are in infix notation
   * @return expTree.pop(), the final expression tree on top of the stack,
   * is popped, and that is what we return 
   */
public static Expression infixToExpression(List<Token> exp) {
  Deque<Expression> expTree = new ArrayDeque<>();
  Deque<Token> s = new ArrayDeque<>();
  List<Token> Postfix = infixToPostfix(exp); // convert infix to postfix

  for (int index = 0; index < Postfix.size(); index++) {
  Token currentToken = Postfix.get(index);
  //if node is an operand, we can just push it to our tree stack after making a node
      if (currentToken.isOperand()) {
          Expression Node = new Expression(currentToken); //create a node of currentToken
          expTree.push(Node); //push operand nodes to the expTree stack 
      } else
      // whenever we have an operator we need to pop two operands so we can evaluate the expression
      {
          Expression rightNode = expTree.pop(); //first operand  popped is the right node 
          Expression leftNode = expTree.pop(); //second is the left node
          Expression newTree = new Expression(currentToken, leftNode, rightNode); //create new subtree
          expTree.push(newTree); //push new subtree onto stack 
      }
  }

  return expTree.pop(); 
}



  
/** 
 * List of tokens that correspond to an infix expression is returned into a postfix expression,
 * 
 * @param exp, a list of tokens representing the infix expression
 * @return output, returns a list of the tokens representing postfix
 */
  public static List<Token> infixToPostfix(List<Token> exp) {
  Deque<Token> s = new ArrayDeque<>();  //stack for operators
  Queue<Token> output = new LinkedList<>(); //output queue
      
  for (int index = 0; index < exp.size(); index++) {
      Token currentToken = exp.get(index);
     

      if (currentToken.isOperand()) {
          output.add(currentToken); //add operands directly to output
         
      }
      //open parenthesis has highest priority 
      else if (currentToken.token == TokenType.OPEN) {
         
          s.push(currentToken);
      //while the token is close, we pop the head of the stack until it points to open parenthesis    
      }
      else if (currentToken.token == TokenType.CLOSE) {
          while (!s.isEmpty() && s.peek().token != TokenType.OPEN) {
              output.add(s.pop());
          }
          if (!s.isEmpty() && s.peek().token == TokenType.OPEN) {
              s.pop(); // disgard opening parenthesis
              
          } 
      }
      else  {
          //we pop operators while stack is not empty and the head token is not open parenthesis, and the top of the stack is the same or higher priority as currentToken
          while (!s.isEmpty() && s.peek().token != TokenType.OPEN && s.peek().priority >= currentToken.priority) {
              output.add(s.pop());
          }
          //when the top of the stack is no longer a higher priority, we push currentToken
          s.push(currentToken);
          
      }
     
  }

  //pop anything remaining from stack to the output because at this point we have reached end of expression
  while (!s.isEmpty()) {
      output.add(s.pop());
  }


 

  return new LinkedList<>(output);
}


  /**
   * From a postfix expression, this method will evaluate the given expression,
   * by pushing operands onto the stack, and popping appropriate values when an operator is encountered
   * and performing the given behaviors based on the token
   * 
   * @param exp, the list of tokens representing a postfix expression
   * @return result, which is the resultant of the value of the postfix expression
   */
public static long evaluatePostfix(List<Token> exp) {
  Deque<Long> s = new ArrayDeque<>();  //stack to hold operands

      for (int index = 0; index < exp.size(); index++) {
      Token currentToken = exp.get(index);
      
      

//if our token is a operand, we can easily push it to the stack
      if (currentToken.token == TokenType.NUMBER) {
           
          
          
          s.push(currentToken.getValue()); // add operands to the stack
         
      } else {
          //when the currentToken is an operator, we pop two operands from the stack 
          Long O1 = s.pop();
          Long O2 = s.pop();
          
//perform the operation needed based on what token currentToken is 
          switch (currentToken.token) {
              case PLUS:
                  s.push(O1 + O2);
                  break;
              case MINUS:
                  s.push(O2 - O1);
                  break;
              case TIMES:
                  s.push(O1 * O2);
                  break;
              case DIV:
                  s.push(O2 / O1);
                  break;
              case MOD:
                  s.push(O2 % O1);
                  break;
              case POWER:
                  s.push((long) Math.pow(O2, O1));
                  break;
              default:
                  break;
          }
         
      }
  }

  //after processing the entire expression we should be left with one expression at top of stack
  long result = s.pop();
 
  return result;
}


/**
* given an expression tree, the method will traverse through the tree using recursion and return the value
* of the expression, based on the operators encountered in the nodes
* 
* @param tree, the given expression tree we must evaluate
* @return the result of the operation is returned as a long type
*/
  // Given an expression tree, evaluate it and return its value.// Given an expression tree, evaluate it and return its value.
public static long evaluateExpression(Expression tree) {
  //if tree is a leaf node we can just return the value
  if (tree.left == null && tree.right == null) {
      return tree.element.getValue();
  } else 
  //we need to recursively evaluate both sides of the subtree
  {
      var root = tree.element.token;
      long L = evaluateExpression(tree.left);
      long R = evaluateExpression(tree.right);
//based on what the root value is, we perform the operation needed
      switch (root) {
          case PLUS -> {
              return L + R;
          }
          case MINUS -> {
              return L - R;  
          }
          case TIMES -> {
              return L * R;
          }
          case DIV -> {
              return L / R;  
          }
          case MOD -> {
              return L % R;  
          }
          case POWER -> {
              return (long) Math.pow(L, R);
          }
          //if the root is not any of the other operators, we cannot properly evaluate it 
          default -> throw new IllegalArgumentException("invalid operator: " + root);
      }
  }
}




  // sample main program for testing
  public static void main(String[] args) throws FileNotFoundException {
	Scanner in;
	
	if (args.length > 0) {
	    File inputFile = new File(args[0]);
	    in = new Scanner(inputFile);
	} else {
	    in = new Scanner(System.in);
	}

	int count = 0;
	while(in.hasNext()) {
	    String s = in.nextLine();
	    List<Token> infix = new LinkedList<>();
	    Scanner sscan = new Scanner(s);
	    int len = 0;
	    while(sscan.hasNext()) {
		infix.add(getToken(sscan.next()));
		len++;
	    }
	    if(len > 0) {
		count++;
		System.out.println("Expression number: " + count);
		System.out.println("Infix expression: " + infix);
		Expression exp = infixToExpression(infix);
		List<Token> post = infixToPostfix(infix);
		System.out.println("Postfix expression: " + post);
		long pval = evaluatePostfix(post);
		long eval = evaluateExpression(exp);
		System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
	    }
	}
  }
}
