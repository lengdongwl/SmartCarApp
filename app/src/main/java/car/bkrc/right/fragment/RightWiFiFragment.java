package car.bkrc.right.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import car.bkrc.com.car2018.LoginActivity;
import car.bkrc.com.car2018.R;


public class RightWiFiFragment extends Fragment {
    private static TextView dataShow2 =null;
    private static TextView dataShow =null;
    public static RightWiFiFragment getInstance()
    {
        return RightWiFiHolder.mInstance;
    }

    private static class RightWiFiHolder
    {
        private static final RightWiFiFragment mInstance =new RightWiFiFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_wifi_fragment, container, false);
        View testview=null;
        if (LoginActivity.isPad(getActivity()))
            testview=inflater.inflate(R.layout.left_fragment,container,false);
        else
            testview = inflater.inflate(R.layout.left_fragment_mobilephone,container,false);
        dataShow2 =(TextView) view.findViewById(R.id.showip_wifi);
        dataShow =(TextView) testview.findViewById(R.id.showip);

        dataShow2.setText(dataShow.getText().toString());
        //dataShow2.setText("test");
        return view;
    }

    // useNetwork();   useUart();

}
