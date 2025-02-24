import java.util.Scanner;

public class Main7 {
    public static void main(String[] args) {
        char ch1 = 'h';
        Scanner input = new Scanner(System.in);
        boolean game = false;
        while (!game) {
            char ch2 = input.next().charAt(0);
            if (ch2 == ch1) {
                System.out.println("Right!");
                game = true;
            } else if (ch2 < ch1) {
                System.out.println("You’re too low. Попробуйте ещё раз.");
            } else {
                System.out.println("You’re too high. Попробуйте ещё раз.");
            }
        }
    }
}