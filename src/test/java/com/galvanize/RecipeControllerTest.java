package com.galvanize;

import com.galvanize.entities.Ingredient;
import com.galvanize.entities.Recipe;
import com.galvanize.entities.Step;
import com.galvanize.repositories.IngredientsRepository;
import com.galvanize.repositories.RecipeRepository;
import com.galvanize.repositories.StepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    IngredientsRepository ingredientsRepository;
    @Autowired
    StepRepository stepRepository;

    Recipe recipe1;
    Ingredient ingredient1;
    Step step1;

    @BeforeEach
    public void init() {
        recipe1 = new Recipe();
        ingredient1 = new Ingredient();
        step1 = new Step();

        recipe1.setTitle("Hard Boiled Eggs");

        ingredient1.setName("Egg");
        ingredient1.setUnitType("each");
        ingredient1.setUnits("1");

        List<Step> addList = new ArrayList<>();
        addList.add(step1);

        List<Ingredient> inAddList = new ArrayList<>();
        inAddList.add(ingredient1);

        List<Recipe> recAddList = new ArrayList<>();
        recAddList.add(recipe1);

        recipe1.setStepList(addList);
        recipe1.setIngredientList(inAddList);


        step1.setInstructions("Bring water to boil and insert eggs.  Boil for ten minutes");
    }

    @Test
    @Transactional
    @Rollback
    public void getRecipesGetsAllRecipes() throws Exception {
        this.mvc.perform(get("/recipe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        recipeRepository.save(recipe1);
        ingredientsRepository.save(ingredient1);
        stepRepository.save(step1);

        this.mvc.perform(get("/recipe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].title").value("Hard Boiled Eggs"))
                .andExpect(jsonPath("$[0].ingredientList[0].name").value("Egg"))
                .andExpect(jsonPath("$[0].stepList[0].id").isNumber());
    }

    @Test
    @Transactional
    @Rollback
    public void postRecipeSavesRecipe() throws Exception {
        MockHttpServletRequestBuilder request = post("/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "title": "Hard Boiled Eggs",
                        "ingredientList": [{"id":1,"name":"Egg","unitType":"each","units":"1"],
                        "stepList": [{"id":1,"instructions":"Bring water to boil and insert eggs.  Boil for ten minutes"}]
                        }
                        """);

        this.mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Hard Boiled Eggs"))
                .andExpect(jsonPath("$.ingredientList[0].name").value("Egg"))
                .andExpect(jsonPath("$.stepList[0].id").isNumber());
    }

    @Test
    @Transactional
    @Rollback
    public void getRecipeByIdGetsCorrectRecipe() throws Exception {
        recipeRepository.save(recipe1);
        ingredientsRepository.save(ingredient1);
        stepRepository.save(step1);

        String url = String.format("/recipe/%d", recipe1.getId());

        this.mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Hard Boiled Eggs"))
                .andExpect(jsonPath("$.ingredientList[0].name").value("Egg"))
                .andExpect(jsonPath("$.stepList[0].id").isNumber());

        this.mvc.perform(get("/recipe/-1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No recipe with ID: -1"));
    }

    @Test
    @Transactional
    @Rollback
    public void updateRecipeUpdatesRecipe() throws Exception {
        recipeRepository.save(recipe1);
        ingredientsRepository.save(ingredient1);
        stepRepository.save(step1);

        String url = String.format("/recipe/%d", recipe1.getId());

        MockHttpServletRequestBuilder request = patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "title": "Soft Boiled Eggs"
                        }
                        """);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Soft Boiled Eggs"))
                .andExpect(jsonPath("$.ingredientList[0].name").value("Egg"))
                .andExpect(jsonPath("$.stepList[0].id").isNumber());

        request = patch("/recipe/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "title": "Soft Boiled Eggs"
                        }
                        """);

        this.mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string("No recipe with ID: -1"));
    }

    @Test
    @Transactional
    @Rollback
    public void updateIngredientListUpdatesList() throws Exception {
        recipeRepository.save(recipe1);
        ingredientsRepository.save(ingredient1);
        stepRepository.save(step1);

        String url = String.format("/recipe/%d/ingredients", recipe1.getId());

        MockHttpServletRequestBuilder request = patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        [{
                        "name":"Bacon",
                        "unitType":"each",
                        "units":"1"
                        }]
                        """);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Hard Boiled Eggs"))
                .andExpect(jsonPath("$.ingredientList[0].name").value("Bacon"))
                .andExpect(jsonPath("$.stepList[0].id").isNumber());

        request = patch("/recipe/-1/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        [{
                        "name":"Bacon",
                        "unitType":"each",
                        "units":"1"
                        }]
                        """);

        this.mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string("No recipe with ID: -1"));
    }

    @Test
    @Transactional
    @Rollback
    public void patchStepsUpdatesList() throws Exception {
        recipeRepository.save(recipe1);
        ingredientsRepository.save(ingredient1);
        stepRepository.save(step1);

        String url = String.format("/recipe/%d/steps", recipe1.getId());

        MockHttpServletRequestBuilder request = patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        [{
                        "instructions":"Blah test"
                        }]
                        """);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Hard Boiled Eggs"))
                .andExpect(jsonPath("$.ingredientList[0].name").value("Egg"))
                .andExpect(jsonPath("$.stepList[0].id").isNumber())
                .andExpect(jsonPath("$.stepList[0].instructions").value("Blah test"));

        request = patch("/recipe/-1/steps")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        [{
                        "instructions":"Blah test"
                        }]
                        """);

        this.mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string("No recipe with ID: -1"));
    }

    @Test
    @Transactional
    @Rollback
    public void deleteRecipeDeletesRecipe() throws Exception {
        recipeRepository.save(recipe1);
        ingredientsRepository.save(ingredient1);
        stepRepository.save(step1);

        String url = String.format("/recipe/%d", recipe1.getId());

        assertEquals(this.recipeRepository.count(), 1);

        this.mvc.perform(delete(url))
                .andExpect(status().isOk());
        assertEquals(this.recipeRepository.count(), 0);

        this.mvc.perform(delete("/recipe/-1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No recipe with ID: -1"));
    }
}

// {
//         "title": "Cream Cheese Icing",
//         "picUrl": "https://www.allrecipes.com/thmb/t9_6LTUjys0H8o6ZRVrxDglw20I=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/Basic-Cream-Cheese-Frosting-19b8a0437ced4e69925667be2f239f88.jpg",
//         "description": "Perfect Hard Boiled Eggs for any occasion.",
//         "ingredientList": [
//         {
//         "name": "Stick Butter",
//         "unitType": "stick",
//         "units": "2"
//         },
//         {
//         "name": "Cream Cheese",
//         "unitType": "ounce",
//         "units": "8"
//         },
//         {
//         "name": "Vanilla",
//         "unitType": "tsp",
//         "units": "2 1/2"
//         },
//         {
//         "name": "Powdered Sugar",
//         "unitType": "cups",
//         "units": "4 - 4 1/2"
//         }
//         ],
//         "stepList": [
//         {
//         "instructions": "Soften both butter and cream cheese."
//         },
//         {
//         "instructions": "Beat butter till creamy."
//         },
//         {
//         "instructions": "Add cream cheese and vanilla, mix well. Gradually increase speed to high
//         and beat till light and fluffy."
//         },
//         {
//         "instructions": "Gradually add sugar, mixing on low till combined and desired consistency
//         is reached."
//         },
//         {
//         "instructions": "Mix on high till light and fluffy."
//         },
//         ]
//         }