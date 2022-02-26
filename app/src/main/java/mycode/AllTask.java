package mycode;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.daiyinger.carplate.CarPlateDetection;

import car.bkrc.com.car2018.FileService;
import car.bkrc.com.car2018.FirstActivity;
import car.bkrc.right.fragment.LeftFragment;
import car2017_demo.ConnectTransport;


//识别失败给主车发送0x55 0xAA 0xF4
//五角星主指令0xC0

public class AllTask {
    private final static String tft_xk = Environment.getExternalStorageDirectory()
            + "/carxml/tft_xk.jpg";
    private final static String tft_tra = Environment.getExternalStorageDirectory()
            + "/carxml/tft_tra.jpg";
    public final static String traffic_sign = Environment.getExternalStorageDirectory()
            + "/carxml/raw.xml";
    private static ConnectTransport mConnectTransport;
    private static int LuDeng;
    public AllTask(ConnectTransport connectTransport) {
        mConnectTransport = connectTransport;
    }

    public static final char[] Base64_str = {'a','i','q','y','6','B','H','l','b','j',
            'r','z','7','V','G','U','c','k','s','0','8','C','F','Y',
            'd','I','t','1','9','X','D','T','e','m','u','2','+','Z',
            'S','R','f','n','v','3','/','L','A','E','g','o','w','4','M',
            'K','P','W','h','p','x','5','N','J','O','Q','='};
    public static final int TFT_A = (byte) 0xB0;
    public static final int TFT_B = (byte) 0xB1;
    public static final int TRAFFICLIGHT_A = (byte) 0xB2;
    public static final int TRAFFICLIGHT_B = (byte) 0xB3;
    public static final int QR_A = (byte) 0xB4;
    public static final int QR_B = (byte) 0xB5;
    public static final int Car_tra = (byte) 0xB6;
    public static final int algorithm_top = (byte) 0xB7;//算法上半部分接收
    public static final int algorithm_below = (byte) 0xB8;//算法下半部分接收
    public static final int Car2 = (byte) 0xCC;
    public static final int ZDYSHUJU = (byte) 0xA4;
    public static final int Car1 = (byte) 0xCD; //上传调试信息1  ASCLL转文字
    public static final int Car12 = (byte) 0xCE; //上传调试信息2  十进制
    public static final int Car13 = (byte) 0xCF; //上传调试信息2  十六进制
    public static byte[] DebugCar = {0,0,0,0,0,0};
    public static char[] base= {'a','i','q','y','6','B','H','1',
            'b','j','r','z','7','V','G','U',
            'c','k','s','0','8','C','F','Y',
            'd','l','t','1','9','X','D','T',
            'e','m','u','2','+','Z','S','R',
            'f','n','v','3','/','L','A','E',
            'g','o','w','4','M','K','P','W',
            'h','p','x','5','N','J','O','Q'
    };//Base64编码索引表
    public static String str = "";//用于存储算法的初始公式码
    public static char[] str_data = new char[6];//用于存储算法处理完后的六位字节码
    public static int True_jx = -1, True_lx = -1, True_wj = -1, True_yx = -1,True_sj = -1;
    public static int True_tra = -1;
    public static int tra = 3;

    //在平板主页上显示传入的string
    public static void DataShow(String s) {
        Message msg = LeftFragment.showidHandler.obtainMessage(30, s);
        msg.sendToTarget();
    }

    static int x = 0;

    public static void b1() {
        x++;
        Log.i("kk", "x的值是：" + x);
        Message msg = LeftFragment.showidHandler.obtainMessage(34, x);
        msg.sendToTarget();
    }

    public static void b2() {
        x--;
        Message msg = LeftFragment.showidHandler.obtainMessage(34, x);
        msg.sendToTarget();
    }

    /* * * * * * *四个测试按键调用的函数 * * * * * * * * * * * * */
    public void SavePictures(int i) {
        FileService.savePhoto(LeftFragment.bitmap, "test" + i + ".jpg", "haha");
    }

//    陈悦 17:02:11
//    int h1 = 0, h2 = 255, s1 = 0, s2 = 255, v1 = 238, v2 = 255;//车牌
//
//    陈悦 17:03:13
//    int h1 = 0, h2 = 255, s1 = 0, s2 = 255, v1 = 0, v2 = 219;//车牌底
//
//    陈悦 17:04:12
//    int h1 = 0, h2 = 255, s1 = 0, s2 = 152, v1 = 210, v2 = 255;//图形LCD

//摄像头预设位调用函数

