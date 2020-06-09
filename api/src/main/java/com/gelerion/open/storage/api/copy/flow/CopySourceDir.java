package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.stream.Stream;

import static com.gelerion.open.storage.api.ops.ListFilesOption.RECURSIVELY;

public class CopySourceDir extends SourceSpec {
    private final StorageDirectory dir;

    public CopySourceDir(StorageDirectory dir) {
        this.dir = dir;
    }

    //TODO: optimize:
    @Override
    Stream<StorageFile> files() {
        final Stream<StorageFile> stream = sourceStorage.files(dir, RECURSIVELY).stream()
                .map(file -> {
                    if (mapper == null) return file;
                    return mapper.onFile(sourceStorage, file);
                });

        if (predicate != null) return stream.filter(predicate);
        else return stream;
    }
}
