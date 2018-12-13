package com.example.sumukhaspc.gym_workout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class MainActivity extends AppCompatActivity implements Imageutils.ImageAttachmentListener {

    static public String PYTHON_SERVER_IP = "192.168.43.10";
    static public int PYTHON_SERVER_HANDSHAKE_PORT = 12346;
    static public int PYTHON_SERVER_FILE_PORT = 12345;


    ImageView iv_attachment;
    Imageutils imageutils;
    Button submitBtn;
    Socket socket;
    String pathOfImage, imageName;
    private Bitmap bitmap;
    private String file_name;
    private byte[] imgbyte;

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
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        submitImageToServer();
                    }
                });
                thread.start();
                startActivity(new Intent(MainActivity.this,process.class));
            }
        });

    }

    private void submitImageToServer() {
        Bitmap bitmap;
        bitmap = ((BitmapDrawable) iv_attachment.getDrawable()).getBitmap();
        imgbyte = getBytesFromBitmap(bitmap);
        Log.d("ClientActivity", "C: Connecting...");
        String filePath = pathOfImage + file_name;
        File file=new File(filePath);


        try {
            socket = new Socket(MainActivity.PYTHON_SERVER_IP, MainActivity.PYTHON_SERVER_FILE_PORT);
            while (true) {


                OutputStream outputStream =socket.getOutputStream();
                byte[] buffer = new byte[4096];
                FileInputStream in = new FileInputStream(file);
                int rBytes;
                while((rBytes = in.read(buffer, 0, 4096)) != -1)
                {
                    outputStream.write(buffer, 0, rBytes);
                }

                outputStream.flush();
                outputStream.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
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
        Log.e("This is the filename:", filename);
        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        Log.e("This is the filename:", path);
        imageutils.createImage(file, filename, path, false);
        imageName = filename;
        pathOfImage = path;
    }
}
