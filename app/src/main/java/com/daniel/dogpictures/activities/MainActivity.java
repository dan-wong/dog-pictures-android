package com.daniel.dogpictures.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.daniel.dogpictures.R;
import com.daniel.dogpictures.dogbreed.Breed;
import com.daniel.dogpictures.dogbreed.DogBreeds;
import com.daniel.dogpictures.util.InternetUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    public static final String DOG_BREED = "com.daniel.dogpictures.dog.breed";

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mDatabase;

    private String dogBreed = "Corgi";

    @BindView(R.id.dogBreedsSpinner) Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (mDatabase == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DogBreeds dogBreeds = DogBreeds.getInstance();
                dogBreeds.clearList();
                for (DataSnapshot breedDataSnapshot : dataSnapshot.getChildren()) {
                    dogBreeds.addBreeds(breedDataSnapshot.getValue(Breed.class));
                }
                setupSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), "Something went wrong :o", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<Breed> adapter = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                DogBreeds.getInstance().getBreeds()
        );
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
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
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, dogBreed);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
