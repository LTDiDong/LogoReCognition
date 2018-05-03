package tunglee.logopro;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnThem;
    Button btnVideo;
    ListView lvAnh;
    ArrayList<Anh> arrayAnh;
    AnhAdapter adapter;
    public  static Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnThem = (Button) findViewById(R.id.buttonThem);
        btnVideo = (Button) findViewById(R.id.buttonVideo);

        lvAnh = (ListView) findViewById(R.id.listviewAnh);
        arrayAnh = new ArrayList<>();

        adapter = new AnhAdapter(this,R.layout.dong_anh,arrayAnh);
        lvAnh.setAdapter(adapter);

        database = new Database(this,"QuanLy",null,1);

        database.QueryData("CREATE TABLE IF NOT EXISTS Anh (Id INTEGER PRIMARY KEY AUTOINCREMENT ,HinhAnh BLOB)");

        //get data
        Cursor cursor = database.GetData("SELECT * FROM Anh");
        while(cursor.moveToNext()){
            arrayAnh.add(new Anh(

                cursor.getInt(0) ,
                cursor.getBlob(1)

            ));
        }

        adapter.notifyDataSetChanged();

        btnThem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                startActivity(new Intent(MainActivity.this,ThemAnhActivity.class));
            }

        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ThemVideoActivity.class));

            }
        });


    }

}
