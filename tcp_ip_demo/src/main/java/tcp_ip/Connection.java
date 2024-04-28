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
}


