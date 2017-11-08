package com.example.a123.storage_ppt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class StorageActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICKFILE_RESULT_CODE = 1;
    private ImageView image;
    Button choose,upload;
    Button show;
    EditText NameText;
    private Uri filepath;
    private StorageReference StorageRef;
    CheckBox Share;
    String Filename;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Upload");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        StorageRef = FirebaseStorage.getInstance().getReference();

        image=(ImageView)findViewById(R.id.image);
        choose=(Button)findViewById(R.id.choose);
        upload=(Button)findViewById(R.id.upload);
        NameText = (EditText) findViewById(R.id.Name);
        Share=(CheckBox) findViewById(R.id.recycle_share_checkBox);
        show=(Button)findViewById(R.id.show);

        choose.setOnClickListener(this);
        upload.setOnClickListener(this);
       show.setOnClickListener(this);

    }
    private void filechooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/pptx");
        startActivityForResult(intent.createChooser(intent,"請選擇一項要上傳的檔案"),PICKFILE_RESULT_CODE);
    }
    private void fileupload() {
        if (filepath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("上傳中......");
            progressDialog.show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();//屬性命名為message

            if (Share.isChecked() == true) {
                Filename = "Public/";
            } else {
                Filename = "Pravite/";
            }
            myRef.child("Upload").child(Filename).child(NameText.getText().toString()).child("PPT'sName").setValue(NameText.getText().toString());
            StorageReference riversRef = StorageRef.getStorage().getReference().child(Filename).child(NameText.getText().toString());
            riversRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "上傳成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("已上傳" + ((int) progress) + "%，請稍後!");
                        }
                    });
        } else {
            Toast.makeText(this, "未選擇檔案", Toast.LENGTH_SHORT).show();
        }
    }
    private void showfild(){

        startActivity(new Intent(StorageActivity.this, ViewrActivity.class));
        StorageActivity.this.finish();
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
        if(v==choose) {
            filechooser();
        }else if(v==upload){
            fileupload();
        }else if(v==show){
          showfild();
        }
    }
}