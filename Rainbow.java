import java.io.*;
import java.util.*;
import java.util.zip.CRC32;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Rainbow{
    public static void main (String [] args){
        //Implementación base de ataque arcoiris por medio de tablas rainbow
        //El espacio de claves que se ha elegido es caracterees minuscula de la "a" a la "z"
        int contador=0;
        int n = 456976; //numero de entradas de la tabla rainbow (200 passwords a analizar)
        int t = 3; //numero de columnas de la tabla rainbow
        int tamanyo = 4;
        
        byte[] hash_aux;
        byte[] hash_aux2;
        String pass_aux; //cadena de texto auxiliar para generar pass
        String pass_aux2="";
        String combination="";
        String [] tabla = new String [n];
        List<String> passwords = new ArrayList<>();
        LinkedHashMap <String,Long> rt = new LinkedHashMap <String,Long>();

        //CONSTRUCCION DE LA TABLA
        //relleno primera columna de la tabla
        //tabla[0][0]="aa";
     
            for (char a = 'a'; a <= 'z'; a++) {
                for (char b = 'a'; b <= 'z'; b++) {
                    for (char c = 'a'; c <= 'z'; c++) {
                        for (char d = 'a'; d <= 'z'; d++) {
                            //System.out.println(i);
                            combination = String.valueOf(a) + String.valueOf(b) + String.valueOf(c) + String.valueOf(d);
                            passwords.add(combination);
                        }
                    }
                }
            }
       

       
        for(String clave:passwords){
            if (contador == n){
                break;
            }else{
                tabla[contador]=clave;
                contador++;
            }
        }
    
       
        

     
        for (int i=0;i<n;i++){
            pass_aux="";
            pass_aux=tabla[i];
            for (int j=1;j<t;j++){
                //pass_aux=reconstruccion(generaHash(tabla[i],tamanyo));
                hash_aux=(generaHash(pass_aux,tamanyo));
                pass_aux2=reconstruccion(hash_aux);
                pass_aux=pass_aux2;
                if(j==t-1){
                    //hash_aux2=generaHash(pass_aux,tamanyo);
                    rt.put(tabla[i],bytesToLong(hash_aux));
                }
            }
        }
       
        System.out.println("TAMAÑO >>>" +rt.size());

        //******************BANCO DE PRUEBAS***********/
        int numerocolisiones = 0;
        int numeropruebas = 1000;
        int iteracion=0;
        String p="";
        String pwd="";
        String [] pruebas = new String [numeropruebas];
        boolean colision =false;
        long hp0_long;
        long hash_aux_p;
        for (int i=0;i<pruebas.length;i++){
            //pruebas[i]=generaPassword(tamanyo);
            pruebas[i]=generaPassword(tamanyo);
        }

        while(iteracion<numeropruebas){
            System.out.println("PRUEBA "+iteracion);
            String p0=pruebas[iteracion]; //primera contraseña
            
            byte[] hp0=generaHash(p0,tamanyo);
            for (int i=0;i<t;i++){
                if(i==t-1){
                    System.out.println("La contraseña "+pruebas[iteracion] +" NO ES VALIDA");;
                    break;
                }else{
                    if(rt.containsValue(bytesToLong(hp0))){
                        System.out.println("COLISION!");
                        numerocolisiones++;
                        colision = true;
                        break;
                    }else{
                        //System.out.println("NADA!");
                        //hp0=generaHash(reconstruccion(hp0),tamanyo);
                    }
                }
            }//fin for

            

            if (colision){
                for (Map.Entry<String,Long> entrada : rt.entrySet()){
                    if(entrada.getValue()==bytesToLong(hp0)){
                        p=entrada.getKey(); //obtengo el password de la primera columna
                    }
                }
                
                //pwd=p;
                hp0_long=bytesToLong(hp0);
                hash_aux_p=bytesToLong(generaHash(p,tamanyo));
                
                if (hp0_long == hash_aux_p){
                    pwd=p;
                    System.out.println("CONTRASEÑA OBTENIDA  "+pwd);
                    iteracion++;
                    colision=false;
                }else{
                    do{
                        p=reconstruccion(generaHash(p,tamanyo));
                        hash_aux_p=bytesToLong(generaHash(p,tamanyo));
                    }while(hp0_long!=hash_aux_p);
                    pwd=p;
                    System.out.println("CONTRASEÑA OBTENIDA  "+pwd);
                    iteracion++;
                    colision=false;
                }
            }else{
                iteracion++;
            }
        }//fin While
        
        System.out.println("RESULTADOS>>>");
        System.out.println("Número passwords generados >>> "+rt.size());
        System.out.println("Número colisiones encontradas >>> "+numerocolisiones);
        double success = ((double)numerocolisiones/numeropruebas)*100;
        System.out.println("SUCCESS RATE = " + success + "%");
    }//finMain



    public static String generaPassword (int tamanyo){
    /* Método para generar password de forma aleatoria de tamaño tamanyo*/
        StringBuilder password = new StringBuilder();
        Random random = new Random ();
        for (int i=0;i<tamanyo;i++){
            char letra = (char) ('a' + random.nextInt(26));
            password.append(letra);
        } 
        return password.toString();
    } //fin generaPassword ()


    public static String reconstruccion (byte[] hash){
        StringBuilder result = new StringBuilder();
        for (byte b : hash){
            if((b >= (byte)0x61) && (b<=(byte)0x7A)){
                result.append((char) b); //Si el byte comprende el rango ASCII de [a-z], no modifico
            }else if (b>= (byte)0x80){
                b = (byte) ((b & 0x7F) + 0x01);
                byte diferencia = (byte) (0x7A - 0x61 + 1) ;
                byte bytetransformado = (byte) (((byte) b % diferencia) + (byte)0x61);
                result.append((char) bytetransformado);
            }else{
                byte diferencia = (byte) (0x7A - 0x61 + 1) ;
                byte bytetransformado = (byte) (((byte) b % diferencia) + (byte)0x61);
                result.append((char) bytetransformado);
            }
        }
        return (result.toString());
    }
    

    public static byte[] generaHash (String mensaje, int tamanyo){
        CRC32 crc32 = new CRC32();
        byte[] mensajeBytes = mensaje.getBytes();
        crc32.update(mensajeBytes);
        int hash = (int) crc32.getValue(); // Obtenemos el valor como un int de 32 bits

        // Convertimos el valor int en un array de bytes
        byte[] hashBytes = new byte[tamanyo];
        for (int i = 0; i < tamanyo; i++) {
            hashBytes[i] = (byte) ((hash >> (i * 8)) & 0xFF);
        }

        return hashBytes;
    }

    public static long bytesToLong (byte[] cadena){
        long result = 0;
        for (int i = 0; i < cadena.length; i++) {
            result = (result << 8) | (cadena[i] & 0xFF);
        }
        return result;
    } //Fin bytesToLong


    public static String generarClaveAleatoria() {
        Random random = new Random();
        int min = 0x61616161; // Valor mínimo en hexadecimal
        int max = 0x7A7A7A7A; // Valor máximo en hexadecimal
        int randomNumber = min + random.nextInt(max - min + 1);
        String claveAleatoria = String.format("%04x", randomNumber); // Convierte el número a una cadena hexadecimal de 4 dígitos
        return claveAleatoria;
    }
}