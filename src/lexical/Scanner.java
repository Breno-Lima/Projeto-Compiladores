package lexical;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import utils.TokenType;

public class Scanner {
	private int state;
	private char[] sourceBuffer;
	private int pos;
	private int row;
	private int col;
	
	public Scanner(String source) {
		pos = 0;
		row = 0;
		col = 0;
		try {
			String buffer = new String(Files.readAllBytes(Paths.get(source)));
			sourceBuffer = buffer.toCharArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Token nextToken() {
		state = 0;
		char currentChar;
		String content = "";
		while(true) {
			if(isEOF()) {
				return null;
			}
			currentChar = nextChar();
			
			switch (state) {
			case 0:
				if(isSpace(currentChar)) {
					state = 0;
				} else if(Character.isLetter(currentChar)) {
					content += currentChar;
					state = 1;
				} else if (Character.isDigit(currentChar)) {
					content += currentChar;
					state = 3;
				} else {
					throw new RuntimeException("Invalid character: " + currentChar);
				}
				break;
			case 1:
				if(Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
					content += currentChar;
					state = 1;
				}
				else {
					state = 2;
				}
				break;
			
			case 2:
				back();
				return new Token(TokenType.IDENTIFIER, content);
			
			case 3:
				if(Character.isDigit(currentChar)) {
					content += currentChar;
					state = 3;
				} else if (isOperator(currentChar) || isSpace(currentChar)) {
					state = 4;
				} else {
					throw new RuntimeException("Malformed number: " + content+currentChar);
				}
				break;
		
			case 4:
				back();
				return new Token(TokenType.NUMBER, content);
			default:
				break;
			}
			
		}
		
	}
	
	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	private boolean isSpace(char c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\r';
	}
	
	private boolean isOperator(char c) {
		return c=='=' || c == '>' || c == '<' || c == '!' || c == '+' || c == '-' ||
				c == '*' || c == '/';
	}
	
	private char nextChar() {
		return sourceBuffer[pos++];
	}
	
	private void back() {
		pos--;
	}
	
	private boolean isEOF() {
		if(pos >= sourceBuffer.length) {
			return true;
		}
		return false;
	}
	

}





