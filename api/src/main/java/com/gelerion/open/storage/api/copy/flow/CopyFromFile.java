package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.stream.Stream;

public class CopyFromFile extends FromSpec {
    private final StorageFile file;

    public CopyFromFile(StorageFile file) {
        this.file = file;
    }

    //TODO: optimzie
    @Override
    Stream<StorageFile> files() {
        final Stream<StorageFile> stream = Stream.of(file)
                .map(file -> {
                    if (mapper == null) return file;
                    return mapper.onFile(sourceStorage, file);
                });

        if (predicate != null) return stream.filter(predicate);
        else return stream;
    }
}
