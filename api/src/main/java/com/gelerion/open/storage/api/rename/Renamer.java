package com.gelerion.open.storage.api.rename;

import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.function.Function;

//TODO; return types
public interface Renamer<T extends StoragePath<T>> {

    T to(String target);

    T to(T target);

    T to(Function<T, T> func);
}
