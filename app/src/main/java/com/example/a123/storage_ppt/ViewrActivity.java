package com.example.a123.storage_ppt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.itsrts.pptviewer.PPTViewer;

/**
 * Created by 123 on 2017/11/6.
 */
public class ViewrActivity extends AppCompatActivity {
    PPTViewer pptViewer;
    String FilePath ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pptviewr);
        pptViewer = (PPTViewer) findViewById(R.id.pptview);
        FilePath =this.getIntent().getExtras().getString("path");
        pptViewer.setNext_img(R.drawable.next).setPrev_img(R.drawable.prev)
                .setSettings_img(R.drawable.settings)
                .setZoomin_img(R.drawable.zoomin)
                .setZoomout_img(R.drawable.zoomout);
        try {
            pptViewer.loadPPT(this,FilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

