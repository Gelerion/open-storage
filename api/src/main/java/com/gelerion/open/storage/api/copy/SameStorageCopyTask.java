package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.DELETE_SOURCE_FILES;
import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.FLATTEN;
import static java.util.stream.Collectors.toList;

public abstract class SameStorageCopyTask extends CopyTaskSkeleton {

//    private final Consumer<StorageFile> copyFile = sourceFile -> {
//        StorageFile tgtFile = hasOption(FLATTEN) ?
//                target.resolveTargetPathFlatten(sourceFile) :
//                target.resolveTargetPath(sourceFile);
//
//        //TODO: [optimization] create dir only once and not per file
//        target.storage()/*.create(tgtFile.parentDir())*/.move(sourceFile, tgtFile);
//    };

    private final Consumer<StorageFile> deleteFile = file -> source.storage().delete(file);

    public SameStorageCopyTask(CopyFlow.Source source, CopyFlow.Target sink) {
        super(source, sink);
    }

    @Override
    public void execute() {
        //TODO: [optimization] source.files() might have complicated logic added by the client, we better cache subsequent calls
        List<StorageFile> sourceFiles = source.files().collect(toList());

        if (hasOption(FLATTEN)) {
            //TODO;
            sourceFiles.forEach(file -> copyFile(file, target.resolveTargetPathFlatten(file)));
            return;
        }


//        target.resolveTargetPath(sourceFiles).forEach(file -> {
//            copyFile(file, );
//        });

//        if (hasOption(DELETE_SOURCE_FILES)) {
//            //TODO: [optimization] for s3 storage it's better to delete files in batches
//            source.files().forEach(deleteFile);
//        }
    }

    protected abstract void copyFile(StorageFile source, StorageFile target);


}
