package car.bkrc.com.car2018;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bkrc.camera.XcApplication;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;

import car.bkrc.right.fragment.LeftFragment;
import car.bkrc.right.fragment.RightFragment1;
import car.bkrc.right.fragment.RightInfraredFragment;
import car.bkrc.right.fragment.RightOtherFragment;
import car.bkrc.right.fragment.RightWiFiFragment;
import car.bkrc.right.fragment.RightZigbeeFragment;
import car2017_demo.ConnectTransport;
import mycode.AllTask;
import mycode.Sendtool;
import ui.bkrc.car.TitleToolbar;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.InterruptedException;
import static car.bkrc.right.fragment.LeftFragment.showidHandler;
import car.bkrc.com.car2018.LoginActivity;
public class FirstActivity extends AppCompatActivity {
    private ViewPager viewPager;
    public static ConnectTransport connectTransport;
    public AllTask alltask;
    public  LoginActivity wifi_login;//wifi??????
    // ??????ip
    public static String IPCar;
    // ?????????IP
    public static String IPCamera = null;
    public static String purecameraip = null;
    public static boolean chief_status_flag = true;//????????????-

    public static boolean chief_control_flag = true; //????????????
    public static Handler recvhandler = null;
    public static Handler but_handler;  //????????????menu??????

    private Thread quan=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first1);
        but_handler = button_handler;  //??????leftfragment????????????????????????????????????menu??????

        if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL) {  //???????????????a72??????usb???????????????
            mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS); //??????usb??????????????????
            Transparent.showLoadingMessage(this, "?????????", false);//???????????????????????????????????????usb??????????????????
        }

        TitleToolbar mToolbar = (TitleToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);//??????viewPager????????????????????????
        viewPager.setOffscreenPageLimit(3);

        //?????????????????????????????????
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        int[] image = {R.drawable.ic_home, R.drawable.ic_zigbee,
                R.drawable.ic_infrared, R.drawable.ic_other};
        int[] color = {ContextCompat.getColor(this, R.color.firstColor), ContextCompat.getColor(this, R.color.secondColor),
                ContextCompat.getColor(this, R.color.thirdColor), ContextCompat.getColor(this, R.color.fourthColor)};
        if (bottomNavigationView != null) {
            bottomNavigationView.isWithText(false);
            // bottomNavigationView.activateTabletMode();
            bottomNavigationView.isColoredBackground(true);
            bottomNavigationView.setTextActiveSize(getResources().getDimension(R.dimen.text_active));
            bottomNavigationView.setTextInactiveSize(getResources().getDimension(R.dimen.text_inactive));
            bottomNavigationView.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(this, R.color.firstColor));
        }

        BottomNavigationItem bottomNavigationItem = new BottomNavigationItem
                ("??????", color[0], image[0]);
        BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem
                ("zigbee", color[1], image[1]);
        BottomNavigationItem bottomNavigationItem2 = new BottomNavigationItem
                ("??????", color[2], image[2]);
        BottomNavigationItem bottomNavigationItem3 = new BottomNavigationItem
                ("??????", color[3], image[3]);

        bottomNavigationView.addTab(bottomNavigationItem);
        bottomNavigationView.addTab(bottomNavigationItem1);
        bottomNavigationView.addTab(bottomNavigationItem2);
        bottomNavigationView.addTab(bottomNavigationItem3);
        bottomNavigationView.setOnBottomNavigationItemClickListener(new OnBottomNavigationItemClickListener() {
            @Override
            public void onNavigationItemClick(int index) {
                switch (index) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        break;
                    case 3:
                        viewPager.setCurrentItem(3);

                        break;
                }
            }
        });
        connectTransport = new ConnectTransport();    //??????????????????
        alltask=new AllTask(connectTransport);
        setupViewPager(viewPager);                      //??????fragment
