package tcp_ip;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.Binary;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.Binary;

public class Connection {
    private static Connection instance = null;
    private MongoClient mongoClient;

    private Connection() {
        String connectionString = "mongodb+srv://juanjo:juan@sistemasd.qvqclgn.mongodb.net/?retryWrites=true&w=majority&appName=SistemasD";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try {
            this.mongoClient = MongoClients.create(settings);
            // Send a ping to confirm a successful connection
            MongoDatabase database = mongoClient.getDatabase("admin");
            database.runCommand(new Document("ping", 1));
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    public String searchWordInDatabase(String word) {
        MongoDatabase database = mongoClient.getDatabase("UDP");
        MongoCollection<Document> collection = database.getCollection("dictionary");
        Document document = collection.find(Filters.eq("palabra", word)).first();
        if (document != null) {
            return document.getString("definicion");
        } else {
            return "The word was not found in the database.";
        }
    }

    public void insertWordInDatabase(String word, String definition) {
        MongoDatabase database = mongoClient.getDatabase("UDP");
        MongoCollection<Document> collection = database.getCollection("dictionary");
        Document document = new Document("palabra", word)
                .append("definicion", definition);
        collection.insertOne(document);
        System.out.println("Word inserted successfully!");
    }

    public void insertPdfInDatabase(String filePath) {
        MongoDatabase database = mongoClient.getDatabase("UDP");
        MongoCollection<Document> collection = database.getCollection("pdfs");

        try {
            // Read the PDF file as a byte array
            File file = new File(filePath);
            byte[] pdfBytes = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(pdfBytes);
            fileInputStream.close();

            // Extract the file name from the file path
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            System.out.println(fileName);

            // Create a Binary object from the byte array
            Binary pdfBinary = new Binary(pdfBytes);

            // Create a new document and insert the PDF Binary and the file name
            Document document = new Document("fileName", fileName)
                    .append("file", pdfBinary);
            collection.insertOne(document);

            System.out.println("PDF file inserted successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
 
    public String showAllPdfsInDatabase() {
        MongoDatabase database = mongoClient.getDatabase("UDP");
        MongoCollection<Document> collection = database.getCollection("pdfs");
    
        StringBuilder pdfFileNames = new StringBuilder();
        for (Document document : collection.find()) {
            String fileName = document.getString("fileName");
            pdfFileNames.append(fileName).append("\n");
        }
    
        return pdfFileNames.toString();
    }




    public String searchPdfInDatabase(String fileName) {
        MongoDatabase database = mongoClient.getDatabase("UDP");
        MongoCollection<Document> collection = database.getCollection("pdfs");
    
        Document document = collection.find(Filters.eq("fileName", fileName)).first();
        if (document != null) {
            Binary pdfBinary = document.get("file", Binary.class);
            byte[] pdfBytes = pdfBinary.getData();
    
            try (OutputStream outputStream = new FileOutputStream(fileName)) {
                outputStream.write(pdfBytes);
                return "PDF file downloaded successfully!";
            } catch (IOException e) {
                e.printStackTrace();
                return "Error occurred while downloading the PDF file.";
            }
        } else {
            return "The PDF file was not found in the database.";
        }
    }
    
}


