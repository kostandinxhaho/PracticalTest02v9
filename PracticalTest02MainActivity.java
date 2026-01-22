package ro.pub.cs.sytems.eim.practicaltest02v9;



import android.os.Bundle;

import android.util.Log;

import android.widget.Button;

import android.widget.EditText;

import android.widget.TextView;

import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;



import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;



public class PracticalTest02MainActivity extends AppCompatActivity {



    private static final String TAG = "PT02V9";



    private EditText etPort, etWord, etMinLetters;

    private TextView tvResult;

    private Button btnStartServer, btnStopServer, btnSendRequest;



    private AnagramServerThread serverThread;

    private final ExecutorService clientExecutor = Executors.newSingleThreadExecutor();



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        etPort = findViewById(R.id.etPort);

        etWord = findViewById(R.id.etWord);

        etMinLetters = findViewById(R.id.etMinLetters);

        tvResult = findViewById(R.id.tvResult);



        btnStartServer = findViewById(R.id.btnStartServer);

        btnStopServer = findViewById(R.id.btnStopServer);

        btnSendRequest = findViewById(R.id.btnSendRequest);



        btnStartServer.setOnClickListener(v -> startServer());

        btnStopServer.setOnClickListener(v -> stopServer());

        btnSendRequest.setOnClickListener(v -> sendClientRequest());

    }



    private void startServer() {

        String portStr = etPort.getText().toString().trim();

        if (portStr.isEmpty()) {

            Toast.makeText(this, "Port required", Toast.LENGTH_SHORT).show();

            return;

        }



        int port;

        try {

            port = Integer.parseInt(portStr);

        } catch (NumberFormatException e) {

            Toast.makeText(this, "Invalid port", Toast.LENGTH_SHORT).show();

            return;

        }



        if (serverThread != null && serverThread.isRunning()) {

            Toast.makeText(this, "Server already running", Toast.LENGTH_SHORT).show();

            return;

        }



        serverThread = new AnagramServerThread(port);

        serverThread.startServer();



        Log.i(TAG, "Server started on port " + port);

        Toast.makeText(this, "Server started on port " + port, Toast.LENGTH_SHORT).show();

    }



    private void stopServer() {

        if (serverThread != null) {

            serverThread.stopServer();

            Log.i(TAG, "Server stopped");

        }

        Toast.makeText(this, "Server stopped", Toast.LENGTH_SHORT).show();

    }



    private void sendClientRequest() {

        String portStr = etPort.getText().toString().trim();

        String word = etWord.getText().toString().trim();

        String minStr = etMinLetters.getText().toString().trim();



        if (portStr.isEmpty() || word.isEmpty() || minStr.isEmpty()) {

            Toast.makeText(this, "Port/word/min letters required", Toast.LENGTH_SHORT).show();

            return;

        }



        int port, minLetters;

        try {

            port = Integer.parseInt(portStr);

            minLetters = Integer.parseInt(minStr);

        } catch (NumberFormatException e) {

            Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show();

            return;

        }



        tvResult.setText("Loading...");



        clientExecutor.execute(() -> {

            try {

                String requestLine = word + " " + minLetters; // format simplu: "<cuvant> <min_litere>"

                Log.i(TAG, "CLIENT sending: " + requestLine);



                String resp = AnagramClient.request("127.0.0.1", port, requestLine);



                runOnUiThread(() -> tvResult.setText(resp));

            } catch (Exception e) {

                Log.e(TAG, "Client error", e);

                runOnUiThread(() -> tvResult.setText("Client error: " + e.getMessage()));

            }

        });

    }



    @Override

    protected void onDestroy() {

        super.onDestroy();

        stopServer();

        clientExecutor.shutdownNow();

    }

}