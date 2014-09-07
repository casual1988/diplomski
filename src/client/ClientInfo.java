/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.io.Serializable;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;

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
    private PublicKey key;  //mozda cu cuvati javne kljuceve na serveru ako nadjem nacin
    private Key secretKey;
   
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

    public PublicKey getKey() {
        return key;
    }

    public void setKey(PublicKey key) {
        this.key = key;
    }

    public Key getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Key secretKey) {
        this.secretKey = secretKey;
    }
    

    @Override
    public String toString() {
        return "username=" + username + ", ip=" + ip + '}';
    }
    
    
}
