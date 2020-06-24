package com.example.sockettest;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    TextInputLayout username;
    Spinner  roomOptions;
    Button joinChat;
    String selectdRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username  = findViewById(R.id.username);
        roomOptions = findViewById(R.id.room_option);
        joinChat = findViewById(R.id.join_chat);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chat_rooms, android.R.layout.simple_list_item_1);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomOptions.setAdapter(adapter);

        roomOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectdRoom = roomOptions.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
           instantiateWebSocket();





           //// when you click to join chat
           joinChat.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(validateuserName()){
                       Intent intent = new Intent(getBaseContext(),ChatRoom.class);
                        intent.putExtra("CHAT_ROOM",selectdRoom);
                       intent.putExtra("USER_NAME",username.getEditText().getText().toString());
                       startActivity(intent);
                   }
               }
           });


    }

    private Boolean validateuserName() {
        String val = username.getEditText().getText().toString();
        if(val.isEmpty()){
            username.setError("username cant be empty");
            return false;
        }
        username.setError(null);
        username.setEnabled(true);
        return true;
    }


    private void instantiateWebSocket() {
//        try{
//            IO.Options opts = new IO.Options();
//            opts.transports = new String[] { Polling.NAME };
//            socket = IO.socket("http://192.168.0.13:3000",opts);
//            socket.connect();
//
//            if(socket.connected()){
//                System.out.println("connection approved");
//            }
//            else{
//                System.out.println("connection failed");
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }


    }


    ////// callback functions

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        socket.disconnect();

    }


}
