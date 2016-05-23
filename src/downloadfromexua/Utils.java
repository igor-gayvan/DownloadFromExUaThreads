/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadfromexua;

/**
 *
 * @author Igor Gayvan
 */
public class Utils {

    public static final long COUNT_BYTES_IN_MEGABYTE = 1024 * 1024;

    public static String padl(String in, int size, char padChar) {                
    if (in.length() <= size) {
        char[] temp = new char[size];
        /* Llenado Array con el padChar*/
        for(int i =0;i<size;i++){
            temp[i]= padChar;
        }
        int posIniTemp = size-in.length();
        for(int i=0;i<in.length();i++){
            temp[posIniTemp]=in.charAt(i);
            posIniTemp++;
        }            
        return new String(temp);
    }
    return "";
}
}
