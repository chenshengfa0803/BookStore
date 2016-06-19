package com.bookstore.main.UserInfo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bookstore.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/25.
 */
public class MyDialogFragment extends DialogFragment implements LocationSource, AMapLocationListener, TextWatcher, PoiSearch.OnPoiSearchListener, AMap.OnMarkerClickListener {
    public static final String ARGS_DIALOG_TYPE = "fragmentType";
    public static final int DIALOG_TYPE_USERIMAGE = 0;
    public static final int DIALOG_TYPE_USERNAME = 1;
    public static final int DIALOG_TYPE_USERSIGN = 2;
    public static final int DIALOG_TYPE_USERLOCATION = 3;
    public MapView mapView = null;


    private int mDialogType = -1;
    private View mDialogContainer = null;
    private UserInfoEditFragment.UserInfoListener mListener;
    private AMap aMap;
    private OnLocationChangedListener mLocationListener;//定位监听
    private AMapLocationClient mlocationClient;//定位发起端
    private AMapLocationClientOption mLocationOption;//定位参数
    private EditText searchEditText = null;
    private TextView locationText = null;
    private ListView resultListView = null;
    private String searchkeyWord = "";
    private ProgressDialog progDialog = null;
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult;
    private UserLocation userLocation;


    public static MyDialogFragment newInstance(Bundle arg) {
        MyDialogFragment fragment = new MyDialogFragment();
        Bundle args = arg;
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogType = getArguments().getInt(ARGS_DIALOG_TYPE, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mDialogContainer = inflater.inflate(R.layout.dialog_fragment, null);

//        TextView text = (TextView) container.findViewById(R.id.dialog_text);
//        text.setText(R.string.dialog_text);
//        text.setSelected(true);
//        ImageView image = (ImageView) container.findViewById(R.id.dialog_image);
//        image.setImageDrawable(getResources().getDrawable(R.drawable.zhifubao));
        switch (mDialogType) {
            case DIALOG_TYPE_USERNAME:
                initEditUserName();
                break;
            case DIALOG_TYPE_USERSIGN:
                initEditSign();
                break;
            case DIALOG_TYPE_USERLOCATION:
                setUserLocation(savedInstanceState);
                break;
        }


        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(mDialogContainer);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        return dialog;
    }

    private void initEditUserName() {
        View userNameDialog = mDialogContainer.findViewById(R.id.edit_username);
        String nickName = getArguments().getString("nickName");
        final EditText nameText = (EditText) userNameDialog.findViewById(R.id.edit_nick_name);
        nameText.setText(nickName);
        nameText.setSelection(nickName.length());
        userNameDialog.setVisibility(View.VISIBLE);

        Button cancel = (Button) userNameDialog.findViewById(R.id.cancel_edit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogFragment.this.dismiss();
            }
        });

