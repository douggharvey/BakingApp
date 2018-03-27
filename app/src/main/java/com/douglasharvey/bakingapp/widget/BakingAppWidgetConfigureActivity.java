package com.douglasharvey.bakingapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.douglasharvey.bakingapp.R;
import com.douglasharvey.bakingapp.api.ApiEndpointInterface;
import com.douglasharvey.bakingapp.api.NetworkController;
import com.douglasharvey.bakingapp.models.Recipe;
import com.douglasharvey.bakingapp.uihelper.UiUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class BakingAppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.douglasharvey.bakingapp.widget.BakingAppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ArrayList<Recipe> recipeList;
    private Spinner spinner;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = BakingAppWidgetConfigureActivity.this;
            int position;

            position = spinner.getSelectedItemPosition();

            String widgetText = recipeList.get(position).getName() + "\n\n" + UiUtils.formatIngredients(recipeList.get(position).getIngredients());
            saveTextPref(context, mAppWidgetId, widgetText);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BakingAppWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public BakingAppWidgetConfigureActivity() {
        super();
    }

    private static void saveTextPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static String loadTextPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTextPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_app_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        if (NetworkController.isInternetAvailable(this)) {
            loadRecipes();
        }
        else
        {
            Toast.makeText(this, R.string.error_internet_connectivity, Toast.LENGTH_LONG).show();
            findViewById(R.id.add_button).setVisibility(View.GONE);
        }
    }

    private void loadRecipes() {
        ApiEndpointInterface apiService =
                NetworkController
                        .getClient(NetworkController.getOkHttpClient())
                        .create(ApiEndpointInterface.class);
        final Call<ArrayList<Recipe>> recipeListCall = apiService.getRecipe();

        recipeListCall.enqueue(new Callback<ArrayList<Recipe>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    recipeList = response.body();
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (Recipe recipe : recipeList) {
                        arrayList.add(recipe.getName());
                    }
                    setupSpinner(arrayList);

                } else Timber.e("onResponse: %s", statusCode);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                Timber.e(t.getMessage());
            }
        });
    }

    private void setupSpinner(ArrayList<String> arrayList) {
        spinner = findViewById(R.id.appwidget_spinner);
        @SuppressWarnings("unchecked") ArrayAdapter<String> adapter = new ArrayAdapter(BakingAppWidgetConfigureActivity.this,
                android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

}

