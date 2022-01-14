package es.fermax.notificationservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.fermax.fermaxsecurity.UserIDEncryptorService;
import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.exception.UserIdForAdminMandatoryException;
import es.fermax.notificationservice.exception.UserNotExistException;
import es.fermax.notificationservice.service.AppTokenService;
import es.fermax.notificationservice.util.UserUtils;
import es.fermax.notificationservice.util.Utils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Validated
@RestController
@RequestMapping(value = { "api/v1/" })
@Slf4j
public class AppTokenController extends AController {

	@Autowired
	private UserIDEncryptorService userIDEncryptorService;

	@Autowired
	AppTokenService appTokenService;

	/**
	 * Add a mobile token of a user with mobile information
	 *
	 * @param appTokenDTO
	 * @return message
	 */
	@ApiOperation(value = "Update User App Token")
	@PostMapping(value = "/apptoken")
	@ApiResponses(value = { @ApiResponse(code = 200, message = OK, response = String.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR) })
	public ResponseEntity<Object> updateAppToken(@Valid @RequestBody(required = true) AppTokenDTO appTokenDTO) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Integer userId = UserUtils.retrieveUserId(authentication, appTokenDTO.getUserId(), userIDEncryptorService);
			appTokenDTO.setUserId(userId);
			log.info("Updating Token {}", appTokenDTO);
			appTokenService.updateAppToken(appTokenDTO);
			return new ResponseEntity<>("Token Updated", HttpStatus.OK);
		} catch (UserIdForAdminMandatoryException | UserNotExistException userException) {
			log.error(USER_ERROR, userException);
			return new ResponseEntity<>(USER_ERROR, HttpStatus.PRECONDITION_FAILED);
		} catch (Exception e) {
			log.error(INTERNAL_SERVER_ERROR, e);
			return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delete a mobile token of a user
	 *
	 * @param token
	 * @param userId
	 */
	@ApiOperation(value = "Delete App Token")
	@DeleteMapping(value = "/apptoken")
	@ApiResponses(value = { @ApiResponse(code = 200, message = OK, response = String.class),
			@ApiResponse(code = 404, message = TOKEN_NOT_FOUND, response = String.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR) })
	public ResponseEntity<Object> deleteUserAppToken(@RequestParam(required = true) @ApiParam(value = "token") String token,
			@RequestParam(required = false) @ApiParam(value = "userId", example = "0") Integer userId) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			userId = UserUtils.retrieveUserId(authentication, userId, userIDEncryptorService);
			token = Utils.sanitizeInputString(token);
			log.info("Deleting Token {}", token);
			if (appTokenService.deleteToken(token, userId)) {
				return new ResponseEntity<>("Token Deleted", HttpStatus.OK);
			}
			return new ResponseEntity<>(TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND);
		} catch (UserIdForAdminMandatoryException | UserNotExistException userException) {
			log.error(USER_ERROR, userException);
			return new ResponseEntity<>(USER_ERROR, HttpStatus.PRECONDITION_FAILED);
		} catch (Exception e) {
			log.error(INTERNAL_SERVER_ERROR, e);
			return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	/**
	 * Get a mobile token of a user with mobile information
	 *
	 * @param token
	 * @param os
	 * @param osVersion
	 * @param appVersion
	 * @param locale
	 * @param userId
	 * @return message
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGEMENT','ROLE_MANAGER')")
	@ApiOperation(value = "Get Paginated and Filtered App Tokens")
	@GetMapping(value = "/apptoken")
	@ApiResponses(value = { @ApiResponse(code = 200, message = OK, response = AppTokenDTO.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR) })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
			@ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
			@ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
					+ "Default sort order is ascending. " + "Multiple sort criteria are supported.") })
	public ResponseEntity<Object> getPaginatedAndFilteredAppTokens(@RequestParam(required = false) @ApiParam(value = "token") String token,
			@RequestParam(required = false) @ApiParam(value = "os") String os,
			@RequestParam(required = false) @ApiParam(value = "osVersion") String osVersion,
			@RequestParam(required = false) @ApiParam(value = "appVersion") String appVersion,
			@RequestParam(required = false) @ApiParam(value = "locale") String locale,
			@RequestParam(required = false) @ApiParam(value = "userId", example = "0") Integer userId, Pageable pageable,
			@ApiParam(value = "Query for all elements") @Valid @RequestParam(value = "question", required = false) String question) {
		try {
			return new ResponseEntity<>(
					appTokenService.getPaginatedAndFiltered(token, os, osVersion, appVersion, locale, userId, pageable, question),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error(INTERNAL_SERVER_ERROR, e);
			return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
}
