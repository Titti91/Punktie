package de.titti.punktie3.ui.main.foodItem;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.titti.punktie3.PointCalcActivity;

public class FoodItemContent {
    public static List<FoodItem> ITEMS = new ArrayList<FoodItem>();
    public static List<FoodItem> itemsForStorage = new ArrayList<>();
    public static final Map<String, FoodItem> ITEM_MAP = new HashMap<String, FoodItem>();
    public static boolean hasInstance = false;
    private static final int COUNT = 5;
    //    private Context context;
    private static final String LIST_STORE = "LIST_STORE";
    private static final String MAP_STORE = "MAP_STORE";
    private static SharedPreferences preferences;

    public FoodItemContent(Context context) {
        hasInstance = true;
        preferences = context.getSharedPreferences(LIST_STORE, Context.MODE_PRIVATE);
//        this.context = context;
        loadList();
        Collections.sort(ITEMS, new AlphabetComparator());
        Collections.sort(itemsForStorage, new AlphabetComparator());
        //load values
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createFoodItem("Banane", "Stück", 1, 4, 100, 100));
//        }
    }

    public static FoodItem addItem(String unit, float amount, float points, float fett, float calories) {
        FoodItem temp = addItem(createFoodItem(unit, amount, points, fett, calories));
//        Collections.sort(ITEMS, new AlphabetComparator());
        return temp;
    }

    private static void saveList() {

//        System.out.println("Liste beim speichern: " + itemsForStorage.toString());
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
//        String json = gson.toJson(ITEMS); //vorher war es so
        String json = gson.toJson(itemsForStorage);
        editor.putString(LIST_STORE, json);
        editor.commit();

    }

    private void loadList() {
        if (preferences.contains(LIST_STORE)) {
            Gson gson = new Gson();
            String json = preferences.getString(LIST_STORE, "");
            Type type = new TypeToken<List<FoodItem>>() {
            }.getType();
//            ITEMS = gson.fromJson(json, type);
            itemsForStorage = gson.fromJson(json, type);
            ITEMS = new ArrayList<>(itemsForStorage);
//            System.out.println("Liste nach dem laden: " + itemsForStorage.toString());

        }

    }


    private static FoodItem addItem(FoodItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
        itemsForStorage.add(item);

        //store values
        Collections.sort(ITEMS, new AlphabetComparator());
        Collections.sort(itemsForStorage, new AlphabetComparator());
        saveList();
        return item;
    }

    private static FoodItem createFoodItem(String unit, float amount, float points, float fett, float calories) {
        return new FoodItem(unit, amount, points, fett, calories);
    }

    public static int getPosition(FoodItem foodItem) {
        return ITEMS.indexOf(foodItem);
    }

    public static void deleteFoodItem(FoodItem foodItem) {
        ITEMS.remove(foodItem);
        ITEM_MAP.remove(foodItem);
//        System.out.println("item for storage vor löschen: " + itemsForStorage.toString());
        itemsForStorage.remove(foodItem);
//        System.out.println("item for storage nach löschen: " + itemsForStorage.toString());
        saveList();
    }

    public static void updateFoodItem(FoodItem foodItem) {
        ITEMS.set(ITEMS.indexOf(foodItem), foodItem);
        itemsForStorage.set(itemsForStorage.indexOf(foodItem), foodItem);
//        ITEM_MAP.replace()  drum kuemmern
        Collections.sort(ITEMS, new AlphabetComparator());
        Collections.sort(itemsForStorage, new AlphabetComparator());
        saveList();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class FoodItem implements Serializable {
        public String name = "";
        public String unit;
        public float amount;
        public float points;
        public float fett;
        public float calories;


        public FoodItem(String unit, float amount, float points, float fett, float calories) {
//            this.name = name;
            this.unit = unit;
            this.amount = amount;
            this.points = points;
            this.calories = calories;
            this.fett = fett;
        }

        public String contentToString() {
            return points + " Punkte - " + amount + " " + unit;
        }
//        @Override
//        public String toString() {
//            return unit;
//        }
    }

    public static class AlphabetComparator implements Comparator<FoodItem> {
        public int compare(FoodItem left, FoodItem right) {
            return left.name.toLowerCase().compareTo(right.name.toLowerCase());
        }
    }
}
