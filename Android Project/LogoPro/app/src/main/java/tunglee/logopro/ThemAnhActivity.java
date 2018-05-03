package tunglee.logopro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.jar.Manifest;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ThemAnhActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnAdd, btnHuy,btnXL;
    ImageButton ibtnCamera, ibtnFolder;
    public static ImageView IvIamge;
    ViewFlipper viewFlipper;
    Button next;
    Button prev;

    final int REQUEST_CODE_CAMERA = 123;
    final int REQUEST_CODE_FOLDER = 456;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_anh);

        Anhxa();
        PhotoViewAttacher photoView = new PhotoViewAttacher(IvIamge);
        photoView.update();


        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        btnHuy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(ThemAnhActivity.this,MainActivity.class));
            }

        });

        btnXL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThemAnhActivity.this,XuLi.class));

            }
        });



        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //chuyen data imageview -> byte[]

                BitmapDrawable bitmapDrawable = (BitmapDrawable) IvIamge.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
                byte[] hinhAnh = byteArray.toByteArray();

                MainActivity.database.INSERT_ANH(
                        hinhAnh
                );
                Toast.makeText(ThemAnhActivity.this,"Da Them",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ThemAnhActivity.this,MainActivity.class));
            }
        });
        ibtnCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public   void onClick(View view){
              //  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               // startActivityForResult(intent,REQUEST_CODE_CAMERA);
                ActivityCompat.requestPermissions(ThemAnhActivity.this,
                        new String[]{android.Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
                // ActivityCompat.requestPermissions(
                //        ThemAnhActivity.this,
                //      new String[]{android.Manifest.permission.CAMERA},
                //      REQUEST_CODE_CAMERA
                // );
            }
        });
        ibtnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageDownload = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageDownload.putExtra("crop", "true");
                imageDownload.putExtra("aspectX", 1);
                imageDownload.putExtra("aspectY", 1);
                imageDownload.putExtra("outputX", 200);
                imageDownload.putExtra("outputY", 200);
                imageDownload.putExtra("return-data", true);
                startActivityForResult(imageDownload, 2);




            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
        else {
            Toast.makeText(this,"Bạn không cấp phép mở camera!", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            IvIamge.setImageBitmap(bitmap);
        }
        if(requestCode == 2 && resultCode == RESULT_OK && data !=null){
            Bundle extras = data.getExtras();
            Bitmap image = extras.getParcelable("data");
            IvIamge.setImageBitmap(image);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Anhxa() {
        btnAdd = (Button) findViewById(R.id.buttonAdd);
        btnHuy = (Button) findViewById(R.id.buttonHuy);
        ibtnCamera = (ImageButton) findViewById(R.id.buttonCamera);
        IvIamge = (ImageView) findViewById(R.id.imageView);
        ibtnFolder = (ImageButton) findViewById(R.id.buttonFolder);
        btnXL = (Button) findViewById(R.id.xl);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        next = (Button) findViewById(R.id.nextbtn);
        prev = (Button) findViewById(R.id.prevbtn);

    }

    @Override
    public void onClick(View v) {
        if(v == next)
        {
            viewFlipper.showNext();

        }
        else if ( v == prev)
        {
            viewFlipper.showPrevious();
        }

    }
}