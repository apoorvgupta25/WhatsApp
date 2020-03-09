package com.parse.starter.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ArrayList<String> usersList = new ArrayList<>();
    ArrayAdapter aad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("User List");

//      1.Setting List
        ListView userListView = findViewById(R.id.userListView);
        usersList.clear();
        aad = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usersList);
        userListView.setAdapter(aad);

//      2.Getting user list from Parse
        ParseQuery query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser user : objects) {
                        usersList.add(user.getUsername());
                    }
                    aad.notifyDataSetChanged();
                }
            }
        });

//      3.getting to the Chats
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("username", usersList.get(position));
                startActivity(intent);
            }
        });

    }
//       4.Log out

    @Override
    public void onBackPressed() {
        ParseUser.logOut();
        super.onBackPressed();
    }
}
