# Bài tập tuần 4 - Lập trình thiết bị di động

- **Họ tên:** Võ Văn Khương Thịnh
- **MSSV:** 049206001215
- **Lớp:** 012012103402

## Công nghệ sử dụng
- Ngôn ngữ: Kotlin
- Giao diện: Jetpack Compose (Material 3)
- Điều hướng: Navigation Compose
- Môi trường: Android Studio

## Danh sách các bài tập

| File | Bài | Nội dung | Kỹ năng rèn luyện |
|---|---|---|---|
| `BT01.kt` | BTVN 01 – UTH Todo V2 | Màn Start (splash) → Onboarding 3 bước → Trang chủ | Navigation, `popUpTo`, `sealed class`, `abstract class`, `data class`, đa hình |
| `BT02.kt` | Hệ thống Quản lý Thư viện | Danh sách sách, danh sách sinh viên, mượn/trả sách, xem thông tin sách | Lớp `ThuVien` quản lý dữ liệu, `mutableStateListOf`, thanh điều hướng đáy 3 tab |
| `BT03.kt` | SmartTasks – Quên mật khẩu | 4 màn hình truyền dữ liệu qua nhau, màn 4 gửi kết quả về màn 1 | Tham số route, `Uri.encode`, `savedStateHandle` để trả kết quả |
| `MainActivity.kt` | Điều hướng | Chọn bài để chạy | MaterialTheme, ComponentActivity |

## Kết quả mong đợi

**BT01 – UTH Todo V2**
- Màn Start màu xanh, thanh tiến độ chạy khoảng 2 giây rồi tự chuyển sang Onboarding.
- Onboarding 3 bước: "Mọi việc, gọn trong một chỗ" → "Chia theo Học tập và Cuộc sống" → "Nhắc đúng lúc". Nút **Tiếp tục** đổi thành **Bắt đầu ngay** ở bước cuối, **Bỏ qua** đi thẳng vào Trang chủ.
- Ở Trang chủ bấm Back sẽ thoát app (Start và Onboarding đã bị xóa khỏi back stack).
- OOP: `ManHinh` là `sealed class` chứa các route; `HinhMinhHoa` là lớp trừu tượng, 3 lớp con tự vẽ hình riêng; `BuocGioiThieu` là `data class`.

**BT02 – Quản lý Thư viện**
- Tab **Quản lý**: ô Sinh viên + nút **Thay đổi** để chọn sinh viên; khung "Danh sách sách" hiện sách sinh viên đó đang mượn (bỏ tick = trả sách, bấm vào tên = xem thông tin sách); nút **Thêm** để mượn sách (chỉ hiện sách chưa ai mượn).
- Tab **DS Sách**: thêm sách mới, xem danh sách kèm trạng thái "Còn trong thư viện" / "Đang mượn: …".
- Tab **Sinh viên**: thêm sinh viên mới, xem mỗi sinh viên đang mượn những sách nào.
- Dữ liệu mẫu: Nguyen Van A đang mượn Sách 01 và Sách 02 (giống hình đề bài).

**BT03 – SmartTasks quên mật khẩu**
- Màn 1 nhập email (kiểm tra định dạng) → Màn 2 nhập mã 5 số → Màn 3 nhập mật khẩu mới + nhập lại (tối thiểu 6 ký tự, phải khớp) → Màn 4 hiển thị lại email, mã, mật khẩu.
- Bấm **Submit** ở màn 4: quay về màn 1, các màn 2–4 bị xóa khỏi back stack, màn 1 hiện khung "Dữ liệu nhận từ màn hình 4" gồm email, OTP, password.
- Dữ liệu đi tới qua tham số route (`verify/{email}`, `reset/{email}/{otp}`, `confirm/{email}/{otp}/{pass}`), dữ liệu đi ngược về qua `savedStateHandle` của màn 1.

## Hướng dẫn chạy trên Android Studio

1. Tạo project **Empty Activity** với package `com.example.tuan_4`.
2. Trong `app/build.gradle.kts`, thêm vào khối `dependencies` rồi bấm **Sync Now**:
   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.8.9")
   implementation("androidx.compose.material:material-icons-extended")
   ```
3. Sao chép `BT01.kt`, `BT02.kt`, `BT03.kt`, `MainActivity.kt` vào `app/src/main/java/com/example/tuan_4/`.
4. Trong `MainActivity.kt`, bỏ comment dòng bài muốn chạy (`BT01Screen()`, `BT02Screen()` hoặc `BT03Screen()`).
5. Nhấn Run (▶).
