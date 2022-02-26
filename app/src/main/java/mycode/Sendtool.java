package mycode;

import android.util.Log;
import car.bkrc.right.fragment.LeftFragment;
import car2017_demo.ConnectTransport;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* 说明：本类封装了由主车直接转发数据给设备的一些静态方法
*
* void preSetCammera(int preSet,int delayTime)；                                          跳转到预设的的摄像头，preSet为预设的摄像头，delayTime为延时时间
* void send5data(int data1,int data2,int data3,int data4,int data5)                       发送5位数据
* void sendData(byte data1,byte data2,byte data3,byte data4)                              发送给主车数据，一个主指令，三个副指令
* public void send_six(byte one, byte two, byte thrid, byte four, byte five,byte six)     发送六个数据给主车
* void preSetCammera(int preSet,int delayTime)     调用预设位并且设置延时时间
* void page_down(int equipment,int command)       TFT翻页函数，equipment 1为TFT-A，2为TFT-B；command  0x01为上翻，0x02为下翻，0x03为每10秒自动下翻
* void RecognitionMode(int i)                     控制交通灯进入识别模式，参数i为1 则控制交通灯A 为2则控制交通灯B
* void sendTrafficResult(byte equipment,byte result)equipment 1和2分变代表给交通灯A和B发送识别结果,result为识别结果。
* * * * * * * * * * * * * * * * * * * * * * * * ** * * * *  * *  */

public class Sendtool{
    static ConnectTransport Send=new ConnectTransport();
    public static void send5data(int data1,int data2,int data3,int data4,int data5)
    {
        Send.TYPE=(byte)data1;
        Send.MAJOR=(byte)data2;
        Send.FIRST=(byte)data3;
        Send.SECOND=(byte)data4;
        Send.THRID=(byte)data5;
        Send.send();
    }
    public static void sendData(int data1,int data2,int data3,int data4){
        Send.TYPE=0xAA;
        Send.MAJOR=(byte)data1;
        Send.FIRST=(byte)data2;
        Send.SECOND=(byte)data3;
        Send.THRID=(byte)data4;
        Send.send();
    }
    public static  void send_six(int onedata,int twodata,int one, int two, int thrid, int four, int five, int six) {
        Send.TYPE = 0xAA;
        Send.MAJOR = (byte)onedata;
        Send.FIRST = (byte)one;
        Send.SECOND = (byte)two;
        Send.THRID = (byte)thrid;
        Send.send();
        Send.yanchi(1000);
        Send.MAJOR = (byte)twodata;
        Send.FIRST = (byte)four;
        Send.SECOND = (byte)five;
        Send.THRID = (byte)six;
        Send.send();
    }
    public static void StartUp(){
        Send.TYPE=0xAA;
        Send.MAJOR = (short) 0xA0;
        Send.send();
    }
    /************************************************************************
     * 调用预设位函数
     * preSet 预设位号数    delayTime  延时时间
     * ***********************************************************************/
    public static void preSetCammera(int preSet,int delayTime) {
        LeftFragment.camera_position_preset = preSet+10;
        Send.yanchi(delayTime);
    }
    /************************************************************************
     * 设置预设位函数
     * preSet 上下左右    delayTime  延时时间
     * ***********************************************************************/
    public static void CammeraDirection(int preSet,int delayTime) {
        LeftFragment.camera_position_preset = preSet;
        Send.yanchi(delayTime);
    }

    /********************************************
     *   TFT下翻函数
     *   参数：equipment 1为TFT-A，2为TFT-B；command  0x01为上翻，0x02为下翻，0x03为每10秒自动下翻
     * *************************************************/
    public static void page_down(int equipment,int command)
    {
        if(equipment==1)
        {
            Send.TYPE=0x0B;
        }
        else if(equipment==2)
        {
            Send.TYPE=0x08;
        }
        else
        {
            Log.i("kk","TFT翻页参数错误");
        }
        Send.MAJOR =  0x10;
        Send.FIRST =  (byte)command;
        Send.SECOND =  0x00;
        Send.THRID =0x00;
        Send.send();
        Log.i("kk","TFT翻页");
    }
    public static void page_down(char equipment,int command)
    {
        if(equipment=='A')
        {
            Send.TYPE=0x0B;
        }
        else if(equipment=='B')
        {
            Send.TYPE=0x08;
        }
        else
        {
            Log.i("kk","TFT翻页参数错误");
        }
        Send.MAJOR =  0x10;
        Send.FIRST =  (byte)command;
        Send.SECOND =  0x00;
        Send.THRID =0x00;
        Send.send();
        Log.i("kk","TFT翻页");
    }


    public static void RecognitionMode(int i)//参数xx用来指定给哪个交通灯发数据
    {
        if (i==1) {
            Log.i("kk", "到达交通灯A，进入识别");
            Send.TYPE = 0x0E;
        }
        else if(i==2) {
            Log.i("kk", "到达交通灯B，进入识别");
            Send.TYPE = 0x0F;
        }
        Send.MAJOR = 0x01;
        Send.FIRST = 0x00;
        Send.SECOND = 0x00;
        Send.send();
        Send.yanchi(100);
        Send.send();
    }
    public static void sendTrafficResult(byte equipment,byte result) {
        if(equipment==1) {
            send5data(0x0E,0x02,result,0x00,0x00);
        }
        else {
            send5data(0x0F,0x02,result,0x00,0x00);
        }
    }

}
