package GetStarted;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Simple application that shows how to use Azure Cosmos DB with the MongoDB API and Java.
 *
 */
public class Program {
	
    public static void main(String[] args)
    {
	/*
	* Replace connection string from the Azure Cosmos Portal
        */
	    if(args.length < 2) {
	        System.out.println("Please provide host and port");
	        return;
        }
        MongoClientURI uri = new MongoClientURI("mongodb://drone-barcode-reader:h5BKWwsqJWRK3TzjXNodITYvWWWtFiRpItLFj9vslsUjFjp2iVBGp7b7lrxyNwFangkqkkYzFXMi0pFJDNUybw==@drone-barcode-reader.documents.azure.com:10255/?ssl=true&replicaSet=globaldb");
		
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(uri);
            
            // Get database
            MongoDatabase database = mongoClient.getDatabase("db");

            // Get collection
            MongoCollection<Document> collection = database.getCollection("coll");
            Document document;
//            String host = "192.168.225.121";
//            int port = 9997;
            String host = args[0];
            int port = Integer.valueOf(args[1]);
            Socket socket = new Socket(host, port);
            DataInputStream inputStream;
            while(true) {
                inputStream = new DataInputStream(socket.getInputStream());
                String responseLine;
                while ((responseLine = inputStream.readLine()) != null) {
//                    System.out.println("Server: " + responseLine);
                    document = new Document("barcode", responseLine);
                    collection.insertOne(document);
                    if (responseLine.indexOf("Ok") != -1) {
                        break;
                    }
                }
            }
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if (mongoClient != null) {
        		mongoClient.close();
        	}
        }
    }
}
