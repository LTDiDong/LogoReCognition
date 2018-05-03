package tunglee.logopro;

import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.VideoView;
import android.widget.MediaController;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ThemVideoActivity extends AppCompatActivity {
    ImageButton ibtnVideo,ibtnFolderV;
    VideoView vview;
    Button btnThemV,btnHuyV;
    MediaController media;

    int REQUEST_CODE_VIDEO = 123;
    int REQUEST_CODE_FOLDERV = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_video);

        Anhxa2();

        //Nút quay video
        ibtnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_VIDEO);
            }
        });
        //Nút hủy video
        btnHuyV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThemVideoActivity.this,MainActivity.class));
            }
        });
        //Nút thư viện video
        ibtnFolderV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDERV);
            }
        });


        btnThemV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri introURI = Uri.parse("android.resource://your.app.package/" + R.id.videoView);
                vview.setVideoURI(introURI);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();



            }
        });


    }
       //Nơi hiện video
        @Override
        protected void onActivityResult (int requestCode, int resultCode, Intent data){
         if(requestCode == REQUEST_CODE_VIDEO && resultCode == RESULT_OK && data != null){
             Uri uriVideo = data.getData();
             this.vview.setVideoURI(uriVideo);
             this.vview.start();


        }
         if(requestCode == REQUEST_CODE_FOLDERV && resultCode == RESULT_OK && data != null){
                Uri fvideo = data.getData();
                 this.vview.setVideoURI(fvideo);
                 this.vview.setMediaController(media);
                 media.setAnchorView(vview);
                 this.vview.start();
            }

    }
    private  void Anhxa2(){
        btnThemV = (Button) findViewById(R.id.buttonthemvideo);
        btnHuyV = (Button) findViewById(R.id.buttonhuyvideo);
        ibtnVideo = (ImageButton) findViewById(R.id.buttonVideo);
        ibtnFolderV = (ImageButton) findViewById(R.id.buttonFolderVideo);
        vview = (VideoView) findViewById(R.id.videoView);
        media = new MediaController(this);
    }
}
