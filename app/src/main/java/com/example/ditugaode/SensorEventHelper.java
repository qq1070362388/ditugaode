package com.example.ditugaode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.amap.api.maps.model.Marker;

/**
 * 方向传感器
 */
public class SensorEventHelper implements SensorEventListener {

    private static final String TAG = "SensorEventHelper";
    private final int TIME_SENSOR = 100;
    private SensorManager sensorManager;
    private Sensor sensor;
    private long lastTime = 0;
    private float mAngle;
    private Context context;
    private Marker mMarker;
    private boolean rotate = true;//是否可以旋转marker
    private float rotation;//旋转角度

    public SensorEventHelper(Context context){
        this.context = context;
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    /**
     * 获取当前屏幕旋转角度
     *
     * @param context
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static int  getScreenRotationOnPhone(Context context){
        final Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()){
            case Surface.ROTATION_0:
                return 0;

            case Surface.ROTATION_90:
                return 90;

            case Surface.ROTATION_180:
                return 180;

            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    public void registerSensorListener() {
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterSensorListener() {
        sensorManager.unregisterListener(this, sensor);
    }

    public void setCurrentMarker(Marker marker) {
        mMarker = marker;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(System.currentTimeMillis() - lastTime < TIME_SENSOR){
            return;
        }
        switch (event.sensor.getType()){
            case Sensor.TYPE_ORIENTATION:
                if(!rotate){
                    return;
                }
                float x = event.values[0];
                Log.d("zh","onSensorChanged,"+"mLocMarker="+mMarker);
                x += getScreenRotationOnPhone(context);
                x %= 360.0F;
                if (x > 180.0F)
                    x -= 360.0F;
                else if (x < -180.0F)
                    x += 360.0F;
                if (Math.abs(mAngle - x) < 3.0f) {
                    break;
                }
                mAngle = Float.isNaN(x) ? 0 : x;
                if (mMarker != null) {
                    rotation = 360 - mAngle;
                    mMarker.setRotateAngle(rotation);
                }
                lastTime = System.currentTimeMillis();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
            * 获取旋转角度
     * @return
             */
    public float getRotationAngle(){
        return rotation;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }
}
