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
import com.douglasharvey.bakingapp.uihelper.ItemClickSupport;
import com.douglasharvey.bakingapp.uihelper.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class DetailActivity extends AppCompatActivity //implements StepFragment.OnFragmentInteractionListener
        {
    private static Recipe recipe;
    private boolean twoPane;
    
    private StepFragment stepFragment;
    private final String FRAGMENT_TAG = "myfragmenttag";

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

        checkTwoPane();

        if (receivedIntent.hasExtra(getString(R.string.EXTRA_SELECTED_RECIPE))) {
            Bundle data = receivedIntent.getExtras();
            if (data != null) {
                Timber.d("onCreate: get parcelable");
                recipe = data.getParcelable(getString(R.string.EXTRA_SELECTED_RECIPE));
                populateUI();
            }
        }

        if (twoPane) {
            if (savedInstanceState != null ) {
                stepFragment = (StepFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                Timber.d("onCreate: got fragment by tag");
            } else if (stepFragment == null) {
                handleTwoPaneMode();
            }
        }

    }

    private void checkTwoPane() {
        if (findViewById(R.id.fl_video_step_container) != null) {
            twoPane = true;
        }
    }

    private void createNewStepFragment(String videoUrl, String description) {
        //normally inside adapter
 /*       Bundle arguments = new Bundle();
        arguments.putString(StepFragment.VIDEO_URL, videoUrl);
        arguments.putString(StepFragment.DESCRIPTION, description);
        //StepFragment stepFragment = new StepFragment();
       //     stepFragment.setArguments(arguments);
 */
        Timber.d("createNewStepFragment: ");
        StepFragment stepFragment = StepFragment.newInstance(videoUrl, description);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_video_step_container, stepFragment, FRAGMENT_TAG)
                .commit();
        // TODO need to auto play first step on initial entry. Consider playing video in full screen then when finished going to other screen
        //todo save exoplayer position, consider option to automatically play full screen video (for phone especially).
        //or just enable full screen button as per
    }
    //TODO SHOW WHICH STEPS HAVE VIDEOS
    private void populateUI() {
        //todo check lint warning
        handleActionBar();
        setupIngredients();
        setupSteps();
        if (!twoPane) handleOnePaneMode();

    }

    private void handleTwoPaneMode() { //todo maybe cleanup this?
        createNewStepFragment(recipe.getSteps().get(0).getVideoURL(), recipe.getSteps().get(0).getDescription());
    }

    private void handleOnePaneMode() {
        scrollToTop();
    }

    private void handleStepClick(String videoUrl, String description, int position) {
        if (twoPane) createNewStepFragment(videoUrl, description);
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
                        handleStepClick(recipe.getSteps().get(position).getVideoURL(), recipe.getSteps().get(position).getDescription(),position);
//todo need to change colour of clicked position or some other method

                    }
                }
        );

    }

    private void scrollToTop() {
        ScrollView scrollView = findViewById(R.id.layout_detail);
        scrollView.smoothScrollTo(0, 0); // todo need to save scroll position so that re-create of activity goes to same place
    }

/*    @Override
    public void onFragmentInteraction(Uri uri) {

    }
*/
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
