package FADD;

/**
 *
 * @author LECD
 */
public class SymConverter {

    public static String acentuadas(String oldString) {
        String nString = "";
        for (int i = 0; i < oldString.length(); i++) {
            switch (oldString.charAt(i)) {
                case 'á': nString += "&#225;"; break;
                case 'é': nString += "&#233;"; break;
                case 'í': nString += "&#237;"; break;
                case 'ó': nString += "&#243;"; break;
                case 'ú': nString += "&#250;"; break;
                default: nString += oldString.charAt(i); break;
            }
        }
        return nString;
    }
}
