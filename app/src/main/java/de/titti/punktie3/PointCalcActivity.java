package de.titti.punktie3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;

public class PointCalcActivity extends AppCompatActivity {

    private int punkte = 0;
    private EditText gewicht;
    private Button submit;
    private SharedPreferences preferences;
    public static final String DAY_POINTS_STORE = "DAY_POINTS_STORE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_calc);
        submit = this.findViewById(R.id.buttonAngabenBestaetigen);
        gewicht = this.findViewById(R.id.editTextGewichtsangabe);
        preferences = this.getApplicationContext().getSharedPreferences(DAY_POINTS_STORE, Context.MODE_PRIVATE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formularBerechnen();
            }
        });


    }



    private void formularBerechnen(){
        int sum = 0;
        int count = 0;

        RadioButton r1 = findViewById(R.id.radioButtonMaennlich);
        RadioButton r2 = findViewById(R.id.radioButtonWeiblich);
        RadioButton r3 = findViewById(R.id.radioButtonAge18);
        RadioButton r4 = findViewById(R.id.radioButtonAge21);
        RadioButton r5 = findViewById(R.id.radioButtonAge36);
        RadioButton r6 = findViewById(R.id.radioButtonAge51);
        RadioButton r7 = findViewById(R.id.radioButtonAge65);
        RadioButton r8 = findViewById(R.id.radioButtonGroesseUnter160);
        RadioButton r9 = findViewById(R.id.radioButtonGroesseUeber160);
        RadioButton r10 = findViewById(R.id.radioButtonMeistSitzend);
        RadioButton r11 = findViewById(R.id.radioButtonSeltenSitzend);
        RadioButton r12 = findViewById(R.id.radioButtonImmerLaufend);
        RadioButton r13 = findViewById(R.id.radioButtonSchwerstarbeit);
        RadioButton r14 = findViewById(R.id.radioButtonAbnehmen);
        RadioButton r15 = findViewById(R.id.radioButtonGewichtHalten);


        if(!gewicht.getText().toString().equals("")){

            if(r1.isChecked()){
                sum += 15;
                count++;
            }
            if(r2.isChecked()){
                sum += 7;
                count++;
            }

            if(r3.isChecked()){
                sum += 5;
                count++;
            }
            if(r4.isChecked()){
                sum += 4;
                count++;
            }
            if(r5.isChecked()){
                sum += 3;
                count++;
            }
            if(r6.isChecked()){
                sum += 2;
                count++;
            }
            if(r7.isChecked()){
                sum += 1;
                count++;
            }

            if(r8.isChecked()){
                sum += 1;
                count++;
            }
            if(r9.isChecked()){
                sum += 2;
                count++;
            }

            if(r10.isChecked()){
                sum += 0;
                count++;
            }
            if(r11.isChecked()){
                sum += 2;
                count++;
            }
            if(r12.isChecked()){
                sum += 4;
                count++;
            }
            if(r13.isChecked()){
                sum += 6;
                count++;
            }

            if(r14.isChecked()){
                sum += 0;
                count++;
            }
            if(r15.isChecked()){
                sum += 4;
                count++;
            }

            int temp = 0;
            temp = Integer.parseInt(gewicht.getText().toString());

            temp /= 10;

            sum += temp;

            if(count == 5){


                this.punkte = sum;


                String text = "Du hast: "+ sum + "Tagespunkte.";

                Toast.makeText(PointCalcActivity.this, text, Toast.LENGTH_LONG).show();

                eintragen();


            }
            else{
                String text = "Deine Angaben sind nicht vollstaendig!!";
                Toast.makeText(PointCalcActivity.this, text, Toast.LENGTH_LONG).show();
            }


        }
        else{
            String text = "Deine Angaben sind nicht vollstaendig!!";
            Toast.makeText(PointCalcActivity.this, text, Toast.LENGTH_LONG).show();
        }

    }

    private void eintragen(){

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(DAY_POINTS_STORE, punkte);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(DAY_POINTS_STORE, punkte);
        startActivity(intent);

    }

}