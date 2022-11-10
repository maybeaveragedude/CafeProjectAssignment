package placebo_cafe_apu;

import java.text.DecimalFormat;
import java.util.Calendar;

public class TextWrapper {
    static DecimalFormat dec = new DecimalFormat("00");

    public synchronized static void bigWrap(String informationString, String warningMessage) {
        Calendar time = Calendar.getInstance();
        String timeStamp = "[" + dec.format(time.get(Calendar.MINUTE)) + ":" +  dec.format(time.get(Calendar.SECOND)) + "] ";
        warningMessage = timeStamp + " " + warningMessage;
        int margin = 8;
        informationString = " " + informationString + " ";
        int msgLength = warningMessage.length() - informationString.length();
        ;
        System.out.println("\n");
        for (int n = 0; n < (msgLength / 2) + margin / 2; n++) {
            System.out.print("=");
        }
        System.out.print(informationString);
        for (int n = 0; n < (msgLength / 2) + margin / 2; n++) {
            System.out.print("=");
        }
        System.out.println("\n");
        for (int i = 0; i < margin / 2; i++) {
            System.out.print(" ");
        }

        System.out.println(warningMessage);
        System.out.println();

        for (int n = 0; n < msgLength + informationString.length() + margin; n++) {
            System.out.print("=");
        }
        System.out.println();

    }

    public synchronized static void compactWrap(String informationString, String warningMessage) {
        Calendar time = Calendar.getInstance();
        String timeStamp = "[" + dec.format(time.get(Calendar.MINUTE)) + ":" +  dec.format(time.get(Calendar.SECOND)) + "] ";
        informationString = timeStamp + " " + informationString + " ";

        int margin = 8;

        if (informationString.length() / 8 == 0) {
            System.out.print(informationString + "\t\t\t\t :");

        } else if (informationString.length() / 8 == 1) {
            System.out.print(informationString + "\t\t\t :");

        } else if (informationString.length() / 8 == 2) {
            System.out.print(informationString + "\t\t :");

        } else if (informationString.length() / 8 == 3) {
            System.out.print(informationString + "\t :");

        }

        for (int i = 0; i < margin / 2; i++) {
            System.out.print(" ");
        }
        System.out.println(warningMessage);

    }

    public static void main(String[] args) {
        compactWrap("ERROR", "This is a warning message");
        bigWrap("WARNING", "This is a warning message");
        bigWrap("WARNING", "This is a warning message");
        bigWrap("WARNING", "This is a warning message");
        compactWrap("ERROR", "This is a warning message");
        compactWrap("ERROR", "This is a warning message");

    }
}
