import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class CLI {
    private File currentDirectory;

    public CLI() {
        this.currentDirectory = new File(System.getProperty("user.dir"));
    }

    public void setCurrentDirectory(File directory) {
        this.currentDirectory = directory;
    }

    public void executeCommand(String command) {
        String[] args = command.split(" ");
        String cmd = args[0];

        switch (cmd) {
            case "pwd":
                System.out.println(currentDirectory.getAbsolutePath());
                break;
            case "cd":
                changeDirectory(args);
                break;
            case "ls":
                listDirectory(args);
                break;
            case "mkdir":
                createDirectory(args);
                break;
            case "rmdir":
                removeDirectory(args);
                break;
            case "touch":
                createFile(args);
                break;
            case "rm":
                deleteFile(args);
                break;
            case "cat":
                displayFileContent(args);
                break;
            case ">":
                writeToFile(args, false);
                break;
            case ">>":
                writeToFile(args, true);
                break;
            case "help":
                displayHelp();
                break;
            case "exit":
                System.out.println("Exiting CLI...");
                System.exit(0);
                break;
            default:
                System.out.println("Command not recognized.");
        }
    }

    private void changeDirectory(String[] args) {
        if (args.length < 2) {
            System.out.println("No directory specified.");
            return;
        }
        File newDir = new File(args[1]);
        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir;
        } else {
            System.out.println("Directory not found.");
        }
    }

    private void listDirectory(String[] args) {
        boolean showHidden = Arrays.asList(args).contains("-a");
        boolean reverse = Arrays.asList(args).contains("-r");

        String[] files = currentDirectory.list((dir, name) -> showHidden || !name.startsWith("."));

        if (files != null) {
            if (reverse) {
                Arrays.sort(files, Collections.reverseOrder());
            }
            for (String file : files) {
                System.out.println(file);
            }
        } else {
            System.out.println("Directory is empty or cannot be read.");
        }
    }

    private void createDirectory(String[] args) {
        if (args.length < 2) {
            System.out.println("Directory name not specified.");
            return;
        }
        File dir = new File(currentDirectory, args[1]);
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            System.out.println("Directory already exists.");
        }
    }

    private void removeDirectory(String[] args) {
        if (args.length < 2) {
            System.out.println("Directory name not specified.");
            return;
        }
        File dir = new File(currentDirectory, args[1]);
        if (dir.exists() && dir.isDirectory()) {
            dir.delete();
        } else {
            System.out.println("Directory not found.");
        }
    }

    private void createFile(String[] args) {
        if (args.length < 2) {
            System.out.println("File name not specified.");
            return;
        }
        File file = new File(currentDirectory, args[1]);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error creating file.");
        }
    }

    private void deleteFile(String[] args) {
        if (args.length < 2) {
            System.out.println("File name not specified.");
            return;
        }
        File file = new File(currentDirectory, args[1]);
        if (file.exists() && file.isFile()) {
            file.delete();
        } else {
            System.out.println("File not found.");
        }
    }

    private void displayFileContent(String[] args) {
        if (args.length < 2) {
            System.out.println("File name not specified.");
            return;
        }
        File file = new File(currentDirectory, args[1]);
        try {
            Files.lines(file.toPath()).forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    private void writeToFile(String[] args, boolean append) {
        if (args.length < 3) {
            System.out.println("Insufficient arguments.");
            return;
        }
        File file = new File(currentDirectory, args[1]);
        try (FileWriter fw = new FileWriter(file, append)) {
            fw.write(args[2]);
            System.out.println("Write successful.");
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("pwd - print current directory");
        System.out.println("cd [directory] - change directory");
        System.out.println("ls - list files in directory");
        System.out.println("ls -a - list all files, including hidden");
        System.out.println("ls -r - list files in reverse order");
        System.out.println("mkdir [name] - create directory");
        System.out.println("rmdir [name] - remove directory");
        System.out.println("touch [name] - create file");
        System.out.println("rm [name] - remove file");
        System.out.println("cat [file] - display file content");
        System.out.println("> [file] [text] - overwrite file with text");
        System.out.println(">> [file] [text] - append text to file");
        System.out.println("exit - exit the CLI");
        System.out.println("help - display this help message");
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the CLI! Type 'help' for available commands.");

        while (true) {
            System.out.print("CLI> ");
            String command = scanner.nextLine();
            cli.executeCommand(command);
        }
    }
}
