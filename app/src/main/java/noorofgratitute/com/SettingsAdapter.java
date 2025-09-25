package noorofgratitute.com;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private ImageButton btnBack;
    private Context context;
    private List<SettingsItem> settingsList;
    public SettingsAdapter(Context context, List<SettingsItem> settingsList) {
        this.context = context;
        this.settingsList = settingsList;
    }
    @Override
    public int getItemViewType(int position) {
        // Differentiate between categories and items based on the 'isCategory' flag
        return settingsList.get(position).isCategory() ? VIEW_TYPE_CATEGORY : VIEW_TYPE_ITEM;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate different layouts for categories and items
        if (viewType == VIEW_TYPE_CATEGORY) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_setting, parent, false);
            return new ItemViewHolder(view);
        } }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SettingsItem item = settingsList.get(position);
        if (holder instanceof CategoryViewHolder) {
            ((CategoryViewHolder) holder).title.setText(item.getTitle());
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).title.setText(item.getTitle());
            //click listener for each item
            holder.itemView.setOnClickListener(v -> {
                //handle navigation based on item title
                Intent intent = null;
                switch (item.getTitle()) {
                    case "Profile":
                        intent = new Intent(context, ProfileActivity.class);
                        break;
                    case "Notifications":
                        intent = new Intent(context, NotificationsActivity.class);
                        break;
                    case "Privacy":
                        intent = new Intent(context, PrivacyActivity.class);
                        break;
                    default:
                           break;
                }
                if (intent != null) {
                    context.startActivity(intent);
                } });
        } }
    @Override
    public int getItemCount() {
        return settingsList.size(); // Return the size of the list
    }
    //for category
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_title); // Bind category title
        } }
    //for settings Item
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.setting_title); // Bind item title
        } } }
