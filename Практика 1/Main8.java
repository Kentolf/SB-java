public class Main8 {
    public static void main(String[] args) {
        double a = 4.5;
        double b = 5.4;
        double c = -6.3;
        double discriminant = b * b - 4 * a * c;
        if (discriminant > 0) {
            double x1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            double x2 = (-b - Math.sqrt(discriminant)) / (2 * a);
            System.out.println("x1 = " + x1);
            System.out.println("x2 = " + x2);
        } else if (discriminant == 0) {
            double x = -b / (2 * a);
            System.out.println("x = " + x);
        } else {
            System.out.println("Уравнение не имеет вещественных корней.");
        }
    }
}