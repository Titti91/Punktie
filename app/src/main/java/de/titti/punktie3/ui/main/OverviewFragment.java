package de.titti.punktie3.ui.main;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import de.titti.punktie3.MainActivity;
import de.titti.punktie3.R;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart lineChart;
    private HashMap<String, float[]> pointvalues;
    private List<String> dates = new ArrayList<>();
    private List<float[]> punkte = new ArrayList<>();
    private PageViewModel pageViewModel;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        pageViewModel = new ViewModelProvider(requireActivity()).get(PageViewModel.class);




        lineChart = view.findViewById(R.id.lineChart);
        lineChart.setPinchZoom(true);
        lineChart.setScaleEnabled(false);//false
        lineChart.setDragEnabled(true);
        lineChart.getDescription().setEnabled(false);
//        lineChart.setVisibleXRangeMaximum(5f);
//        lineChart.setVisibleXRange(1, 5);
//        lineChart.setScaleMinima(10f, 1f);

        pageViewModel.getPointValues().observe(getViewLifecycleOwner(), new Observer<HashMap<String, float[]>>() {
            @Override
            public void onChanged(HashMap<String, float[]> stringHashMap) {
                pointvalues = stringHashMap;

                Map<String, float[]> map = new TreeMap<>(pointvalues);  //sortieren der werte nach key
                Map<String, float[]> map2 = new TreeMap<>();  //sortieren der werte nach key

               SimpleDateFormat df2 = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());


                for(Map.Entry<String, float[]> entry : map.entrySet()){                                 //umdrehen des datums fuer eine korreckte sortierung
                    try {
                        map2.put(df2.format(MainActivity.df.parse(entry.getKey())), entry.getValue());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    df2.format(entry.getKey());
                }


                dates = new ArrayList<>(map2.keySet());  //keys as list
                for(int i =0; i < dates.size(); i++){                              //datum in anzeige umdrehen
                    try {
                        dates.set(i, MainActivity.df.format(df2.parse(dates.get(i))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                punkte = new ArrayList<>(map2.values()); //values as list



                updateLineChart();
            }
        });


        return view;
    }


    private ValueFormatter formatter2 = new ValueFormatter() {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
//            return super.getAxisLabel(value, axis);
            try {
                return dates.get((int) value);
            } catch (Exception e){
                return "";
            }
        }
    };

    private void updateLineChart(){



        Legend legend = lineChart.getLegend();
        legend.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisLeft.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
//        yAxisLeft.setDrawGridLines(false);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1.0f);
        xAxis.setValueFormatter(formatter2);
        xAxis.setDrawGridLines(false);

        List<Entry> lineEntrys1 = new ArrayList<>();
        List<Entry> lineEntrys2 = new ArrayList<>();

        for(int i = 0; i < punkte.size(); i++){
            lineEntrys1.add(new Entry(i, punkte.get(i)[0]));
            lineEntrys2.add(new Entry(i, punkte.get(i)[2]));

        }
        Collections.sort(lineEntrys1, new EntryXComparator());
        Collections.sort(lineEntrys2, new EntryXComparator());


//        lineEntrys.add(new Entry(pointvalues.get("12.12.2020")[0]));
//        lineEntrys1.add(new Entry(0, 5));
//        lineEntrys1.add(new Entry(1, -3));
//        lineEntrys1.add(new Entry(2, 10));
//        lineEntrys1.add(new Entry(3, 10));
//        lineEntrys1.add(new Entry(4, 10));
//        lineEntrys1.add(new Entry(5, 10));
//        lineEntrys1.add(new Entry(6, 10));
//        lineEntrys1.add(new Entry(7, 10));
//        lineEntrys1.add(new Entry(8, 10));

        LineDataSet dataSet1 = new LineDataSet(lineEntrys1, "Offen");  //enthaelt einen grafen
        LineDataSet dataSet2 = new LineDataSet(lineEntrys2, "Überzogen");
        dataSet1.setValueTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        dataSet2.setValueTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        dataSet1.setColor(Color.rgb(103, 214, 144));    //offen, gruen
        dataSet2.setColor(Color.rgb(217, 37, 82));      //ueberzogen rot
        dataSet1.setLineWidth(3f);
        dataSet2.setLineWidth(3f);
        dataSet1.setCircleRadius(8f);
        dataSet2.setCircleRadius(8f);
        dataSet1.setCircleColor(Color.rgb(103, 214, 144));
        dataSet2.setCircleColor(Color.rgb(217, 37, 82));
        dataSet1.setValueTextSize(14f);
        dataSet2.setValueTextSize(14f);
//        List<Entry> lineEntrys2 = new ArrayList<>();
//
////        lineEntrys.add(new Entry(pointvalues.get("12.12.2020")[0]));
//        lineEntrys2.add(new Entry(0, 10));
//        lineEntrys2.add(new Entry(1, 5));
//        lineEntrys2.add(new Entry(2, -6));
//
//        LineDataSet dataSet2 = new LineDataSet(lineEntrys2, "Test");  //enthaelt einen grafen




        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);


        LineData data = new LineData(dataSets);
        if(lineEntrys1.isEmpty()){
            lineChart.clear();
            lineChart.invalidate();
        }
        else {
            System.out.println(lineEntrys1.size());
            lineChart.setData(data);
            lineChart.invalidate();
        }

        lineChart.setVisibleXRangeMaximum(6f);      //anzahl begrenzen
        lineChart.moveViewToX(punkte.size() - 7); //bildauschnitt setzen

    }

//    private class MyAxisValueFormatter implements IAxisValueFormatter {
//
//        @Override
//        public String getFormattedValue(float value, AxisBase axis) {
//            return dates.get((int) value);
//        }
//
//
//    }

}