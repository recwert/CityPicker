package com.yang.picker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yang.picker.address.City;
import com.yang.picker.address.County;
import com.yang.picker.address.Province;
import com.yang.picker.wheel.OnWheelChangedListener;
import com.yang.picker.wheel.OnWheelClickedListener;
import com.yang.picker.wheel.WheelView;
import com.yang.picker.wheel.adapter.AbstractWheelTextAdapter;

import java.util.ArrayList;

/**
 * Created by zhourh on 2017/12/5.
 */

public class CityPickerView extends LinearLayout{

    private final static int DEFAULT_ITEMS = 5;
    private final static int UPDATE_CITY_WHEEL = 11;
    private final static int UPDATE_COUNTY_WHEEL = 12;

    private ArrayList<Province> mProvinces = new ArrayList<Province>();
    private ArrayList<City> mCities = new ArrayList<City>();
    private ArrayList<County> mCounties = new ArrayList<County>();
    AbstractWheelTextAdapter provinceAdapter;
    AbstractWheelTextAdapter cityAdapter;
    AbstractWheelTextAdapter countyAdapter;

    WheelView provinceWheel;
    WheelView citiesWheel;
    WheelView countiesWheel;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CITY_WHEEL:
                    mCities.clear();
                    mCities.addAll(mProvinces.get(msg.arg1).getCities());
                    citiesWheel.invalidateWheel(true);
                    citiesWheel.setCurrentItem(0, false);

                    mCounties.clear();
                    mCounties.addAll(mCities.get(0).getCounties());
                    countiesWheel.invalidateWheel(true);
                    countiesWheel.setCurrentItem(0, false);
                    break;
                case UPDATE_COUNTY_WHEEL:
                    mCounties.clear();
                    mCounties.addAll(mCities.get(msg.arg1).getCounties());
                    countiesWheel.invalidateWheel(true);
                    countiesWheel.setCurrentItem(0, false);
                    break;
                default:
                    break;
            }
        }
    };

    public static interface onCityPickedListener {
        public void onPicked(Province selectProvince, City selectCity,
                             County selectCounty);
    }

    public CityPickerView(Context context) {
        super(context);
        init();
    }

    public CityPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CityPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        provinceWheel = newWheel();
        citiesWheel = newWheel();
        countiesWheel = newWheel();
        addView(provinceWheel);
        addView(citiesWheel);
        addView(countiesWheel);

        provinceAdapter = new AbstractWheelTextAdapter(getContext(),
                R.layout.wheel_text) {

            @Override
            public int getItemsCount() {

                return mProvinces.size();
            }

            @Override
            protected CharSequence getItemText(int index) {

                return mProvinces.get(index).getAreaName();
            }
        };

        cityAdapter = new AbstractWheelTextAdapter(getContext(),
                R.layout.wheel_text) {

            @Override
            public int getItemsCount() {

                return mCities.size();
            }

            @Override
            protected CharSequence getItemText(int index) {

                return mCities.get(index).getAreaName();
            }
        };

        countyAdapter = new AbstractWheelTextAdapter(getContext(),
                R.layout.wheel_text) {

            @Override
            public int getItemsCount() {

                return mCounties.size();
            }

            @Override
            protected CharSequence getItemText(int index) {

                return mCounties.get(index).getAreaName();
            }
        };

        provinceWheel.setViewAdapter(provinceAdapter);
        provinceWheel.setCyclic(false);
        provinceWheel.setVisibleItems(DEFAULT_ITEMS);

        citiesWheel.setViewAdapter(cityAdapter);
        citiesWheel.setCyclic(false);
        citiesWheel.setVisibleItems(DEFAULT_ITEMS);

        countiesWheel.setViewAdapter(countyAdapter);
        countiesWheel.setCyclic(false);
        countiesWheel.setVisibleItems(DEFAULT_ITEMS);

        OnWheelClickedListener clickListener = new OnWheelClickedListener() {

            @Override
            public void onItemClicked(WheelView wheel, int itemIndex) {
                if (itemIndex != wheel.getCurrentItem()) {
                    wheel.setCurrentItem(itemIndex, true, 500);
                }
            }
        };

        provinceWheel.addClickingListener(clickListener);
        citiesWheel.addClickingListener(clickListener);
        countiesWheel.addClickingListener(clickListener);

        provinceWheel.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mHandler.removeMessages(UPDATE_CITY_WHEEL);
                Message msg = Message.obtain();
                msg.what = UPDATE_CITY_WHEEL;
                msg.arg1 = newValue;
                mHandler.sendMessageDelayed(msg, 50);
            }
        });

        citiesWheel.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mHandler.removeMessages(UPDATE_COUNTY_WHEEL);
                Message msg = Message.obtain();
                msg.what = UPDATE_COUNTY_WHEEL;
                msg.arg1 = newValue;
                mHandler.sendMessageDelayed(msg, 50);

            }
        });
    }

    private WheelView newWheel() {
        WheelView wheelView = new WheelView(getContext());
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        wheelView.setLayoutParams(layoutParams);
        return wheelView;

    }


    public void setProvinces(ArrayList<Province> mProvinces) {
        this.mProvinces = mProvinces;
    }

    public void setDefaultArea(Province defaultProvince, City defaultCity,
                                County defaultCounty) {

        int provinceItem = 0;
        int cityItem = 0;
        int countyItem = 0;

        if (defaultProvince == null) {
            defaultProvince = mProvinces.get(0);
            provinceItem = 0;
        } else {
            for (int i = 0; i < mProvinces.size(); i++) {
                if (mProvinces.get(i).getAreaId()
                        .equals(defaultProvince.getAreaId())) {
                    provinceItem = i;
                    break;
                }
            }
        }
        mCities.clear();
        mCities.addAll(defaultProvince.getCities());
        if (mCities.size() == 0) {
            mCities.add(new City());
            cityItem = 0;
        } else if (defaultCity == null) {
            defaultCity = mCities.get(0);
            cityItem = 0;
        } else {
            for (int i = 0; i < mCities.size(); i++) {
                if (mCities.get(i).getAreaId().equals(defaultCity.getAreaId())) {
                    cityItem = i;
                    break;
                }
            }
        }

        mCounties.clear();
        if (defaultCity != null) {
            mCounties.addAll(defaultCity.getCounties());
        }
        if (mCounties.size() == 0) {
            mCounties.add(new County());
            countyItem = 0;
        } else if (defaultCounty == null) {
            countyItem = 0;
        } else {
            for (int i = 0; i < mCounties.size(); i++) {
                if (mCounties.get(i).getAreaId()
                        .equals(defaultCounty.getAreaId())) {
                    countyItem = i;
                    break;
                }
            }
        }
        provinceWheel.setCurrentItem(provinceItem, false);
        citiesWheel.setCurrentItem(cityItem, false);
        countiesWheel.setCurrentItem(countyItem, false);

    }

    public void setCenterDrawable(Drawable centerDrawable) {
        provinceWheel.setCenterDrawable(centerDrawable);
        citiesWheel.setCenterDrawable(centerDrawable);
        countiesWheel.setCenterDrawable(centerDrawable);
    }

    public Province getSelectProvince() {
        return mProvinces.size() > 0 ? mProvinces
                .get(provinceWheel.getCurrentItem()) : null;
    }

    public City getSelectCity() {
        return mCities.size() > 0 ? mCities.get(citiesWheel
                .getCurrentItem()) : null;
    }

    public County getSelectCounty() {
        return  mCounties.size() > 0 ? mCounties
                .get(countiesWheel.getCurrentItem()) : null;
    }

}
