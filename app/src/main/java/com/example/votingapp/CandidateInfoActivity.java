package com.example.votingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class CandidateInfoActivity extends Activity {
    List<CandidateData> listCandidates;
//    MyApplication myApplication = (MyApplication) this.getApplication();
    private int id;
    TextView tvSecondName, tvFullName, tvParty, tvDescription;
    ImageView ivCandidatePic;
    Button btn_back;
    public static final String QUERY_IMAGE = "https://adlibtech.ru/elections/upload_images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        tvSecondName = findViewById(R.id.textLastName);
        tvFullName = findViewById(R.id.textFirstName);
        tvParty = findViewById(R.id.textParty);
        tvDescription = findViewById(R.id.textDescription);
        ivCandidatePic = findViewById(R.id.imageCandidate);
        btn_back = findViewById(R.id.buttonBack);

        btn_back.setOnClickListener(v -> finish());

        listCandidates = MyApplication.getListCandidates();

        Intent intent = getIntent();
        id = intent.getIntExtra("candidate_id", -1);

        loadInfoCandidate();
    }

    @SuppressLint("SetTextI18n")
    private void loadInfoCandidate() {
        CandidateData candidate = null;

        if (id >= 0) {
            for (CandidateData c: listCandidates) {
                if (c.getId() == id) {
                    candidate = c;
                }
            }
            if (candidate != null) {
                tvSecondName.setText(candidate.getSecondname());
                tvFullName.setText(candidate.getFirstname() + " " + candidate.getThirdName());
                tvParty.setText(candidate.getParty());
                tvDescription.setText(candidate.getDescription());
                Glide.with(this).load(QUERY_IMAGE + candidate.getImage()).into(ivCandidatePic);
            } else {
                Toast.makeText(CandidateInfoActivity.this, "Ошибка загрузки кандидата", Toast.LENGTH_LONG).show();
            }

        }
    }
}
