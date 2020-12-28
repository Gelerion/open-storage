package com.gelerion.open.storage.api.copy.resolver;

import com.gelerion.open.storage.api.copy.flow.CopyFlow.Target;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.List;

public class HierarchyPreservingTargetPathResolver implements TargetPathResolver {
    private final Target target;
    private final String sourceFilesCommonPrefix;

    public HierarchyPreservingTargetPathResolver(Target target, List<StorageFile> sourceFiles) {
        this.target = target;
        this.sourceFilesCommonPrefix = computeLongestCommonPrefix(sourceFiles);
    }

    @Override
    public StorageFile resolve(StorageFile sourcePath) {
        String keyToCopy = sourcePath.toString().substring(sourceFilesCommonPrefix.length());
        StorageFile targetPath = target.dir().toStorageFile(keyToCopy);
        return target.applyTransformations(targetPath);
    }

    private String computeLongestCommonPrefix(List<StorageFile> files) {
        String[] paths = files.stream().map(Object::toString).toArray(String[]::new);
        return longestCommonPrefix(paths);
    }

    private String longestCommonPrefix(String[] strs) {
        if(strs == null || strs.length == 0) return "";
        String pre = strs[0];
        int i = 1;
        while(i < strs.length){
            while(strs[i].indexOf(pre) != 0)
                pre = pre.substring(0,pre.length()-1);
            i++;
        }
        return pre;
    }
}
