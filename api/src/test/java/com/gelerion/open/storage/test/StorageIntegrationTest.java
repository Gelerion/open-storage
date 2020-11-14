package com.gelerion.open.storage.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class StorageIntegrationTest {
    private static final int LINE_SEP_SIZE = System.getProperty("line.separator").getBytes(UTF_8).length;

    protected Storage storage;

    private final List<StorageFile> filesToDelete = new ArrayList<>();
    private final List<StorageDirectory> dirsToDelete = new ArrayList<>();

    @BeforeAll
    protected void init() {
        this.storage = storageImpl();
    }

    @Test
    public void createEmptyFile() throws IOException {
        String fileName = "empty-test.txt";
        StorageFile file = createStorageFile(fileName);

        //when
        storage.create(file);

        //then
        assertFileExist(file);
        assertFileSizeEqualsTo(file, 0);
    }

    //todo list files

    @Test
    public void createEmptyDir() throws IOException {
        String dirName = "test";
        StorageDirectory dir = createStorageDir(dirName);

        //when
        storage.create(dir);
        markForCleanup(dir);

        //then
        assertDirExist(dir);
    }

    @Test
    public void createFile() throws IOException {
        String fileName = "test.txt";
        StorageFile file = createStorageFile(fileName);
        Set<String> content = Stream.of("Hello world!", "What a perfect day!").collect(toSet());
        storage.writer(file).write(content);

        assertFileExist(file);
        assertFileSizeEqualsTo(file, calcContentSize(content));
    }

    @Test
    public void createNewFileInsideNonExistingDirectory() throws IOException {
        String dir = "abc";
        String fileName = "test.txt";
        StorageDirectory storageDir = createStorageDir(dir);
        StorageFile file = storageDir.toStorageFile(fileName);

        Set<String> content = Stream.of("Hello world!", "What a perfect day!").collect(toSet());
        storage.writer(file).write(content);

        assertFileExist(file);
        //should create directory
        assertDirExist(storageDir);
        assertFileSizeEqualsTo(file, calcContentSize(content));
    }

    @Test
    public void readFileContent() {
        StorageFile file = createStorageFile("test.txt");
        List<String> expected = asList("Hello world!", "What a perfect day!");

        //write file
        storage.writer(file).write(expected.stream());

        //read content
        List<String> content = storage.reader(file).read();
        assertIterableEquals(expected, content);
    }


    @Test
    public void writingToAlreadyExistingFileShouldOverrideContent() throws IOException {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createStorageFile(dir + "/" + fileName);
        storage.writer(file).write(Stream.of("Hello world!"));


        //file must be rewritten with new content
        storage.writer(file).write(Stream.of("Mad world!"));
        List<String> content = storage.reader(file).read();
        List<String> expected = Collections.singletonList("Mad world!");
        assertIterableEquals(expected, content);
    }

    @Test
    public void deleteFile() throws IOException {
        String fileName = "abc.txt";
        StorageFile file = createStorageFile(fileName);
        storage.create(file);

        //when
        storage.delete(file);

        //then
        assertNotExist(file);
    }


    @Test
    public void deletingNotExistingFileShotNotThrowException() throws IOException {
        String fileName = "abc.txt";
        StorageFile file = createStorageFile(fileName);
        storage.create(file);

        //when
        storage.delete(file);
        storage.delete(file);

        //then
        assertNotExist(file);
    }

    @Test
    public void deleteDirectory() throws IOException {
        String abcDir = "abc";
        String xyzDir = "xyz";
        String file1InAbcDir = abcDir + "/test.txt";
        String file2InAbcDir = abcDir + "/example.txt";
        String fileInXyzDir = xyzDir + "/foggy.txt";

        StorageFile test = createStorageFile(file1InAbcDir);
        StorageFile example = createStorageFile(file2InAbcDir);
        StorageFile foggy = createStorageFile(fileInXyzDir);

        storage.writer(test).write(Stream.of("Hello test world!"));
        storage.writer(example).write(Stream.of("Hello example world!"));
        storage.writer(foggy).write(Stream.of("Hello foggy world!"));

        StorageDirectory abcDirPath = test.parentDir();
        storage.delete(abcDirPath);
        assertNotExist(abcDirPath);

        //assert Xyz exists
        //assertFalse(Files.exists(Paths.get(abcDirPath.toString())));
    }


//
//    @Test
//    public void appendingToNotExistingFileShouldCreateNewFileAndWriteContent() throws IOException {
//        String dir = "abc";
//        String fileName = "test.txt";
//        StorageFile file = createFile(Paths.get(dir, fileName));
//        storage.writer(file).append(Stream.of("Hello world!"));
//
//        Path created = Paths.get(dir, fileName);
//        assertTrue(Files.exists(created));
//        List<String> content = Files.readAllLines(created);
//        assertEquals(1, content.size());
//        assertTrue(content.contains("Hello world!"));
//    }
//
//    @Test
//    public void appendingToAlreadyExistingFileShouldNotWipePreviousContent() throws IOException {
//        String dir = "abc";
//        String fileName = "test.txt";
//        StorageFile file = createFile(Paths.get(dir, fileName));
//        storage.writer(file).write(Stream.of("Hello world!"));
//
//        Path created = Paths.get(dir, fileName);
//        assertTrue(Files.exists(created));
//
//        List<String> content = Files.readAllLines(created);
//        assertEquals(1, content.size());
//        assertTrue(content.contains("Hello world!"));
//
//        storage.writer(file).append(Stream.of("Mad world!"));
//        content = Files.readAllLines(created);
//        assertEquals(2, content.size());
//
//        ArrayList<String> expected = new ArrayList<>();
//        expected.add("Hello world!");
//        expected.add("Mad world!");
//        assertIterableEquals(expected, content);
//    }
//

//
//    @Test
//    public void checkFileExist() {
//        String fileName = "test.txt";
//        StorageFile test = createFile(fileName);
//
//        storage.writer(test).write(Stream.of("Hello world!"));
//
//        Path created = Paths.get(fileName);
//        assertTrue(Files.exists(created));
//        assertTrue(storage.exists(test));
//
//        String nonExistingFile = "nonExist.txt";
//        Path notCreated = Paths.get(nonExistingFile);
//        assertFalse(Files.exists(notCreated));
//        assertFalse(storage.exists(LocalStorageFile.get(nonExistingFile)));
//    }
//
//    @Test
//    public void checkDirExist() {
//        String fileName = "test.txt";
//        String dirName = "abc";
//        StorageFile file = createFile(Paths.get(dirName, fileName));
//        storage.writer(file).write(Stream.of("Hello world!"));
//
//        assertTrue(storage.exists(file.parentDir()));
//    }
//
//    @Test
//    public void checkDirSize() throws IOException {
//        String fileName = "test.txt";
//        String abcDir = "abc";
//        StorageFile test = createFile(fileName);
//        StorageFile example = createFile(Paths.get(abcDir, "example.txt"));
//
//        storage.writer(example).write(Stream.of("Hello example world!"));
//        storage.writer(test).write(Stream.of("Hello world!"));
//
//        Path parentDir = Paths.get(test.parentDir().toString());
//        assertEquals(computeSize(parentDir), storage.size(test.parentDir()));
//
//        Path filePath = Paths.get(fileName);
//        assertEquals(Files.size(filePath), storage.size(test));
//    }
//
//    @Test
//    public void listFiles() {
//        StorageFile test = createFile("abc/test.txt");
//        StorageFile example = createFile("abc/example.txt");
//        StorageFile example2 = createFile("abc/xyz/example2.txt");
//
//        storage.writer(test).write(Stream.of("Hello world!"));
//        storage.writer(example).write(Stream.of("Hello world!"));
//        storage.writer(example2).write(Stream.of("Hello world!"));
//
//        Set<StorageFile> files = storage.files(LocalStorageDirectory.get(Paths.get("abc")));
//        assertEquals(files.size(), 2);
//        Set<String> fileNames = files.stream().map(StorageFile::fileName).collect(toSet());
//        assertTrue(fileNames.contains("test.txt"));
//    }
//
//    @Test
//    public void listDirs() {
//        String abcDir = "abc";
//        String xyzFolder = "xyz";
//        StorageFile test = createFile(abcDir + "/test.txt");
//        StorageFile example = createFile(abcDir + "/example.txt");
//        StorageFile example2 = createFile(abcDir + "/" + xyzFolder + "/example2.txt");
//
//        storage.writer(test).write(Stream.of("Hello test world!"));
//        storage.writer(example).write(Stream.of("Hello example world!"));
//        storage.writer(example2).write(Stream.of("Hello example2 world!"));
//
//        String qweDir = "qwe";
//        String ertDir = "ert";
//        String ghFolder = "gh";
//        storage.create(LocalStorageDirectory.get(Paths.get(abcDir + "/" + qweDir)));
//        storage.create(LocalStorageDirectory.get(Paths.get(abcDir + "/" + qweDir + "/" + ertDir)));
//        LocalStorageDirectory folder = LocalStorageDirectory.get(abcDir, ghFolder);
//        LocalStorageDirectory ert = folder.addSubDirectory(ertDir);
//        storage.create(ert);
//
//        //storage layout:
//        //  -abc
//        //      -xyz
//        //      -qwe
//        //          -ert
//        //      -gh
//        //          -ert
//        Set<String> expectedLayout = new HashSet<>();
//        expectedLayout.add("abc/xyz");
//        expectedLayout.add("abc/qwe");
//        expectedLayout.add("abc/qwe/ert");
//        expectedLayout.add("abc/gh");
//        expectedLayout.add("abc/gh/ert");
//
//        Set<String> actual = storage.dirs(LocalStorageDirectory.get(abcDir)).stream().map(Object::toString).collect(toSet());
//        assertIterableEquals(expectedLayout, actual);
//    }
//
//    @Test
//    public void renameDir() {
//        String srcDirName = "source";
//        String dstDirName = "target";
//        StorageDirectory current = createDir(srcDirName);
//        StorageDirectory renamed = createDir(dstDirName);
//        storage.create(current);
//
//        storage.move(current, renamed);
//        assertFalse(Files.exists(Paths.get(srcDirName)));
//        assertTrue(Files.exists(Paths.get(dstDirName)));
//    }
//
//    @Test
//    public void renameDirWithRenamer() {
//        String srcDirName = "source";
//        String dstDirName = "target";
//        StorageDirectory current = createDir(srcDirName);
//        StorageDirectory renamed = createDir(dstDirName);
//        storage.create(current);
//
//
//        ((LocalStorage) storage).rename(current).to(renamed);
////        storage.move(current, renamed);
//        assertFalse(Files.exists(Paths.get(srcDirName)));
//        assertTrue(Files.exists(Paths.get(dstDirName)));
//    }
//
//
//    @Test
//    public void renameAndResolveDir() {
//        String srcDirName = "abc/source";
//        String dstDirName = "target";
//        StorageDirectory current = createDir(srcDirName);
//        StorageDirectory renamed = createDir(dstDirName);
//        storage.create(current);
//
//        storage.rename(current).to(renamed);
////        storage.rename(current, renamed);
//        assertFalse(Files.exists(Paths.get(srcDirName)));
//        assertTrue(Files.exists(Paths.get("abc", dstDirName)));
//    }
//
//    @Test
//    public void renameAndResolveSubDir() {
//        String srcDirName = "abc/source";
//        String dstDirName = "abc/target";
//        StorageDirectory current = createDir(srcDirName);
//        StorageDirectory renamed = createDir(dstDirName);
//        storage.create(current);
//
//        storage.move(current, renamed);
//        assertFalse(Files.exists(Paths.get(srcDirName)));
//        assertTrue(Files.exists(Paths.get(dstDirName)));
//    }
//
////    @Test
////    public void renameAndResolveSubDirs() throws IOException {
////        String srcDirName = "abc/source";
////        String dstDirName = "ebc/target";
////        StorageDirectory current = createDir(srcDirName);
////        StorageDirectory renamed = createDir(dstDirName);
////
////        storage.create(current);
////
////        storage.move(current, renamed);
////        assertFalse(Files.exists(Paths.get(srcDirName)));
////        assertTrue(Files.exists(Paths.get(dstDirName)));
////    }
//
////    @Test
////    public void renameAndResolveNestedDstDirs() {
////        String srcDirName = "source";
////        String dstDirName = "abc/target";
////        StorageDirectory current = createDir(srcDirName);
////        StorageDirectory renamed = createDir(dstDirName);
////        storage.create(current);
////
////        storage.move(current, renamed);
////        assertFalse(Files.exists(Paths.get(srcDirName)));
////        assertTrue(Files.exists(Paths.get(dstDirName)));
////    }
//
//    //TODO: more understandable exception than NoSuchFileException: source -> /Users/denisshuvalov/Projects/Amazon/open-storage/local/abc/target
///*    @Test
//    public void renameAndResolveNestedDstDirs() {
//        String srcDirName = "source";
//        String dstDirName = "abc/target";
//        StorageDirectory current = createDir(srcDirName);
//        StorageDirectory renamed = createDir(dstDirName);
//        storage.create(current);
//
//        storage.rename(current, renamed);
//        assertFalse(Files.exists(Paths.get(srcDirName)));
//        assertTrue(Files.exists(Paths.get(dstDirName)));
//    }*/
//
// /*   @Test
//    public void moveDirWithFiles() {
//        String srcDirName = "abc/source";
//        String dstDirName = "target";
//        StorageDirectory current = createDir(srcDirName);
//        StorageDirectory target = createDir(dstDirName);
//    }*/
//
//    @Test
//    public void renameFile() {
//        String dir = "dir";
//        String srcFileName = "source.txt";
//        String dstFileName = "target.txt";
//        StorageFile current = LocalStorageFile.get(Paths.get(dir, srcFileName)); //dir/source.txt
//        StorageFile renamed = LocalStorageFile.get(dstFileName);                 //target.txt
//        createFile(Paths.get(dir, dstFileName)); //schedule deletion after the test
//        storage.writer(current).write("1");
//
//        storage.rename(current).to(renamed); //dir/target.txt
////        storage.rename(current, renamed); //dir/target.txt
//        assertFalse(Files.exists(Paths.get(dir, srcFileName)));
//        assertTrue(Files.exists(Paths.get(dir, dstFileName)));
//    }
//
//    @Test
//    public void properlyRenameFileWithingSameDir() {
//        String dir = "dir";
//        String srcFileName = "source.txt";
//        String dstFileName = "target.txt";
//        StorageFile current = LocalStorageFile.get(Paths.get(dir, srcFileName)); //dir/source.txt
//        StorageFile renamed = LocalStorageFile.get(Paths.get(dir, dstFileName)); //dir/target.txt
//        createFile(Paths.get(dir, dstFileName)); //schedule deletion after the test
//        storage.writer(current).write("1");
//
//        storage.move(current, renamed); //dir/target.txt
//        assertFalse(Files.exists(Paths.get(dir, srcFileName)));
//        assertTrue(Files.exists(Paths.get(dir, dstFileName)));
//    }

    @AfterEach
    protected void afterEach() throws IOException {
        for (StorageFile file : filesToDelete) {
            deleteFile(file);
        }

        for (StorageDirectory dir : dirsToDelete) {
            deleteDir(dir);
        }

        filesToDelete.clear();
        dirsToDelete.clear();
    }

    private void markForCleanup(StorageFile file) {
        filesToDelete.add(file);
    }
    private void markForCleanup(StorageDirectory dir) {
        dirsToDelete.add(dir);
    }

    private StorageFile createStorageFile(String fileName) {
        StorageFile result = pathToStorageFile(fileName);
        filesToDelete.add(result);
        return result;
    }

    private StorageDirectory createStorageDir(String path) {
        StorageDirectory result = pathToStorageDir(path);
        dirsToDelete.add(result);
        return result;
    }

    private int calcContentSize(Collection<String> content) {
        //line separator is added after each line
        int separatorsSize = content.size() * LINE_SEP_SIZE;
        return content.stream()
                .map(line -> line.getBytes(UTF_8).length)
                .reduce(0, Integer::sum) + separatorsSize;
    }

    //storage specific methods
    protected abstract Storage storageImpl();
    protected abstract StorageFile pathToStorageFile(String path);
    protected abstract StorageDirectory pathToStorageDir(String path);

    protected abstract void deleteFile(StorageFile file) throws IOException;
    protected abstract void deleteDir(StorageDirectory dir) throws IOException;

    //assertions
    protected abstract void assertFileExist(StorageFile file) throws IOException;
    protected abstract void assertFileSizeEqualsTo(StorageFile file, long size) throws IOException;
    protected abstract void assertFileHasContent(Collection<String> lines) throws IOException;
    protected void assertFileHasContent(String line) throws IOException {
        assertFileHasContent(Stream.of(line).collect(toList()));
    }
    protected abstract void assertDirExist(StorageDirectory dir) throws IOException;
    protected abstract void assertNotExist(StoragePath<?> path) throws IOException;
}
