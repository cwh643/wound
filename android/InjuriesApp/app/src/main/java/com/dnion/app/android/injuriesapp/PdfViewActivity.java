package com.dnion.app.android.injuriesapp;

import android.os.Bundle;
import android.widget.Toast;

import java.io.File;

/**
 * Created by 卫华 on 2018/5/12.
 */

public class PdfViewActivity extends BaseActivity {

    private static final String TAG = "PdfViewActivity";

    private PdfViewActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view);
        mActivity = this;
        String filePath = getIntent().getStringExtra("filePath");
        initPdfView(filePath);
    }

    private void initPdfView(String filePath) {
        com.github.barteksc.pdfviewer.PDFView pdfView = (com.github.barteksc.pdfviewer.PDFView)findViewById(R.id.pdfView);
        pdfView.fromFile(new File(filePath)).load();
        Toast.makeText(mActivity, "双击放大缩小", Toast.LENGTH_LONG).show();
    }
}
