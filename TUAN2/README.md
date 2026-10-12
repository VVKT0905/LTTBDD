# Bài tập tuần 2 - Lập trình thiết bị di động

- **Họ tên:** Võ Văn Khương Thịnh
- **MSSV:** 049206001215
- **Lớp:** 012012103402

## Công nghệ sử dụng
- Ngôn ngữ: Kotlin
- Giao diện: Jetpack Compose (Material 3)
- Môi trường: Android Studio

## Danh sách các bài tập

| File | Bài | Nội dung | Kỹ năng rèn luyện |
|---|---|---|---|
| `BT01.kt` | Thực hành 3 | Máy tính đơn giản: nhập 2 số, chọn phép toán `+ - * /`, hiển thị kết quả | TextField, mutableStateOf, sự kiện click |
| `BT02.kt` | Bài 1 | Danh sách sinh viên, xử lý giá trị null với toán tử `?:` | Vòng lặp `for`, kiểu nullable `?`, Elvis operator |
| `BT03.kt` | Bài 2 | Xếp loại học lực từ điểm số | Câu lệnh `if / else`, hàm có giá trị trả về |
| `BT04.kt` | Bài 3 | Bảng điểm lớp: tính trung bình, đếm giỏi, chưa có điểm, điểm cao nhất | Tổng hợp `for` + `if` + nullable |
| `MainActivity.kt` | Điều hướng | Chọn bài để chạy | MaterialTheme, ComponentActivity |

## Kết quả mong đợi

- **BT01:** Nhập 4 và 4, bấm `+` hiển thị `Kết quả: 8`. Bấm chia cho 0 báo `Không thể chia cho 0`.
- **BT02:** 5 sinh viên, 2 người thiếu email. Cuối danh sách hiển thị `Tổng: 5 sinh viên` và `Thiếu email: 2`.
- **BT03:** Điểm 8.5 xếp loại Giỏi. Phần kiểm tra thêm: 9 (Giỏi), 7 (Khá), 5.5 (Trung bình), 3 (Yếu), 11 (Không hợp lệ).
- **BT04:** Bảng điểm lớp: Điểm trung bình 6.63, 1 Giỏi, 1 chưa có điểm, cao nhất 9.0.

## Hướng dẫn chạy trên Android Studio

1. Tạo một project **Empty Activity** trong Android Studio với package `com.example.tuan_2`.
2. Sao chép các file `BT01.kt`, `BT02.kt`, `BT03.kt`, `BT04.kt` và `MainActivity.kt` vào thư mục `app/src/main/java/com/example/tuan_2/`.
3. Trong `MainActivity.kt`, bỏ comment dòng bài tập bạn muốn kiểm tra (ví dụ: `BT01Screen()`).
4. Nhấn nút Run (▶) để chạy ứng dụng trên Emulator hoặc thiết bị thật.

> **Lưu ý:** `BT04.kt` sử dụng lại hàm `xepLoai()` và `mauChu()` từ `BT03.kt` (theo đúng yêu cầu của đề bài), vì vậy cần giữ cả 2 file trong cùng project.
