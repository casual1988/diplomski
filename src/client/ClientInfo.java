/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author Aleksandar
 */
public class ClientInfo implements Serializable{
    private String username;
    private String password;
    private int id;
    private String ip;
    private Socket sock;
   
    public ClientInfo() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }

    @Override
    public String toString() {
        return "username=" + username + ", ip=" + ip + '}';
    }
    
    
}
