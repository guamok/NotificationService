package es.fermax.notificationservice.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.repo.ext.AppTokenExtRepository;

@Repository("appTokenRepository")
public interface AppTokenRepository extends AppTokenExtRepository, MongoRepository<AppToken, Integer>,
		PagingAndSortingRepository<AppToken, Integer>, QueryByExampleExecutor<AppToken> {

	List<AppToken> findByUserId(Integer userId);

	Optional<AppToken> findByTokenAndUserId(String token, Integer userId);

	List<AppToken> findByUserIdAndActiveTrue(Integer userId);

	List<AppToken> findByTokenAndActiveTrue(String token);
}
