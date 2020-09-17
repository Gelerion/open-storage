package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.function.Consumer;

import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.DELETE_SOURCE_FILES;
import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.FLATTEN;

public class SameStorageCopyTask extends CopyTaskSkeleton {

    private final Consumer<StorageFile> copyFile = sourceFile -> {
        StorageFile tgtFile = hasOption(FLATTEN) ?
                target.resolveTargetPathFlatten(sourceFile) :
                target.resolveTargetPath(sourceFile);

        //TODO: [optimization] create dir only once and not per file
        target.storage().create(tgtFile.parentDir()).move(sourceFile, tgtFile);
    };

    private final Consumer<StorageFile> deleteFile = file -> source.storage().delete(file);

    public SameStorageCopyTask(CopyFlow.Source source, CopyFlow.Target sink) {
        super(source, sink);
    }

    @Override
    public void execute() {
        //TODO: [optimization] source.files() might have complicated logic added by the client, we better cache subsequent calls
        source.files().forEach(copyFile);

        if (hasOption(DELETE_SOURCE_FILES)) {
            //TODO: [optimization] for s3 storage it's better to delete files in batches
            source.files().forEach(deleteFile);
        }
    }
}
