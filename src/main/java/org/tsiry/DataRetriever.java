package org.tsiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private DBConnection dbConnection = new DBConnection();

   public Dish findDishById(Integer id){
        String sql = "select d.id, d.name, d.dish_type, i.name as ingredient_name from Dish d join Ingredient i on " +
                "i.id_dish = d.id where d.id=?";
        Dish dish = null;

        try{
            Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            List<Ingredient> ingredients = new ArrayList<>();
            while(rs.next()){
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

                Ingredient i = new Ingredient();
                i.setName(rs.getString("ingredient_name"));
                ingredients.add(i);
                d.setIngredients(ingredients);
                dish = d;
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dish;
    }

    public List<Ingredient> findIngredients(int page, int size){
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "select i.id, i.name, i.price, i.category, d.id as dish_id, d.name as dish_name, d.dish_type from Ingredient i join Dish d " +
                "on i.id_dish = d.id limit ? offset ?";
        try{
            Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, page);
            pstmt.setInt(2, size);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                Dish dish = new Dish();
                dish.setId(rs.getInt("dish_id"));
                dish.setName(rs.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

                ingredient.setDish(dish);
            ingredients.add(ingredient);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }

//    public List<Ingredient> createIngredients(List<Ingredient> newIngredients){}
//
//    public Dish saveDish(Dish dishToSave){}
//
//    public List<Dish> findDishsByIngredientName(String ingredientName){}
//
//    public List<Ingredient> findngredientsByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size){}

}
