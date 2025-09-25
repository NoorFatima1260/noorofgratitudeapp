package noorofgratitute.com;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Hamesha app MainActivity se start karegi
        startActivity(new Intent(SplashActivity.this, MainActivity.class));

        // Splash band kar dena
        finish();

        finish();
    }
}

