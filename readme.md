# Xử lý ngoại lệ nâng cao

## Sơ đồ

![image](./advance-handing-exceptionjpg.jpg)

## Đặt vấn đề

Ở bài trước, thay vì trả về các response mặc định của hệ thống nếu có ngoại lệ xảy ra, ta đã có thể gom các xử lý ngoại lệ vào một class tên `GlobalExceptionHandler` và gửi trả về response một cách chủ động hơn.

Tuy nhiên nếu có xảy ra ngoại lệ, thì sẽ chỉ trả về message.

* Ví dụ:

```bash
User not found.
```

Thực tế thì ngay cả khi request gửi đi và nhận về reponse, chúng ta vẫn muốn response trả cho client được rõ ràng hơn nữa.

Ví dụ như mã của response này là bao nhiêu? Message của response có mã này là gì? Có thành công hay gặp lỗi? Nếu thành công thì đối tượng được trả về trông như thế nào? Nếu thất bài thì có gì?

Vì vậy ta cần phải chuẩn hóa Api Response với một cấu trúc cụ thể.

Ta thử vào trang web [currencylayer](https://currencylayer.com/documentation) và đọc tài liệu thì thấy họ có giải thích về api response của họ:

```json
{
    "success": true,
    "terms": "https://currencylayer.com/terms",
    "privacy": "https://currencylayer.com/privacy",
    "timestamp": 1432400348,
    "source": "USD",
    "quotes": {
        "USDAUD": 1.278342,
        "USDEUR": 1.278342,
        "USDGBP": 0.908019,
        "USDPLN": 3.731504
    }
}
```

Ta cũng sẽ làm điều tương tự với dự án của chúng ta. Tạo ra một Api Response của riêng chúng ta với các field tùy theo mục đích và yêu cầu của dự án.

Trong ví dụ này ta sẽ chuẩn hóa cấu trúc Api Response theo dạng:

```json
{
    "code": 1000, // Mã code tương ứng
    "message": "success", // Message tương ứng với ý nghĩa của code
    "result": { // Đối tượng được trả về
        "id": "2fe31e5f-0e1c-11ef-baeb-0242ac120002",
        "username": "user2",
        "email": "email2@mail.com",
        "password": "password2",
        "firstName": "Jane",
        "lastName": "Smith",
        "dob": "1985-05-15"
    }
}
```

## Giải quyết

### Khi response trả về thành công

Đầu tiên để có được response có cấu trúc như trên, ta cần tạo ra một class `ApiResponse`:

```java
public class ApiResponse<T> {
    private int code = 1000;
    private String message;
    private T result;
}
```

Giải thích: 
* Đầu tiên là `code`: mã code mặc định khi response thành công sẽ là `1000` (hoặc bất kì số nào bạn muốn thống nhất trong project).
* Tiếp theo là `message`: đây sẽ là thông báo tương ứng với ý nghĩa của mã `code` ở trên.
* Cuối cùng là `result` với kiểu dữ liệu Generic `T`, bởi ta chưa biết trước được kiểu dữ liệu (hoặc đối tượng) nào sẽ được truyền vào.


Ok, sau khi đã có `ApiResponse` ta sẽ cần thay đổi một chút trong `UserController`, kiểu response trả về không còn là `ResponseEntity` nữa mà sẽ là `ApiResponse`:

```java
    // Create
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        User user = userService.createUser(request);
        ApiResponse<User> apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);
        return apiResponse;
    }

    // Read
    @GetMapping("/{userId}")
    public ApiResponse<User> getUserById(@PathVariable("userId") String userId) {
        User user = userService.getUserById(userId);
        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }
    
    // Update
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable("userId") String userId, @RequestBody @Valid UserUpdateRequest request) {
        User user = userService.updateUser(userId, request);
        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }
```

### Khi xảy ra ngoại lệ

Vừa rồi là các trường hợp trả về thành công, vậy nếu có ngoại lệ xảy ra như bài trước thì sao?

Bài trước ta đã tạo ra `GlobalExceptionHandler` để gom các ngoại lệ xử lý. Tuy nhiên những gì ta nhận được lại chỉ là một message. 

Tôi muốn ngay cả khi xảy ra ngoại lệ, response trả về cũng phải theo cấu trúc `ApiResponse` ở trên.

Hãy lấy ví dụ về phương thức `getUserById()`:

```java
public User getUserById(String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found."));
}
```

Giờ tôi sẽ thay đổi một chút ở phương thức `handlingRuntimeException()` để có thể trả về response chứa `code` và `message`, không cần trả về `result` vì trong trường hợp này là lỗi:

```java
    // Xử lý những ngoại lệ RuntimeException
@ExceptionHandler(value = RuntimeException.class)
public ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
    ApiResponse apiResponse = new ApiResponse<>();

    apiResponse.setCode(1001);
    apiResponse.setMessage(exception.getMessage());

    return ResponseEntity.badRequest().body(apiResponse);
}
```

Giải thích:
* Thay đổi đối tượng trả về là `ApiResponse` thay vì `String`.
* `setCode(1001)`, ta tạm quy ước `1001` là mã lỗi dành cho việc không tìm thấy `User`.

Bây giờ thử gửi một request với `id` không đúng xem kết quả thế nào:

```json
{
    "code": 1001,
    "message": "User not found."
}
```

Ok, vậy là đã đúng với cấu trúc ta muốn.

Tuy nhiên có một vấn đề ở đây, nếu ta để nguyên phương thức `handlingRuntimeException()` như thế kia, thì bất cứ lỗi nào cũng đều có mã là `1001`.

Ví dụ như ngoại lệ trùng `username`:

```json
{
    "code": 1001, // Vẫn là mã 1001
    "message": "Username existed."
}
```

Chúng ta nên xử lý như thế nào? Ta không thể viết lặp đi lặp lại phương thức `handlingRuntimeException()` với tất cả các mã code tương ứng với mã lỗi được.

### Khởi tạo enum `ErrorCode`

Câu trả lời cho câu hỏi trên chính là danh sách enum `ErrorCode`. Ta sẽ chứa tất cả các mã code về lỗi ở đây, sau đó chỉ cần lấy ra là xong.

```java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_EXISTED(1001, "User existed."),
    EMAIL_EXISTED(1002, "Email existed."),
    USER_NOT_FOUND(1003, "User not found."),
    INVALID_USERNAME(1004, "Username must be at least 3 characters."),
    INVALID_PASSWORD(1005, "Password must be at least 8 characters."),
    INVALID_EMAIL(1006, "Invalid email.")
    ;
    private int code;
    private String message;
}
```

Giải thích:
* ErrorCode sẽ có nội dung bao gồm cả `code` và `message` tương ứng.

Tuy nhiên thì ta lại có một vấn đề nữa, ấy là `RuntimeException` chỉ trả về `message`, trong khi đó ta cần có cả mã code nữa. 

Vậy nên ta sẽ tạo ra một class `RuntimeException` của riêng ta.

### Khởi tạo `AppException`:

class `AppException` sẽ như sau:

```java
@AllArgsConstructor
@Getter
@Setter
public class AppException extends RuntimeException{
    private ErrorCode errorCode;
}
```

Giờ ta đổi lại `RuntimeException` thành `AppException` trong class `UserSerivce`, ví dụ với `user not found`:

```java
public User getUserById(String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
}
```

Và để chương trình có thể xử lý được `AppException` thì ta cần phải khai báo trong `GlobalExceptionHandler`:

```java
@ExceptionHandler(value = AppException.class)
public ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
    ErrorCode errorCode = exception.getErrorCode();

    ApiResponse apiResponse = new ApiResponse<>();

    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(errorCode.getMessage());

    return ResponseEntity.badRequest().body(apiResponse);
}
```

Giải thích:
* Ta cần lấy ra được `ErrorCode` từ `exception` truyền vào, sau đó lấy ra được `code` và `message` từ `ErrorCode` tương ứng.

Kết quả:

```json
{
    "code": 1003,
    "message": "User not found."
}
```

### Không rơi vào trường hợp nào đã được định nghĩa

Yep! Có khả năng xảy ra một ngoại lệ trong quá trình chạy chương trình, mà ta không thể lường trước được. Vì vậy chúng ta cần một phương thức xử lý những ngoại lệ không nằm trong những trường hợp ta đã định nghĩa.

`ErrorCode` cho trường hợp này sẽ là:

```java
UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error."),
```

Tạo riêng một phương thức trong `GlobalExceptionHandler`:

```java
// Trường hợp không rơi vào các exception đã được định nghĩa
@ExceptionHandler(value = Exception.class)
public ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
    ApiResponse apiResponse = new ApiResponse<>();
    ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(errorCode.getMessage());v

    return ResponseEntity.badRequest().body(apiResponse);
}
```
### Xử lý những ngoại lệ có đối số không hợp lệ

Với phần đối số, ta sẽ thay đổi `message` thành các `String` tương ứng với enum `ErrorCode`:

```java
public class UserCreationRequest {
    @Size(min = 3, message = "INVALID_USERNAME")
    private String username;
    
    @Email(message = "INVALID_EMAIL")
    private String email;

    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;

    // Các thuộc tính khác
}
```

Bây giờ ta sửa lại phương thức xử lý exception:

```java
// Xử lý những ngoại lệ đối số không hợp lệ
@ExceptionHandler(value = MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

    String enumKey = exception.getFieldError().getDefaultMessage(); // Lấy ra đoạn chuỗi, ví dụ "INVALID_USERNAME"
    ErrorCode errorCode = ErrorCode.valueOf(enumKey);
    ApiResponse apiResponse = new ApiResponse<>();

    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(errorCode.getMessage());

    return ResponseEntity.badRequest().body(apiResponse);
}
```

Nhìn chung là chúng ta đã gần xong rồi, tuy nhiên vẫn còn một vấn đề nhỏ nữa.

### Nếu `message` của thuộc tính viết sai?
Ấy là nếu trong trường hợp ta lỡ tay viết sai `message` trong thuộc tính của class request thì sao?
Ví dụ: 

```java
public class UserCreationRequest {
    @Size(min = 3, message = "INVALID_USENAME") // Thiếu một chữ R
    private String username;
    
    @Email(message = "INVALID_EMAIL")
    private String email;

    @Size(min = 8, message = "INVALID_PASWORD") // Thiếu một chữ S
    private String password;

    // Các thuộc tính khác
}
```

Ta sẽ tạo thêm một mã `ErrorCode` cho trường hợp này, để biết rằng có sai sót trong `message` của thuộc tính:

```java
INVALID_MESSAGE(8888, "Invalid message key.")
```

Rồi giờ ta sửa lại phương thức xử lý đối số:
```java
// Xử lý những ngoại lệ đối số không hợp lệ
@ExceptionHandler(value = MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

    String enumKey = exception.getFieldError().getDefaultMessage(); // Lấy ra đoạn chuỗi, ví dụ "INVALID_USERNAME"
    ErrorCode errorCode = ErrorCode.INVALID_MESSAGE;
    ApiResponse apiResponse = new ApiResponse<>();

    // Thử xem enumKey có nằm trong danh sách enum của ErrorCode không, 
    // nếu có thì gán enum mới cho biến errorCode,
    // nếu không thì errorCode vẫn giữ giá trị là INVALID_MESSAGE
    try {
        errorCode = ErrorCode.valueOf(enumKey);
    } catch (IllegalArgumentException e) {
        
    }

    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(errorCode.getMessage());

    return ResponseEntity.badRequest().body(apiResponse);
}
```