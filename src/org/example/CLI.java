package org.example;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

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
        String Arg = new String();
        String path = new String();

        for(int i = 1; i < args.length; i++) {
            Arg = Arg + " " + args[i];
            path = path + args[i];
        }
        switch (cmd) {
            case "pwd":
                System.out.println(currentDirectory.getAbsolutePath());
                break;
            case "cd":
                changeDirectory(path);
                break;
            case "ls":
                listDirectory(Arg);
                break;
            case "mkdir":
                createDirectory(Arg);
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
            case "mv":
                moveFileOrDirectory(args);
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

    private void changeDirectory(String path) {
        File newPath;
        // Handle Special Cases
        if (path.equals("..")) {
            newPath = currentDirectory.getParentFile();
            if (newPath == null) {
                System.out.println("Directory not found.");
                return;
            }
        } else {
            newPath = new File(path);
            if (!newPath.isAbsolute()) {
                newPath = new File(currentDirectory, path);
            }
        }

        if (newPath.exists() && newPath.isDirectory()) {
            currentDirectory = newPath;

            return;
        } else {
            System.out.println("Directory not found.");
            return;
        }
    }


    private void listDirectory(String command) {
        String[] words = command.split(" ");
        ArrayList<String> realWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if(words[i].length() > 0)
                realWords.add(words[i]);
        }
        String currentDir = currentDirectory.getAbsolutePath();
        File directory = new File(currentDir);
        File[] filesList = directory.listFiles();
        Arrays.sort(filesList);
        if(filesList.length == 0){
            System.out.println("Directory is empty or cannot be read.");
            return;
        }
        if(realWords.size() == 0){
            for(int i = 0; i < filesList.length; i++)
                if(!filesList[i].isHidden()){
                    if(filesList[i].isDirectory())
                        System.out.println("[DIR] " + filesList[i].getName());
                    else
                        System.out.println("[FILE] " + filesList[i].getName());
                }

        }
        else if(realWords.size() == 2 && (realWords.get(0).equals("-r") && realWords.get(1).equals("-a")|| realWords.get(0).equals("-a") && realWords.get(1).equals("-r")) || realWords.size() == 1 && realWords.get(0).equals("-ar")){
            for(int i = filesList.length - 1; i > -1; i--){
                if(filesList[i].isDirectory())
                    System.out.println("[DIR] " + filesList[i].getName());
                else
                    System.out.println("[FILE] " + filesList[i].getName());
            }
        }
        else if(realWords.size() == 1) {
            if(realWords.get(0).equals("-r")){
                for(int i = filesList.length - 1; i > -1; i--)
                    if(!filesList[i].isHidden()){
                        if(filesList[i].isDirectory())
                            System.out.println("[DIR] " + filesList[i].getName());
                        else
                            System.out.println("[FILE] " + filesList[i].getName());
                    }
            }
            else if(realWords.get(0).equals("-a")){
                for(int i = 0; i < filesList.length; i++) {
                    if (filesList[i].isDirectory())
                        System.out.println("[DIR] " + filesList[i].getName());
                    else
                        System.out.println("[FILE] " + filesList[i].getName());
                }
            }
            else
                System.out.println("The given command is not recognized as internal or external command");
        }
        else
            System.out.println("The given command is not recognized as internal or external command");

    }

    private void createDirectory(String command) {
        String[] words = command.split("\"");
        ArrayList<String> dirs = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if(i % 2 != 0)
                dirs.add(words[i]);
            else {
                String[] word = words[i].split(" ");
                for(int j = 0; j < word.length; j++){
                    if(!word[j].equals(""))
                        dirs.add(word[j]);
                }
            }
        }
        for(int i = 0; i < dirs.size(); i++){
            File Directory = new File(currentDirectory.getAbsolutePath()+ "\\" + dirs.get(i));
            if(!Directory.mkdir()){
                if(Directory.exists())
                    System.out.println("Directory with path: " + dirs.get(i) + " already exists");
                else if(!Directory.mkdirs())
                    System.out.println("Directory with path: " + dirs.get(i) + " already exists");
            }
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

    private void moveFileOrDirectory(String[] args) {
        if (args.length < 3) {
            System.out.println("Error. Usage: mv [source] [destination]");
            return;
        }

        File source = new File(currentDirectory, args[1]);
        File destination = new File(currentDirectory, args[2]);

        if (source.exists()) {
            // If destination is a directory, move the source inside it
            if (destination.isDirectory()) {
                destination = new File(destination, source.getName());
            }

            try {
                Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved: " + source.getName() + " to " + destination.getAbsolutePath() + "Successfully!");
            } catch (IOException e) {
                System.out.println("Error moving file/directory.");
            }
        } else {
            System.out.println("Source file/directory not found.");
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
        System.out.println("mv [source] [destination] - move or rename file/directory");
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
