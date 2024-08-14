package extras;

import java.util.Arrays;

public class Utilidades {
    public static char[] toArrayBinarie(int data, int size_bits){
        String binario = Integer.toBinaryString(data);
        char[] b = binario.toCharArray();
        char[] conversion = new char[size_bits];
        Arrays.fill(conversion, '0');
        if(b.length> size_bits){
            return null;
        }
        for(int i = 0; b.length>i; i++){
            conversion[size_bits-b.length+i-1] = b[i];
        }
        return conversion;
    }
    
    public static int toIntFromBinary(char[] binaryArray) {
        int result = 0;
        for (int i = 0; i < binaryArray.length; i++) {
            result <<= 1; // Desplaza el resultado 1 bit a la izquierda
            if (binaryArray[i] == '1') {
                result |= 1; // Si el bit actual es '1', añade 1 al bit menos significativo
            }
        }
        return result;
    }
    
    public static void addBitToArrayBinarie(char[] array){
        boolean s = true;
        for(int i= array.length -1; i>0; i--){
            if(array[i]=='1' && s){
                s = true;
                array[i] = '0';
            }else if(array[i]=='0' && s){
                array[i] = '1';
                s = false;
            }
        }
    }
    
    public static double log2(int N){
        double result = Math.log(N)/Math.log(2);
        return result;
    }
}
