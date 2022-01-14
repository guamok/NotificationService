package es.fermax.notificationservice.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import es.fermax.notificationservice.model.FcmMessage;

@Repository("fcmMessageRepository")
public interface FcmMessageRepository
		extends MongoRepository<FcmMessage, String>, PagingAndSortingRepository<FcmMessage, String>, QueryByExampleExecutor<FcmMessage> {

	Optional<FcmMessage> findByFcmId(String fcmId);

}
