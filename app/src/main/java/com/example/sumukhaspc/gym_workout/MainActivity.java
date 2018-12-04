package com.example.sumukhaspc.gym_workout;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button Submit_image = (Button) findViewById(R.id.submit);

        Submit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(contact_server()){
                    //start another activity or display request
                }
                else{
                    //show error message or whatever
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean contact_server(){
        try{
            String serverAddress = "192.168.43.10";
            int serverPort = 12345;
            Socket socket = new Socket(serverAddress,serverPort);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write("one");
            bw.write("/0");
            bw.write("/0");
            bw.flush();

            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            while((read = is.read(buffer)) != -1) {
                String output = new String(buffer, 0, read);
                if(output.equals("True"))
                    return true;
                else if(output.equals(("False")))
                    return false;
            }


            socket.close();
        }
        catch (java.net.UnknownHostException a)
        {
            Log.i("error","java.net.UnknownHostException");
        }
        catch (java.io.IOException b){
            Log.i("error","java.io.IOException");
        }
    }



}
