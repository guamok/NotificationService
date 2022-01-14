package es.fermax.notificationservice.repo.ext;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.model.AppToken;

@Repository
public class AppTokenExtRepositoryImpl implements AppTokenExtRepository {

	@Autowired
	 MongoTemplate mongoTemplate;

	/**
	 * Filters for appToken data.
	 * 
	 * @param token
	 * @param os
	 * @param osVersion
	 * @param appVersion
	 * @param locale
	 * @param userId
	 * @param pageable
	 * @param question
	 * @return
	 */
	@Override
	public Page<AppToken> getPaginatedAndFiltered(String token, String os, String osVersion, String appVersion, String locale,
			Integer userId, Pageable pageable, @Nullable String question) {
		Query query = new Query().with(pageable);
		Criteria criteria = new Criteria();
		AppTokenDTO appTokenDTO = new AppTokenDTO();
		AppTokenDTO appTokenDTOQ = new AppTokenDTO();
		appTokenDTO.setToken(token);
		appTokenDTO.setOs(os);
		appTokenDTO.setOsVersion(osVersion);
		appTokenDTO.setAppVersion(appVersion);
		appTokenDTO.setUserId(userId);
		appTokenDTO.setLocale(locale);
		if (question == null || question.isEmpty()) {
			criteria = getAppTokenCriteria(appTokenDTO, true);
		} else {
			appTokenDTOQ.setToken(question);
			appTokenDTOQ.setOs(question);
			appTokenDTOQ.setOsVersion(question);
			appTokenDTOQ.setAppVersion(question);
			appTokenDTOQ.setLocale(question);
			criteria.andOperator(getAppTokenCriteria(appTokenDTOQ, false), getAppTokenCriteria(appTokenDTO, true));
		}

		// Sonar shows issue if don't check null
		if (criteria != null) {
			query.addCriteria(criteria);
		}

		return PageableExecutionUtils.getPage(mongoTemplate.find(query, AppToken.class), pageable,
				() -> mongoTemplate.count(query.skip(0).limit(0), AppToken.class));
	}

	/**
	 * Creates and return a Criteria.
	 * 
	 * @param appTokenDTO
	 * @param isConjuntion
	 * @return
	 */
	private Criteria getAppTokenCriteria(AppTokenDTO appTokenDTO, boolean isConjuntion) {

		List<Criteria> criterias = new ArrayList<>();
		Criteria criteria = new Criteria();

		if (appTokenDTO.getUserId() != null) {
			criterias.add(Criteria.where("userId").is(appTokenDTO.getUserId()));
		}
		if (isStringNullOrEmpty(appTokenDTO.getToken())) {
			criterias.add(Criteria.where("token").regex(appTokenDTO.getToken()));
		}
		if (isStringNullOrEmpty(appTokenDTO.getOs())) {
			criterias.add(Criteria.where("os").regex(appTokenDTO.getOs()));
		}
		if (isStringNullOrEmpty(appTokenDTO.getOsVersion())) {
			criterias.add(Criteria.where("osVersion").regex(appTokenDTO.getOsVersion()));
		}
		if (isStringNullOrEmpty(appTokenDTO.getAppVersion())) {
			criterias.add(Criteria.where("appVersion").regex(appTokenDTO.getAppVersion()));
		}
		if (isStringNullOrEmpty(appTokenDTO.getLocale())) {
			criterias.add(Criteria.where("locale").regex(appTokenDTO.getLocale()));
		}

		if (criterias.size() > 1) {

			if (Boolean.TRUE.equals(isConjuntion)) {
				criteria.andOperator(criterias.toArray(new Criteria[0]));
			} else {
				criteria.orOperator(criterias.toArray(new Criteria[0]));
			}
		} else if (criterias.size() == 1) {
			criteria = criterias.get(0);
		}

		return criteria;
	}

	private boolean isStringNullOrEmpty (String anyString){
		return anyString != null && anyString.isEmpty();
	}


}
