# Hướng dẫn khởi tạo, ký và xác thực JWT

## Sơ đồ
![image](./issue-and-verify-jwt-token.jpg)

## Cấu trúc thư mục

```bash
+---src
|   +---main
|   |   +---java
|   |   |   \---com
|   |   |       \---learnspringboot
|   |   |           \---identity_user
|   |   |               |   IdentityUserApplication.java
|   |   |               |
|   |   |               +---controller
|   |   |               |       AuthenticationController.java
|   |   |               |       UserController.java
|   |   |               |
|   |   |               +---dto
|   |   |               |   +---request
|   |   |               |   |       AuthenticationRequest.java
|   |   |               |   |       IntrospectRequest.java
|   |   |               |   |       UserCreationRequest.java
|   |   |               |   |       UserUpdateRequest.java
|   |   |               |   |
|   |   |               |   \---response
|   |   |               |           ApiResponse.java
|   |   |               |           AuthenticationResponse.java
|   |   |               |           IntrospectResponse.java
|   |   |               |           UserResponse.java
|   |   |               |
|   |   |               +---entity
|   |   |               |       User.java
|   |   |               |
|   |   |               +---exception
|   |   |               |       AppException.java
|   |   |               |       ErrorCode.java
|   |   |               |       GlobalExceptionHandler.java
|   |   |               |
|   |   |               +---mapper
|   |   |               |       UserMapper.java
|   |   |               |
|   |   |               +---repository
|   |   |               |       UserRepository.java
|   |   |               |
|   |   |               \---service
|   |   |                       AuthenticationService.java
|   |   |                       UserService.java
|   |   |
|   |   \---resources
|   |       |   application.yml
|   |       |
|   |       +---static
|   |       \---templates
```

## Dependencies

* Spring Web
* Spring Data JPA
* MySQL Driver
* OpenAPI
* Lombok
* Mapstruct
* Lombok Binding Mapstruct
* Spring Security Crypto 
* Nimbus JOSE+JWT

## Đặt vấn đề

**Nhược Điểm Khi Gửi Thông Tin Đăng Nhập Qua Request**

Như ở bài trước ta đã gửi thông tin đăng nhập của `User` với `username` và `password`. Tuy nhiên cách này tiềm ẩn nhiều rủi ro.

* Nguy Cơ Bị Đánh Cắp Thông Tin: Nếu kết nối không được bảo vệ bằng HTTPS, thông tin đăng nhập có thể bị đánh chặn bởi các attacker trong quá trình truyền tải dữ liệu. Điều này đặc biệt nguy hiểm trên các mạng không an toàn như Wi-Fi công cộng.

* Tấn Công Man-in-the-Middle (MitM): Ngay cả khi sử dụng HTTPS, vẫn tồn tại nguy cơ bị tấn công MitM nếu attacker có thể can thiệp vào quá trình xác thực SSL/TLS và thiết lập kết nối giả mạo.

* Lưu Trữ Không An Toàn: Thông tin đăng nhập có thể bị lưu trữ không an toàn trên client (trình duyệt, ứng dụng di động), dẫn đến nguy cơ bị lộ khi thiết bị bị xâm nhập hoặc đánh cắp.

* Tần Suất Gửi Thông Tin Đăng Nhập Cao: Mỗi lần người dùng thực hiện một hành động yêu cầu xác thực, thông tin đăng nhập phải được gửi lại, làm tăng nguy cơ bị đánh cắp thông tin và làm giảm hiệu suất hệ thống.

* Không Có Cơ Chế Hết Hạn: Khi gửi thông tin đăng nhập trực tiếp, khó khăn trong việc quản lý phiên làm việc và cơ chế hết hạn tự động, dễ dẫn đến các phiên làm việc không an toàn kéo dài.

## Giải pháp: Sử Dụng JWT (JSON Web Token)

**Trước hết ta cần hiểu Token-based authentication là gì và cách hoạt động của nó ra sao.**

### Token-based authentication là gì

Token-based authentication là một phương thức xác thực người dùng trong đó server cấp một mã token cho người dùng khi họ đăng nhập thành công. Token này sau đó được sử dụng để xác minh các yêu cầu tiếp theo mà không cần phải gửi thông tin đăng nhập (username và password) mỗi lần. Đây là cách mà nhiều ứng dụng web và dịch vụ API hiện đại quản lý xác thực và phân quyền.

