public class Main {
    public static void main(String[] args) {
        for (int i = 0; i <= 10; i++) {
            System.out.println("Factorielle de " + i + " est: " + factorielle(i));
        }
    }

    public static long factorielle(int n) {
        if (n == 0) {
            return 1;
        }
        long resultat = 1;
        for (int i = 1; i <= n; i++) {
            resultat *= i;
        }
        return resultat;
    }
}
