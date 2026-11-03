# Bài tập tuần 6 - Lập trình thiết bị di động (Xử lý mạng và gọi API)

- **Họ tên:** Võ Văn Khương Thịnh
- **MSSV:** 049206001215
- **Lớp:** 012012103402

## Đề bài – Thực hành 2
Hiểu và áp dụng các khái niệm liên quan đến việc xử lý API: gọi API lấy dữ liệu về và hiển thị lên màn **Product detail** (UTH SmartTasks).

```
GET | https://mock.apidog.com/m1/890655-872447-default/v2/product
```

Dữ liệu trả về (mock, mỗi lần gọi là một sản phẩm ngẫu nhiên):
```json
{
  "name": "Ergonomic Plastic Hat",
  "id": "30979047-e976-4c5e-998d-bb49ea480e1c",
  "des": "The slim & simple Maple Gaming Keyboard ...",
  "price": 780.65,
  "imgURL": "https://avatars.githubusercontent.com/u/25746425"
}
```

## Công nghệ sử dụng
- Kotlin, Jetpack Compose (Material 3)
- **Retrofit + Gson**: gửi HTTP GET và chuyển JSON thành `data class Product`
- **Coroutine + ViewModel (StateFlow)**: gọi mạng không chặn giao diện, giữ dữ liệu khi xoay màn hình
- **Coil**: tải ảnh sản phẩm từ `imgURL`

## Danh sách file

| File | Nội dung |
|---|---|
| `BT01.kt` | Model `Product`, interface `ProductApi` (Retrofit), `ProductViewModel` với 3 trạng thái Loading / Success / Error, màn `ProductDetailScreen` |
| `MainActivity.kt` | Mở màn Product detail, nút Back thoát app |
| `app.build.gradle.kts` | Nội dung file `app/build.gradle.kts` (thêm Retrofit, Gson, Coil, lifecycle-viewmodel-compose) |
| `AndroidManifest.xml` | Quyền `INTERNET` |

## Luồng xử lý
1. Mở màn hình → `ProductViewModel` gọi `RetrofitClient.api.getProduct()` trong `viewModelScope`, giao diện hiện vòng xoay **Loading**.
2. Thành công → **Success**: hiện ảnh (bo góc), tên, giá màu đỏ (định dạng `780,65₫`), mô tả trong khung xám.
3. Lỗi → **Error**: báo "Không có kết nối mạng" (`IOException`) hoặc "Máy chủ trả về lỗi xxx" (`HttpException`), kèm nút **Thử lại**.
4. Nút ⟳ trên thanh tiêu đề gọi lại API, vì là mock nên mỗi lần sẽ ra sản phẩm khác.

## Hướng dẫn chạy trên Android Studio
1. Tạo project **Empty Activity** với package `com.example.tuan_6`.
2. Thay `app/build.gradle.kts` bằng `app.build.gradle.kts`, bấm **Sync Now**.
3. Thay `app/src/main/AndroidManifest.xml` bằng `AndroidManifest.xml` trong thư mục này.
4. Sao chép `BT01.kt`, `MainActivity.kt` vào `app/src/main/java/com/example/tuan_6/` (xóa thư mục `ui/theme` mặc định nếu báo trùng tên), nhấn Run (▶).

Kiểm tra trường hợp lỗi: bật chế độ máy bay trên emulator rồi bấm ⟳ → hiện thông báo lỗi và nút **Thử lại**.
