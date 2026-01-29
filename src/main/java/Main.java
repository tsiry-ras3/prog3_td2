import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
        Dish saladeVerte = dataRetriever.findDishById(1);
//        System.out.println(saladeVerte);
//
//        Dish poulet = dataRetriever.findDishById(2);
//        System.out.println(poulet);
//
//        Dish rizLegume = dataRetriever.findDishById(3);
//        rizLegume.setPrice(100.0);
//        Dish newRizLegume = dataRetriever.saveDish(rizLegume);
//        System.out.println(newRizLegume); // Should not throw exception


//        Dish rizLegumeAgain = dataRetriever.findDishById(3);
//        rizLegumeAgain.setPrice(null);
//        Dish savedNewRizLegume = dataRetriever.saveDish(rizLegume);
//        System.out.println(savedNewRizLegume); // Should throw exception

//        Ingredient laitue = dataRetriever.findIngredientById(1);
//        System.out.println(laitue);

//        System.out.println(System.getProperty("file.encoding"));
//        System.out.println("fraîche – gâteau – légumes");

        // Test 2: Modifier une commande non payée
        Dish dish = dataRetriever.findDishById(1); // ⚠️ ID EXISTANT
        System.out.println("Dish trouvé : " + dish);

        // 2️⃣ Créer DishOrder
        DishOrder dishOrder = new DishOrder();
        dishOrder.setDish(dish);
        dishOrder.setQuantity(2); // le client commande 2 plats
        Order order = new Order();
        order.setReference("CMD-001");
        order.setCreationDatetime(Instant.now());
        order.setDishOrderList(List.of(dishOrder));
        order.setPayementStatus(PayementStatusEnum.PAID);
//        System.out.println(dataRetriever.findOrderByReference( "CMD-001"));
        System.out.println(dataRetriever.saveOrder(order));



    }
}
