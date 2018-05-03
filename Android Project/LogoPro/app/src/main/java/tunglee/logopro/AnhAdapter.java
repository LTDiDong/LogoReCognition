package tunglee.logopro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;

/**
 * Created by HATRANG on 5/31/2017.
 */

public class AnhAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Anh> anhList;

    public AnhAdapter(Context context, int layout, List<Anh> anhList) {
        this.context = context;
        this.layout = layout;
        this.anhList = anhList;
    }


    @Override
    public int getCount() {
        return anhList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        ImageView imgHinh;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
          holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            holder.imgHinh = (ImageView) view.findViewById(R.id.imageHinhCustom);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();

        }
        Anh anh = anhList.get(i);

        //Chuyen byte[] -> bitmap
        byte[] hinhAnh = anh.getHinh();
        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh,0,hinhAnh.length);
        holder.imgHinh.setImageBitmap(bitmap);

        return view;

    }
}
