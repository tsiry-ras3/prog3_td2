package org.tsiry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

public class DataRetrieverTest {
    private DataRetriever dataRetriever;

    @BeforeEach
    void setUp() {
        dataRetriever = new DataRetriever();
    }

    @Test
    void testFindDishByIdNotFound() {
        assertThrows(RuntimeException.class, () -> {
            dataRetriever.findDishById(999);
        });
    }

    @Test
    void testFindIngredientsPage2Size2() {
        List<Ingredient> ingredients = dataRetriever.findIngredients(2, 2);
        assertEquals(2, ingredients.size());
        assertEquals("Poulet", ingredients.get(0).getName());
        assertEquals("Chocolat", ingredients.get(1).getName());
    }

    @Test
    void testFindIngredientsPage3Size5() {
        List<Ingredient> ingredients = dataRetriever.findIngredients(3, 5);
        assertTrue(ingredients.isEmpty());
    }

    @Test
    void testFindDishsByIngredientNameEur() {
        List<Dish> dishes = dataRetriever.findDishsByIngredientName("eur");
        assertFalse(dishes.isEmpty());
        assertEquals("Gâteau au chocolat", dishes.get(0).getName());
    }

    @Test
    void testFindIngredientsByCriteriaVegetable() {
        List<Ingredient> ingredients = dataRetriever.findIngredientsByCriteria(
                null,
                CategoryEnum.VEGETABLE,
                null,
                1,
                10
        );
        assertEquals(2, ingredients.size());
        List<String> names = ingredients.stream()
                .map(Ingredient::getName)
                .toList();
        assertTrue(names.contains("Laitue"));
        assertTrue(names.contains("Tomate"));
    }

    @Test
    void testFindIngredientsByCriteriaChoSalEmpty() {
        List<Ingredient> ingredients = dataRetriever.findIngredientsByCriteria(
                "cho",
                null,
                "Sal",
                1,
                10
        );
        assertTrue(ingredients.isEmpty());
    }

    @Test
    void testFindIngredientsByCriteriaChoGateau() {
        List<Ingredient> ingredients = dataRetriever.findIngredientsByCriteria(
                "cho",
                null,
                "teau",
                1,
                10
        );
        assertEquals(1, ingredients.size());
        assertEquals("Chocolat", ingredients.get(0).getName());
    }

    @Test
    void testCreateIngredientsFromageOignon() {
        List<Ingredient> newIngredients = Arrays.asList(
                new Ingredient("Fromage", 1200.0, CategoryEnum.DAIRY),
                new Ingredient("Oignon", 500.0, CategoryEnum.VEGETABLE)
        );
        List<Ingredient> created = dataRetriever.createIngredients(newIngredients);
        assertEquals(2, created.size());
        assertTrue(created.stream().anyMatch(i -> i.getName().equals("Fromage")));
        assertTrue(created.stream().anyMatch(i -> i.getName().equals("Oignon")));
        for (Ingredient ing : created) {
            assertTrue(ing.getId() > 0);
        }
    }

    @Test
    void testCreateIngredientsLaitueExistante() {
        List<Ingredient> newIngredients = Arrays.asList(
                new Ingredient("Carotte", 2000.0, CategoryEnum.VEGETABLE),
                new Ingredient("Laitue", 2000.0, CategoryEnum.VEGETABLE)
        );
        assertThrows(RuntimeException.class, () -> {
            dataRetriever.createIngredients(newIngredients);
        });
    }

    @Test
    void testSaveDishCreateSoupeLegumes() {
        Dish newDish = new Dish();
        newDish.setName("Soupe de légumes");
        newDish.setDishType(DishTypeEnum.START);
        Ingredient oignon = new Ingredient();
        oignon.setId(findIngredientId("Oignon"));
        newDish.setIngredients(Arrays.asList(oignon));
        Dish saved = dataRetriever.saveDish(newDish);
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);
        assertEquals("Soupe de légumes", saved.getName());
    }

    @Test
    void testSaveDishUpdateSaladeAddIngredients() {
        Dish dish = new Dish();
        dish.setId(1);
        dish.setName("Salade fraîche");
        dish.setDishType(DishTypeEnum.START);
        dish.setIngredients(Arrays.asList(
                createIngredientWithId("Laitue"),
                createIngredientWithId("Tomate"),
                createIngredientWithId("Oignon"),
                createIngredientWithId("Fromage")
        ));
        Dish updated = dataRetriever.saveDish(dish);
        assertEquals(1, updated.getId());
        assertEquals("Salade fraîche", updated.getName());
    }

    @Test
    void testSaveDishUpdateSaladeRemoveIngredients() {
        Dish dish = new Dish();
        dish.setId(1);
        dish.setName("Salade de fromage");
        dish.setDishType(DishTypeEnum.START);
        dish.setIngredients(Arrays.asList(createIngredientWithId("Fromage")));
        Dish updated = dataRetriever.saveDish(dish);
        assertEquals(1, updated.getId());
        assertEquals("Salade de fromage", updated.getName());
    }

    private int findIngredientId(String name) {
        List<Ingredient> ingredients = dataRetriever.findIngredientsByCriteria(
                name, null, null, 1, 1
        );
        if (!ingredients.isEmpty()) {
            return ingredients.get(0).getId();
        }
        throw new RuntimeException();
    }

    private Ingredient createIngredientWithId(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(findIngredientId(name));
        return ingredient;
    }
}