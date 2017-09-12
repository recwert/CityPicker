package com.yang.picker;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yang.picker.address.City;
import com.yang.picker.address.County;
import com.yang.picker.address.Province;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhourh on 2017/9/12.
 */

public class InitAreaTask extends AsyncTask<Integer, Integer, List<Province>> {

    Context mContext;

    Dialog progressDialog;

    private List<Province> provinces = new ArrayList<>();

    private OnPostProvincesListener onPostProvincesListener;

    public InitAreaTask(Context context, OnPostProvincesListener onPostProvincesListener) {
        mContext = context;
        this.onPostProvincesListener = onPostProvincesListener;
        progressDialog = Util.createLoadingDialog(mContext, "请稍等...", true,
                0);
    }

    @Override
    protected void onPreExecute() {

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(List<Province> result) {
        progressDialog.dismiss();
        if (onPostProvincesListener != null) {
            onPostProvincesListener.onPostProvinces(result);
        }
    }

    @Override
    protected List<Province> doInBackground(Integer... params) {
        String address = null;
        InputStream in = null;
        try {
            in = mContext.getResources().getAssets().open("address.txt");
            byte[] arrayOfByte = new byte[in.available()];
            in.read(arrayOfByte);
            address = EncodingUtils.getString(arrayOfByte, "UTF-8");
            JSONArray jsonList = new JSONArray(address);
            Gson gson = new Gson();
            for (int i = 0; i < jsonList.length(); i++) {
                try {
                    provinces.add(gson.fromJson(jsonList.getString(i),
                            Province.class));
                } catch (Exception e) {
                }
            }
            return provinces;
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return provinces;
    }

    public interface OnPostProvincesListener {
        void onPostProvinces(List<Province> provinces);
    }
}
