package SOFT2412.A2;
import java.io.*;
import java.util.*;

public class Seller extends User{

    public Seller(String name, String username, String password) {
        super(name, username, password);
    }

    //Edit item name
    public static void editItemName(String name, String newName) {
        UserInterface.vm.updateLine("./src/main/resources/inventory.txt", name, newName, 0);
        for (Food food : UserInterface.vm.getInventory().keySet()) {
            if (food.getName().equals(name)) {
                food.setName(newName);
            }
        }
    }

    //Edit item code
    public static void editItemCode(String code, String newCode) {
        UserInterface.vm.updateLine("./src/main/resources/inventory.txt", code, newCode, 2);
        for (Food food : UserInterface.vm.getInventory().keySet()) {
            if (food.getItemCode().equals(code)) {
                food.setItemCode(newCode);
            }
        }
    }

    //Edit item category
    public static void editItemCategory(String itemCode, String newCategory) {
        UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, newCategory, 1);
        for (Food food : UserInterface.vm.getInventory().keySet()) {
            if (food.getItemCode().equals(itemCode)) {
                food.setCategory(newCategory);
            }
        }
    }

    //Edit item price
    public static void editItemPrice(String itemCode, double newPrice) {
        for (Food food : UserInterface.vm.getInventory().keySet()) {
            if (food.getItemCode().equals(itemCode)) {
                food.setCost(newPrice);
            }
        }

        UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, Double.toString(newPrice), 3);
    }

    //Edit item quantity
    public static void editItemQuantity(String itemCode, int newQuantity) {
        if (newQuantity > 15) { 
            System.out.println("Error: Maximum quantity is 15.");
        } else if (newQuantity < 0) {
            System.out.println("Error: Please enter a valid quantity.");
        } else {
            UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, Integer.toString(newQuantity), 4);
            UserInterface.vm.loadInventory();
        }
    }

    public static String getSummary() {
        String summary = "";
        summary += "--------------------------------------------\n";
        summary += "|  Snack Name  | Item Code | Quantity Sold |\n";
        summary += "--------------------------------------------\n";
        try{
            File file = new File("./src/main/resources/quantities.txt");
            Scanner scan = new Scanner(file);

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] parts =  line.split(", ");
                String name = parts[0];
                String code = parts[1];
                String quantitySold = parts[2];
                summary += ("|" + UserInterface.foodDetailString(14, name)) +  UserInterface.foodDetailString(11, code) +
                    (UserInterface.foodDetailString(15,  quantitySold)  + "\n");
            }
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        summary += "--------------------------------------------\n";
        return summary;
    }
}
