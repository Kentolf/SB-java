import java.util.Scanner;
public class Main9 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int number1 = input.nextInt();
        String number2 = convertToRoman(number1);
        System.out.println(number2);
    }
    public static String convertToRoman(int number) {
        int[] decimalValues = {100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanSymbols = {"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder romanNumber = new StringBuilder();
        for (int i = 0; i < decimalValues.length; i++) {
            while (number >= decimalValues[i]) {
                romanNumber.append(romanSymbols[i]);
                number -= decimalValues[i];
            }
        }
        return romanNumber.toString();
    }
}