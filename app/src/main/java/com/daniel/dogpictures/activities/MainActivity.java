package com.daniel.dogpictures.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.daniel.dogpictures.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity {
    public static final String DOG_BREED = "com.daniel.dogpictures.dog.breed";

    private String dogBreed = "Corgi";

    @BindView(R.id.dogBreedsSpinner) Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupSpinner();
    }

    private void setupSpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.dog_array,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
    }

    @OnClick(R.id.getDogsButton)
    public void getDogsButton() {
        Intent intent = new Intent(getApplicationContext(), DogActivity.class);
        intent.putExtra(DOG_BREED, dogBreed);
        startActivity(intent);
    }

    @OnItemSelected(R.id.dogBreedsSpinner)
    public void dogBreedsSpinnerItemSelected(Spinner spinner, int position) {
        dogBreed = spinner.getItemAtPosition(position).toString();
    }
}
