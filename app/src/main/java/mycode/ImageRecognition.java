package mycode;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.daiyinger.carplate.CarPlateDetection;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.HashMap;
import java.util.Map;

import car.bkrc.com.car2018.FileService;
import car.bkrc.right.fragment.LeftFragment;
import car2017_demo.RGBLuminanceSource;
/* * * * * * * * * * * * * * * * * * * * * * * * *
 *     String qr_recognition()   获取摄像头的bitmap识别二维码，返回String,识别不成功返回null.
 *     int TrafficlightRecognition()
 *
 *
 * * * * * * * * * * * * * * * * * * * * * * * * */

public class ImageRecognition {
    private final static String sdpath = "";
    private final static String logpath  = Environment.getExternalStorageDirectory()
            + "/carxml/log.txt";
    public final static String  trafficlight = Environment.getExternalStorageDirectory()
            + "/carxml/trafficlight.jpg";
    public final static String svmpath = Environment.getExternalStorageDirectory()
            + "/carxml/svm.xml";
    public final static String annpath = Environment.getExternalStorageDirectory()
            + "/carxml/ann.xml";
    public final static String tft_cp = Environment.getExternalStorageDirectory()
            + "/carxml/tft_cp.jpg";
    public final static String tft_xk = Environment.getExternalStorageDirectory()
            + "/carxml/tft_xk.jpg";
    public final static String traffic_sign = Environment.getExternalStorageDirectory()
            + "/carxml/raw.xml";
    public final static String raw = Environment.getExternalStorageDirectory()
            + "/carxml/temp.jpg";
    /************************************************************************
     * 二维码识别函数
     * 作用：识别二维码返回String
     * ***********************************************************************/
    public static String qr_recognition()
    {
        Result result = null;
        String result_qr=null;
        if (LeftFragment.bitmap!=null)
        {
            RGBLuminanceSource rSource = new RGBLuminanceSource(
                    LeftFragment.bitmap);

            try {
                BinaryBitmap binaryBitmap = new BinaryBitmap(
                        new HybridBinarizer(rSource));
                Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
                hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
                QRCodeReader reader = new QRCodeReader();
                result = reader.decode(binaryBitmap, hint);
                result_qr = result.toString();
            } catch (NotFoundException e) {
                Log.i("kk","异常1"+e.toString());
                FileService.savePhoto(LeftFragment.bitmap,"qr_exception.jpg");
                e.printStackTrace();
            } catch (ChecksumException e) {
                Log.i("kk","异常2"+e.toString());
                e.printStackTrace();
            } catch (FormatException e) {
                Log.i("kk","异常3"+e.toString());
                e.printStackTrace();
            }catch(Exception e){
                Log.i("kk","异常3"+e.toString());
            }
        }
        return result_qr;
    }
    /************************************************************************
     * 交通灯识别函数
     * 作用：识别交通灯 ,返回数据
     * ***********************************************************************/
    public static int TrafficlightRecognition()
    {
        FileService.savePhoto(LeftFragment.bitmap, "trafficlight.jpg");
        int trafficlight_data = CarPlateDetection
                .RecognizeTrafficLight(trafficlight);
        Log.i("kk","交通灯识别结果"+trafficlight_data);
        return trafficlight_data;
    }
    /************************************************************************
     * 车牌识别函数
     * 作用：
     * ***********************************************************************/
    public static byte[] LicenseplateRecognition()
    {
        Bitmap bmp=LeftFragment.bitmap;
        if (bmp!=null) {
            FileService.savePhoto(bmp, "tft_cp.jpg");
            byte[] data = CarPlateDetection.ImageProc(sdpath, logpath,
                    tft_cp, svmpath, annpath);
            if (data != null)
                Log.i("kk", "车牌识别结果" + new String(data));
            return data;
        }else
        return null;
    }
    public static byte[] LicenseplateRecognition1()
    {

        byte[] data =null;
        try {
            data = CarPlateDetection.ImageProc(sdpath, logpath,
                    Environment.getExternalStorageDirectory()
                            + "/A_mytest/test/te.png", svmpath, annpath);
        }catch (Exception e)
        {
            data=null;
        }
        if(data!=null)
            Log.i("kk","车牌识别结果"+new String(data));
        return data;
    }
    /************************************************************************
     * 拍照保存，并进行形状颜色形状识别
     * 作用：
     * ***********************************************************************/
    public static int colorShapeRecognition(int what,int color,int shape)
    {
        int result = -2;
        try {

            FileService.savePhoto(LeftFragment.bitmap, "tft_xk.jpg");

            if (what == 1)
                result = CarPlateDetection.getColorNumber(
                        tft_xk, color);
            Log.i("kk", "2");
            if (what == 2)
                result = CarPlateDetection.getShapeNumber(
                        tft_xk, shape);
            if (what == 3)
                result = CarPlateDetection.getColorShape(
                        tft_xk, color, shape);
            Log.i("kk", "形块结果：" + result);

        }catch (Exception e){
            Log.i("kk",e.getStackTrace()+"");
        }
        return result;
    }

    public static void ceshi()
    {
        int temp = 999;

        for(int i=0;i<5;i++) {
            temp = CarPlateDetection.getTrafficSignNumber(raw, traffic_sign);
            Log.i("car","当前为第"+(i+1)+ "次，当前检测到的结果是：" + temp);
        }

    }


    /* * * * * * * * * * * * * * * * *
* 方法功能：
*
*
* */
    public static Bitmap cutPicture(Bitmap b,int direction,int number,int number2)
    {
        Bitmap cutedBmp=null;
        if(direction==1)
        {
            cutedBmp = Bitmap.createBitmap(b, 0, 0, b.getWidth() / number * number2, b.getHeight());
        }
        else if(direction==2)
        {
            cutedBmp = Bitmap.createBitmap(b, b.getWidth() / number * (number - number2), 0, b.getWidth() - b.getWidth() / number * (number - number2), b
                    .getHeight());
        }
        return cutedBmp;
    }


}
