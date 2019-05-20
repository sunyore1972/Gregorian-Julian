package com.stevensun.clock.testclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
//import android.widget.TextView;

import java.util.Calendar;
import static java.lang.Math.*;
import static java.lang.Integer.*;

public class AlarmClockView extends View {

    private int mSecondHandColor; /*** 秒针颜色*/
    private int mMinuteHandColor; /*** 分针颜色*/
    private int mHourHandColor;   /*** 时针颜色*/
    private int mMinuteScaleColor;/*** 分钟刻度颜色*/
    private int mPointScaleColor; /*** 当分钟是5的倍数时刻度的颜色*/
    private int mDateValueColor;  /*** 时钟底部时间文本颜色*/
    private int mSunColor;        /*** 时钟底部时间文本颜色*/
    private int mMoonColor;       /*** 时钟底部时间文本颜色*/
    protected int mClockWid;      /*** 时钟宽度*/
    private int mOuterRadius;     /*** 时钟最外层圆半径*/
    private int mCenterX;         /*** 时钟圆心x*/
    private int mCenterY;         /*** 时钟圆心y*/
    private int mWid;             /*** 控件宽*/
    private int mHei;             /*** 控件高*/
    private Paint mPaint = new Paint();
    private int mOuterCircleColor;/*** 最外层圆颜色*/
    private int mInnerCircleColor;/*** 内层圆颜色*/
    private int mInnerRadius;     /*** 内层半径*/
    private int mSpace = 10;      /*** 内外圆的间距*/
    private int mHour;            /*** 现在的时间小时*/
    private int mMinute;          /*** 现在的时间分钟*/
    private int mSecond;          /*** 现在的时间秒*/
    //private int mScaleValueHei;   /*** 时钟上刻度值的高度*/
    private String[] arr = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private int mDay;             /*** 现在的时间天*/
    protected int mWeek;            /*** 现在的时间周几*/
    private int mMonth;           /*** 现在的时间月*/
    private int mYear;            /*** 现在的时间年*/
    private boolean mIsShowTime;  /*** 是否显示时钟底部的时间文本*/
    //private String mWeekStr;      /*** 真实的周几*/
    private TimeChangeListener listener;/*** 时间监听*/
    private float mProportion;    /*** 时钟占空间整体的比例*/
    private boolean mIsNight;     /*** 是否为夜间模式*/
    private Context context;
    private AttributeSet attrs;

    private String [] lunarDayOfMonth = {
            "初一","初二","初三","初四","初五","初六","初七","初八","初九","初十",
            "十一","十二","十三","十四","十五","十六","十七","十八","十九","二十",
            "廿一","廿二","廿三","廿四","廿五","廿六","廿七","廿八","廿九","三十"};
    public StringBuffer [] jieQi;
    public StringBuffer [] chuYi;
    public final String [] jiaZi = {
            //2000-01-07 00:00:00 甲子 JDN=2451550.500000
            // 0              2                     5                                   10       11
            "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉", "甲戌", "乙亥",
            //12             14                    17                    20
            "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未", "甲申", "乙酉", "丙戌", "丁亥",
            //24             26                    29     30
            "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳", "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥",
            //36             38             40     41
            "庚子", "辛丑", "壬寅", "癸卯", "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥",
            //48             50                    53
            "壬子", "癸丑", "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥",
    };
    public String [] diZhiCangGan = {
            "癸", "癸己辛", "戌甲丙", "乙", "乙戊癸", "戌丙庚", "丁己", "丁己乙", "戊庚壬", "辛" , "辛戊丁" ,"壬甲"
    };

