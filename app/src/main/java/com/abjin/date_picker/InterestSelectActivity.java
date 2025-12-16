package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterestSelectActivity extends AppCompatActivity {

    private InterestAdapter adapter;
    private MaterialButton btnNext;
    private Set<String> selectedInterests = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_select);

        RecyclerView rvInterests = findViewById(R.id.rvInterests);
        btnNext = findViewById(R.id.btnNext);

        List<Interest> interests = new ArrayList<>();
        interests.add(new Interest("🍽", "맛집 / 카페"));
        interests.add(new Interest("🎬", "영화 / 공연"));
        interests.add(new Interest("🏞", "산책 / 야경"));
        interests.add(new Interest("🎨", "전시 / 미술관"));
        interests.add(new Interest("🎮", "액티비티"));
        interests.add(new Interest("🛍", "쇼핑"));
        interests.add(new Interest("🎲", "랜덤 코스"));

        adapter = new InterestAdapter(interests, this::onInterestToggled);
        rvInterests.setLayoutManager(new GridLayoutManager(this, 2));
        rvInterests.setAdapter(adapter);

        btnNext.setEnabled(false);
        btnNext.setOnClickListener(v -> {
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
