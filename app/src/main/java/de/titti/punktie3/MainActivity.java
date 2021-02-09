package de.titti.punktie3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.titti.punktie3.ui.main.CalcFragment;
import de.titti.punktie3.ui.main.ListFragment;
import de.titti.punktie3.ui.main.MyItemRecyclerViewAdapter;
import de.titti.punktie3.ui.main.PageViewModel;
import de.titti.punktie3.ui.main.SectionsPagerAdapter;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

import static de.titti.punktie3.ui.main.CalcFragment.POINT_STORE;

public class MainActivity extends AppCompatActivity  {


    private FoodItemContent foods;
    private Intent intent;
    private int dayPointValueFromCalc;
    private int dayPointValue;
    private HashMap<String, float[]> pointvalues = new HashMap<>();
    private PageViewModel viewModel;
    private SharedPreferences preferencesDayValue;
    private SharedPreferences preferencesList;
    public static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private FloatingActionButton fab;
    private int currentTabPosition = 0;
    private boolean editIsSeekbar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesDayValue = getApplicationContext().getSharedPreferences(PointCalcActivity.DAY_POINTS_STORE, Context.MODE_PRIVATE);
        preferencesList = getApplicationContext().getSharedPreferences(PointCalcActivity.DAY_POINTS_STORE, Context.MODE_PRIVATE);
        intent = getIntent();
        dayPointValueFromCalc = intent.getIntExtra(PointCalcActivity.DAY_POINTS_STORE, 0);
        viewModel = new ViewModelProvider(this).get(PageViewModel.class); //requireActivity()


        setContentView(R.layout.activity_main);

        ///////////test wegen dem menue
        Toolbar mToolbar =  findViewById(R.id.topAppBar);
        this.setSupportActionBar(mToolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        fab = findViewById(R.id.fabList);
//        fab.hide();
        if (!FoodItemContent.hasInstance) foods = new FoodItemContent(getApplicationContext());
//        FoodItemContent.addItem("Apfel", "Stück", 1, 3, 200, 200);

        viewModel.getPointValues().observe(this, new Observer<HashMap<String, float[]>>() {
            @Override
            public void onChanged(HashMap<String, float[]> stringHashMap) {
                pointvalues = stringHashMap;
                savePointList();
            }
        });

        viewModel.getEditIsSeekbar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                editIsSeekbar = aBoolean;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        currentTabPosition = position;
                        fab.setImageResource(R.drawable.ic_baseline_swap_horiz_24);
                        fab.show();
                        return;
                    case 1:
                        currentTabPosition = position;
                        fab.setImageResource(R.drawable.ic_baseline_add);
                        fab.show();
                        return;
                    default:
                        currentTabPosition = position;
                        fab.hide();
                        return;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (currentTabPosition){
                    case 0:
                        viewModel.setEditIsSeekbar(! editIsSeekbar);
                        return;
                    case 1:
                        FoodItemContent.FoodItem currentFoodItem = FoodItemContent.addItem( "Gramm", 100, 0, 0, 0);
                        viewModel.setFoodItemForEdit(currentFoodItem);
                        EditEntries editEntries = new EditEntries();

                        editEntries.show(getSupportFragmentManager(), "editDialog"); //as modal
                        viewModel.setFoodItemForEdit(currentFoodItem);
                        return;
                    default:
                        return;
                }






//                TabLayout tabs = findViewById(R.id.tabs);
//                tabs.getTabAt(1).select();
//
//                viewModel.setTriggerSearch(true); //gucken ob es anders geht




//                MenuItem search = findViewById(R.id.action_search);
//                onOptionsItemSelected(search);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        //pruefen ob ein tagespunktwert vorliegt


    }

