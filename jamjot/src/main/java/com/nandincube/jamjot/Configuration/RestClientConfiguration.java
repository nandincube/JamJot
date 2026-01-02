package com.nandincube.jamjot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor.ClientRegistrationIdResolver;
import org.springframework.web.client.RestClient;

//https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html#oauth2-client-access-protected-resources-current-user
@Configuration
public class RestClientConfiguration {

	@Bean
	public RestClient restClient(OAuth2AuthorizedClientManager authorizedClientManager) {
		OAuth2ClientHttpRequestInterceptor requestInterceptor =
				new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
		requestInterceptor.setClientRegistrationIdResolver(clientRegistrationIdResolver());

		return RestClient.builder()
				.requestInterceptor(requestInterceptor)
				.build();
	}

	private static ClientRegistrationIdResolver clientRegistrationIdResolver() {
		return (request) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return (authentication instanceof OAuth2AuthenticationToken principal)
				? principal.getAuthorizedClientRegistrationId()
				: null;
		};
	}

}
