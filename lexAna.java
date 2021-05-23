import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class Lexical {
	//variables
	static int charClass;
	static char[] lexeme = new char[100];
	static char nextChar;
	static int lexLen;
	static int tok;
	static int nextTok;
	static File in_fp;
	static int states = 0;

	//Char Types
	static final int LETTER = 0;
	static final int DIGIT = 1;
	static final int OPERATOR = 29;
	static final int BOOL = 30;
	static final int UNKNOWN = 99;
	static final int EOF = -1;
	static final int EQUALITY = 35;
	static final int RPAREN = 34;
	static final int LPAREN = 36;
	static final int DOT = 37;

	//char values
	static final int STR_LIT = 1;
	static final int IDENT = 2;
	static final int INT_LIT = 3;
	static final int FLO_LIT = 4;
	static final int dot = 5;
	static final int QUOTES = 6;
	static final int DUB_EQ = 10;
	static final int NOT_EQ = 11;
	static final int EQ = 12;
	static final int LE = 13;
	static final int GR = 15;
	static final int LE_EQ = 16;
	static final int GR_EQ = 17;
	static final int ADD_OP = 18;
	static final int SUB_OP = 19;
	static final int MULT_OP = 20;
	static final int DIV_OP = 21;
	static final int MOD_OP = 22;
	static final int L_PARE = 25;
	static final int R_PARE = 26;
	static final int AND_OP =27;
	static final int OR_OP = 28;
	static final int EXCL = 31;
	static final int LESS = 32; 
	static final int MORE = 33;
		
	//special characters
	static int Flag = 0;


	static int lookup(char ch){
		switch (ch) {
		case '.':
			addChar();
			nextTok = dot;
			break;
		case '"':
			addChar();
			nextTok = QUOTES;
			break;
		case '(':
			addChar();
			nextTok = L_PARE;
			break;
		case ')':
			addChar();
			nextTok = R_PARE;
			break;
		case '+':
			addChar();
			nextTok = ADD_OP;
			break;
		case '-':
			addChar();
			nextTok = SUB_OP;
			break;
		case '*':
			addChar();
			nextTok = MULT_OP;
			break;
		case '/':
			addChar();
			nextTok = DIV_OP;
			break;
		case '%':
			addChar();
			nextTok = MOD_OP;
			break;
		case '!':
			addChar();
			nextTok = EXCL;
			break;
		case '=':
			addChar();
			nextTok = EQ;
		case '<':
			addChar();
			nextTok = LESS;
		case '>':
			addChar();
			nextTok = MORE;

		default:
			addChar();
			nextTok = 0;
			break;
		}
		return nextTok;
	}

	static void addChar(){
		if (lexLen <= 98) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		} else {
			System.out.println("Error - lexeme is too long \n");
		}
	}
	static void getChar(BufferedReader br) throws IOException{
		int nc;
		char f = 'f';
		char e = 'e';
		if ((nc = br.read()) != -1) { 
			nextChar = (char) nc;
			if (Character.isLetter(nextChar)) {
				if(nextChar == e) {
					Flag = 1;
				}
				if(nextChar == f) {
					Flag = 2;
				}
				charClass = LETTER;
			} else if (Character.isDigit(nextChar)) {
				charClass = DIGIT;
			}else if(nextChar == '(') {
				charClass = L_PARE;
				states = LPAREN;
			} else if(nextChar == ')') {
				charClass = R_PARE;
				states = RPAREN;
			} else if(nextChar == '*') {
				charClass = MULT_OP;
				states = OPERATOR;
			} else if(nextChar == '/') {
				charClass = DIV_OP;
				states = OPERATOR;
			} else if(nextChar == '+') {
				charClass = ADD_OP;
				states = OPERATOR;
			} else if(nextChar == '-') {
				charClass = SUB_OP;
				states = OPERATOR;
			} else if(nextChar == '.') {
				charClass = dot;
				states = DOT;
			} else if(nextChar == '<') {
				charClass = EQ;	
				states = EQ;
			} else if(nextChar == '>') {
				charClass = EQ;
				states = EQUALITY;
			} else if(nextChar == '=') {
				charClass = EQ;
				states = EQUALITY;
			} else {
				charClass = UNKNOWN;
			}
		} else {
			charClass = EOF;
		}
	}
	static void getNonBlank(BufferedReader br) throws IOException {
		while (Character.isWhitespace(nextChar)) {
			getChar(br);
		}
	}

	static int lex(BufferedReader br) throws IOException {
		lexeme = new char[lexeme.length];
		lexLen = 0;
		getNonBlank(br);
		switch (charClass) {
		/* Identifiers */
		case LETTER:
			addChar();
			getChar(br);
			while (charClass == LETTER || charClass == DIGIT) {
				addChar();
				getChar(br);
			}
			nextTok = IDENT;
			break;
			/* Integer literals and Float Literals*/
		case DIGIT:
			addChar();
			getChar(br);
			while (charClass == DIGIT) {
				addChar();
				getChar(br);
			}
			if(charClass == UNKNOWN) {
				lookup(nextChar);
				if(nextTok == dot) {
					getChar(br);
					while(charClass == DIGIT || (charClass == LETTER && Flag == 1)) {
						addChar();
						getChar(br);
					}
					if(charClass == LETTER && Flag == 2) {
						addChar();
						getChar(br);
					}
					nextTok = FLO_LIT;
					break;
				}		
			}
			nextTok = INT_LIT;
			break;
			/*quotes for Strings*/
		case UNKNOWN:
			lookup(nextChar);
			if(nextTok == QUOTES) {
				getChar(br);
				while (charClass == LETTER || charClass == DIGIT) {
					addChar();
					getChar(br);
				}
				addChar();
				getChar(br);
				nextTok = STR_LIT;	
			}
			if(nextTok == dot) {
				getChar(br);
				while(charClass == DIGIT || (charClass == LETTER && Flag == 1)) {
					addChar();
					getChar(br);
				}
				if(charClass == LETTER && Flag == 2) {
					addChar();
					getChar(br);
				}
				nextTok = FLO_LIT;
			}
			if(nextTok == EQ) {
				getChar(br);
				while(charClass == DIGIT || charClass == LETTER) {
					addChar();
					getChar(br);
				}
				nextTok = IDENT;
			} else {
				getChar(br);
			}
			break;
	    /* EOF */
		case EOF:
			nextTok = 0;
			lexeme[0] = 0;
			break;
		} /* End of switch */
		System.out.print("Lexeme is: ");
		for(int i=0; i<lexeme.length;i++) {
			System.out.print(lexeme[i]);
		}
		System.out.print("\n");
		System.out.print("\n");
		return nextTok;
	}

	public static void assign(BufferedReader br) throws IOException {
		//System.out.println("Enter <assign>\n");
		logic(br);
		while (nextTok == EQ) {
			lex(br);
			logic(br);
		}
		//System.out.println("Exit <assign>\n");
	}

	public static void logic(BufferedReader br) throws IOException {
		//for debugging purposes
		//System.out.println("Enter <logic>\n");
		equal(br);
		while (nextTok == AND_OP || nextTok == OR_OP) {
			lex(br);
			equal(br);
		}
		//System.out.println("Enter <logic>\n");
	}
	public static void equal(BufferedReader br) throws IOException {
		//System.out.println("Enter <equal>\n");
		rel(br);
		while (nextTok == DUB_EQ || nextTok == NOT_EQ) {
			lex(br);
			rel(br);
		}
		//System.out.println("Enter <equal>\n");
	}

	public static void rel(BufferedReader br) throws IOException {
		//System.out.println("Enter <rel>\n");
		add(br);
		while (nextTok == LE || nextTok == LE_EQ || nextTok == GR || nextTok == GR_EQ) {
			lex(br);
			add(br);
		}
		//System.out.println("Enter <rel>\n");
	}

	public static void add(BufferedReader br) throws IOException {
		//System.out.println("Enter <add>\n");
		mult(br);
		while (nextTok == ADD_OP || nextTok == SUB_OP) {
			lex(br);
			mult(br);
		}
		//System.out.println("Enter <add>\n");
	}
	public static void mult(BufferedReader br) throws IOException {
		//System.out.println("Enter <mult>\n");
		term(br);
		while (nextTok == MULT_OP || nextTok == DIV_OP || nextTok == MOD_OP) {
			lex(br);
			term(br);
		}
		//System.out.println("Enter <mult>\n");
	}

	public static void term(BufferedReader br) throws IOException {
		//System.out.println("Enter <term>\n");
		if (nextTok == IDENT || nextTok == INT_LIT) {
			lex(br);
		} else {
			if (nextTok == L_PARE) {
				lex(br);
				assign(br);
				if (nextTok == R_PARE) {
					lex(br);
				}
				else {
					error();
				}
			} else
				error();
		}
		//System.out.println("Exit <term>\n");
	}
	
	static void error() {
		System.out.println("Error = Symbol not found");
	}

	public static void main(String[]args) throws IOException{
		System.out.println("State Diagram Recognition");
		if ((in_fp = new File("lexTester.txt")) == null) {
			System.out.println("ERROR - cannot open file \n");
		}
		else {
			BufferedReader br = new BufferedReader(new FileReader(in_fp));
			getChar(br);
			do {
				lex(br);
				assign(br);
			} while (nextTok != 0);
			br.close();
		}
	}	
}
