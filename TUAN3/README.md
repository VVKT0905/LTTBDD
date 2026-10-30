# Bài tập tuần 3 - Lập trình thiết bị di động

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
| `BT01.kt` | Thực hành 1 | UTH Navigation: 3 màn hình Root → List (LazyColumn) → Detail | `NavHost`, `navigate`, `popBackStack`, truyền tham số qua route |
| `BT02.kt` | Thực hành 2 | So sánh Column và LazyColumn khi hiển thị 1.000.000 dòng sinh ngẫu nhiên | `LazyColumn`, `items(count)`, `verticalScroll` |
| `MainActivity.kt` | Điều hướng | Chọn bài để chạy | MaterialTheme, ComponentActivity |

## Kết quả mong đợi

**BT01 – Navigation + LazyColumn**
- Root → bấm **PUSH** → List (1.000.000 dòng) → bấm một dòng → Detail hiển thị câu nói và số dòng đã chọn.
- Detail → nút quay lại → List → nút quay lại → Root.
- Detail → bấm **BACK TO ROOT** → về thẳng Root (dùng `popBackStack("root", inclusive = false)` nên List và Detail bị xóa khỏi back stack, bấm Back ở Root sẽ thoát app).

**BT02 – Column vs LazyColumn**
- Màn **Choose List Type** có 2 nút.
- **LazyColumn (1,000,000 items):** mở ngay, cuộn mượt vì chỉ tạo các dòng đang hiện trên màn hình.
- **Column (1,000,000 items):** app bị treo rất lâu và có thể bị crash (hết bộ nhớ / ANR) vì Column tạo cùng lúc cả 1.000.000 dòng. Đây chính là điểm bài muốn chứng minh.
- Nội dung mỗi dòng được chọn ngẫu nhiên với seed là số thứ tự dòng, nên không phải lưu sẵn 1.000.000 chuỗi trong bộ nhớ và cuộn qua lại vẫn ra cùng nội dung.

## Hướng dẫn chạy trên Android Studio

1. Tạo project **Empty Activity** với package `com.example.tuan_3`.
2. Trong `app/build.gradle.kts`, thêm vào khối `dependencies` rồi bấm **Sync Now**:
   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.8.9")
   implementation("androidx.compose.material:material-icons-extended")
   ```
3. Sao chép `BT01.kt`, `BT02.kt`, `MainActivity.kt` vào `app/src/main/java/com/example/tuan_3/`.
4. Trong `MainActivity.kt`, bỏ comment dòng bài muốn chạy (`BT01Screen()` hoặc `BT02Screen()`).
5. Nhấn Run (▶).

> **Lưu ý:** `BT02.kt` dùng lại hằng số `SO_DONG` và màu `XanhChinh` khai báo trong `BT01.kt`, nên cần giữ cả 2 file trong cùng project.
