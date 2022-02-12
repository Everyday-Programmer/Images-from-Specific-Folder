package com.android.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        String path = null;
        ImageView imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        if (intent != null) {
            Glide.with(ImageViewerActivity.this).load(intent.getStringExtra("image")).placeholder(R.drawable.ic_baseline_broken_image_24).into(imageView);
            path = intent.getStringExtra("image");
        }

        ImageButton share = findViewById(R.id.shareImage);
        String finalPath = path;
        share.setOnClickListener(v -> new ShareCompat.IntentBuilder(ImageViewerActivity.this).setStream(Uri.parse(finalPath)).setType("image/*").setChooserTitle("Share Image").startChooser());

        ImageButton delete = findViewById(R.id.deleteImage);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ImageViewerActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to delete this image ?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] projection = new String[]{MediaStore.Images.Media._ID};
                        String selection = MediaStore.Images.Media.DATA + " = ?";
                        String[] selectionArgs = new String[]{new File(finalPath).getAbsolutePath()};
                        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver contentResolver = getContentResolver();
                        Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                        if (cursor.moveToFirst()) {
                            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                            try {
                                contentResolver.delete(deleteUri, null, null);
                                boolean delete1 = new File(finalPath).delete();
                                Log.e("TAG", delete1 + "");
                                Toast.makeText(ImageViewerActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ImageViewerActivity.this, "Error Deleting Video", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ImageViewerActivity.this, "File Not Find", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }
}