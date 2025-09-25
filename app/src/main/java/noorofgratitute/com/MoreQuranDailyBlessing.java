package noorofgratitute.com;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MoreQuranDailyBlessing extends AppCompatActivity {
    private CardView cardQuran, cardDua;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_quran_daily_blessing);
        //cardviews
        cardQuran = findViewById(R.id.card_Quran);
        cardDua = findViewById(R.id.card_Dua);
        btnBack = findViewById(R.id.btnBack);
        //click listeners of cardviews
        cardQuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open QuranActivity
                Intent intent = new Intent(MoreQuranDailyBlessing.this, QuranActivity.class);
                startActivity(intent);
            } });
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        cardDua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open DuaActivity
                Intent intent = new Intent(MoreQuranDailyBlessing.this, DuaActivity.class);
                startActivity(intent);
            }}); } }
