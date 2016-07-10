package com.zhongyianda.busdriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.AMapNavi;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    public AMapLocationClient mLocationClient = null;

    public static double lastLng = -1;
    public static double lastLat = -1;
    //声明定位回调监听器

    public double destLat;
    public double destLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AVOSCloud.initialize(this, "p6974wyAF311qTN0tvxrw8oT-gzGzoHsz", "VEi5qSwbn4Hh4Q8pauISVFyD");

        ((Button) findViewById(R.id.btn_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GPSNaviActivity.class);
                if (destLat > 0) {
                    intent.putExtra("lat", destLat);
                    intent.putExtra("lng", destLng);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "请稍候",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


//        AMapNavi.getInstance(this).startGPS();

        new AVQuery<>("Routes").getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(),
                            "获得路线错误,请重启",
                            Toast.LENGTH_LONG).show();
                    return;
                }
//初始化定位
                AMapLocationListener mLocationListener = new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        Log.i("NAV", "long" + aMapLocation.getLongitude());
                        lastLng = aMapLocation.getLongitude();
                        lastLat = aMapLocation.getLatitude();
                        try {
                            destLat = avObject.getJSONArray("destinationCoordinates").getDouble(1);
                            destLng = avObject.getJSONArray("destinationCoordinates").getDouble(0);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                };
                mLocationClient = new AMapLocationClient(getApplicationContext());
                mLocationClient.setLocationListener(mLocationListener);
                //声明mLocationOption对象
                AMapLocationClientOption mLocationOption = null;
//初始化定位参数
                mLocationOption = new AMapLocationClientOption();
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置是否返回地址信息（默认返回地址信息）
                mLocationOption.setNeedAddress(true);
//设置是否只定位一次,默认为false

                mLocationOption.setOnceLocation(true);
//设置是否强制刷新WIFI，默认为强制刷新
                mLocationOption.setWifiActiveScan(true);
//设置是否允许模拟位置,默认为false，不允许模拟位置
                mLocationOption.setMockEnable(false);
//设置定位间隔,单位毫秒,默认为2000ms
                mLocationOption.setInterval(10000);
//给定位客户端对象设置定位参数
                mLocationClient.setLocationOption(mLocationOption);
//启动定位
                mLocationClient.startLocation();

//                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//                fab.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//
//                        if (lastLng > 0 && lastLng > 0) {
////                if (AMapNavi.getInstance(MainActivity.this).isGpsReady()) {
//                            Snackbar.make(view, "Getting start", Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
//                            Intent intent = new Intent(MainActivity.this, GPSNaviActivity.class);
//                            startActivity(intent);
//                        } else {
//                            Snackbar.make(view, "Not ready", Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
//                        }
//
//                    }
//                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
