package com.liu.custom.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Liu on 2019/11/6.
 */
public class Utils {

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static Drawable getDrawable(Context context, int id) {
        Drawable drawable = null;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 22) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    /**
     * 获取屏幕宽高
     *
     * @param activity activity
     * @return 返回屏幕宽高
     */
    public static int[] getScreenHeightAndWidth(Activity activity) {
        int[] hw = new int[2];
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        hw[0] = outMetrics.heightPixels;
        hw[1] = outMetrics.widthPixels;
        return hw;
    }

    /**
     * 获取状态栏的高度
     *
     * @param activity activity
     * @return 返回状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }


    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static String getGUID() {
        return UUID.randomUUID().toString();
    }

    public static String bytesToString(byte[] b) {
        StringBuffer result = new StringBuffer("");
        int length = b.length;
        for (int i = 0; i < length; i++) {
            result.append((char) (b[i] & 0xff));
        }
        return result.toString();
    }

    public static float getFormatterFloat(float f) {
        float f1 = 0.0f;
        try {
            BigDecimal b = new BigDecimal(f);
            f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
//        float   f2   =   (float)(Math.round(f*100))/100;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return f1;
        }

    }

    public static String getFormatterFloat(float f, int digits) {
        digits = digits > 0 ? digits : 2;
        StringBuilder sb = new StringBuilder();
        if (digits > 0) {
            sb.append("###0.");
            for (int i = 0; i < digits; i++) {
                sb.append("0");
            }
        } else {
            sb.append("#0");
        }
        //部分国家，格式化数据后会把小数点转换成逗号
        DecimalFormat df2 = new DecimalFormat(sb.toString());
        return df2.format(f).replace(",", ".");
    }

    public static String getFormatterDouble(double f, int digits) {
        digits = digits > 0 ? digits : 2;
        StringBuilder sb = new StringBuilder();
        if (digits > 0) {
            sb.append("###0.");
            for (int i = 0; i < digits; i++) {
                sb.append("0");
            }
        } else {
            sb.append("#0");
        }
        //部分国家，格式化数据后会把小数点转换成逗号
        DecimalFormat df2 = new DecimalFormat(sb.toString());
        return df2.format(f).replace(",", ".");
    }

    /**
     * 格式化报价
     *
     * @param price      报价
     * @param digit      有效位数
     * @param high_level true表示高级，false为普通模式
     * @return
     */
    public static SpannableString formatSymbolQuote(float price, int digit, boolean high_level) {
        SpannableString ss = new SpannableString(Utils.getFormatterFloat(price, digit));
        if (high_level && digit >= 2) {
            //高级模式
            if (digit % 2 == 1) {
                ss.setSpan(new RelativeSizeSpan(0.72f), 0, ss.length() - 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                //奇数，小数点后最后一位上标并且字号较小
                ss.setSpan(new SuperscriptSpan(), ss.length() - 1, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ss.setSpan(new RelativeSizeSpan(0.6f), ss.length() - 1, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                //倒数二三为字号较大
                ss.setSpan(new RelativeSizeSpan(1.3f), ss.length() - 3, ss.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            } else {
                ss.setSpan(new RelativeSizeSpan(0.72f), 0, ss.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                //偶数,小数点最后两位字号较大
                ss.setSpan(new RelativeSizeSpan(1.3f), ss.length() - 2, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        return ss;
    }

    public static String getFormatterDouble(Double f, int digits) {
        StringBuilder sb = new StringBuilder();
        if (digits > 0) {
            sb.append("###0.");
            for (int i = 0; i < digits; i++) {
                sb.append("0");
            }
        } else {
            sb.append("#0");
        }

        DecimalFormat df2 = new DecimalFormat(sb.toString());
        //部分国家，格式化数据后会把小数点转换成逗号
        return df2.format(f).replace(",", ".");
    }

    public static String getFormatter(int digits) {
        StringBuilder sb = new StringBuilder();
        if (digits > 0) {
            sb.append("###0.");
            for (int i = 0; i < digits; i++) {
                sb.append("0");
            }
        } else {
            sb.append("#0");
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否在列表中
     *
     * @param str        字符串
     * @param stringList 字符串列表
     * @return
     */
    public static boolean isStringExistence(String str, List<String> stringList) {
        boolean isExist = false;
        if (!TextUtils.isEmpty(str) && stringList != null) {
            int length = stringList.size();
            for (int i = 0; i < length; i++) {
                if (str.equals(stringList.get(i))) {
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }

    public static float digitsValue(float f) {
        float dVlaue = 0.0f;
        String str = f + "";
        int digits = str.length() - 1 - str.indexOf(".");
        StringBuilder sb = new StringBuilder();
        sb.append("0.");
        for (int i = 0; i < digits; i++) {
            if (i == digits - 1) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }
        dVlaue = Float.parseFloat(sb.toString());
        return dVlaue;
    }

    public static float getDigitsValue(int digits) {
        StringBuilder sb = new StringBuilder();
        sb.append("0.");
        for (int i = 0; i < digits; i++) {

            if (i == digits - 1) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }
        return Float.parseFloat(sb.toString());
    }

    public static int getDigitsLength(float f) {
        String str = f + "";
        int length = str.length();
        return length - 1 - str.indexOf(".");
    }

    public static int getDigitsLength(double f) {
        String str = f + "";
        int length = str.length();
        return length - 1 - str.indexOf(".");
    }

    public static int getDigitsLength(String str) {
        int digitsLength = 0;
        if (!TextUtils.isEmpty(str) && str.contains(".")) {
            digitsLength = str.length() - 1 - str.indexOf(".");
        }
        return digitsLength;
    }

    public static String getFormatterPrice(float f) {
        String str = "";
        int digits = 0;
        String fStr = f + "";
        int index = fStr.indexOf(".");
        if (index < 6) {
            if (fStr.length() > 7) {
                digits = 2;
            } else {
                digits = 6 - index;
            }

        } else {
            digits = 2;
            getDigitsLength(f);
        }

        str = getFormatterFloat(f, digits);
        return str;
    }

    public static String getFormatterPrice(double f) {
        String str = "";
        int digits = 0;
        String fStr = f + "";
        int index = fStr.indexOf(".");
        if (index < 6) {
            if (fStr.length() > 7) {
                digits = 2;
            } else {
                digits = 6 - index;
            }

        } else {
            digits = 2;
            getDigitsLength(f);
        }

        str = getFormatterDouble(f, digits);
        return str;
    }

    /**
     * 格式化金额
     *
     * @param amount 金额
     * @param digit  位数
     * @return
     */
    public static String formatAmount(double amount, int digit) {
        String amountStr = getFormatterDouble(amount, digit);
        if (!TextUtils.isEmpty(amountStr) && amount >= 1000) {
            StringBuilder sb = new StringBuilder();
            String[] amounts = amountStr.split("\\.");

            int length = amounts == null ? 0 : amounts[0].length();
            if (length >= 4) {
                for (int i = 0; i < length; i++) {
                    if (i == length - 3) {
                        sb.append(",");
                    }
                    sb.append(amounts[0].charAt(i));
                }
                if (amounts.length > 1) {
                    sb.append(".");
                    sb.append(amounts[1]);
                }
                amountStr = sb.toString();
            }

        }
        return amountStr;
    }

    public static int getDigits(float f) {
        String str = "";
        int digits = 0;
        String fStr = f + "";
        int index = fStr.indexOf(".");
        if (index < 6) {
            if (fStr.length() > 7) {
                digits = 2;
            } else {
                digits = 6 - index;
            }

        } else {
            digits = 2;
        }
        return digits;
    }

    public static String getVolUnit(float num) {

        int e = (int) Math.floor(Math.log10(num));

        if (e >= 8) {
            return "亿手";
        } else if (e >= 4) {
            return "万手";
        } else {
            return "手";
        }


    }

    /**
     * 解析浮点型
     *
     * @param value 字符串
     * @return fValue
     */
    public static float parseFloat(String value) {
        float fValue = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                fValue = Float.parseFloat(value);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return fValue;
    }

    /**
     * 解析double
     *
     * @param value 字符串
     * @return fValue
     */
    public static double parseDouble(String value) {
        double fValue = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                fValue = Double.parseDouble(value);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return fValue;
    }

    /**
     * 保证金比例小于400，设定提示颜色
     *
     * @param margin 保证金比例
     * @return
     */
    public static int setColorOfMargin(Context context, double margin, int defaultColor, int singColor) {
        if (margin > 0 && margin < 400) {
            return context.getResources().getColor(singColor);
        }
        return context.getResources().getColor(defaultColor);

    }


    /**
     * 解析整形
     *
     * @param value 字符串
     * @return fValue
     */
    public static int parseInt(String value) {
        int fValue = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                fValue = Integer.parseInt(value);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return fValue;
    }

    public static long parseLong(String value) {
        long lValue = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                lValue = Long.parseLong(value);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return lValue;
    }

    public static boolean isLe() {
        String model = Build.MODEL;
        if (model.contains("Le")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPhoneNumValid(String phone) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String regExp = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        boolean result = false;
        if (!TextUtils.isEmpty(phone)) {
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(phone);
            result = m.matches();
        }
        return result;
    }

    /**
     * 判断邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmailFormatCorrect(String email) {
        String regex = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        // "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email.trim());
        return matcher.matches();
    }

    public static boolean isPasswordCorrect(String password) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password.trim());
        return matcher.matches();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 判断字符串是否全是字母
     *
     * @param content 内容
     * @return
     */
    public static boolean isInputAllEn(String content) {
        boolean isAllEn = false;
        if (!TextUtils.isEmpty(content)) {
            Pattern pattern = Pattern.compile("[a-zA-Z]+");
            Matcher matcher = pattern.matcher(content);
            if (matcher.matches()) {
                isAllEn = true;
            }
        }
        return isAllEn;
    }

    /**
     * 判断字符串是否全是数字
     *
     * @param content 内容
     * @return
     */
    public static boolean isInputAllNumber(String content) {
        boolean isAllNum = false;
        if (!TextUtils.isEmpty(content)) {
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(content);
            if (matcher.matches()) {
                isAllNum = true;
            }
        }
        return isAllNum;
    }

    /**
     * 中文转换成全拼
     *
     * @param content 内容
     * @return
     */
    public static String cnToPinyin(String content) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(content)) {
            int length = content.length();
            for (int i = 0; i < length; i++) {
//                sb.append(Pinyin.toPinyin(content.charAt(i)).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static boolean isTel(String url) {
        boolean isTel = false;
        if (!TextUtils.isEmpty(url)) {
            String[] strs = url.split(":");
            if (strs[0].equals("tel")) {
                isTel = true;
            }
        }
        return isTel;
    }

    //使用Bitmap加Matrix来缩放
    public static Drawable resizeImage(Resources resources, Bitmap bitmap, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newHeight = h;
        int newWidth = h * width / height;


        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(resources, resizedBitmap);
    }

    /**
     * 判断列表是否为空
     *
     * @param list 列表
     * @return
     */
    public static boolean isArrayEmpty(List list) {
        return list == null || list.size() == 0;
    }


    /**
     * 获取application中指定的meta-data。调用方法时key就是UMENG_CHANNEL
     *
     * @return 如果没有获取成功(没有对应值, 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }


}
