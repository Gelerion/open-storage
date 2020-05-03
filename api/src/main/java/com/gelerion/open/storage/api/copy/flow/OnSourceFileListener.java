package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.Objects;

@FunctionalInterface
public interface OnSourceFileListener {
    StorageFile onFile(Storage storage, StorageFile file);

    default OnSourceFileListener andThen(OnSourceFileListener after) {
        Objects.requireNonNull(after);
        return (storage, file) -> after.onFile(storage, onFile(storage, file));
    }
}
