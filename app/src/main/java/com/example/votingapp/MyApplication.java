package com.example.votingapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static List<CandidateData> listCandidates = new ArrayList<>();


    public static List<CandidateData> getListCandidates() {
        return listCandidates;
    }

    public static void setListCandidates(List<CandidateData> listCandidates) {
        MyApplication.listCandidates = listCandidates;
    }
}
