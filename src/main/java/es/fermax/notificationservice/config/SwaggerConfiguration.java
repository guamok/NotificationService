package es.fermax.notificationservice.config;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;
import static com.google.common.base.Predicates.or;

import java.util.Arrays;


@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

    @Value("${swagger.host:}")
    private String swaggerHost = null;
    
    @Value("${oauth.server}")
	private String oauthServer;
    
	@Value("${oauth.client}")
	private String oauthClientId;
	
	@Value("${oauth.secret}")
	private String oauthClientSecret;

    /**
     * Publish a bean to generate swagger2 endpoints
     * @return a swagger configuration bean
     */
    @Bean
    public Docket notificationServiceApiV1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(notificationServiceApiInfo())
                .groupName("NotificationService-api-1.0")
                .select()
                .paths(getPaths("v1"))
                .apis(RequestHandlerSelectors.any())
                .build()
                .host(swaggerHost)
                .useDefaultResponseMessages(false)
                .securitySchemes(Arrays.asList(securityScheme()))
				.securityContexts(Arrays.asList(securityContext("v1")));
    }
    
    /**
     * Api info
     * @return ApiInfo
     */
    private ApiInfo notificationServiceApiInfo() {
        return new ApiInfoBuilder()
                .title("Notification Service")
                .version("1.0")
                .build();
    }


    /**
     * Config paths.
     *
     * @return the predicate
     */
    private Predicate<String> getPaths(String version) {
    	return or(regex("/api/"+version+".*"),regex("/public/api/"+version+".*"));
    }
    
    

	@Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().clientId(oauthClientId).clientSecret(oauthClientSecret).useBasicAuthenticationWithAccessCodeGrant(true).build();
    }

	private SecurityScheme securityScheme() {
		GrantType grantType = new AuthorizationCodeGrantBuilder()
				.tokenEndpoint(new TokenEndpoint(oauthServer + "/token", "oauthtoken"))
				.tokenRequestEndpoint(new TokenRequestEndpoint(oauthServer + "/authorize", oauthClientId, oauthClientId))
				.build();

		return new OAuthBuilder().name("spring_oauth").grantTypes(Arrays.asList(grantType))
				.scopes(Arrays.asList(scopes())).build();
	}

	private AuthorizationScope[] scopes() {
		return new AuthorizationScope[]{
				new AuthorizationScope("admin", "for admin operations"),
				new AuthorizationScope("user", "for user operations"),
				new AuthorizationScope("management", "for management operations")
		};
	}

	private SecurityContext securityContext(String version) {
		return SecurityContext.builder()
				.securityReferences(Arrays.asList(new SecurityReference("spring_oauth", scopes())))
				.forPaths(PathSelectors.regex("/api/" + version + ".*")).build();
	}
}
