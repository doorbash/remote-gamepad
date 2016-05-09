package ir.doorbash.gamepad;


public class InputConfig {

    public static int getButton(int i) {
        switch (i) {
            case 10:
                return 0b1;
            case 8:
                return 0b10;
            case 0:
                return 0b100;
            case 1:
                return 0b1000;
            case 12:
                return 0b10000;
            case 9:
                return 0b100000;
            case 13:
                return 0b1000000;
            case 11:
                return 0b10000000;
            case 2:
                return 0b100000000;
            case 3:
                return 0b1000000000;
            case 7:
                return 0b1000000000000;
            case 6:
                return 0b10000000000000;
            case 5:
                return 0b1000000000000000000;
            case 4:
                return 0b10000000000000000000;
        }
        return 0;
    }

}
