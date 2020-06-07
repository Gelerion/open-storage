package com.gelerion.open.storage.local.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.local.LocalStorage;
import com.gelerion.open.storage.local.domain.LocalStorageDirectory;
import com.gelerion.open.storage.local.domain.LocalStorageFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Comparator.reverseOrder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

public class LocalStorageIntegrationTest /*extends StorageIntegrationTest*/ {
    private final Storage storage = LocalStorage.newLocalStorage();

    private final List<Path> filesToDelete = new ArrayList<>();
    private final List<String> dirsToDelete = new ArrayList<>();

    @Test
    public void createNewFile() {
        String fileName = "test.txt";
        StorageFile file = createFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        Path created = Paths.get(fileName);
        assertTrue(Files.exists(created));
    }

    @Test
    public void createNewFileInsideNonExistingDirectory() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createDir(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        Path created = Paths.get(dir, fileName);
        assertTrue(Files.exists(created));
    }

    @Test
    public void createNewFileInsideNonExistingDirectoryUsingStorageFile() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createFile(Paths.get(dir, fileName));
        storage.writer(file).write(Stream.of("Hello world!"));

        Path created = Paths.get(dir, fileName);
        assertTrue(Files.exists(created));
    }

    @Test
    public void readPreviouslyCreatedFile() {
        StorageFile file = createFile("test.txt");
        List<String> expected = asList("Hello world!", "What a perfect day!");

        storage.writer(file).write(expected.stream());
        List<String> content = storage.reader(file).read();

        assertIterableEquals(expected, content);
    }

    @Test
    public void writingToAlreadyExistingFileShouldOverrideContent() throws IOException {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createFile(Paths.get(dir, fileName));
        storage.writer(file).write(Stream.of("Hello world!"));

        Path created = Paths.get(dir, fileName);
        assertTrue(Files.exists(created));

        List<String> content = Files.readAllLines(created);
        assertEquals(1, content.size());
        assertTrue(content.contains("Hello world!"));

        //file must be rewritten with new content
        storage.writer(file).write(Stream.of("Mad world!"));
        content = Files.readAllLines(created);
        assertEquals(1, content.size());
        assertTrue(content.contains("Mad world!"));
    }

    @Test
    public void appendingToNotExistingFileShouldCreateNewFileAndWriteContent() throws IOException {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createFile(Paths.get(dir, fileName));
        storage.writer(file).append(Stream.of("Hello world!"));

        Path created = Paths.get(dir, fileName);
        assertTrue(Files.exists(created));
        List<String> content = Files.readAllLines(created);
        assertEquals(1, content.size());
        assertTrue(content.contains("Hello world!"));
    }

    @Test
    public void appendingToAlreadyExistingFileShouldNotWipePreviousContent() throws IOException {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createFile(Paths.get(dir, fileName));
        storage.writer(file).write(Stream.of("Hello world!"));

        Path created = Paths.get(dir, fileName);
        assertTrue(Files.exists(created));

        List<String> content = Files.readAllLines(created);
        assertEquals(1, content.size());
        assertTrue(content.contains("Hello world!"));

        storage.writer(file).append(Stream.of("Mad world!"));
        content = Files.readAllLines(created);
        assertEquals(2, content.size());

        ArrayList<String> expected = new ArrayList<>();
        expected.add("Hello world!");
        expected.add("Mad world!");
        assertIterableEquals(expected, content);
    }

    @Test
    public void deleteDirectory() {
        String abcDir = "abc";
        String xyzDir = "xyz";

        StorageFile test = LocalStorageFile.get(abcDir + "/test.txt");
        StorageFile example = LocalStorageFile.get(Paths.get(abcDir, "example.txt"));
        StorageFile foggy = LocalStorageFile.get(xyzDir + "/foggy.txt");

        storage.writer(test).write(Stream.of("Hello test world!"));
        storage.writer(example).write(Stream.of("Hello example world!"));
        storage.writer(foggy).write(Stream.of("Hello foggy world!"));

        //whole storage
        StorageDirectory abcDirPath = test.parentDir();
        storage.delete(abcDirPath);

        assertFalse(Files.exists(Paths.get(abcDirPath.toString())));
    }

    @Test
    public void deletingNotExistingFileShotNotThrowException() {
        String fileName = "abc.txt";
        storage.delete(LocalStorageFile.get(fileName));
        Path created = Paths.get(fileName);
        assertFalse(Files.exists(created));
    }

    @Test
    public void checkFileExist() {
        String fileName = "test.txt";
        StorageFile test = createFile(fileName);

        storage.writer(test).write(Stream.of("Hello world!"));

        Path created = Paths.get(fileName);
        assertTrue(Files.exists(created));
        assertTrue(storage.exists(test));

        String nonExistingFile = "nonExist.txt";
        Path notCreated = Paths.get(nonExistingFile);
        assertFalse(Files.exists(notCreated));
        assertFalse(storage.exists(LocalStorageFile.get(nonExistingFile)));
    }

    @Test
    public void checkDirExist() {
        String fileName = "test.txt";
        String dirName = "abc";
        StorageFile file = createFile(Paths.get(dirName, fileName));
        storage.writer(file).write(Stream.of("Hello world!"));

        assertTrue(storage.exists(file.parentDir()));
    }

    @Test
    public void checkDirSize() throws IOException {
        String fileName = "test.txt";
        String abcDir = "abc";
        StorageFile test = createFile(fileName);
        StorageFile example = createFile(Paths.get(abcDir, "example.txt"));

        storage.writer(example).write(Stream.of("Hello example world!"));
        storage.writer(test).write(Stream.of("Hello world!"));

        Path parentDir = Paths.get(test.parentDir().toString());
        assertEquals(computeSize(parentDir), storage.size(test.parentDir()));

        Path filePath = Paths.get(fileName);
        assertEquals(Files.size(filePath), storage.size(test));
    }

    @Test
    public void listFiles() {
        StorageFile test = createFile("abc/test.txt");
        StorageFile example = createFile("abc/example.txt");
        StorageFile example2 = createFile("abc/xyz/example2.txt");

        storage.writer(test).write(Stream.of("Hello world!"));
        storage.writer(example).write(Stream.of("Hello world!"));
        storage.writer(example2).write(Stream.of("Hello world!"));

        Set<StorageFile> files = storage.files(LocalStorageDirectory.get(Paths.get("abc")));
        assertEquals(files.size(), 2);
        Set<String> fileNames = files.stream().map(StorageFile::fileName).collect(toSet());
        assertTrue(fileNames.contains("test.txt"));
    }

    @Test
    public void listDirs() {
        String abcDir = "abc";
        String xyzFolder = "xyz";
        StorageFile test = createFile(abcDir + "/test.txt");
        StorageFile example = createFile(abcDir + "/example.txt");
        StorageFile example2 = createFile(abcDir + "/" + xyzFolder + "/example2.txt");

        storage.writer(test).write(Stream.of("Hello test world!"));
        storage.writer(example).write(Stream.of("Hello example world!"));
        storage.writer(example2).write(Stream.of("Hello example2 world!"));

        String qweDir = "qwe";
        String ertDir = "ert";
        String ghFolder = "gh";
        storage.create(LocalStorageDirectory.get(Paths.get(abcDir + "/" + qweDir)));
        storage.create(LocalStorageDirectory.get(Paths.get(abcDir + "/" + qweDir + "/" + ertDir)));
        LocalStorageDirectory folder = LocalStorageDirectory.get(abcDir, ghFolder);
        LocalStorageDirectory ert = folder.addSubDirectory(ertDir);
        storage.create(ert);

        //storage layout:
        //  -abc
        //      -xyz
        //      -qwe
        //          -ert
        //      -gh
        //          -ert
        Set<String> expectedLayout = new HashSet<>();
        expectedLayout.add("abc/xyz");
        expectedLayout.add("abc/qwe");
        expectedLayout.add("abc/qwe/ert");
        expectedLayout.add("abc/gh");
        expectedLayout.add("abc/gh/ert");

        Set<String> actual = storage.dirs(LocalStorageDirectory.get(abcDir)).stream().map(Object::toString).collect(toSet());
        assertIterableEquals(expectedLayout, actual);
    }

    @Test
    public void renameDir() {
        String srcDirName = "source";
        String dstDirName = "target";
        StorageDirectory current = createDir(srcDirName);
        StorageDirectory renamed = createDir(dstDirName);
        storage.create(current);

        storage.move(current, renamed);
        assertFalse(Files.exists(Paths.get(srcDirName)));
        assertTrue(Files.exists(Paths.get(dstDirName)));
    }

    @Test
    public void renameAndResolveDir() {
        String srcDirName = "abc/source";
        String dstDirName = "target";
        StorageDirectory current = createDir(srcDirName);
        StorageDirectory renamed = createDir(dstDirName);
        storage.create(current);

        storage.move(current, renamed);
        assertFalse(Files.exists(Paths.get(srcDirName)));
        assertTrue(Files.exists(Paths.get("abc", dstDirName)));
    }

    @Test
    public void renameAndResolveSubDir() {
        String srcDirName = "abc/source";
        String dstDirName = "abc/target";
        StorageDirectory current = createDir(srcDirName);
        StorageDirectory renamed = createDir(dstDirName);
        storage.create(current);

        storage.move(current, renamed);
        assertFalse(Files.exists(Paths.get(srcDirName)));
        assertTrue(Files.exists(Paths.get(dstDirName)));
    }

    //TODO: more understandable exception than NoSuchFileException: source -> /Users/denisshuvalov/Projects/Amazon/open-storage/local/abc/target
