package com.example.rumahistimewa.ui.profile.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center / FAQ", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPrimary
                )
            )
        }
    ) { paddingValues ->
        val faqs = remember { getFaqList() }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "🏡 FAQ – Aplikasi Booking Villa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = RedPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(faqs.size) { index ->
                FaqItem(faq = faqs[index])
            }
        }
    }
}

@Composable
fun FaqItem(faq: Faq) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.Gray
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }
    }
}

data class Faq(val question: String, val answer: String)

fun getFaqList(): List<Faq> {
    return listOf(
        Faq(
            "1. Apa itu Rumah Istimewa?",
            "Rumah Istimewa adalah platform digital yang memudahkan Anda mencari, memesan, dan membayar villa berkualitas di berbagai destinasi favorit — mulai dari Bali, Lombok, hingga Jambi. Semua proses dilakukan secara aman, cepat, dan transparan dalam satu genggaman."
        ),
        Faq(
            "2. Bagaimana cara memesan villa?",
            """Mudah! Ikuti 4 langkah ini:

1. Login atau daftar akun di aplikasi.
2. Cari & pilih villa berdasarkan lokasi, harga, atau fasilitas.
3. Atur tanggal check-in & check-out, lalu isi data tamu.
4. Bayar via metode yang tersedia (e.g., Kartu Kredit, Gopay, dll).

✅ Setelah pembayaran sukses, Anda akan menerima konfirmasi & kode booking secara instan."""
        ),
        Faq(
            "3. Apakah saya bisa membatalkan pemesanan?",
            """Ya, Anda bisa membatalkan pemesanan sebelum tanggal check-in, dengan ketentuan:

- Jika dibatalkan ≥ 7 hari sebelum check-in: pengembalian 100% dana.
- Jika dibatalkan 3–6 hari sebelum check-in: pengembalian 50% dana.
- Jika dibatalkan < 3 hari sebelum check-in atau no-show: dana tidak dapat dikembalikan.

💡 Pembatalan dilakukan via menu “Riwayat Pemesanan” → pilih booking → “Batalkan”."""
        ),
        Faq(
            "4. Bagaimana cara membayar? Apakah aman?",
            """Kami bekerja sama dengan Midtrans, penyedia pembayaran terpercaya di Indonesia. Metode yang tersedia:

- Kartu Debit/Kredit (Visa/Mastercard)
- E-Wallet (Gopay, OVO, Dana, LinkAja)
- Transfer Bank (Virtual Account BCA, BNI, Mandiri, dll)

🔒 Semua transaksi di-enkripsi SSL dan tidak menyimpan data kartu Anda. Anda hanya akan menerima notifikasi sukses/gagal — tanpa risiko kebocoran data."""
        ),
        Faq(
            "5. Di mana saya bisa melihat riwayat pemesanan & bukti pembayaran?",
            """Buka menu “Transaksi Saya” di halaman profil. Di sana Anda bisa:

- Melihat status booking (Dikonfirmasi, Dibayar, Dibatalkan)
- Mengunduh detail pemesanan (PDF)
- Menampilkan bukti pembayaran digital
- Menghubungi pemilik villa (jika sudah dikonfirmasi)"""
        ),
        Faq(
            "6. Apakah saya bisa menyimpan villa favorit?",
            """Tentu! Tekan ikon ❤️ di halaman detail villa untuk menambahkannya ke Wishlist. Villa favorit Anda akan tersimpan dan bisa diakses kapan saja — bahkan saat offline."""
        ),
        Faq(
            "7. Bagaimana jika villa yang saya pesan tidak sesuai deskripsi?",
            """Kami menjamin akurasi informasi melalui verifikasi langsung ke pemilik. Namun, jika terjadi ketidaksesuaian:

1. Laporkan segera via “Laporan Masalah” di halaman booking.
2. Tim kami akan meninjau dalam 24 jam.
3. Jika terbukti, Anda berhak mendapat pengembalian dana penuh + kompensasi."""
        ),
        Faq(
            "8. Apakah ada biaya tambahan saat check-in?",
            """Tidak. Harga yang tertera di aplikasi sudah termasuk:

- Biaya menginap
- Pajak & layanan
- Biaya kebersihan (jika disebutkan di deskripsi)

⚠️ Biaya tambahan hanya berlaku jika Anda memesan extra service (e.g., sarapan, antar-jemput) — dan itu akan dikonfirmasi & disetujui sebelum pembayaran."""
        )
    )
}
