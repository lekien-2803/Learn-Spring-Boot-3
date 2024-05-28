package com.learnspringboot.identity_user.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learnspringboot.identity_user.dto.request.AuthenticationRequest;
import com.learnspringboot.identity_user.dto.request.IntrospectRequest;
import com.learnspringboot.identity_user.dto.response.AuthenticationResponse;
import com.learnspringboot.identity_user.dto.response.IntrospectResponse;
import com.learnspringboot.identity_user.exception.AppException;
import com.learnspringboot.identity_user.exception.ErrorCode;
import com.learnspringboot.identity_user.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(request.getUsername());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }

    // Tạo token
    private String generateToken(String username){

        // Khởi tạo Header với mã hóa là HS512
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Khởi tạo ClaimSet với các thông tin
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                                        .subject(username)
                                        .issuer("learn-spring-boot-3")
                                        .issueTime(new Date())
                                        .expirationTime(new Date(
                                            Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                                        ))
                                        .claim("userId", "Custom")
                                        .build();

        // Khởi tạo Payload với các thông tin từ ClaimSet
        Payload  payload = new Payload(jwtClaimsSet.toJSONObject());

        // Khởi tạo đối tượng JWS với phần Header và Payload ở trên
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            // Thực hiện kí token
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.CAN_NOT_CREATE_TOKEN);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        // Lấy token từ request
        var token = request.getToken();
    
        // Khởi tạo JWSVerifier với SIGNER_KEY để xác minh chữ ký của JWT
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
    
        // Phân tích token từ chuỗi JWT thành đối tượng SignedJWT
        SignedJWT signedJWT = SignedJWT.parse(token);
    
        // Lấy thời gian hết hạn của token từ các claim của JWT
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
    
        // Xác minh chữ ký của token
        var verified = signedJWT.verify(jwsVerifier);
    
        // Tạo đối tượng IntrospectResponse và trả về
        // Trường 'valid' sẽ là 'true' nếu chữ ký hợp lệ và token chưa hết hạn
        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }
    
}
