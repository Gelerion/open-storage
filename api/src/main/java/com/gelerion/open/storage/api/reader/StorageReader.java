package com.gelerion.open.storage.api.reader;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

/**
 * Created by denis.shuvalov on 03/01/2018.
 *
 * Usage example:
 *   StorageReader reader = LocalStorageReader.ofStrings("log4j.properties");
 *   StorageReader reader = S3StorageReader.ofStrings("s3//abc/");
 *
 *   if zipped:
 *   StorageReader reader = HdfsStorageReader.ofStrings("s3//abc/").unzip();
 *
 *   and then:
 *   List<String> strings = reader.read(Collectors.toList());
 *   Set<String>  strings = reader.read(Collectors.toSet());
 *   Map<K, V>    strings = reader.read(Collectors.toMap(keyMapper, valueMapper)
 */
public interface StorageReader {
    StorageReader unzip();

    StorageReader charset(Charset charset);

    <R, A> R read(Collector<? super String, A, R> structure);

    List<String> read();

    //implements auto closeable
    <R> R lazyRead(Function<Stream<String>, R> func);

    /**
     * Important
     * Always wrap this stream with try with resources to avoid resource leak
     * e.g: try(Stream<String> stream = lazyRead()) {...}
     */
    Stream<String> lazyRead();

    InputStream stream();

    default String readFully() {
        return read(joining(lineSeparator()));
    }
}
