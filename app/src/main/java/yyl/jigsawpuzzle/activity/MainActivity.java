package yyl.jigsawpuzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yyl.jigsawpuzzle.R;
import yyl.jigsawpuzzle.Util.ScreenUtil;
import yyl.jigsawpuzzle.adpter.GridPicListAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //显示选择难度等级
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private LayoutInflater mInflater;
    private TextView degreeSelect;
    private TextView mType1;
    private TextView mType2;
    private TextView mType3;
    private int mType = 2;

    public static final String MY_PICPATH = "mPicPath";

    //主页图片资源
    private int[] mResPicId;

    //本地相册图片选择
    private String[] mCustomItem = {"本地相册", "相机拍照"};
    //返回码：本地图库
    private static final int RESULT_IMAGE = 100;
    //IMAGE TYPE
    private static final String IMAGE_TYPE = "image/*";

    //Temp照片路径
    public static String TEMP_IMAGE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/temp.png";
    //返回码：相机
    private static final int RESULT_CAMERA = 200;


    //GridView 图片显示
    private GridView mGvPicList;
    private List<Bitmap> mPicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化Views
        initViews();
        //数据适配器
        mGvPicList.setAdapter(new GridPicListAdapter(mPicList, MainActivity.this));
        //Item点击监听
        mGvPicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == mResPicId.length - 1) {
                    //如果是最后一个图片，则选择打开本地图库、相机
                    showDialogCustom();
                } else {
                    //否则默认选择图片
                    Intent intent = new Intent(MainActivity.this, PuzzleMain.class);
                    intent.putExtra("picSelectedID", mResPicId[position]);
                    intent.putExtra("mType", mType);
                    startActivity(intent);
                }
            }
        });

        /**
         * 显示难度Type
         */
        degreeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出popup window
                popupShow(v);
            }
        });
    }

    //显示选择系统图库和相机对话框
    private void showDialogCustom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择：");
        builder.setItems(mCustomItem,   new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == which) {
                    //本地相册
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
                    startActivityForResult(intent, RESULT_IMAGE);
                } else if (1 == which) {
                    //系统相机
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, RESULT_CAMERA);
                }
            }
        });
        builder.create().show();
    }

    /**
     * 调用图库相机回调方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE && data != null) {
                //相册
                Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                    Intent intent = new Intent(MainActivity.this, PuzzleMain.class);
                    intent.putExtra(MY_PICPATH, imagePath);
                    intent.putExtra("mType", mType);
                    cursor.close();
                    startActivity(intent);
                }
            } else if (requestCode == RESULT_CAMERA) {
                //相机
                Intent intent = new Intent(MainActivity.this, PuzzleMain.class);
                intent.putExtra(MY_PICPATH, TEMP_IMAGE_PATH);
                intent.putExtra("mType", mType);
                startActivity(intent);
            }
        }
    }

    /**
     * 初始化view
     */
    private void initViews() {

        mGvPicList = (GridView) findViewById(R.id.gv_puzzle_main_pic_list);
        //初始化Bitmap数据
        mResPicId = new int[]{
                R.drawable.pic1, R.drawable.pic2,
                R.drawable.pic3, R.drawable.pic4,
                R.drawable.pic5, R.drawable.pic6,
                R.drawable.pic7, R.drawable.pic8,
                R.drawable.pic9, R.drawable.pic10,
                R.drawable.pic11, R.drawable.pic12,
                R.drawable.pic13, R.drawable.pic14,
                R.drawable.pic15, R.mipmap.ic_launcher};
        Bitmap[] bitmap = new Bitmap[mResPicId.length];
        for (int i = 0; i < bitmap.length; i++) {
            bitmap[i] = BitmapFactory.decodeResource(getResources(), mResPicId[i]);
            mPicList.add(bitmap[i]);
        }

        //显示难度等级
        degreeSelect = (TextView) findViewById(R.id.tv_puzzle_main_type_selected);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopupView = mInflater.inflate(R.layout.puzzle_main_selected, null);

        mType1 = (TextView) mPopupView.findViewById(R.id.id_difficulty_type1);
        mType2 = (TextView) mPopupView.findViewById(R.id.id_difficulty_type2);
        mType3 = (TextView) mPopupView.findViewById(R.id.id_difficulty_type3);
        //监听难度等级事件
        mType1.setOnClickListener(this);
        mType2.setOnClickListener(this);
        mType3.setOnClickListener(this);
    }


    /**
     * 显示 popup window
     *
     * @param view popup window
     */
    private void popupShow(View view) {
        int density = (int) ScreenUtil.getDeviceDensity(this);
        //显示popup Window
        mPopupWindow = new PopupWindow(mPopupView, 200 * density, 50 * density);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        //设置透明背景
        Drawable backdrop = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(backdrop);
        //获取位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - 40 * density, location[1] + 30 * density);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_difficulty_type1:
                mType = 2;
                degreeSelect.setText("2x2");
                break;
            case R.id.id_difficulty_type2:
                mType = 3;
                degreeSelect.setText("3x3");
                break;
            case R.id.id_difficulty_type3:
                mType = 4;
                degreeSelect.setText("4x4");
                break;
            default:
                break;
        }
        mPopupWindow.dismiss();
    }
}
