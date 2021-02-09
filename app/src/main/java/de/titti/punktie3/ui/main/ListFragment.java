package de.titti.punktie3.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import de.titti.punktie3.EditEntries;
import de.titti.punktie3.ListDetail;
import de.titti.punktie3.R;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A fragment representing a list of Items.
 */
public class ListFragment extends Fragment implements MyItemRecyclerViewAdapter.OnFoodItemListener{

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private PageViewModel pageViewModel;
    private MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";


    private MenuItem searchItem;
    private boolean triggerSearch = false;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
//        pageViewModel.setAddPoints(30);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_list, container, false);
        pageViewModel = new ViewModelProvider(requireActivity()).get(PageViewModel.class);

//        FloatingActionButton fab = view.findViewById(R.id.fabList);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(FoodItemContent.ITEMS, this::onFoodItemClick);
            recyclerView.setAdapter(myItemRecyclerViewAdapter);
        }
        pageViewModel.getFoodItemForDeletion().observe(getViewLifecycleOwner(), new Observer<FoodItemContent.FoodItem>() {
            @Override
            public void onChanged(FoodItemContent.FoodItem foodItem) {
                //übergebenes element aus der liste löschen
                int pos = FoodItemContent.getPosition(foodItem);
                FoodItemContent.deleteFoodItem(foodItem);
                //todo irgendwas stimmt mit der liste nict wenn innnerhalb einer suche elemente geloescht werden
//                myItemRecyclerViewAdapter.notifyItemRemoved(pos);
//                myItemRecyclerViewAdapter.setFullFilterList(FoodItemContent.ITEMS);
                myItemRecyclerViewAdapter.setFullFilterList(FoodItemContent.itemsForStorage); //update full list after adding an element
                myItemRecyclerViewAdapter.notifyDataSetChanged();
                Snackbar.make(view, foodItem.name +" wurde gelöscht", Snackbar.LENGTH_LONG)
                        .setAction("Rückgänig", null).show();

            }
        });
        pageViewModel.getFoodItemForUpdate().observe(getViewLifecycleOwner(), new Observer<FoodItemContent.FoodItem>() {
            @Override
            public void onChanged(FoodItemContent.FoodItem foodItem) {
                FoodItemContent.updateFoodItem(foodItem);
                myItemRecyclerViewAdapter.setFullFilterList(FoodItemContent.itemsForStorage);// bei updates original liste in filterliste schreiben
                // myItemRecyclerViewAdapter.setFullFilterList(FoodItemContent.ITEMS); //updaten der full filterliste beim hinzufügen neuer elemente, ist das hier richtig?
                //myItemRecyclerViewAdapter.updateFullFilterList(foodItem); //update item in full filtered list weil es bei aufheben des filters die warheit spiegelt
                myItemRecyclerViewAdapter.notifyDataSetChanged();
                Snackbar.make(view, foodItem.name +" wurde gespeichert", Snackbar.LENGTH_LONG).show();
            }
        });
        pageViewModel.getTriggerSearch().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                System.out.println("aufgerufen "+ aBoolean);
                if(aBoolean){
//                    triggerSearch = aBoolean;
                    System.out.println("aufgerufen");
//                    MenuItem search = getView().findViewById(R.id.action_search);
//                    onOptionsItemSelected(searchItem);
//                    getActivity().findViewById(R.id.action_search).callOnClick();

                    pageViewModel.setTriggerSearch(false);
                }

            }
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FoodItemContent.FoodItem currentFoodItem = FoodItemContent.addItem( "Gramm", 100, 0, 0, 0);
//                pageViewModel.setFoodItemForEdit(currentFoodItem);
//                EditEntries editEntries = new EditEntries();
//
//                editEntries.show(getActivity().getSupportFragmentManager(), "editDialog"); //as modal
//                pageViewModel.setFoodItemForEdit(currentFoodItem);
//
//                InputMethodManager imm = (InputMethodManager)getSystemService(getContext(), InputMethodManager.class); //,Context.INPUT_METHOD_SERVICE
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }
//        });

//        myItemRecyclerViewAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.filter_menu, menu);

        searchItem = menu.findItem(R.id.action_search);

//        onOptionsItemSelected(searchItem);//????



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
//                myItemRecyclerViewAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search :
//                MenuItem searchItem = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        myItemRecyclerViewAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
                return true;
            case R.id.userProfile :
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onFoodItemClick(int postition) {
//        ListDetail.isVisible = true;
        FoodItemContent.FoodItem foodItem = FoodItemContent.ITEMS.get(postition); // objekt das geklickt wurde
//        Snackbar.make(getView(), "Tada: "+foodItem.name, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        ListDetail listDetail= ListDetail.newInstance(foodItem);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(getActivity().getSupportFragmentManager().findFragmentById(R.id.list).getId(), listDetail, "findThisFragment")
//                .addToBackStack(null)
//                .commit();
        listDetail.show(getActivity().getSupportFragmentManager(), "bottomSheet");


    }



}