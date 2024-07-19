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
    private int dot;

    public Scanner(String source) {
        dot = 0;
        pos = 0;
        row = 1;
        col = 1;
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
                    } else if (isHashtag(currentChar)) {
                        state = 14;
                    } else if (isSpecialCharacter(currentChar)){
                        throw new RuntimeException("Invalid Char at line " + row + ", column " + col + ": " + content + currentChar);
                    } else if (isLetter(currentChar) || isUnderscore(currentChar)){
                        content += currentChar;
                        state = 1;
                    } else if (isMathOperator(currentChar)){
                        content += currentChar;
                        state = 4;
                    } else if (isAssignment(currentChar)){
                        content += currentChar;
                        state = 5;
                    } else if (isRelOperator(currentChar)){
                        content += currentChar;
                        state = 7;
                    } else if (isParenthesis(currentChar)){
                        content += currentChar;
                        state = 10;
                    } else if (isDigit(currentChar)){
                        content += currentChar;
                        state = 11;
                    } else if (isPoint(currentChar)){
                        content += currentChar;
                        state = 13;
                    }
                    break;
                case 1:
                    if( isLetter(currentChar) || isDigit(currentChar) || isUnderscore(currentChar) ) {
                        content += currentChar;
                        state = 1;
                    } else if(isReserved(content)){
                        back();
                        state = 3;
                    } else if (isSpecialCharacter(currentChar)){
                        throw new RuntimeException("Malformed Identifier at line " + row + ", column " + col + ": " + content + currentChar);
                    } else {
                        back();
                        state = 2;
                    }
                    break;

                case 2:
                    back();
                    return new Token(TokenType.IDENTIFIER, content);
                case 3:
                    back();
                    return new Token(TokenType.RESERVED, content);
                case 4:
                    back();
                    return new Token(TokenType.MATH_OPERATOR, content);
                case 5:
                    if(isAssignment(currentChar)){
                        content += currentChar;
                        state = 8;
                    } else {
                        back();
                        state = 6;
                    }
                    break;
                case 6:
                    back();
                    return new Token(TokenType.ASSIGNMENT, content);
                case 7:
                    if(isAssignment(currentChar)){
                        content+=currentChar;
                        state = 8;
                    } else {
                        back();
                        state = 9;
                    }
                    break;
                case 8:
                    back();
                    return new Token(TokenType.REL_OPERATOR, content);
                case 9:
                    back();
                    return new Token(TokenType.REL_OPERATOR, content);
                case 10:
                    back();
                    return new Token(TokenType.PARENTHESES, content);
                case 11:
                    if(isDigit(currentChar)){
                        content += currentChar;
                        state = 11;
                    } else if (isPoint(currentChar)) {
                        dot++;
                        back();
                        state = 13;
                    } else if (isLetter(currentChar) || isSpecialCharacter(currentChar)) {
                        throw new RuntimeException("Malformed number at line " + row + ", column " + col + ": " + content + currentChar);
                    } else {
                        back();
                        state = 12;
                    }
                    break;
                case 12:
                    back();
                    return new Token(TokenType.NUMBER, content);
                case 13:
                    if (isDigit(currentChar)){
                        content += currentChar;
                        state = 13;
                    } else if (isPoint(currentChar)) {
                        dot += 1;
                        content += currentChar;
                        state = 13;
                    } else if( content.charAt(content.length() - 1) == '.' || dot > 2){
                        back();
                        back();
                        throw new RuntimeException("Malformed number at line " + row + ", column " + col + ": " + content + currentChar);
                    } else {
                        back();
                        state = 12;
                    }
                    break;
                case 14:
                    while (!isEndOfLine(currentChar)) {
                        currentChar = this.nextChar();
                    }
                    state = 0;
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isHashtag(char c) {
        return c == '#';
    }

    private boolean isAssignment(char c) {
        return c == '=';
    }

    private boolean isEndOfLine(char c) {
        return c == '\n' || c == '\r';
    }

    private boolean isReserved(String c) {
        return c.equals("int") || c.equals("float") ||
                c.equals("print") || c.equals("if") ||
                c.equals("else");
    }

    private boolean isUnderscore(char c) {
        return c == '_';
    }

    private boolean isPoint(char c) {
        return c == '.';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    private boolean isRelOperator(char c) {
        return c=='=' || c == '>' || c == '<' || c == '!' ;
    }

    private boolean isMathOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isParenthesis(char c) {
        return c == '(' || c == ')';
    }

    private boolean isSpecialCharacter(char c) {
        return !(isParenthesis( c) || isMathOperator( c) ||
                isRelOperator(c) || isSpace(c) || isLetter(c) ||
                isDigit(c) || isPoint(c) || isUnderscore(c) ||
                isAssignment(c) || isHashtag(c));
    }

    private char nextChar() {
        char c = sourceBuffer[pos++];
        if (c == '\n') {
            row++;
            col = 1;
        } else {
            col++;
        }
        return c;
    }

    private void back() {
        pos--;
        char c = sourceBuffer[pos];
        if (c == '\n') {
            row--;
        } else {
            col--;
        }
    }

    private boolean isEOF() {
        if(pos >= sourceBuffer.length) {
            return true;
        }
        return false;
    }
}

