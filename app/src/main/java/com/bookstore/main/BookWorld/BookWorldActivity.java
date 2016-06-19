package com.bookstore.main.BookWorld;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bookstore.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/6/20.
 */
public class BookWorldActivity extends Activity implements AMap.OnMarkerClickListener, LocationSource, AMapLocationListener, AMap.InfoWindowAdapter {
    public MapView mapView = null;
    private AMap aMap;
    private boolean first = true;
    private LocationSource.OnLocationChangedListener mLocationListener;//定位监听
    private AMapLocationClient mlocationClient;//定位发起端
    private AMapLocationClientOption mLocationOption;//定位参数
    private MarkerOptions markerOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookworld);
        mapView = (MapView) findViewById(R.id.book_world_map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//显示定位图标
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setOnMarkerClickListener(this);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.i("location", location.toString());

            }
        });
        aMap.setInfoWindowAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.limit(1000);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject object : list) {
                    String nick_name = object.getString("nickName");
                    String user_name = object.getString("username");
                    double longitude, latitude;
                    String name = (nick_name == null ? user_name : nick_name);
                    String longitude_str = object.getString("locationLongitude");
                    String latitude_str = object.getString("locationLatitude");
                    markerOption = new MarkerOptions();
                    if (longitude_str != null && latitude_str != null) {
                        longitude = Double.valueOf(longitude_str);
                        latitude = Double.valueOf(latitude_str);
                        markerOption.position(new LatLng(latitude, longitude));
                    } else {
                        markerOption.position(new LatLng(123.475139, 25.742234));//没有填地址的都放到钓鱼岛
                    }
                    markerOption.title(name);
                    markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    Marker marker = aMap.addMarker(markerOption);
                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(2000);
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
                mLocationListener.onLocationChanged(aMapLocation);
                if (first) {
                    first = false;
                    double longitude = aMapLocation.getLongitude();//经度
                    double latitude = aMapLocation.getLatitude();//纬度
                    LatLng currentLocation = new LatLng(latitude, longitude);
                    CameraPosition cameraPosition = new CameraPosition(currentLocation, 6, 0, 0);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    aMap.animateCamera(cameraUpdate, 1000, new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
