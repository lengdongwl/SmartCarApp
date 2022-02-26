package car.bkrc.right.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bkrc.camera.XcApplication;
import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.daiyinger.carplate.CarPlateDetection;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import car.bkrc.com.car2018.FileService;
import car.bkrc.com.car2018.FirstActivity;
import car.bkrc.com.car2018.LoginActivity;
import car.bkrc.com.car2018.R;
import car2017_demo.ConnectTransport;
import mycode.AllTask;
import mycode.Sendtool;

public class LeftFragment extends Fragment implements View.OnClickListener{

    private float x1 = 0;
    private float y1 = 0;
    private  ImageView image_show =null;
    // 摄像头工具
    public static CameraCommandUtil cameraCommandUtil;
    private static TextView dataShow =null;
    private static TextView yc =null;

    private Button state;
    private Button control;
    public static Handler btchange_handler;

    public static int camera_position_preset;
    // 开启线程接受摄像头当前图片
    private Thread controlCameraRotateThread = new Thread(new Runnable() {
        public void run() {

            while (true) {
                switch (camera_position_preset) {
                    case 1: // 摄像头向上移动
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 0, 1);
                        camera_position_preset = 0;
                        break;
                    case 2: // 摄像头向下移动
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 2, 1);
                        camera_position_preset = 0;
                        break;
                    case 3: // 摄像头向左移动
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 4, 1);
                        camera_position_preset = 0;
                        System.out.println("左");
                        break;
                    case 4: // 摄像头向右移动
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 6, 1);
                        camera_position_preset = 0;
                        System.out.println("右");
                        break;
                    // /预设位5到9
                    case 5: // 设置预设位1
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 30, 0);
                        camera_position_preset = 0;
                        break;
                    case 6: // 设置预设位2
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 32, 0);
                        camera_position_preset = 0;
                        break;
                    case 7: // 设置预设位3
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 34, 0);
                        camera_position_preset = 0;
                        break;
                    case 8: // 设置预设位4
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 36, 0);
                        camera_position_preset = 0;
                        break;
                    case 9: // 设置预设位5
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 38, 0);
                        camera_position_preset = 0;
                        break;
                    case 10: // 设置预设位6
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 40, 0);
                        camera_position_preset = 0;
                        break;
                    // /调用设位10到14
                    case 11: // 调用预设位1
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 31, 0);
                        camera_position_preset = 0;
                        break;
                    case 12: // 调用预设位2
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 33, 0);
                        camera_position_preset = 0;
                        break;
                    case 13: // 调用预设位3
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 35, 0);
                        camera_position_preset = 0;
                        break;
                    case 14: // 调用预设位4
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 37, 0);
                        camera_position_preset = 0;
                        break;
                    case 15: // 调用预设位5
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 39, 0);
                        camera_position_preset = 0;
                        break;
                    case 16: // 调用预设位6
                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 41, 0);
                        camera_position_preset = 0;
                        break;
                    default:
                        break;
                }

            }

        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = null;
        // 判断设备
        if (LoginActivity.isPad(getActivity()))
            view=inflater.inflate(R.layout.left_fragment,container,false);
        else
            view = inflater.inflate(R.layout.left_fragment_mobilephone,container,false);


        image_show =(ImageView)view.findViewById(R.id.img);
        dataShow =(TextView) view.findViewById(R.id.showip);
        dataShow.setMovementMethod(ScrollingMovementMethod.getInstance());
        yc =(TextView) view.findViewById(R.id.yc);
        image_show.setOnTouchListener(new ontouchlistener1());



        state = (Button) view.findViewById(R.id.state);
        control = (Button)view.findViewById(R.id.control);
        Button clear_coded_disc = (Button) view.findViewById(R.id.clear_coded_disc);



        state.setOnClickListener(this);
        control.setOnClickListener(this);
        clear_coded_disc.setOnClickListener(this);

        cameraCommandUtil = new CameraCommandUtil();

        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
                if (FirstActivity.IPCamera.equals("null:81")) return;

                // 显示线程
                while (true) {
                    getBitmap();
                }
            }
        });

        controlCameraRotateThread.start();

        btchange_handler =bt_handler;
        if(XcApplication.isserial == XcApplication.Mode.SOCKET)
         {
            dataShow.setText("WIFIIP:" + FirstActivity.IPCar + "\n" + "CameraIP:" + FirstActivity.purecameraip);

        }
        return view;
    }


    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
    //获取当前时间
    @SuppressLint("HandlerLeak")
    public static Handler showidHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what ==22)
            {
                dataShow.setText(msg.obj + "\n" + "CameraIP:" + FirstActivity.IPCamera);
            }

            if(msg.what==30) {
                dataShow.setText(dataShow.getText().toString()+"\n"+simpleDateFormat.format(new Date(System.currentTimeMillis()))+" "+msg.obj);
                int line = dataShow.getLineCount();
                if (line > 5) {//超出屏幕自动滚动显示(5是当前页面显示的最大行数)
                    int offset = dataShow.getLineCount() * dataShow.getLineHeight();
                    dataShow.scrollTo(0, offset - 5*dataShow.getLineHeight());
                }
            }
            if(msg.what==31) {
                dataShow.setText(dataShow.getText().toString() + "\n" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + " 主车数据：");
                int line = dataShow.getLineCount();
                if (line > 5) {//超出屏幕自动滚动显示(5是当前页面显示的最大行数)
                    int offset = dataShow.getLineCount() * dataShow.getLineHeight();
                    dataShow.scrollTo(0, offset - 5 * dataShow.getLineHeight());
                }
            }
            if(msg.what==32) {
                byte[] data=(byte[])msg.obj;
                String s="";
                for(int i=0;i<3;i++)
                {

                    s+=(" "+Integer.toHexString(data[i+3]>>>31==1?(data[i+3]&0x7f+0x80):data[i+3]));
                }
                dataShow.setText(dataShow.getText().toString()+s);
                int line = dataShow.getLineCount();
                if (line > 5) {//超出屏幕自动滚动显示(5是当前页面显示的最大行数)
                    int offset = dataShow.getLineCount() * dataShow.getLineHeight();
                    dataShow.scrollTo(0, offset - 5 * dataShow.getLineHeight());
                }
            }
            if(msg.what==33)
            {
                byte[] temp=(byte[])msg.obj;
                byte[] data =new byte[3];
                for(int i=0;i<3;i++)
                {
                    data[i]=temp[i+3];
                }
                dataShow.setText(dataShow.getText().toString()+new String(data));
                int line = dataShow.getLineCount();
                if (line > 5) {//超出屏幕自动滚动显示(5是当前页面显示的最大行数)
                    int offset = dataShow.getLineCount() * dataShow.getLineHeight();
                    dataShow.scrollTo(0, offset - 5 * dataShow.getLineHeight());
                }
            }
            if(msg.what==34)
            {
                int data =(int)msg.obj;
                Log.i("kk","data的值是："+data);
                yc.setText(data+"");
                yc.setVisibility(View.VISIBLE);
                showidHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //yc.setVisibility(View.GONE); //view是要隐藏的控件
                        yc.setVisibility(View.INVISIBLE);
                    }
                }, 2000);
            }
        }
    };
    // 图片
    public static Bitmap bitmap;


    // 得到当前摄像头的图片信息
    public void getBitmap() {
        bitmap = cameraCommandUtil.httpForImage(FirstActivity.IPCamera);
        phHandler.sendEmptyMessage(10);
    }
    public static Bitmap bitmap_data()
    {
        return bitmap;
    }
    // 显示图片
    @SuppressLint("HandlerLeak")
    private Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                image_show.setImageBitmap(bitmap);
            }
        }
    };

    private class ontouchlistener1 implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO 自动生成的方法存根
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 点击位置坐标
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    break;
                // 弹起坐标
                case MotionEvent.ACTION_UP:
                    float x2 = event.getX();
                    float y2 = event.getY();
                    float xx = x1 > x2 ? x1 - x2 : x2 - x1;
                    float yy = y1 > y2 ? y1 - y2 : y2 - y1;
                    // 判断滑屏趋势
                    int MINLEN = 30;
                    if (xx > yy) {
                        if ((x1 > x2) && (xx > MINLEN)) {        // left
                            Toast.makeText(getActivity(),"左转",Toast.LENGTH_SHORT).show();
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(FirstActivity.IPCamera, 4, 1);  //左
                                }
                            });

                        } else if ((x1 < x2) && (xx > MINLEN)) { // right
                            Toast.makeText(getActivity(),"右转",Toast.LENGTH_SHORT).show();
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(FirstActivity.IPCamera, 6, 1);  //右
                                }
                            });
                        }
                    } else {
                        if ((y1 > y2) && (yy > MINLEN)) {        // up
                            Toast.makeText(getActivity(),"抬头",Toast.LENGTH_SHORT).show();
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                        cameraCommandUtil.postHttp(FirstActivity.IPCamera, 0, 1);  //上
                                }
                            });
                        } else if ((y1 < y2) && (yy > MINLEN)) { // down
                            Toast.makeText(getActivity(),"低头",Toast.LENGTH_SHORT).show();
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                       cameraCommandUtil.postHttp(FirstActivity.IPCamera, 2, 1);  //下
                                }
                            });
                        }
                    }
                    x1 = 0;
                    x2 = 0;
                    y1 = 0;
                    y2 = 0;
                    break;
            }
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        Button bt =(Button)view;
        String content=(String)bt.getText();

        switch (view.getId())
        {
            case R.id.state:
                if(content.equals("主车状态"))
                {
                    FirstActivity.chief_status_flag =true;
                    bt.setText(getResources().getText(R.string.follow_status));
                    FirstActivity.connectTransport.vice(2);
                    FirstActivity.but_handler.obtainMessage(11).sendToTarget();
                }
                else if(content.equals("从车状态"))
                {
                    FirstActivity.chief_status_flag =false;
                    bt.setText(getResources().getText(R.string.main_status));
                    FirstActivity.connectTransport.vice(1);
                    FirstActivity.but_handler.obtainMessage(22).sendToTarget();
                }
                break;
            case R.id.control:
                if(content.equals("主车控制"))
                {
                    FirstActivity.chief_control_flag =true;
                    bt.setText(getResources().getText(R.string.follow_control));
                    FirstActivity.connectTransport.TYPE = 0xAA;
                    FirstActivity.but_handler.obtainMessage(33).sendToTarget();
                }
                else if(content.equals("从车控制"))
                {
                    FirstActivity.chief_control_flag =false;
                    bt.setText(getResources().getText(R.string.main_control));
                    FirstActivity.connectTransport.TYPE =  0x02;
                    FirstActivity.but_handler.obtainMessage(44).sendToTarget();
                }
                break;
            case R.id.clear_coded_disc:
                FirstActivity.connectTransport.clear();
                break;
            default:
                break;
        }
    }



    @SuppressLint("HandlerLeak")
    private Handler bt_handler =new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 21:
                    state.setText(getResources().getText(R.string.follow_status));
                    break;
                case 22:
                    state.setText(getResources().getText(R.string.main_status));
                    break;
                case 23:
                    control.setText(getResources().getText(R.string.follow_control));
                    break;
                case 24:
                    control.setText(getResources().getText(R.string.main_control));
                    break;
                default:
                    break;
            }
        }
    };

}
