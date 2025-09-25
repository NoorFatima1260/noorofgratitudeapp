package noorofgratitute.com;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CommentBottomSheet extends BottomSheetDialogFragment {
    private int postPosition;
    private int postId;
    private CommentListener commentListener;
    public CommentBottomSheet(int position, int postId, CommentListener listener) {
        this.postPosition = position;
        this.postId = postId;
        this.commentListener = listener;}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false);
        EditText etComment = view.findViewById(R.id.etNewComment);
        ImageButton btnSubmit = view.findViewById(R.id.btnSendComment);
        btnSubmit.setOnClickListener(v -> {
            String comment = etComment.getText().toString().trim();
            if (!comment.isEmpty()) {
                TokenManager.getValidToken(requireContext(), new TokenManager.TokenCallback() {
                    @Override
                    public void onTokenReady(String jwt) {
                        sendComment(jwt, comment);
                    }
                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getContext(), "Auth error: " + errorMessage, Toast.LENGTH_SHORT).show();}
                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), "Failed to retrieve token", Toast.LENGTH_SHORT).show();
                    }});
            } else {
                etComment.setError("Enter a comment!"); }});
        return view; }
    private void sendComment(String jwt, String comment) {
        CommentRequest request = new CommentRequest(comment);
        ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
        api.sendComment("Bearer " + jwt, postId, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (commentListener != null) {
                        commentListener.onCommentSubmitted(postPosition, comment);
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to post comment", Toast.LENGTH_SHORT).show();}}
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }});}
    public interface CommentListener {
        void onCommentSubmitted(int position, String comment);
    }}