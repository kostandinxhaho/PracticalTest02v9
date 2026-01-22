package ro.pub.cs.sytems.eim.practicaltest02v9;



import android.util.Log;



import java.io.BufferedReader;

import java.io.BufferedWriter;

import java.io.InputStreamReader;

import java.io.OutputStreamWriter;

import java.net.ServerSocket;

import java.net.Socket;

import java.nio.charset.StandardCharsets;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;



public class AnagramServerThread {



    private static final String TAG = "PT02V9_SERVER";



    private final int port;

    private volatile boolean running = false;

    private ServerSocket serverSocket;



    private Thread acceptThread;

    private final ExecutorService workers = Executors.newCachedThreadPool();



    public AnagramServerThread(int port) {

        this.port = port;

    }



    public boolean isRunning() {

        return running;

    }



    public void startServer() {

        running = true;



        acceptThread = new Thread(() -> {

            try {

                serverSocket = new ServerSocket(port);

                Log.i(TAG, "Listening on port " + port);



                while (running) {

                    Socket client = serverSocket.accept();

                    workers.execute(() -> handleClient(client));

                }

            } catch (Exception e) {

                if (running) {

                    Log.e(TAG, "Server error", e);

                } else {

                    Log.i(TAG, "Server socket closed");

                }

            }

        });



        acceptThread.start();

    }



    public void stopServer() {

        running = false;

        try {

            if (serverSocket != null) serverSocket.close();

        } catch (Exception ignored) {}

        workers.shutdownNow();

    }



    private void handleClient(Socket client) {

        try (Socket c = client;

             BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8));

             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(c.getOutputStream(), StandardCharsets.UTF_8))) {



            String line = in.readLine();

            Log.i(TAG, "Received from client: " + line);



            if (line == null || line.trim().isEmpty()) {

                out.write("ERROR empty request\n");

                out.flush();

                return;

            }



            String[] parts = line.trim().split("\\s+");

            String word = parts[0];

            int minLetters = 1;

            if (parts.length >= 2) {

                try {

                    minLetters = Integer.parseInt(parts[1]);

                } catch (NumberFormatException ignored) {

                    minLetters = 1;

                }

            }


            String response = AnagramWebService.fetchAndFilter(word, minLetters);



            out.write(response + "\n");

            out.flush();



        } catch (Exception e) {

            Log.e(TAG, "Client handler error", e);

        }

    }

}