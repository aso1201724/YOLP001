package jp.ac.st.asojuku.yolp001;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.PinOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay.RouteOverlayListener;
import jp.co.yahoo.android.maps.weather.WeatherOverlay;
import jp.co.yahoo.android.maps.weather.WeatherOverlay.WeatherOverlayListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener, WeatherOverlayListener, RouteOverlayListener, MapView.MapTouchListener {

		//LocationManagerを準備
		LocationManager mLocationManager = null;
		//MapViewを準備
		MapView mMapView = null;
		//直前の緯度（1000000倍精度）
		int lastLatitude = 0;
		//直前の経度（1000000倍精度）
		int lastLongitude = 0;

		WeatherOverlay mWweatherOverlay = null;

		PinOverlay mPinOverlay=null;//開始位置のピン
	    RouteOverlay mRouteOverlay=null;//ルート検索Overlay
	    GeoPoint mStartPos;//出発地(緯度経度)
	    GeoPoint mGoalPos;//目的地(緯度経度)
	    ProgressDialog mProgDialog = null;//プログレスダイアログ
	    TextView mDistLabel=null;//距離表示用テキストビュー
	    static final int MENUITEM_CLEAR = 1;//メニュー（クリア）


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //クリアメニューを追加
        menu.removeItem(MENUITEM_CLEAR);
        menu.add(0, MENUITEM_CLEAR, 0, "クリア");
        return true;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //メニュー選択処理
        switch (item.getItemId()) {
            case MENUITEM_CLEAR:
                //地図上からルートと距離表示をクリア
                if(mMapView!=null){
                    mMapView.getOverlays().remove(mRouteOverlay);
                    mRouteOverlay = null;
                    if(mDistLabel!=null) mDistLabel.setVisibility(View.INVISIBLE);
                }
                return true;
        }
        return false;
    }



	@Override
	public void errorUpdateWeather(WeatherOverlay arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void finishUpdateWeather(WeatherOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		mMapView = new MapView(this,"dj0zaiZpPTdhZ1hERlB4QU01ViZzPWNvbnN1bWVyc2VjcmV0Jng9Mjg-");
		mMapView.setBuiltInZoomControls(true);
		mMapView.setScalebar(true);

		double lat = 35.658516;
		double lon = 139.701773;
		GeoPoint gp = new GeoPoint((int)(lat * 1000000),(int)(lon * 1000000));
		MapController c = mMapView.getMapController();

		c.setCenter(gp);
		c.setZoom(3);
		setContentView(mMapView);

		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();

		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		String provider = mLocationManager.getBestProvider(criteria, true);

		mLocationManager.requestLocationUpdates(provider, 0, 0, this);

		mWweatherOverlay = new WeatherOverlay(this);

		mWweatherOverlay.setWeatherOverlayListener(this);

		mWweatherOverlay.startAutoUpdate(1);

		mMapView.getOverlays().add(mWweatherOverlay);


	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ

		double lat = location.getLatitude();
		int latitude = (int)(lat * 1000000);

		double lon = location.getLongitude();
		int longitude = (int)(lon * 1000000);

		if(latitude/1000 != this.lastLatitude/1000 || longitude/1000 != this.lastLongitude/1000) {
			GeoPoint gp = new GeoPoint(latitude, longitude);
			MapController c = mMapView.getMapController();
			c.setCenter(gp);

			this.lastLatitude = latitude;
			this.lastLongitude = longitude;


		}

		//プログレスダイアログを停止
        if(mProgDialog!=null){
            mProgDialog.dismiss();
            mProgDialog = null;
        }
        //LocationManagerを停止
        mLocationManager.removeUpdates(this);

        //MapViewを作成
        mMapView = new MapView(this,"dj0zaiZpPTdhZ1hERlB4QU01ViZzPWNvbnN1bWVyc2VjcmV0Jng9Mjg-");

        //地図の表示位置をLocationManagerで取得した位置に変更
        MapController mapController = mMapView.getMapController();
        GeoPoint centerPos = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
        mapController.setCenter(centerPos);

        //ズームレベルをセット
        mapController.setZoom(4);

        //地図上で長押しイベントの発行を許可する
        mMapView.setLongPress(true);

        //MapTouchListenerを設定
        mMapView.setMapTouchListener(this);

        //LocationManagerで取得した位置にピンを立てる
        mPinOverlay = new PinOverlay(PinOverlay.PIN_VIOLET);
        mMapView.getOverlays().add(mPinOverlay);
        mPinOverlay.addPoint(centerPos,null);

        //LocationManagerで取得した位置をルート開始位置とします
        mStartPos = centerPos;

        //MapViewをカレントビューに追加
        setContentView(mMapView);



	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onLongPress(MapView map, Object obj, PinOverlay pin,GeoPoint point) {
        //前回の処理を停止
        if(mRouteOverlay!=null) mRouteOverlay.cancel();
        //目的地を設定
        mGoalPos = point;

        //長押しピンをクリア onLongPressが発生すると自動的にピンが追加されるので、ここで削除しておきます。
        mMapView.getOverlays().remove(pin);

        //前回のRouteOverlayを地図から削除
        mMapView.getOverlays().remove(mRouteOverlay);

        //距離テキストビューを非表示
        if(mDistLabel!=null) mDistLabel.setVisibility(View.INVISIBLE);

        //RouteOverlay作成
        mRouteOverlay = new RouteOverlay(this,"＜あなたのアプリケーションID＞");

        //出発地ピン吹き出し設定
        mRouteOverlay.setStartTitle("出発地");

        //目的地ピン吹き出し設定
        mRouteOverlay.setGoalTitle("目的地");

        //出発地、目的地、移動手段を設定
        mRouteOverlay.setRoutePos(mStartPos, mGoalPos, RouteOverlay.TRAFFIC_WALK);

        //RouteOverlayListenerの設定
        mRouteOverlay.setRouteOverlayListener(this);

        //検索を開始
        mRouteOverlay.search();

        //MapViewにRouteOverlayを追加
        mMapView.getOverlays().add(mRouteOverlay);

        return false;
    }



	@Override
	public boolean onPinchIn(MapView arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onPinchOut(MapView arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onTouch(MapView arg0, MotionEvent arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean errorRouteSearch(RouteOverlay arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public boolean finishRouteSearch(RouteOverlay routeOverlay) {
        //プログレスダイアログを消します
        if(mProgDialog!=null){
            mProgDialog.dismiss();
            mProgDialog = null;
        }

        //距離テキストビューを表示
        if(mDistLabel!=null){
            mDistLabel.setVisibility(View.VISIBLE);
        }else{
            mDistLabel = new TextView(this);
            this.addContentView(mDistLabel, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        }
        mDistLabel.setTextSize(20);
        mDistLabel.setTextColor(Color.argb(255, 255, 255, 255));
        mDistLabel.setBackgroundColor(Color.argb(127, 0, 0, 0));

        //距離を設定
        mDistLabel.setText(String.format("距離　%.3fキロメートル",(routeOverlay.getDistance()/1000)));
        return false;
    }


    protected boolean isRouteDisplayed() {
        return false;
    }




}
