package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.StorageOperations;

import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.gelerion.open.storage.api.ops.StorageOperations.run;

public class CopyTaskImpl implements CopyTask {
    private final CopyFlow.Source source;
    private final CopyFlow.Target sink;

    public CopyTaskImpl(CopyFlow.Source source, CopyFlow.Target sink) {
        this.source = source;
        this.sink = sink;
    }

    @Override
    public void execute() {
/*        Storage srcStorage = source.storage();
        Storage tgtStorage = target.storage();

        List<StorageFile> srcFiles = source.files();
        StorageDirectory  tgtDir   = target.dir();

        for (StorageFile srcFile : srcFiles) {
            //this function doesn't honor nesting - flatten the path
            tgtStorage.writer(tgtDir.toStorageFile(srcFile.fileName())).write(srcStorage.reader(srcFile).lazyRead());
            //srcStorage.reader(srcFile)
//            tgtStorage.writer()
        }*/

        Storage srcStorage = source.storage();
        Storage tgtStorage = sink.storage();

        //TODO: different CopyTask implementations
        //TODO: remove existing
        //e.g. Same src and target storage -> should be done with native fs api
        //copy between storages require usage readers/writers api

        if (srcStorage.name().equals(tgtStorage.name())) {
            //push down to the native copy api
            source.files().forEach(srcFile -> {
//                StorageFile tgtFile = sink.resolve(srcFile);
                run(() -> {
                    StorageDirectory dir = sink.dir();
                    StorageFile tgtFile = dir.toStorageFile(srcFile.fileName());

                    Files.createDirectories(tgtFile.parentDir().unwrap(Path.class));
                    srcStorage.move(srcFile, tgtFile);
                });

//                run(() -> {
//                    final Path srcPath = srcFile.unwrap(Path.class);
//                    final Path newDir = dir.unwrap(Path.class);
//                    Files.createDirectories(newDir);
//                    Files.copy(srcPath, newDir.resolve(srcPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
//                });
            });
        }

/*        source.files().forEach(srcFile -> {
            //this function doesn't honor nesting - flatten the path
            source.storage().reader(srcFile).stream();

            tgtStorage.writer(sink.r)


            sink.resolve(srcFile)
//            tgtStorage.writer(tgtDir.toStorageFile(srcFile.fileName())).write(srcStorage.reader(srcFile).lazyRead());
        });*/

    }
}
