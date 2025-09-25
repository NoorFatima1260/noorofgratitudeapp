package noorofgratitute.com;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
public class QiblaActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private ImageView compassImage, qiblaIndicator;
    private TextView directionText, gpsStatus;
    private SensorManager sensorManager;
    private ImageButton btnBack;
    private float compassCurrentDegree = 0f;  //compass image rotation
    private float qiblaCurrentDegree = 0f;    //Qibla indicator rotation
    private LocationManager locationManager;
    private double qiblaDirection = 0;
    private boolean isLocationAvailable = false;
    private float[] gravity;
    private float[] geomagnetic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qibla);
        compassImage = findViewById(R.id.compassImage);
        qiblaIndicator = findViewById(R.id.qiblaIndicator);
        directionText = findViewById(R.id.directionText);
        gpsStatus = findViewById(R.id.gpsStatus);
        btnBack = findViewById(R.id.btnBack);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        } }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
        }
        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);
                if (azimuth < 0) azimuth += 360;
                Log.d("Compass", "Azimuth: " + azimuth);
                //animate compass image rotation using objectanimator
                ObjectAnimator compassAnimator = ObjectAnimator.ofFloat(compassImage, "rotation", compassCurrentDegree, azimuth);
                compassAnimator.setDuration(500);
                compassAnimator.setInterpolator(new DecelerateInterpolator());
                compassAnimator.start();
                compassCurrentDegree = azimuth;
                //updating Qibla indicator if location is available
                if (isLocationAvailable) {
                    updateQiblaIndicator(azimuth);
                } } } }
    private void updateQiblaIndicator(float currentAzimuth) {
        //calculate angle for the Qibla indicator
        float desiredAngle = (float) ((qiblaDirection - currentAzimuth + 360) % 360);
        Log.d("Qibla", "Desired Qibla angle: " + desiredAngle);
        //animate Qibla indicator rotation using objectanimator
        ObjectAnimator qiblaAnimator = ObjectAnimator.ofFloat(qiblaIndicator, "rotation", qiblaCurrentDegree, desiredAngle);
        qiblaAnimator.setDuration(500);
        qiblaAnimator.setInterpolator(new DecelerateInterpolator());
        qiblaAnimator.start();
        qiblaCurrentDegree = desiredAngle;
        // If within 5° of the desired angle, change indicator color to red
        if (Math.abs(desiredAngle) < 5) {
            qiblaIndicator.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
            directionText.setText("Facing Qibla");
        } else {
            qiblaIndicator.setColorFilter(getResources().getColor(android.R.color.black));
            directionText.setText("Adjust to find Qibla");
        } }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        isLocationAvailable = true;
        gpsStatus.setText("GPS: Active");
        qiblaDirection = getQiblaDirection(location.getLatitude(), location.getLongitude());
        Log.d("GPS", "Location: Lat " + location.getLatitude() + ", Lon " + location.getLongitude() +
                ", QiblaDirection: " + qiblaDirection);
    }
    private double getQiblaDirection(double lat, double lon) {
        double kaabaLat = 21.4225;
        double kaabaLon = 39.8262;
        double deltaLon = Math.toRadians(kaabaLon - lon);
        double lat1 = Math.toRadians(lat);
        double lat2 = Math.toRadians(kaabaLat);
        double y = Math.sin(deltaLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Log sensor accuracy changes if needed
        Log.d("SensorAccuracy", sensor.getName() + " accuracy changed: " + accuracy);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (accelerometerSensor != null && magneticSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
        } }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        gpsStatus.setText("GPS: Disabled");
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        gpsStatus.setText("GPS: Searching...");
    } }