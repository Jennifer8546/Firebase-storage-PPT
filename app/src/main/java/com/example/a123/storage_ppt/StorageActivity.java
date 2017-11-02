package com.example.a123.storage_ppt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class StorageActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICKFILE_RESULT_CODE = 1;
    private ImageView image;
    Button choose,upload;
    EditText NameText;
    private Uri filepath;
    private StorageReference StorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        StorageRef = FirebaseStorage.getInstance().getReference();

        image=(ImageView)findViewById(R.id.image);
        choose=(Button)findViewById(R.id.choose);
        upload=(Button)findViewById(R.id.upload);
        NameText = (EditText) findViewById(R.id.Name);
        choose.setOnClickListener(this);
        upload.setOnClickListener(this);

    }
    private void showfilechooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent.createChooser(intent,"Select the PPT"),PICKFILE_RESULT_CODE);
    }
    private void fileupload(){
        if(filepath!=null) {
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference riversRef = StorageRef.getStorage().getReference().child("PPT/").child(NameText.getText().toString());;

            riversRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"uploaded",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress =(100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int)progress)+"% uploaded...!!");
                        }
                    });
        }

        else{
            Toast.makeText(this,"no file selected",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== PICKFILE_RESULT_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            filepath=data.getData();

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v==choose)
        {showfilechooser();

        }else if(v==upload){
            fileupload();

        }
    }
}