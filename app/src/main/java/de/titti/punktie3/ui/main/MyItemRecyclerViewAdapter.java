package de.titti.punktie3.ui.main;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import de.titti.punktie3.R;
//import de.titti.punktie3.ui.main.dummy.DummyContent.foodItem;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent.FoodItem;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FoodItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<FoodItem> mValues;
    private List<FoodItem> mValuesFull;
    private OnFoodItemListener mOnFoodItemListener;
    public MyItemRecyclerViewAdapter(List<FoodItem> items, OnFoodItemListener onFoodItemListener) {


        mValues = items; // items sind die echte liste der daten, bei einem gefilterten löschen, sind die echten daten nur der gefilterte ausschnitt, das problem ist, dass beim löschen die liste gespeichert wird
//        mValues = new ArrayList<>(items); //reicht es die liste zu kopieren?
        mValuesFull = new ArrayList<>(items);
        this.mOnFoodItemListener = onFoodItemListener;
    }

    public void setFullFilterList(List<FoodItem> items){
//        mValuesFull = new ArrayList<>(FoodItemContent.ITEMS);
        mValuesFull = new ArrayList<>(items);
    }

    public void updateFullFilterList(FoodItem item){
        mValuesFull.set(mValuesFull.indexOf(item),item);
    }

//    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            int itemPosition = mRecyclerView.getChildLayoutPosition(view);
//            String item = mList.get(itemPosition);
//            Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();
//        }
//    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list, parent, false);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                view.getCh
//            }
//        });
        return new ViewHolder(view, mOnFoodItemListener);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int test = position;
//                FragmentTransaction ft =  get v.getContext().getgetFragmentManager().beginTransaction();
//                Fragment fragment = CommentsFragment.newInstance(mDescribable);
//                ft.replace(R.id.comments_fragment, fragment);
//                ft.commit();
//            }
//        });

        holder.mNameView.setText(mValues.get(position).name);
        holder.mContentView.setText(mValues.get(position).contentToString());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mNameView;
        public final TextView mContentView;
        public FoodItem mItem;
        OnFoodItemListener onFoodItemListener;

        public ViewHolder(View view, OnFoodItemListener onFoodItemListener) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mContentView = (TextView) view.findViewById(R.id.item_content);
            this.onFoodItemListener = onFoodItemListener;

            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View v) { //es wurde das element angeklickt
            onFoodItemListener.onFoodItemClick(getAdapterPosition());
        }


    }

    public interface OnFoodItemListener{
        void onFoodItemClick(int postition);
    }

    @Override
    public Filter getFilter() {
//        mValuesFull = new ArrayList<>(mValues);
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FoodItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(mValuesFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (FoodItem item: mValuesFull){
                    if(item.name.toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mValues.clear();
            mValues.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}