    @Override
    protected void onStart() {  //auch beim starten aus dem hintergrund werte neu laden, fals sich das datum geaendert hat.
        super.onStart();
        initPoints();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        System.out.println("menue erstellen");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adap
//                return false;
//            }
//        });

        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.userProfile:
                // hier activity aufrufen
                startActivity(new Intent(this, PointCalcActivity.class));
                return true;
            case R.id.action_search:
                return false;
            default: return super.onOptionsItemSelected(item);
        }

    }




    private void initPoints() {
        loadDayPointValue();
        loadPointList();
        if(dayPointValue == 0){
            startActivity(new Intent(this, PointCalcActivity.class));
            generateInitialFoodList();

        } //pruefen, ob ein intentwert vorliegt
        else if(dayPointValueFromCalc > 0){
            pointvalues.put(df.format(Calendar.getInstance().getTime()), new float[]{dayPointValueFromCalc, 0, 0});
            savePointList();
            viewModel.setPointValues(pointvalues);

        }
        else {//liste mit punktwerten laden
            // pruefen, ob letztes element vom selben tag ist

            if(pointvalues.get(df.format(Calendar.getInstance().getTime())) != null ){
                viewModel.setPointValues(pointvalues);
            }
            else{ //wenn nein, neues listenelement mit startwerten erzeugen, dann speichern, dann uebergeben
                pointvalues.put(df.format(Calendar.getInstance().getTime()), new float[]{dayPointValue, 0, 0});
                savePointList();
                viewModel.setPointValues(pointvalues);
            }
        }

    }

    private void generateInitialFoodList() {
        FoodItemContent.FoodItem foodItem = FoodItemContent.addItem( "Gramm", 50, 2, 0, 0);
        foodItem.name = "Brot";
        viewModel.setFoodItemForUpdate(foodItem);
        foodItem = FoodItemContent.addItem( "Gramm", 0, 0, 0, 0);
        foodItem.name = "Essig";
        viewModel.setFoodItemForUpdate(foodItem);
        foodItem = FoodItemContent.addItem( "Gramm", 0, 0, 0, 0);
        foodItem.name = "Cola Light";
        viewModel.setFoodItemForUpdate(foodItem);
        foodItem = FoodItemContent.addItem( "Gramm", 0, 0, 0, 0);
        foodItem.name = "Gemüsebrühe";
        viewModel.setFoodItemForUpdate(foodItem);
        foodItem = FoodItemContent.addItem( "Gramm", 0, 0, 0, 0);
        foodItem.name = "Kräuter";
        viewModel.setFoodItemForUpdate(foodItem);
        foodItem = FoodItemContent.addItem( "Gramm", 0, 0, 0, 0);
        foodItem.name = "Senf";
        viewModel.setFoodItemForUpdate(foodItem);
        foodItem = FoodItemContent.addItem( "Gramm", 0, 0, 0, 0);
        foodItem.name = "Süßstoff";
        viewModel.setFoodItemForUpdate(foodItem);
        foodItem = FoodItemContent.addItem( "Gramm", 0, 0, 0, 0);
        foodItem.name = "Honig";
        viewModel.setFoodItemForUpdate(foodItem);
    }


    private void loadDayPointValue(){
        if(preferencesDayValue.contains(PointCalcActivity.DAY_POINTS_STORE)){
            dayPointValue = preferencesDayValue.getInt(PointCalcActivity.DAY_POINTS_STORE, 0);
        }

    }
    private void saveDayPointValue(){

    }
    private void loadPointList(){
        Gson gson = new Gson();
        String json = preferencesList.getString(POINT_STORE, "");
        if (json.length() > 0){
            Type type = new TypeToken<HashMap<String, float[]>>() {}.getType();
            pointvalues = gson.fromJson(json, type);
            System.out.println("anzahl der einträge: " +pointvalues.size());
            System.out.println("in main geladen");


        }
    }
    private void savePointList(){
        SharedPreferences.Editor editor = preferencesList.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pointvalues);
        editor.putString(POINT_STORE, json);
        editor.commit();
//        System.out.println("nach speichern");
//        float[] temp = pointvalues.get(df.format(Calendar.getInstance().getTime()));
//        for (int i = 0; i < temp.length; i++){
//            System.out.println(i + ": " + temp[i]);
//        }
    }

//    @Override
//    public void addToDay(View v, float points) {
//        //vom tagesbedarf abziehen und zum rechner wechseln
////        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + ViewPager.getCurrentItem());
//        TabLayout tabs = findViewById(R.id.tabs);
//        tabs.getTabAt(0).select();
////        CalcFragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + ViewPager.getCurrentItem());
//
//
////        String tabTag = tabs.getTabAt(0).getTag().toString();
//        CalcFragment fragment = (CalcFragment) getSupportFragmentManager().findFragmentById(0);
//        fragment.addToChart(points);
//
//
//
////        Snackbar.make(v, "Hinzufügen", Snackbar.LENGTH_LONG)
////                .setAction("Action", null).show();
//
//
//        //zu einem anderen tab wechseln
        //tabhost.getTabAt(2).select();
//    }
//
//    @Override
//    public void deleteCurrent(View v, FoodItemContent.FoodItem foodItem) {
//
//        //übergebenes element löschen liste aktuallisieren
//        Snackbar.make(v, "Löschen", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
//
//    }

//    @Override
//    public void onFoodItemClick(int postition) {
//        FoodItemContent.ITEMS.get(postition); // objekt das geklickt wurde
//    }
}