/*    @Test
    public void renameAndResolveNestedDstDirs() {
        String srcDirName = "source";
        String dstDirName = "abc/target";
        StorageDirectory current = createDir(srcDirName);
        StorageDirectory renamed = createDir(dstDirName);
        storage.create(current);

        storage.rename(current, renamed);
        assertFalse(Files.exists(Paths.get(srcDirName)));
        assertTrue(Files.exists(Paths.get(dstDirName)));
    }*/

 /*   @Test
    public void moveDirWithFiles() {
        String srcDirName = "abc/source";
        String dstDirName = "target";
        StorageDirectory current = createDir(srcDirName);
        StorageDirectory target = createDir(dstDirName);
    }*/

    @Test
    public void renameFile() {
        String dir = "dir";
        String srcFileName = "source.txt";
        String dstFileName = "target.txt";
        StorageFile current = LocalStorageFile.get(Paths.get(dir, srcFileName)); //dir/source.txt
        StorageFile renamed = LocalStorageFile.get(dstFileName);                 //target.txt
        createFile(Paths.get(dir, dstFileName)); //schedule deletion after the test
        storage.writer(current).write("1");

        storage.rename(current, renamed); //dir/target.txt
        assertFalse(Files.exists(Paths.get(dir, srcFileName)));
        assertTrue(Files.exists(Paths.get(dir, dstFileName)));
    }

    @Test
    public void properlyRenameFileWithingSameDir() {
        String dir = "dir";
        String srcFileName = "source.txt";
        String dstFileName = "target.txt";
        StorageFile current = LocalStorageFile.get(Paths.get(dir, srcFileName)); //dir/source.txt
        StorageFile renamed = LocalStorageFile.get(Paths.get(dir, dstFileName)); //dir/target.txt
        createFile(Paths.get(dir, dstFileName)); //schedule deletion after the test
        storage.writer(current).write("1");

        storage.move(current, renamed); //dir/target.txt
        assertFalse(Files.exists(Paths.get(dir, srcFileName)));
        assertTrue(Files.exists(Paths.get(dir, dstFileName)));
    }

    //TODO: handle exception NoSuchFileException: source.txt -> /Users/denisshuvalov/Projects/Amazon/open-storage/local/dir/target.txt
/*    @Test
    public void renameAndCopyFile() {
        String dir = "dir";
        String srcFileName = "source.txt";
        String dstFileName = "target.txt";
        StorageFile current = LocalStorageFile.get(Paths.get(srcFileName));       //source.txt
        StorageFile renamed = LocalStorageFile.get(Paths.get(dir, dstFileName));  //dir/target.txt
        createFile(Paths.get(dir, dstFileName)); //schedule deletion after the test
        storage.writer(current).write("1");

        storage.rename(current, renamed); //dir/target.txt
        assertFalse(Files.exists(Paths.get(srcFileName)));
        assertTrue(Files.exists(Paths.get(dir, dstFileName)));
    }*/

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
        final String[] dirs = path.split("/");
        dirsToDelete.add(dirs[0]); //adding root dir is enough
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

    private long computeSize(Path storage) throws IOException {
        long result = 0;
        Predicate<Path> isDirectory = Files::isDirectory;
        //flatMap() is the only operation that internally closes the stream after its done
        List<Path> files = Stream.of(Files.walk(storage)).flatMap(identity()).filter(isDirectory.negate()).collect(toList());
        for (Path file : files) {
            result += Files.size(file);
        }
        return result;
    }
}
