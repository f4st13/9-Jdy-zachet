package com.app.client;

import com.app.server.RemoteServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Arrays;

public class CommandExecutor {

    static final String[] COMMANDS = {"cd", "md", "rd", "deltree", "mf", "del", "block", "unblock", "copy", "move", "print", "dir"};
    private String[] listRoots = {""};

    private String currentPath;
    private RemoteServer server;

    public CommandExecutor(RemoteServer server) {
        this.server = server;
        try {
            listRoots = server.getRoots();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String execute(String[] commands, String userName) throws RemoteException {
        switch (commands[0].toLowerCase()) {
            case "cd": {
                return cd(commands);
            }
            case "md": {
                return md(commands, userName);
            }
            case "print": {
                System.out.println(server.getFilesTree(currentPath));
                return currentPath;
            }
            case "rd": {
                return rd(commands, userName);
            }
            case "dir": {
                return dir(commands);
            }
            case "copy": {
                return copy(commands, userName);
            }
            case "block": {
                return block(commands, userName);
            }
            case "unblock": {
                return unblock(commands, userName);
            }
            case "move": {
                return move(commands, userName);
            }
            case "deltree": {
                return deltree(commands, userName);
            }
            case "mf": {
                return mf(commands, userName);
            }
            case "del": {
                return del(commands, userName);
            }
        }
        return currentPath;
    }

    private String del(String[] commands, String userName) throws RemoteException {
        if (commands.length < 2) {
            System.out.println("\nwrong arguments");
            return currentPath;
        }

        String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
        newPath = combinePaths(currentPath, newPath);

        if (newPath.equals(currentPath)) {
            System.out.println("can't delete current directory");
            return currentPath;
        }

        if (server.isBlocked(newPath)) {
            System.out.println("can't delete blocked directory");
            return currentPath;
        }

        server.removeFile(newPath);
        server.addMessage(String.format("file \"%s\" deleted by user %s", newPath, userName));
        return currentPath;
    }

    private String mf(String[] commands, String userName) throws RemoteException {
        if (commands.length == 1) {
            System.out.println("zero arguments");
            return currentPath;
        }
        if (commands.length > 2) {
            System.out.println("invalid arguments");
            return currentPath;
        }
        String path = combinePaths(currentPath, commands[1]);
        server.makeFile(path);
        server.addMessage(String.format("file \"%s\" created by user %s", path, userName));
        return currentPath;
    }

    private String deltree(String[] commands, String userName) throws RemoteException {

        if (commands.length < 2) {
            System.out.println("\nwrong arguments");
            return currentPath;
        }

        String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
        newPath = combinePaths(currentPath, newPath);

        if (newPath.equals(currentPath)) {
            System.out.println("can't delete current directory");
            return currentPath;
        }

        if (server.isBlocked(newPath)) {
            System.out.println("can't delete blocked directory");
            return currentPath;
        }

        if (containsBlockedFile(newPath)) {
            System.out.println("directory contains blocked file");
            return currentPath;
        }

        boolean removed = server.removeDirectoryRecursive(newPath);
        if (removed) {
            server.addMessage(String.format("directory \"%s\" deleted by user %s", newPath, userName));
        }
        return currentPath;
    }

    private boolean containsBlockedFile(String newPath) {
        try {
            if (Files.walk(Paths.get(newPath)).anyMatch(x -> {
                try {
                    return server.isBlocked(x.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return false;
            })) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String move(String[] commands, String userName) throws RemoteException {
        if (commands.length != 3) {
            System.out.println("wrong arguments. try to put arguments in \"\"");
            return currentPath;
        }

        String stringPathFrom = commands[1];
        String stringPathTo = commands[2];

        stringPathFrom = combinePaths(currentPath, stringPathFrom);
        stringPathTo = combinePaths(currentPath, stringPathTo);

        if (containsBlockedFile(stringPathFrom)) {
            System.out.println("directory contains blocked file");
            return currentPath;
        }

        server.move(stringPathFrom, stringPathTo);
        server.addMessage(String.format("user %s moved \"%s\" to \"%s\"", userName, stringPathFrom, stringPathTo));
        return currentPath;
    }

    private String unblock(String[] commands, String userName) throws RemoteException {
        if (commands.length < 2) {
            System.out.println("wrong arguments");
            return currentPath;
        }

        String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
        newPath = combinePaths(currentPath, newPath);

        server.unblock(newPath, userName);
        server.addMessage(String.format("file \"%s\" is unblocked by %s", newPath, userName));
        return currentPath;
    }

    private String block(String[] commands, String userName) throws RemoteException {
        if (commands.length < 2) {
            System.out.println("wrong arguments");
            return currentPath;
        }

        String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
        newPath = combinePaths(currentPath, newPath);

        server.block(newPath, userName);
        server.addMessage(String.format("file \"%s\" is blocked by %s", newPath, userName));
        return currentPath;
    }

    private String dir(String[] commands) throws RemoteException {
        if (commands.length != 1) {
            System.out.println("wrong arguments");
            return currentPath;
        }

        Arrays.stream(server.getFilesInDirectory(currentPath))
                .sorted()
                .forEachOrdered(System.out::println);
        return currentPath;
    }

    private String copy(String[] commands, String userName) throws RemoteException {
        if (commands.length != 3) {
            System.out.println("wrong arguments. try to put arguments in \"\"");
            return currentPath;
        }

        String stringPathFrom = commands[1];
        String stringPathTo = commands[2];

        stringPathFrom = combinePaths(currentPath, stringPathFrom);
        stringPathTo = combinePaths(currentPath, stringPathTo);

        if (containsBlockedFile(stringPathFrom)) {
            System.out.println("directory contains blocked file");
            return currentPath;
        }

        server.copy(stringPathFrom, stringPathTo);
        server.addMessage(String.format("user %s copied \"%s\" to \"%s\"", userName, stringPathFrom, stringPathTo));
        return currentPath;
    }

    private String rd(String[] commands, String userName) throws RemoteException {
        if (commands.length < 2) {
            System.out.println("\nwrong arguments");
            return currentPath;
        }

        String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
        newPath = combinePaths(currentPath, newPath);

        if (newPath.equals(currentPath)) {
            System.out.println("can't delete current directory");
            return currentPath;
        }

        if (server.isBlocked(newPath)) {
            System.out.println("can't delete blocked directory");
            return currentPath;
        }

        boolean removed = server.removeDirectory(newPath);
        if (removed) {
            server.addMessage(String.format("directory \"%s\" deleted by user %s", newPath, userName));
        }
        return currentPath;
    }

    private String md(String[] commands, String userName) throws RemoteException {
        if (commands.length == 1) {
            System.out.println("zero arguments");
            return currentPath;
        }
        if (commands.length > 2) {
            System.out.println("invalid arguments");
            return currentPath;
        }
        String path = combinePaths(currentPath, commands[1]);
        server.makeDirectory(path);
        server.addMessage(String.format("directory \"%s\" created by user %s", path, userName));
        return currentPath;
    }

    private String cd(String[] commands) throws RemoteException {
        if (commands.length == 1) {
            System.out.println(currentPath);
            return currentPath;
        }
        String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
        newPath = combinePaths(currentPath, newPath);
        if (!server.isDirectoryExist(newPath)) {
            System.out.println("path is not exist");
            return currentPath;
        } else {
            setCurrentPath(newPath);
            return newPath;
        }
    }

    private String combinePaths(String currentPath, String newPath) {
        String combined = newPath;
        if (Arrays.stream(listRoots).map(String::toLowerCase).noneMatch(p -> newPath.toLowerCase().startsWith(p))) {
            try {
                combined = currentPath + server.getDelimiter() + newPath;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        combined = Paths.get(combined).normalize().toString();

        return combined;
    }
}
