package com.gelerion.open.storage.local;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.local.domain.LocalStorageDirectory;
import com.gelerion.open.storage.local.domain.LocalStorageFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("test.txt");
        System.out.println("path = " + path);
        System.out.println(path.hashCode());

        Path written = Files.write(path, Stream.of("a", "b").collect(Collectors.toList()));
        System.out.println("written = " + written);
        System.out.println(written.hashCode());

        System.out.println("Files.exists(path) = " + Files.exists(path));


        System.out.println(path.getFileSystem());
        System.out.println("path.toAbsolutePath() = " + path.toAbsolutePath());

//        StorageFile file = LocalStorageFile.get("abc");
//        LocalStorageDirectory directory = new LocalStorageDirectory();
//
//        Storage storage = LocalStorage.newLocalStorage();
//
//        storage.delete(file);
    }

}
