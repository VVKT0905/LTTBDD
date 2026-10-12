# Bài tập tuần 1 - Lập trình thiết bị di động

- **Họ tên:** Võ Văn Khương Thịnh
- **MSSV:** 049206001215
- **Lớp:** 012012103402

## Nội dung

- **Câu 1 và câu 2:** trả lời trong file `BT_TUAN1.docx` (trong thư mục `TaiLieu/`).
- **Câu 3:** ứng dụng có giao diện theo đề (mô tả bên dưới).

## Câu 3. Màn hình hồ sơ sinh viên

Giao diện gồm:
- Nút quay lại ở góc trên bên trái, nút chỉnh sửa ở góc trên bên phải.
- Ảnh đại diện hình tròn ở giữa màn hình.
- Họ tên (chữ đậm) và mã sinh viên (chữ xám) bên dưới ảnh.

**Công nghệ:** Kotlin, Jetpack Compose, Android Studio.

**Các file:**
- `MainActivity.kt`: code màn hình `ProfileScreen`.
- `avatar.png`: ảnh đại diện, đặt trong `app/src/main/res/drawable/`.

**Cách chạy:**
1. Tạo project **Empty Activity** với package `com.example.tuan_1`.
2. Chép `avatar.png` vào `app/src/main/res/drawable/`.
3. Trong `app/build.gradle.kts`, thêm dòng sau vào khối `dependencies` rồi bấm **Sync Now** (để dùng icon):
   ```kotlin
   implementation("androidx.compose.material:material-icons-core")
   ```
4. Thay nội dung `MainActivity.kt` bằng file trong thư mục này.
5. Bấm Run (▶) để chạy trên emulator.
