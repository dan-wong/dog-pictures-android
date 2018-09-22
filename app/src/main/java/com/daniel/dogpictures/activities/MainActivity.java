package com.daniel.dogpictures.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.daniel.dogpictures.R;
import com.daniel.dogpictures.util.InternetUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    public static final String DOG_BREED = "com.daniel.dogpictures.dog.breed";

    private FirebaseAnalytics mFirebaseAnalytics;

    private String dogBreed = "Corgi";

    @BindView(R.id.dogBreedsSpinner) Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
        if (!InternetUtil.isOnline()) {
            Toasty.warning(getApplicationContext(), "No Internet Access!", Toast.LENGTH_SHORT, true).show();
        } else {
            logFirebaseEvent();

            Intent intent = new Intent(getApplicationContext(), DogActivity.class);
            intent.putExtra(DOG_BREED, dogBreed);
            startActivity(intent);
        }
    }

    @OnItemSelected(R.id.dogBreedsSpinner)
    public void dogBreedsSpinnerItemSelected(Spinner spinner, int position) {
        dogBreed = spinner.getItemAtPosition(position).toString();
    }

    private void logFirebaseEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, dogBreed);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
