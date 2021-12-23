package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class LeaderBoardActivity extends AppCompatActivity {
    private Button  returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v ->{
            Intent intent = new Intent(this , HelperMapActivity.class);
            startActivity(intent);
            finish();
        });
        ArrayList<String> users = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(this , R.layout.activity_list_values , users);
        ListView listView = (ListView) findViewById(R.id.rating_list);
        DatabaseReference Helpers = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers");
        Helpers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HelperData> list = new ArrayList<>();
                int rating ;
                String name;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    name = (String) dataSnapshot.child("name").getValue();
                    rating = Math.toIntExact((Long) dataSnapshot.child("rating").getValue());
                    list.add(new HelperData(name , rating));
                }
                Collections.sort(list);
                users.clear();
                for (int i = 0; i < 20 && i < list.size(); i++) {
                    users.add("" + (i+1) + list.get(i).toString());
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private class HelperData implements Comparable<HelperData>{
        String name ;
        int rating;

        public HelperData(String name,  int rating) {
            this.name = name;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return " " + name +
                    "\n" + rating ;

        }

        @Override
        public int compareTo(HelperData o) {
            return o.rating - this.rating;
        }
    }
}