    void qr_camera(int N)
    {
        switch (N)
        {
            case 1:
                //摄像头向上
                LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 33, 0);
                break;
            case 2:
                //摄像头复位
                LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 33, 0);
                break;

        }

    }
    public void test1() {

        DataShow("翻页");
        Sendtool.page_down(1,2);
        DataShow("翻页结束");

    }

    public void test2() {
        //TFT_Traffic(0);
        TFT_Traffic_one();
    }

    public void test3() {
        TFT_A_text();
    }

    /* * * * * * * * * * * * * * * * * * * * * * */
    public static void automatic(byte rbyte[]) {
        /*Sendtool.StartUp();
        Sendtool.Send.yanchi(100);
        Sendtool.StartUp();
        Sendtool.Send.yanchi(100);
        Sendtool.StartUp();
        Sendtool.Send.yanchi(100);*/

        if (FirstActivity.wifi_threadExit) {
                switch (rbyte[2]) {
                    case algorithm_top://接收到上半部分算法数据

                        master_order_start();//确认接收到指令应答

                        Base64_below_receive(rbyte[3],rbyte[4],rbyte[5]);//执行Base64上半位接收

                        master_order_over();//执行完毕应答
                        break;
                    case algorithm_below://接收到下半部分算法数据

                        master_order_start();

                        Base64_low_receive(rbyte[3],rbyte[4],rbyte[5]);//执行Base64下半位接收，并将算法处理

                        master_order_over();

                        str="";
                        break;

                    case TFT_A:
                        master_order_start();
                        DataShow("开始识别图形");
                        Sendtool.preSetCammera(1, 3000);
                        try {
                             //recognitionTFT_A(3);
                            //TFT_Traffic(0);//识别对应的交通标志物的值并返回
                            TFT_A_text();//识别所有形块的值并返回
                            //TFT_Traffic(tra);//识别所需交通标志物
                            //ColorShapeTFT_A();//识别特定颜色以及形块的值并返回
                            DataShow("识别图形完毕");
                        } catch (Exception e) {
                            DataShow("识别图形异常");
                            Log.e("bug", Log.getStackTraceString(e));
                        }
                        master_order_over();
                        break;
                    case TFT_B:
                        master_order_start();
                        DataShow("开始识别车牌");
                        Sendtool.preSetCammera(2, 3000);
                        try {

                            recognitionTFT_B(3);
                            DataShow("识别车牌完毕");
                        } catch (Exception e) {
                            DataShow("识别车牌异常");
                            Log.e("bug", Log.getStackTraceString(e));
                        }
                        master_order_over();
                        break;
                    case TRAFFICLIGHT_A:
                        master_order_start();
                        DataShow("开始识别交通灯A");

                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 35, 0);
                        //Sendtool.preSetCammera(3, 3000);
                        try {
                            switch (trafficlight_A()) {
                                case 1:
                                    DataShow("交通灯A:红灯");
                                    break;
                                case 2:
                                    DataShow("交通灯A:绿灯");
                                    break;
                                case 3:
                                    DataShow("交通灯A:黄灯");
                                    break;
                                default:
                                    DataShow("交通灯A:识别失败");

                            }
//                            qr_camera(2);
                            DataShow("识别交通灯A完毕");
                        } catch (Exception e) {
                            Log.e("bug", Log.getStackTraceString(e));
                        }
                        master_order_over();
                        break;
                    case TRAFFICLIGHT_B:
                        master_order_start();
                        DataShow("开始识别交通灯B");

                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 35, 0);
                        //Sendtool.preSetCammera(3, 3000);
                        try {
                            switch (trafficlight_A()) {
                                case 1:
                                    DataShow("交通灯B:红灯");
                                    break;
                                case 2:
                                    DataShow("交通灯B:绿灯");
                                    break;
                                case 3:
                                    DataShow("交通灯B:黄灯");
                                    break;
                                default:
                                    DataShow("交通灯B:识别失败");

                            }
//                            qr_camera(2);
                            DataShow("识别交通灯B完毕");
                        } catch (Exception e) {
                            Log.e("bug", Log.getStackTraceString(e));
                        }

                        master_order_over();

                        break;
                    case QR_A:
                        master_order_start();
                        DataShow("开始识别二维码A");
                        Sendtool.preSetCammera(4, 3000);
                        DataShow("开始检测");

                        for(int i=0;i<5;i++)
                        {
                            try {
                                str = recognition_QRA();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                str_data = BLF(str);
                                DataShow("检测成功");
                                break;
                            } catch (Exception e) {
                                DataShow("检测失败");
                                e.printStackTrace();
                            }
                        }

                        ConnectTransport.yanchi(300);
                        Sendtool.sendData(0xA2, str_data[0],str_data[1],str_data[2]);
                        ConnectTransport.yanchi(300);
                        Sendtool.sendData(0xA3, str_data[3],str_data[4],str_data[5]);
                        ConnectTransport.yanchi(300);

                        DataShow("烽火台密码为:"+str_data[0]+str_data[1]+str_data[2]+str_data[3]+str_data[4]+str_data[5]);
                        master_order_over();
                        break;
                    case QR_B:
                        master_order_start();
                        DataShow("开始识别二维码B");
                        Sendtool.preSetCammera(5, 3000);
                        try {
                            recognition_QRB();
                        } catch (Exception e) {
                            Log.e("bug", Log.getStackTraceString(e));
                        }
                        mConnectTransport.yanchi(500);
                        Sendtool.sendData(0XA2, 0, 0, 0);
                        mConnectTransport.yanchi(200);
                        Sendtool.sendData(0XA2, 0, 0, 0);
                        mConnectTransport.yanchi(200);
                        Sendtool.sendData(0XA3, 0, 0, 0);
                        master_order_over();
                        break;
                    case Car1:
                        DataShow("主车：" + (char) mConnectTransport.rbyte[3] + " " + (char) mConnectTransport.rbyte[4] + " " + (char) mConnectTransport.rbyte[5]);

                        break;
                    case Car12:
                        DataShow("主车：" + Integer.toString(Car1noSign(mConnectTransport.rbyte[3])) + " " + Integer.toString(Car1noSign(mConnectTransport.rbyte[4])) + " " + Integer.toString(Car1noSign(mConnectTransport.rbyte[5])));


                        break;
                    case Car13:
                        DataShow("主车：" + hexString8(mConnectTransport.rbyte[3]) + " " + hexString8(mConnectTransport.rbyte[4]) + " " + hexString8(mConnectTransport.rbyte[5]));


                        break;
                    case Car2:
                        Car2Data();
                        //DataShow("副车："+Integer.toString(mConnectTransport.rbyte[3])+" "+ Integer.toString(mConnectTransport.rbyte[4])+" "+Integer.toString(mConnectTransport.rbyte[5]));

                        break;


                    case (byte) 0xB9://上传破损车牌
                        master_order_start();
                        DebugCar[0] = mConnectTransport.rbyte[3];
                        DebugCar[1] = mConnectTransport.rbyte[4];
                        DebugCar[2] = mConnectTransport.rbyte[5];
                        DataShow("车牌byte1："+DebugCar[0]+","+DebugCar[1]+","+DebugCar[2]);
                        break;
                    case (byte) 0xBA:
                        master_order_start();
                        DebugCar[3] = mConnectTransport.rbyte[3];
                        DebugCar[4] = mConnectTransport.rbyte[4];
                        DebugCar[5] = mConnectTransport.rbyte[5];
                        DataShow("车牌byte2："+DebugCar[3]+","+DebugCar[4]+","+DebugCar[5]);
                        break;
                    case Car_tra:
                        tra = mConnectTransport.rbyte[3];
                        break;
                }

        }
    }


    public static void Car2Data() {
        /*副车交通灯识别*/
        if (mConnectTransport.rbyte[3] == 0x01) {
            DataShow("副车交通灯：" + "识别结果" + Integer.toString(Car1noSign(mConnectTransport.rbyte[4])));
            /*副车二维码*/
        } else if (mConnectTransport.rbyte[3] == 0x02) {
            if (mConnectTransport.rbyte[4] == 0x01) {
                DataShow("副车二维码：" + "识别成功，数据长度" + Integer.toString(Car1noSign(mConnectTransport.rbyte[5])));
            } else if (mConnectTransport.rbyte[4] == 0x02) {
                DataShow("副车二维码：" + "计算结果" + Integer.toString(Car1noSign(mConnectTransport.rbyte[5])));
                mConnectTransport.yanchi(500);
                Sendtool.sendData(0XA2,0,0,0);
                mConnectTransport.yanchi(200);
                Sendtool.sendData(0XA2,0,0,0);
                mConnectTransport.yanchi(200);
                Sendtool.sendData(0XA3,0,0,0);
            }
        } else if (mConnectTransport.rbyte[3] == 0x03) {
            DataShow("副车测距：" + Integer.toString(Car1noSign(mConnectTransport.rbyte[4])) + "." + Integer.toString(Car1noSign(mConnectTransport.rbyte[5])) + "cm");
        }else{
            DataShow("副车数据：" + Integer.toString(Car1noSign(mConnectTransport.rbyte[3])) +","+Integer.toString(Car1noSign(mConnectTransport.rbyte[4])) +"," + Integer.toString(Car1noSign(mConnectTransport.rbyte[5])));
        }
    }

    /**
     功   能: TFT交通标志识别(单次)
     参   数: target所需要搜寻到的交通标志
     返回值：每个交通标志物对应的数字
     1-未查找到文件
     02-掉头
     03-右转
     04-直行
     05-左转
     06-禁止通行
     07-禁止直行
     */
    public static int TFT_Traffic_one()
    {
        int temp_Traffic_num = 0;
        int[] temp_Traffic_num_int = new int[10];
        int result = 0;

        Bitmap bmp=null;
        DataShow("开始识别交通标志物");

        try {
            for(int i=0;i<8;i++)
            {
                bmp=LeftFragment.bitmap;//拍照
                mConnectTransport.yanchi(10);

                if (bmp != null)
                {
                    FileService.savePhoto(bmp, "tft_tra.jpg");//保存图像
                    //DataShow("照片保存成功");

                    temp_Traffic_num_int[temp_Traffic_num] = CarPlateDetection.getTrafficSignNumber(tft_tra, traffic_sign);//识别图形个数

                    if(temp_Traffic_num_int[temp_Traffic_num]>1)
                    {
                        //DataShow("当前识别结果为:" + result);
                        temp_Traffic_num++;
                        //break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[] last_Traffic_num = new int[temp_Traffic_num--];
        int temp=0;

        for(int i:temp_Traffic_num_int)
        {
            if(i>1)
            {
                last_Traffic_num[temp] = i;
                temp++;
            }
        }

        result = Extract_mode(last_Traffic_num);
        DataShow("当前识别结果为:" + result);

        return result;
    }

    /**
     功   能: TFT交通标志识别
     参   数: target所需要搜寻到的交通标志
     返回值：每个交通标志物对应的数字
     1-未查找到文件
     02-掉头
     03-右转
     04-直行
     05-左转
     06-禁止通行
     07-禁止直行
     */
    public static void TFT_Traffic(int target)
    {
        int flag = 0;
        int temp_Traffic_num = 0;
        int[] temp_Traffic_num_int = new int[10];
        int result = 0;

        for(int i=0;i<5;i++)
        {
            DataShow("正在准备翻页搜寻");
            Sendtool.page_down(1,2);//TFTA，下翻一页
            mConnectTransport.yanchi(3500);
            result = TFT_Traffic_one();

            if(target <= 5)
            {
                if(result == target)
                {
                    DataShow("已找到所需标志物");
                    flag = 1;
                    break;
                }
                else
                {
                    DataShow("未找到，继续翻页");
                }
            }
            else
            {
                if(target>=5)
                {
                    DataShow("已找到所需标志物");
                    flag = 1;
                    break;
                }
                else
                {
                    DataShow("未找到，继续翻页");
                }
            }

        }

        if(True_tra>1)
        {
            DataShow("正在准备将数据发送给主车(G)");

            Sendtool.sendData(0xA5, True_tra, True_tra, True_tra);
            mConnectTransport.yanchi(20);
            Sendtool.sendData(0xA5, True_tra, True_tra, True_tra);
            mConnectTransport.yanchi(20);
            Sendtool.sendData(0xA5, True_tra, True_tra, True_tra);
            mConnectTransport.yanchi(20);

            DataShow("成功将数据通过wifi发送，交通标志物识别结束");
        }
        else if(flag == 1)
        {
            DataShow("正在准备将数据发送给主车");

            Sendtool.sendData(0xA5, result, result, result);
            mConnectTransport.yanchi(20);
            Sendtool.sendData(0xA5, result, result, result);
            mConnectTransport.yanchi(20);
            Sendtool.sendData(0xA5, result, result, result);
            mConnectTransport.yanchi(20);

            DataShow("成功将数据通过wifi发送，交通标志物识别结束");

        }
        else
        {
            DataShow("正在准备将数据发送给主车（f）");

            Sendtool.sendData(0xA5, 2, 2, 2);
            mConnectTransport.yanchi(20);
            Sendtool.sendData(0xA5, 2, 2, 2);
            mConnectTransport.yanchi(20);
            Sendtool.sendData(0xA5, 2, 2, 2);
            mConnectTransport.yanchi(20);

            DataShow("成功将数据通过wifi发送，交通标志物识别结束");

        }
    }

    /*
        获取指定颜色和形状的个数：比如红色三角形个数
        imagePath：图片文件路径
        color：颜色类型
            红色：1    绿色：2    黄色：3    蓝色：4    品红：5    青色：6  黑色：7  白色：8
        shape：形状类型
            三角形：1    圆形：2      矩形：3    菱形：4    五角星：5
        返回：指定颜色和形状的数量
    * */
    //识别特定的颜色以及指定形状的图形，并将读取到的值返回给主车
    public static void ColorShapeTFT_A() {

        CarPlateDetection.setColorHSV(0, 80, 40, 222, 125, 120, 255);
        CarPlateDetection.setColorHSV(1, 139, 200, 50, 255, 255, 255);//红色设置
        CarPlateDetection.setColorHSV(9, 77, 49, 224, 255, 138, 255);

        int shape = 2;

        for(int  tp=0;tp<4;tp++) //循环识别三张图片
        {
            Bitmap bmp=null;//重新定义用于实时照片数据的对象
            int[] temp = {0, 0, 0};//重新定义用于存储形块的数组

            try {

                DataShow("准备翻页");
                Sendtool.page_down(1,2);//TFTA，下翻一页
                mConnectTransport.yanchi(3500);//翻页速度慢需延迟
                DataShow("翻页完毕");

                for (int j = 0; j < 3; j++)
                {
                    mConnectTransport.yanchi(50);
                    bmp = LeftFragment.bitmap;

                    if (bmp != null)
                    {
                        FileService.savePhoto(bmp, "tft_xk.jpg");//保存图像
                        mConnectTransport.yanchi(50);
                        temp[j] = CarPlateDetection.getColorShape(tft_xk, 1, 3);//在这里设置所需要识别的颜色和形状
                        DataShow("本次检测到的形块值为的值:" + temp[j]);
                    }

                    else
                    {
                        DataShow("拍照失败");
                        break;
                    }

                }

                if(Extract_mode(temp)>0)
                {
                    DataShow("找到所需要识别的图片");
                    shape = Extract_mode(temp);
                    break;
                }
            }
            catch (Exception e)
            {
                DataShow("识别失败");
            }

        }

        if(True_jx>0 || True_lx>0 || True_wj>0 || True_yx>0 || True_sj>0)
        {
            DataShow("即将发送数据(G):"+True_jx);
            for (int i = 0; i < 3; i++)
            {
                mConnectTransport.yanchi(10);
                Sendtool.sendData(0x45, True_jx,True_jx , True_jx);
                Sendtool.sendData(0x46, True_jx, True_jx, True_jx);
            }
        }
        else
        {
            DataShow("即将发送数据:"+shape);

            for (int i = 0; i < 3; i++)
            {
                mConnectTransport.yanchi(10);
                Sendtool.sendData(0x45, shape, shape, shape);
                Sendtool.sendData(0x46, shape, shape, shape);
            }
        }

        DataShow("发送完毕");

    }

    //识别所有形块并将数值发送给主车
    public static void TFT_A_text()
    {
        int flag = 0;
        int jx = -1, lx = -1, wj = -1, yx = -1,sj = -1;
        Bitmap bmp=null;

        CarPlateDetection.setColorHSV(0, 80, 40, 222, 125, 120, 255);
        CarPlateDetection.setColorHSV(9, 59, 28,177,153, 137,255);
        try {
            for (int i = 0; i < 5; i++) {
                jx = -1;lx = -1; wj = -1; yx = -1;sj = -1;
                DataShow("开始识别图形，准备翻页");

                Sendtool.page_down(1, 2);
                mConnectTransport.yanchi(3500);

                mConnectTransport.yanchi(50);
                bmp = LeftFragment.bitmap;

                mConnectTransport.yanchi(50);

                FileService.savePhoto(bmp, "tft_xk.jpg");//保存图像
                mConnectTransport.yanchi(50);

                sj = CarPlateDetection.getShapeNumber(tft_xk, 1);//识别图形个数
                mConnectTransport.yanchi(10);
                yx = CarPlateDetection.getShapeNumber(tft_xk, 2);
                mConnectTransport.yanchi(10);
                jx = CarPlateDetection.getShapeNumber(tft_xk, 3);
                mConnectTransport.yanchi(10);
                lx = CarPlateDetection.getShapeNumber(tft_xk, 4);
                mConnectTransport.yanchi(10);
                wj = CarPlateDetection.getShapeNumber(tft_xk, 5);
                mConnectTransport.yanchi(10);

                if (jx == -1 || lx == -1 || jx == -1 || yx == -1 || sj == -1) {
                    DataShow("未检测到平板内图片中形块总数");
                } else {
                    DataShow("检测到平板内图片中形块总数\n三角：" + sj + " 圆形：" + yx + " 矩形：" + jx + " 菱形：" + lx + " 五角：" + wj);
                    if(jx > 4)//检测当前图片矩形数是否大于4（因为菱形无法检测）
                    {
                        DataShow("当前为所需图形");
                        jx-=2;
                        lx+=2;
                        break;
                    }
                    DataShow("当前并非所需图形");
                    flag = 1;
                    //break;
                }

            }
        } catch (Exception e) {
            DataShow("图形检测异常");
            e.printStackTrace();
        }

        try {
            if(True_jx>0 || True_lx>0 || True_wj>0 || True_yx>0 || True_sj>0)
            {
                DataShow("即将发送数据(G):"+True_jx);
                for (int i = 0; i < 3; i++)
                {
                    mConnectTransport.yanchi(10);
                    Sendtool.sendData(0x45, True_jx,True_yx , True_sj);
                    Sendtool.sendData(0x46, True_lx, True_wj, 0);
                }
            }
            else if (flag == 1) {
                DataShow("即将发送数据:"+True_jx);
                for (int i = 0; i < 3; i++) {
                    mConnectTransport.yanchi(10);
                    Sendtool.sendData(0x45, jx, yx, sj);
                    Sendtool.sendData(0x46, lx, wj, 0);
                }
            } else {
                DataShow("即将发送数据(F):"+True_jx);
                for (int i = 0; i < 3; i++) {
                    mConnectTransport.yanchi(10);
                    Sendtool.sendData(0x45, 2, 2, 2);
                    Sendtool.sendData(0x46, 2, 2, 0);
                }
            }
            DataShow("数据发送完毕");
        }
        catch (Exception e) {
            DataShow("数据发送异常");
            e.printStackTrace();
        }


    }

//    SHAPE_LCD   0  LCD  代表设置LCD的HSV  需要的LCD为白色
//    SHAPE_RED  1  代表设置红色的HSV  需要的颜色为黑色
//    SHAPE_GREEN  2    代表设置绿色的HSV 需要的颜色为黑色
//    SHAPE_YELLOW 3    代表设置黄色的HSV 需要的颜色为黑色
//    SHAPE_BLUE  4     代表设置蓝色的HSV 需要的颜色为黑色
//    SHAPE_FUCHSIN  5    代表设置品红的HSV 需要的颜色为黑色
//    SHAPE_CYAN 6   代表设置青色的HSV 需要的颜色为黑色
//    SHAPE_BLACK 7   代表设置黑色的HSV 需要的颜色为黑色
//    SHAPE_WHITE 8   代表设置白色的HSV 需要的颜色为黑色

//    SHAPE_HSV   9   // 设置扣出所有图像的HSV  需要的为黑色
//    CAR_PLATE_HSV   10   // 车牌蓝色区域 扣出车牌蓝底区域  这个参数是这支车牌蓝色/黄色区域用的   需要的车牌为白色
//    CAR_PLATE_SHAPE_HSV   11   // 车牌蓝底里的字 扣出车牌蓝底区域内车牌数据    需要的字为黑色
    // 0 LCD底色
//@parm n 图片的总数
public static void recognitionTFT_A(int N) {

         Bitmap bmp=null;
         CarPlateDetection.setColorHSV(0, 80, 40, 222, 125, 120, 255);
         CarPlateDetection.setColorHSV(9, 59, 28,177,153, 137,255);
        //CarPlateDetection.setColorHSV(0,0, 0,185,255, 162,255);
        //.setColorHSV(9,0, 0,185,255, 162,255);
        int jxz = 0, lxz = 0, wjz = 0, yxz = 0,sjz = 0;

        for (int j=0;j<N;j++) {//识别N张图

            int jx = 0, lx = 0, wj = 0, yx = 0, k = 0,sj=0;
            int[] jxi = {-1, -1, -1}, lxi = {-1, -1, -1}, wji = {-1, -1, -1}, yxi = {-1, -1, -1},sji = {-1, -1, -1};

            //识别
            for (int i = 0; i < 3; i++) {

                bmp=LeftFragment.bitmap;
                if (bmp!=null) {
                    FileService.savePhoto(bmp, "tft_xk.jpg");//保存图像
                    mConnectTransport.yanchi(100);
                    sj = CarPlateDetection.getShapeNumber(tft_xk, 1);//识别图形个数
                    yx = CarPlateDetection.getShapeNumber(tft_xk, 2);
                    jx = CarPlateDetection.getShapeNumber(tft_xk, 3);
                    lx = CarPlateDetection.getShapeNumber(tft_xk, 4);
                    wj = CarPlateDetection.getShapeNumber(tft_xk, 5);
                    if (jx > -1 || wj > -1 || lx > -1) {
                        sji[k] = sj;
                        yxi[k] = yx;
                        jxi[k] = jx;
                        lxi[k] = lx;
                        wji[k] = wj;
                        k++;
                    }
                }
            }

            sj = Extract_mode(sji);//识别3次取众数
            wj = Extract_mode(wji);
            yx = Extract_mode(yxi);
            lx = Extract_mode(lxi);
            jx = Extract_mode(jxi);
            sjz = sj;
            wjz = wj;
            yxz = yx;
            lxz = lx;
            jxz = jx;

            DataShow("A:矩形--"+jxz+"B:圆形--"+yxz+"F:三角--"+sjz+"D:菱形--"+lxz+"E:五角--"+wjz);//调试识别结果

            if ((jxz+lxz+yxz+wjz+sjz) > 3) { //设置欲寻找的图形跳出条件 圆形与五角星数量一致
                //保存返回结果
                //发送结果到主车
                DataShow("检测到当前图片为所需图片，即将结束检测");
                Sendtool.sendData(0x45,jxz,yxz,sjz);
                Sendtool.sendData(0x46,lxz,wjz,0);
                mConnectTransport.yanchi(200);//翻页速度慢需延迟
                Sendtool.sendData(0x45,jxz,yxz,sjz);
                Sendtool.sendData(0x46,lxz,wjz,0);
                break;
            }
            else {
                DataShow("检测到当前图片并非所需图片，即将翻页");
                Sendtool.page_down(1, 2);
                mConnectTransport.yanchi(4000);//翻页速度慢需延迟
            }

        }

        Sendtool.sendData(0x45,1,1,1);
        Sendtool.sendData(0x46,1,1,1);

    }

    // 0 LCD底色
    // 10 车牌底色
    // 11 车牌底色
    //@parm N 车牌个数
    public static String recognitionTFT_B(int N) {
        try {

            CarPlateDetection.setColorHSV(0, 23, 0, 208, 192, 255, 255);
            CarPlateDetection.setColorHSV(10, 0, 0,222,161, 157,255);
            CarPlateDetection.setColorHSV(11, 0, 169, 155, 255, 255, 255);

            byte[] result_cp = null;
            byte[] result_cp2 = null;
            String result_show = null;

            DataShow("破损车牌："+new String(DebugCar));
            for (int c=0;c<6;c++)
            {
                if ((DebugCar[c]<'A'||DebugCar[c]>'Z')&&(DebugCar[c]<'0'||DebugCar[c]>'9'))
                {
                    DebugCar[c]=0;
                }
            }//过滤破损车牌数据

            for (int n=0;n<N;n++) {//识别N张图

                result_cp = Recognition_func.LicenseplateRecognition(3);//车牌识别
                if (result_cp != null) {
                    result_show = new String(result_cp);
                    DataShow("车牌识别结果：" + result_show);

                    for (int j = 0, jk = 0; j < 6; j++) {//对比是否未欲寻找的车牌
                        if (DebugCar[j] != 0) {
                            if (result_cp[j] == DebugCar[j]) {
                                jk++;//
                            } else {
                                break;
                            }
                        }
                        if (jk == 3) {
                            result_cp2 = result_cp;
                            break;
                        }
                    }
                }
                if (result_cp2!=null)
                {
                    break;
                }
                Sendtool.page_down(2, 2);//TFT翻页
                mConnectTransport.yanchi(4000);//翻页速度慢需延迟
            }

                result_cp2 = result_cp;
                if (result_cp2 != null) {
                    result_show = new String(result_cp2);
                }


                if (result_cp != null)//向主车发送结果
                {
                    Sendtool.send_six(0x40, 0x41, (int) result_cp2[0], (int) result_cp2[1], (int) result_cp2[2], (int) result_cp2[3], (int) result_cp2[4], (int) result_cp2[5]);
                    Sendtool.send_six(0x40, 0x41, (int) result_cp2[0], (int) result_cp2[1], (int) result_cp2[2], (int) result_cp2[3], (int) result_cp2[4], (int) result_cp2[5]);

                }
                else
                {
                    Sendtool.send_six(0x40, 0x41, 0, 0, 0, 0, 0, 0);
                    Sendtool.send_six(0x40, 0x41, 0, 0, 0, 0, 0, 0);
                }
                return result_show;

        }catch (Exception e)
        {
            Log.d("TFTCP", e.toString());
        }
        return null;
    }




    public static String recognition_QRA() {
        String data = null;
        //byte[] data=null;

        Log.d("QRA", "recognition_QRA: ");
        byte[] code = new byte[8];
        try {
            data = ImageRecognition.qr_recognition();//启动识别二维码
            if (data != null) {
                char intArray[] = new char[QRStr(data).length];

                //code = Qrchongqing(data);

               // Sendtool.sendData(0xA2, data.charAt(0), data.charAt(1), data.charAt(2));
                //Sendtool.sendData(0xA3, data.charAt(3), data.charAt(4), data.charAt(5));
                mConnectTransport.yanchi(100);
               // Sendtool.sendData(0xA2, data.charAt(0), data.charAt(1), data.charAt(2));
               // Sendtool.sendData(0xA3, data.charAt(3), data.charAt(4), data.charAt(5));
                DataShow("二维码识别结果:" + data+" 长度:"+QRStr(data).length);
            } else {

            }
        }catch (Exception e)
        {

            //Sendtool.sendData(0xA2, code[0], code[1], code[2]);
           // Sendtool.sendData(0xA2, code[0], code[1], code[2]);
        }

        Log.d("QRA", "recognition_QRA: 识别结束");
        return data;
    }

    static void recognition_QRB() {
        String result = null;
        byte[] data = null;
        int mode=1;
        for (int i = 0; i < 4; i++) {
            if(mode ==1) {
                result = Recognition_func.QrSeparationRecognition(1, 50, 10);
            }else if(mode ==2){
                result = Recognition_func.QrSeparationRecognition(2, 50, 10);
            }
            if (result != null) {
                DataShow("二维码识别信息"+result);
                if(mode == 1) {
                    char receive[] = new char[StrTool.Stringextraction(result).length];
                    receive = StrTool.Stringextraction(result);
                    for (int i2 = 0; i2 < receive.length - 1; i2++) {
                        if (receive[i2] == 'B' && receive[i2 + 1] == '4') {
                            Sendtool.send5data(0X02,0x0C,0xB4,0XB4,0XB4);
                            DataShow("从车朝向"+"B4");
                            mConnectTransport.yanchi(100);
                            Sendtool.send5data(0x02,0x0C,0xB4,0XB4,0XB4);
                        }
                        if (receive[i2] == 'F' && receive[i2 + 1] == '4') {
                            Sendtool.send5data(0x02,0x0C,0xF4,0XF4,0XF4);
                            DataShow("从车朝向"+"F4");
                            mConnectTransport.yanchi(100);
                            Sendtool.send5data(0x02,0x0C,0xF4,0XF4,0XF4);
                        }
                        if (receive[i2] == 'D' && receive[i2 + 1] == '2') {
                            Sendtool.send5data(0x02,0x0C,0xD2,0XD2,0XD2);
                            DataShow("从车朝向"+"D2");
                            mConnectTransport.yanchi(100);
                            Sendtool.send5data(0x02,0x0C,0xD2,0XD2,0XD2);
                        }
                        if (receive[i2] == 'D' && receive[i2 + 1] == '6') {
                            Sendtool.send5data(0x02,0x0C,0xD6,0XD6,0XD6);
                            DataShow("从车朝向"+"D6");
                            mConnectTransport.yanchi(100);
                            Sendtool.send5data(0x02,0x0C,0xD6,0XD6,0XD6);
                        }
                    }
                    mode = 2;
                }else if (mode == 2){
                    int s1[] = {0,0,0,0,0,0,0,0,0,0,0,0};
                    char receive[] = new char[StrTool.Stringextraction(result).length];
                    receive =StrTool.Stringextraction(result);
                    for (int i2=0;i2<receive.length;i2++){
                        if (i2%2==0) {
                            s1[i2] = (int)receive[i2]-0x37;
                        }else{
                            s1[i2] = (int)receive[i2]-0x30;
                        }
                    }
                    Sendtool.send5data(0x02,0x08,s1[0]*16+s1[1],s1[2]*16+s1[3],s1[4]*16+s1[5]);
                    mConnectTransport.yanchi(100);
                    Sendtool.send5data(0x02,0x08,s1[0]*16+s1[1],s1[2]*16+s1[3],s1[4]*16+s1[5]);
                    mConnectTransport.yanchi(100);
                    Sendtool.send5data(0x02,0x08,s1[0]*16+s1[1],s1[2]*16+s1[3],s1[4]*16+s1[5]);
                    mConnectTransport.yanchi(100);
                    Sendtool.send5data(0x02,0x09,s1[6]*16+s1[7],s1[8]*16+s1[9],s1[10]*16+s1[11]);
                    mConnectTransport.yanchi(100);
                    Sendtool.send5data(0x02,0x09,s1[6]*16+s1[7],s1[8]*16+s1[9],s1[10]*16+s1[11]);
                    mConnectTransport.yanchi(100);
                    Sendtool.send5data(0x02,0x09,s1[6]*16+s1[7],s1[8]*16+s1[9],s1[10]*16+s1[11]);
                    mode = 3;
                    DataShow("从车路径"+numToHex8(s1[0])+s1[1]+" "+numToHex8(s1[2])+s1[3]+" "+numToHex8(s1[4])+s1[5]+" "+numToHex8(s1[6])+s1[7]+" "+numToHex8(s1[8])+s1[9]+" "+numToHex8(s1[10])+s1[11]);
                    break;
                }
            }
            if (mode != 3){
                DataShow("二维码识别失败");
            }
            mConnectTransport.yanchi(500);
        }
    }

    static  int trafficLight = 0;
    static int trafficLight_mode[] = {0,0,0};

    public static int trafficlight_A() {
        Sendtool.RecognitionMode(1);
        mConnectTransport.yanchi(2000);

        for (int i = 0,j = 0; ((i<3) && (j<5));j++) {
            trafficLight = ImageRecognition.TrafficlightRecognition();
            //DataShow("交通灯识别中：" +trafficLight_mode[i]+"");
            //mConnectTransport.yanchi(50);
            if (trafficLight != 0) {
                trafficLight_mode[i] = trafficLight;
                i++;
            }
        }
        //DataShow("交通灯识别数组：" +trafficLight_mode[0]+" "+trafficLight_mode[1]+" "+trafficLight_mode[2]+" ");
        trafficLight = Extract_mode(trafficLight_mode);
        //DataShow("交通灯识别结果：" + trafficLight);
        if (trafficLight == 0) {
            Sendtool.sendTrafficResult((byte) 1, (byte) 1);
            mConnectTransport.yanchi(200);
            Sendtool.sendTrafficResult((byte) 1, (byte) 1);
        }else{
            Sendtool.sendTrafficResult((byte) 1, (byte) trafficLight);
            mConnectTransport.yanchi(200);
            Sendtool.sendTrafficResult((byte) 1, (byte) trafficLight);
        }

        return trafficLight;
    }
    /****************************************************************
     * 函 数 名 ：  字符提取 2019年国赛选拔题 山东
     * 参    数 ：  QCString
     * 返 回 值 ：2位RFDI扇区
     * 全局变量 ：  无
     * 备    注 ：  2021/1/6
     ****************************************************************/
    public static char[] QrR(String QCString) {
        char[] code = {0,0};
        if(QCString != null) {
            char[] s = QCString.toCharArray();
            for(int i=0;i < s.length;i++) {
                if (s[i]=='S') {
                    if (s[i+1]=='E' && (i+1) < s.length) {
                        if (s[i+2]=='C' && (i+2) < s.length) {
                            if (s[i+3]=='=' && (i+3) < s.length) {
                                if (s[i+4]<='9' && s[i+4]>='0' && (i+4) < s.length) {
                                    code[0] = s[i+4];
                                }
                            }
                        }
                    }
                }
                if (s[i]=='B') {
                    if (s[i+1]=='L' && (i+1) < s.length) {
                        if (s[i+2]=='K' && (i+2) < s.length) {
                            if (s[i+3]=='=' && (i+3) < s.length) {
                                if (s[i+4]<='9' && s[i+4]>='0' && (i+4) <= s.length) {
                                    code[1] = s[i+4];
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return code;
        }else {
            return code;
        }
    }
    public static char[] QRStr(String QRString) {
        char[] s2 = QRString.toCharArray();
        if(QRString != null) {
//            int Many_letters = 0;
            char[] s = QRString.toCharArray();
            for (int i = 0, j = 0; i < s.length; i++) {
                if ((s[i] >= 'a' && s[i] <= 'z') || (s[i] >= 'A' && s[i] <= 'Z')) {
//                    Many_letters++;
                    s2[j] = s[i];
                    j++;
                }
            }
            return s2;
        }else{
            return null;
        }
    }

    public static byte[] Qrchongqing(String QCString) {
        char[] s3= QCString.toCharArray();
        char[] s1= {0,0,0,0,0,0,0,0};
        for(int i=0;i<QCString.length();i++) {
            s1[i] = s3[i];
        }
        byte[] s2 = {0x3d,0x3d,0x3d,0x3d,0x3d,0x3d,0x3d,0x3d};
        s2[0] = (byte) (((byte)s1[0]>>2)&0x3f);
        s2[1] = (byte) ((((byte)s1[0]<<4)&0x30)+(((byte)s1[1]>>4)&0x0f));
        s2[2] = (byte) ((((byte)s1[1]<<2)&0x3C)+(((byte)s1[2]>>6)&0x03));
        s2[3] = (byte) ((byte)s1[2]&0x3f);
        s2[4] =  (byte) (((byte)s1[3]>>2)&0x3f);
        s2[5] = (byte) ((((byte)s1[3]<<4)&0x30)+(((byte)s1[4]>>4)&0x0f));
        s2[6] = (byte) ((((byte)s1[4]<<2)&0x3C)+(((byte)s1[5]>>6)&0x03));
        s2[7] =  (byte) ((byte)s1[5]&0x3f);
        if (QCString.length()>8){
        }else {
            switch (QCString.length()) {
                case 1:
                    s2[2] = 0x3d;
                    s2[3] = 0x3d;
                    s2[4] = 0x3d;
                    s2[5] = 0x3d;
                    s2[6] = 0x3d;
                    s2[7] = 0x3d;
                    break;
                case 2:
                    s2[3] = 0x3d;
                    s2[4] = 0x3d;
                    s2[5] = 0x3d;
                    s2[6] = 0x3d;
                    s2[7] = 0x3d;
                    break;
                case 3:
                    s2[4] = 0x3d;
                    s2[5] = 0x3d;
                    s2[6] = 0x3d;
                    s2[7] = 0x3d;
                    break;
                case 4:
                    s2[6] = 0x3d;
                    s2[7] = 0x3d;
                    break;
                case 5:
                    s2[7] = 0x3d;
                    break;
            }
        }

        for(int i =0;i<8;i++) {
            s2[i] = (byte) base[s2[i]];
        }
        for (int i = 1; i < s2.length; i++) {
            //挖出一个要用来插入的值,同时位置上留下一个可以存新的值的坑
            int x = s2[i];
            int j = i - 1;
            //在前面有一个或连续多个值比x大的时候,一直循环往前面找,将x插入到这串值前面
            while (j >= 0 && s2[j] > x) {
                //当arr[j]比x大的时候,将j向后移一位,正好填到坑中
                s2[j + 1] = s2[j];
                j--;
            }
            //将x插入到最前面
            s2[j + 1] = (byte) x;
        }
        System.out.println(s2[0]+","+s2[1]+","+s2[2]+","+s2[3]+","+s2[4]+","+s2[5]+","+s2[6]+","+s2[7]);
        return s2;
    }

    /**
     * 2021.12.3 ZX
     功   能: 成功接收到主车命令,尚未开始执行命令时，为主车反馈确认指令
     参   数:无
     返回值:无
     */
    public static void master_order_start()
    {
        for(int i=0;i<3;i++)//为主车循环发送三次指令，可更改循环值来改变发送次数
        {
            ConnectTransport.yanchi(20);//少许添加延时，以防数据紊乱
            Sendtool.sendData(0xA1, 0, 0, 0);//为主车发送相应反馈
        }
    }

    /**
     * 2021.12.3 ZX
     功   能: 接收到主车命令后，并执行完成对应命令时，为主车反馈确认指令
     参   数:无
     返回值:无
     */
    public static void master_order_over()
    {
        for(int i=0;i<3;i++)//为主车循环发送三次指令，可更改循环值来改变发送次数
        {
            ConnectTransport.yanchi(20);//少许添加延时，以防数据紊乱
            Sendtool.sendData(0xA1, 1, 1, 1);//为主车发送相应反馈
        }
    }

    /**
     * 2021.12.3 ZX
     功   能: 发送特殊数据处理
     参   数:无
     返回值:无
     */
    public static void master_ts_over()
    {
        for(int i=0;i<3;i++)//为主车循环发送三次指令，可更改循环值来改变发送次数
        {
            ConnectTransport.yanchi(20);//少许添加延时，以防数据紊乱
            Sendtool.sendData(0xA1, 2,2,2);//为主车发送相应反馈
        }
    }

    /**
     * 2021.12.3 ZX
     功   能: 接收算法数据的上半部分（Base64）
     参   数:a,b,c(上半部分的三位数据)
     返回值:无
     */
    public static void Base64_below_receive(byte a,byte b,byte c)
    {
        ConnectTransport.yanchi(300);

        DataShow("开始接收算法源式上半部分");
        try {

            str = str.concat(String.valueOf((char)a));
            str = str.concat(String.valueOf((char)b));
            str = str.concat(String.valueOf((char)c));

            Log.i("car", "算法源式上半部分:"+str);
            DataShow("上半部分接收完毕");
        } catch (Exception e) {
            DataShow("上半部分接收异常");
            Log.e("bug", Log.getStackTraceString(e));
        }
    }

    /**
     * 2021.12.3 ZX
     功   能: 接收算法数据的下半部分，并将上下两部分数据进行处理（Base64）
     参   数: a,b,c(下半部分的三位数据)
     返回值:解出并排序后的六位ASCII码数组
     */
    public static void Base64_low_receive(byte a,byte b,byte c)
    {
        ConnectTransport.yanchi(300);

        DataShow("开始接收算法源式下半部分");
        try {

            str = str.concat(String.valueOf((char)a));
            str = str.concat(String.valueOf((char)b));
            str = str.concat(String.valueOf((char)c));

            Log.i("car", "算法源式下半部分:"+str);
            DataShow("下半部分接收完毕:"+str);
            DataShow("开始处理......");

            try {

                DataShow("原数据 = "+str);
                str_data = Base64_Car(str);
                DataShow("编码后字符 = "+str_data[0]+str_data[1]+str_data[2]+str_data[3]+str_data[4]+str_data[5]);
                DataShow("处理成功，正在发送数据");
                ConnectTransport.yanchi(500);
                Sendtool.sendData(0xAE, str_data[0],str_data[1],str_data[2]);
                DataShow("上半部分处理结果已发送");
                ConnectTransport.yanchi(300);
                Sendtool.sendData(0xAF,str_data[3],str_data[4],str_data[5]);
                DataShow("下半部分处理结果已发送，算法部分完毕");

            }
            catch (Exception e) {
                DataShow("处理失败");
                Log.e("bug", Log.getStackTraceString(e));
            }


        } catch (Exception e) {
            DataShow("下半部分接收异常");
            Log.e("bug", Log.getStackTraceString(e));
        }
    }

    /**
     * 2021.12.3 ZX
     功   能: Base64小车任务用(从前三位字符开始自动识别成)（未优化）
     参   数: str:要进行Base64编码的字符串
     返回值:解出并排序后的六位ASCII码数组
     */

    public static char[] Base64_Car(String str)
    {
        int flag=0;
        int flag1=0;
        int flag2=0;
        int temp=0;
        String str_temp = "";
        char[] str_data = str.toCharArray();

        int[] bin_temp = new int[50];
        int[] bin = new int[50];
        char[] Last_data = new char[6];

        bin[0] = -1;

        for(char i:str_data)
            flag++;//用于记录str字符串长度

        str_temp = "";

        while(flag>=3)
        {
            str_temp = "";

            for(int i=0;i<3;i++)
            {
                str_temp = str_temp.concat(String.valueOf(str_data[i]));
            }

            for(int i=3;i<flag;i++)
            {
                str_data[i-3] = str_data[i];

            }

            flag-=3;


            bin_temp = Str_transition_bin(str_temp);//将二维码数据转换为二进制int型的数组
            bin_temp = Bit_6(bin_temp);//将传输进的二进制数据6Bit分组
            bin_temp = Base64_encryption(bin_temp);//将数据索引解出

            flag1=0;

            for(int i:bin)
            {
                if(i == -1)
                {
                    for(int j:bin_temp)
                    {
                        if(j == -1)
                            break;
                        bin[flag1] = j;
                        flag1++;
                    }
                    bin[flag1] = -1;
                    break;
                }
                flag1++;
            }
        }

        if(flag >0 && flag<3)
        {
            str_temp="";
            for(int i=0;i<flag;i++)
            {
                str_temp = str_temp.concat(String.valueOf(str_data[i]));
            }

            bin_temp = Str_transition_bin(str_temp);//将二维码数据转换为二进制int型的数组
            bin_temp = Bit_6(bin_temp);//将传输进的二进制数据6Bit分组
            bin_temp = Base64_encryption(bin_temp);//将数据索引解出

            flag=0;
            for(int i:bin)
            {
                if(i == -1)
                {
                    for(int j:bin_temp)
                    {
                        if(j == -1)
                            break;
                        bin[flag] = j;
                        flag++;
                    }
                    bin[flag] = -1;
                    break;
                }
                flag++;
            }
        }

        flag=0;
        for(int i:bin)
        {
            if(i == -1)
                break;

            bin[flag] = Base64_str[i];
            flag++;
        }

        for(int i=0;bin[i]!=-1;i++)
            flag2++;//用于记录str字符串长度

        flag = flag1 = 0;
        for (flag = 0; flag < flag2 - 1; flag++)
        {
            for (flag1 = 0; flag1 < flag2 - 1 - flag; flag1++)
            {
                if (bin[flag1] < bin[flag1 + 1])
                {
                    temp = bin[flag1];
                    bin[flag1] = bin[flag1 + 1];
                    bin[flag1 + 1] = temp;
                }
            }
        }

        flag=0;
        for(int i:bin)
        {
            if(i == -1 || flag==6)
                break;
            Last_data[flag] = (char)bin[flag];
            flag++;
        }

        return Last_data;
    }
    /**
     * 2021.12.3 ZX
     功   能: Base64编码加密（未优化）
     参   数: Bin:所需要加密的二进制数组(int)
     返回值:加密完成的数组(int)
     */
    public static int[] Base64_encryption(int[] Bin)
    {
        int[] str_data = new int[50];
        int flag[] = new int[50];//游标
        int[] Bin_str_temp = new int[32];

        flag[0] = flag[1] = 0;
        for(int i : Bin)
        {
            if(i == -2)
                break;
            Bin_str_temp[flag[0]] = i;
            flag[0]++;

            if(flag[0] == 8)
            {
                flag[0] = 0;
                str_data[flag[1]] = Bin_Dec(Bin_str_temp);

                if(str_data[flag[1]]!=0)
                    flag[1]++;
            }
        }

        if(flag[1]>=3)
        {
            str_data[4] = -1;
        }
        else
        {
            for(int i=flag[1];i<4;i++)
            {
                str_data[i] = 64;
            }

            str_data[4] = -1;
        }
        flag[0] = flag[1] = 0;
        return str_data;
    }
    /**
     * 2021.12.3 ZX
     功   能:将传输进的二进制数据，转换为十进制数据
     参   数: Bin：二进制数组(int[])
     返回值: 转换后的十进制数组
     */
    public static int Bin_Dec(int[] Bin)
    {
        int num=0;

        for(int i=7;i>=0;i--)
        {
            if(Bin[i] == 1)
            {
                num += (int)Math.pow(2,Math.abs(i-7));
            }
        }

        return num;
    }
    /**
     * 2021.12.3 ZX
     功   能:将传输进的二进制数据，进行6Bit分组,高位补0，并分为4个字节的二进制数据（Base64编码），结尾为-2（未优化）
     参   数: Bin：二进制数组（int）
     返回值: 处理好的二进制数组（int[]）（数组结尾为-2）
     */
    public static int[] Bit_6(int[] Bin)
    {
        int[] Bit_6 = new int[100];
        int[] temp = new int[100];
        int flag=0;//二进制数组游标
        int flag2=0;//二进制数组游标2号
        int flag3=0;//二进制数组游标3号

        for (int i : Bin)
        {

            if(i==-1)
            {
                break;
            }

            temp[flag] = i;
            flag++;//此处的flag游标代表temp数组索引

            if(flag == 6)
            {
                temp[flag] = -1;//用-1来分割6位数据
                flag++;
            }
            else if(flag % 7 == 0 && flag != 6)
            {
                temp[flag] = -1;//用-1来分割6位数据
                flag++;
            }
        }

        temp[flag] = -2;//数据转换结束时在结尾填入-2，声明到此为止结束

        for (int i : temp)
        {
            if(i==-2)
                break;

            if(i==1 || i==0)
                flag3++;
        }

        for(int i=(flag3%6);i<6;i++)
        {
            temp[flag] = 0;
            flag++;
        }

        temp[flag] = -2;//数据转换结束时在结尾填入-2，声明到此为止结束
        flag3=flag=0;


        for (int i : temp)
        {
            if(i==-2)
                break;
        }

        for (int i : temp)
        {
            if(i==-1)
            {
                flag++;//此时flag游标代表遇到-1的次数
                continue;
            }
            else if(i==-2 || flag == 4)
                break;
            else if(flag2 == 0 || flag2%8 == 0)
            {
                flag2+=2;
            }

            Bit_6[flag2] = i;
            flag2++;
        }


        Bit_6[flag2] = -2;//数据转换结束时在结尾填入-1，声明到此为止结束
        flag=0;//重置游标值
        flag2=0;//重置游标值


        return Bit_6;
    }
    /**
     * 2021.12.3 ZX
     功   能:将二维码数据转换为二进制int型的数组
     参   数: 字符串数值（char）
     返回值: 处理后的二维码数据（int[]）
     */
    public static int[] Str_transition_bin(String str)//将二进制数据转换为二进制int型的数组
    {

        int temp=0;//临时变量
        int flag=0;//二进制数组游标
        String[] binStr = new String[50];
        int[] Bin = new int[100];
        int[] temp_num = new int[100];
        char[] ch = new char[50];
        char[] dispose_ch = new char[50];

        ch = str.toCharArray();

        //Integer.parseInt(输入的字符串, 几进制);将会输出转换后的十进制值

        for(int i=0;i<ch.length;i++)
        {
            binStr[i] = Integer.toBinaryString(ch[i]);

            //System.out.printf("%s",binStr[i]);

            if(binStr[i].length() < '8')
            {
                temp = 8 - binStr[i].length();
            }

            if(temp != 0)//如果转换出的值不满8位
            {
                while(temp > 0)//高位补0
                {
                    Bin[flag] = 0;//不足0的位填充0
                    flag++;
                    temp--;
                }
            }

            temp_num = BinstrToIntArray(binStr[i]);

            for(int j=0;j<temp_num.length;j++)
            {
                //System.out.printf("%d",temp_num[j]);
                Bin[flag] = temp_num[j];
                flag++;
            }

            temp = 0;
        }

        Bin[flag] = -1;//数据转换结束时在结尾填入-1，声明到此为止结束
        return Bin;
    }

    /**
     * 2021.12.3 ZX
     功   能:将二进制字符串转换成int数组
     参   数: 字符串数值（String）
     返回值: 转换后的数据（int[]）
     */
    public static int[] BinstrToIntArray(String binStr) {
        char[] temp=binStr.toCharArray();
        int[] result=new int[temp.length];
        for(int i=0;i<temp.length;i++) {
            result[i]=temp[i]-48;
        }
        return result;
    }

    /****************************************************************
     * 函 数 名 ：  提取RFID扇区，数据块，N  2020江苏
     * 参    数 ：  QR_data
     * 返 回 值 ：RFID扇区，数据块，N
     * 全局变量 ：  无
     * 备    注 ：  2021/3/1
     ****************************************************************/
    public static int[]QCRFIDdata(String QRString){
        int[] code = {1,1,1};
        if(QRString != null) {
            int Many_letters = 0;
            char[] s = QRString.toCharArray();
            for (int i = 0; i < QRString.length(); i++) {
                if (s[i]=='<'&&digit(s[i+1])&&digit(s[i+2])&&s[i+3]=='-'&&digit(s[i+4])&&s[i+5]=='>'&&s[i+6]=='/'&&digit(s[i+7])){
                    code[0]=(s[i+1]-48)*10+(s[i+2]-48);
                    code[1]=s[i+4]-48;
                    code[2]=(s[i+7]-48)%4+1;
                    break;
                }
            }
        }
        return code;
    }
    /****************************************************************
     * 函 数 名 ：  CRC校验配套二维码数据处理 2018年国赛
     * 参    数 ：  QR_data
     * 返 回 值 ：6位烽火台开启码
     * 全局变量 ：  无
     * 备    注 ：  2020/12/28
     ****************************************************************/
    public static char[] CRCQR(String QRString) {
        char[] code = {0,0,0,0,0,0};
        if(QRString != null) {
            int Many_letters = 0;
            char[] s = QRString.toCharArray();
            char[] s2 = QRString.toCharArray();
            char register_CRC[] = {0x41,0x61,0x42,0x62};
            for(int i=0,j=0;i < s.length;i++) {
                if ((s[i]>='a'&&s[i]<='z')||(s[i]>='A'&&s[i]<='Z')) {
                    Many_letters++;
                    s2[j]=s[i];
                    j++;
                }
            }
            register_CRC[0] = s2[0];
            register_CRC[1] = s2[1];
            register_CRC[2] = s2[Many_letters-2];
            register_CRC[3] = s2[Many_letters-1];
            code[0] = (char)(CRC_16_MODBUS(register_CRC) >> 8);
            code[1] = s2[0];
            code[2] = s2[1];;
            code[3] = s2[Many_letters-2];
            code[4] = s2[Many_letters-1];
            code[5] = (char)(CRC_16_MODBUS(register_CRC)&0xff);
            return code;


        }else {
            return code;
        }
    }
    /****************************************************************
     * 函 数 名 ：  用于交通灯识别、图形识别提取众数
     * 参    数 ：  check_code：需要提取中枢的数据数组 识别的数字0-14
     * 返 回 值 ： 众数
     * 全局变量 ：  无
     * 备    注 ：   2021/1/7
     ****************************************************************/
    public static int Extract_mode(int check_code[]){
        if (check_code.length != 1){
            int mode_sort[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
            int mode = 0;
            for (int i=0;i<check_code.length;i++){
                if(check_code[i]>=0 && check_code[i]<=14) {
                    mode_sort[check_code[i]]++;
                }else{
                    Log.w("mode", "Extract_mode: "+check_code[i]);
                }
            }
            for (int i=1;i<mode_sort.length-1;i++){
                if (mode_sort[i]<mode_sort[mode]){
                }else if(mode_sort[i]>mode_sort[mode]){
                    mode = i;
                }else if((mode_sort[i]==mode_sort[mode])&&mode_sort[i]!=-1){
                    mode = i;
                    Log.i("mode", "Extract_mode: "+i);
                    DataShow("众数提取异常，出现多众数"+i+"和"+(i+1)+" 取"+i);
                }
            }
            return mode;
        }else {
            return -1;
        }

    }

    /****************************************************************
     * 函 数 名 ：  CRC校验 2018年国赛
     * 参    数 ：  check_code：需要校验的数据数组 length：数组长度
     * 返 回 值 ： 校验计算结果
     * 全局变量 ：  无
     * 备    注 ：  （CRC-16/MODBUS） 2020/12/28
     ****************************************************************/
    public static int CRC_16_MODBUS(char check_code[])
    {
        int i;
        int polynomial = 0xA001;		//多项式
        int CRC_register = 0xFFFF;		//初始值
        char displacement = 0;			//位移值
        for (i = 0; i < check_code.length; i++)
        {
            CRC_register = CRC_register ^ check_code[i];
            while (true)
            {

                if ((CRC_register & 1) == 1)//LSB为1时
                {
                    CRC_register = CRC_register >> 1;
                    displacement += 1;
                    CRC_register = CRC_register ^ polynomial;
                }
                else if ((CRC_register & 1) == 0)//LSB为0时
                {
                    CRC_register = CRC_register >> 1;
                    displacement += 1;
                }
                if (displacement > 7)
                {
                    displacement = 0;
                    break;
                }
            }
        }
        return CRC_register;
    }
    /****************************************************************
     * 函 数 名 ： 偶合算法 2020年福建省赛
     * 参    数 ：  result：识别二维码结果
     * 返 回 值 ： 1成功 or 0失败
     * 全局变量 ：  无
     * 备    注 ：  2020/12/28
     ****************************************************************/
    int Fujian2020OuheFireCode(String result) {
        int calculate = 1;
        if (result != null) {
            char[] s = result.toCharArray();
            int calculateAr[] = {5, 7, 9, 11, 13, 15};
            int caFlag = 0;
            for (int ii = 0, j = 0; ii < s.length; ii++) {
                if ((s[ii] - 48) % 2 == 0 && s[ii] >= '0' && s[ii] <= '9'&& (s[ii] - 48) !=0) {
                    calculate = calculate * (s[ii] - 48);
                }
            }
            Log.d("QRA", "Fujian2020OuheFireCode: "+calculate);
            for (int ii = calculate; ii > 1; ii--) {
                for (int iii = ii - 1; iii > 0; iii--) {
                    if (ii % iii != 0) {
                        if (iii == 2) {
                            caFlag = ii;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (caFlag != 0) {
                    break;
                }
            }
            if (caFlag != 0) {
                for (int iii = 0; iii < 6; iii++) {
                    calculateAr[iii] = (caFlag % calculateAr[iii]);
                }

                DataShow("二维码识别结果：" + calculateAr[0] + "," + calculateAr[1] + "," + calculateAr[2] + "," + calculateAr[3] + "," + calculateAr[4] + "," + calculateAr[5]);
                Sendtool.sendData(0xA2,calculateAr[0],calculateAr[1],calculateAr[2]);
                Sendtool.Send.yanchi(1500);
                Sendtool.sendData(0xA3,calculateAr[3],calculateAr[4],calculateAr[5]);
                return 1;
            }
        }
        return 0;
    }

    /**
     功   能: BLF小车任务用
     参   数: str:要进行BLF编码的字符串
     返回值:解出后的六位ASCII码数组
     */
    public static char[] BLF(String str)
    {
        char[] data = new char[6];
        char[][] jz = new char[5][5];
        char[] zm = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','R','S','T','U','V','W','X','Y','Z'};
        char[] str_data = new char[50];
        char[] str_data_temp = new char[50];
        char[] temp = new char[50];//明文
        char[] temp1 = new char[50];//密匙
        int flag=0;
        int flag1=0;
        int x1=0,x2=0,y1 = 0,y2=0;

        str_data = str.toCharArray();
        for(char i:str_data)//提取出字符串中的明文和密匙(算法步骤1)
        {
            if(i == '|')
            {
                flag =	1;
                flag1 = 0;
                continue;
            }

            if(flag == 0 && i != '<' && i != '>')
            {
                temp[flag1] = i;
                flag1++;
            }

            if(flag == 1 && i != '<' && i != '>')
            {
                temp1[flag1] = i;
                flag1++;
            }
        }

        flag=0;
        flag1=0;

        for(char i:temp1)//过滤密匙(算法步骤2)
        {
            flag1=0;

            for(char j:str_data_temp)
            {
                if(j==i)
                {
                    flag1=1;
                    break;
                }
            }

            if(i == '\0')
                break;
            if(flag1 == 1 || (i>='0' && i<= '9'))
                continue;

            str_data_temp[flag] = i;
            flag++;
        }

        if(flag<25)//在密匙不足25位时填充置25位(算法步骤2)
        {
            for(char i:zm)//过滤密匙,在不足25位时填充置25位
            {
                flag1=0;

                if(flag >= 25)
                    break;

                for(char j:str_data_temp)
                {
                    if(j==i || j==(char)((int)i+32))
                    {
                        flag1=1;
                        break;
                    }
                }

                if(flag1 == 1 || (i>='0' && i<= '9'))
                    continue;

                str_data_temp[flag] = i;
                flag++;
            }

        }

        flag=0;
        for(int i=0;i<5;i++)//将密匙填入矩阵(算法步骤2)
        {
            for(int j=0;j<5;j++)
            {
                if((int)str_data_temp[flag] != '\0')
                {
                    if(str_data_temp[flag] >= 'A' && str_data_temp[flag] <= 'Z')
                    {
                        jz[i][j] = str_data_temp[flag];
                        flag++;
                    }
                    else
                    {
                        jz[i][j] = (char) ((int)str_data_temp[flag] - 32);
                        flag++;
                    }

                }
            }
        }

        flag=0;

        for(char i:temp)
        {
            if(i=='\0')
                break;
            flag++;
        }

        if(flag%2 == 1)
            temp[flag] = 'X';

        for(char i=0,j=1; temp[j]!='\0'; i+=2,j+=2)//查找明文在矩阵中的值并转换为密文
        {
            flag = flag1 = 0;

            for(x1=0;x1<5;x1++)
            {
                for(y1=0;y1<5;y1++)
                {
                    if(jz[x1][y1]==temp[i] || jz[x1][y1]==(char)((int)temp[i]-32))
                    {
                        flag=1;
                        break;
                    }
                }
                if(flag==1)
                    break;
            }

            flag=0;
            for(x2=0;x2<5;x2++)
            {
                for(y2=0;y2<5;y2++)
                {
                    if(jz[x2][y2]==temp[j] || jz[x2][y2]==(char)((int)temp[j]-32))
                    {
                        flag=1;
                        break;
                    }
                }
                if(flag==1)
                    break;
            }

            if(x1 == x2)
            {
                if(y1 != 4)
                    temp[i] = jz[x1][y1+1];
                else
                    temp[i] = jz[x1][0];
                if(y2!= 4)
                    temp[j] = jz[x1][y1+1];
                else
                    temp[j] = jz[x1][0];
            }
            else if(y1 == y2)
            {
                if(x1 != 4)
                    temp[i] = jz[x1+1][y1];
                else
                    temp[i] = jz[0][y1];
                if(x2!= 4)
                    temp[j] = jz[x2+1][y2];
                else
                    temp[j] = jz[0][y2];
            }
            else
            {
                temp[i] = jz[x1][y2];
                temp[j] = jz[x2][y1];
            }
        }

        data[0] = temp[0];
        data[1] = temp[1];
        data[2] = temp[2];

        flag=0;
        for(char i:temp)
        {
            if(i=='\0')
                break;

            flag++;
        }

        data[3] = temp[flag-3];
        data[4] = temp[flag-2];
        data[5] = temp[flag-1];

        return data;

    }

    /**
     功   能: TFT交通标志识别
     参   数: target所需要搜寻到的交通标志
     返回值：每个交通标志物对应的数字
     1-未查找到文件
     02-掉头
     03-右转
     04-直行
     05-左转
     06-禁止通行
     07-禁止直行
     */
    /**
     * 特殊数据处理函数
     * @return 无
     */
    //public static int True_jx = -1, True_lx = -1, True_wj = -1, True_yx = -1,True_sj = -1;
    public static void Ts_Go(int HL,int tft_tx,int tft_tfa)
    {
        if(HL <= 0)//数值减少
        {
            if(tft_tx >= 1)
            {
                switch(tft_tx)
                {
                    case 1:
                        True_jx--;
                        DataShow("矩形数据减少，当前矩形数据为:"+True_jx);
                        break;
                    case 2:
                        True_lx--;
                        DataShow("菱形数据减少，当前菱形数据为:"+True_lx);
                        break;
                    case 3:
                        True_lx--;
                        DataShow("五角数据减少，当前五角数据为:"+True_wj);
                        break;
                    case 4:
                        True_yx--;
                        DataShow("圆形数据减少，当前圆形数据为:"+True_yx);
                        break;
                    case 5:
                        True_sj--;
                        DataShow("三角数据减少，当前三角数据为:"+True_sj);
                        break;
                }
                DataShow("矩形:"+True_jx+"菱形:"+True_lx+"五角:"+True_wj+"圆形:"+True_yx+"三角:"+True_sj);
            }
            else
            {
                True_tra--;

                switch(True_tra)
                {
                    case 0x02:
                        DataShow("当前交通标志物数据为0x02(掉头)");
                        break;
                    case 0x03:
                        DataShow("当前交通标志物数据为0x03(右转)");
                        break;
                    case 0x04:
                        DataShow("当前交通标志物数据为0x04(直行)");
                        break;
                    case 0x05:
                        DataShow("当前交通标志物数据为0x05(左转)");
                        break;
                    case 0x06:
                        DataShow("当前交通标志物数据为0x06(禁止通行)");
                        break;
                    case 0x07:
                        DataShow("当前交通标志物数据为0x07(禁止直行)");
                        break;
                    default:
                        DataShow("当前交通标志物数值未被收录，数值为:"+True_tra);
                        break;
                }
            }
        }
        else//数值增加
        {
            if(tft_tx >= 1)
            {
                switch(tft_tx)
                {
                    case 1:
                        True_jx++;
                        DataShow("矩形数据增加，当前矩形数据为:"+True_jx);
                        break;
                    case 2:
                        True_lx++;
                        DataShow("菱形数据增加，当前菱形数据为:"+True_lx);
                        break;
                    case 3:
                        True_wj++;
                        DataShow("五角数据增加，当前五角数据为:"+True_wj);
                        break;
                    case 4:
                        True_yx++;
                        DataShow("圆形数据增加，当前圆形数据为:"+True_yx);
                        break;
                    case 5:
                        True_sj++;
                        DataShow("三角数据增加，当前三角数据为:"+True_sj);
                        break;
                }
                DataShow("矩形:"+True_jx+"菱形:"+True_lx+"五角:"+True_wj+"圆形:"+True_yx+"三角:"+True_sj);
            }
            else
            {
                True_tra++;
                switch(True_tra)
                {
                    case 0x02:
                        DataShow("当前交通标志物数据为0x02(掉头)");
                        break;
                    case 0x03:
                        DataShow("当前交通标志物数据为0x03(右转)");
                        break;
                    case 0x04:
                        DataShow("当前交通标志物数据为0x04(直行)");
                        break;
                    case 0x05:
                        DataShow("当前交通标志物数据为0x05(左转)");
                        break;
                    case 0x06:
                        DataShow("当前交通标志物数据为0x06(禁止通行)");
                        break;
                    case 0x07:
                        DataShow("当前交通标志物数据为0x07(禁止直行)");
                        break;
                    default:
                        DataShow("当前交通标志物数值未被收录，数值为:"+True_tra);
                        break;
                }
            }
        }
    }

    /**
     * 判断是否是数字字符
     * @return 布尔类型
     */
    public static boolean digit(char d){
        boolean dig = true;
        if (d<'0'||d>'9'){
            dig = false;
        }
        return dig;
    }
    /**
     * 将char转为无符号整型
     * @return 无符号整型
     */
    public static int  Car1noSign(byte c){
        int s = c;
        if(s<0){
            s=256+s;
        }
        return s;
    }
    /**
     * 将10进制整型转为16进制字符串 （使用1个字节表示）
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex8(int b) {
        return String.format("%02x", b);
    }
    /**
     * 将10进制整型转为16进制字符串 （使用2个字节表示）
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }
    /**
     * 将10进制整型转为16进制字符串 （使用4个字节表示）
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex32(int b) {
        return String.format("%08x", b);
    }
    /**
     * 将10进制整型转为16进制字符串 （使用8个字节表示）
     * @param b 10进制整型
     * @return 16进制字符串
     */
    public static String numToHex64(long b) {
        return String.format("%016x", b);
    }
    /**
     * 获取16进制的底图标签值 （使用2个字节表示）
     * @return 16进制底图标签值  显示0x00-0xFF 适用于数据显示
     */
    public static String hexString8(int b) {
        return "0x".concat(numToHex8(b));
    }
    /**
     * 获取16进制的底图标签值 （使用2个字节表示）
     * @return 16进制底图标签值
     */
    public static String hexString16(int b) {
        return "0x".concat(numToHex16(b));
    }
    /**
     * 获取16进制的底图标签值 （使用4个字节表示）
     * @return 16进制底图标签值
     */
    public static String hexString32(int b) {
        return "0x".concat(numToHex32(b));
    }
    /**
     * 获取16进制的底图标签值 （使用4个字节表示）
     * @return 16进制底图标签值
     */
    public static String hexString64(long b) {
        return "0x".concat(numToHex64(b));
    }
}
