package com.app.client;

import com.app.server.RemoteServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientConnector {

    public RemoteServer connect(String address, int port, String serverName) throws Exception {

        Registry registry = LocateRegistry.getRegistry(address, port);
        RemoteServer server = (RemoteServer) registry.lookup(serverName);
        return server;

    }

}
