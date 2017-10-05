package com.popland.pop.aboutnodejs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvUser, lvChat;
    Button btnDangki, btnGui;
    EditText edtType;
    ArrayList<String> arrlUser, arrlTinnhan;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.95:3000/");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvUser = (ListView)findViewById(R.id.LVuser);
        lvChat = (ListView)findViewById(R.id.LVchat);
        btnDangki = (Button)findViewById(R.id.BTNdangki);
        btnGui = (Button)findViewById(R.id.BTNgui);
        edtType = (EditText)findViewById(R.id.EDTtype);
        arrlTinnhan = new ArrayList<String>();
        mSocket.connect();

        btnDangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("Client gui dang ki user", edtType.getText().toString());
            }
        });
        mSocket.on("Serverthongbaodangki", onNewMessage_dangki);
        mSocket.on("ServerGuiMangUser",onNewMessage_mangUser);

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("ClientGuiTinNhan", edtType.getText().toString());
            }
        });
        mSocket.on("ServerGuiTinNhan",onNewMessage_tinnhan);
    }

    private Emitter.Listener onNewMessage_tinnhan = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String tinnhan="";
                    try {
                        tinnhan = data.getString("noidung");
                        arrlTinnhan.add(tinnhan);
                        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,arrlTinnhan);
                        lvChat.setAdapter(adapter);
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private Emitter.Listener onNewMessage_mangUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arrlUser = new ArrayList<String>();
                    JSONObject data = (JSONObject) args[0];
                    JSONArray mangUser;
                    try {
                        mangUser = data.getJSONArray("noidung");
                        for(int i=0;i<mangUser.length();i++){
                            arrlUser.add(mangUser.getString(i));
                        }
                        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,arrlUser);
                        lvUser.setAdapter(adapter);
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private Emitter.Listener onNewMessage_dangki = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String ketqua;
                    try {
                        ketqua = data.getString("noidung");
                        if(ketqua.equals("true"))
                            Toast.makeText(MainActivity.this,"thanh cong",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"that bai",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

}
