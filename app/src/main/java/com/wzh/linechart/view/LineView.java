package com.wzh.linechart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.wzh.linechart.R;


/**
 * Created by zhihao.wen on 2016/11/18.
 */
public class LineView extends View {
    private int width = 200, height = 200;
    /**
     * 绘制坐标轴的画笔
     */
    private Paint axesPaint;
    /**
     * 绘制图的时候画笔
     */
    private Paint linePaint;

    private Path path;
    private Path p;

    private final int marginBorder = 80;
    private int[] data ;
    private String[] lables;
    /**
     * 存放横坐标的值
     */
    private float x[];
    /**
     * 存放纵坐标的值
     */
    private float y[];
    private int yMaxNum = 0;
    private float h1, h2;
    private int valueMax, valueMin;

    private DisplayMetrics dm;
    private Shader mShader;
    /**
     * shape的是底部渐变效果所用的
     */
    private Paint shapePaint;
    private Path shapePath;
    /**
     * 文字的颜色
     */
    private int textColor;
    /**
     * 折线的颜色
     */
    private int lineColor;
    /**
     * 折线的粗细
     */
    private float lineWidth;
    /**
     * 折线点的半径大小
     */
    private float dotRadius;
    /**
     * 数据的放大因子，比如数据是2位数的就是10,2位数的就是100。根据数据来定
     */
    private int dataFactor = 100;
    /**
     * 是否显示纵向的垂直线
     */
    private boolean isShowColumnLine;
    /**
     * 是否显示横向的垂直线
     */
    private boolean isShowRowLine;
    /**
     * 是否显示下面的渐变颜色
     */
    private boolean isLinearGradient;
    /**
     * 垂直的虚线的颜色
     */
    private int dashColor;
    private String yTitle = "y坐标" ;
    private String xTitle ="x坐标";

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LineView);
        textColor = ta.getColor(R.styleable.LineView_textColor, ContextCompat.getColor(getContext(), R.color.black));
        lineColor = ta.getColor(R.styleable.LineView_lineColor, ContextCompat.getColor(getContext(), R.color.colorAccent));
        lineWidth = ta.getDimension(R.styleable.LineView_lineWidth, 3);
        dotRadius = ta.getDimension(R.styleable.LineView_dotRadius, 10);
        dataFactor = ta.getInteger(R.styleable.LineView_dataFactor, 100);
        isShowColumnLine = ta.getBoolean(R.styleable.LineView_isShowColumnLine, false);
        isShowRowLine = ta.getBoolean(R.styleable.LineView_isShowRowLine, false);
        isLinearGradient = ta.getBoolean(R.styleable.LineView_isLinearGradient, true);
        dashColor = ta.getColor(R.styleable.LineView_dashColor, ContextCompat.getColor(getContext(), R.color.colorPrimary));

        ta.recycle();
        init();
    }

    private void init() {
        dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);

        axesPaint = new Paint();
        axesPaint.setColor(textColor);
        axesPaint.setAntiAlias(true);
        axesPaint.setTextSize(getResources().getDimension(R.dimen.font_smalll));

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        path = new Path();
        p = new Path();
        shapePath = new Path();
        mShader = new LinearGradient(10, 0, 0, dm.heightPixels / 2,
                new int[]{Color.parseColor("#00ffffff"), Color.parseColor("#BF00e5ff")}, null, Shader.TileMode.MIRROR);
        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        if (modeWidth == MeasureSpec.EXACTLY) {
            width = sizeWidth;
        }
        if (modeHeight == MeasureSpec.EXACTLY) {
            height = Math.min(sizeHeight, sizeWidth);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data==null || lables==null){
            return;
        }
        if (data.length==0 || lables.length==0){
            return;
        }
        yMaxNum = Math.round(getMax() / dataFactor) + 1;
        x = new float[lables.length];
        y = new float[data.length];

        drawYAxes(canvas);
        drawXAxes(canvas);
        drawLine(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        postInvalidate();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    /**
     * 绘制折现
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        for (int i = 0; i < y.length; i++) {
            /**
             * 重置画笔，绘制折线
             */
            linePaint.setColor(lineColor);
            linePaint.setStyle(Paint.Style.FILL);//设置空
            linePaint.setShader(null);
            linePaint.setPathEffect(null);
            canvas.drawCircle(x[i], y[i], dotRadius, linePaint);
            /**
             * 绘制点上的值，如果是V字型的点就在底下显示，默认在下面
             */
            if (i < y.length - 1 && i > 1) {
                if (y[i] > y[i - 1] && y[i] > y[i + 1]) {
                    canvas.drawText(data[i] + "", x[i] - marginBorder / 3, y[i] + (marginBorder / 3) * 2, axesPaint);
                } else {
                    canvas.drawText(data[i] + "", x[i] - marginBorder / 4, y[i] - (marginBorder / 3), axesPaint);
                }
            } else {
                canvas.drawText(data[i] + "", x[i] - marginBorder / 3, y[i] + (marginBorder / 3) * 2, axesPaint);
            }
            /**
             * 绘制折线
             */
            if (i == 0) {
                shapePath.moveTo(x[i], y[i]);
                path.moveTo(x[i], y[i]);
            } else {
                shapePath.lineTo(x[i], y[i]);
                path.lineTo(x[i], y[i]);
            }
            linePaint.setStrokeWidth(lineWidth);
            linePaint.setStyle(Paint.Style.STROKE);//设置空
            canvas.drawPath(path, linePaint);

            /**
             * 绘制竖直方向的虚线
             */
            linePaint.setStrokeWidth(dip2px(1));
            if (isShowColumnLine) {
                DashPathEffect pathEffect = new DashPathEffect(new float[]{15, 15, 15, 15}, 1);
                linePaint.setPathEffect(pathEffect);
                linePaint.setColor(dashColor);
                p.reset();
                p.moveTo(x[i], y[i]);
                p.lineTo((width / (lables.length + 1)) * (i + 1) + getPaddingLeft(), height - marginBorder - 20);
                canvas.drawPath(p, linePaint);
            }
            if (isShowRowLine) {
                /**
                 * 横向的虚线
                 */
                DashPathEffect pathEffect = new DashPathEffect(new float[]{15, 15, 15, 15}, 1);
                linePaint.setPathEffect(pathEffect);
                linePaint.setColor(dashColor);
                p.reset();
                p.moveTo(x[i], y[i]);
                p.lineTo(getPaddingLeft() + marginBorder + 20, y[i]);
                canvas.drawPath(p, linePaint);
            }
        }
        /**
         * 绘制下部分渐变颜色
         */
        shapePath.lineTo((width / (lables.length + 1)) * (y.length) + getPaddingLeft(), height - marginBorder);
        shapePath.lineTo((width / (lables.length + 1)) + getPaddingLeft(), height - marginBorder);
        shapePath.close(); // 使这些点构成封闭的多边形
        if (isLinearGradient){
            shapePaint.setShader(mShader);
            canvas.drawPath(shapePath, shapePaint);
        }
    }

    /**
     * 绘制y相关坐标
     *
     * @param canvas
     */
    private void drawYAxes(Canvas canvas) {
        /**
         * 绘制Y纵坐标
         */
        canvas.drawLine(getPaddingLeft() + marginBorder, height - marginBorder, getPaddingLeft() + marginBorder, marginBorder, axesPaint);
        for (int i = 0; i < yMaxNum; i++) {
            if (i == 0) {
                valueMax = (yMaxNum - i) * dataFactor;
                h2 = ((height - marginBorder) / (yMaxNum + 1)) * (i + 1);
            } else {
                canvas.drawLine(getPaddingLeft() + marginBorder, ((height - marginBorder) / (yMaxNum + 1)) * (i + 1), getPaddingLeft() + marginBorder + 20, ((height - marginBorder) / (yMaxNum + 1)) * (i + 1), axesPaint);
                canvas.drawText((yMaxNum - i) * dataFactor + "", getPaddingLeft() / 2, ((height - marginBorder * 4 / 5) / (yMaxNum + 1)) * (i + 1), axesPaint);
            }
        }
        for (int i = 0; i < data.length; i++) {
            y[i] = height - marginBorder - ((data[i] * 1.0f / (valueMax * 1.0f))) * (height - marginBorder - h2);
        }

        /**
         * 绘制小箭头
         */
        canvas.drawLine(getPaddingLeft() + marginBorder - 20, marginBorder + 20, getPaddingLeft() + marginBorder, marginBorder, axesPaint);
        canvas.drawLine(getPaddingLeft() + marginBorder + 20, marginBorder + 20, getPaddingLeft() + marginBorder, marginBorder, axesPaint);
        /**
         * 绘制数据单位
         */
        axesPaint.setTextSize(getResources().getDimension(R.dimen.font_smalll));

        canvas.drawText(yTitle, getPaddingLeft() / 2, marginBorder / 2, axesPaint);
    }

    /**
     * 绘制x相关坐标
     *
     * @param canvas
     */
    private void drawXAxes(Canvas canvas) {
        /**
         * 绘制X横坐标
         */
        canvas.drawLine(getPaddingLeft() + marginBorder, height - marginBorder, width - getPaddingRight() / 2, height - marginBorder, axesPaint);

        for (int i = 0; i < lables.length; i++) {
            canvas.drawLine((width / (lables.length + 1)) * (i + 1) + getPaddingLeft(), height - marginBorder, (width / (lables.length + 1)) * (i + 1) + getPaddingLeft(), height - marginBorder - 20, axesPaint);
            canvas.drawText(lables[i], (width / (lables.length + 1)) * (i + 1) - marginBorder * 2 / 3 + getPaddingLeft(), height - marginBorder / 3, axesPaint);
            x[i] = (width / (lables.length + 1)) * (i + 1) + getPaddingLeft();
        }
        canvas.drawText(xTitle, getPaddingLeft() / 2, height - marginBorder / 3, axesPaint);
        /**
         * 绘制小箭头
         */
        canvas.drawLine(width - getPaddingRight() / 2 - 20, height - marginBorder - 20, width - getPaddingRight() / 2, height - marginBorder, axesPaint);
        canvas.drawLine(width - getPaddingRight() / 2 - 20, height - marginBorder + 20, width - getPaddingRight() / 2, height - marginBorder, axesPaint);
    }

    /**
     * 求最大值
     *
     * @return
     */
    public int getMax() {
        int max = data[0];
        for (int i = 1; i < data.length; i++) {
            if (max < data[i]) {
                max = data[i];
            }
        }
        return max;
    }

    public void setLables(String[] lables) {
        this.lables = lables;
        postInvalidate();
    }

    public void setData(int[] data) {
        this.data = data;
        postInvalidate();
    }

    public void setyTitle(String yTitle) {
        this.yTitle = yTitle;
        postInvalidate();
    }

    public void setxTitle(String xTitle) {
        this.xTitle = xTitle;
        postInvalidate();
    }

    public void setDataFactor(int dataFactor) {
        this.dataFactor = dataFactor;
        postInvalidate();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        return (int) (dpValue * dm.density + 0.5f);
    }
}
