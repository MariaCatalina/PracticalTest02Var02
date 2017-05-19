package ro.pub.cs.systems.eim.practicaltest02var02.network;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Utilities;

/**
 * Created by cata on 15.05.2017.
 */

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word !)");

            String word = bufferedReader.readLine();

            if (word == null || word.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word !)");
                return;
            }

            HashMap<String, ArrayList<String>> data = serverThread.getData();
            ArrayList<String> wordsInformation = new ArrayList<>();

            if (data.containsKey(word)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                wordsInformation = data.get(word);
                String result = wordsInformation.toString();

                printWriter.println(result);
                printWriter.flush();

            } else {
                try {

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);

                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, word));

                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                    httpPost.setEntity(urlEncodedFormEntity);

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String pageSourceCode = httpClient.execute(httpPost, responseHandler);

                    // parse response
                    if (pageSourceCode == null) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                        return;
                    }


                    Document document = Jsoup.parse(pageSourceCode);
                    Log.i(Constants.TAG, "run: " + document);

                    Element element = document.child(0);
                    Elements elements = element.getElementsByTag(Constants.SEARCH_KEY);

                    for (Element script : elements) {
                        String wordReceive = script.toString();
                        String[] array = wordReceive.split("\n");

                        Log.i(Constants.TAG, "run.....: " + array[1]);
                        wordsInformation.add(array[1]);
                    }

                    serverThread.setData(word, wordsInformation);

                    if (wordsInformation == null) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] WordsInformation is null!");
                        return;
                    }

                    String result = wordsInformation.toString();

                    printWriter.println(result);
                    printWriter.flush();

                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();

                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException ioException) {
                            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                            if (Constants.DEBUG) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                }

            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}