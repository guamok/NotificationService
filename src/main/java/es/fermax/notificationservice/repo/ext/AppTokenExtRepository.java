package es.fermax.notificationservice.repo.ext;

import javax.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.fermax.notificationservice.model.AppToken;

public interface AppTokenExtRepository {

	Page<AppToken> getPaginatedAndFiltered(String token, String os, String osVersion, String appVersion, String locale,
			Integer userId, Pageable pageable, @Nullable String question);
}
