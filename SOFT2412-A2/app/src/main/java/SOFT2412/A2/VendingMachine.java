package SOFT2412.A2;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
public class VendingMachine {

    private List<Customer> customers = new ArrayList<Customer>();
    private List<Card> cards = new ArrayList<Card>();

    // A hashmap of the form: foodType: quantity in the vending machine
    private Map<Food, Integer> inventory = new HashMap<>();

    // A hashmap that records all the cash in the form cashType: quantity
    private HashMap<String, Integer> cash = new HashMap<String, Integer>();

    public VendingMachine(){
        // Load in the cash and the inventory from "inventory.txt" and "cash.txt" files
        try{
            // load in the cash numbers from "cash.txt"
            File cashFile = new File("./src/main/resources/cash.txt");
            Scanner scan1 = new Scanner(cashFile);
            while (scan1.hasNextLine()){
                String[] line = scan1.nextLine().split(", ");
                cash.put(line[0], Integer.valueOf(line[1]));
            }

            // load in the inventory from "inventory.txt"
            File invenFile = new File("./src/main/resources/inventory.txt");
            Scanner scan2 = new Scanner(invenFile);
            while (scan2.hasNextLine()){
                String[] line = scan2.nextLine().split(", ");
                inventory.put(new Food(line[0], line[1], line[2], Double.parseDouble(line[3])), Integer.valueOf(line[4]));
            }

        }
        catch(FileNotFoundException f){System.out.println(f);}
    }

    // I'm following Frank's structure that user input will be of the form:
    // buyer cash 3 mw 50c*3 $5*3
    // i.e. userType paymentType quantity itemCode givenMoney
    // GivenMoney can have a variable length so it's simply all the inputs after the itemCode
    public String payByCash(int quantity, String itemCode, String givenMoney){
        Food toBuy = searchByItemCode(itemCode);
        double toPay = toBuy.getCost() * quantity;
        String[] givenCash = givenMoney.split(" ");
        double paid = 0;

        // Go through every cash item and calculate how much money has been given
        for (String typeOfCash: givenCash){
            try {
                String[] thisCash = typeOfCash.split("\\*");

                if (!cash.keySet().contains(thisCash[0])) {
                    System.out.println(thisCash[0]);
                    return "incorrect format";
                }
                // Check if the input type given is a dollar (starts with $)
                if (Character.toString(thisCash[0].charAt(0)).equals("$")) {
                    paid += (Double.parseDouble(thisCash[0].substring(1)) * Double.parseDouble(thisCash[1]));
                }
                // Otherwise it's cents
                else {
                    paid += ((Double.parseDouble(thisCash[0].substring(0, thisCash[0].length() - 1)) / 100) * Double.parseDouble(thisCash[1]));
                }

            }
            catch(NumberFormatException f){ return "incorrect format";}

        }

        // Check that they've given enough
        if (toPay > paid && Math.abs(toPay - paid) >= 0.00001){
            return "not enough paid";
        }

        DecimalFormat df = new DecimalFormat("0.00");
        BigDecimal change = new BigDecimal(paid - toPay);

        // Calculate change breakdown
        Map<String, Integer> changeCash = new LinkedHashMap<String, Integer>();
        List<String> cashTypes = new ArrayList<>(){{
           add("$100");
           add("$50");
           add("$20");
           add("$10");
           add("$5");
           add("$2");
           add("$1");
           add("50c");
           add("20c");
           add("10c");
           add("5c");
        }};
        BigDecimal changeNum = change;
        // This is a disgusting line but basically it's giving change to the customer while there is still change to be given (change > 0)
        while (change.subtract(new BigDecimal(0.0001)).compareTo(new BigDecimal(0)) == 1){

            // Check every cash value to try to add a coin or note to the change
            for (String cashType: cashTypes){
                BigDecimal value;

                // Check how much this cash type is worth according to whether it's a dollar or cent
                if (Character.toString(cashType.charAt(0)).equals("$")){
                    value = new BigDecimal(cashType.substring(1));
                }
                else{
                    value = BigDecimal.valueOf(Double.parseDouble(cashType.substring(0, cashType.length() - 1)) / 100);
                }

                // round to the nearest 5 cents
                change = round(change, new BigDecimal("0.05"), RoundingMode.HALF_UP);
                // Try to add it to the list of change as many times as you can
                while (change.compareTo(value) >= 0 && cash.get(cashType) > 0){
                    change = change.subtract(value);

                    if (!changeCash.containsKey(cashType)){
                        changeCash.put(cashType, 1);
                    }
                    else{
                        changeCash.put(cashType, changeCash.get(cashType) + 1);
                    }

                }
            }

        }
        String changeBreakdown = "";
        for (Map.Entry<String, Integer> payment: changeCash.entrySet()){
            changeBreakdown += (" (" + payment.getKey() + "*" + payment.getValue() + ")");
        }
        return "Transaction successful!\n" + "Paid: $" + df.format(paid) + "\nDue: $" + df.format(toPay) + "\nChange: $" + df.format(changeNum)
                + "\n\nChange Breakdown: \n" + changeBreakdown;

    }

    public Food searchByItemCode(String itemCode){
        for (Food f: inventory.keySet()){
            if (f.getItemCode().equals(itemCode)){
                return f;
            }
        }
        return null;
    }

    public static BigDecimal round(BigDecimal value, BigDecimal increment, RoundingMode roundingMode) {
        if (increment.signum() == 0) {
            // 0 increment does not make much sense, but prevent division by 0
            return value;
        } else {
            BigDecimal divided = value.divide(increment, 0, roundingMode);
            BigDecimal result = divided.multiply(increment);
            return result;
        }
    }

}