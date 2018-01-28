package no.northcode.java.tokenizer;


public class Program {

    public static final int NUMBER = 1;
    public static final int WORD = 2;
    
    public static void main(String[] args) {
	System.out.println("Hello world!");

	Tokenizer tokenizer = Tokenizer.default_whitespace();

	Tokenizer.TokenMatcher.make("([a-zA-Z]+)", WORD)
	    .flatMap(m -> tokenizer.add_matcher(m))
	    .on_error(System.err::println);

	Tokenizer.TokenMatcher.make("(\\d+(?:\\.\\d+)?)", NUMBER)
	    .flatMap(m -> tokenizer.add_matcher(m))
	    .on_error(System.err::println);

	tokenizer.tokenize("123this -3.14159is2a3test")
	    .on_error(System.out::println)
	    .consume(strem ->
		     strem.forEach(t ->
				   System.out.println(String.format("Token: { type: %d, parts: %s }", t.type, String.join(",", t.parts)))));

	
	
    }
}
