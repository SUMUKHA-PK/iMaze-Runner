package com.example.sumukhaspc.gym_workout;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class process extends AppCompatActivity {

    Button action_1;
    Button action_2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        action_1 =  findViewById(R.id.action_1);
        action_2 =  findViewById(R.id.action_2);

        action_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = perform_actions("action1");
                        Log.i("errorman","CAME TOACTION!");
                    }
                });
                thread.start();
            }
        });

        action_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = perform_actions("action2");
                        Log.i("errorman","CAME TOACTION@");
                    }
                });
                thread.start();
            }
        });


    }

    public String perform_actions(String action) {
        String result = "";
        try {
            String serverAddress = LoginActivity.IP;
            int serverPort = MainActivity.PYTHON_SERVER_PORT;
            Socket socket = new Socket(serverAddress, serverPort);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write(action);
            bw.flush();

            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                String output = new String(buffer, 0, read);
                result = output;
            }
            socket.close();
        } catch (java.net.UnknownHostException a) {
            Log.i("errorboy", "java.net.UnknownHostException");
            a.printStackTrace();
        } catch (java.io.IOException b) {
            Log.i("errorboy", "java.io.IOException");
            b.printStackTrace();
        }

        return result;
    }
}

