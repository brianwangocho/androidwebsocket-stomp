package com.example.sockettest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;

import java.net.Socket;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class ChatRoom extends AppCompatActivity {
    EditText messageBox;
    Button send;
    ListView messageList;
    TextView roomName;
     MessageListAdapter adapter = new MessageListAdapter();
     WebSocket webSocket;
     MainActivity activity;
    String chatRoom;
    String userName;
    private Socket mSocket;
    private StompClient mStompClient;
    Date now = new Date();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
         chatRoom = getIntent().getStringExtra("CHAT_ROOM");
        userName = getIntent().getStringExtra("USER_NAME");

        ///connect to the socket for this room

        roomName = findViewById(R.id.room_name);
        roomName.setText(chatRoom);
        messageList = findViewById(R.id.message_list);
        messageBox = findViewById(R.id.message_box);
        send = findViewById(R.id.send);
        messageList.setAdapter(adapter);

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,"ws://192.168.0.13:8080/client" );
        mStompClient.withServerHeartbeat(1000);
        mStompClient.connect();

//       websocketOkhttp();


//        instantiateWebSocket();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setMessage(messageBox.getText().toString());
                message.setUserName(userName);
               String time = new SimpleDateFormat("HH:mm:a", Locale.ENGLISH).format(now);
                message.setTime(time);
//                adapter.addMessage(message);
                Gson gson = new Gson();
                String json = gson.toJson(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStompClient.send("/app/message/"+chatRoom, json).subscribe(
                                () -> Log.d("socket-data", json),
                                error -> Log.e("socket-error", "Encountered error while sending data!", error)
                        );

                    }
                });



            }
        });



        mStompClient.topic("/topic/"+chatRoom).subscribe(data -> {
            Log.i("server-data", "Received message: " + data.getPayload());
            runOnUiThread(new Runnable() {

                Gson gson = new Gson();
                Message message = gson.fromJson(data.getPayload(),Message.class);
                @Override
                public void run() {
                    Message message1 = new Message();
                    message1.setUserName(message.userName);
                    message1.setMessage(message.getMessage());
                    message1.setTime(message.getTime());
                    adapter.addMessage(message1);
                }
            });


        });

    }

//    private void websocketOkhttp() {
//
//        try{
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder().url("ws://192.168.0.13:8080/client").build();
//            SocketListener socketListener = new SocketListener(this);
//            webSocket = client.newWebSocket(request,socketListener);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }


    public class SocketListener extends WebSocketListener {

        public  ChatRoom activity;


        public SocketListener(ChatRoom activity) {
            this.activity = activity;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            webSocket.send("hello");

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            webSocket.cancel();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {

            Log.d("response",t.getMessage());

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"CONNECTION FAILED",Toast.LENGTH_LONG).show();
                }
            });
        }
    }


//    private void instantiateWebSocket() {
//                try{
//            IO.Options opts = new IO.Options();
//            opts.transports = new String[] { Polling.NAME };
//            socket = IO.socket("http://192.168.0.13:3000",opts);
//            socket.connect();
//            socket.on("chatMessage",onNewMessage);
//
//            ///send to socket on joining chat
////                    JoinRoom joinRoom = new JoinRoom();
////                    joinRoom.setUserName(userName);
////                    joinRoom.setRoom(chatRoom);
//
//                    JSONObject data = new JSONObject();
//                    data.put("userName", userName);
//                    data.put("room",chatRoom);
//
//                    socket.emit("joinRoom",data);
//
//                    //works
////           socket.emit("join",userName);
//
//            socket.on("joinRoom", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            try{
//                                JSONObject data = (JSONObject) args[0];
//                                Message m = new Message();
//                                String messageData = data.get("message").toString();
//                                m.setUserName(data.get("UserName").toString());
//                                m.setMessage(data.get("message").toString());
//                                m.setTime(data.get("time").toString());
//                                Toast.makeText(getApplicationContext(), messageData,
//                                        Toast.LENGTH_SHORT).show();
//                                adapter.addMessage(m);
//
//
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                        }
//                    });
//
//
//                }
//            });
//
////
////            socket.on("chatMessage", new Emitter.Listener() {
////                @Override
////                public void call(Object... args) {
////
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            JSONObject data = (JSONObject) args[0];
////                            try{
////                                Message m = new Message();
////                                String messageData = data.get("message").toString();
////                                m.setUserName(data.get("UserName").toString());
////                                m.setMessage(data.get("message").toString());
////                                m.setTime(data.get("time").toString());
////                                Toast.makeText(getApplicationContext(), messageData,
////                                        Toast.LENGTH_SHORT).show();
////                                adapter.addMessage(m);
////                            }
////                            catch (Exception e){
////
////                            }
////
////
////                        }
////                    });
////
////                }
////            });
////
//
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }


    public class MessageListAdapter  extends BaseAdapter {

        List<Message> messages = new ArrayList<>();
        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.message_list_item,parent,false);
            }
            TextView userName = convertView.findViewById(R.id.userName);
            TextView message = convertView.findViewById(R.id.message);
            TextView time = convertView.findViewById(R.id.time);

            Message message1 = messages.get(position);
            userName.setText(message1.getUserName());
            message.setText(message1.getMessage());
            time.setText(message1.getTime());
            return convertView;
        }

        public void addMessage(Message message){
            messages.add(message);
            notifyDataSetChanged();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStompClient.disconnect();


    }


//
//    Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    try{
//                        Message m = new Message();
//                        String messageData = data.get("message").toString();
//                        m.setUserName(data.get("UserName").toString());
//                        m.setMessage(data.get("message").toString());
//                        m.setTime(data.get("time").toString());
//                        Toast.makeText(getApplicationContext(), messageData,
//                                Toast.LENGTH_SHORT).show();
//
//                        Log.i( "message " ,data.get("message").toString());
//                        adapter.addMessage(m);
//                    }
//                    catch (Exception e){
//
//                    }
//
//                }
//            });
//        }
//    };










}
