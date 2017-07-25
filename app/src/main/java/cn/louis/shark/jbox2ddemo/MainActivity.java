package cn.louis.shark.jbox2ddemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor defaultSensor;
    private Collision collisionView;

    private int[] imgs = {
            R.mipmap.share_fb,
            R.mipmap.share_kongjian,
            R.mipmap.share_pyq,
            R.mipmap.share_qq,
            R.mipmap.share_tw,
            R.mipmap.share_wechat,
            R.mipmap.share_weibo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        collisionView = (Collision) findViewById(R.id.collision);
        initView();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    private void initView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        for (int i = 0; i < imgs.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imgs[i]);
            imageView.setTag(R.id.view_circle_tag, true);
            collisionView.addView(imageView,layoutParams);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1] * 6;
            collisionView.onSensorChanged(-x, y);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
