package com.daiyinger.carplate;

/**
 *  @author lby 24/04/2018
 */
import android.util.Log;
public class CarPlateDetection {

    static {
        try {
            System.loadLibrary("native");
        }catch (Exception e)
        {
            Log.d("native","加载native库异常 ："+e.toString());
        }
    }

    /**
     * 识别车牌字符。注意，要指定svmpath、annpath、logpath绝对路径
     *
     * 注：用户权限 存储权限要打开
     *
     * 调用示例：
     *
     String logpath = "/sdcard/carxml/log.txt";
     //String imagpath = "/sdcard/carxml/test.jpg";
     String imagpath = "/sdcard/43.jpg";
     String svmpath= "/sdcard/carxml/svm.xml";
     String annpath= "/sdcard/carxml/ann.xml";
     byte[] data = CarPlateDetection.ImageProc(sdpath, logpath, imagpath, svmpath, annpath);
     *
     * @param sdpath 可任意
     * @param logpath 日志信息文件的路径
     * @param imgpath 待识别的图片的路径
     * @param svmpath svm.xml文件路径
     * @param annpath ann.xml文件路径
     */
    //public static native byte[] recognizeCarPlate(int[] src, int srcWidth, int srcHeight);
    public static native byte[] ImageProc(String sdpath,String logpath, String imgpath, String svmpath, String annpath);


    /*
    * 识别交通灯
    * 注：用户权限 存储权限要打开
    *
    *     错误：返回0
    正确：
        红1
        绿2
        橙3
    * */
    public static native int RecognizeTrafficLight(String imgpath);

    /*
         获取颜色数量
         imagePath：图片文件路径
         colorType：颜色类型
            红色：1    绿色：2    黄色：3    蓝色：4    品红：5    青色：6  黑色：7  白色：8
         返回：指定颜色的数量
     */
    public static native int getColorNumber(String imagePath, int colorType);

    // JNIEXPORT int JNICALL Java_com_daiyinger_carplate_CarPlateDetection_getColorShape
    //(JNIEnv *env, jclass obj, jstring imgpath, int color, int shapeType)
    /*
        获取指定颜色和形状的个数：比如红色三角形个数
        imagePath：图片文件路径
        color：颜色类型
            红色：1    绿色：2    黄色：3    蓝色：4    品红：5    青色：6  黑色：7  白色：8
        shape：形状类型
            三角形：1    圆形：2      矩形：3    菱形：4    五角星：5
        返回：指定颜色和形状的数量
    * */
    public static native int getColorShape(String imagePath, int color, int shape);

    /*
        获取指定形状类型数量
        imagePath：图片文件路径
        shape：形状类型
             三角形：1    圆形：2      矩形：3    菱形：4    五角星：5
             A矩形B圆形C三角D菱形E五角
        返回：指定形状的数量
     */
    public static native int getShapeNumber(String imagePath, int shapeType);


    /*
         设置HSV的区域颜色，包括LCD区域的HSV
            color：设置那种颜色的HSV值
              SHAPE_LCD   0  LCD  代表设置LCD的HSV
              SHAPE_RED  1  代表设置红色的HSV
              SHAPE_GREEN  2    代表设置绿色的HSV
              SHAPE_YELLOW 3    代表设置黄色的HSV
              SHAPE_BLUE  4     代表设置蓝色的HSV
              SHAPE_FUCHSIN  5    代表设置品红的HSV
              SHAPE_CYAN 6   代表设置青色的HSV
              SHAPE_BLACK 7   代表设置黑色的HSV
              SHAPE_WHITE 8   代表设置白色的HSV
              SHAPE_HSV   9   // 设置扣出所有图像的HSV
			  CAR_PLATE_HSV   10   // 车牌蓝色区域 扣出车牌蓝底区域  这个参数是这支车牌蓝色/黄色区域用的
              CAR_PLATE_SHAPE_HSV   11   // 车牌蓝底里的字 扣出车牌蓝底区域内车牌数据

            lowerH, lowerS, lowerV 下限HSV
            highH, highS, highV 上限HSV
            返回值：无效，仅仅作为保留

        调用例子：
            // 设置车牌的HSV范围为
            //  低值：lcdLowerHSV(44, 47, 130);
            //  高值：lcdHighHSV(190, 255, 255);
            setColorHSV(0, 44, 47, 130, 190, 255, 255);

        注意：该函数最好被调用，并且在识别车牌、颜色、形状等前

        默认的HSV范围：该范围是实验室已经调好的。不调用该函数就是使用默认的HSV
        // 车牌的HSV
            lcdLowerHSV(44, 47, 130);
            lcdHighHSV(190, 255, 255);

        // 红色
            redLowerHSV(156,153,133);
            redHighHSV(175,255,255);

        // 绿色
            greenLowerHSV(46,160,162);
            greenHighHSV(61,255,255);

        // 黄色
            yellowLowerHSV(9,160,162);
            yellowHighHSV(44,255,255);

        // 蓝色
            blueLowerHSV(89,255,209);
            blueHighHSV(118,255,255);

        // 品色
            fuchsinLowerHSV(121,160,162);
            fuchsinHighHSV(146,255,255);

        // 青色
            cyanLowerHSV(79,147,200);
            cyanHighHSV(89,255,255);

        // 黑色
            blackLowerHSV(26,34,13);
            blackHighHSV(135,255,82);

        // 白色 该颜色没测试，如果题目有该颜色则要现场测试下
            whiteLowerHSV(82, 75, 225);
            whiteHighHSV(110, 228, 255);

            //形状HSV：能够把所有图给抠出来的HSV界限， 以下是 color 9的默认值
            //Scalar shapeLowerHSV(63,29,64);
            //Scalar shapeHighHSV(100,218,255);


    * */
    public static native int setColorHSV(int color, int lowerH, int lowerS, int lowerV,
                                         int highH, int highS,  int highV);

    /*
         补充完成二维码：左上角位置补充完整
         @param srcPath:原始图片路径
         @param direction:补线的位置
            #define DIRECTION_LEFT  0 默认值：适配广东省赛题，补充左边线
            #define DIRECTION_TOP  1 补充上边线
            #define DIRECTION_RIGHT  2 补充右边线
            #define DIRECTION_BOTTOM  3 补充底边线
         @param dstPath:目标图片路径

         srcPath = "/storage/tmp/1.jpg"
         0
         dstPath = "/storage/tmp/1test.jpg"

         @return 成功：返回0 失败：返回-1
    */
    public static native int completeQRCodeLeftTopPosition
    (String srcPath,int direction, String dstPath);

    /*
     * 识别交通标志
     * 注：用户权限 存储权限要打开
       获取交通标识
     imagePath：图片文件路径
     path：模型文件路径

     返回：指定交通标识对应的代号
         -1-未查找到文件
         02-掉头
         03-右转
         04-直行
         05-左转
         06-禁止通行
         07-禁止直行

         其他数据为错误，要做返回值验证

     * */
    public static native int getTrafficSignNumber(String imgpath, String path);
}
