package com.example.votingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CandidatesAdapter extends RecyclerView.Adapter<CandidatesAdapter.ViewHolder> {

    private List<CandidateData> listCandidates;
    private int total;
    private final Context context;
    private final CandidateDataService candidateDataService;
    private int selectedPosition = -1;

    public static final String QUERY_IMAGE = "https://adlibtech.ru/elections/upload_images/";
    private final Map<String, Bitmap> imageCache = new WeakHashMap<>();

    public CandidatesAdapter(List<CandidateData> list, Context context, CandidateDataService candidateDataService) {
        this.listCandidates = list != null ? list : new ArrayList<>();
        this.context = context;
        this.total = 0;
        this.candidateDataService = candidateDataService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candidate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        CandidateData candidate = listCandidates.get(position);

        String fullname = candidate.getFirstname() + " " + candidate.getThirdName();
        String votes = "Голосов: " + candidate.getVotes();
        String totalVotes = "Процент: " + (int) Math.round(candidate.getVotes() * 100.0 / this.total) + "%";

        holder.tvSecondName.setText(candidate.getSecondname());
        holder.tvFirstName.setText(fullname);
        holder.tvVotes.setText(votes);
        holder.tvTotal.setText(totalVotes);
        loadCandidateImage(holder.ivCandidatePic, QUERY_IMAGE + candidate.getImage());
        holder.parentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CandidateInfoActivity.class);
            intent.putExtra("candidate_id", candidate.getId());
            context.startActivity(intent);
        });

        if (holder.getBindingAdapterPosition() == selectedPosition) {
            holder.ivVoteCheckmark.setVisibility(View.VISIBLE);
        } else {
            holder.ivVoteCheckmark.setVisibility(View.GONE);
        }
        holder.ivVoteSquare.setOnClickListener(v -> {
            if (holder.getBindingAdapterPosition() == selectedPosition) {
                return;
            }
            int previousSelected = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();

            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
            String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

            int candidateId = candidate.getId();
            int prevCandidateId = previousSelected != -1 ? listCandidates.get(previousSelected).getId() : -1;
            candidateDataService.addVote(deviceId, deviceName, candidateId, prevCandidateId, new CandidateDataService.VoteCallback() {
                @Override
                public void onResponse(int numVotes) {
                    updateNumVotes(numVotes, selectedPosition, previousSelected);
                    Toast.makeText(context, "Количество голосов обновлено!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(context, "Ошибка при выборе кандидата", Toast.LENGTH_LONG).show();
                }
            });
        });

    }

    @Override
    public int getItemCount() { return listCandidates.size(); }
    @SuppressLint("NotifyDataSetChanged")
    public void setCandidates(List<CandidateData> newCandidates, int total) {

        if (newCandidates == null) {
            this.listCandidates = new ArrayList<>();
        } else {
            this.listCandidates = new ArrayList<>(newCandidates);
            this.total = total;
        }

        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateNumVotes(int numVotes, int position, int prevposition) {
        CandidateData newCandidate = listCandidates.get(position);
        newCandidate.setVotes(numVotes);

        if (prevposition != -1) {
            CandidateData prevCandidate = listCandidates.get(prevposition);
            prevCandidate.setVotes(prevCandidate.getVotes()-1);
        }
        notifyDataSetChanged();
    }
    private void loadCandidateImage(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        Bitmap cachedBitmap = imageCache.get(imageUrl);
        if (cachedBitmap != null && !cachedBitmap.isRecycled()) {
            imageView.setImageBitmap(cachedBitmap);
            return;
        }

        int cornerRadiusDp = 25;
        int borderWidthDp = 10;

        // Преобразуем dp в пиксели
        float density = context.getResources().getDisplayMetrics().density;
        int cornerRadius = (int) (cornerRadiusDp * density);
        int borderWidth = (int) (borderWidthDp * density);

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .apply(new RequestOptions()
                        .centerCrop()
                        .override(500, 500))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {

                        // Создаем Bitmap с рамкой
                        Bitmap output = Bitmap.createBitmap(
                                bitmap.getWidth() + borderWidth * 2,
                                bitmap.getHeight() + borderWidth * 2,
                                Bitmap.Config.ARGB_8888
                        );

                        Canvas canvas = new Canvas(output);

                        // Рисуем закругленную рамку
                        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        borderPaint.setColor(Color.parseColor("#9E9E9E"));
                        borderPaint.setStyle(Paint.Style.FILL);

                        RectF borderRect = new RectF(0, 0, output.getWidth(), output.getHeight());
                        canvas.drawRoundRect(borderRect, cornerRadius + borderWidth, cornerRadius + borderWidth, borderPaint);

                        // Рисуем закругленное изображение
                        Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        RectF imageRect = new RectF(borderWidth, borderWidth,
                                output.getWidth() - borderWidth, output.getHeight() - borderWidth);

                        Path path = new Path();
                        path.addRoundRect(imageRect, cornerRadius, cornerRadius, Path.Direction.CW);
                        canvas.clipPath(path);

                        canvas.drawBitmap(bitmap, borderWidth, borderWidth, imagePaint);

                        // Сохраняем
                        imageCache.put(imageUrl, output);

                        // Устанавливаем изображение
                        imageView.setImageBitmap(output);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        imageView.setImageDrawable(placeholder);
                    }
                });
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFirstName, tvSecondName, tvVotes, tvTotal;
        ImageView ivCandidatePic, ivVoteSquare, ivVoteCheckmark;
        LinearLayout parentLayout;
        ViewHolder(View itemView) {
            super(itemView);
            tvFirstName = itemView.findViewById(R.id.textName);
            tvSecondName = itemView.findViewById(R.id.textSecondName);
            tvVotes = itemView.findViewById(R.id.textVotes);
            tvTotal = itemView.findViewById(R.id.textVotesPercent);
            ivCandidatePic = itemView.findViewById(R.id.imageCandidate);
            parentLayout = itemView.findViewById(R.id.itemLayout);
            ivVoteSquare = itemView.findViewById(R.id.voteBorder);
            ivVoteCheckmark = itemView.findViewById(R.id.voteCheckmark);
        }
    }
}
