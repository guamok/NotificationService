package es.fermax.notificationservice.config;

import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.model.FcmMessage;
import es.fermax.notificationservice.repo.AppTokenRepository;
import es.fermax.notificationservice.repo.FcmMessageRepository;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javax.annotation.PostConstruct;

@Configuration
@EnableMongoAuditing
public class MongoDbConfiguration {

	@Value("${spring.data.mongodb.database}")
	String database;
	@Value("${spring.data.mongodb.uri}")
	String uri;

	@Autowired
	AppTokenRepository appTokenRepository;

	@Autowired
	FcmMessageRepository fcmMessageRepository;

	@Bean
	public MongoDatabase mongoDatabase() {

		MongoClient mongoClient;
		MongoDatabase mongoDatabase;

		mongoClient = MongoClients.create(buildMongoClientSettings(uri));
		mongoDatabase = mongoClient.getDatabase(database).withCodecRegistry(codecRegistries());
		mongoClient.close();

		return mongoDatabase;
	}

	private MongoClientSettings buildMongoClientSettings(String clusterUrl) {
		return MongoClientSettings.builder().applyConnectionString(new ConnectionString(clusterUrl)).build();
	}

	@PostConstruct
	public void initDatabase() {
		AppToken appToken = new AppToken();
		appToken.setAppVersion("1");
		appTokenRepository.save(appToken);
		appTokenRepository.delete(appToken);

		FcmMessage fcmMessage = new FcmMessage();
		fcmMessage.setTarget("1");
		fcmMessageRepository.save(fcmMessage);
		fcmMessageRepository.delete(fcmMessage);

	}

	private CodecRegistry codecRegistries() {
		return CodecRegistries.fromRegistries(
				// save uuids as UUID, instead of LUUID
				CodecRegistries.fromProviders(new UuidCodecProvider(UuidRepresentation.STANDARD)),
				MongoClientSettings.getDefaultCodecRegistry());
	}
}
