package com.douglasharvey.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.douglasharvey.bakingapp.R;
import com.douglasharvey.bakingapp.adapters.MasterAdapter;
import com.douglasharvey.bakingapp.api.ApiEndpointInterface;
import com.douglasharvey.bakingapp.api.NetworkController;
import com.douglasharvey.bakingapp.models.Recipe;
import com.douglasharvey.bakingapp.uihelper.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class MasterListActivity extends AppCompatActivity {
    private static final String RECIPE_LIST = "recipe_list";
    @BindView(R.id.rv_recipe_master_list)
    RecyclerView rvRecipeMasterList;
    @BindView(R.id.pb_master_list)
    ProgressBar pbMasterList;
    private MasterAdapter adapter;
    ArrayList<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_list);
        ButterKnife.bind(this);
        if (NetworkController.isInternetAvailable(this)) {
            pbMasterList.setVisibility(View.VISIBLE);
            if (savedInstanceState == null || !savedInstanceState.containsKey(RECIPE_LIST)) {
                loadRecipes();
            } else {
                recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST);
                pbMasterList.setVisibility(View.INVISIBLE);
            }
            setupRecipesList();
        } else {
            Toast.makeText(this, R.string.error_internet_connectivity, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_LIST,
                recipeList);
    }

    private void setupRecipesList() {
        //NOTE: not setting LayoutManager here because it is done in AutoRecyclerView according to dynamically calculated spancount.

        rvRecipeMasterList.setHasFixedSize(true);
        adapter = new MasterAdapter();
        adapter.setRecipesData(recipeList);
        rvRecipeMasterList.setAdapter(adapter);
        ItemClickSupport.addTo(rvRecipeMasterList).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent startDetailIntent = new Intent(MasterListActivity.this, DetailActivity.class);
                        startDetailIntent.putExtra(getResources().getString(R.string.EXTRA_SELECTED_RECIPE), recipeList.get(position));
                        startActivity(startDetailIntent);
                    }
                }
        );
    }

    private void loadRecipes() {

        ApiEndpointInterface apiService =
                NetworkController
                        .getClient()
                        .create(ApiEndpointInterface.class);
        final Call<ArrayList<Recipe>> recipeListCall = apiService.getRecipe();

        recipeListCall.enqueue(new Callback<ArrayList<Recipe>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    adapter.setRecipesData(response.body());
                    recipeList = response.body();
                    pbMasterList.setVisibility(View.INVISIBLE);
                } else Timber.e("onResponse: %s", statusCode);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                Timber.e(t.getMessage());
            }
        });
    }

}
