package ro.pub.cs.systems.eim.practicaltest02var02.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02var02.general.Constants;


/**
 * Created by cata on 15.05.2017.
 */

public class ServerThread extends Thread {
    private int port;
    private ServerSocket serverSocket;

    private HashMap<String, ArrayList<String>> data = null;

    public ServerThread(int port) {
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.i(Constants.TAG, "ServerThread: " + e.getMessage());
        }

        this.data = new HashMap<>();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            Log.i(Constants.TAG, "server in on and waiting for clients... ");
            try {
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();


            } catch (IOException e) {
                Log.i(Constants.TAG, "serverThread: " +  e.getMessage());
            }

        }
    }

    public Object getServerSocket() {
        return serverSocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public synchronized HashMap<String,  ArrayList<String>> getData() {
        return data;
    }

    public synchronized void setData(String city, ArrayList<String> words) {
        this.data.put(city, words);
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