        Button okBtn = (Button) userNameDialog.findViewById(R.id.ok_edit);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser avUser = AVUser.getCurrentUser();
                String name = nameText.getText().toString().trim();
                avUser.put("nickName", name);
                avUser.saveInBackground();
                if (mListener != null) {
                    mListener.onNickNameChange(name);
                }
                MyDialogFragment.this.dismiss();
            }
        });
    }

    private void initEditSign() {
        View signDialog = mDialogContainer.findViewById(R.id.edit_sign);
        String sign = getArguments().getString("sign");
        final EditText signText = (EditText) signDialog.findViewById(R.id.edit_sign_text);
        signText.setText(sign);
        signText.setSelection(sign.length());
        signDialog.setVisibility(View.VISIBLE);

        Button cancel = (Button) signDialog.findViewById(R.id.cancel_edit_sign);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogFragment.this.dismiss();
            }
        });

        Button okBtn = (Button) signDialog.findViewById(R.id.ok_edit_sign);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser avUser = AVUser.getCurrentUser();
                String sign = signText.getText().toString().trim();
                avUser.put("userSignature", sign);
                avUser.saveInBackground();
                if (mListener != null) {
                    mListener.onSignChange(sign);
                }
                MyDialogFragment.this.dismiss();
            }
        });
    }

    private void setUserLocation(Bundle savedInstanceState) {
        View location_view = mDialogContainer.findViewById(R.id.user_location);
        userLocation = new UserLocation();
        searchEditText = (EditText) mDialogContainer.findViewById(R.id.search_text);
        searchEditText.addTextChangedListener(this);
        locationText = (TextView) mDialogContainer.findViewById(R.id.user_location_text);

        //resultListView = (ListView) mDialogContainer.findViewById(R.id.search_result_list);
        //searchResultAdapter = new AmapSearchAdapter(getActivity());
        //resultListView.setAdapter(searchResultAdapter);
        //resultListView.setVisibility(View.GONE);

        mapView = (MapView) location_view.findViewById(R.id.location_map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//显示定位图标
        //MyLocationStyle myLocationStyle = new MyLocationStyle();
        //myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        //myLocationStyle.strokeColor(Color.RED);
        //aMap.setMyLocationStyle(myLocationStyle);//自定义marker
        aMap.setOnMarkerClickListener(this);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.i("location", location.toString());
                userLocation.setAddress(((AMapLocation) location).getAddress());
                userLocation.setLongitude(location.getLongitude());
                userLocation.setLatitude(location.getLatitude());
                locationText.setText(userLocation.getAddress());
            }
        });
        location_view.setVisibility(View.VISIBLE);

        Button okBtn = (Button) location_view.findViewById(R.id.set_location);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser avUser = AVUser.getCurrentUser();
                String locationAddress = locationText.getText().toString().trim();
                avUser.put("locationAddress", locationAddress);
                avUser.put("locationLongitude", userLocation.getLongitude() + "");
                avUser.put("locationLatitude", userLocation.getLatitude() + "");
                avUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {

                        } else {
                            Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (mListener != null) {
                    mListener.onLocationChange(locationAddress);
                }
                MyDialogFragment.this.dismiss();
            }
        });

        Button cancel_position = (Button) location_view.findViewById(R.id.cancel_location);
        cancel_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogFragment.this.dismiss();
            }
        });
    }

    public void registerUserInfoListener(UserInfoEditFragment.UserInfoListener listener) {
        mListener = listener;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mLocationListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(final AMapLocation aMapLocation) {
        if (mLocationListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                double longitude = aMapLocation.getLongitude();//经度
                double latitude = aMapLocation.getLatitude();//纬度
                LatLng currentLocation = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition(currentLocation, 16, 0, 0);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                aMap.animateCamera(cameraUpdate, 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        mLocationListener.onLocationChanged(aMapLocation);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                userLocation.setAddress(aMapLocation.getAddress());
                userLocation.setLongitude(aMapLocation.getLongitude());
                userLocation.setLatitude(aMapLocation.getLatitude());
                locationText.setText(userLocation.getAddress());
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(getActivity(), errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            searchkeyWord = checkEditText();
            //showProgressDialog();
            currentPage = 0;
            query = new PoiSearch.Query(searchkeyWord, "", "");
            query.setPageSize(30);
            query.setPageNum(currentPage);

            poiSearch = new PoiSearch(getActivity(), query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public String checkEditText() {
        if (searchEditText != null && searchEditText.getText() != null
                && !(searchEditText.getText().toString().trim().equals(""))) {
            return searchEditText.getText().toString().trim();
        } else {
            return "";
        }
    }

    @Override
    public void onPoiSearched(PoiResult searchResult, int i) {
        //dissmissProgressDialog();
        if (i == 1000) {
            if (searchResult != null && searchResult.getQuery() != null) {
                //resultListView.setVisibility(View.VISIBLE);
                poiResult = searchResult;
                List<PoiItem> poiItems = poiResult.getPois();
                //searchResultAdapter.registerResultData(poiItems);
                //searchResultAdapter.notifyDataSetChanged();
                if (poiItems != null && poiItems.size() > 0) {
                    userLocation.setProvince(poiItems.get(0).getProvinceName());
                    userLocation.setCity(poiItems.get(0).getCityName());
                    userLocation.setArea(poiItems.get(0).getAdName());
                    userLocation.setAddress(poiItems.get(0).getSnippet());
                    userLocation.setPoi_name(poiItems.get(0).getTitle());
                    userLocation.setLongitude(poiItems.get(0).getLatLonPoint().getLongitude());
                    userLocation.setLatitude(poiItems.get(0).getLatLonPoint().getLatitude());
                    locationText.setText(userLocation.getProvince() + userLocation.getCity() + userLocation.getArea() + userLocation.getAddress() + userLocation.getPoi_name());

                    aMap.clear();// 清理之前的图标
                    PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                    poiOverlay.removeFromMap();
                    poiOverlay.addToMap();
                    poiOverlay.zoomToSpan();
                }
            } else {
                //resultListView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "没有搜索结果", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "错误码：" + i, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        //Toast.makeText(getActivity(),marker.getTitle() + marker.getSnippet(), Toast.LENGTH_LONG).show();
        userLocation.setAddress(marker.getSnippet());
        userLocation.setPoi_name(marker.getTitle());
        userLocation.setLongitude(marker.getPosition().longitude);
        userLocation.setLatitude(marker.getPosition().latitude);

        locationText.setText(userLocation.getProvince() + userLocation.getCity() + userLocation.getArea() + userLocation.getAddress() + userLocation.getPoi_name());

        return false;
    }

    public static class UserLocation {
        private String province = null;
        private String city = null;
        private String area = null;
        private String address = null;
        private String poi_name = null;
        private double longitude = 0.0;
        private double latitude = 0.0;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPoi_name() {
            return poi_name;
        }

        public void setPoi_name(String poi_name) {
            this.poi_name = poi_name;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
