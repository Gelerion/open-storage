package com.gelerion.open.storage.s3.utils;

import static java.util.Objects.requireNonNull;

public class S3KeySplitter {

    public static S3KeyOps split(String key) {
        String[] parts = requireNonNull(key).split("/");
        return new S3KeyOps(key, parts);
    }

    public static class S3KeyOps {
        public static final S3KeyOps EMPTY = new S3KeyOps(null, null);
        private final String key;
        private final String[] parts;

        S3KeyOps(String key, String[] parts) {
            this.key = key;
            this.parts = parts;
        }

        //return path with first element removed or file itself
        // a/b/c/file.txt -> a/b/c/
        // a/b/c/ -> a/b/
        public S3KeyOps butFirst() {
            if (key == null) return EMPTY;
            if (parts.length <= 1) return this;

            String[] result = new String[parts.length - 1];
            System.arraycopy(parts, 0, result, 0, result.length);
            return new S3KeyOps(String.join("/", result), result);
        }

        //return path with first element removed or file itself
        // a/b/c/file.txt -> b/c/file.txt
        public S3KeyOps butLast() {
            if (key == null) return EMPTY;
            if (parts.length <= 1) return this;

            String[] result = new String[parts.length - 1];
            System.arraycopy(parts, 1, result, 0, result.length);
            return new S3KeyOps(String.join("/", result), result);
        }

        //Nullable
        public String key() {
            return key;
        }

        public String getKeyOr(String defaultVal) {
            if (key == null) return defaultVal;
            return key;
        }
    }


}
