package es.fermax.notificationservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.rabbit.AppTokenListener;
import es.fermax.notificationservice.rabbit.AppTokenSender;
import es.fermax.notificationservice.rabbit.messages.AppTokenMessage;
import es.fermax.notificationservice.repo.AppTokenRepository;

@Service
public class AppTokenService {

	@Autowired
	AppTokenRepository appTokenRepository;

	@Autowired
	AppTokenSender appTokenSender;

	private static final Logger log = LoggerFactory.getLogger(AppTokenService.class);

	public boolean updateAppToken(AppTokenDTO appTokenDTO) {
		log.info("Add token {} - {}", appTokenDTO.getToken(), appTokenDTO.getUserId());
		appTokenSender.sendMessage(appTokenDTO.getUserId(), appTokenDTO.getToken(), appTokenDTO.getOs(), appTokenDTO.getOsVersion(),
				appTokenDTO.getAppVersion(), appTokenDTO.getLocale(), appTokenDTO.getActive());
		return true;
	}

	public boolean deleteToken(String token, Integer userId) {
		Optional<AppToken> appToken = appTokenRepository.findByTokenAndUserId(token, userId);
		if (!appToken.isPresent()) {
			log.info("App Token not found {} - {}", userId, token);
			return false;
		}
		appTokenRepository.delete(appToken.get());
		log.info("App Token deleted {} - {}", userId, token);
		return true;

	}

	public Page<AppTokenDTO> getPaginatedAndFiltered(String token, String os, String osVersion, String appVersion, String locale,
			Integer userId, Pageable pageable, @Nullable String question) {
		return appTokenRepository.getPaginatedAndFiltered(token, os, osVersion, appVersion, locale, userId, pageable, question)
				.map(AppTokenDTO::new);
	}

	public List<AppToken> getTokensByUser(Integer userId) {
		return appTokenRepository.findByUserId(userId);
	}

	public List<AppToken> getActiveTokens(Integer userId) {
		return appTokenRepository.findByUserIdAndActiveTrue(userId);
	}

	public Optional<AppToken> getAppToken(String token, Integer userId) {
		return appTokenRepository.findByTokenAndUserId(token, userId);
	}

	public void save(AppToken appToken) {
		appTokenRepository.save(appToken);
	}

	public List<AppTokenDTO> getAppTokenDTOsByUserId(Integer userId) {
		return appTokenRepository.findByUserIdAndActiveTrue(userId).stream().map(AppTokenDTO::new).collect(Collectors.toList());
	}

	public List<AppTokenDTO> getAppTokenDTOsByToken(String token) {
		return appTokenRepository.findByTokenAndActiveTrue(token).stream().map(AppTokenDTO::new).collect(Collectors.toList());
	}

	public List<AppToken> getAppTokenByToken(String token) {
		return appTokenRepository.findByTokenAndActiveTrue(token);
	}

	public void storeAppToken(AppTokenMessage appTokenMessage) {
		AppToken appToken = null;
		Optional<AppToken> appTokenOptional = getAppToken(appTokenMessage.getToken(), appTokenMessage.getUserId());
		if (appTokenOptional.isPresent()) {
			appToken = appTokenOptional.get();
			AppTokenListener.log.debug("user: {} - token {} reactivated", appToken.getToken(), appToken.getUserId());
		} else {
			appToken = new AppToken(appTokenMessage.getUserId(), appTokenMessage.getToken());
			AppTokenListener.log.debug("user: {} - token {} added", appToken.getToken(), appToken.getUserId());
		}

		if (appTokenMessage.getActive() != null) {
			appToken.setActive(appTokenMessage.getActive());
		}
		if (!StringUtils.isEmpty(appTokenMessage.getAppVersion())) {
			appToken.setAppVersion(appTokenMessage.getAppVersion());
		}
		if (!StringUtils.isEmpty(appTokenMessage.getLocale())) {
			appToken.setLocale(appTokenMessage.getLocale());
		}
		if (!StringUtils.isEmpty(appTokenMessage.getOs())) {
			appToken.setOs(appTokenMessage.getOs());
		}
		if (!StringUtils.isEmpty(appTokenMessage.getOsVersion())) {
			appToken.setOsVersion(appTokenMessage.getOsVersion());
		}
		save(appToken);
	}

	public void deactivateOldTokens(final AppTokenMessage appTokenMessage) {
		List<AppToken> tokensToDesactivate = getAppTokenByToken(appTokenMessage.getToken());
		tokensToDesactivate.forEach(token -> {
			token.setActive(false);
			save(token);
		});
	}
}