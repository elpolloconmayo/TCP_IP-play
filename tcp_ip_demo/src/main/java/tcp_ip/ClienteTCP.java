package tcp_ip;
import java.util.Scanner;
import java.net.*;
import java.io.*;


public class ClienteTCP {
    public static void main(String[] args) {
        while (true) {
            // input
            String menu = "\n\nSelecciona una opcion\n1) Servicio de significado de una palabra basado en un diccionario  (BD)\n2) Servicio de bibliotecas de documentos en formato pdf.";
            String input = System.console().readLine(menu + "\nEscribe 'exit' para salir\n");
            if (input.equals("exit")) {
                break;
            }
            else if (input.equals("1")) {
                menu = "\n\nSeleccione una opción\n1) Ingresar una palabra y su significado\n2) Consultar por el significado de una palabra";
                String input2 = System.console().readLine(menu + "\nEscribe 'exit' para salir\n");
                if (input2.equals("exit")) {
                    break;
                }
                else if (input2.equals("1")) {
                    String palabra = System.console().readLine("Escribe la palabra: ");
                    String significado = System.console().readLine("Escribe el significado: ");
                    String message = input + " " + input2 + " " + palabra + " " + significado;
                    String response = sendString(message);
                    System.out.println(response);
                }
                else if (input2.equals("2")) {
                    String palabra = System.console().readLine("Escribe la palabra que deseas buscar: ");
                    String message = input + " " + input2 + " " + palabra;
                    String response = sendString(message);
                    System.out.println(response);
                }
                else {
                    System.out.println("Opción no válida");
                    continue;
                }
            }
            else if (input.equals("2")) {
                menu = "\n\nSelecciona una opción\n1) Enviar documentos en formato pdf\n2) Recuperar un documento en pdf desde la Biblioteca\n3) Consultar acerca de los documentos existentes en biblioteca ";
                String input2 = System.console().readLine(menu + "\nEscribe 'exit' para salir\n");
                if (input2.equals("exit")) {
                    break;
                }
                else if (input2.equals("1")) {
                    // send pdf using path
                    // Ask the user to input the path enclosed in double quotes
                    System.out.println("Escribe la ruta del archivo (enclose the path in double quotes): ");
                    Scanner scanner = new Scanner(System.in);
                    String path = scanner.nextLine();
                    // Remove the double quotes from the file path
                    path = path.substring(1, path.length()-1);
                    path = path.replace(" ", "@@");
                    String message = input + " " + input2 + " " + path;
                    String response = sendString(message);
                    System.out.println(response);                    
                }
                else if (input2.equals("2")) {
                    String nombre = System.console().readLine("Escribe el nombre del documento que deseas recuperar: ");
                    String message = input + " " + input2 + " " + nombre;
                    String response = sendString(message);
                    System.out.println(response);
                }
                else if (input2.equals("3")) {
                    String message = input + " " + input2;
                    String response = sendString(message);
                    System.out.println(response);
                }
                else {
                    System.out.println("Opción no válida");
                    continue;
                }
            }
            else {
                System.out.println("Opción no válida");
                continue;
            }


        }
    }

    public static String sendString(String message){
        try {
            int puertoServicio = 7896;
            Socket s = new Socket("localhost", puertoServicio);
            DataInputStream entrada = new DataInputStream(s.getInputStream());
            DataOutputStream salida = new DataOutputStream(s.getOutputStream());
            salida.writeUTF(message);
            String datos = entrada.readUTF();
            //System.out.println("Recibido: " + datos);
            s.close();
            return datos;
        } catch (UnknownHostException e) {
            System.out.println("Socket: " + e.getMessage());
            return null;
        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            return null;
        }
    }
}