package com.douglasharvey.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.douglasharvey.bakingapp.R;
import com.douglasharvey.bakingapp.adapters.StepAdapter;
import com.douglasharvey.bakingapp.models.Ingredient;
import com.douglasharvey.bakingapp.models.Recipe;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class DetailActivity extends AppCompatActivity {

    private static Recipe recipe;

    private StepAdapter adapter;

    @BindView(R.id.tv_ingredients)
    TextView tvIngredients;
    @BindView(R.id.rv_steps_list)
    RecyclerView rvStepsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent receivedIntent = getIntent();


//  //TODO add this back      if (receivedIntent.hasExtra(getString(R.string.EXTRA_SELECTED_RECIPE))) {
        Bundle data = receivedIntent.getExtras();
        if (data != null) {
            recipe = data.getParcelable(getString(R.string.EXTRA_SELECTED_RECIPE));
            populateUI(); //todo check scroll functionality
        }
    }

    private void populateUI() {
        //todo check lint warning
        getSupportActionBar().setTitle(recipe.getName()); //todo add servings?
        setupIngredients();
        setupSteps();
    }

    private void setupIngredients() {
        StringBuilder ingredientsDisplay = new StringBuilder(100);
        for (Ingredient ingredient : recipe.getIngredients()) {
//https://stackoverflow.com/questions/3904579/how-to-capitalize-the-first-letter-of-a-string-in-java
            ingredientsDisplay.append(StringUtils.capitalize(ingredient.getIngredient()));
            ingredientsDisplay.append(" (");
            ingredientsDisplay.append(ingredient.getQuantity());
            ingredientsDisplay.append(" ");
            ingredientsDisplay.append(ingredient.getMeasure().toLowerCase());
            ingredientsDisplay.append(")\n");
        }
        tvIngredients.setText(ingredientsDisplay);
    }


    private void setupSteps() {
        rvStepsList.setHasFixedSize(true);
        adapter = new StepAdapter(recipe.getSteps());
        rvStepsList.setAdapter(adapter);
    }

}
