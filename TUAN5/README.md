# Bài tập tuần 5 - Lập trình thiết bị di động (Firebase)

- **Họ tên:** Võ Văn Khương Thịnh
- **MSSV:** 049206001215
- **Lớp:** 012012103402

## Công nghệ sử dụng
- Ngôn ngữ: Kotlin, giao diện Jetpack Compose (Material 3), Navigation Compose
- Firebase: Authentication, Cloud Firestore, Analytics, Remote Config, Cloud Messaging, Crashlytics
- Đăng nhập Google: Credential Manager (`androidx.credentials` + `googleid`)

## Danh sách các bài tập

| File | Bài | Nội dung |
|---|---|---|
| `BT01.kt` | BTVN 01 – Firebase Authentication | Màn đăng nhập (Google, Email/Mật khẩu, dùng thử ẩn danh), báo lỗi bằng tiếng Việt, màn Hồ sơ và Đăng xuất |
| `BT02.kt` | BTVN 02 – Tích hợp Firebase | Trang chủ UTH Todo: lưu việc trên Firestore, ghi sự kiện Analytics, câu động viên từ Remote Config, nhận thông báo FCM, Crashlytics |
| `MainActivity.kt` | Điều hướng | `login` → `home` → `profile`, đã đăng nhập thì vào thẳng `home` |
| `app.build.gradle.kts` | Cấu hình | Nội dung file `app/build.gradle.kts` |
| `project.build.gradle.kts` | Cấu hình | Nội dung file `build.gradle.kts` ở thư mục gốc |
| `AndroidManifest.xml` | Cấu hình | Quyền Internet, thông báo, khai báo service nhận FCM |
| `firestore.rules` | Cấu hình | Luật bảo mật Firestore: mỗi người chỉ đọc/ghi việc của mình |

## Kết quả mong đợi

**BT01 – Authentication**
- Đăng nhập lỗi → hộp thoại "Đăng nhập thất bại" kèm lý do: sai email hoặc mật khẩu, email đã được đăng ký, mật khẩu quá yếu, không có mạng, hủy chọn tài khoản Google…
- Đăng nhập thành công → Toast "Đăng nhập thành công!" và vào Trang chủ. Bấm Back ở Trang chủ sẽ thoát app, không quay lại màn đăng nhập.
- Từ Trang chủ bấm vào avatar → màn Hồ sơ: tên, email, nhãn "Đã liên kết tài khoản Google" (nếu đăng nhập bằng Google), thống kê việc đã xong / đang chờ / % hoàn thành, nút **Đăng xuất**.
- Tắt app mở lại vẫn còn đăng nhập.

**BT02 – Các dịch vụ Firebase**

| Dịch vụ | Dùng ở đâu trong app | Cách kiểm tra |
|---|---|---|
| Authentication | Đăng nhập / đăng xuất | Console → Authentication → Users có tài khoản vừa tạo |
| Cloud Firestore | Thêm (nút +), tick hoàn thành, xóa việc. Lưu ở `users/{uid}/tasks` | Console → Firestore thấy dữ liệu. Sửa trực tiếp trên Console thì app đổi ngay (realtime) |
| Analytics | Sự kiện `login`, `them_viec`, `hoan_thanh_viec` | Console → Analytics → DebugView (xem lệnh bật debug bên dưới) |
| Remote Config | Câu nhỏ trong thẻ "Tiến độ hôm nay" | Tạo tham số `thong_diep_trang_chu` trên Console, Publish, mở lại app |
| Cloud Messaging | Bấm chuông ở Trang chủ để lấy FCM token; app đăng ký topic `uth_todo` | Console → Messaging → gửi thử tới token hoặc topic |
| Crashlytics | Nút "Thử Crashlytics" trong Hồ sơ | Bấm nút (app crash), mở lại app, vài phút sau thấy trên Console → Crashlytics |

Không làm **Storage** vì từ 10/2024 Firebase yêu cầu gói trả phí Blaze mới tạo được bucket mới. Cũng không làm **Realtime Database** vì đã dùng Firestore để lưu dữ liệu.

## Cài đặt Firebase (làm 1 lần)

1. Vào [Firebase Console](https://console.firebase.google.com) → **Add project** (bật Google Analytics khi được hỏi).
2. **Add app → Android**, package name: `com.example.tuan_5`. Điền thêm **SHA-1** (bắt buộc để đăng nhập Google). Lấy SHA-1: trong Android Studio mở terminal, chạy `./gradlew signingReport`, copy dòng `SHA1` của variant `debug`.
3. **Authentication → Sign-in method**: bật **Email/Password**, **Google**, **Anonymous**.
4. Tải **google-services.json** (sau khi đã bật Google ở bước 3, để file có web client ID) và đặt vào thư mục `app/`.
5. **Firestore Database → Create database** → tab **Rules**: dán nội dung `firestore.rules` → Publish.
6. (Tùy chọn) **Remote Config**: thêm tham số `thong_diep_trang_chu`, kiểu String, ví dụ "Đã hơn nửa chặng, cố lên!" → Publish.

## Hướng dẫn chạy trên Android Studio

1. Tạo project **Empty Activity** với package `com.example.tuan_5`.
2. Thay nội dung `build.gradle.kts` (thư mục gốc) bằng `project.build.gradle.kts`, và `app/build.gradle.kts` bằng `app.build.gradle.kts`. Bấm **Sync Now**.
3. Thay `app/src/main/AndroidManifest.xml` bằng file `AndroidManifest.xml` trong thư mục này.
4. Sao chép `BT01.kt`, `BT02.kt`, `MainActivity.kt` vào `app/src/main/java/com/example/tuan_5/` (xóa thư mục `ui/theme` mặc định nếu báo trùng tên).
5. Đặt `google-services.json` vào `app/`, nhấn Run (▶). Emulator cần có **Google Play** và đã đăng nhập một tài khoản Google để thử đăng nhập Google.

Xem sự kiện Analytics ngay lập tức (DebugView):
```bash
adb shell setprop debug.firebase.analytics.app com.example.tuan_5
```

> **Lưu ý:** `google-services.json` chứa thông tin project Firebase cá nhân, nên không đưa lên GitHub (đã nằm trong `.gitignore`).
