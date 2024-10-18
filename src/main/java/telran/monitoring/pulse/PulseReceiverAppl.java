package telran.monitoring.pulse;

import java.net.*;
import java.util.Arrays;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;


public class PulseReceiverAppl {
	private static final int PORT = 4000;
	private static final int MAX_BUFFER_SIZE = 1500;
	static DatagramSocket socket;
	
	//creating DynamoDB client
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider("default"))
            .withRegion(Regions.US_EAST_1)
            .build();
	//assigning client to the DynamoDB
	static DynamoDB dynamo = new DynamoDB(client);
	//connecting to the Table (can be hardcoded)
	static Table table = dynamo.getTable("pulse_values");

	public static void main(String[] args) throws Exception {
		socket = new DatagramSocket(PORT);
		byte[] buffer = new byte[MAX_BUFFER_SIZE];
		while (true) {
			DatagramPacket packet = new DatagramPacket(buffer, MAX_BUFFER_SIZE);
			socket.receive(packet);
			processReceivedData(buffer, packet);
		}

	}

	private static void processReceivedData(byte[] buffer, DatagramPacket packet) {
		String json = new String(Arrays.copyOf(packet.getData(), packet.getLength()));
		System.out.println(json);
		//place data to the DynamoDB Table
		table.putItem(new PutItemSpec().withItem(Item.fromJSON(json)));
	}

}
