package com.androiddevs.firebasestoragerecyclerviewjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        rvImages = findViewById(R.id.rvImages);
        loadImagesDynamically();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddImageActivity.class));
            }
        });
    }

    private void loadImagesDynamically() {
        final ArrayList<ImageData> images = new ArrayList<>();
        CollectionReference db = FirebaseFirestore.getInstance().collection("images");
        db.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null) {
                    images.clear();
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        ImageData imageData = snapshot.toObject(ImageData.class);
                        images.add(imageData);
                    }
                    rvImages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvImages.setAdapter(new ImageAdapter(images));
                }

            }
        });
    }
}
