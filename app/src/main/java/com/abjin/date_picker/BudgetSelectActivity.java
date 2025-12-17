package com.abjin.date_picker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.abjin.date_picker.preferences.UserPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

public class BudgetSelectActivity extends AppCompatActivity {

    private MaterialCardView cardPreset1, cardPreset2, cardPreset3;
    private Slider sliderBudget;
    private TextView tvBudgetAmount;
    private int selectedPreset = -1;
    private float currentBudget = 5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_select);

        cardPreset1 = findViewById(R.id.cardPreset1);
        cardPreset2 = findViewById(R.id.cardPreset2);
        cardPreset3 = findViewById(R.id.cardPreset3);
        sliderBudget = findViewById(R.id.sliderBudget);
        tvBudgetAmount = findViewById(R.id.tvBudgetAmount);
        MaterialButton btnNext = findViewById(R.id.btnNext);

        // 기존 budget 로드
        UserPreferenceManager userPrefManager = UserPreferenceManager.getInstance(this);
        float existingBudget = userPrefManager.getBudget();
        if (existingBudget > 0) {
            currentBudget = existingBudget;
            sliderBudget.setValue(existingBudget);
        } else {
            sliderBudget.setValue(5f);
        }
        updateBudgetText(currentBudget);

        cardPreset1.setOnClickListener(v -> selectPreset(1, 4f));
        cardPreset2.setOnClickListener(v -> selectPreset(2, 8f));
        cardPreset3.setOnClickListener(v -> selectPreset(3, 12f));

        sliderBudget.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                selectedPreset = -1;
                updateCardStyles();
            }
            currentBudget = value;
            updateBudgetText(value);
        });

        btnNext.setOnClickListener(v -> {
            // 선택한 budget 저장
            userPrefManager.setBudget(currentBudget);

            Intent intent = new Intent(BudgetSelectActivity.this, LocationSelectActivity.class);
            startActivity(intent);
        });
    }

    private void selectPreset(int preset, float budget) {
        selectedPreset = preset;
        currentBudget = budget;
        sliderBudget.setValue(budget);
        updateBudgetText(budget);
        updateCardStyles();
    }

    private void updateCardStyles() {
        updateCardStyle(cardPreset1, selectedPreset == 1);
        updateCardStyle(cardPreset2, selectedPreset == 2);
        updateCardStyle(cardPreset3, selectedPreset == 3);
    }

    private void updateCardStyle(MaterialCardView card, boolean isSelected) {
        if (isSelected) {
            card.setCardBackgroundColor(getColor(R.color.primary_variant));
            card.setCardElevation(8f);
            card.setStrokeColor(getColor(R.color.primary));
            card.setStrokeWidth(4);
        } else {
            card.setCardBackgroundColor(getColor(R.color.surface_variant));
            card.setCardElevation(0f);
            card.setStrokeWidth(0);
        }
    }

    private void updateBudgetText(float value) {
        int budgetInt = Math.round(value);
        tvBudgetAmount.setText(budgetInt + "만원");
    }
}
