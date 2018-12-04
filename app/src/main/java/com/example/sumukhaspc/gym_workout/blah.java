package com.example.peekay.ocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import org.opencv.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.OpenCVLoader.OPENCV_VERSION_3_2_0;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1, flag = 0;
    public String mCurrentPhotoPath, imageFileName, To_Parse = "";
    static final int REQUEST_TAKE_PHOTO = 1;
    Uri photoURI;
    Mat m;
    public MenuItem items;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    m = new Mat();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(MainActivity.this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Functionality of listening to clicks in side navbar
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    // Handle back pressing
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Contact_button) {
            openURL();
        }
        return super.onOptionsItemSelected(item);
    }
    // This is where I should call the camera and other intents

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here
        items = item;
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            dispatchTakePictureIntent();   //Calling picture click
        } else if (id == R.id.nav_gallery) {
            openGalleryIntent();
        } else if (id == R.id.nav_PAN) {
            dispatchTakePictureIntent();
        } else if (id == R.id.nav_Aadhaar) {
            dispatchTakePictureIntent();
        } else if (id == R.id.nav_DL) {
            dispatchTakePictureIntent();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Create an image file

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    // Intent to take the picture

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //After an image is captured
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                ImageView profileImageView = new ImageView(getBaseContext());
                profileImageView.setImageBitmap(bitmap);

                if (!isBlurredImage(bitmap)) {
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);  // Pass image to create a FirebaseVisionImage

                    FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();  //Instance of the firebaseimagevision detector

                    Task<FirebaseVisionText> result =                 //Passing to detectInImage method
                            detector.detectInImage(image)
                                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                        @Override
                                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                            To_Parse = "";  // Making string null
                                            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                                                Rect boundingBox = block.getBoundingBox();

                                                Point[] cornerPoints = block.getCornerPoints();

                                                String text = block.getText();
                                                Add_to_string(text);

                                                for (FirebaseVisionText.Line line : block.getLines()) {

                                                    for (FirebaseVisionText.Element element : line.getElements()) {

                                                    }
                                                }
                                            }
                                            if (items.getItemId() == R.id.nav_camera){
                                                Toast.makeText(getApplicationContext(), "Picture stored", Toast.LENGTH_LONG).show();
                                            }
                                            if (items.getItemId() == R.id.nav_PAN) {
                                                pan_scan(To_Parse);
                                                //Toast.makeText(getApplicationContext(), "Sample Text", Toast.LENGTH_LONG).show();
                                            }
                                            if (items.getItemId() == R.id.nav_DL) {
                                                dl_scan(To_Parse);
                                            }
                                            if (items.getItemId() == R.id.nav_Aadhaar) {
                                                aadhaar_scan(To_Parse);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Task failed with an exception
                                                    // ...
                                                }
                                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Image Blurred", Toast.LENGTH_LONG).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Opens gallery of the photos shot in application
    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getRootDirectory().getPath() + "/com.example.peekay.ocr/files/Pictures");
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open"));
    }

    private void openURL() {
        Uri uri = Uri.parse("https://github.com/SUMUKHA-PK");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private synchronized boolean isBlurredImage(Bitmap image) {
        try {
            if (image != null) {

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inDither = true;
                opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

                int l = CvType.CV_8UC1;

                Mat matImage = new Mat();

                Utils.bitmapToMat(image, matImage);
                Mat matImageGrey = new Mat();
                Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

                Mat dst2 = new Mat();
                Utils.bitmapToMat(image, dst2);

                Mat laplacianImage = new Mat();
                dst2.convertTo(laplacianImage, l);
                Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
                Mat laplacianImage8bit = new Mat();
                laplacianImage.convertTo(laplacianImage8bit, l);
                System.gc();
                Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(),
                        laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(laplacianImage8bit, bmp);

                int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
                        bmp.getHeight());
                if (bmp != null)
                    if (!bmp.isRecycled()) {
                        bmp.recycle();

                    }
                int maxLap = -16777216;

                for (int i = 0; i < pixels.length; i++) {

                    if (pixels[i] > maxLap) {
                        maxLap = pixels[i];
                    }
                }
                int soglia = -6118750;

                if (maxLap < soglia || maxLap == soglia) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (OutOfMemoryError e) {
            return false;
        }
    }

    public void sendMessage(String s) {
        TextView textView = (TextView) findViewById(R.id.Output_data);
        Log.i("Out:", s);
        textView.setText(s);
    }

    public void Add_to_string(String string) {
        To_Parse = To_Parse + string;
    }

    // Below methods must call the dispatchPictureIntent and add all the scanned data, recognise which card has been scannned
    public void pan_scan(String parsing) {
        sendMessage(parsing);
        String check1 = "INCOME"; // PAN
        String check2 = "Permanent Account Number"; //PAN
        String check3 = "DL";       //DL
        String check4 = "Licencing";//DL
        String check5 = "LMV";   // DL : Light motor vehicle
        String check6 = "MCWG";  // DL : Motorcycle with gear
        String check7 = "TAX";   // PAN
        String check8 = "COV";   // DL : Category of vehicle
        if ((parsing.toLowerCase().contains(check1.toLowerCase())) || (parsing.toLowerCase().contains(check2.toLowerCase())) || (parsing.toLowerCase().contains(check7.toLowerCase()))) {
            Toast.makeText(getApplicationContext(), "PAN card accepted", Toast.LENGTH_LONG).show();
        } else if ((parsing.toLowerCase().contains(check3.toLowerCase())) || (parsing.toLowerCase().contains(check4.toLowerCase())) || (parsing.toLowerCase().contains(check5.toLowerCase())) || (parsing.toLowerCase().contains(check6.toLowerCase()))|| (parsing.toLowerCase().contains(check8.toLowerCase()))) {
            Toast.makeText(getApplicationContext(), "You've chosen PAN, but this is DL", Toast.LENGTH_LONG).show();
        }
        else if (check_aadhaar(parsing)) {
            Toast.makeText(getApplicationContext(), "You've chosen PAN, but this is an Aadhaar card", Toast.LENGTH_LONG).show();
        }
    }

    public void dl_scan (String parsing){
        sendMessage(parsing);
        String check1 = "INCOME"; // PAN
        String check2 = "Permanent Account Number"; //PAN
        String check3 = "DL";       //DL
        String check4 = "Licencing";//DL
        String check5 = "LMV";   // DL : Light motor vehicle
        String check6 = "MCWG";  // DL : Motorcycle with gear
        String check7 = "TAX";   // PAN
        String check8 = "COV";   // DL : Category of vehicle
        if ((parsing.toLowerCase().contains(check3.toLowerCase())) || (parsing.toLowerCase().contains(check4.toLowerCase())) || (parsing.toLowerCase().contains(check5.toLowerCase())) || (parsing.toLowerCase().contains(check6.toLowerCase()))|| (parsing.toLowerCase().contains(check8.toLowerCase()))) {
            Toast.makeText(getApplicationContext(), "DL accepted", Toast.LENGTH_LONG).show();
        }
        else if ((parsing.toLowerCase().contains(check1.toLowerCase())) || (parsing.toLowerCase().contains(check2.toLowerCase())) || (parsing.toLowerCase().contains(check7.toLowerCase()))) {
            Toast.makeText(getApplicationContext(), "You've chosen DL but this is a PAN card", Toast.LENGTH_LONG).show();
        }
        else if (check_aadhaar(parsing)) {
            Toast.makeText(getApplicationContext(), "You've chosen DL but this is an Aadhaar card", Toast.LENGTH_LONG).show();
        }
    }

    private void aadhaar_scan (String parsing){
        sendMessage(parsing);
        String check1 = "INCOME"; // PAN
        String check2 = "Permanent Account Number"; //PAN
        String check3 = "DL";       //DL
        String check4 = "Licencing";//DL
        String check5 = "LMV";   // DL : Light motor vehicle
        String check6 = "MCWG";  // DL : Motorcycle with gear
        String check7 = "TAX";   // PAN
        String check8 = "COV";   // DL : Category of vehicle
        String check9 = "";
        String check10 = "";
        if(check_aadhaar(parsing)){
            Toast.makeText(getApplicationContext(), "Aadhaar accepted", Toast.LENGTH_LONG).show();
        }
        else if ((parsing.toLowerCase().contains(check3.toLowerCase())) || (parsing.toLowerCase().contains(check4.toLowerCase())) || (parsing.toLowerCase().contains(check5.toLowerCase())) || (parsing.toLowerCase().contains(check6.toLowerCase()))|| (parsing.toLowerCase().contains(check8.toLowerCase()))) {
            Toast.makeText(getApplicationContext(), "You've chosen Aadhaar but this is a DL", Toast.LENGTH_LONG).show();
        }
        else if ((parsing.toLowerCase().contains(check1.toLowerCase())) || (parsing.toLowerCase().contains(check2.toLowerCase())) || (parsing.toLowerCase().contains(check7.toLowerCase()))) {
            Toast.makeText(getApplicationContext(), "You've chosen Aadhaar but this is a PAN card", Toast.LENGTH_LONG).show();
        }
    }
    private boolean check_aadhaar(String s){
        long len = s.length();
        int i;
        for(i=0;i<len-13;i++)
        {
            if(((s.charAt(i)>='0')&&(s.charAt(i)<='9'))&&((s.charAt(i+1)>='0')&&(s.charAt(i+1)<='9'))&&((s.charAt(i+2)>='0')&&(s.charAt(i+2)<='9')))
            {
                if(((s.charAt(i+3)>='0')&&(s.charAt(i+3)<='9'))&&(s.charAt(i+4)==' ')&&((s.charAt(i+5)>='0')&&(s.charAt(i+5)<='9'))){
                    if(((s.charAt(i+7)>='0')&&(s.charAt(i+7)<='9'))&&((s.charAt(i+8)>='0')&&(s.charAt(i+8)<='9'))&&((s.charAt(i+6)>='0')&&(s.charAt(i+6)<='9'))){
                        if(((s.charAt(i+10)>='0')&&(s.charAt(i+10)<='9'))&&((s.charAt(i+11)>='0')&&(s.charAt(i+11)<='9'))&&(s.charAt(i+9)==' ')){
                            if(((s.charAt(i+13)>='0')&&(s.charAt(i+13)<='9'))&&((s.charAt(i+12)>='0')&&(s.charAt(i+12)<='9'))){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