    private Handler mHandler = new Handler();/*** handler用来处理定时任务，没隔一秒刷新一次*/
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initCurrentTime();
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mHandler.postAtTime(this, next);
        }

    };
    private int mApm;

    public AlarmClockView(Context context) {
        this(context, null);
    }

    public AlarmClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        this.attrs = attrs;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AlarmClockView);
        if (array != null) {
            mOuterCircleColor = array.getColor(R.styleable.AlarmClockView_outerCircleColor, getResources().getColor(R.color.gray,null));
            mInnerCircleColor = array.getColor(R.styleable.AlarmClockView_innerCircleColor, getResources().getColor(R.color.grayInner,null));
            mSecondHandColor  = array.getColor(R.styleable.AlarmClockView_secondHandColor, getResources().getColor(R.color.green,null));
            mMinuteHandColor  = array.getColor(R.styleable.AlarmClockView_minuteHandColor, getResources().getColor(R.color.black,null));
            mHourHandColor    = array.getColor(R.styleable.AlarmClockView_hourHandColor, getResources().getColor(R.color.black,null));
            mMinuteScaleColor = array.getColor(R.styleable.AlarmClockView_minuteScaleColor, getResources().getColor(R.color.black,null));
            mPointScaleColor  = array.getColor(R.styleable.AlarmClockView_scaleColor, getResources().getColor(R.color.black,null));
            mDateValueColor   = array.getColor(R.styleable.AlarmClockView_dateValueColor, getResources().getColor(R.color.black,null));
            mSunColor         = array.getColor(R.styleable.AlarmClockView_dateValueColor, getResources().getColor(R.color.sunColor,null));
            mMoonColor        = array.getColor(R.styleable.AlarmClockView_dateValueColor, getResources().getColor(R.color.moonColor,null));
            mIsShowTime       = array.getBoolean(R.styleable.AlarmClockView_isShowTime, true);
            mProportion       = array.getFloat(R.styleable.AlarmClockView_proportion, (float) 0.75);
            mIsNight          = array.getBoolean(R.styleable.AlarmClockView_night, false);
            if (mProportion > 1 || mProportion < 0) { mProportion = (float) 0.75; }

            array.recycle();
        }

        Calendar calendar = Calendar.getInstance();
        SolarTerm st = new SolarTerm();
        int iYear = calendar.get(Calendar.YEAR);
        jieQi = st.getJieQi(iYear);
        chuYi = st.getLunarChuYi(iYear);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWid = w;
        mHei = h;
        //使闹钟的宽为控件宽的mProportion;
        mClockWid = (int) (w * mProportion);
        mOuterRadius = mClockWid / 2;
        mInnerRadius = mOuterRadius - mSpace;
        mCenterX = w / 2;
        mCenterY = mCenterX;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int modeHei = MeasureSpec.getMode(heightMeasureSpec);
        if (modeHei == MeasureSpec.UNSPECIFIED || modeHei == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, width);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.alpha(Color.BLACK));        //设置整体控件的背景为白色背景
        canvas.drawRect(0, 0, mWid, mHei, mPaint);
        //drawOuterCircle(canvas);//画外层圆
        //drawInnerCircle(canvas);//画内层圆
        drawTickMark(canvas); //画刻度
        //drawScaleValue(canvas);//画刻度值
        drawHand(canvas);//画针
        if (mIsShowTime) { drawCurrentTime(canvas);}//画现在时间显示
    }

    public double getJulianDN(int year, int month, int day, int hour, int minute, int second){
        double y = year;
        double m = month;
        double n = 0;
        if (m <= 2) { m += 12; y--; }
        if (year * 372 + month * 31 + day >= 588829) {// 判断是否为格里高利历日1582*372+10*31+15
            n = (floor(y / 100) < 0)? floor(y / 100) + 1 :floor(y / 100)  ;
            n = 2 - n + ((floor(n / 4) < 0) ? floor(n / 4) + 1 :floor(n / 4));// 加百年闰
        }
        n += floor(365.2500001 * (y + 4716))< 0 ? floor(365.2500001 * (y + 4716)) + 1 : floor(365.2500001 * (y + 4716)); // 加上年引起的偏移日数
        n += (floor(30.6 * (m + 1))<0 ? floor(30.6 * (m + 1))+1 : floor(30.6 * (m + 1))) + day; // 加上月引起的偏移日数及日偏移数
        n += ((second / 60.0 + minute) / 60.0 + hour) / 24.0 - 1524.5;

        return n;
    }

    private void drawCurrentTime(Canvas canvas) {
        mPaint.setColor(mDateValueColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(50);
        mPaint.setTypeface(Typeface.MONOSPACE);
        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();//使当前时间文本正好在时钟底部距离有2 * mSpace的位置
        //gregorian calendar Time
        int baseLineY = mCenterY - mOuterRadius - fm.top + 2 * mSpace;
        if (mApm != 0) { mHour +=12; }
        String time = "" + (mHour<10?"0"+mHour:mHour) + ":" + (mMinute<10?"0"+mMinute:mMinute) + ":" + (mSecond<10?"0"+mSecond:mSecond);
        canvas.drawText(time, mCenterX, baseLineY, mPaint);
        //gregorian calendar Date
        int baseLineY2 = mCenterY - 180;
        String gregorian = "公历" + mYear + "年" + (mMonth+1) + "月" + mDay+"日 "+ arr[mWeek-1];
        canvas.drawText(gregorian, mCenterX, baseLineY2, mPaint);
        //lunar calendar Date
        Calendar calendar = Calendar.getInstance();
        int baseLineY3 = mCenterY - 110;
        StringBuffer lunar = null;
        for (int i=0;i < this.chuYi.length; i++){
            String [] s1 = this.chuYi[i].toString().split(" ");
            String [] s2;
            int mn = calendar.get(Calendar.MONTH) + 1;
            int dn = calendar.get(Calendar.DAY_OF_MONTH);
            if (s1.length < 3)
                s2 = s1[0].split("-");
            else
                s2 = s1[1].split("-");
            if(parseInt(s2[1]) == mn){ //初一月份 等于 当前月份
                int difference = dn - parseInt(s2[2]);
                if (difference >= 0) {//当前日期 减 初一日期
                    lunar = new StringBuffer("阴历 " + s1[0] + " " + lunarDayOfMonth[difference]);
                }else {
                    s1 = this.chuYi[i-1].toString().split(" ");
                    int l = s1[0].length();
                    String bs = s1[0].substring(l-1, l);
                    int bsm = 30; //lunarDayOfMonth[0~29] 30天
                    if(bs.equals("小")){bsm = 29;}
                    lunar = new StringBuffer("阴历 "+ s1[0]+" "+lunarDayOfMonth[ bsm + difference ]);
                }
            }
        }
        assert lunar != null;
        canvas.drawText(lunar.toString(), mCenterX, baseLineY3, mPaint);
        //干支计时
        int yn = calendar.get(Calendar.YEAR);
        int mn = calendar.get(Calendar.MONTH) + 1;
        int dn = calendar.get(Calendar.DAY_OF_MONTH);
        int hn = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sn = calendar.get(Calendar.SECOND);
        double cJDN = getJulianDN(yn,mn,dn,hn,min,sn);
        String liChun = null;
        for (int i= 0;i<24;i++){
            if ("立春".equals(jieQi[i].substring(0,2))) { liChun = jieQi[i].toString(); break; }
        }
        //年柱计算
        String nianZhu;
        assert liChun != null;
        int liChunYn = parseInt(liChun.split(" ")[1].split("-")[0]);
        int liChunMn = parseInt(liChun.split(" ")[1].split("-")[1]);
        int liChunDn = parseInt(liChun.split(" ")[1].split("-")[2]);
        int liChunHn = parseInt(liChun.split(" ")[2].split(":")[0]);
        int liChunMin = parseInt(liChun.split(" ")[2].split(":")[1]);
        //int liChunSn = parseInt(liChun.split(" ")[2].split(":")[2]);
        double liChunJDN = getJulianDN(liChunYn,liChunMn,liChunDn,liChunHn,liChunMin,0);
        nianZhu = (cJDN < liChunJDN) ? jiaZi[(yn - 4 - 1) % 60 ] : jiaZi[(yn - 4) % 60];
        //月柱计算
        String yueZhu = null;
        int number = 0;
        //只计算节之间的时间，大雪 小寒 立春 惊蛰 清明 立夏 芒种 小暑 立秋 白露 寒露 立冬。
        for (int i=0; i < 46; i+=2){
            String[] j = jieQi[i].toString().split(" ")[1].split("-");
            String[] j2 = jieQi[i].toString().split(" ")[2].split(":");
            double   j1 = getJulianDN(parseInt(j[0]), parseInt(j[1]), parseInt(j[2]), parseInt(j2[0]), parseInt(j2[1]), 0);
            String[] k = jieQi[i + 2].toString().split(" ")[1].split("-");
            String[] k2 = jieQi[i + 2].toString().split(" ")[2].split(":");
            double   k1 = getJulianDN(parseInt(k[0]), parseInt(k[1]), parseInt(k[2]), parseInt(k2[0]), parseInt(k2[1]), 0);
            if ((cJDN > j1) && (cJDN < k1)){
                number = ((int)round(i/2.0)<2)?((int)round(i/2.0) + 10):((int)round(i/2.0)-2);
                break;
            }
        }
        String nianGan = nianZhu.substring(0,1);
        if (nianGan.equals("甲") || nianGan.equals("己")){yueZhu = jiaZi[2 +number];}
        if (nianGan.equals("乙") || nianGan.equals("庚")){yueZhu = jiaZi[14+number];}
        if (nianGan.equals("丙") || nianGan.equals("辛")){yueZhu = jiaZi[26+number];}
        if (nianGan.equals("丁") || nianGan.equals("壬")){yueZhu = jiaZi[38+number];}
        if (nianGan.equals("戊") || nianGan.equals("癸")){yueZhu = jiaZi[(50+number)%60];}
        double jiaZiRi = 2451550.4583333335 % 60.0;
        double currJDN = cJDN % 60.0;
        double diff = 0.0;
        int riZhuNumber;
        if (currJDN >= jiaZiRi){//当前日期模大于2000-01-06 23:00:00甲子日的模，从甲子日向后算。
            diff = currJDN - jiaZiRi;
            riZhuNumber = diff < 1.0 ? 0 : (int)floor(diff);
            //System.out.println("1: >>>>"+currJDN+">>>>>"+jiaZiRi+">>>>>>>>>"+diff+"<<<< >>>>"+floor(diff));
        } else {
            diff = jiaZiRi - currJDN;
            //System.out.println("2: >>>>"+currJDN+">>>>>"+jiaZiRi+">>>>>>>>>"+diff+"<<<< >>>>"+floor(diff));
            riZhuNumber = 59 - (int)floor(diff);
        }
        String riZhu = jiaZi[riZhuNumber];
        String shiGan = riZhu.substring(0,1);
        String shiZhu = null;
        number = (int)(Math.round((hn+24)/2.0))%12;
        if (shiGan.equals("甲") || shiGan.equals("己")){ shiZhu = jiaZi[number];}
        if (shiGan.equals("乙") || shiGan.equals("庚")){ shiZhu = jiaZi[12+number];}
        if (shiGan.equals("丙") || shiGan.equals("辛")){ shiZhu = jiaZi[24+number];}
        if (shiGan.equals("丁") || shiGan.equals("壬")){ shiZhu = jiaZi[36+number];}
        if (shiGan.equals("戊") || shiGan.equals("癸")){ shiZhu = jiaZi[48+number];}
        assert yueZhu != null;
        assert shiZhu != null;
        String tianGan = "天干" + " " + nianZhu.substring(0,1) + " " + yueZhu.substring(0,1) + " "+riZhu.substring(0,1)+ " " + shiZhu.substring(0,1);
        String diZhi   = "地支" + " " + nianZhu.substring(1,2) + " " + yueZhu.substring(1,2) + " "+riZhu.substring(1,2)+ " " + shiZhu.substring(1,2);
        int baseLineY4 = mCenterY + mOuterRadius + fm.top +  2*mSpace;
        canvas.drawText(tianGan, mCenterX, baseLineY4 - 120, mPaint);
        canvas.drawText(diZhi  , mCenterX, baseLineY4 - 60 , mPaint);
    }
    public String getDiZhiCangGan(String dz){
        String cg = null;
        if (dz.equals("子")) cg = diZhiCangGan[0];
        if (dz.equals("丑")) cg = diZhiCangGan[1];
        if (dz.equals("寅")) cg = diZhiCangGan[2];
        if (dz.equals("卯")) cg = diZhiCangGan[3];
        if (dz.equals("辰")) cg = diZhiCangGan[4];
        if (dz.equals("巳")) cg = diZhiCangGan[5];
        if (dz.equals("午")) cg = diZhiCangGan[6];
        if (dz.equals("未")) cg = diZhiCangGan[7];
        if (dz.equals("申")) cg = diZhiCangGan[8];
        if (dz.equals("酉")) cg = diZhiCangGan[9];
        if (dz.equals("戌")) cg = diZhiCangGan[10];
        if (dz.equals("亥")) cg = diZhiCangGan[11];
        assert cg != null;
        return cg;
    }
    public String getJiaZiTime(String nrTG,int ysNum, boolean isYue){
        String jz = null;
        int number = ysNum;
        if (isYue) number+=2;
        if (nrTG.equals("甲") || nrTG.equals("己")){ jz = jiaZi[number];}
        if (nrTG.equals("乙") || nrTG.equals("庚")){ jz = jiaZi[12+number];}
        if (nrTG.equals("丙") || nrTG.equals("辛")){ jz = jiaZi[24+number];}
        if (nrTG.equals("丁") || nrTG.equals("壬")){ jz = jiaZi[36+number];}
        if (nrTG.equals("戊") || nrTG.equals("癸")){ jz = jiaZi[48+number];}
        assert jz != null;
        return jz;
    }

    private void drawHand(Canvas canvas) {
        //画时针
        canvas.save();
        int hourWid = 16;
        mPaint.setColor(mHourHandColor);
        mPaint.setStrokeWidth(hourWid);

        for (int i = 0; i < 12; i++) {
            if (i == mHour) {
                //计算时针的偏移量
                int offset = (int) (((float) mMinute / (float) 60) * (float) 30);
                canvas.rotate(offset, mCenterX, mCenterY);
                RectF rectF = new RectF(mCenterX - hourWid/2, mCenterY - mInnerRadius + 30, mCenterX + hourWid/2, mCenterY);
                canvas.drawRoundRect(rectF, hourWid/2, hourWid/2, mPaint);
                break;
            } else {
                canvas.rotate(30, mCenterX, mCenterY);
            }
        }
        canvas.restore();

        //画分针
        canvas.save();
        int minuteWid = 10;
        mPaint.setColor(mMinuteHandColor);
        mPaint.setStrokeWidth(10);

        for (int i = 0; i < 60; i++) {
            if (i == mMinute) {
                //计算分针的偏移量
                int offset = (int) ((float) mSecond / (float) 60 * (float) 6);
                canvas.rotate(offset, mCenterX, mCenterY);
                RectF rectF = new RectF(mCenterX - minuteWid/2, mCenterY - mInnerRadius - 60, mCenterX + minuteWid/2, mCenterY);
                canvas.drawRoundRect(rectF, minuteWid/2, minuteWid/2, mPaint);
                break;
            } else {
                canvas.rotate(6, mCenterX, mCenterY);
            }
        }
        canvas.restore();

        //画秒针
        canvas.save();
        mPaint.setColor(mSecondHandColor);
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(mCenterX, mCenterY, mSpace, mPaint);

        for (int i = 0; i < 60; i++) {
            if (i == mSecond) {
                canvas.drawLine(mCenterX, mCenterY + 3 * mSpace, mCenterX, mCenterY - mInnerRadius - 120/*mSpace*/, mPaint);
                break;
            } else {
                canvas.rotate(6, mCenterX, mCenterY);
            }
        }
        canvas.restore();

        //画太阳
        canvas.save();
        mPaint.setColor(mSecondHandColor);
        mPaint.setStrokeWidth(3);

        canvas.drawCircle(mCenterX,mCenterY,mSpace,mPaint);
        canvas.restore();

        //画月亮
        canvas.save();
        mPaint.setColor(mSecondHandColor);
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(mCenterX,mCenterY,mSpace,mPaint);
        canvas.restore();
    }

    private void drawScaleValue(Canvas canvas) {
        mPaint.setColor(mSunColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(30);

        //计算刻度值的文本高度
        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();
        //mScaleValueHei = fm.bottom - fm.top;

        float[] temp = calculatePoint(30, mInnerRadius - mSpace * 4 - mPaint.getTextSize() / 2);
        canvas.drawText("S", temp[2] + mCenterX, mCenterY + temp[3] + mPaint.getTextSize() / 2, mPaint);
    }

    private float[] calculatePoint(float angle, float length) {
        int POINT_BACK_LENGTH = 1;
        float[] points = new float[4];
        if (angle <= 90f) {
            points[0] = -(float) Math.sin(angle * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = (float) Math.cos(angle * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = (float) Math.sin(angle * Math.PI / 180) * length;
            points[3] = -(float) Math.cos(angle * Math.PI / 180) * length;
        } else if (angle <= 180f) {
            points[0] = -(float) Math.cos((angle - 90) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = -(float) Math.sin((angle - 90) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = (float) Math.cos((angle - 90) * Math.PI / 180) * length;
            points[3] = (float) Math.sin((angle - 90) * Math.PI / 180) * length;
        } else if (angle <= 270f) {
            points[0] = (float) Math.sin((angle - 180) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = -(float) Math.cos((angle - 180) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = -(float) Math.sin((angle - 180) * Math.PI / 180) * length;
            points[3] = (float) Math.cos((angle - 180) * Math.PI / 180) * length;
        } else if (angle <= 360f) {
            points[0] = (float) Math.cos((angle - 270) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[1] = (float) Math.sin((angle - 270) * Math.PI / 180) * POINT_BACK_LENGTH;
            points[2] = -(float) Math.cos((angle - 270) * Math.PI / 180) * length;
            points[3] = -(float) Math.sin((angle - 270) * Math.PI / 180) * length;
        }
        return points;
    }
    private void drawTickMark(Canvas canvas) {
        canvas.save();

        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                mPaint.setColor(mPointScaleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(5);
                mPaint.setAntiAlias(true);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setTextSize(30);

                //canvas.drawLine(mCenterX, mSpace * 2 + mCenterY - mOuterRadius, mCenterX, mSpace * 4 + mCenterY - mOuterRadius, mPaint);
            } else {
                mPaint.setColor(mMinuteScaleColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(2);
                mPaint.setAntiAlias(true);

                canvas.drawLine(mCenterX,   mCenterY - mOuterRadius - 90, mCenterX, mSpace * 3 + mCenterY - mOuterRadius - 130, mPaint);
            }

            canvas.rotate(6, mCenterX, mCenterY);
        }

        canvas.restore();
    }

    private void drawInnerCircle(Canvas canvas) {
        mPaint.setColor(mInnerCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint);

        if (mIsNight) {
            mPaint.setColor(Color.BLACK);
        } else {
            mPaint.setColor(Color.WHITE);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius - mSpace, mPaint);
    }

    private void drawOuterCircle(Canvas canvas) {
        mPaint.setColor(mOuterCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mPaint);
    }

    /**
     * 获取当前时间
     */
    public void initCurrentTime() {
        //Calendar mCalendar = Calendar.getInstance();
        resetTime(Calendar.getInstance());
        invalidate();
    }

    private void resetTime(Calendar calendar) {
        //if (listener != null) { listener.onTimeChange(calendar); }
        //calendar.add(Calendar.SECOND, 1);//因为获取的时间总是晚一秒，这里加上这一秒
        mYear   = calendar.get(Calendar.YEAR);
        mMonth  = calendar.get(Calendar.MONTH);
        mDay    = calendar.get(Calendar.DAY_OF_MONTH);
        mWeek   = calendar.get(Calendar.DAY_OF_WEEK);
        mHour   = calendar.get(Calendar.HOUR);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);
        mApm = calendar.get(Calendar.AM_PM);//apm=0 表示上午，apm=1表示下午。
        //nianZhu = jiZi[(mYear - 4)%60];
        //riZhu = jiZi[riZhuNumber];
        //mWeekStr = arr[calendar.get(calendar.DAY_OF_WEEK) - 1];//1.数组下标从0开始；2.老外的第一天是从星期日开始的
        //System.out.println("现在时间：小时：" + mHour + ",分钟：" + mMinute + ",秒：" + mSecond);
        if (listener != null) { listener.onTimeChange(calendar); }
    }

    public void start(TimeChangeListener listener) {
        this.listener = listener;
        mHandler.postDelayed(runnable, 1000);
        initCurrentTime();
    }

    /**
     * 运行闹钟
     */
    public void start() {
        mHandler.postDelayed(runnable, 1000);
        initCurrentTime();
    }

    /**
     * 停止闹钟
     */
    public void stop() {
        mHandler.removeCallbacks(runnable);
    }


    public void setCurrentTime(Calendar calendar) {
        stop();
        resetTime(calendar);
        invalidate();
    }

}

interface TimeChangeListener {
    void onTimeChange(Calendar calendar);
}
