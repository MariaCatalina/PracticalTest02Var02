package eim.systems.cs.pub.ro.practicaltest02var02.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import eim.systems.cs.pub.ro.practicaltest02.R;
import eim.systems.cs.pub.ro.practicaltest02var02.general.Constants;
import eim.systems.cs.pub.ro.practicaltest02var02.network.ClientThread;
import eim.systems.cs.pub.ro.practicaltest02var02.network.ServerThread;

public class PracticalTest02Var02MainActivity extends AppCompatActivity {

    // Server
    private EditText portServerEditText;
    private Button connectServerBtn;

    //Client
    private EditText wordEditText;
    private Button getWordBtn;
    private TextView wordInformationsTextView;

    //
    private ServerThread serverThread = null;
    private ClientThread clientThread = null;


    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = portServerEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please insert a valid port for server", Toast.LENGTH_SHORT).show();
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null){
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }

            serverThread.start();
        }
    }

    private GetWeatherForecastListener getWeatherForecastListener = new GetWeatherForecastListener();
    private class GetWeatherForecastListener implements Button.OnClickListener{

        @Override
        public void onClick(View view) {
            String clientAddress = Constants.ADDRESS;
            String clientPort = portServerEditText.getText().toString();

            if (clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()){
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect", Toast.LENGTH_SHORT).show();
                return;
            }

            String city = wordEditText.getText().toString();

            wordInformationsTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(Integer.valueOf(clientPort), clientAddress, city, wordInformationsTextView);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_var02_main);

        // server
        connectServerBtn = (Button) findViewById(R.id.connectServer);
        connectServerBtn.setOnClickListener(connectButtonClickListener);

        portServerEditText = (EditText) findViewById(R.id.serverPort);

        getWordBtn = (Button) findViewById(R.id.getWeatherForecastBtn);
        getWordBtn.setOnClickListener(getWeatherForecastListener);

        wordEditText = (EditText) findViewById(R.id.word);

        wordInformationsTextView = (TextView) findViewById(R.id.weatherForecastInformation);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
