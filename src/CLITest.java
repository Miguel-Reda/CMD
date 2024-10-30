// CLITester.java
import org.example.CLI;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CLITest {
    private CLI cli;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private Path testDir;

    @BeforeAll
    public void setupClass() throws IOException {
        testDir = Files.createTempDirectory("cliTestDir");
    }

    @BeforeEach
    public void setUp() {
        cli = new CLI();
        cli.setCurrentDirectory(testDir.toFile());
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    @AfterAll
    public void cleanup() throws IOException {
        Files.walk(testDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void testPwd() {
        cli.executeCommand("pwd");
        assertTrue(outputStream.toString().contains(testDir.toString().trim()));
    }

    @Test
    public void testCd() throws IOException {
        // Create a subdirectory and navigate into it
        Path subDir = Files.createDirectory(testDir.resolve("subDir"));
        cli.executeCommand("cd " + subDir.toString());
        cli.executeCommand("pwd");
        assertTrue(outputStream.toString().contains("subDir"));
    }

    @Test
    public void testCdInvalidDirectory() {
        cli.executeCommand("cd nonExistentDir");
        assertTrue(outputStream.toString().contains("Directory not found."));
    }

    @Test
    public void testLsEmptyDirectory() {
        cli.executeCommand("ls");
        assertTrue(outputStream.toString().contains("Directory is empty or cannot be read."));
    }

    @Test
    public void testLsWithFiles() throws IOException {
        // Create a file to list
        Files.createFile(testDir.resolve("testFile.txt"));
        cli.executeCommand("ls");
        assertTrue(outputStream.toString().contains("testFile.txt"));
    }

    @Test
    public void testLsAll() throws IOException {
        // Create a hidden file
        Files.createFile(testDir.resolve(".hiddenFile"));
        cli.executeCommand("ls -a");
        assertTrue(outputStream.toString().contains(".hiddenFile"));
    }

    @Test
    public void testLsReverseOrder() throws IOException {
        // Create files in order to check reverse listing
        Files.createFile(testDir.resolve("AFile.txt"));
        Files.createFile(testDir.resolve("BFile.txt"));
        cli.executeCommand("ls -r");
        String output = outputStream.toString();
        assertTrue(output.indexOf("BFile.txt") < output.indexOf("AFile.txt"));
    }

    @Test
    public void testMkdir() {
        cli.executeCommand("mkdir newDir");
        assertTrue(Files.exists(testDir.resolve("newDir")));
    }

    @Test
    public void testRmdir() throws IOException {
        // Create and remove a directory
        Path dir = Files.createDirectory(testDir.resolve("toRemove"));
        cli.executeCommand("rmdir toRemove");
        assertFalse(Files.exists(dir));
    }

    @Test
    public void testTouch() {
        cli.executeCommand("touch newFile.txt");
        assertTrue(Files.exists(testDir.resolve("newFile.txt")));
    }

    @Test
    public void testRm() throws IOException {
        // Create a file and delete it
        Path file = Files.createFile(testDir.resolve("fileToDelete.txt"));
        cli.executeCommand("rm fileToDelete.txt");
        assertFalse(Files.exists(file));
    }

    @Test
    public void testCat() throws IOException {
        // Create a file with content and read it
        Path file = Files.write(testDir.resolve("fileToRead.txt"), "Hello CLI".getBytes());
        cli.executeCommand("cat fileToRead.txt");
        assertTrue(outputStream.toString().contains("Hello CLI"));
    }
    @Test
    public void testMv() throws IOException {
    Path sourceFile = Files.createFile(testDir.resolve("sourceFile.txt"));
    Path destinationDir = Files.createDirectory(testDir.resolve("destinationDir"));
    cli.executeCommand("mv sourceFile.txt destinationDir");
    assertFalse(Files.exists(sourceFile));
    assertTrue(Files.exists(destinationDir.resolve("sourceFile.txt")));
}
    @Test
    public void testWriteToFileOverwrite() throws IOException {
        cli.executeCommand("> writeFile.txt HelloWorld");
        Path filePath = testDir.resolve("writeFile.txt");
        assertTrue(Files.readString(filePath).contains("HelloWorld"));
    }

    @Test
    public void testWriteToFileAppend() throws IOException {
        Path filePath = Files.write(testDir.resolve("appendFile.txt"), "Hello".getBytes());
        cli.executeCommand(">> appendFile.txt World");
        assertTrue(Files.readString(filePath).contains("HelloWorld"));
    }

    @Test
    public void testHelp() {
        cli.executeCommand("help");
        assertTrue(outputStream.toString().contains("Available commands:"));
    }

    @Test
    public void testInvalidCommand() {
        cli.executeCommand("nonexistentCommand");
        assertTrue(outputStream.toString().contains("Command not recognized."));
    }

    @Test
    public void testExit() {
        // Exit terminates program, not typically testable in unit tests.
        cli.executeCommand("exit");
        assertTrue(outputStream.toString().contains("Exiting CLI..."));
    }
}
