package com.example.sumukhaspc.gym_workout;

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
import java.net.Socket;

public class process extends AppCompatActivity {

    static public String PYTHON_SERVER_IP = "192.168.43.10";
    static public int PYTHON_SERVER_ACTION_PORT = 12347;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button action_1 = (Button) findViewById(R.id.action_1);
        Button action_2 = (Button) findViewById(R.id.action_2);

        action_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = perform_action1();
            }
        });


    }

    public boolean perform_action1() {
        boolean result = false;
        try {
            String serverAddress = process.PYTHON_SERVER_IP;
            int serverPort = process.PYTHON_SERVER_ACTION_PORT;
            Socket socket = new Socket(serverAddress, serverPort);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write("action1");
            bw.flush();

            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                String output = new String(buffer, 0, read);
                if (output.equals("True"))
                    result = true;
                else if (output.equals(("False")))
                    result = false;
            }


            socket.close();
        } catch (java.net.UnknownHostException a) {
            Log.i("error", "java.net.UnknownHostException");
            a.printStackTrace();
        } catch (java.io.IOException b) {
            Log.i("error", "java.io.IOException");
            b.printStackTrace();
        }
        return result;
    }
}
