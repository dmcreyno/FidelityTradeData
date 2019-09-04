public class ScratchPad {

    public static void main(String[] a) {
        Integer price = 2;
        Integer ask = 3;
        int r1 = price.compareTo(ask); // if n1 <= n2 then result is <= 0
        System.out.println(r1);
    }

    /*
    3.compareTo 2 = 1
    2.compareTo(3) = -1
     */
}
