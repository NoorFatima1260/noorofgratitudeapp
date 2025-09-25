package noorofgratitute.com;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class QuranActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView surahRecyclerView, juzRecyclerView;
    private SurahAdapter surahAdapter;
    private JuzAdapter juzAdapter;
    private ImageButton btnBack;
    private List<Surah> surahList, filteredSurahList;
    private List<Juz> juzList, filteredJuzList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran);
        btnBack = findViewById(R.id.btnBack);
        searchView = findViewById(R.id.searchView);
        surahRecyclerView = findViewById(R.id.surahRecyclerView);
        juzRecyclerView = findViewById(R.id.juzRecyclerView);
        //lists
        surahList = new ArrayList<>();
        juzList = new ArrayList<>();
        filteredSurahList = new ArrayList<>();
        filteredJuzList = new ArrayList<>();
        //load data
        loadSurahs();
        loadJuz();
        filteredSurahList.addAll(surahList);
        filteredJuzList.addAll(juzList);
        //adapters
        surahAdapter = new SurahAdapter(this, filteredSurahList);
        juzAdapter = new JuzAdapter(this, filteredJuzList);
        //adapters to recyclerView
        surahRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        juzRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        surahRecyclerView.setAdapter(surahAdapter);
        juzRecyclerView.setAdapter(juzAdapter);
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        //searchview
        setupSearchView(); }
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            } }); }
    private void filterData(String query) {
        if (query.isEmpty()) {
            filteredSurahList.clear();
            filteredSurahList.addAll(surahList);
            filteredJuzList.clear();
            filteredJuzList.addAll(juzList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredSurahList.clear();
            filteredJuzList.clear();

            for (Surah surah : surahList) {
                if (surah.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredSurahList.add(surah);
                } }
            for (Juz juz : juzList) {
                if (juz.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredJuzList.add(juz);
                } } }
        surahAdapter.notifyDataSetChanged();
        juzAdapter.notifyDataSetChanged();
    }
    private void loadSurahs() {
        QuranApiService api = ApiClientQuran.getService();
        api.getSurahs().enqueue(new Callback<List<Surah>>() {
            @Override
            public void onResponse(Call<List<Surah>> call, Response<List<Surah>> response) {
                if (response.isSuccessful()) {
                    surahList.clear();
                    surahList.addAll(response.body());
                    filteredSurahList.clear();
                    filteredSurahList.addAll(surahList);
                    surahAdapter.notifyDataSetChanged();
                } }
            @Override
            public void onFailure(Call<List<Surah>> call, Throwable t) {
                Toast.makeText(QuranActivity.this, "Failed to load Surahs", Toast.LENGTH_SHORT).show();
            } }); }
    private void loadJuz() {
        QuranApiService api = ApiClientQuran.getService();
        api.getJuzs().enqueue(new Callback<List<Juz>>() {
            @Override
            public void onResponse(Call<List<Juz>> call, Response<List<Juz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    juzList.clear();
                    juzList.addAll(response.body());
                    filteredJuzList.clear();
                    filteredJuzList.addAll(juzList);
                    juzAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(QuranActivity.this, "Failed to load Juz from server", Toast.LENGTH_SHORT).show();
                } }
            @Override
            public void onFailure(Call<List<Juz>> call, Throwable t) {
                Toast.makeText(QuranActivity.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            } }); } }
