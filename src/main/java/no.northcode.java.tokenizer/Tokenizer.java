package no.northcode.java.tokenizer;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.function.Function;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import no.northcode.java.tokenizer.Result;
import java.util.regex.Matcher;

public class Tokenizer {

    public static class Token {
	int type;
	List<String> parts;

	public Token(int type, List<String> parts) {
	    this.type = type;
	    this.parts = parts;
	}
    }

    public static class TokenMatcher {
	public Pattern pattern;
	public int to_type;

	TokenMatcher(Pattern pattern, int to_type) {
	    this.pattern = pattern;
	    this.to_type = to_type;
	}

	public static Result<TokenMatcher,String> make(String pattern, int to_type) {
	    try {
		Pattern pat = Pattern.compile(pattern);
		return Result.ok(new TokenMatcher(pat, to_type));
	    } catch (Exception ex) {
		return Result.error(ex.getMessage());
	    }
	}
    }

    List<TokenMatcher> matchers;

    public Tokenizer() {
	matchers = new ArrayList<>();
    }

    public static class TOKEN_TYPE_CONSTANTS {
	public static final int WHITESPACE = 0;
    }

    public static Tokenizer default_whitespace() {
	Tokenizer tokenizer = new Tokenizer();
	TokenMatcher.make("(\\s+)", TOKEN_TYPE_CONSTANTS.WHITESPACE)
	    .flatMap(m -> tokenizer.add_matcher(m));

	return tokenizer;
    }

    public Result<Void,String> add_matcher(TokenMatcher matcher) {
	boolean conflicts = matchers.stream().anyMatch(m -> m.to_type == matcher.to_type);
	if (! conflicts) {
	    matchers.add(matcher);
	    return Result.ok();
	} else {
	    return Result.error("Matcher conflicts with another already in the set!");
	}
    }

    public Result<Stream<Token>, String> tokenize(String to_parse) {
	Stream.Builder<Token> builder = Stream.<Token>builder();

	String current = to_parse;

	int curr_pos = 0;

	boolean found_match = false;
	boolean at_end = false;
	do {
	    // System.out.println("Testing string: " + current);
	    found_match = false;
	    for (TokenMatcher m : matchers) {
		Matcher matcher = m.pattern.matcher(current);
		if (matcher.find() && matcher.start() == 0) {
		    // System.out.println(String.format("Found a match: %d - %d", matcher.start(), matcher.end()));
		    int endofmatch = matcher.end();

		    List<String> groups = new ArrayList<>();

		    for (int i = 0; i < matcher.groupCount(); i++) {
			groups.add(matcher.group(i));
		    }

		    Token tok = new Token(m.to_type, groups);
		    builder.accept(tok);

		    if (endofmatch >= current.length()) {
			at_end = true;
			break;
		    }

		    curr_pos += endofmatch;
		    current = current.substring(endofmatch);

		    found_match = true;
		    break;
		}
	    }
	} while (found_match && !at_end);

	if (!at_end && !found_match) {
	    return Result.error(String.format("Failed to parse token at: %d", curr_pos));
	}

	return Result.ok(builder.build());
    }
    
}
