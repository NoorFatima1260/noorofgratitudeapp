package noorofgratitute.com;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PasswordResetSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset_success);
        TextView successMessage = findViewById(R.id.successMessage);
        Button backToLoginBtn = findViewById(R.id.backToLoginBtn);

        // Email jo ForgotPasswordActivity se aya hai
        String email = getIntent().getStringExtra("email");
        successMessage.setText("We’ve sent a password reset link to:\n" + email);

        backToLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordResetSuccessActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}