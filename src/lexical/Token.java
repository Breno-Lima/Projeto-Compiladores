package lexical;

import utils.TokenType;

public class Token {
	private TokenType type;
	private String text;
	
	public Token(TokenType type, String text) {
		super();
		this.type = type;
		this.text = text;
	}

	public TokenType getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", text=" + text + "]";
	}

}
