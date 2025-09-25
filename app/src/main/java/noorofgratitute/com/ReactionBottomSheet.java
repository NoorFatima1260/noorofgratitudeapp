package noorofgratitute.com;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ReactionBottomSheet extends BottomSheetDialogFragment {
    private int postId;
    public ReactionBottomSheet(int postId) {
        this.postId = postId;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reaction_bottom_sheet, container, false);
        RecyclerView rvReactions = view.findViewById(R.id.rvReactions);
        TextView tvSummary = view.findViewById(R.id.tvSummary);
        rvReactions.setLayoutManager(new LinearLayoutManager(getContext()));
        TokenManager.getValidToken(getContext(), new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                api.getReactions("Bearer " + jwt, postId).enqueue(new Callback<List<ReactionDetail>>() {
                    @Override
                    public void onResponse(Call<List<ReactionDetail>> call, Response<List<ReactionDetail>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ReactionDetail> reactions = response.body();
                            rvReactions.setAdapter(new ReactionAdapter(getContext(), reactions));
                            tvSummary.setText("Total reactions: " + reactions.size());
                        } }
                    @Override
                    public void onFailure(Call<List<ReactionDetail>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error loading reactions", Toast.LENGTH_SHORT).show();
                    } }); }
            @Override public void onError(String errorMessage) {}
            @Override public void onFailure() {} });
        return view; } }
