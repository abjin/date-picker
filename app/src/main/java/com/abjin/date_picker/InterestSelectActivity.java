package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abjin.date_picker.preferences.UserPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterestSelectActivity extends AppCompatActivity {

    private InterestAdapter adapter;
    private MaterialButton btnNext;
    private Set<String> selectedInterests = new HashSet<>();
    private TextInputEditText etAdditionalRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_select);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        RecyclerView rvInterests = findViewById(R.id.rvInterests);
        btnNext = findViewById(R.id.btnNext);
        etAdditionalRequest = findViewById(R.id.etAdditionalRequest);

        // 기존 interests 로드
        UserPreferenceManager userPrefManager = UserPreferenceManager.getInstance(this);
        Set<String> existingInterests = userPrefManager.getInterests();
        if (!existingInterests.isEmpty()) {
            selectedInterests = new HashSet<>(existingInterests);
        }
        // 기존 추가 요청 프리필
        if (etAdditionalRequest != null) {
            String saved = userPrefManager.getAdditionalRequest();
            if (saved != null && !saved.isEmpty()) {
                etAdditionalRequest.setText(saved);
                etAdditionalRequest.setSelection(saved.length());
            }
        }

        List<Interest> interests = new ArrayList<>();
        interests.add(new Interest("🍽", "맛집 / 카페"));
        interests.add(new Interest("🎬", "영화 / 공연"));
        interests.add(new Interest("🏞", "산책 / 야경"));
        interests.add(new Interest("🎨", "전시 / 미술관"));
        interests.add(new Interest("🎮", "액티비티"));
        interests.add(new Interest("🛍", "쇼핑"));
        interests.add(new Interest("🎲", "랜덤 코스"));

        adapter = new InterestAdapter(interests, this::onInterestToggled);
        adapter.selectedItems.addAll(selectedInterests);
        rvInterests.setLayoutManager(new GridLayoutManager(this, 2));
        rvInterests.setNestedScrollingEnabled(false);
        rvInterests.setAdapter(adapter);

        btnNext.setEnabled(!selectedInterests.isEmpty());
        btnNext.setOnClickListener(v -> {
            userPrefManager.setInterests(selectedInterests);
            if (etAdditionalRequest != null) {
                String extra = etAdditionalRequest.getText() != null ? etAdditionalRequest.getText().toString().trim() : "";
                userPrefManager.setAdditionalRequest(extra);
            }

            Intent intent = new Intent(InterestSelectActivity.this, BudgetSelectActivity.class);
            startActivity(intent);
        });
    }

    private void onInterestToggled(String title, boolean isSelected) {
        if (isSelected) {
            selectedInterests.add(title);
        } else {
            selectedInterests.remove(title);
        }
        btnNext.setEnabled(!selectedInterests.isEmpty());
    }

    static class Interest {
        String emoji;
        String title;

        Interest(String emoji, String title) {
            this.emoji = emoji;
            this.title = title;
        }
    }

    static class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {
        private List<Interest> interests;
        private Set<String> selectedItems = new HashSet<>();
        private OnInterestToggleListener listener;

        interface OnInterestToggleListener {
            void onToggle(String title, boolean isSelected);
        }

        InterestAdapter(List<Interest> interests, OnInterestToggleListener listener) {
            this.interests = interests;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_interest, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Interest interest = interests.get(position);
            holder.bind(interest, selectedItems.contains(interest.title));
        }

        @Override
        public int getItemCount() {
            return interests.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvEmoji, tvTitle;
            MaterialCardView cardInterest;

            ViewHolder(View itemView) {
                super(itemView);
                tvEmoji = itemView.findViewById(R.id.tvEmoji);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                cardInterest = itemView.findViewById(R.id.cardInterest);
            }

            void bind(Interest interest, boolean isSelected) {
                tvEmoji.setText(interest.emoji);
                tvTitle.setText(interest.title);

                if (isSelected) {
                    cardInterest.setCardBackgroundColor(
                            itemView.getContext().getColor(R.color.primary_variant));
                    cardInterest.setCardElevation(8f);
                } else {
                    cardInterest.setCardBackgroundColor(
                            itemView.getContext().getColor(R.color.surface_variant));
                    cardInterest.setCardElevation(0f);
                }

                cardInterest.setOnClickListener(v -> {
                    boolean newState = !isSelected;
                    if (newState) {
                        selectedItems.add(interest.title);
                    } else {
                        selectedItems.remove(interest.title);
                    }
                    listener.onToggle(interest.title, newState);
                    notifyItemChanged(getAdapterPosition());
                });
            }
        }
    }
}