//        View v=mToolbar.findViewById(R.id.jiajian);
//        Button b1=(Button)v.findViewById(R.id.b1);
//        Button b2=(Button)v.findViewById(R.id.b2);
//        b1.setOnClickListener(this.mylistener);
//        b2.setOnClickListener(this.mylistener);
    }
    View.OnClickListener mylistener=new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch(view.getId()){
                case R.id.b1:
                    AllTask.b1();
                    break;
                case R.id.b2:
                    AllTask.b2();
                    break;
                case R.id.shanchu:
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            b1.setVisibility(View.GONE); //view?????????????????????
                            b2.setVisibility(View.GONE); //view?????????????????????
                        }
                    }, 1500);
                    break;
                default:
                    break;

            }
        }
    };


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(RightFragment1.getInstance());
        adapter.addFragment(RightZigbeeFragment.getInstance());
        adapter.addFragment(RightInfraredFragment.getInstance());
        adapter.addFragment(RightOtherFragment.getInstance());
        adapter.addFragment(RightWiFiFragment.getInstance());
        viewPager.setAdapter(adapter);
    }

    private Menu toolmenu;


    Button b1;
    Button b2;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {   //activity?????????????????????Menu
        getMenuInflater().inflate(R.menu.tool_rightitem, menu);
        final MenuItem item=menu.findItem(R.id.jiajian);
        b1=item.getActionView().findViewById(R.id.b1);
        b2=item.getActionView().findViewById(R.id.b2);
        Button shanchu=item.getActionView().findViewById(R.id.shanchu);
        b1.setOnClickListener(this.mylistener);
        b2.setOnClickListener(this.mylistener);
        shanchu.setOnClickListener(this.mylistener);
        toolmenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    //?????????????????????unicode??????
    public static String toUnicode(String s) {
        String as[] = new String[s.length()];

        String s1 = "";

        for (int i = 0;i<as.length;i++)
        {

            as[i] = Integer.toHexString(s.charAt(i) & 0xffff);
            s1 = s1 + "\\u" + as[i];

        }

        return s1;

    }



    public static volatile boolean wifi_threadExit=false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //???????????????
        int id = item.getItemId();
        // Toast.makeText(FirstActivity.this,item.getTitle(),Toast.LENGTH_SHORT).show();
        switch (id) {
            case R.id.car_status:
                if (item.getTitle().equals("????????????")) {
                    chief_status_flag = true;
                    item.setTitle(getResources().getText(R.string.follow_status));
                    connectTransport.vice(2);
                    LeftFragment.btchange_handler.obtainMessage(21).sendToTarget();
                } else if (item.getTitle().equals("????????????")) {
                    chief_status_flag = false;
                    item.setTitle(getResources().getText(R.string.main_status));
                    connectTransport.vice(1);
                    LeftFragment.btchange_handler.obtainMessage(22).sendToTarget();
                }
                break;
            case R.id.car_control:
                if (item.getTitle().equals("????????????")) {
                    chief_control_flag = true;
                    item.setTitle(getResources().getText(R.string.follow_control));
                    connectTransport.TYPE = 0xAA;
                    LeftFragment.btchange_handler.obtainMessage(23).sendToTarget();
                } else if (item.getTitle().equals("????????????")) {
                    chief_control_flag = false;
                    item.setTitle(getResources().getText(R.string.main_control));
                    connectTransport.TYPE = 0x02;
                    LeftFragment.btchange_handler.obtainMessage(24).sendToTarget();
                }
                break;
            case R.id.connect://????????????
                wifi_login.cameraInit();
                wifi_login.search();

                Log.e(this.TAG, "????????????...");
                if(wifi_login.useNetwork())
                {
                    Log.e(this.TAG, "WIFI????????????????????????????????????......");
                    wifi_login.useUartCamera();//???????????????
                    Sendtool.preSetCammera(1, 3000);
                }else
                {
                    Log.e(this.TAG, "WIFI?????????????????????");
                }

                break;
            case R.id.clear_coded_disc:
                connectTransport.clear();
                break;
            case R.id.full_automatic:
                Sendtool.StartUp();//??????????????????
                break;
            case R.id.deal_wifi:
                if (wifi_threadExit==true) {
                    AllTask.DataShow("??????????????????WIFI?????????");
                    Toast.makeText(FirstActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    toolmenu.findItem(R.id.deal_wifi).setTitle("????????????");
                    wifi_threadExit=false;
                }else
                {
                    AllTask.DataShow("??????????????????WIFI?????????");
                    Toast.makeText(FirstActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    toolmenu.findItem(R.id.deal_wifi).setTitle("????????????");
                    wifi_threadExit=true;
                }
                break;
            case R.id.test:
                View v=findViewById(R.id.test);
                PopupMenu popup = new PopupMenu(this,v);//?????????????????????????????????view
                //?????????????????????
                MenuInflater inflater = popup.getMenuInflater();
                //????????????
                inflater.inflate(R.menu.test, popup.getMenu());
                //??????????????????????????????
                popup.setOnMenuItemClickListener(n);
                //??????(??????????????????????????????)
                popup.show();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static int count=0;
    PopupMenu.OnMenuItemClickListener n=new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()) {
                case R.id.test0:
                    Toast.makeText(FirstActivity.this, "??????", Toast.LENGTH_SHORT).show();
                    alltask.SavePictures(count++);
                    break;
                case R.id.test1:
                    Toast.makeText(FirstActivity.this, "??????  1", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            alltask.test1();
                        }
                    }).start();

                    break;
                case R.id.test2:
                    Toast.makeText(FirstActivity.this, "??????  2", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            alltask.test2();
                        }
                    }).start();
                    break;
                case R.id.test3:
                    Toast.makeText(FirstActivity.this, "??????  3", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            alltask.test3();
                        }
                    }).start();
                    break;

                default:
                    break;
            }
            return false;
        }
    };



    private Handler button_handler = new Handler()  //??????menu???leftfragment????????????????????????
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    toolmenu.getItem(1).setTitle(getResources().getText(R.string.follow_status));
                    break;
                case 22:
                    toolmenu.getItem(1).setTitle(getResources().getText(R.string.main_status));
                    break;
                case 33:
                    toolmenu.getItem(2).setTitle(getResources().getText(R.string.follow_control));
                    break;
                case 44:
                    toolmenu.getItem(2).setTitle(getResources().getText(R.string.main_control));
                    break;
                default:
                    break;

            }
        }
    };

    //------------------------------------------------------------------------------------------
    //???????????????usb???????????????????????????A72??????????????????????????????
    public static UsbSerialPort sPort = null;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.e(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {   //????????????
                    FirstActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = recvhandler.obtainMessage(1, data);
                            msg.sendToTarget();
                            FirstActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    protected void controlusb() {
        Log.e(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            Toast.makeText(FirstActivity.this, "No serial device.", Toast.LENGTH_SHORT).show();
        } else {
            openUsbDevice();
            if (connection == null) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
                Toast.makeText(FirstActivity.this, "Opening device failed", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Toast.makeText(FirstActivity.this, "Error opening device: ", Toast.LENGTH_SHORT).show();
                try {
                    sPort.close();
                } catch (IOException e2) {
                }
                sPort = null;
                return;
            }
            Toast.makeText(FirstActivity.this, "Serial device: " + sPort.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        }
        onDeviceStateChange();
        Transparent.dismiss();//?????????????????????
    }

    // ?????????usb????????????????????????????????????????????????usb??????
    private void openUsbDevice() {
        tryGetUsbPermission();
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbDeviceConnection connection;

    private void tryGetUsbPermission() {

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbPermissionActionReceiver, filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //here do emulation to ask all connected usb device for permission
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            //add some conditional check if necessary
            if (mUsbManager.hasPermission(usbDevice)) {
                //if has already got permission, just goto connect it
                //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                //and also choose option: not ask again
                afterGetUsbPermission(usbDevice);
            } else {
                //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
        }
    }

    private void afterGetUsbPermission(UsbDevice usbDevice) {

        Toast.makeText(FirstActivity.this, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_LONG).show();
        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice) {
        connection = mUsbManager.openDevice(usbDevice);
    }

    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (null != usbDevice) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.e(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.e(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener); //????????????
            mExecutor.submit(mSerialIoManager); //?????????????????????????????????????????????
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        //  Log.e("read data is ??????","   "+message);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (XcApplication.isserial == XcApplication.Mode.USB_SERIAL) {
            unregisterReceiver(mUsbPermissionActionReceiver);
            try {
                sPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sPort = null;
        } else if (XcApplication.isserial == XcApplication.Mode.SOCKET) {
            connectTransport.destory();
        }
    }

    private static final int MESSAGE_REFRESH = 101;
    private static final long REFRESH_TIMEOUT_MILLIS = 5000;
    private UsbManager mUsbManager;
    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
    private final String TAG = FirstActivity.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REFRESH:
                    refreshDeviceList();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler usbHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                useUsbtoserial();
            }
        }
    };

    private void useUsbtoserial() {
        final UsbSerialPort port = mEntries.get(0);  //A72??????????????? usb???????????????position =0??????
        final UsbSerialDriver driver = port.getDriver();
        final UsbDevice device = driver.getDevice();
        final String usbid = String.format("Vendor %s  ???Product %s",
                HexDump.toHexString((short) device.getVendorId()),
                HexDump.toHexString((short) device.getProductId()));
        Message msg = showidHandler.obtainMessage(22, usbid);
        msg.sendToTarget();
        FirstActivity.sPort = port;
        if (sPort != null) {
            controlusb();  //??????usb??????
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void refreshDeviceList() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                Log.e(TAG, "Refreshing device list ...");
                Log.e("mUsbManager is :", "  " + mUsbManager);
                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.e(TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                usbHandler.sendEmptyMessage(2);
                Log.e(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
            }
        }.execute((Void) null);
    }
}
