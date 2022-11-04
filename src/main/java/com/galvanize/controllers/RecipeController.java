package com.galvanize.controllers;

import com.galvanize.entities.Ingredient;
import com.galvanize.entities.Recipe;
import com.galvanize.entities.Step;
import com.galvanize.repositories.IngredientsRepository;
import com.galvanize.repositories.RecipeRepository;
import com.galvanize.repositories.StepRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@CrossOrigin
@RestController
public class RecipeController {
    RecipeRepository recipeRepository;
    IngredientsRepository ingredientsRepository;
    StepRepository stepRepository;

    public RecipeController(RecipeRepository recipeRepository, IngredientsRepository ingredientsRepository, StepRepository stepRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientsRepository = ingredientsRepository;
        this.stepRepository = stepRepository;
    }

    @GetMapping("/recipe")
    public Iterable<Recipe> getRecipes() {
        return this.recipeRepository.findAll();
    }

    @PostMapping("/recipe")
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe postRecipe(@RequestBody Recipe recipe) {
//
        recipe.getIngredientList().forEach((ingredient) -> {
            this.ingredientsRepository.save(ingredient);
        });

        recipe.getStepList().forEach(step -> {
            this.stepRepository.save(step);
        });
        return this.recipeRepository.save(recipe);
    }

    @GetMapping("/recipe/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return this.recipeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("No recipe with ID: %d", id)
        ));
    }

    @PatchMapping("/recipe/{id}")
    public Recipe patchReciepe(@PathVariable Long id, @RequestBody Map<String, Object> updateMap) {
        Recipe oldRecipe = this.recipeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("No recipe with ID: %d", id)
        ));

        updateMap.forEach((key, value) -> {
//            ObjectMapper mapper = new ObjectMapper();
//            List<Ingredient> updatedIngList;
//            List<Step> updatedStepList = new ArrayList<>();

            switch (key) {
                case "title":
                    oldRecipe.setTitle(String.valueOf(value));
                    break;
                case "picUrl":
                    oldRecipe.setPicUrl(String.valueOf(value));
                    break;
                case "description":
                    oldRecipe.setDescription(String.valueOf(value));
                    break;
                default:
                    throw new NoSuchElementException("Your switch did not work.");
            }
        });
        return this.recipeRepository.save(oldRecipe);
    }

    @PatchMapping("/recipe/{id}/ingredients")
    public Recipe patchIngredients(@PathVariable Long id, @RequestBody List<Ingredient> updatedList) {
        Recipe oldRecipe = this.recipeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("No recipe with ID: %d", id)
        ));

        updatedList.forEach((ingredient -> {
            AtomicBoolean alreadyExists = new AtomicBoolean(false);
            oldRecipe.getIngredientList().forEach((ingredient1 -> {
                if (ingredient == ingredient1)
                    alreadyExists.set(true);
            }));
            if (!alreadyExists.get())
                this.ingredientsRepository.save(ingredient);
        }));

        oldRecipe.setIngredientList(updatedList);
        return this.recipeRepository.save(oldRecipe);
    }

    @PatchMapping("/recipe/{id}/steps")
    public Recipe patchSteps(@PathVariable Long id, @RequestBody List<Step> updatedList) {
        Recipe oldRecipe = this.recipeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("No recipe with ID: %d", id)
        ));
        updatedList.forEach((step -> {
            AtomicBoolean alreadyExists = new AtomicBoolean(false);
            oldRecipe.getStepList().forEach((step1 -> {
                if (step == step1)
                    alreadyExists.set(true);
            }));
            if (!alreadyExists.get())
                this.stepRepository.save(step);
        }));

        oldRecipe.setStepList(updatedList);
        return this.recipeRepository.save(oldRecipe);
    }

    @DeleteMapping("/recipe/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        try {
            this.recipeRepository.deleteById(id);
        } catch (Exception e) {
            throw new NoSuchElementException(String.format("No recipe with ID: %d", id));
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleElementNotFound(Exception e) {
        return e.getMessage();
    }
}