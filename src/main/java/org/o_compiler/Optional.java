package org.o_compiler;

import java.util.function.Function;

public class Optional<T> {
    T value;

    public Optional(T toWrap) {
        value = toWrap;
    }

    public boolean isNone(){
        return value==null;
    }

    public<R> Optional<R> map(Function<T, R> mapper){
        if (value == null){
            return new Optional<>(null);
        }
        return new Optional<>(mapper.apply(value));
    }

    public T get(){
        return value;
    }

    public String toString(){
        return map(T::toString).get();
    }
}
