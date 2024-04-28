package tcp_ip;
import java.net.*;
import java.io.*;

public class ServidorTCP {
    public static void main(String args[]) {
        try {
            
            int puertoServicio = 7896;
            ServerSocket escuchandoSocket = new ServerSocket(puertoServicio);
            while (true) {
                Socket socketCliente = escuchandoSocket.accept();
                Conexion c = new Conexion(socketCliente);
                System.out.println("Conexion: " + c);
            }
        } catch (IOException e) {
            System.out.println("Escuchando:" + e.getMessage());
        }
    }
}

class Conexion extends Thread {
    DataInputStream entrada;
    DataOutputStream salida;
    Socket socketCliente;

    public Conexion(Socket unSocketCliente) {
        try {
            socketCliente = unSocketCliente;
            entrada = new DataInputStream(socketCliente.getInputStream());
            salida = new DataOutputStream(socketCliente.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Conexión :" + e.getMessage());
        }
    }

    
    public void run() {
        try { // un servidor eco
                  // Read message from client
                  String message = entrada.readUTF();
                  String[] parts = message.split(" ");
      
                  // If the client chose the "Consultar por el significado de una palabra" option
                  if (parts.length >= 3 && parts[0].equals("1") && parts[1].equals("2")) {
                      String word = parts[2];
      
                      // Search for word's meaning in MongoDB database
                      Connection connection = Connection.getInstance();
                      String meaning = connection.searchWordInDatabase(word);
      
                      // Send the meaning back to the client
                      salida.writeUTF(meaning);
                  } else {
                      // Echo back the message to the client
                      salida.writeUTF(message);
                  }

            socketCliente.close();
        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}