package yyl.jigsawpuzzle.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import yyl.jigsawpuzzle.Util.ScreenUtil;

/**
 * Created by Administrator on 2016/5/25 0025.
 */
public class GridPicListAdapter extends BaseAdapter {
    
    private List<Bitmap> picList;
    private Context mContext;

    public GridPicListAdapter(List<Bitmap> picList, Context mContext) {
        this.picList = picList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    @Override
    public Object getItem(int position) {
        return picList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_pic_item = null;
        int density = (int) ScreenUtil.getDeviceDensity(mContext);
        if (convertView == null) {
            iv_pic_item = new ImageView(mContext);
            //设置布局图片
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(
                    80*density,100*density));
            //设置显示比例类型
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            iv_pic_item = (ImageView) convertView;
        }
        
        iv_pic_item.setBackgroundColor(Color.parseColor("#000000"));
        iv_pic_item.setImageBitmap(picList.get(position));
        return iv_pic_item;
    }
}
