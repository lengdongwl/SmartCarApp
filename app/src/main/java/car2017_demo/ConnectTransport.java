package car2017_demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bkrc.camera.XcApplication;
import com.daiyinger.carplate.CarPlateDetection;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import android.serialport.SerialPort;

import car.bkrc.com.car2018.FirstActivity;
import car.bkrc.right.fragment.LeftFragment;
import car.bkrc.right.fragment.RightFragment1;
import mycode.AllTask;
import mycode.Sendtool;
import mycode.QR_Recognition;

public class ConnectTransport {
    public static DataInputStream bInputStream = null;
    public static DataOutputStream bOutputStream = null;
    public static Socket socket = null;
    public byte[] rbyte = new byte[50];
    private Handler reHandler;
    public short TYPE = 0xAA;
    public short MAJOR = 0x00;
    public short FIRST = 0x00;
    public short SECOND = 0x00;
    public short THRID = 0x00;
    public short CHECKSUM = 0x00;

    private static OutputStream SerialOutputStream;
    private InputStream SerialInputStream;
    private boolean Firstdestroy = false;  ////Firstactivity 是否已销毁了
    private String Tag;

    public void destory() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                bInputStream.close();
                bOutputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void connect(Handler reHandler, String IP) {
        try {
            this.reHandler = reHandler;
            Firstdestroy = false;
            int port = 60000;
            socket = new Socket(IP, port);
            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            reThread.start();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void serial_connect(Handler reHandler) {
        this.reHandler = reHandler;
        try {
            int baudrate = 115200;
            String path = "/dev/ttyS4";
            SerialPort mSerialPort = new SerialPort(new File(path), baudrate, 0);
            SerialOutputStream = mSerialPort.getOutputStream();
            SerialInputStream = mSerialPort.getInputStream();
            //new Thread(new SerialRunnable()).start();
            //reThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XcApplication.executorServicetor.execute(new SerialRunnable());
        //new Thread(new serialRunnable()).start();
    }

    byte[] serialreadbyte = new byte[50];

    class SerialRunnable implements Runnable {
        @Override
        public void run() {
            while (SerialInputStream != null) {
                try {
                    int num = SerialInputStream.read(serialreadbyte);
                    // String  readserialstr =new String(serialreadbyte);
                    String readserialstr = new String(serialreadbyte, 0, num, "utf-8");
                    Log.e("----serialreadbyte----", "******" + readserialstr);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = serialreadbyte;
                    reHandler.sendMessage(msg);

                    /*
                    for (int i = 0; i < num; i++) {
                        Log.e("----serialreadbyte----", "******" +Integer.toHexString(serialreadbyte[i]));
                      //  Log.e("----serialreadbyte----", "******" + serialreadbyte[i]);
                    }
                    */
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Thread reThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // TODO Auto1-generated method stub
            while (socket != null && !socket.isClosed()) {
                if (Firstdestroy == true)  //Firstactivity 已销毁了
                {
                    break;
                }
                try {
                    bInputStream.read(rbyte);
                    Log.i("receivedata,",bytes2HexString(rbyte));
                    if(rbyte[2]==(byte)0xF0||rbyte[2]==(byte)0xF1||rbyte[2]==(byte)0xF2)
                    {
                        if(rbyte[2]==(byte) 0xF0)
                        {
                            Message msg = LeftFragment.showidHandler.obtainMessage(31);
                            msg.sendToTarget();
                        }
                        if(rbyte[2]==(byte)0xF1)
                        {
                            Message msg = LeftFragment.showidHandler.obtainMessage(32,rbyte);
                            msg.sendToTarget();
                        }
                        if(rbyte[2]==(byte)0xF2)
                        {
                            Message msg = LeftFragment.showidHandler.obtainMessage(33,rbyte);
                            msg.sendToTarget();
                        }
                    }else
                    {
                        AllTask.automatic(rbyte);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = rbyte;
                    reHandler.sendMessage(msg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            rbyte.clone();//清除缓存区数据
        }
    });

    public void send() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // 发送数据字节数组

        final byte[] sbyte = {0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};
//        for(int i=1;i<8;i++)
//        Log.i("kk",""+sbyte[i]);

        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(sbyte, 0, sbyte.length);
                            bOutputStream.flush();
                            Log.i("wifi_send",bytes2HexString(sbyte));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("wifi_send","发送异常"+e.toString());

                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(sbyte, 0, sbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(sbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void send_voice(final byte[] textbyte) {
        if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if (socket != null && !socket.isClosed()) {
                            bOutputStream.write(textbyte, 0, textbyte.length);
                            bOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.SERIAL) {

            XcApplication.executorServicetor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        SerialOutputStream.write(textbyte, 0, textbyte.length);
                        SerialOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL)
            try {
                FirstActivity.sPort.write(textbyte, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }

				/*
				try {
					// ���������ֽ�����
					if (socket != null && !socket.isClosed()) {
						bOutputStream.write(textbyte, 0, textbyte.length);
						bOutputStream.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				*/
    }


    // 前进
    public void go(int sp_n, int en_n) {
        MAJOR = 0x02;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }

    // 后退
    public void back(int sp_n, int en_n) {
        MAJOR = 0x03;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }


    //左转
    public void left(int sp_n,int en_n) {
        MAJOR = 0x04;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }


    // 右转
    public void right(int sp_n,int en_n) {
        MAJOR = 0x05;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }

    // 停车
    public void stop() {
        MAJOR = 0x01;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 循迹
    public void line(int sp_n) {  //寻迹
        MAJOR = 0x06;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //清除码盘值
    public void clear() {
        MAJOR = 0x07;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void vice(int i) {//主从车状态转换
        short temp = TYPE;
        if (i == 1) {//从车状态
            TYPE = 0x02;
            MAJOR = 0x80;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            yanchi(500);

            TYPE = (byte) 0xAA;
            MAJOR = 0x80;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            TYPE = 0x02;
        } else if (i == 2) {// 主车状态
            TYPE = 0x02;
            MAJOR = 0x80;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            yanchi(500);

            TYPE = (byte) 0xAA;
            MAJOR = 0x80;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            TYPE = 0xAA;
        }
        TYPE = temp;
    }

    // 红外
    public void infrared(byte one, byte two, byte thrid, byte four, byte five,
                         byte six) {
        MAJOR = 0x10;
        FIRST = one;
        SECOND = two;
        THRID = thrid;
        send();
        yanchi(500);
        MAJOR = 0x11;
        FIRST = four;
        SECOND = five;
        THRID = six;
        send();
        yanchi(500);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(1000);
    }

    // 双色led灯
    public void lamp(byte command) {
        MAJOR = 0x40;
        FIRST = command;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 指示灯
    public void light(int left, int right) {
        if (left == 1 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 1 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
    }


    // 蜂鸣器
    public void buzzer(int i) {
        if (i == 1)
            FIRST = 0x01;
        else if (i == 0)
            FIRST = 0x00;
        MAJOR = 0x30;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void gear(int i) {// 加光照档位
        if (i == 1)
            MAJOR = 0x61;
        else if (i == 2)
            MAJOR = 0x62;
        else if (i == 3)
            MAJOR = 0x63;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //立体显示
    public void infrared_stereo(short[] data) {
        MAJOR = 0x10;
        FIRST = 0xff;
        SECOND = data[0];
        THRID = data[1];
        send();
        yanchi(500);
        MAJOR = 0x11;
        FIRST = data[2];
        SECOND = data[3];
        THRID = data[4];
        send();
        yanchi(500);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(500);
    }

    //智能交通灯
    public void traffic_control(int type,int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short)type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    //立体车库控制
    public void garage_control(int type,int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = (short)type;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    //openmv摄像头
    public void opencv_control(int major, int first) {
        byte temp = (byte) TYPE;
        TYPE = 0x02;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void gate(int major, int first, int second, int third) {// 闸门
        byte temp = (byte) TYPE;
        TYPE = 0x03;
        MAJOR = (byte) major;
        FIRST = (byte) first;
        SECOND = (byte) second;
        THRID = (byte) third;
        send();
        TYPE = temp;
    }

    //LCD 显示标志物进入计时模式
    public void digital_close() {//数码管关闭
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_open() {//数码管打开
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x01;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_clear() {//数码管清零
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x02;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = temp;
    }

    public void digital_dic(int dis) {//LCD显示标志物第二排显示距离

        byte temp = (byte) TYPE;
        int a = 0, b = 0, c = 0;
        a = (dis / 100) & (0xF);
        b = (dis % 100 / 10) & (0xF);
        c = (dis % 10) & (0xF);
        b = b << 4;
        b = b | c;
        TYPE = 0x04;
        MAJOR = 0x04;
        FIRST = 0x00;
        SECOND = (short) (a);
        THRID = (short) (b);
        send();
        TYPE = temp;
    }

    public void digital(int i, int one, int two, int three) {// 数码管
        byte temp = (byte) TYPE;
        TYPE = 0x04;
        if (i == 1) {//数据写入第一排数码管
            MAJOR = 0x01;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        } else if (i == 2) {//数据写入第二排数码管
            MAJOR = 0x02;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        }
        send();
        TYPE = temp;
    }

    public void VoiceBroadcast()  //语音播报随机指令
    {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x06;
        MAJOR = (short) 0x20;
        FIRST = (byte) 0x01;
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
        TYPE = temp;
    }

    public void TFT_LCD(int type,int MAIN, int KIND, int COMMAD, int DEPUTY)  //tft lcd
    {
        byte temp = (byte) TYPE;
        TYPE = (short) type;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = temp;
    }

    public void magnetic_suspension(int MAIN, int KIND, int COMMAD, int DEPUTY) //磁悬浮
    {
        byte temp = (byte) TYPE;
        TYPE = (short) 0x0A;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = temp;
    }

    // 沉睡
    public static void yanchi(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex+" ";
        }
        return ret;
    }

    //将二维码数据上半部分转发给主车
    public void qr_code_dataup(int data_1,int data_2,int data_3) {
        MAJOR = 0xA2;
        FIRST = (short) data_1;
        SECOND = (short) data_2;
        THRID = (short) data_3;
        send();
    }

    //将二维码数据下半部分转发给主车
    public void qr_code_datalow(int data_1,int data_2,int data_3) {
        MAJOR = 0xA3;
        FIRST = (short) data_1;
        SECOND = (short) data_2;
        THRID = (short) data_3;
        send();
    }
    //新二维码识别
    public void qr(String str)
    {

        try
        {
            switch (str.length()) //识别长度决定发送长短 大于大于6就发送前六位
            {
                case 1:
                    MAJOR = 0xA2;
                    FIRST = (byte) str.charAt(0);
                    send();
                    break;
                case 2:
                    MAJOR = 0xA2;
                    FIRST = (byte) str.charAt(0);
                    SECOND = (byte) str.charAt(1);
                    send();
                    break;
                case 3:
                    MAJOR = 0xA2;
                    FIRST = (byte) str.charAt(0);
                    SECOND = (byte) str.charAt(1);
                    THRID = (byte) str.charAt(2);
                    send();
                    break;
                case 4:
                    MAJOR = 0xA2;
                    FIRST = (byte) str.charAt(0);
                    SECOND = (byte) str.charAt(1);
                    THRID = (byte) str.charAt(2);
                    send();
                    MAJOR = 0xA3;
                    FIRST = (byte) str.charAt(3);
                    send();
                    break;
                case 5:
                    MAJOR = 0xA2;
                    FIRST = (byte) str.charAt(0);
                    SECOND = (byte) str.charAt(1);
                    THRID = (byte) str.charAt(2);
                    send();
                    MAJOR = 0xA3;
                    FIRST = (byte) str.charAt(3);
                    SECOND = (byte) str.charAt(4);
                    send();
                    break;
                default:
                    MAJOR = 0xA2;
                    FIRST = (byte) str.charAt(0);
                    SECOND = (byte) str.charAt(1);
                    THRID = (byte) str.charAt(2);
                    send();
                    MAJOR = 0xA3;
                    FIRST = (byte) str.charAt(3);
                    SECOND = (byte) str.charAt(4);
                    THRID = (byte) str.charAt(5);
                    send();
            }
        }
        catch (Exception e)
        {

        }
    }
    //将车牌数据上半部分转发给主车
    public void car_code_dataup(int data_1,int data_2,int data_3) {
        MAJOR = 0x40;
        FIRST = (short) data_1;
        SECOND = (short) data_2;
        THRID = (short) data_3;
        send();
    }

    //将车牌数据下半部分转发给主车
    public void car_code_datalow(int data_1,int data_2,int data_3) {
        MAJOR = 0x41;
        FIRST = (short) data_1;
        SECOND = (short) data_2;
        THRID = (short) data_3;
        send();
    }

    //将图形数据上半部分转发给主车
    public void shape_code_dataup(int data_1,int data_2,int data_3) {
        MAJOR = 0x45;
        FIRST = (short) data_1;
        SECOND = (short) data_2;
        THRID = (short) data_3;
        send();
    }

    //将图形数据下半部分转发给主车
    public void shape_code_datalow(int data_1,int data_2,int data_3) {
        MAJOR = 0x46;
        FIRST = (short) data_1;
        SECOND = (short) data_2;
        THRID = (short) data_3;
        send();
    }

    //交通灯识别函数
    //返回结果为1时，为红色
    //返回结果为2时，为绿色
    //返回结果为3时，为黄色
    public int trafficlight()
    {
        int i = AllTask.trafficlight_A();

        return i;
    }


    public void mByte_null(byte[] mByte)
    {
        int i ;

        for(i=0;i<7;i++)
        {
            mByte[i] = 0;
        }
    }

    //自动识别一次交通灯数据，并转发给主车
    public void trafficlight_data()
    {
        int i;
        String r="";
        try {


            i = trafficlight();

            if (i == 1) {
                FirstActivity.connectTransport.traffic_control(0x0E, 0x02, 0x01);
                r = "红灯";
            }
            if (i == 2) {
                FirstActivity.connectTransport.traffic_control(0x0E, 0x02, 0x02);
                r = "绿灯";
            }
            if (i == 3) {
                FirstActivity.connectTransport.traffic_control(0x0E, 0x02, 0x03);
                r = "黄灯";
            }
            AllTask.DataShow("交通灯识别结果：" + r + "\n");
        }catch (Exception e)
        {
            AllTask.DataShow("发送识别交通灯失败");
        }
    }

    //二维码识别
    private String result_qr=null;
    private Bitmap qrBitmap=null;
    private int qrCount = 0;
    /**
     * 图像灰度化
     *
     * @param bmSrc
     * @return
     */
    public Bitmap bitmap2Gray(Bitmap bmSrc) {
    // 得到图片的长和宽
    if (bmSrc == null)
        return null;
    int width = bmSrc.getWidth();
    int height = bmSrc.getHeight();
    // 创建目标灰度图像
    Bitmap bmpGray = null;
    bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    // 创建画布
    Canvas c = new Canvas(bmpGray);
    Paint paint = new Paint();
    ColorMatrix cm = new ColorMatrix();
    cm.setSaturation(0);
    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
    paint.setColorFilter(f);
    c.drawBitmap(bmSrc, 0, 0, paint);
    return bmpGray;
    }
    public void QRRecon() {
        Result result;
        AllTask.DataShow("开始二维码识别");
        try {
            qrBitmap = LeftFragment.bitmap;

            if (qrBitmap != null) {

                QR_Recognition rSource = new QR_Recognition(bitmap2Gray(qrBitmap));

                BinaryBitmap binaryBitmap = new BinaryBitmap(
                        new HybridBinarizer(rSource));
                Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
                hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
                QRCodeReader reader = new QRCodeReader();
                result = reader.decode(binaryBitmap, hint);
                if (result.toString() != null) {
                    result_qr = result.toString();
                    qr(result_qr);
                    qr(result_qr);//二次发送以免丢包
                    AllTask.DataShow("二维码识别成功");
                    qrCount=0;
                }else
                {
                    AllTask.DataShow("二维码识别失败");
                    qrCount++;
                }

            }else
            {
                AllTask.DataShow("二维码识别失败");
                qrCount++;
            }


        }catch(Exception e){
            AllTask.DataShow("二维码识别异常");
            qrCount++;
        }
        if (qrCount!=0 &&qrCount<3)//识别失败再次识别
        {
            QRRecon();

        }else{
            qrCount=0;
        }
    }

/*


    public void auto()
    {
        byte[] mByte = new byte[50];
        while(true) {

            mByte = RightFragment1.data_transmit();
            RightFragment1.show();
            yanchi(1000);

            switch (mByte[2] )
            {
                case (byte) 0xb0:

                        TFTA();

                        break;
                case (byte)0xb1:
                        TFTB();


                        break;
                case (byte)0xb2:

                        trafficlight_data();

                    break;
                case (byte)0xb3:

                        trafficlight_data();

                    break;
                case (byte)0xb4:
                        QRRecon();

                    break;
                case (byte)0xb5:
                        QRRecon();

                    break;
            }
            mByte_null(mByte);
        }

    }
*/
}
