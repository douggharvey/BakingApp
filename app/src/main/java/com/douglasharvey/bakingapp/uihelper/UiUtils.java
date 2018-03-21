package com.douglasharvey.bakingapp.uihelper;

import com.douglasharvey.bakingapp.models.Ingredient;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UiUtils {
    public static StringBuilder formatIngredients(List<Ingredient> ingredientList) {
        StringBuilder ingredientsDisplay = new StringBuilder(100);
        for (Ingredient ingredient : ingredientList) {
//https://stackoverflow.com/questions/3904579/how-to-capitalize-the-first-letter-of-a-string-in-java
            ingredientsDisplay.append("- "); //todo find a better character?
            ingredientsDisplay.append(StringUtils.capitalize(ingredient.getIngredient()));
            ingredientsDisplay.append(" (");
            ingredientsDisplay.append(ingredient.getQuantity());
            ingredientsDisplay.append(" ");
            ingredientsDisplay.append(ingredient.getMeasure().toLowerCase());
            ingredientsDisplay.append(")\n");
        }
        return ingredientsDisplay;
    }

}
