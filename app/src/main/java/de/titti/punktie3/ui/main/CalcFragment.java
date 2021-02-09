package de.titti.punktie3.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import de.titti.punktie3.EditEntries;
import de.titti.punktie3.MainActivity;
import de.titti.punktie3.R;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalcFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalcFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private PageViewModel pageViewModel;
    //pichart color sets
    public static final int[] NORMAL_COLORS = {
            Color.rgb(103, 214, 144), Color.rgb(204, 96, 143)
    };
    public static final int[] CRITICAL_COLORS = {
            Color.rgb(204, 96, 143), Color.rgb(217, 37, 82)
    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View layout;

    private Button add;
    private ImageButton save;
    private EditText kcal, fett, menge, punkte, zielmengeAnzahl;
    private TextView zielmengeText;
    private SeekBar portion;
    private Spinner einheit;
    public static final String[] einheiten = {"Gramm", "Milliliter", "Kilogramm", "Liter", "TL", "EL", "Stück"};
    private final String PROGRAMTAG = "PROGRAMTAG";

    private boolean editIsSeekbar = true;

    private float baseValueKcal, baseValueFett, baseValueMenge, baseValuePunkte, baseValuePointsForNewTarget = 0;
    private int lastSeekValue = 50;  //50
    //chart data

    private SharedPreferences preferences;
    public static final String POINT_STORE = "POINT_STORE";

    private PieChart piChart;
    private HashMap<String, float[]> pointvalues = new HashMap<>();
    private float amount[] = {70, 0, 0};  //offen, verbraucht, ueberzogen

    private String chartLabel[] = {"offen", "verbraucht", "überzogen"};
    private FoodItemContent.FoodItem foodItemForCalc;

    private float tagesMaxPunkte = 70;

    public CalcFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalcFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalcFragment newInstance(String param1, String param2) {
        CalcFragment fragment = new CalcFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(POINT_STORE, Context.MODE_PRIVATE);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_calc, container, false);

        //load values if they not exist
//        if (pointvalues.isEmpty()) {
//            loadPointValues();
//        }

        pageViewModel = new ViewModelProvider(requireActivity()).get(PageViewModel.class);

        add = layout.findViewById(R.id.buttonHinzufuegen);
        save = layout.findViewById(R.id.imageButtonSaveFromCalc);
        kcal = layout.findViewById(R.id.editTextNumberDecimalKcal);
        fett = layout.findViewById(R.id.editTextNumberDecimalFett);
        menge = layout.findViewById(R.id.editTextNumberDecimalMenge);
        punkte = layout.findViewById(R.id.editTextNumberDecimalPunkte);
        portion = layout.findViewById(R.id.seekBarMenge);
        einheit = layout.findViewById(R.id.spinnerEinheit);
        piChart = layout.findViewById(R.id.chart);
        zielmengeAnzahl = layout.findViewById(R.id.editTextZielmenge);
        zielmengeText = layout.findViewById(R.id.textViewZielmenge);


        pageViewModel.getAddPoints().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                if(aFloat > -1){
                    addToChart(aFloat);
                    Snackbar.make(layout, aFloat + " Punkte wurden verbraucht", Snackbar.LENGTH_LONG)
                            .setAction("Rückgänig", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeFromChart(aFloat);
                                }
                            }).show();
                    pageViewModel.setAddPoints(-1);
                }



            }
        });
        pageViewModel.getPointValues().observe(getViewLifecycleOwner(), new Observer<HashMap<String, float[]>>() {
            @Override
            public void onChanged(HashMap<String, float[]> stringHashMap) {
                pointvalues = stringHashMap;
                initDayPoints();
            }
        });
        pageViewModel.getFoodItemForCalc().observe(getViewLifecycleOwner(), new Observer<FoodItemContent.FoodItem>() {
            @Override
            public void onChanged(FoodItemContent.FoodItem foodItem) {
                foodItemForCalc = foodItem;
                setCalcValuesFromObject();
            }
        });

        pageViewModel.getEditIsSeekbar().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                editIsSeekbar = aBoolean;
                if(editIsSeekbar){
                    portion.setVisibility(View.VISIBLE);
                    zielmengeText.setVisibility(View.INVISIBLE);
                    zielmengeAnzahl.setVisibility(View.INVISIBLE);
                    if(zielmengeAnzahl.getText().length() > 0){
                        if (zielmengeAnzahl.isEnabled()){
                            if(kcal.getText().length() > 0 && fett.getText().length() > 0){
                                kcal.setText(String.valueOf(round(getFloatFromEditText(kcal)/getFloatFromEditText(menge) * getFloatFromEditText(zielmengeAnzahl), 2)));
                                fett.setText(String.valueOf(round(getFloatFromEditText(fett)/getFloatFromEditText(menge) * getFloatFromEditText(zielmengeAnzahl), 2)));
                            }
                            menge.setText(zielmengeAnzahl.getText());
                        }

                    }
                }
                else {
                    portion.setVisibility(View.INVISIBLE);
                    zielmengeText.setVisibility(View.VISIBLE);
                    zielmengeAnzahl.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_spinner_dropdown_item, einheiten);
        einheit.setAdapter(adapter);

        menge.setText("100");
        portion.setEnabled(false);
        zielmengeAnzahl.setEnabled(false);

        kcal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rechner();
            }
        });
        fett.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rechner();
            }
        });

        punkte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                punkte.selectAll();
            }
        });

        punkte.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if( punkte.getText().toString().length() > 0){
                    if(layout.getTag() != PROGRAMTAG){    //wert wurde vom nutzer angepasst
//                        baseValuePointsForNewTarget = getFloatFromEditText(punkte);
                    }
                    add.setEnabled(true);
                }
                else {
                    add.setEnabled(false);
                    portion.setEnabled(false);
                    zielmengeAnzahl.setEnabled(false);
                }

            }
        });

        zielmengeAnzahl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(zielmengeAnzahl.getText().length() > 0){
                    changeTargetQuantity();
                }
                else rechner();

            }
        });

        portion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    changeScale(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setBaseValues();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                lastSeekValue = seekBar.getProgress();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////// 0= punkte uebrig // 1 = punkte verbraucht // 2 = punkte ueberzogen
                if (punkte.getText().toString().length() > 0) {
                    float fPunkte = getFloatFromEditText(punkte);

                    pageViewModel.setAddPoints(fPunkte);
//                    addToChart(fPunkte);
                    InputMethodManager imm = (InputMethodManager)getSystemService(getContext(), InputMethodManager.class); //,Context.INPUT_METHOD_SERVICE
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);



//                    float temp = amount[0] - fPunkte;
//                    if(temp >= 0){
//                        amount[0] -= fPunkte;
//                        amount[1] += fPunkte;
//                    }
//                    else{
//                        amount[0] = 0;
//                        amount[1] = tagesMaxPunkte;
//                        amount[2] += Math.abs(temp);
//                    }
//
//                    updatePiChart();
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("angeklickt");
                //TODO
                //modal für eingabe von namen erstellen
//                FoodItemContent.addItem( einheit.getSelectedItem().toString(), getFloatFromEditText(menge), getFloatFromEditText(punkte), getFloatFromEditText(fett), getFloatFromEditText(kcal));
                float tempMenge = getFloatFromEditText(menge);
                if(zielmengeAnzahl.isEnabled() && zielmengeAnzahl.getVisibility() == View.VISIBLE && zielmengeAnzahl.getText().length() > 0){
                    tempMenge = getFloatFromEditText(zielmengeAnzahl);
                }
                FoodItemContent.FoodItem currentFoodItem = FoodItemContent.addItem( einheit.getSelectedItem().toString(), tempMenge, getFloatFromEditText(punkte), getFloatFromEditText(fett), getFloatFromEditText(kcal));
                pageViewModel.setFoodItemForEdit(currentFoodItem);
                EditEntries editEntries = new EditEntries();

                editEntries.show(getActivity().getSupportFragmentManager(), "editDialog"); //as modal
                pageViewModel.setFoodItemForEdit(currentFoodItem);

                InputMethodManager imm = (InputMethodManager)getSystemService(getContext(), InputMethodManager.class); //,Context.INPUT_METHOD_SERVICE
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
            }
        });


        updatePiChart();

        return layout;
    }

    private void setCalcValuesFromObject() {
        kcal.setText(String.valueOf(foodItemForCalc.calories));
        fett.setText(String.valueOf(foodItemForCalc.fett));
        menge.setText(String.valueOf(foodItemForCalc.amount));
        punkte.setTag(PROGRAMTAG);
        punkte.setText(String.valueOf(foodItemForCalc.points));
//        punkte.setTag(null);
        baseValuePointsForNewTarget = foodItemForCalc.points;
        einheit.setSelection(Arrays.asList(einheiten).indexOf(foodItemForCalc.unit));
    }

    private void initDayPoints() {
        amount = pointvalues.get(MainActivity.df.format(Calendar.getInstance().getTime()));
        updatePiChart();
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
//    }

    public void addToChart(float punkte) {  //{70, 0, 0};  //offen, verbraucht, ueberzogen
//        System.out.println("aufgerufen mit: "+ punkte);
        float fPunkte = punkte;
        float temp = amount[0] - fPunkte;
        if (temp >= 0) {
            amount[0] -= fPunkte;
            amount[1] += fPunkte;
        } else {
            amount[0] = 0;
            amount[1] = tagesMaxPunkte;
            amount[2] += Math.abs(temp);
        }


        updateTodayPoints();
        pageViewModel.setPointValues(pointvalues);  //speichaert auch die werte
        updatePiChart();
    }



    public void removeFromChart(float punkte) { // {70, 0, 0};  //offen, verbraucht, ueberzogen
        float temp = punkte - amount[2];

        if(temp <= 0){           //punkte passen in ueberzogen
            amount[2] -= punkte;
        }
        else {
            amount[2] = 0;
            amount[1] -= temp;
            amount[0] += temp;
        }

        updateTodayPoints();
        pageViewModel.setPointValues(pointvalues);  //speichaert auch die werte
        updatePiChart();

    }
    private void updateTodayPoints() {
        pointvalues.put(MainActivity.df.format(Calendar.getInstance().getTime()), amount);

    }
    private float rechner() {
        if (this.kcal.getText().toString().length() > 0 && this.fett.getText().toString().length() > 0) {
            String kcal = this.kcal.getText().toString();
            String fett = this.fett.getText().toString();

            float punkte = 0.0f;
            punkte = (Float.parseFloat(fett) * 0.11f) + (Float.parseFloat(kcal) * 0.0165f);

            this.punkte.setTag(PROGRAMTAG);
            this.punkte.setText(String.valueOf(round(punkte, 1)));
//            this.punkte.setTag(null);
            baseValuePointsForNewTarget = punkte;
            portion.setEnabled(true);
            zielmengeAnzahl.setEnabled(true);
            add.setEnabled(true);
            save.setVisibility(View.VISIBLE);
            return punkte;
        } else {
            punkte.setTag(PROGRAMTAG);
            this.punkte.setText("");
//            punkte.setTag(null);
            baseValuePointsForNewTarget = 0;
            portion.setProgress(50);
            portion.setEnabled(false);
            zielmengeAnzahl.setEnabled(false);
            add.setEnabled(false);
            save.setVisibility(View.INVISIBLE);
            return 0;
        }

    }

    private static float round(float value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }

    private void changeScale(int value) {
//        System.out.println(value + "before");

//        value *= 2;//skalaverschiebung


        float newValue = ((value - lastSeekValue) / 10.0f) >= 0 ? (((value - lastSeekValue) / 10.0f) + 1) : (((value - lastSeekValue) / 10.0f) - 1); //umrechnung slider auf multiplikator
//        System.out.println(newValue + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if (newValue >= 0) {
            //alle werte multiplizieren
//            System.out.println(newValue + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            newValue *= 1.5d; // faktor
            kcal.setText(String.valueOf(round(baseValueKcal * newValue, 2)));
            fett.setText(String.valueOf(round(baseValueFett * newValue, 2)));
            menge.setText(String.valueOf(round(baseValueMenge * newValue, 2)));
            punkte.setTag(PROGRAMTAG);
            punkte.setText(String.valueOf(round(baseValuePunkte * newValue, 1)));
//            punkte.setTag(null);
            baseValuePointsForNewTarget = round(baseValuePunkte * newValue, 1);

        } else {
            //alle werte teilen, vorher newValue positiv machen
            newValue = Math.abs(newValue);
//            newValue *= 1.5d; // faktor
            kcal.setText(String.valueOf(round(baseValueKcal / newValue, 2)));
            fett.setText(String.valueOf(round(baseValueFett / newValue, 2)));
            menge.setText(String.valueOf(round(baseValueMenge / newValue, 2)));
            punkte.setTag(PROGRAMTAG);
            punkte.setText(String.valueOf(round(baseValuePunkte / newValue, 1)));
//            punkte.setTag(null);
            baseValuePointsForNewTarget = round(baseValuePunkte / newValue, 1);
        }
    }

    private void changeTargetQuantity(){
        if(menge.getText().length() > 0 && baseValuePointsForNewTarget > 0 && zielmengeAnzahl.getText().length() > 0){
            //todo
//            float punkte = getFloatFromEditText(this.punkte);
            float punkte = baseValuePointsForNewTarget;
            float mengeBasis = getFloatFromEditText(menge);
            float zielmenge = getFloatFromEditText(zielmengeAnzahl);

            float zielPunkte = punkte / mengeBasis * zielmenge;

            this.punkte.setText(String.valueOf(round(zielPunkte, 1)));

        }
    }


    private float getFloatFromEditText(EditText et) {
        return Float.parseFloat(et.getText().toString());
    }

    private void setBaseValues() {
        baseValueFett = getFloatFromEditText(fett);
        baseValueKcal = getFloatFromEditText(kcal);
        baseValueMenge = getFloatFromEditText(menge);
        baseValuePunkte = getFloatFromEditText(punkte);
    }

    private void updatePiChart() {
        Legend legend = piChart.getLegend();
        legend.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        List<PieEntry> piEntries = new ArrayList<>();
        for (int i = 0; i < amount.length; i++) {
            if (amount[i] > 0) {
                piEntries.add(new PieEntry(amount[i], chartLabel[i])); //farben  binden
            }

        }

        PieDataSet dataSet = new PieDataSet(piEntries, "");

        if (amount[2] > 0) {
            dataSet.setColors(CRITICAL_COLORS);
        } else {
            dataSet.setColors(NORMAL_COLORS);
        }
//        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);

        piChart.setData(data);
        piChart.setDrawEntryLabels(false);
        piChart.getDescription().setEnabled(false);
        piChart.setCenterText("Tagesverbrauch");
        piChart.setCenterTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        piChart.setHoleColor(getColor(getContext(), R.color.transparent));
        piChart.setRotationEnabled(false);
        piChart.animateY(600);
        piChart.notifyDataSetChanged();
        piChart.invalidate();
    }

    private void loadPointValues() {
        Gson gson = new Gson();
        String json = preferences.getString(POINT_STORE, "");
        if (json.length() > 0) {
            Type type = new TypeToken<HashMap<String, float[]>>() {
            }.getType();
            pointvalues = gson.fromJson(json, type);
            System.out.println("anzahl der einträge: " +pointvalues.size());
        }

    }

    private void savePointValues() {
//        System.out.println("neues array" + amount.toString());
//        for (int i = 0; i < amount.length; i++){
//            System.out.println(i + ": " + amount[i]);
//        }

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pointvalues);
        editor.putString(POINT_STORE, json);
        editor.commit();

    }

}