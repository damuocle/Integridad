import java.util.*;
import java.io.*;
import java.util.zip.CRC32;


public class Yuval{
  public static void main (String [] args){
        int m=32; //numero de bits de la funcion hash 
        int t = (int) Math.pow(2,m/2); //Numero de modificaciones a realizar de los mensajes
        String [][]xl_matriz = new String[][2];
        String [][]xi_matriz = new String[][2];

        String ml="Mensaje legítimo original"; //Mensaje legítimo original
        String mi="Mensaje ilegítimo original"; //Mensaje ilegítimo original
        
        //Hacer división de bloques de los mensajes legítimos e ilegítmos con las modificaciones

        LinkedHashMap <String,Long> xl_hxl = new LinkedHashMap <String,Long>(t);

        int [] aux_modificaciones = new int [m/2];
        String xl_prima = "";
        long hxl_prima;
        String xi_prima = "";
        long hxi_prima;
        byte[] hashes;
        CRC32 crc32 = new CRC32();

        for (int i=0;i<t;i++){
          aux_modificaciones=generaModificaciones(m/2,i);
          xl_prima=matriztoString(aux_modificaciones,xl_matriz);
          crc32.update(xl_prima.getBytes());
          xl_hxl.put(xl_prima, crc32.getValue());
               //System.out.println("Mensaje >>>"+i)}
        }



        boolean colision=false;
        int iteracion=0;

        while(!colision){
          aux_modificaciones=generaModificaciones(m/2,iteracion);
          xi_prima=matriztoString(aux_modificaciones,xi_matriz);
          crc32.update(xi_prima.getBytes());
          hxi_prima=crc32.getValue();
          System.out.println("ITERACION>>>"+iteracion);
          if(xl_hxl.containsValue(hxi_prima)){
            System.out.println("COLISION ENCONTRADA!!!");
            colision=true;
          }else{
            iteracion++;
          }

        }
        System.out.println("Mensaje legítimo modificado: ");
        System.out.println();
        for (Map.Entry<String,Long> entrada : xl_hxl.entrySet()){
          if(entrada.getValue()==crc32.getValue()){
            System.out.println(entrada.getKey());
          }
          
        }

        System.out.println("=============================");
        System.out.println("Mensaje ilegítimo modificado: ");
        System.out.println();
        System.out.println(xi_prima);

           /*Collection<byte[]> hashes = xl_hxl.values();
           System.out.println(hashes.size());*/

    }//fin Main

    public static int[] intToBinaryArray(int numero, int longitud) {
      int[] binario = new int[longitud];

      for (int i = longitud - 1; i >= 0; i--) {
        binario[i] = (numero & 1);
        numero >>= 1;
      }
      int start = 0;
      int end = binario.length - 1;

      while (start < end){
        int temp = binario[start];
        binario[start] = binario[end];
        binario[end] = temp;

        start++;
        end--;
      }
      return binario;
    }

    public static int[] generaModificaciones(int tamanyo, int numero){
      int [] resultado = new int [tamanyo];
      for (int i = tamanyo - 1; i >= 0; i--) {
        resultado[i] = (int) (numero >> i) &  1;
      }
      return resultado;
    } //fin generaModificaciones

    public static String matriztoString (int [] modificaciones, String [][] matrizmensajes){
      String resultado ="";
      int [] auxiliar = new int[matrizmensajes.length];
      for (int i=0;i<auxiliar.length;i++){
        auxiliar[i]=0;
      }

      for(int i=0;i<modificaciones.length;i++){
        auxiliar[i]=modificaciones[i];
      }

      for (int i=0;i<matrizmensajes.length;i++){
        resultado = resultado + matrizmensajes[i][auxiliar[i]];
      }
      return resultado;

    } //fin matriztoString


  }