package no.northcode.java.tokenizer;

import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T,Q> {
    T value;
    Q error;

    boolean is_ok;

    public Result(boolean is_ok, T value, Q error) {
	this.is_ok = is_ok;
	this.value = value;
	this.error = error;
    }

    public static <Q> Result<Void,Q> ok() {
	return new Result<Void,Q>(true, new Void(), null);
    }

    public static <T,Q> Result<T,Q> ok(T value) {
	return new Result<T,Q>(true, value, null);
    }

    public static <T,Q> Result<T,Q> error(Q error) {
	return new Result<T,Q>(false, null, error);
    }

    public <E> Result<E,Q> map(Function<T,E> mapper) {
	if (is_ok) {
	    return Result.ok(mapper.apply(value));
	} else {
	    return Result.error(error);
	}
    }

    public <E,W> Result<E,Q> flatMap(Function<T,Result<E,Q>> mapper) {
	if (is_ok) {
	    Result<E,Q> res = mapper.apply(value);
	    return res;
	} else {
	    return Result.error(error);
	}
    }

    public void consume(Consumer<T> consumer) {
	if (is_ok) {
	    consumer.accept(value);
	}
    }

    public Result<T,Q> on_error(Consumer<Q> consumer) {
	if (! is_ok) {
	    consumer.accept(error);
	}
	return this;
    }
}
