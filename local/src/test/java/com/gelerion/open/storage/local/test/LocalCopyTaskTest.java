package com.gelerion.open.storage.local.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.local.LocalStorage;
import com.gelerion.open.storage.local.model.LocalStorageDirectory;
import com.gelerion.open.storage.local.model.LocalStorageFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.copy.flow.SourceSpec.dir;
import static com.gelerion.open.storage.api.copy.flow.TargetSpec.path;
import static com.gelerion.open.storage.api.copy.functions.CopyTaskFunctions.renameTo;
import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.DELETE_SOURCE_FILES;
import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.FLATTEN;
import static com.gelerion.open.storage.api.copy.functions.CopyTaskPredicates.*;
import static java.util.Comparator.reverseOrder;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalCopyTaskTest {
    private final Storage storage = LocalStorage.newLocalStorage();

    private final List<Path> filesToDelete = new ArrayList<>();
    private final List<String> dirsToDelete = new ArrayList<>();

//    @Test
//    void DO_NOT_COMMIT_ME() {
//        String copyTest = "/Users/dshuvalov/Intuit/tmp/copy-test/";
//        LocalStorageDirectory dqRestApi = LocalStorageDirectory.get(copyTest + "data-quality-rest-api-source");
//
//        storage.copy()
//                .source(dir(dqRestApi)
//                        .filter(pathContains("target").negate()
//                                .and(pathContains(".git").negate())
//                                .and(pathContains(".idea").negate())
//                                .and(fileNameEndsWith(".jar").negate())
//                                .and(fileNameStartsWith(".DS").negate())))
//                .target(path(LocalStorageDirectory.get(copyTest, "dq-rest-api-target"))
//                        .peek(file -> System.out.println("about to copy " + file)))
//                .execute();
//    }

    @Test
    void copyFile() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createDir(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = createDir("abc/efg");

        //source: abc/text.txt
        //target: abc/efg
        //result: abc/efg/test.txt
        storage.copy().source(file).target(tgtDir).execute();

        assertTrue(Files.exists(tgtDir.toStorageFile(fileName).unwrap(Path.class)));
    }

    @Test
    void copyFile1() {
        //1. file -> dir --- always flatten, only filename
        //2. dir  -> dir  --- preserve hierarchy based on a common prefix

        //source: abc/xyz/text.txt
        //target: abc/efg
        //result: ---- ???
        //        abc/efg/text.txt

        //source: qwe/xyz/text.txt
        //target: abc/efg
        //result: ---- ???
        //        abc/efg/text.txt

        // copy 'qwe' dir to 'abc/efg'
        //source: qwe/xyz/text.txt
        //        qwe/sde/xyz/text.txt
        //target: abc/efg
        //result: ---- ???
        //        abc/efg/xyz/text.txt
        //        abc/efg/sde/xyz/text.txt


        //source: qwe/xyz/text.txt
        //        hjk/sde/xyz/text.txt
        //target: abc/efg
        //result: ---- ???
    }


    @Test
    void copyAndRename() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createDir(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = createDir("abc/efg");

        String newName = "test2.txt";
        storage.copy()
                .source(file)
                .target(path(tgtDir).map(renameTo(newName)))
                .execute();

        //source: abc/text.txt
        //target: abc/efg + rename
        //result: abc/efg/test2.txt
        assertTrue(Files.exists(tgtDir.toStorageFile(newName).unwrap(Path.class)));
    }

    @Test
    void copyWithinSameDir() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createDir(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = createDir("abc");

        //source: abc/text.txt
        //target: abc/
        //result: abc/test.txt
        storage.copy().source(file).target(tgtDir).execute();

        assertTrue(Files.exists(tgtDir.toStorageFile(fileName).unwrap(Path.class)));
    }

    @Test
    void copyAndRenameWithinSameDir() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createDir(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = createDir("abc");

        //source: abc/text.txt
        //target: abc/
        //result: abc/test2.txt
        String newName = "test2.txt";
        storage.copy()
                .source(file)
                .target(path(tgtDir).map(it -> it.rename(newName)))
                .execute();

        assertTrue(Files.exists(tgtDir.toStorageFile(newName).unwrap(Path.class)));
    }

    @Test
    void copyFileAndDeleteSource() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createDir(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = createDir("cbs");

        //source: abc/text.txt
        //target: cbs/

        //expected result:
        // source: abc/
        // target: cbs/test.txt
        storage.copy().source(file).target(tgtDir).options(DELETE_SOURCE_FILES).execute();

        assertFalse(Files.exists(file.unwrap(Path.class)));
        assertTrue(Files.exists(tgtDir.toStorageFile(fileName).unwrap(Path.class)));
    }

    //TODO: copy + delete source to the same dir

    @Test
    void copyRecursively() {
        String dir = "abc";
        String subdirA = "a";
        String subdirB = "b";
        String fileName = "test.txt";
        String fileNameA = "Atest.txt";
        String fileNameB = "Btest.txt";
        StorageDirectory abcDir = createDir(dir);
        StorageFile file = abcDir.toStorageFile(fileName);
        StorageFile fileA = abcDir.addSubDirectory(subdirA).toStorageFile(fileNameA);
        StorageFile fileB = abcDir.addSubDirectory(subdirB).toStorageFile(fileNameB);
        storage.writer(file).write("source");
        storage.writer(fileA).write("fileA");
        storage.writer(fileB).write("fileB");

        /*
        abc/test.txt
           /a/Atest.txt
           /b/Btest.txt
         */

        StorageDirectory tgtDir = createDir("cba");
        storage.copy().source(abcDir).target(tgtDir)
                .execute();
        //TODO: copy option DELETE SOURCE

       /* Expected result
        cba/test.txt
           /a/Atest.txt
           /b/Btest.txt
         */

        //TODO: copy option FLATTEN
        /*
        cba/test.txt
            Atest.txt
            Btest.txt
         */

        assertTrue(Files.exists(tgtDir.toStorageFile(fileName).unwrap(Path.class)));
        assertTrue(Files.exists(tgtDir.addSubDirectory(subdirA).toStorageFile(fileNameA).unwrap(Path.class)));
        assertTrue(Files.exists(tgtDir.addSubDirectory(subdirB).toStorageFile(fileNameB).unwrap(Path.class)));
    }

    @Test
    void copyRecursivelyAndDeleteSource() {
        String dir = "abc";
        String subdirA = "a";
        String subdirB = "b";
        String fileName = "test.txt";
        String fileNameA = "Atest.txt";
        String fileNameB = "Btest.txt";
        StorageDirectory abcDir = createDir(dir);
        StorageFile file = abcDir.toStorageFile(fileName);
        StorageFile fileA = abcDir.addSubDirectory(subdirA).toStorageFile(fileNameA);
        StorageFile fileB = abcDir.addSubDirectory(subdirB).toStorageFile(fileNameB);
        storage.writer(file).write("source");
        storage.writer(fileA).write("fileA");
        storage.writer(fileB).write("fileB");

        /*
        abc/test.txt
           /a/Atest.txt
           /b/Btest.txt
         */

        StorageDirectory tgtDir = createDir("cba");
        storage.copy().source(abcDir).target(tgtDir).options(DELETE_SOURCE_FILES).execute();

        /* Expected result
        abc/
           /a/
           /b/

        cba/test.txt
            /a/Atest.txt
            /b/Btest.txt
         */

        assertTrue(Files.exists(tgtDir.toStorageFile(fileName).unwrap(Path.class)));
        assertTrue(Files.exists(tgtDir.addSubDirectory(subdirA).toStorageFile(fileNameA).unwrap(Path.class)));
        assertTrue(Files.exists(tgtDir.addSubDirectory(subdirB).toStorageFile(fileNameB).unwrap(Path.class)));

        //check deleted
        assertFalse(Files.exists(file.unwrap(Path.class)));
        assertFalse(Files.exists(fileA.unwrap(Path.class)));
        assertFalse(Files.exists(fileB.unwrap(Path.class)));
    }

    @Test
    void copyRecursivelyAndFlatten() {
        String dir = "abc";
        String subdirA = "a";
        String subdirB = "b";
        String fileName = "test.txt";
        String fileNameA = "Atest.txt";
        String fileNameB = "Btest.txt";
        StorageDirectory abcDir = createDir(dir);
        StorageFile file = abcDir.toStorageFile(fileName);
        StorageFile fileA = abcDir.addSubDirectory(subdirA).toStorageFile(fileNameA);
        StorageFile fileB = abcDir.addSubDirectory(subdirB).toStorageFile(fileNameB);
        storage.writer(file).write("source");
        storage.writer(fileA).write("fileA");
        storage.writer(fileB).write("fileB");

        /*
        abc/test.txt
           /a/Atest.txt
           /b/Btest.txt
         */

        StorageDirectory tgtDir = createDir("cba");
        storage.copy().source(abcDir).target(tgtDir).options(FLATTEN).execute();

        /* Expected result
        cba/test.txt
            Atest.txt
            Btest.txt
         */

        assertTrue(Files.exists(tgtDir.toStorageFile(fileName).unwrap(Path.class)));
        assertTrue(Files.exists(tgtDir.toStorageFile(fileNameA).unwrap(Path.class)));
        assertTrue(Files.exists(tgtDir.toStorageFile(fileNameB).unwrap(Path.class)));
    }

    private StorageFile createFile(String path) {
        return createFile(Paths.get(path));
    }

    private StorageFile createFile(Path path) {
        int elements = path.getNameCount();
        if (elements > 1) {
            addDirsToDelete(path.getParent());
        }
        filesToDelete.add(path);
        return LocalStorageFile.get(path);
    }

    private StorageDirectory createDir(String path) {
        dirsToDelete.add(path);
        return LocalStorageDirectory.get(path);
    }

    private void addDirsToDelete(Path path) {
        if (path == null) return;
        dirsToDelete.add(path.getFileName().toString());
        addDirsToDelete(path.getParent());
    }

    @AfterEach
    void afterEach() throws IOException {
        for (Path file : filesToDelete) {
            Files.delete(file);
        }

        for (String dir : dirsToDelete) {
            deleteAllFiles(dir);
        }

        filesToDelete.clear();
        dirsToDelete.clear();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteAllFiles(String dir) throws IOException {
        Path storage = Paths.get(dir);
        if (Files.exists(storage)) {
            Stream.of(Files.walk(storage))
                    .flatMap(identity()) //free up the resources
                    .sorted(reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }


    /*
    Tests Matrix
     */
}
