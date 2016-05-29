package yyl.jigsawpuzzle.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import yyl.jigsawpuzzle.R;
import yyl.jigsawpuzzle.Util.GameUtil;
import yyl.jigsawpuzzle.Util.ImagesUtil;
import yyl.jigsawpuzzle.Util.ScreenUtil;
import yyl.jigsawpuzzle.adpter.GridItemsAdapter;
import yyl.jigsawpuzzle.bean.PicItem;

/**
 * Created by Administrator on 2016/5/28 0028.
 */
public class PuzzleMain extends Activity implements View.OnClickListener {

    //显示步数
    private TextView mPuzzleStepCounts;
    //步数显示
    public static int COUNT_INDEX = 0;
    //显示时间
    private TextView mPuzzleTime;
    //时间显示
    public static int TIMER_INDEX = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //更新计时器
                    TIMER_INDEX++;
                    mPuzzleTime.setText("" + TIMER_INDEX);
                    break;
                default:
                    break;
            }
        }
    };

    // 切图后的图片
    private List<Bitmap> mBitmapItemLists = new ArrayList<Bitmap>();
    // GridView适配器
    private GridItemsAdapter mAdapter;

    //Button
    private Button mBtnImage;
    private Button mBtnReset;
    private Button mBtnBack;

    //flag是否已经显示原图
    private boolean mIsShowImg;

    //选择的图片
    private Bitmap mPicSelected;

    // PuzzlePanel
    private GridView mGvPuzzleMainDetail;
    private int mResId;
    private String mPicPath;
    private ImageView mImageView;

    //拼图完成时显示的最后一个图片
    public static Bitmap mLastBitmap;
    // 设置为N*N显示
    public static int TYPE = 2;

    //计时器类
    private Timer mTimer;
    /**
     * 计时器线程
     */
    private TimerTask mTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_main);
        //获取选择的图片
        Bitmap picSelectedTemp;
        mResId = getIntent().getExtras().getInt("picSelectedID");
        mPicPath = getIntent().getExtras().getString(MainActivity.MY_PICPATH);
        if (mResId != 0) {
            picSelectedTemp = BitmapFactory.decodeResource(getResources(), mResId);
        } else {
            picSelectedTemp = BitmapFactory.decodeFile(mPicPath);
        }

        TYPE = getIntent().getExtras().getInt("mType", 2);
        //对图片进行处理
        handlerImage(picSelectedTemp);
        //初始化Views
        initViews();
        //生成游戏数据
        generateGame();
        //GridView点击事件
        mGvPuzzleMainDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断是否可移动
                if (GameUtil.isMoveable(position)) {
                    //交换空格与点击Item的位置
                    GameUtil.swapItems(GameUtil.mItemBeans.get(position), GameUtil.mBlankPicItem);
                    // 重新获取图片
                    recreateData();
                    //通知GridView更改UI
                    mAdapter.notifyDataSetChanged();
                    //更新步数
                    COUNT_INDEX++;
                    mPuzzleStepCounts.setText("" + COUNT_INDEX);
                    //判断是否拼图成功
                    if (GameUtil.isSuccess()) {
                        //将最后一张图片显示完整
                        recreateData();
                        mBitmapItemLists.remove(TYPE * TYPE - 1);
                        mBitmapItemLists.add(mLastBitmap);
                        //通知GridView更改UI
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(PuzzleMain.this,"拼图成功！！！",Toast.LENGTH_SHORT).show();
                        //设置成功后，GridView不可点击
                        mGvPuzzleMainDetail.setEnabled(false);
                        //计时器停止计时
                        mTimer.cancel();
                        mTimerTask.cancel();
                    }
                }
            }
        });

        //返回按钮点击事件
        mBtnBack.setOnClickListener(this);
        //原图按钮点击事件
        mBtnImage.setOnClickListener(this);
        //重置按钮点击事件
        mBtnReset.setOnClickListener(this);
    }

    /**
     * 重新获取图片
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (PicItem temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getmBitmap());
        }
    }

    /**
     * 生成游戏数据
     */
    private void generateGame() {
        // 切图 获取初始拼图数据拼图 正常顺序
        new ImagesUtil().createInitBitmaps(TYPE, mPicSelected, PuzzleMain.this);

        //生成随机数据
        GameUtil.getPuzzleGenerator();
        //获取Bitmap集合
        for (PicItem temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getmBitmap());
        }
        //数据适配器
        mAdapter = new GridItemsAdapter(this, mBitmapItemLists);
        mGvPuzzleMainDetail.setAdapter(mAdapter);

        //启动计时器
        mTimer = new Timer(true);
        //计时器线程
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
        //没1000ms执行，延迟0s
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        //Button
        mBtnImage = (Button) findViewById(R.id.btn_puzzle_main_img);
        mBtnReset = (Button) findViewById(R.id.btn_puzzle_main_reset);
        mBtnBack = (Button) findViewById(R.id.btn_puzzle_main_back);
        //flag是否已显示原图
        mIsShowImg = false;
        //GridView
        mGvPuzzleMainDetail = (GridView) findViewById(R.id.gv_puzzle_main_detail);
        //设置为N*N显示
        mGvPuzzleMainDetail.setNumColumns(TYPE);
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(mPicSelected.getWidth(), mPicSelected.getHeight());
        //水平居中
        gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //其它格式属性
        gridParams.addRule(RelativeLayout.BELOW, R.id.ll_puzzle_main_spinner);
        //Grid显示
        mGvPuzzleMainDetail.setLayoutParams(gridParams);
        mGvPuzzleMainDetail.setHorizontalSpacing(0);
        mGvPuzzleMainDetail.setVerticalSpacing(0);
        //TV步数
        mPuzzleStepCounts = (TextView) findViewById(R.id.tv_puzzle_main_counts);
        mPuzzleStepCounts.setText("" + COUNT_INDEX);
        //TV计时器
        mPuzzleTime = (TextView) findViewById(R.id.tv_puzzle_main_time);
        mPuzzleTime.setText("0秒");

        //添加显示原图的View
        addImgView();

    }

    /**
     * 添加显示原图的View
     */
    private void addImgView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_puzzle_main_main_layout);
        mImageView = new ImageView(PuzzleMain.this);
        mImageView.setImageBitmap(mPicSelected);
        int x = (int) (mPicSelected.getWidth() * 0.9f);
        int y = (int) (mPicSelected.getHeight() * 0.9f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(x, y);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    /**
     * 对图片进行处理 自适应大小
     *
     * @param bitmap
     */
    private void handlerImage(Bitmap bitmap) {
        //将图片缩小到固定尺寸
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeight = ScreenUtil.getScreenSize(this).heightPixels;
        mPicSelected = new ImagesUtil().resizeBitmap(screenWidth * 0.8f, screenHeight * 0.7f, bitmap);
    }

    /**
     * Button点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回按钮点击事件
            case R.id.btn_puzzle_main_back:
                PuzzleMain.this.finish();
                break;
            //原图按钮点击事件
            case R.id.btn_puzzle_main_img:
                Animation animShow = AnimationUtils.loadAnimation(
                        PuzzleMain.this,R.anim.image_show_anim);
                Animation animHide = AnimationUtils.loadAnimation(
                        PuzzleMain.this,R.anim.image_hide_anim);
                if (mIsShowImg) {
                    mImageView.startAnimation(animHide);
                    mImageView.setVisibility(View.GONE);
                    mIsShowImg = false;
                } else {
                    mImageView.startAnimation(animShow);
                    mImageView.setVisibility(View.VISIBLE);
                    mIsShowImg = true;
                }
                break;
            //重置按钮点击事件
            case R.id.btn_puzzle_main_reset:
                cleanConfig();
                generateGame();
                recreateData();
                //通知GridView更改UI
                mPuzzleStepCounts.setText("" + COUNT_INDEX);
                mAdapter.notifyDataSetChanged();
                mGvPuzzleMainDetail.setEnabled(true);
                break;
            default:
                break;
        }
    }

    /**
     * 清空相关参数
     */
    private void cleanConfig() {
        //清空相关参数设置
        GameUtil.mItemBeans.clear();
        //停止计时器
        mTimer.cancel();
        mTimerTask.cancel();
        COUNT_INDEX = 0;
        TIMER_INDEX = 0;

        //清除拍摄的照片
        if (mPicPath != null) {
            //删除照片
            File file = new File(MainActivity.TEMP_IMAGE_PATH);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 返回时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空相关数据
        cleanConfig();
        this.finish();
    }
}
