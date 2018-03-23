package com.douglasharvey.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.douglasharvey.bakingapp.R;
import com.douglasharvey.bakingapp.adapters.StepAdapter;
import com.douglasharvey.bakingapp.models.Recipe;
import com.douglasharvey.bakingapp.models.Step;
import com.douglasharvey.bakingapp.uihelper.ItemClickSupport;
import com.douglasharvey.bakingapp.uihelper.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class DetailActivity extends AppCompatActivity
{
    private static Recipe recipe;
    @BindView(R.id.tv_ingredients)
    TextView tvIngredients;
    @BindView(R.id.rv_steps_list)
    RecyclerView rvStepsList;

    private boolean twoPane;
    private StepFragment stepFragment;
    private final String FRAGMENT_TAG = "steps_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        checkTwoPane();

        Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra(getString(R.string.EXTRA_SELECTED_RECIPE))) {
            Bundle data = receivedIntent.getExtras();
            if (data != null) {
                recipe = data.getParcelable(getString(R.string.EXTRA_SELECTED_RECIPE));
                populateUI();
            }
        }

        if (twoPane) {
            if (savedInstanceState != null) { // to handle config. change correctly first looking for existing fragment
                stepFragment = (StepFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            } else if (stepFragment == null) {
                //Defaulting to opening first step's video
                Step firstStep = recipe.getSteps().get(0);
                createNewStepFragment(firstStep.getVideoURL(), firstStep.getDescription(),firstStep.getThumbnailURL());
            }
        } else if (savedInstanceState == null) { //by default, scrollview position is saved therefore only want to scrolltoTop on first entry to a recipe.
            scrollToTop();
        }

    }

    private void checkTwoPane() {
        if (findViewById(R.id.fl_video_step_container) != null) {
            twoPane = true;
        }
    }

    private void createNewStepFragment(String videoUrl, String description, String thumbnailUrl) {
        StepFragment stepFragment = StepFragment.newInstance(videoUrl, description, thumbnailUrl);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_video_step_container, stepFragment, FRAGMENT_TAG)
                .commit();
    }

    private void populateUI() {
        handleActionBar();
        setupIngredients();
        setupSteps();
    }

    private void scrollToTop() {
        ScrollView scrollView = findViewById(R.id.layout_detail); //cannot use butterknife for this because scrollview is only present for one-pane mode.
        scrollView.smoothScrollTo(0, 0);
    }

    private void handleStepClick(String videoUrl, String description, String thumbnailUrl, int position) {
        if (twoPane) createNewStepFragment(videoUrl, description, thumbnailUrl);
        else {
            Intent startStepIntent = new Intent(DetailActivity.this, StepActivity.class);
            startStepIntent.putParcelableArrayListExtra(getResources().getString(R.string.EXTRA_STEPS), recipe.getSteps());
            startStepIntent.putExtra(getResources().getString(R.string.EXTRA_STEP_POSITION), position);
            startStepIntent.putExtra(getResources().getString(R.string.EXTRA_RECIPE_NAME), recipe.getName());
            startActivity(startStepIntent);
        }
    }

    private void handleActionBar() {
        ActionBar actionBar =
                getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(recipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupIngredients() {
        tvIngredients.setText(UiUtils.formatIngredients(recipe.getIngredients()));
    }

    private void setupSteps() {
        rvStepsList.setHasFixedSize(true);
        StepAdapter adapter = new StepAdapter(recipe.getSteps());
        rvStepsList.setAdapter(adapter);
        ItemClickSupport.addTo(rvStepsList).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Step selectedStep = recipe.getSteps().get(position);
                        handleStepClick(selectedStep.getVideoURL(), selectedStep.getDescription(), selectedStep.getThumbnailURL(), position);
                    }
                }
        );

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, DetailActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
