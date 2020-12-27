package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.stream.Stream;

public class CopySourceFile extends SourceSpec {
    private final StorageFile file;

    public CopySourceFile(StorageFile file) {
        this.file = file;
    }

    //TODO: optimzie
    @Override
    Stream<StorageFile> files() {
        final Stream<StorageFile> stream = Stream.of(file)
                .map(file -> {
                    if (mapper == null) return file;
                    return mapper.onFile(sorage, file);
                });

        if (predicate != null) return stream.filter(predicate);
        else return stream;
    }
}
