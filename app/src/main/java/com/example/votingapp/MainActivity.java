package com.example.votingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity {
    private RecyclerView recyclerView;
    private CandidatesAdapter adapter;
    List<CandidateData> listCandidates;
//    MyApplication myApplication = (MyApplication) this.getApplication();
    private CandidateDataService candidateDataService;
    private TextView tvTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        candidateDataService = new CandidateDataService(this);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        dividerItemDecoration.setDrawable(
                Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.devider))
        );
        recyclerView.addItemDecoration(dividerItemDecoration);

        listCandidates = MyApplication.getListCandidates();
        adapter = new CandidatesAdapter(listCandidates, this, candidateDataService);
        recyclerView.setAdapter(adapter);

        tvTotal = findViewById(R.id.total);

        loadListCandidates();
    }

    private void loadListCandidates() {
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

        candidateDataService.loadCandidates(deviceId, deviceName, new CandidateDataService.CandidateCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(List<CandidateData> candidates, int total) {
                Toast.makeText(MainActivity.this,
                        "Загружено: " + candidates.size() + " кандидатов. " + "total: " + total,
                        Toast.LENGTH_SHORT).show();

                MyApplication.setListCandidates(candidates);

                adapter.setCandidates(candidates, total);

                String text = tvTotal.getText() + " " + total;
                tvTotal.setText(text);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this,
                        message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
