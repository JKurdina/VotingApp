package com.example.votingapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandidateDataService {

    private RequestQueue requestQueue;
    private Gson gson;
    public static final String QUERY_LIST_CANDIDATES = "https://adlibtech.ru/elections/api/getcandidates.php";
    public static final String QUERY_ADD_VOTE = "https://adlibtech.ru/elections/api/addvote.php ";

    public CandidateDataService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    public interface CandidateCallback {
        void onResponse(List<CandidateData> candidates, int total);
        void onError(String message);
    }
    public void loadCandidates(String deviceId, String deviceName, CandidateCallback candidateCallback) {
        StringRequest request = new StringRequest(
                Request.Method.POST, QUERY_LIST_CANDIDATES,
                response -> {
                    try {
                        Log.d("API", "Получен ответ: " + response);

                        JsonArray arrayObj = gson.fromJson(response, JsonArray.class);

                        if (arrayObj == null || arrayObj.isEmpty()) {
                            candidateCallback.onError("Пустой ответ от сервера");
                            return;
                        }

                        JsonObject totalObj = arrayObj.get(arrayObj.size() - 1).getAsJsonObject();
                        int total = 0;

                        if (totalObj.has("total")) {
                            total = totalObj.get("total").getAsInt();
                            arrayObj.remove(arrayObj.size() - 1);
                        }

                        Type listType = new TypeToken<List<CandidateData>>(){}.getType();
                        List<CandidateData> candidates = gson.fromJson(arrayObj, listType);

                        candidateCallback.onResponse(candidates, total);

                    } catch (Exception e) {
                        candidateCallback.onError("Ошибка: " + e.getMessage());
                        Log.e("API", "Ошибка парсинга", e);
                    }
                },
                error -> {
                    String message = "Ошибка сети";
                    if (error.networkResponse != null) {
                        message += " (код: " + error.networkResponse.statusCode + ")";
                    }
                    candidateCallback.onError(message);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", deviceId);
                params.put("device_name", deviceName);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public interface VoteCallback {
        void onResponse(int numVotes);
        void onError(String message);
    }

    public void addVote(String deviceId, String deviceName, int candidateId, int prevCandidateId, VoteCallback voteCallback) {
        StringRequest request = new StringRequest(
                Request.Method.POST, QUERY_ADD_VOTE,
                response -> {
                    try {
                        Log.d("API", "Получен ответ: " + response);
                        int numVotes = Integer.parseInt(response);
                        voteCallback.onResponse(numVotes);

                    } catch (Exception e) {
                        voteCallback.onError("Ошибка: " + e.getMessage());
                        Log.e("API", "Ошибка парсинга", e);
                    }
                },
                error -> {
                    String message = "Ошибка сети";
                    if (error.networkResponse != null) {
                        message += " (код: " + error.networkResponse.statusCode + ")";
                    }
                    voteCallback.onError(message);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", deviceId);
                params.put("device_name", deviceName);
                params.put("candidate_id", String.valueOf(candidateId));
                params.put("last_id", String.valueOf(prevCandidateId));
                return params;
            }
        };

        requestQueue.add(request);
    }
}
