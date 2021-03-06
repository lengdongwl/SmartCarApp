package right.fragemt.recyclerview.data;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bkrc.camera.XcApplication;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import car.bkrc.com.car2018.FirstActivity;
import car.bkrc.com.car2018.R;
import car.bkrc.right.fragment.LeftFragment;
import car2017_demo.RGBLuminanceSource;

public class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {

    private List<Other_Landmark> mOtherLandmarkList;
    Context context = null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View InfrareView;
        ImageView OtherImage;
        TextView OtherName;

        public ViewHolder(View view) {
            super(view);
            InfrareView = view;
            OtherImage = (ImageView) view.findViewById(R.id.landmark_image);
            OtherName = (TextView) view.findViewById(R.id.landmark_name);
        }
    }

    public OtherAdapter(List<Other_Landmark> InfrareLandmarkList, Context context) {
        mOtherLandmarkList = InfrareLandmarkList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.OtherName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Other_Landmark otherLandmark = mOtherLandmarkList.get(position);
                Other_select(otherLandmark);
                Toast.makeText(v.getContext(), "you clicked view " + otherLandmark.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.OtherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = holder.getAdapterPosition();
                Other_Landmark otherLandmark = mOtherLandmarkList.get(position);
                Other_select(otherLandmark);
                Toast.makeText(v.getContext(), "you clicked image " + otherLandmark.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Other_Landmark InfrareLandmark = mOtherLandmarkList.get(position);
        holder.OtherImage.setImageResource(InfrareLandmark.getImageId());
        holder.OtherName.setText(InfrareLandmark.getName());
    }

    @Override
    public int getItemCount() {
        return mOtherLandmarkList.size();
    }


    private void Other_select(Other_Landmark InfrareLandmark) {
        switch (InfrareLandmark.getName()) {
            case "?????????":
                position_Dialog();
                break;
            case "?????????":
                qrHandler.sendEmptyMessage(10);
                break;
            case "?????????":
                buzzerController();
                break;
            case "?????????":
                lightController();
                break;
//            case "OpenMV?????????":
//                openmv_camera();
//                break;

            default:
                break;
        }

    }


    private Timer timer;
    private String result_qr;
    // ????????????????????????
    @SuppressLint("HandlerLeak")
    Handler qrHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Result result = null;
                            RGBLuminanceSource rSource = new RGBLuminanceSource(
                                    LeftFragment.bitmap);
                            try {
                                BinaryBitmap binaryBitmap = new BinaryBitmap(
                                        new HybridBinarizer(rSource));
                                Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
                                hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
                                QRCodeReader reader = new QRCodeReader();
                                result = reader.decode(binaryBitmap, hint);
                                if (result.toString() != null) {
                                    result_qr = result.toString();
                                    timer.cancel();
                                    qrHandler.sendEmptyMessage(20);
                                }
                                System.out.println("????????????");
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            } catch (ChecksumException e) {
                                e.printStackTrace();
                            } catch (FormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 200);
                    break;
                case 20:
                    Toast.makeText(context, result_qr, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };


    private int state_camera = 0;

    private void position_Dialog()  //??????????????????
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("???????????????");
        String[] set_item = {"???????????????1TFTA", "???????????????2TFTB", "???????????????3TRAFFICLIGHT", "???????????????4QR_A", "???????????????5QR_B","???????????????6", "???????????????1",
                "???????????????2","???????????????3","???????????????4","???????????????5","???????????????6"};
        builder.setSingleChoiceItems(set_item, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                state_camera = which + 5;
                camerastate_control();
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean flag_camera;
    int k = 3;

    // ???????????????????????????????????????
    private void camerastate_control() {
        XcApplication.executorServicetor.execute(new Runnable() {
            public void run() {
                Log.d("IPCamera", "run: "+state_camera);
                switch (state_camera) {
                    //??????????????????
                    case 1:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 0, 1);  //??????
                        break;
                    case 2:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 2, 1);  //??????
                        break;
                    case 3:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 4, 1);  //??????
                        break;
                    case 4:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 6, 1);  //??????
                        break;
                    // / 5-10   ???????????????1???6
                    case 5:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 30, 0);
                        break;
                    case 6:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 32, 0);
                        break;
                    case 7:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 34, 0);
                        break;
                    case 8:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 36, 0);
                        break;
                    case 9:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 38, 0);
                        break;
                    case 10:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 40, 0);
                        break;
                    // / 11-16   ???????????????1???6
                    case 11:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 31, 0);
                        break;
                    case 12:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 33, 0);
                        break;
                    case 13:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 35, 0);
                        break;
                    case 14:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 37, 0);
                        break;
                    case 15:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 39, 0);
                        break;
                    case 16:
                        LeftFragment.cameraCommandUtil.postHttp(FirstActivity.IPCamera, 41, 0);
                        break;
                    default:
                        break;
                }
                state_camera = 0;
            }
        });
    }

    // OpenMV ???????????????
    private void openmv_camera() {
        AlertDialog.Builder openmv_builder = new AlertDialog.Builder(context);
        openmv_builder.setTitle("OpenMV?????????");
        String[] openmv_str = {"??????", "?????????????????????", "?????????????????????"};
        openmv_builder.setSingleChoiceItems(openmv_str, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                break;
                            case 1:  //?????????????????????
                                FirstActivity.connectTransport.opencv_control(0x92, 0x01);
                                break;
                            case 2:  //????????????
                                FirstActivity.connectTransport.opencv_control(0x92, 0x02);
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        openmv_builder.create().show();
    }

    // ?????????
    private void buzzerController() {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        build.setTitle("?????????");
        String[] im = {"???", "???"};
        build.setSingleChoiceItems(im, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {
                            // ???????????????
                            FirstActivity.connectTransport.buzzer(1);
                        } else if (which == 1) {
                            // ???????????????
                            FirstActivity.connectTransport.buzzer(0);
                        }
                        dialog.dismiss();
                    }
                });
        build.create().show();
    }

    // ??????????????????
    private void lightController() {
        AlertDialog.Builder lt_builder = new AlertDialog.Builder(context);
        lt_builder.setTitle("?????????");
        String[] item = {"??????", "??????", "??????", "??????"};
        lt_builder.setSingleChoiceItems(item, -1,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {
                            FirstActivity.connectTransport.light(1, 0);
                        } else if (which == 1) {
                            FirstActivity.connectTransport.light(1, 1);
                        } else if (which == 2) {
                            FirstActivity.connectTransport.light(0, 1);
                        } else if (which == 3) {
                            FirstActivity.connectTransport.light(0, 0);
                        }
                        dialog.dismiss();
                    }
                });
        lt_builder.create().show();
    }



}