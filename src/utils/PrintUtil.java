package utils;

public class PrintUtil {

    public static void println(String string) {
        System.out.println(string);
    }

    public static void printf(String formatter, Object... objects) {
        System.out.printf(formatter + "\n", objects);
    }

}
