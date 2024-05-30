package com.learnspringboot.identity_user.configuration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inject giá trị của SIGNER_KEY từ tệp cấu hình (ví dụ: application.properties)
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY; 

    // Khai báo mảng chứa các endpoint công khai, không yêu cầu xác thực
    private final String[] PUBLIC_ENDPOINT = {
        "/api/v1/users",
        "/auth/token",
        "/auth/introspect"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình các quy tắc ủy quyền cho các yêu cầu HTTP
        httpSecurity.authorizeHttpRequests(request -> 
                        // Cho phép các yêu cầu POST tới các endpoint công khai không cần xác thực
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll() 
                        // Yêu cầu tất cả các yêu cầu khác phải được xác thực
                        .anyRequest().authenticated()); 

        // Cấu hình máy chủ tài nguyên OAuth2 để sử dụng JWT
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfig -> jwtConfig.decoder(jwtDecoder())));

        // Tạm thời vô hiệu hóa CSRF trong ví dụ này
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        
        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        // Tạo một khóa bí mật (SecretKeySpec) từ SIGNER_KEY sử dụng thuật toán "HS512"
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
        
        // Tạo và trả về đối tượng JwtDecoder sử dụng khóa bí mật và thuật toán "HS512" để giải mã JWT
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

}

