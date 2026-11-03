package com.example.tuan_6

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.text.NumberFormat
import java.util.Locale

// ===================== 1. MODEL =====================
// Khớp với JSON trả về:
// {"name":"...","id":"...","des":"...","price":780.65,"imgURL":"https://..."}
data class Product(
    val id: String,
    val name: String,
    val des: String,
    val price: Double,
    val imgURL: String
)

// ===================== 2. API (Retrofit) =====================
interface ProductApi {
    // GET | https://mock.apidog.com/m1/890655-872447-default/v2/product
    @GET("v2/product")
    suspend fun getProduct(): Product
}

object RetrofitClient {
    private const val BASE_URL = "https://mock.apidog.com/m1/890655-872447-default/"

    val api: ProductApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON -> Product
            .build()
            .create(ProductApi::class.java)
    }
}

// ===================== 3. TRẠNG THÁI MÀN HÌNH =====================
sealed interface ProductUiState {
    data object Loading : ProductUiState
    data class Success(val product: Product) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

// ===================== 4. VIEWMODEL =====================
class ProductViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        taiSanPham()
    }

    fun taiSanPham() {
        _uiState.value = ProductUiState.Loading
        // Gọi mạng trong coroutine; Retrofit tự chuyển sang luồng nền với hàm suspend
        viewModelScope.launch {
            _uiState.value = try {
                ProductUiState.Success(RetrofitClient.api.getProduct())
            } catch (e: java.io.IOException) {
                ProductUiState.Error("Không có kết nối mạng. Vui lòng kiểm tra Internet.")
            } catch (e: retrofit2.HttpException) {
                ProductUiState.Error("Máy chủ trả về lỗi ${e.code()}.")
            } catch (e: Exception) {
                ProductUiState.Error("Có lỗi xảy ra: ${e.message}")
            }
        }
    }
}

// ===================== 5. GIAO DIỆN =====================
private val XanhUth = Color(0xFF2196F3)
private val DoGia = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Product detail", color = XanhUth, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(XanhUth)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Gọi lại API (mock trả dữ liệu ngẫu nhiên mỗi lần)
                    IconButton(onClick = { viewModel.taiSanPham() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Tải lại", tint = XanhUth)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                is ProductUiState.Loading -> CircularProgressIndicator(color = XanhUth)
                is ProductUiState.Error -> ErrorView(s.message, onRetry = { viewModel.taiSanPham() })
                is ProductUiState.Success -> ProductContent(s.product)
            }
        }
    }
}

@Composable
private fun ProductContent(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Ảnh sản phẩm tải từ imgURL bằng Coil
        AsyncImage(
            model = product.imgURL,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEEEEEE))
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = product.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Giá: ${dinhDangGia(product.price)}",
            color = DoGia,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = product.des,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F2F2))
                .padding(12.dp)
        )
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(message, textAlign = TextAlign.Center, color = DoGia)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = XanhUth)
        ) { Text("Thử lại") }
    }
}

// 780.65 -> "780,65₫" (định dạng số kiểu Việt Nam như thiết kế)
private fun dinhDangGia(gia: Double): String =
    NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(gia) + "₫"
