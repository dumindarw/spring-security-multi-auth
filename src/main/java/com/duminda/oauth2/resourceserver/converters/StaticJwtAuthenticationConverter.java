package com.duminda.oauth2.resourceserver.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class StaticJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    Logger logger = LoggerFactory.getLogger(StaticJwtAuthenticationConverter.class);

    private static final String GROUPS_CLAIM = "groups";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString("sub");
        String role = jwt.getClaimAsString("role");

        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        authorities.forEach(grantedAuthority -> logger.info(grantedAuthority.getAuthority()));
        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
        return new UsernamePasswordAuthenticationToken(/*userDetails.getUsername()*/username, "n/a", authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return this.getGroups(jwt).stream()
                .map(authority -> ROLE_PREFIX + authority.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getGroups(Jwt jwt) {
        Object groups = jwt.getClaims().get(GROUPS_CLAIM);
        if (groups instanceof Collection) {
            return (Collection<String>) groups;
        }
        return Collections.emptyList();
    }
}
