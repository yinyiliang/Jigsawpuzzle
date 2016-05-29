package yyl.jigsawpuzzle.Util;

import java.util.ArrayList;
import java.util.List;

import yyl.jigsawpuzzle.activity.PuzzleMain;
import yyl.jigsawpuzzle.bean.PicItem;

/**
 * Created by Administrator on 2016/5/28 0028.
 */
public class GameUtil {

    //游戏信息单元格
    public static List<PicItem> mItemBeans = new ArrayList<>();
    //空单元格
    public static PicItem mBlankPicItem = new PicItem();

    /**
     * 计算倒置和算法
     *
     * @param data 拼图数组数据
     * @return 改序列的倒置和
     */
    public static int getInversion(List<Integer> data) {
        //序列倒置和
        int inversions = 0;
        //单个数字的 倒置变量值 统计
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }

    /**
     * 生成随机的Item
     */
    public static void getPuzzleGenerator() {
        int index = 0;
        //随机打乱顺序
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() * PuzzleMain.TYPE * PuzzleMain.TYPE);
            swapItems(mItemBeans.get(index), GameUtil.mBlankPicItem);
        }
        List<Integer> data = new ArrayList<>();

        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getmBitmapId());
        }

        //判断生成的数字矩阵是否有解
        if (canSolve(data)) {
            return;
        } else {
            getPuzzleGenerator();
        }

    }

    /**
     * 该数据是否可解
     *
     * @param data
     * @return
     */
    public static boolean canSolve(List<Integer> data) {
        //获取空格ID
        int blankId = GameUtil.mBlankPicItem.getmItemId();
        //可行性原则
        if (data.size() % 2 == 1) {
            //如果序列的宽度为奇数
            return getInversion(data) % 2 == 0;
        } else {
            //如果序列的宽度为偶数
            if (((blankId - 1) / PuzzleMain.TYPE) % 2 == 1) {
                //从下往上数，空格位于奇数行
                return getInversion(data) % 2 == 0;
            } else {
                //从下往上数，空格位于偶数行
                return getInversion(data) % 2 == 1;
            }
        }
    }

    /**
     * 判断点击的Item是否可移动
     *
     * @param position
     * @return
     */
    public static boolean isMoveable(int position) {
        int type = PuzzleMain.TYPE;
        //获取空格Item
        int blankId = GameUtil.mBlankPicItem.getmItemId() - 1;
        //不同行相差type个id数，可以移动（上下位置可以移动）
        if (Math.abs(blankId - position) == type) {
            return true;
        }
        //同一行相差1个id数，可以移动（左右位置可以移动）
        if ((blankId / type == position / type) && Math.abs(blankId - position) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 交换空格与点击Item的位置
     *
     * @param from  交换图
     * @param blank 空白图
     */
    public static void swapItems(PicItem from, PicItem blank) {
        PicItem tempPicItem = new PicItem();
        //交换BitmapId
        tempPicItem.setmBitmapId(from.getmBitmapId());
        from.setmBitmapId(blank.getmBitmapId());
        blank.setmBitmapId(tempPicItem.getmBitmapId());
        //交换Bitmap
        tempPicItem.setmBitmap(from.getmBitmap());
        from.setmBitmap(blank.getmBitmap());
        blank.setmBitmap(tempPicItem.getmBitmap());
        //设置新的Blank
        GameUtil.mBlankPicItem = from;
    }

    /**
     * 判断拼图是否完成
     *
     * @return 是否拼图成功
     */
    public static boolean isSuccess() {

        for (PicItem picItem:GameUtil.mItemBeans) {
            if (picItem.getmBitmapId() != 0 &&
                    (picItem.getmItemId()) == picItem.getmBitmapId()) {
                continue;
            } else if (picItem.getmBitmapId() == 0 &&
                    picItem.getmItemId() == PuzzleMain.TYPE*PuzzleMain.TYPE) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

}