### Cách hoạt động của Token-based Authentication

![token-based authentication](https://www.freecodecamp.org/news/content/images/2023/01/token-based-authentication.jpg)

* Đăng nhập: Người dùng gửi yêu cầu đăng nhập với thông tin xác thực (username và password) tới server.

* Xác thực: Server xác minh thông tin đăng nhập. Nếu hợp lệ, server tạo ra một token (thường là JWT - JSON Web Token) và gửi lại cho người dùng.

* Lưu trữ token: Người dùng nhận token và lưu trữ nó ở một nơi an toàn, thường là trong bộ nhớ của ứng dụng hoặc cookie.

* Gửi token trong các yêu cầu tiếp theo: Khi người dùng muốn truy cập các tài nguyên yêu cầu xác thực, token này được gửi cùng với các yêu cầu HTTP (thường trong header Authorization hoặc như một phần của URL/query parameters).

* Xác thực token: Server kiểm tra token trong mỗi yêu cầu. Nếu token hợp lệ và chưa hết hạn, server cho phép yêu cầu tiếp tục, nếu không thì yêu cầu bị từ chối.

### Sử dụng JWT

JWT là một chuẩn token thường được sử dụng trong token-based authentication. JWT chứa ba phần:


* Header: Chứa thông tin về loại token và thuật toán sử dụng để mã hóa.

* Payload: Chứa dữ liệu thực tế, như thông tin người dùng và các quyền hạn.

* Signature: Chữ ký được tạo từ header và payload để đảm bảo tính toàn vẹn 
của token.

JWT được mã hóa base64, làm cho nó dễ dàng truyền tải qua các giao thức web.

![jwt](https://topdev.vn/blog/wp-content/uploads/2017/12/jwt-la-gi.jpeg)

**Lợi Ích Của JWT**


* Bảo Mật Tốt Hơn: JWT mã hóa thông tin và xác thực tính toàn vẹn của token, giúp ngăn chặn các cuộc tấn công giả mạo.

* Không Cần Gửi Lại Thông Tin Đăng Nhập: Sau khi xác thực ban đầu, chỉ cần gửi token trong các request tiếp theo, giảm tần suất gửi thông tin nhạy cảm.

* Quản Lý Phiên Làm Việc Hiệu Quả: JWT có thể chứa thời gian hết hạn (expiration time), giúp dễ dàng quản lý phiên làm việc của người dùng.

* Lưu Trữ An Toàn Trên Client: JWT có thể được lưu trữ an toàn trong các cookie HTTP-only hoặc local storage của trình duyệt.

### Tạo JWT

Ta cần thay đổi một chút với `AuthenticationResponse`, khi này có thêm thuộc tính `token`:

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean authenticated;
}
```

Tiếp theo ta cần một phương thức sinh token. Ta sẽ tạo ra JWT theo đúng cấu trúc như hình ở trên, bao gồm `Header`, `Payload` và `Signature`.

```java
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
```

Sau đó sửa lại phương thức `authenticate()` của `AuthenticationService`:

```java
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
```

Sửa lại `AuthenticationController`:

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                    .result(authenticationResponse)
                    .build();
    }
    
}
```

Giờ ta thử với API `/auth/token` và xem kết quả:

```json
{
    "code": 1000,
    "result": {
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJsZWFybi1zcHJpbmctYm9vdC0zIiwic3ViIjoidXNlcm5hbWUxMjM0NTYiLCJleHAiOjE3MTY5MTQxNDIsImlhdCI6MTcxNjkxMDU0MiwidXNlcklkIjoiQ3VzdG9tIn0.Jl35agG9mGKoDNGBcXn7v08LJZYanvYtiFuxQiOnKbXBixCtZ7iqiKWv8CCOGaZEynFKWB1MaxuC1MP2VAvSGA",
        "authenticated": true
    }
}
```

### Xác thực JWT

Ta cần một phương thức xác thực `introspect`:

```java
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
```

Rồi tạo một endpoint cho bước xác thực này:

```java
@PostMapping("/introspect")
public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) 
                                                        throws JOSEException, ParseException {

    IntrospectResponse introspectResponse = authenticationService.introspect(request);
    
    return ApiResponse.<IntrospectResponse>builder()
                        .result(introspectResponse)
                        .build();
}
```