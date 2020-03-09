package com.parse.starter.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String activeUser ="";
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter aad;

//    OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//      1.Getting User
        Intent intent = getIntent();
        activeUser = intent.getStringExtra("username");

        setTitle("Chats "+ activeUser);

//       3. Displaying Chats
        ListView chatListView = findViewById(R.id.chatListView);
        aad = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(aad);
        //getting messages from the Parse server
        ParseQuery<ParseObject> messageQuery1 = new ParseQuery<ParseObject>("Message");

        //double query - for checking whether sender is 'rob' or recepit is 'rob'
        //if sender is 'rob' receiver is different than rob
        messageQuery1.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
        messageQuery1.whereEqualTo("recipient", activeUser);

        ParseQuery<ParseObject> messageQuery2 = new ParseQuery<ParseObject>("Message");
        messageQuery2.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());
        messageQuery2.whereEqualTo("sender", activeUser);

        //combining the above 2 queries using List
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(messageQuery1);
        queries.add(messageQuery2);

        //main query
        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size()>0){
                    messages.clear();
                    for(ParseObject message : objects){
                        String messagContent = message.getString("message");
                        if(!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())){          //sender is messages are added "> " to distinguish
                            messagContent = "> " + messagContent;
                        }
                        messages.add(messagContent);
                    }
                    aad.notifyDataSetChanged();
                }
            }
        });
    }

//    2.Sending Chats
    public void sendChat(View view){
        final EditText chatEditText = findViewById(R.id.chatEditText);

        final ParseObject message = new ParseObject("Message");                         //Creating a object of the Message

        final String messageContent = chatEditText.getText().toString();

        message.put("sender", ParseUser.getCurrentUser().getUsername());                            //Adding things in the Message Object(in the Columns)
        message.put("recipient", activeUser);
        message.put("message",messageContent);

        //clearing TextBox after sending the message
        chatEditText.setText("");

        //saving message to server
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    //Adding messages to the chats of the sender
                    messages.add(messageContent);
                    aad.notifyDataSetChanged();
                    Toast.makeText(ChatActivity.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
