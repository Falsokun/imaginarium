package com.example.olesya.boardgames.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.olesya.boardgames.AppDatabase;
import com.example.olesya.boardgames.Utils;
import com.example.olesya.boardgames.interfaces.ImageHolderDao;
import com.example.olesya.boardgames.models.ImageHolder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageHolderDao dao = AppDatabase.getInstance(this).getImagesDao();
        ArrayList<ImageHolder> holders = new ArrayList<>();
        db.collection(Utils.DB_PERSEPHONE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            holders.add(new ImageHolder((String) document.getData().get("img")));
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Toast.makeText(this, "trouble", Toast.LENGTH_SHORT).show();
                    }

                    insertData(dao, holders);
                    startActivity();
                });
    }

    private void insertData(ImageHolderDao dao, ArrayList<ImageHolder> holders) {
        new Thread(() -> dao.insertAll(holders)).start();
    }

    public void startActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
