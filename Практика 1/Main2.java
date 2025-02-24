import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String str = input.nextLine();
        boolean isPalindrome = isPalindrome(str);
        System.out.println(isPalindrome);
    }

    public static boolean isPalindrome(String str) {

        int left = 0;
        int right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}