package com.example.hairsalonbookingstaff;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Adapter.AdminAssessmentAdapter;
import com.example.hairsalonbookingstaff.Common.MySocket;
import com.example.hairsalonbookingstaff.Common.SpaceItemDecoration;
import com.example.hairsalonbookingstaff.Model.Assessment;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StaffAssessmentActivity extends AppCompatActivity {
    RecyclerView recycler_assessment;
    TextView txt_name_barber;
    AdminAssessmentAdapter adapter;
    List<Assessment> assessmentList;
    Socket mSocket = MySocket.getmSocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_assessment);
        mSocket.connect();
        assessmentList = new ArrayList<>();
        recycler_assessment = findViewById(R.id.recycler_assessment);
        recycler_assessment.setLayoutManager(new LinearLayoutManager(this));
        recycler_assessment.addItemDecoration(new SpaceItemDecoration(8));
        recycler_assessment.setHasFixedSize(true);
        txt_name_barber = findViewById(R.id.txt_name_barber);
        String id_baber = getIntent().getStringExtra("ID_BARBER");
        String name_barber = getIntent().getStringExtra("NAME_BARBER");
        txt_name_barber.setText(name_barber);
        mSocket.emit("getAssessment", id_baber).on("getAssessment", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Handler handler = new Handler(getMainLooper());
                JSONObject object = (JSONObject) args[0];
                if (object != null) {
                    try {
                        assessmentList.add(new Assessment(object.getString("date"), object.getString("commend"), object.getString("rate"), object.getString("time")));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new AdminAssessmentAdapter(StaffAssessmentActivity.this, assessmentList);
                                recycler_assessment.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
