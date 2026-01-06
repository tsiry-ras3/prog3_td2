
package org.tsiry;

import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {

        DataRetriever dataRetriever = new DataRetriever();
//        System.out.println(dataRetriever.findDishById(1));
//
//        System.out.println(dataRetriever.findDishById(999));
//
//        System.out.println(dataRetriever.findIngredients(3,5));

//        System.out.println(
//                dataRetriever.createIngredients(
//                        Arrays.asList(
//                                new Ingredient("Oignon12d", 1200.0, CategoryEnum.DAIRY),
//                                new Ingredient("Oignon2", 500.0,  CategoryEnum.VEGETABLE)
//                        )
//                )
//        );

        System.out.println(dataRetriever.findDishsByIngredientName("eur"));


//        System.out.println(dataRetriever.fin);?
    }
}
