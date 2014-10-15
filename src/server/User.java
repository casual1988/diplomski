/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import client.ClientInfo;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author Aleksandar
 */
public class User implements Serializable{
    private ClientInfo client;
    private Socket socket;

    public User() {
    }

    public User(ClientInfo client) {
        this.client = client;
    }

    public ClientInfo getClient() {
        return client;
    }

    public void setClient(ClientInfo client) {
        this.client = client;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

   

  
}
