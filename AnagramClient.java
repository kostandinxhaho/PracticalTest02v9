package ro.pub.cs.sytems.eim.practicaltest02v9;



import java.io.BufferedReader;

import java.io.BufferedWriter;

import java.io.InputStreamReader;

import java.io.OutputStreamWriter;

import java.net.Socket;

import java.nio.charset.StandardCharsets;



public class AnagramClient {



    public static String request(String host, int port, String requestLine) throws Exception {

        try (Socket s = new Socket(host, port);

             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));

             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8))) {



            out.write(requestLine);

            out.write("\n");

            out.flush();



            String resp = in.readLine();

            return (resp == null) ? "(no response)" : resp;

        }

    }

}