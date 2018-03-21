package com.douglasharvey.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.douglasharvey.bakingapp.R;
import com.douglasharvey.bakingapp.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity //implements StepFragment.OnFragmentInteractionListener
 {

    private ArrayList<Step> stepArrayList = new ArrayList<>();
    private String recipeName;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.sliding_tabs)
    TabLayout slidingTabs;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.view_pager_steps)
    ViewPager viewPagerSteps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);
        stepArrayList = getIntent().getParcelableArrayListExtra(getResources().getString(R.string.EXTRA_STEPS));
        int selectedPosition = getIntent().getIntExtra(getResources().getString(R.string.EXTRA_STEP_POSITION), 0);
        recipeName = getIntent().getStringExtra(getResources().getString(R.string.EXTRA_RECIPE_NAME));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPagerSteps.setAdapter(sectionsPagerAdapter);
        viewPagerSteps.setCurrentItem(selectedPosition);

        slidingTabs.setupWithViewPager(viewPagerSteps);

        handleActionBar();

    }

    private void handleActionBar() {
        ActionBar actionBar =
                getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(recipeName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
/*
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
*/
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return StepFragment.newInstance(stepArrayList.get(position).getVideoURL(), stepArrayList.get(position).getDescription());
        }

        @Override
        public int getCount() {
            return stepArrayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return stepArrayList.get(position).getShortDescription();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, StepActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
