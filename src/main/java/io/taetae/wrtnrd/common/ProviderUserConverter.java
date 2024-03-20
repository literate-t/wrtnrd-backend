package io.taetae.wrtnrd.common;

public interface ProviderUserConverter<T, R> {

  R convert(T t);
}
