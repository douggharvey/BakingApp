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

public class StepActivity extends AppCompatActivity
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

//    To handle Fragment config. changes correctly, the below 2 links were followed:
//  https://guides.codepath.com/android/Handling-Configuration-Changes#saving-and-restoring-fragment-state
//  https://learnpainless.com/android/how-to-get-fragment-from-viewpager-android

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager_steps + ":" + viewPagerSteps.getCurrentItem());
            if (page == null) {
                Step step = stepArrayList.get(position);
                page = StepFragment.newInstance(step.getVideoURL(), step.getDescription(), step.getThumbnailURL());
            }
            return page;
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
