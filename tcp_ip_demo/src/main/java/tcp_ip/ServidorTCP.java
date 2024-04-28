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
                  }
                  else if(parts.length >= 3 && parts[0].equals("1") && parts[1].equals("1")) {
                      String word = parts[2];
                      String definition = parts[3];
      
                      // Insert word and definition in MongoDB database
                      Connection connection = Connection.getInstance();
                      connection.insertWordInDatabase(word, definition);
      
                      // Send a confirmation message back to the client
                      salida.writeUTF("The word and its definition were successfully inserted in the database.");
                  }
                  else if(parts.length >= 3 && parts[0].equals("2") && parts[1].equals("1")) {
                      String path = parts[2];
                      path = path.replace("@@", " ");
                      // Insert PDF in MongoDB database
                      Connection connection = Connection.getInstance();
                      connection.insertPdfInDatabase(path);
      
                      // Send a confirmation message back to the client
                      salida.writeUTF("The PDF was successfully inserted in the database.");
                  }
                  else if(parts.length >= 3 && parts[0].equals("2") && parts[1].equals("2")) {
                      String fileName = parts[2];
      
                      // Search for the PDF in MongoDB database
                      Connection connection = Connection.getInstance();
                      String pdf = connection.searchPdfInDatabase(fileName);
      
                      // Send the PDF back to the client
                      salida.writeUTF(pdf);
                  }
                  else if(parts.length >= 2 && parts[0].equals("2") && parts[1].equals("3")) {
                      // Search for all PDFs in MongoDB database
                      Connection connection = Connection.getInstance();
                      String pdfs = connection.searchAllPdfsInDatabase();
      
                      // Send the PDFs back to the client
                      salida.writeUTF(pdfs);
                  }
                  else {
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