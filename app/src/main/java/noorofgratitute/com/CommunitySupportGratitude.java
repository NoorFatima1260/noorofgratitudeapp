package noorofgratitute.com;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CommunitySupportGratitude extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommunityPostAdapter adapter;
    private List<CommunityPost> postList;
    private FloatingActionButton btnAddPost;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AuthUtils.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        FirebaseMessaging.getInstance().subscribeToTopic("gratitude_reminders")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed to gratitude_reminders" : "Subscription failed";
                    Log.d("FCM", msg);
                });
        setContentView(R.layout.activity_community_support_gratitude);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        adapter = new CommunityPostAdapter(this, postList);
        recyclerView.setAdapter(adapter);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        //handle post via intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("gratitude_text")) {
            String text = intent.getStringExtra("gratitude_text");
            String username = intent.getStringExtra("username");
            String timestamp = intent.getStringExtra("timestamp");
            String imageUrl = intent.getStringExtra("image_url");
            String fileUrl = intent.getStringExtra("file_url");
            String fileType = intent.getStringExtra("file_type");
            int postId = intent.getIntExtra("post_id", -1);

            CommunityPost post = new CommunityPost();
            post.setFileUrl(fileUrl);
            post.setId(postId);
            post.setText(text);
            post.setUsername(username);
            post.setTimestamp(timestamp);
            post.setImageUrl(imageUrl);
            post.setFileUrl(fileUrl);
            postList.add(0, post);
            adapter.notifyItemInserted(0);}}
    //retrofit fetch function
    private void fetchCommunityPosts() {
        ApiServiceCommunity service = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
        service.getCommunityPosts().enqueue(new Callback<List<CommunityPost>>() {
            @Override
            public void onResponse(Call<List<CommunityPost>> call, Response<List<CommunityPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    adapter.notifyDataSetChanged();}}
            @Override
            public void onFailure(Call<List<CommunityPost>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();}});}}