package com.gelerion.open.storage.api.copy.resolver;

import com.gelerion.open.storage.api.copy.flow.CopyFlow.Target;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.List;

public interface TargetPathResolver {

    static TargetPathResolver get(Target target, List<StorageFile> sourceFiles, boolean flatten) {
        if (flatten || sourceFiles.size() == 1) {
            return new FlattenTargetPathResolver(target);
        }

        return new HierarchyPreservingTargetPathResolver(target, sourceFiles);
    }

    StorageFile resolve(StorageFile sourcePath);

}
