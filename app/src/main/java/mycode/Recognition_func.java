package mycode;

import android.graphics.Bitmap;
import android.util.Log;

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

public class Recognition_func {



    /* * * * * * * * * * * * * * * * * * * *
     *翻页并循环识别k次车牌
     * * * * * * * * * * * * * * * * * * * */
    public static byte[] LicenseplateRecognition(int k) {
        byte[] result_cp=null;
        for(int i=0;i<k;i++) {
            result_cp=ImageRecognition.LicenseplateRecognition();
            if(result_cp!=null&&result_cp.length==6) {
                return result_cp;
            }
        }
        return result_cp;
    }
    /* * * * * * * * * * * * * * * * * * * *
     *循环识别5次二维码
     * * * * * * * * * * * * * * * * * * * */
    public static String QR_Recognition_5() {
        String result = null;
        for (int i = 0; i < 5; i++) {
            result = ImageRecognition.qr_recognition();
            if (result != null) {
                return result;
            }
            sleep(500);
        }
        return result;
    }

    /* * * * * * * * * * * * * * * * * * * *
     *识别同一张图里有2张二维码
     * @param direction代表方向，1表示从左往右识别第一张二维码，2则从右往左
     * @return 返回一个String
     * * * * * * * * * * * * * * * * * * * */
    public static String QrSeparationRecognition(int direction,int num,int num2) {
        String result = null;

        for (int i = 0; i < 2; i++) {
            Bitmap bitmap = LeftFragment.bitmap;
            for (int j = num2; j < num; j++) {
                //Log.d("Qr", "QrSeparationRecognition: ");
                Bitmap b = ImageRecognition.cutPicture(
                        bitmap, direction, num, j);
                result = qr_recognition(b);
                //FileService.savePhoto(b, j+".jpg");
                if(result!=null){
                    return result;
                }
            }
        }
        return result;
    }

    /* * * * * * * * * * * * * *
     * 识别二维码
     * @param一个待识别的Bitmap对象
     * @return返回二维码内容的String对象
     * * * * * * * * * * * * */
    public static String qr_recognition(Bitmap bitmap)
    {
        Result result = null;
        RGBLuminanceSource rSource = new RGBLuminanceSource(
                bitmap);
        String result_qr=null;
        try {
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                    new HybridBinarizer(rSource));
            Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
            hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
            QRCodeReader reader = new QRCodeReader();
            result = reader.decode(binaryBitmap, hint);
            result_qr = result.toString();
        } catch (NotFoundException e) {
            FileService.savePhoto(LeftFragment.bitmap,"qr_exception.jpg");
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }catch(Exception e){
        }
        return result_qr;
    }
    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
