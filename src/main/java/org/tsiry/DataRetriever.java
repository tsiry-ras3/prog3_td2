package org.tsiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataRetriever {
    private DBConnection dbConnection = new DBConnection();

    Dish findDishById(Integer id){
        String sql = "select id, name, dish_type from Dish where id=?";
        Dish dish = null;

        try{
            Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
//                d.setIngredients(rs.getArray(""));

                dish = d;
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dish;
    }


}
