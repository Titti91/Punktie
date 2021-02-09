package de.titti.punktie3.ui.main;

import java.util.HashMap;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private MutableLiveData<Float> addPoints = new MutableLiveData<>();
    private MutableLiveData<Boolean> triggerSearch = new MutableLiveData<>();
    private MutableLiveData<Boolean> editIsSeekbar = new MutableLiveData<>();
    private MutableLiveData<FoodItemContent.FoodItem> mFoodItemForDeletion = new MutableLiveData<>();
    private MutableLiveData<FoodItemContent.FoodItem> mFoodItemForEdit = new MutableLiveData<>();
    private MutableLiveData<FoodItemContent.FoodItem> mFoodItemForUpdate = new MutableLiveData<>();
    private MutableLiveData<FoodItemContent.FoodItem> mFoodItemForCalc = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, float[]>> mPointValues = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "Hello world from section: " + input;
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setTriggerSearch(boolean triggerSearch){this.triggerSearch.setValue(triggerSearch);}
    public LiveData<Boolean> getTriggerSearch(){return triggerSearch;}

    public void setEditIsSeekbar(boolean editIsSeekbar){this.editIsSeekbar.setValue(editIsSeekbar);}
    public LiveData<Boolean> getEditIsSeekbar(){return editIsSeekbar;}

    public void setAddPoints(float points){addPoints.setValue(points);}
    public LiveData<Float> getAddPoints(){return addPoints;}

    public void setFoodItemForDeletion(FoodItemContent.FoodItem foodItem){
        mFoodItemForDeletion.setValue(foodItem);}
    public LiveData<FoodItemContent.FoodItem> getFoodItemForDeletion(){return mFoodItemForDeletion;}

    public void setFoodItemForEdit(FoodItemContent.FoodItem foodItem){
        mFoodItemForEdit.setValue(foodItem);}
    public LiveData<FoodItemContent.FoodItem> getFoodItemForEdit(){return mFoodItemForEdit;}

    public void setFoodItemForUpdate(FoodItemContent.FoodItem foodItem){
        mFoodItemForUpdate.setValue(foodItem);}
    public LiveData<FoodItemContent.FoodItem> getFoodItemForUpdate(){return mFoodItemForUpdate;}

    public void setFoodItemForCalc(FoodItemContent.FoodItem foodItem){
        mFoodItemForCalc.setValue(foodItem);}
    public LiveData<FoodItemContent.FoodItem> getFoodItemForCalc(){return mFoodItemForCalc;}

    public void setPointValues(HashMap<String, float[]> pointValues){mPointValues.setValue(pointValues);}
    public LiveData<HashMap<String, float[]>> getPointValues(){return mPointValues;}



}