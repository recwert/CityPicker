package com.yang.picker.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.picker.CityPickerDialog;
import com.yang.picker.CityPickerDialog.onCityPickedListener;
import com.yang.picker.CityPickerView;
import com.yang.picker.InitAreaTask;
import com.yang.picker.OnePickerDialog;
import com.yang.picker.address.City;
import com.yang.picker.address.County;
import com.yang.picker.address.Province;
import com.yang.picker.wheel.adapter.AbstractWheelTextAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private List<Province> provinces = new ArrayList<>();
	private Button selectAreaBtn;
	private Button singlePicker;
	private CityPickerView cityPickerView;
	private Button getAreaBtn;
	private TextView areaText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		selectAreaBtn = findViewById(R.id.select_area_btn);
		selectAreaBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (provinces.size() > 0) {
					showAddressDialog();
				} else {
					new InitAreaTask(MainActivity.this, new InitAreaTask.OnPostProvincesListener() {
						@Override
						public void onPostProvinces(List<Province> provinces) {
							MainActivity.this.provinces = provinces;
							if (provinces.size() > 0) {
								showAddressDialog();
								cityPickerView = new CityPickerView(MainActivity.this);
								LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								cityPickerView.setLayoutParams(layoutParams);
								cityPickerView.setProvinces((ArrayList<Province>) provinces);
								cityPickerView.setDefaultArea(null, null, null);
								((LinearLayout)findViewById(R.id.rootView)).addView(cityPickerView);
							} else {
								Toast.makeText(MainActivity.this, "数据获取失败", Toast.LENGTH_LONG).show();
							}
						}
					}).execute(0);
				}
			}
		});
		
		singlePicker =(Button) findViewById(R.id.single_picker);
		singlePicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDateDialog();
			}
		});


		getAreaBtn = findViewById(R.id.get_area_btn);
		areaText = findViewById(R.id.area_text);

		getAreaBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (cityPickerView != null) {
					StringBuilder address = new StringBuilder();
					Province selectProvince = cityPickerView.getSelectProvince();
					City selectCity = cityPickerView.getSelectCity();
					County selectCounty = cityPickerView.getSelectCounty();
					address.append(
							selectProvince != null && selectProvince.getAreaName() != null ? selectProvince
									.getAreaName() : "")
							.append(selectCity != null && selectCity.getAreaName() != null ? selectCity
									.getAreaName() : "")
							.append(selectCounty != null && selectCounty.getAreaName() != null ? selectCounty
									.getAreaName() : "");
					areaText.setText(address);
				}
			}
		});


	}

	private void showAddressDialog() {
		new CityPickerDialog(MainActivity.this, provinces, null, null, null,
				new onCityPickedListener() {

					@Override
					public void onPicked(Province selectProvince,
							City selectCity, County selectCounty) {
						StringBuilder address = new StringBuilder();
						address.append(
								selectProvince != null ? selectProvince
										.getAreaName() : "")
								.append(selectCity != null ? selectCity
										.getAreaName() : "")
								.append(selectCounty != null ? selectCounty
										.getAreaName() : "");
						String text = selectCounty != null ? selectCounty
								.getAreaName() : "";
						selectAreaBtn.setText(address);
					}
				}).show();
	}
	
	
	private void showDateDialog() {
		final ArrayList<String> years = new ArrayList<String>(); 
		years.add("2005年");
		years.add("2006年");
		years.add("2007年");
		years.add("2008年");
		years.add("2009年");
		years.add("2010年");
		years.add("2011年");
		years.add("2012年");
		years.add("2013年");
		years.add("2014年");
		years.add("2015年");
		years.add("2016年");
		years.add("2017年");
		years.add("2018年");
		AbstractWheelTextAdapter adapter = new AbstractWheelTextAdapter(MainActivity.this,
				R.layout.wheel_text) {

			@Override
			public int getItemsCount() {

				return years.size();
			}

			@Override
			protected CharSequence getItemText(int index) {

				return years.get(index);
			}
		};
		
		OnePickerDialog picker = new OnePickerDialog(MainActivity.this,adapter , new OnePickerDialog.onSelectListener() {
			
			@Override
			public void onSelect(AbstractWheelTextAdapter adapter, int position) {
				singlePicker.setText(years.get(position));
			}
		});
		picker.show();
	}

}
