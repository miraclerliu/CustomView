package com.liu.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.liu.custom.R;
import com.liu.custom.utils.DensityUtil;
import com.liu.custom.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Liu on 2019/11/5
 */
public class IndicatorSeekBar extends SeekBar {
    // 画笔
    private TextPaint mPaint;
    // 进度文字位置信息
    private Rect mProgressTextRect = new Rect();
    // 滑块按钮宽度
    private int mThumbWidth;
    //最大金额
    private float mMaxPrice;
    //指示器图片
    private Bitmap mBitmap;
    //需要显示的价格map
    private HashMap<Float, String> mPriceMap = new HashMap<>();
    private OnPriceChangeListener mOnPriceChangeListener;

    public IndicatorSeekBar(Context context) {
        this(context, null);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Utils.getColor(getContext(), R.color.text_color_1e2024_20));
        mPaint.setTextSize(DensityUtil.dip2px(getContext(), 11));
        mThumbWidth = DensityUtil.dip2px(getContext(), 20);
        // 如果不设置padding，当滑动到最左边或最右边时，滑块会显示不全
        setPadding(mThumbWidth / 2, 0, mThumbWidth / 2, 0);
        mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_arrow_down);
        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mOnPriceChangeListener != null) {
                    mOnPriceChangeListener.onPriceChange(progress, calculatePrice(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, 300);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Iterator iterator = mPriceMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry element = (Map.Entry) iterator.next();
            float price = (float) element.getKey();
            int progress = (int) (price * getMax() / mMaxPrice);
            String progressText = (String) element.getValue();
            drawBitmapAndText(canvas, progress, price, progressText);
        }
        drawBitmapAndText(canvas, getProgress(), calculatePrice(getProgress()), getProgress() + "%");
    }

    /**
     * 画指示器和文字
     */
    private void drawBitmapAndText(Canvas canvas, int progress, float price, String progressText) {
        mPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);
        if (progressText.contains("%")) {
            mPaint.setColor(Utils.getColor(getContext(), R.color.layout_bg_fcf749));
        } else {
            mPaint.setColor(getPaintColor(price));
        }

        float progressRatio = (float) progress / getMax();
        float thumbOffset = mThumbWidth / 2 - mThumbWidth * progressRatio;
        float thumbX = getWidth() * progressRatio + thumbOffset - DensityUtil.dip2px(getContext(), 5);

        int bitmapY = getHeight() / 2 - mBitmap.getHeight() - DensityUtil.dip2px(getContext(), 15);
        canvas.drawBitmap(mBitmap, thumbX, bitmapY, mPaint);

        int priceY = getHeight() / 2 - mBitmap.getHeight() - DensityUtil.dip2px(getContext(), 20);
        canvas.drawText(getContext().getString(R.string.dollar) + price, thumbX, priceY, mPaint);

        int textY = priceY - DensityUtil.dip2px(getContext(), 10);
        canvas.drawText(progressText, thumbX, textY, mPaint);
    }

    public void setMaxPrice(float maxPrice) {
        mMaxPrice = maxPrice;
    }

    public void setCustomPrice(float customPrice) {
        int progress = (int) (customPrice * getMax() / mMaxPrice);
        setProgress(progress);
        invalidate();
    }

    public void setPriceMap(HashMap<Float, String> priceMap) {
        mPriceMap = priceMap;
    }

    /**
     * 通过进度计算价格
     */
    private float calculatePrice(int progress) {
        float ratio = (float) progress / getMax();
        return mMaxPrice * Utils.parseFloat(Utils.getFormatterFloat(ratio, 2));
    }

    /**
     * 通过计算绝对值取得画笔颜色
     */
    private int getPaintColor(float targetPrice) {
        int textColor = Utils.getColor(getContext(), R.color.text_color_1e2024_20);
        if (mPriceMap.size() == 0) {
            return textColor;
        }
        HashMap<Float, Integer> colorMap = new HashMap<>();
        Iterator iterator = mPriceMap.entrySet().iterator();
        float priceAbsArr[] = new float[mPriceMap.size()];
        float priceArr[] = new float[mPriceMap.size()];
        float currentPrice = calculatePrice(getProgress());
        int mapIndex = 0;
        while (iterator.hasNext()) {
            Map.Entry element = (Map.Entry) iterator.next();
            float price = (float) element.getKey();
            colorMap.put(price, textColor);
            priceAbsArr[mapIndex] = Math.abs(price - currentPrice);
            priceArr[mapIndex] = price;
            mapIndex++;
        }
        int index[] = arraySort(priceAbsArr);
        if (priceAbsArr.length >= 3) {
            colorMap.put(priceArr[index[0]], Utils.getColor(getContext(), R.color.text_color_1e2024_20));
            colorMap.put(priceArr[index[1]], Utils.getColor(getContext(), R.color.text_color_1e2024_60));
            colorMap.put(priceArr[index[2]], Utils.getColor(getContext(), R.color.text_color_1e2024));
        }
        return colorMap.get(targetPrice);
    }

    /**
     * 数组排序
     */
    private int[] arraySort(float[] arr) {
        float temp;
        int index;
        int k = arr.length;
        int[] Index = new int[k];
        for (int i = 0; i < k; i++) {
            Index[i] = i;
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] < arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    index = Index[j];
                    Index[j] = Index[j + 1];
                    Index[j + 1] = index;
                }
            }
        }
        return Index;
    }

    public void setOnPriceChangeListener(OnPriceChangeListener mOnPriceChangeListener) {
        this.mOnPriceChangeListener = mOnPriceChangeListener;
    }

    public interface OnPriceChangeListener {
        void onPriceChange(int progress, float price);
    }

}
