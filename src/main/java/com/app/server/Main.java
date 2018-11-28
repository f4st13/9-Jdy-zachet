package com.app.server;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {

    public static void main(String[] args) throws Exception {

        //final String SECURITY_POLICY_PATH = "./security.policy";
        //System.setProperty("java.security.policy", SECURITY_POLICY_PATH);

        //if (System.getSecurityManager() == null) {
        //    System.setSecurityManager(new SecurityManager());
        //}

        RemoteServerImpl server = new RemoteServerImpl();
        String name = "server";
        Registry registry = LocateRegistry.createRegistry(2099);
        Remote stub = UnicastRemoteObject.exportObject(server, 0);
        registry.bind(name, stub);
        while (true)
        Thread.sleep(Integer.MAX_VALUE);
    }
}
