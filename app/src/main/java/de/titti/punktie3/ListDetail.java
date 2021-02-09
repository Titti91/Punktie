package de.titti.punktie3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.titti.punktie3.ui.main.PageViewModel;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListDetail extends BottomSheetDialogFragment { //Fragment


//    public static boolean isVisible = false;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    private BottomSheetListener mBottomSheetListener;
    private PageViewModel viewModel;

    private static FoodItemContent.FoodItem foodItem;

    // TODO: Rename and change types of parameters
    private FoodItemContent.FoodItem currentFoodItem;
    private String mParam2;

    public ListDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ListDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static ListDetail newInstance(FoodItemContent.FoodItem param1) {
        ListDetail fragment = new ListDetail();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PageViewModel.class);
        if (getArguments() != null) {
            currentFoodItem = (FoodItemContent.FoodItem) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View layout = inflater.inflate(R.layout.fragment_list_detail, container, false);
        // Inflate the layout for this fragment

        TextView title = layout.findViewById(R.id.food_item_title);
        TextView pointsAndAmount =layout.findViewById(R.id.food_item_points_and_amount);
        TextView fatAndCallories =layout.findViewById(R.id.food_item_fat_and_callories);
        title.setText(currentFoodItem.name);
        pointsAndAmount.setText(currentFoodItem.points+ " Punkte für "+ currentFoodItem.amount + " " + currentFoodItem.unit);
        fatAndCallories.setText("Fett: "+ currentFoodItem.fett + "g.\n" + "Kalorien: "+currentFoodItem.calories );
        TextView addToToday = layout.findViewById(R.id.food_item_add_to_today);
        TextView calcFromCurrent = layout.findViewById(R.id.food_item_calc_from_current);
        TextView edit = layout.findViewById(R.id.food_item_edit);
        TextView delete = layout.findViewById(R.id.food_item_delete);
        addToToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mBottomSheetListener.addToDay(v, currentFoodItem.points);
                viewModel.setAddPoints(currentFoodItem.points);
                System.out.println("gestartet mit: "+ currentFoodItem.points);
                dismiss();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mBottomSheetListener.deleteCurrent(v, currentFoodItem);
                viewModel.setFoodItemForDeletion(currentFoodItem);
                dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditEntries editEntries = new EditEntries();

                editEntries.show(getActivity().getSupportFragmentManager(), "editDialog"); //as modal
                viewModel.setFoodItemForEdit(currentFoodItem);


//as fullscreen
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                // For a little polish, specify a transition animation
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                // To make it fullscreen, use the 'content' root view as the container
//                // for the fragment, which is always the root view for the activity
//                transaction.add(android.R.id.content, editEntries) //content
//                        .addToBackStack(null).commit();



                dismiss();
            }
        });

        calcFromCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                viewModel.setFoodItemForCalc(currentFoodItem);
                //switch fragment

                TabLayout tabs = getActivity().findViewById(R.id.tabs);
                tabs.getTabAt(0).select();

                dismiss();

            }
        });




        return layout;
    }



//    public interface BottomSheetListener {
//        void addToDay(View v, float points);
//        void deleteCurrent(View v, FoodItemContent.FoodItem foodItem);
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        try {
//            mBottomSheetListener = (BottomSheetListener) context;
//        }
//        catch (ClassCastException e){
//            throw new ClassCastException(context.toString()+ " must implement BottomSheetListener");
//        }
//
//    }
}