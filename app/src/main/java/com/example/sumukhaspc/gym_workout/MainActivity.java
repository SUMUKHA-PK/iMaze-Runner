package com.example.sumukhaspc.gym_workout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity implements Imageutils.ImageAttachmentListener {
    static public String PYTHON_SERVER_IP = "192.168.43.125";
    static public int PYTHON_SERVER_PORT = 12345;
    ImageView iv_attachment;
    Imageutils imageutils;
    private Bitmap bitmap;
    private String file_name;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_attachment = findViewById(R.id.imageViewAttach);
        imageutils = new Imageutils(this);
        submitBtn = findViewById(R.id.submitBtn);
        iv_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO: post the image to server on the click of the button
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        iv_attachment.setImageBitmap(file);

        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);
    }

    private boolean contact_server() {
        try {
            String serverAddress = "192.168.43.10";
            int serverPort = 12345;
            Socket socket = new Socket(serverAddress, serverPort);
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
            while ((read = is.read(buffer)) != -1) {
                String output = new String(buffer, 0, read);
                if (output.equals("True"))
                    return true;
                else if (output.equals(("False")))
                    return false;
            }


            socket.close();
        } catch (java.net.UnknownHostException a) {
            Log.i("error", "java.net.UnknownHostException");
        } catch (java.io.IOException b) {
            Log.i("error", "java.io.IOException");
        }
//        TODO: chceck the below line only added return TRUE for deafult since it was throwing an error!
        return true;

    }
}
