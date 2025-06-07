package com.ecommerce.security.jwt;

import com.ecommerce.security.services.UserDetailsImpl;
import com.ecommerce.security.services.UserDetailsServicesImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.observation.transport.ResponseContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookieName;

    public String getJwtFromCookies(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        logger.debug("Cookie found: {}", cookie.getValue());
        if(cookie != null)
            return cookie.getValue();
        else
            return null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal){
        String jwt = generatingTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName,jwt)
                .path("/api")
                .maxAge(24*60*60)
                .httpOnly(false)
                .build();
        return cookie;
    }

    public ResponseCookie generateCleanCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName,null)
                .path("/api")
                .build();
        return cookie;
    }


    // 2. Generating token from username
    public String generatingTokenFromUsername(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    // 2.Getting username from JWT Token(Sometime viceversa scenarios would be there)
    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    // 4. Generating signing key
    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    // 5. Validate Jwt Token
    public boolean validateJwtToken(String authToken){
        try{
            System.out.println("Validated...............................................");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT Token : {}",e.getMessage());
        }
        catch (ExpiredJwtException e) {
            logger.error("JWT Token is expired : {}",e.getMessage());
        }
        catch (UnsupportedJwtException e) {
            logger.error("JWT Token is unsupported : {}",e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.error("JWT claims String is empty : {}",e.getMessage());
        }
        return false;
    }

}
