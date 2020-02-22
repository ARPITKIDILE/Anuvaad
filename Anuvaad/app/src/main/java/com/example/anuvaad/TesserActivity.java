package com.example.anuvaad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TesserActivity extends AppCompatActivity {
    WebView wv;

    static final int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    ValueCallback<Uri> mUploadMessage;
    ValueCallback<Uri[]> uploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesser);

        String url = getIntent().getStringExtra("address");

        wv = findViewById(R.id.web_view);
        wv.getSettings().setAllowFileAccess(true);

        wv.loadUrl(url);

        startWebView();
    }

    private void startWebView(){
        wv.setWebViewClient(new WebViewClient(){
            ProgressDialog progressDialog;

            public boolean shouldOverrideUrlLoading(WebView wv, String url){
                return false;
            }

            public void onLoadResource(WebView view,String url){
                if(progressDialog == null){
                    progressDialog = new ProgressDialog(TesserActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }

            public  void onPageFinished(WebView view,String url){
                try{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        wv.setWebChromeClient(new WebChromeClient(){
            public boolean onShowFileChooser(WebView view,ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams){
                if(uploadMessage != null){
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try{
                    startActivityForResult(intent,REQUEST_SELECT_FILE);
                }
                catch (ActivityNotFoundException e){
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext(),"Cannot Open File Chooser",Toast.LENGTH_LONG).show();
                    return false;
                }

                return true;
            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent intent){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(requestCode == REQUEST_SELECT_FILE){
                if(uploadMessage == null){
                    Toast.makeText(this,"No Image Selected!",Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,intent));
                uploadMessage = null;
            }
        }
        else if(requestCode == FILECHOOSER_RESULTCODE){
            if(null == mUploadMessage){
                Toast.makeText(this,"No Image Selected!",Toast.LENGTH_SHORT).show();
                return;
            }
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed To upload Image!",Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed(){
        if(wv.canGoBack()){
            wv.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}
