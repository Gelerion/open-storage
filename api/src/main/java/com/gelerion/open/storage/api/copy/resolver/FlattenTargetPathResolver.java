package com.gelerion.open.storage.api.copy.resolver;

import com.gelerion.open.storage.api.copy.flow.CopyFlow.Target;
import com.gelerion.open.storage.api.domain.StorageFile;

public class FlattenTargetPathResolver implements TargetPathResolver {
    private final Target target;

    public FlattenTargetPathResolver(Target target) {
        this.target = target;

    }

    public StorageFile resolve(StorageFile sourcePath) {
        StorageFile targetPath = target.dir().toStorageFile(sourcePath.fileName());
        return target.applyTransformations(targetPath);
    }
}
