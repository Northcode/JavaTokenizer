package no.northcode.java.tokenizer;

import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import java.lang.Integer;
import java.util.HashMap;
import java.util.Map;


public class TokenPostProcessor {

    public static final Function<List<String>, Object> identity = t -> t;
    public static final Function<List<String>, Object> first = t -> t.get(0);

    Map<Integer,Function<List<String>, Object>> rules;

    public TokenPostProcessor() {
	rules = new HashMap<>();
    }

    public Result<Void,String> add_rule(int from_type, Function<List<String>,Object> processor) {
	if (rules.containsKey(from_type)) {
	    return Result.error("Conflicting rule already in processor, use another type!");
	}
	rules.put(from_type, processor);
	return Result.ok();
    }

    public Result<Void,String> add_blank(int from_type) {
	return add_rule(from_type, identity);
    }

    public void ignore_whitespace() {
	add_blank(Tokenizer.TOKEN_TYPE_CONSTANTS.WHITESPACE);
    }

    public Result<Pair<Integer, Object>,String> process_token(Tokenizer.Token t) {
	if (rules.containsKey(t.type)) {
	    try {
		Object value = rules.get(t.type).apply(t.parts);
		return Result.ok(Pair.of(t.type, value));
	    } catch (Exception ex) {
		return Result.error(String.format("Error while applying post processor: %s", ex.getMessage()));
	    }
	} else {
	    return Result.error(String.format("Cannot find post processing rule for type: %d", t));
	}
    }
}
