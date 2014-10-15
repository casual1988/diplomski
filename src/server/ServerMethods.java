/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.ClientInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Aleksandar
 */
public class ServerMethods {

//    public static String listUsers1(ArrayList<User> users) {
//        String response = "";
//        for (int i = 0; i < users.size(); i++) {
//            response = response + " " + users.get(i).getUsername() + " ";
//        }
//        return response;
//    }
//
//    public static String listUsers(ArrayList<User> users, Socket sock) {
//        String response = "";
//        for (int i = 0; i < users.size(); i++) {
//            if (!users.get(i).getSocket().equals(sock)) //    response= response+users.get(i).getSocket().getPort()+users.get(i).getSocket().getLocalAddress()+"*";
//            {
//                response = response + users.get(i).getUsername() + " ";
//            }
//            //   response= response+users.get(i).getSocket().getPort()+" ";
//        }
//        return response;
//    }
    //metoda koja vraca Usera skojim prvi klijent zeli da ostvari komunikaciju
//    public static User call(ArrayList<User> users, String calluser) {
//
//        for (int i = 0; i < users.size(); i++) {
//            if (users.get(i).getUsername().equals(calluser)) //    response= response+users.get(i).getSocket().getPort()+users.get(i).getSocket().getLocalAddress()+"*";
//            {
//                return users.get(i);
//            }
//
//        }
//        return null;
//    }
    //metoda koja vraca listu online usera
    public static ArrayList<ClientInfo> getOnlineUser(ArrayList<User> users, Socket sock) {
        ArrayList<ClientInfo> onlines = new ArrayList<ClientInfo>();
        for (int i = 0; i < users.size(); i++) {
            if (!users.get(i).getSocket().equals(sock)) //    response= response+users.get(i).getSocket().getPort()+users.get(i).getSocket().getLocalAddress()+"*";
            {
                System.out.println(users.get(i).getClient());
                onlines.add(users.get(i).getClient());
            }
            //   response= response+users.get(i).getSocket().getPort()+" ";
        }
        return onlines;

    }

    public static void signout(ArrayList<User> users, Socket sock) {
        ArrayList<ClientInfo> onlines = new ArrayList<ClientInfo>();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getSocket().equals(sock)) //    response= response+users.get(i).getSocket().getPort()+users.get(i).getSocket().getLocalAddress()+"*";
            {
                System.out.println("izbrisan");
                Server.getUsers().remove(i);
            }
            //   response= response+users.get(i).getSocket().getPort()+" ";
        }

    }

    public static boolean login(ArrayList<User> users) {
        return true;
    }

    public static void sendMessage(User user, String message) {
        try {

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(user.getSocket().getOutputStream())), true);
            out.println(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
