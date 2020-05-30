package com.gelerion.open.storage.api.writer;

import java.util.Collection;
import java.util.stream.Stream;

public interface StorageWriter {
    /**
     * creates new or rewrites the existing file
     */
    void write(Stream<String> content);

    void write(String content);

    void write(Collection<String> content);

    void write(byte[] content);

    /**
     * creates new or appends to the existing file
     */
    void append(Stream<String> content);

    void append(Collection<String> content);
}
