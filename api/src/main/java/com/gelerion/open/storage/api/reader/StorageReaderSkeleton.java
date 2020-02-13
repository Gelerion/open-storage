package com.gelerion.open.storage.api.reader;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public abstract class StorageReaderSkeleton implements StorageReader {
    protected boolean isZipped;
    protected Charset charset = UTF_8;

    protected abstract Stream<String> rawContent();

    protected abstract void close();

    @Override
    @SuppressWarnings("unchecked")
    public <R, A> R read(Collector<? super String, A, R> structure) {
        A container = structure.supplier().get();
        BiConsumer<A, ? super String> accumulator = structure.accumulator();
        try (Stream<String> rawContent = rawContent()) {
            rawContent.forEach(u -> accumulator.accept(container, u));
        }

        return structure.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)
                ? (R) container
                : structure.finisher().apply(container);
    }

    @Override
    public List<String> read() {
        try (Stream<String> rawContent = rawContent()) {
            return rawContent.collect(toList());
        }
    }

    @Override
    public Stream<String> lazyRead() {
        return rawContent();
    }

    public <R> R lazyRead(Function<Stream<String>, R> func) {
        try (Stream<String> stream = rawContent()) {
            return func.apply(stream);
        }
    }

    @Override
    public StorageReader unzip() {
        this.isZipped = true;
        return this;
    }

    @Override
    public StorageReader charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    protected static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    protected static <T> T asUnchecked(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
