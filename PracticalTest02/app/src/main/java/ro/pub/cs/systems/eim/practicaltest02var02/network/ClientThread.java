package ro.pub.cs.systems.eim.practicaltest02var02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02var02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Utilities;


/**
 * Created by cata on 15.05.2017.
 */

public class ClientThread extends Thread {

    private int port;
    private String address;
    private String word;

    private TextView wordTextView;

    private Socket socket;

    public ClientThread(int port, String address, String city, TextView wordTextView) {
        this.port = port;
        this.address = address;
        this.word = city;
        this.wordTextView = wordTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            printWriter.println(word);
            printWriter.flush();

            String wordInformation;
            while((wordInformation = bufferedReader.readLine()) != null){

                final String finalizedWordInformation = wordInformation;
                wordTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        wordTextView.setText(finalizedWordInformation);
                    }
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }

        }
    }
}

