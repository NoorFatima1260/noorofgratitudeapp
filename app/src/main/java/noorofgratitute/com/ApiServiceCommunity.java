package noorofgratitute.com;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
public interface ApiServiceCommunity {
    // post content gratitude
    @Multipart
    @POST("/posts/create/")
    Call<ResponseBody> createPost(
            @Header("Authorization") String token,
            @Part("content") RequestBody content,
            @Part("privacy") RequestBody privacy,
            @Part MultipartBody.Part image
              ,@Part MultipartBody.Part file);
    //get post content in communitysupport activity
    @GET("/posts/")
    Call<List<CommunityPost>> getCommunityPosts();
    //edit post
    @PUT("/posts/{post_id}/edit/")
    Call<ResponseBody> editPost(
            @Header("Authorization") String token,
            @Path("post_id") int postId,
            @Body PostEditRequest request
    );
    //delete post
    @DELETE("posts/{id}/delete/")
    Call<ResponseBody> deletePost(
            @Header("Authorization") String token,
            @Path("id") int postId
    );


    // post reactions button like
    @POST("/posts/{postId}/reaction/")
    Call<ResponseBody> sendReaction(
            @Header("Authorization") String token,
            @retrofit2.http.Path("postId") int postId,
            @retrofit2.http.Body ReactionRequest reactionRequest);
    // reaction reactiontotal use
    @GET("api/posts/{post_id}/reactions/")
    Call<List<ReactionDetail>> getReactions(
            @Header("Authorization") String token,
            @Path("post_id") int postId);
// comments reactions like dislike
    @POST("/post/{postId}/comment/")
    Call<ResponseBody> sendComment(
            @Header("Authorization") String token,
            @retrofit2.http.Path("postId") int postId,
            @retrofit2.http.Body CommentRequest commentRequest);
    @GET("/posts/{postId}/")
    Call<CommunityPost> getPostById(
            @Header("Authorization") String token,
            @Path("postId") int postId);
//comment reaction
@POST("/comments/{commentId}/reaction/")
Call<ResponseBody> toggleCommentReaction(
        @Header("Authorization") String token,
        @Path("commentId") int commentId,
        @Body ReactionRequest reactionRequest);
    //edit comment
    @PUT("/api/comments/{commentId}/edit/")
    Call<ResponseBody> editComment(
            @Header("Authorization") String token,
            @Path("commentId") int commentId,
            @Body CommentRequest commentRequest);
    //delete comment
    @DELETE("/api/comments/{commentId}/delete/")
    Call<ResponseBody> deleteComment(
            @Header("Authorization") String token,
            @Path("commentId") int commentId);
    // reply Comment
    @POST("/posts/{postId}/comments/{parentCommentId}/reply/")
    Call<ResponseBody> replyToComment(
            @Header("Authorization") String token,
            @Path("postId") int postId,
            @Path("parentCommentId") int parentCommentId,
            @Body CommentRequest commentRequest);
    @GET("/posts/{postId}/comments/")
    Call<List<CommentDetail>> getDetailComments(
            @Header("Authorization") String token,
            @Path("postId") int postId);}


