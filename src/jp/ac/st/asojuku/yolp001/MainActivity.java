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
import android.view.ViewGroup;
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

		PinOverlay mPinOverlay=null;//ルート検索Overlay
		RouteOverlay mRouteOverlay = null;
	    GeoPoint mStartPos;//出発地(緯度経度)
	    GeoPoint mGoalPos;//目的地(緯度経度)
	    //ProgressDialog mProgDialog = null;//プログレスダイアログ
	    TextView mDistLabel=null;//距離表示用テキストビュー
	    //LocationManager mLocationManager = null;//ロケーションマネージャー
	    static final int MENUITEM_CLEAR = 1;//メニュー（クリア）


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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



		mMapView.setLongPress(true);

		mMapView.setMapTouchListener(this);

		mPinOverlay = new PinOverlay(PinOverlay.PIN_VIOLET);
		mMapView.getOverlays().add(mPinOverlay);
		mPinOverlay.addPoint(gp, null);

		mStartPos = gp;






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

			mPinOverlay = new PinOverlay(PinOverlay.PIN_VIOLET);
			mMapView.getOverlays().add(mPinOverlay);
			mPinOverlay.addPoint(gp, null);

			mStartPos = gp;


		}





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

		menu.removeItem(100);
		menu.removeItem(MENUITEM_CLEAR);
		menu.add(0, MENUITEM_CLEAR, 0, "クリア");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ

		switch (item.getItemId()) {
		case MENUITEM_CLEAR:
			if(mMapView!=null) {
				mMapView.getOverlays().remove(mRouteOverlay);
				mRouteOverlay = null;
				if(mDistLabel!=null) mDistLabel.setVisibility(View.INVISIBLE);
			}
			return true;
		}
		return false;
	}


	@Override
	public boolean onLongPress(MapView arg0, Object arg1, PinOverlay arg2, GeoPoint arg3) {
		// TODO 自動生成されたメソッド・スタブ

		if(mRouteOverlay!=null){
			mRouteOverlay.cancel();
		}

		mGoalPos = arg3;

		mMapView.getOverlays().remove(mRouteOverlay);

		mMapView.getOverlays().remove(mRouteOverlay);

		if(mDistLabel!=null){
			mDistLabel.setVisibility(View.INVISIBLE);
		}

		mRouteOverlay = new RouteOverlay(this,"dj0zaiZpPTdhZ1hERlB4QU01ViZzPWNvbnN1bWVyc2VjcmV0Jng9Mjg-");

		mRouteOverlay.setStartTitle("出発地");

		mRouteOverlay.setGoalTitle("目的地");

		mRouteOverlay.setRoutePos(mStartPos, mGoalPos, RouteOverlay.TRAFFIC_WALK);

		mRouteOverlay.setRouteOverlayListener(this);

		mRouteOverlay.search();

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


	@Override
	public boolean finishRouteSearch(RouteOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ
		if(mDistLabel!=null){
			mDistLabel.setVisibility(View.VISIBLE);
		}else{
			mDistLabel = new TextView(this);
			this.addContentView((View)mDistLabel, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		mDistLabel.setTextSize(20);
		mDistLabel.setTextColor(Color.argb(255, 255, 255, 255));
		mDistLabel.setBackgroundColor(Color.argb(127, 0, 0, 0));

		mDistLabel.setText(String.format("距離　%.3fキロメートル", (arg0.getDistance()/1000)));



		return false;
	}





}
