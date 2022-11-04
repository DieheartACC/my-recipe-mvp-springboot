package com.galvanize.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String unitType;
    private String units;

//    @ManyToMany(mappedBy = "ingredientList", fetch = FetchType.LAZY)
//    private List<Recipe> recipeList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

//    public List<Recipe> getRecipeList() {
//        return recipeList;
//    }
//
//    public void setRecipeList(List<Recipe> recipeList) {
//        this.recipeList = recipeList;
//    }
}