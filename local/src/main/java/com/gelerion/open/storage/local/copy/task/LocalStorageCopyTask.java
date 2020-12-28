package com.gelerion.open.storage.local.copy.task;

import com.gelerion.open.storage.api.copy.SameStorageCopyTask;
import com.gelerion.open.storage.api.copy.flow.CopyFlow.Source;
import com.gelerion.open.storage.api.copy.flow.CopyFlow.Target;
import com.gelerion.open.storage.api.copy.resolver.TargetPathResolver;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.StorageOperations.VoidExceptional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.DELETE_SOURCE_FILES;
import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.FLATTEN;
import static com.gelerion.open.storage.api.ops.StorageOperations.run;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.toList;

public class LocalStorageCopyTask extends SameStorageCopyTask {

    public LocalStorageCopyTask(Source source, Target target) {
        super(source, target);
    }

    @Override
    public void execute() {
        boolean deleteSource = hasOption(DELETE_SOURCE_FILES);

        List<StorageFile> filesToCopy = source.files().collect(toList());
        TargetPathResolver targetPathResolver = target.resolver(filesToCopy, hasOption(FLATTEN));

        for (StorageFile sourcePath : filesToCopy) {
            StorageFile targetPath = targetPathResolver.resolve(sourcePath);

            if (deleteSource)
                move(sourcePath, targetPath);
            else
                copy(sourcePath, targetPath);
        }
    }

    private void copy(StorageFile srcPath, StorageFile tgtPath) {
        run(() ->
          createDir(tgtPath).andThen(() ->
            Files.copy(unwrapped(srcPath), unwrapped(tgtPath), REPLACE_EXISTING))
        );
    }

    private void move(StorageFile srcPath, StorageFile tgtPath) {
        run(() ->
          createDir(tgtPath).andThen(() ->
            Files.move(unwrapped(srcPath), unwrapped(tgtPath), REPLACE_EXISTING))
        );
    }


    //TODO; get the deepest sub tree from the source files and create the directories only once
    private VoidExceptional createDir(StorageFile file) throws IOException {
        return () -> {
            Path path = file.unwrap(Path.class);
            boolean isRelative = path.getParent() == null;

            if (!isRelative && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
        };
    }

    //TODO: check local file
    private Path unwrapped(StoragePath<?> file) {
        return file.unwrap(Path.class);
    }

}
