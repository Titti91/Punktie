package de.titti.punktie3;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import de.titti.punktie3.ui.main.CalcFragment;
import de.titti.punktie3.ui.main.PageViewModel;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEntries#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEntries extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText name;
    private EditText kcal;
    private EditText fett;
    private EditText menge;
    private EditText punkte;
    private Spinner einheit;
    private Button cancel;
    private Button save;

    private FoodItemContent.FoodItem currentItem;

    private PageViewModel pageViewModel;

    public EditEntries() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditEntries.
     */
    // TODO: Rename and change types and number of parameters
    public static EditEntries newInstance(String param1, String param2) {
        EditEntries fragment = new EditEntries();
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
        View view = inflater.inflate(R.layout.fragment_edit_entries, container, false);
        name = view.findViewById(R.id.editTextItemName);
        kcal = view.findViewById(R.id.editTextItemKcal);
        fett = view.findViewById(R.id.editTextItemFett);
        menge = view.findViewById(R.id.editTextItemMenge);
        punkte = view.findViewById(R.id.editTextItemPunkte);
        einheit = view.findViewById(R.id.spinnerEditItemEinheit);
        cancel = view.findViewById(R.id.buttonEditItemCancel);
        save = view.findViewById(R.id.buttonEditItemSave);

        pageViewModel = new ViewModelProvider(requireActivity()).get(PageViewModel.class);

        pageViewModel.getFoodItemForEdit().observe(getViewLifecycleOwner(), new Observer<FoodItemContent.FoodItem>() {
            @Override
            public void onChanged(FoodItemContent.FoodItem foodItem) {
                currentItem = foodItem;
                initializeFelds();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, CalcFragment.einheiten);
        einheit.setAdapter(adapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentItem.name == ""){
                    pageViewModel.setFoodItemForDeletion(currentItem);
                }
                dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().length()>0 && menge.getText().length() > 0 && punkte.getText().length() >0){
                    if(kcal.getText().length() > 0){
                        currentItem.calories = Float.parseFloat(kcal.getText().toString());
                    }
                    else currentItem.calories = 0;
                    if(fett.getText().length() > 0){
                        currentItem.fett = Float.parseFloat(fett.getText().toString());
                    }
                    else currentItem.fett = 0;

                    currentItem.name = name.getText().toString();
                    currentItem.amount = Float.parseFloat(menge.getText().toString());
                    currentItem.points = Float.parseFloat(punkte.getText().toString());
                    currentItem.unit = einheit.getSelectedItem().toString();

                    pageViewModel.setFoodItemForUpdate(currentItem);
                    dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Du must mindestens einen Namen, eine Menge und Punkte vergeben", Toast.LENGTH_LONG).show();
                }


            }
        });



        return view;
    }

    private void initializeFelds() {    //public static final String[] einheiten = {"Gramm", "Milliliter", "Kilogramm", "Liter"};
        if(currentItem != null){
            if(currentItem.name != ""){
                name.setText(currentItem.name);
            }
            kcal.setText(String.valueOf(currentItem.calories));
            fett.setText(String.valueOf(currentItem.fett));
            menge.setText(String.valueOf(currentItem.amount));
            punkte.setText(String.valueOf(currentItem.points));
            einheit.setSelection(Arrays.asList(CalcFragment.einheiten).indexOf(currentItem.unit));

        }

    }
}