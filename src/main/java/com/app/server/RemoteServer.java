package com.app.server;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {

    String getRootPath() throws RemoteException;

    void addUser(String userName) throws RemoteException;

    boolean containsUser(String userName) throws RemoteException;

    int getChangesLength() throws RemoteException;

    String getMessage(int num) throws RemoteException;

    void addMessage(String message) throws RemoteException;

    // move
    void copy(String fromPathStr, String toPathStr) throws RemoteException;

    void move(String fromPathStr, String toPathStr) throws RemoteException;

    boolean isDirectoryExist(String pathStr) throws RemoteException;

    // md
    void makeDirectory(String pathStr) throws RemoteException;

    // lock
    void block(String pathStr, String username) throws RemoteException;

    //unlock
    void unblock(String pathStr, String user) throws RemoteException;

    boolean isBlocked(String pathStr) throws RemoteException;

    String[] blockedBy(String pathStr) throws RemoteException;

    // rd
    boolean removeDirectory(String pathStr) throws RemoteException;

    //deltree
    boolean removeDirectoryRecursive(String pathStr) throws RemoteException;

    // del
    void removeFile(String pathStr) throws RemoteException;

    // mf
    void makeFile(String pathStr) throws RemoteException;

    String getDelimiter() throws RemoteException;

    // print
    String getFilesTree(String path) throws RemoteException;

    String[] getRoots() throws RemoteException;

    //dir
    String[] getFilesInDirectory(String path) throws RemoteException;

    void removeUser(String userName) throws RemoteException;
}
