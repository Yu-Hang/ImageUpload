package com.example.imageupload;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// This activity class contains a button that invokes gallery event, from where the user can choose an
// image. The image path will then be pass back to callback function onActivityResult, where the image can be
// load into memory as a Bitmap.

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_GALLERY = 1;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private Button galleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        galleryButton = findViewById(R.id.button);

        // set on click listener
        galleryButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_GALLERY); // goes to callback function onActivityResult after an image is chosen
                    }
                }
        );

        // pop up prompt to get permissions from user
        final List<String> permissionsList = new ArrayList<String>();

        addPermission(permissionsList, Manifest.permission.INTERNET);
        addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionsList.size() > 0){
            ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_PERMISSIONS);

            return;
        }
    }

    // callback function
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = data.getData();

            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, filepath, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filepath[0]);
            final String picturePath = cursor.getString(columnIndex);  // read image from this path
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

        }
    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:

                Map<String, Integer> perms = new HashMap<String, Integer>();

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);

                for(int i=0; i<permissions.length;i++){
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
