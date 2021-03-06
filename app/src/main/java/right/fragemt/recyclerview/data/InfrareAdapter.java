package right.fragemt.recyclerview.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import car.bkrc.com.car2018.FirstActivity;
import car.bkrc.com.car2018.R;
import mycode.AllTask;


public class InfrareAdapter extends RecyclerView.Adapter<InfrareAdapter.ViewHolder>{

    static int Ts_num = 1;
    static int Ts_tra = 0;

    private List<Infrared_Landmark> mInfrareLandmarkList;
    Context context =null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View InfrareView;
        ImageView InfrareImage;
        TextView InfrareName;

        public ViewHolder(View view) {
            super(view);
            InfrareView = view;
            InfrareImage = (ImageView) view.findViewById(R.id.infrared_image);
            InfrareName = (TextView) view.findViewById(R.id.infrared_name);
        }
    }

    public InfrareAdapter(List<Infrared_Landmark> InfrareLandmarkList, Context context) {
        mInfrareLandmarkList = InfrareLandmarkList;
        this.context =context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.infrared_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.InfrareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Infrared_Landmark InfrareLandmark = mInfrareLandmarkList.get(position);
                Infrare_select(InfrareLandmark);
                Toast.makeText(v.getContext(), "you clicked view " + InfrareLandmark.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.InfrareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Infrared_Landmark InfrareLandmark = mInfrareLandmarkList.get(position);
                Infrare_select(InfrareLandmark);
                Toast.makeText(v.getContext(), "you clicked image " + InfrareLandmark.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Infrared_Landmark InfrareLandmark = mInfrareLandmarkList.get(position);
        holder.InfrareImage.setImageResource(InfrareLandmark.getImageId());
        holder.InfrareName.setText(InfrareLandmark.getName());
    }

    @Override
    public int getItemCount() {
        return mInfrareLandmarkList.size();
    }

    private void Infrare_select(Infrared_Landmark InfrareLandmark)
    {
        switch (InfrareLandmark.getName())
        {
            case "?????????":
                policeController();
                break;
            case "?????????":
                gearController();
                break;
            case "??????":
             /*   FirstActivity.ConnectTransport.infrared((byte) 0x03, (byte) 0x05,
                        (byte) 0x14, (byte) 0x45, (byte) 0xDE,
                        (byte) 0x92);
                        */
                break;
            case "????????????":
                //AllTask.DataShow("Hello");
                threeDisplay();
                break;
            case "????????????":
                AllTask.DataShow("Hello");
                break;
            default:
                break;
        }

    }

    // ?????????
    private void policeController() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("?????????");
        String[] item2 = { "???", "???" };
        builder.setSingleChoiceItems(item2,-1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {
                            FirstActivity.connectTransport.infrared((byte) 0x03, (byte) 0x05,
                                    (byte) 0x14, (byte) 0x45, (byte) 0xDE,
                                    (byte) 0x92);

                        } else if (which == 1) {
                            FirstActivity.connectTransport.infrared((byte) 0x67, (byte) 0x34,
                                    (byte) 0x78, (byte) 0xA2, (byte) 0xFD,
                                    (byte) 0x27);
                        }
                        dialog.dismiss();  //???????????????

                    }
                });
        builder.create().show();
    }

    private void gearController() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("???????????????");
        String[] gr_item = { "?????????1???", "?????????2???", "?????????3???" };
        builder.setSingleChoiceItems(gr_item, -1,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {// ?????????
                            FirstActivity.connectTransport.gear(1);
                        } else if (which == 1) {// ?????????
                            FirstActivity.connectTransport.gear(2);
                        } else if (which == 2) {// ?????????
                            FirstActivity.connectTransport.gear(3);
                        }
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private short[] data = { 0x00, 0x00, 0x00, 0x00, 0x00 };

    private void threeDisplay() {
//        AlertDialog.Builder Builder = new AlertDialog.Builder(context);
//        Builder.setTitle("????????????");
//        String[] three_item = { "????????????", "????????????", "????????????", "????????????", "????????????", "????????????" };
//        Builder.setSingleChoiceItems(three_item, -1,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        switch (which) {
//                            case 0:
//                                color();
//                                break;
//                            case 1:
//                                shape();
//                                break;
//                            case 2:
//                                dis();
//                                break;
//                            case 3:
//                                lic();
//                                break;
//                            case 4:
//                                road();
//                                break;
//                            case 5:
//                                data[0] = 0x15;
//                                data[1] = 0x01;
//                                FirstActivity.connectTransport.infrared_stereo(data);
//                                break;
//                            default:
//                                break;
//                        }
//                        dialog.cancel();
//                    }
//                });
//        Builder.create().show();

        int num = 1;
        AlertDialog.Builder Builder = new AlertDialog.Builder(context);
        Builder.setTitle("??????????????????");
        String[] three_item = { "????????????", "????????????", "????????????", "??????????????????", "????????????", "????????????" };
        Builder.setSingleChoiceItems(three_item, -1,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                //color();
                                AllTask.DataShow("????????????");
                                AllTask.Ts_Go(1,Ts_num,Ts_tra);
                                break;
                            case 1:
                                //shape();
                                AllTask.DataShow("????????????");
                                AllTask.Ts_Go(0,Ts_num,Ts_tra);
                                break;
                            case 2:
                                //dis();
                                AllTask.DataShow("????????????");
                                tft_xk();
                                Ts_tra = 0;
//                                Ts_num++;
//                                if(Ts_num >= 6)Ts_num=1;
//
//                                switch(Ts_num)
//                                {
//                                    case 1:
//                                        AllTask.DataShow("?????????????????????");
//                                        break;
//                                    case 2:
//                                        AllTask.DataShow("?????????????????????");
//                                        break;
//                                    case 3:
//                                        AllTask.DataShow("?????????????????????");
//                                        break;
//                                    case 4:
//                                        AllTask.DataShow("?????????????????????");
//                                        break;
//                                    case 5:
//                                        AllTask.DataShow("?????????????????????");
//                                        break;
//                                }

                                break;
                            case 3:
                                //lic();
                                AllTask.DataShow("??????????????????");
                                Ts_tra = 1;
                                Ts_num = 0;
                                break;
                            case 4:
                                road();
                                break;
                            case 5:
                                data[0] = 0x15;
                                data[1] = 0x01;
                                FirstActivity.connectTransport.infrared_stereo(data);
                                break;
                            default:
                                break;
                        }
                        dialog.cancel();
                    }
                });
        Builder.create().show();
    }

    private void color() {
        AlertDialog.Builder colorBuilder = new AlertDialog.Builder(context);
        colorBuilder.setTitle("????????????");
        String[] lg_item = { "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????" };
        colorBuilder.setSingleChoiceItems(lg_item, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data[0] = 0x13;
                        data[1] = (short) (which + 0x01);
                        FirstActivity.connectTransport.infrared_stereo(data);
                        dialog.cancel();
                    }
                });
        colorBuilder.create().show();
    }

    private void shape() {
        AlertDialog.Builder shapeBuilder = new AlertDialog.Builder(context);
        shapeBuilder.setTitle("????????????");
        String[] shape_item = { "??????", "??????", "?????????", "??????", "??????", "??????", "??????",
                "?????????" };
        shapeBuilder.setSingleChoiceItems(shape_item, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data[0] = 0x12;
                        data[1] = (short) (which + 0x01);
                        FirstActivity.connectTransport.infrared_stereo(data);
                        dialog.cancel();
                    }
                });
        shapeBuilder.create().show();
    }

    private void road() {
        AlertDialog.Builder roadBuilder = new AlertDialog.Builder(context);
        roadBuilder.setTitle("????????????");
        String[] road_item = { "???????????????????????????", "????????????????????????" };
        roadBuilder.setSingleChoiceItems(road_item, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data[0] = 0x14;
                        data[1] = (short) (which + 0x01);
                        FirstActivity.connectTransport.infrared_stereo(data);
                        dialog.cancel();
                    }
                });
        roadBuilder.create().show();
    }

    private void tft_xk() {
        AlertDialog.Builder disBuilder = new AlertDialog.Builder(context);
        disBuilder.setTitle("????????????");
        final String[] road_item = { "??????", "??????", "??????", "??????", "??????" };
        disBuilder.setSingleChoiceItems(road_item, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which)
                        {
                            case 0:
                                AllTask.DataShow("??????");
                                Ts_num = 1;
                                break;
                            case 1:
                                AllTask.DataShow("??????");
                                Ts_num = 2;
                                break;
                            case 2:
                                AllTask.DataShow("??????");
                                Ts_num = 3;
                                break;
                            case 3:
                                AllTask.DataShow("??????");
                                Ts_num = 4;
                                break;
                            case 4:
                                AllTask.DataShow("??????");
                                Ts_num = 5;
                                break;
                        }
                        dialog.cancel();
//                        int disNum = Integer.parseInt(road_item[which]
//                                .substring(0, 2));
//                        data[0] = 0x11;
//                        data[1] = (short) (disNum / 10 + 0x30);
//                        data[2] = (short) (disNum % 10 + 0x30);
//                        FirstActivity.connectTransport.infrared_stereo(data);
//                        dialog.cancel();
                    }
                });
        //AllTask.DataShow("Ts_num = "+Ts_num);
        disBuilder.create().show();
    }

    private void dis() {
        AlertDialog.Builder disBuilder = new AlertDialog.Builder(context);
        disBuilder.setTitle("????????????");
        final String[] road_item = { "10cm", "15cm", "20cm", "28cm", "39cm" };
        disBuilder.setSingleChoiceItems(road_item, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int disNum = Integer.parseInt(road_item[which]
                                .substring(0, 2));
                        data[0] = 0x11;
                        data[1] = (short) (disNum / 10 + 0x30);
                        data[2] = (short) (disNum % 10 + 0x30);
                        FirstActivity.connectTransport.infrared_stereo(data);
                        dialog.cancel();
                    }
                });
        disBuilder.create().show();
    }

    //???string?????????short????????????
    private short[] StringToBytes(String licString) {
        if (licString == null || licString.equals("")) {
            return null;
        }
        licString = licString.toUpperCase();
        int length = licString.length();
        char[] hexChars = licString.toCharArray();
        short[] d = new short[length];
        for (int i = 0; i < length; i++) {
            d[i] = (short) hexChars[i];
        }
        return d;
    }
    private Handler licHandler=new Handler(){
        public void handleMessage(Message msg) {
            short [] li=StringToBytes(lic_item[msg.what]);
            data[0] = 0x20;
            data[1] = (short) (li[0]);
            data[2]=(short) (li[1]);
            data[3]=(short) (li[2]);
            data[4]=(short) (li[3]);
            FirstActivity.connectTransport.infrared_stereo(data);
            data[0] = 0x10;
            data[1] = (short) (li[4]);
            data[2]=(short) (li[5]);
            data[3]=(short) (li[6]);
            data[4]=(short) (li[7]);
            FirstActivity.connectTransport.infrared_stereo(data);
        };
    };
    private int lic = -1;
    private String[] lic_item = { "N300Y7A4", "N600H5B4", "N400Y6G6",
            "J888B8C8" };

    private void lic() {
        AlertDialog.Builder licBuilder = new AlertDialog.Builder(context);
        licBuilder.setTitle("????????????");
        licBuilder.setSingleChoiceItems(lic_item, lic,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lic = which;
                        licHandler.sendEmptyMessage(which);
                        dialog.cancel();
                    }
                });
        licBuilder.create().show();
    }

}