package noorofgratitute.com;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText emailEditText;
    private MaterialButton resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);
        TextView backToLoginText = findViewById(R.id.backToLoginText);

        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // current activity band ho jaye
        });

        resetButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Enter your email");
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, PasswordResetSuccessActivity.class);
                            intent.putExtra("email", email); // email bhej rahe hain
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }

    private void showPopupDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_popup, null);
        builder.setView(dialogView);
        TextView popupTitle = dialogView.findViewById(R.id.popupTitle);
        TextView popupMessage = dialogView.findViewById(R.id.popupMessage);
        Button closeBtn = dialogView.findViewById(R.id.closePopupBtn);
        popupTitle.setText(title);
        popupMessage.setText(message);
        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.show();
        closeBtn.setOnClickListener(v -> {
            alert.dismiss();
            finish(); // back to login
        });
    }
}
