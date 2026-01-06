package org.tsiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataRetriever {
    private DBConnection dbConnection = new DBConnection();

    public Dish findDishById(Integer id) {
        String sql = "select d.id, d.name, d.dish_type, i.name as ingredient_name from Dish d join Ingredient i on " +
                "i.id_dish = d.id where d.id=?";
        Dish dish = null;

        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            List<Ingredient> ingredients = new ArrayList<>();
            while (rs.next()) {
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

    public List<Ingredient> findIngredients(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "select i.id, i.name, i.price, i.category, d.id as dish_id, d.name as dish_name, d.dish_type from Ingredient i join Dish d " +
                "on i.id_dish = d.id limit ? offset ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, page);
            pstmt.setInt(2, size);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
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

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        Set<String> names = new HashSet<>();
        for (Ingredient ingredient : newIngredients) {
            if (!names.add(ingredient.getName())) {
                throw new RuntimeException("Doublon dans la liste d'ingredient : " + ingredient.getName());
            }
        }

        List<Ingredient> created = new ArrayList<>();

        Connection conn = null;
        PreparedStatement check = null;
        PreparedStatement insert = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "select name from Ingredient where name ilike ?";

            check = conn.prepareStatement(checkSql);

            for (Ingredient ingredient : newIngredients) {
                check.setString(1, ingredient.getName());
                rs = check.executeQuery();


                if (rs.next()) {
                    conn.rollback();
                    throw new RuntimeException(
                            "Ingredient deja existant: " + ingredient.getName()
                    );
                }

                rs.close();
                rs = null;
            }

            String insertSql = "insert into Ingredient (id, name, price, category) values (?, ?, ?, ?::" +
                    "ingredient_category_enum) returning id, name, price, category";

            insert = conn.prepareStatement(insertSql);
            int nextId = getMaxIngredientId() + 1;
            for (Ingredient ingredient : newIngredients) {

                insert.setInt(1, nextId);
                insert.setString(2, ingredient.getName());
                insert.setDouble(3, ingredient.getPrice());
                insert.setString(4, ingredient.getCategoryName());

                rs = insert.executeQuery();
                if (rs.next()) {
                    Ingredient createdIngredient = new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            CategoryEnum.valueOf(rs.getString("category"))
                    );

                    created.add(createdIngredient);
                }
                nextId++;

                rs.close();
                rs = null;
            }

            conn.commit();
            return created;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (check != null) check.close();
                if (insert != null) insert.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    //
//    public Dish saveDish(Dish dishToSave) {
//        String checkDishSql = "select id, name from Dish where name ilike ?";
//        String dishSql = null;
//        String updateIngredientOfDishSql = "update Ingredient set id_dish = ? where name ilike ?";
//        Connection conn = null;
//        ResultSet rs = null;
//        boolean dishExists = false;
//        int idDishExists = 0;
//        try {
//            conn = dbConnection.getConnection();
//            PreparedStatement checkDishStmt = conn.prepareStatement(checkDishSql);
//            checkDishStmt.setString(1, dishToSave.getName());
//            rs = checkDishStmt.executeQuery();
//            dishExists = rs.next();
//            idDishExists = rs.getInt("id");
//            rs.close();
//
//
//            if (dishExists) {
//                dishSql = "update Dish " +
//                        "set dish_type = ?::dish_type_enum;";
//            } else {
//                dishSql = "insert into Dish (id, name, dish_type) values (?, ?, ?, ?::dish_type_enum)" +
//                        "returning id, name, dish_type;";
//            }
//
//            PreparedStatement statement = conn.prepareStatement(dishSql);
//            int nextIdDish = getMaxDishId() + 1;
//            if (!dishExists) {
//
//                statement.setInt(1, nextIdDish);
//                statement.setString(2, dishToSave.getName());
//                statement.setString(3, dishToSave.getDishType().name());
//            } else {
//                statement.setInt(1, idDishExists);
//            }
//
//            rs = statement.executeQuery();
//            if (rs.next()) {
//                dishToSave.setId(rs.getInt("id"));
//            }
//
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
public List<Dish> findDishsByIngredientName(String ingredientName) {
    List<Dish> dishes = new ArrayList<>();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = dbConnection.getConnection();

        String sql = "select d.id, d.name, d.dish_type " +
                "from dish d " +
                "join ingredient i on d.id = i.id_dish " +
                "where i.name ilike ? " +
                "group by d.id, d.name, d.dish_type " +
                "order by d.name";

        stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + ingredientName + "%");

        rs = stmt.executeQuery();

        while (rs.next()) {
            Dish dish = new Dish(
                    rs.getInt("id"),
                    rs.getString("name"),
                    DishTypeEnum.valueOf(rs.getString("dish_type"))
            );
            dishes.add(dish);
        }

        return dishes;

    } catch (SQLException e) {
        throw new RuntimeException("Erreur recherche plats: " + e.getMessage(), e);
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

public List<Ingredient> findIngredientsByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size) {
    List<Ingredient> ingredients = new ArrayList<>();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = dbConnection.getConnection();

        StringBuilder sql = new StringBuilder();
        sql.append("select i.id, i.name, i.price, i.category, i.id_dish " +
                "from ingredient i " +
                "left join dish d on i.id_dish = d.id " +
                "where 1=1 ");

        List<Object> params = new ArrayList<>();

        if (ingredientName != null && !ingredientName.trim().isEmpty()) {
            sql.append("and i.name ilike ? ");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append("and i.category = ?::ingredient_category_enum ");
            params.add(category.toString());
        }

        if (dishName != null && !dishName.trim().isEmpty()) {
            sql.append("and d.name ilike ? ");
            params.add("%" + dishName + "%");
        }

        sql.append("order by i.id ");
        sql.append("limit ? offset ?");

        stmt = conn.prepareStatement(sql.toString());

        int paramIndex = 1;
        for (Object param : params) {
            stmt.setObject(paramIndex++, param);
        }

        stmt.setInt(paramIndex++, size);
        stmt.setInt(paramIndex, (page - 1) * size);

        rs = stmt.executeQuery();

        while (rs.next()) {
            Ingredient ingredient = new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category"))
            );

            if (rs.getObject("id_dish") != null) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id_dish"));
                ingredient.setDish(dish);
            }

            ingredients.add(ingredient);
        }

        return ingredients;

    } catch (SQLException e) {
        throw new RuntimeException("erreur recherche ingredients: " + e.getMessage(), e);
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


    public int getMaxIngredientId() {
        int maxId = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "select max(id) as max_id from Ingredient group by id";
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("max_id") : maxId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMaxDishId() {
        String sql = "select max(id) as max_id from dish group by id";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("max_id");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
