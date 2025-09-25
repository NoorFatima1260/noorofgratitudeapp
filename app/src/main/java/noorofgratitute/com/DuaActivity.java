package noorofgratitute.com;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class DuaActivity extends AppCompatActivity {
    private RecyclerView duaRecyclerView;
    private SearchView searchView;
    private ArrayList<DuaModel> duaList;
    private DuaAdapter duaAdapter;
    private ImageButton backButoon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dua);
        searchView = findViewById(R.id.searchView);
        duaRecyclerView = findViewById(R.id.duaRecyclerView);
        duaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        backButoon = findViewById(R.id.btnBack);
        //dua list
        duaList = new ArrayList<>();
        loadDuaList();
        //adapter
        duaAdapter = new DuaAdapter(this, duaList);
        duaRecyclerView.setAdapter(duaAdapter);
        backButoon.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        //searchview listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                duaAdapter.filter(query);
                return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                duaAdapter.filter(newText);
                return false;}});}
    private void loadDuaList() {
        duaList.add(new DuaModel("Morning Dua",
                "This dua is recited in the morning to thank Allah and seek His blessings.",
                "ٱللَّهُمَّ بِكَ أَصْبَحْنَا وَبِكَ أَمْسَيْنَا وَبِكَ نَحْيَا وَبِكَ نَمُوتُ وَإِلَيْكَ ٱلنُّشُورُ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Evening Dua",
                "This dua is recited in the evening to seek forgiveness and protection from Allah.",
                "ٱللَّهُمَّ أَنْتَ رَبِّي لَا إِلٰهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Before Eating",
                "This dua is recited before eating to seek blessings and thank Allah for His provision.",
                "ٱللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا وَقِنَا عَذَابَ ٱلنَّارِ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("After Eating",
                "This dua is recited after eating to express gratitude to Allah for the food.",
                "ٱلْحَمْدُ لِلَّهِ ٱلَّذِي أَطْعَمَنِي هَذَا، وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Before Leaving Home",
                "This dua is recited before leaving home to seek Allah’s protection and help.",
                "بِسْمِ ٱللَّهِ تَوَكَّلْتُ عَلَى ٱللَّهِ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِٱللَّهِ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Entering Home",
                "This dua is recited when entering the home to seek blessings and peace from Allah.",
                "ٱللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ ٱلْمَوْلَجِ وَخَيْرَ ٱلْمَخْرَجِ، بِسْمِ ٱللَّهِ وَلَجْنَا وَبِسْمِ ٱللَّهِ خَرَجْنَا وَعَلَى ٱللَّهِ رَبِّنَا تَوَكَّلْنَا.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Wearing Clothes",
                "This dua is recited when wearing clothes to thank Allah for His blessings.",
                "ٱلْحَمْدُ لِلَّهِ ٱلَّذِي كَسَانِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ.",
                0, 0));
        duaList.add(new DuaModel("Before Sleeping",
                "This dua is recited before sleeping to sleep in the protection of Allah.",
                "بِٱسْمِكَ ٱللَّهُمَّ أَمُوتُ وَأَحْيَا.",
                0, 0));
        duaList.add(new DuaModel("Wearing New Clothes",
                "This dua is recited when wearing new clothes to ask Allah for their goodness and protection.",
                "ٱللَّهُمَّ لَكَ ٱلْحَمْدُ أَنْتَ كَسَوْتَنِيهِ، أَسْأَلُكَ مِنْ خَيْرِهِ وَخَيْرِ مَا صُنِعَ لَهُ وَأَعُوذُ بِكَ مِنْ شَرِّهِ وَشَرِّ مَا صُنِعَ لَهُ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("After Waking Up",
                "This dua is recited after waking up to thank Allah for life and renewal.",
                "ٱلْحَمْدُ لِلَّهِ ٱلَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ ٱلنُّشُورُ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("After Wudu",
                "This dua is recited after performing wudu to seek purity and mercy from Allah.",
                "ٱللَّهُمَّ ٱجْعَلْنِي مِنَ ٱلتَّوَّابِينَ وَٱجْعَلْنِي مِنَ ٱلْمُتَطَهِّرِينَ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("During Hardship",
                "This dua is recited in times of hardship to seek help and patience from Allah.",
                "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ، ٱللَّهُمَّ أَجِرْنِي فِي مُصِيبَتِي وَٱخْلُفْ لِي خَيْرًا مِنْهَا.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("In Difficult Times",
                "This dua is recited in difficult times to place trust and reliance on Allah.",
                "حَسْبُنَا ٱللَّهُ وَنِعْمَ ٱلْوَكِيلُ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("For Seeking Cure",
                "This dua is recited when seeking a cure and mercy from Allah during sickness.",
                "ٱللَّهُمَّ رَبَّ ٱلنَّاسِ، أَذْهِبِ ٱلْبَأْسَ، ٱشْفِ أَنْتَ ٱلشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("For Parents",
                "This dua is recited for parents to ask for mercy and forgiveness for them.",
                "رَّبِّ ٱرْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("First Ashra Dua (Days 1-10)",
                "This dua is recited in the first ten days of Ramadan, which are for seeking Allah’s mercy.",
                "رَبِّ ٱغْفِرْ وَٱرْحَمْ وَأَنتَ خَيْرُ ٱلرَّٰحِمِينَ",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Second Ashra Dua (Days 11-20)",
                "This dua is recited in the second ten days of Ramadan, which are for seeking forgiveness.",
                "أَسْتَغْفِرُ اللَّهَ رَبِّي مِنْ كُلِّ ذَنْبٍ وَأَتُوبُ إِلَيْهِ",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Third Ashra Dua (Days 21-30)",
                "This dua is recited in the last ten days of Ramadan, which are for seeking protection from Hellfire.",
                "اللَّهُمَّ أَجِرْنَا مِنَ النَّارِ",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Dua for Keeping Fast (Suhoor)",
                "This dua is recited before starting the fast to make the intention (niyyah).",
                "وَبِصَوْمِ غَدٍ نَوَيْتُ مِنْ شَهْرِ رَمَضَانَ.",
                0, R.raw.azan1));
        duaList.add(new DuaModel("Dua for Breaking Fast (Iftar)",
                "This dua is recited at the time of breaking the fast to thank Allah.",
                "اللَّهُمَّ إِنِّي لَكَ صُمْتُ وَبِكَ آمَنْتُ وَعَلَيْكَ تَوَكَّلْتُ وَعَلَىٰ رِزْقِكَ أَفْطَرْتُ.",
                0, R.raw.azan1));}}
