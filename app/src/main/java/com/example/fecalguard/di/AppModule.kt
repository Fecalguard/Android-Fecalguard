package com.example.fecalguard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.example.fecalguard.BuildConfig
import com.example.fecalguard.data.local.UserPreference
import com.example.fecalguard.data.local.dataStore
import com.example.fecalguard.data.local.database.DiseaseDao
import com.example.fecalguard.data.local.database.DiseaseImageDao
import com.example.fecalguard.data.local.database.DiseaseRoomDatabase
import com.example.fecalguard.data.local.database.MedicineDao
import com.example.fecalguard.data.remote.retrofit.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreference: UserPreference): Interceptor {
        return Interceptor { chain ->
            val token = runBlocking { userPreference.getSession().first().token }

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)


        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : DiseaseRoomDatabase {
        return Room.databaseBuilder(context.applicationContext, DiseaseRoomDatabase::class.java, "DiseaseDB")
            .createFromAsset("database/disease.db")
            .build()
    }

    @Provides
    fun provideDiseaseDao(db: DiseaseRoomDatabase): DiseaseDao {
        return db.diseaseDao()
    }

    @Provides
    fun provideDiseaseImageDao(db: DiseaseRoomDatabase): DiseaseImageDao {
        return db.diseaseImageDao()
    }

    @Provides
    fun provideMedicineDao(db: DiseaseRoomDatabase): MedicineDao {
        return db.medicineDao()
    }

//    @Provides
//    @Singleton
//    fun provideDatabase(
//        @ApplicationContext context: Context,
//        callback: DiseaseRoomDatabase.DatabaseCallback
//    ): DiseaseRoomDatabase {
//        return Room.databaseBuilder(context, DiseaseRoomDatabase::class.java, "disease_database")
//            .addCallback(callback)
//            .build()
//    }
//
//    @Provides
//    fun provideDiseaseDao(db: DiseaseRoomDatabase): DiseaseDao = db.diseaseDao()

}

//                    val disease = Diseases(
//                        diseaseName = "Coccidiosis",
//                        symptoms = """
//                    <p>Ciri-Ciri Ayam yang Mengidap Penyakit</p>
//                    <ul>
//                        <li><b>1. Diare Berdarah atau Berair:</b> Gejala paling khas adalah feses yang bercampur darah atau berair. Hal ini disebabkan oleh kerusakan pada lapisan usus akibat infeksi parasit <i>Eimeria</i>, terutama <i>E. tenella</i>.</li>
//                        <li><b>2. Lesu dan Tidak Aktif:</b> Ayam tampak lemah, malas bergerak, dan lebih banyak diam di sudut kandang. Mereka juga cenderung mengantuk dan tidak responsif terhadap rangsangan.</li>
//                        <li><b>3. Nafsu Makan Menurun:</b> Ayam yang terinfeksi menunjukkan penurunan nafsu makan, yang mengarah pada penurunan berat badan dan pertumbuhan yang terhambat.</li>
//                        <li><b>4. Bulu Kusut dan Kotor:</b> Karena ayam tidak aktif membersihkan bulunya, bulu menjadi kusut dan tampak kotor. Ini juga merupakan tanda bahwa ayam merasa tidak nyaman atau sakit.</li>
//                        <li><b>5. Jengger dan Pial Pucat:</b> Anemia akibat kehilangan darah dari usus yang rusak menyebabkan jengger dan pial ayam tampak pucat.</li>
//                        <li><b>6. Dehidrasi:</b> Diare yang parah menyebabkan kehilangan cairan tubuh, mengakibatkan dehidrasi. Ayam mungkin terlihat lemah dan kulitnya kehilangan elastisitas.</li>
//                        <li><b>7. Pertumbuhan Terhambat:</b> Terutama pada ayam muda, coccidiosis menyebabkan gangguan pertumbuhan dan dapat berujung pada kematian jika tidak ditangani dengan cepat.</li>
//                    </ul>
//                """.trimIndent(),
//                        sideEffects = """
//                    <p>Efek Samping Penyakit</p>
//                    <ul>
//                        <li><b>1. Penurunan Produksi Telur:</b> Ayam betina yang terinfeksi coccidiosis dapat mengalami penurunan produksi telur secara signifikan.</li>
//                        <li><b>2. Kematian:</b> Jika tidak ditangani, coccidiosis dapat menyebabkan kematian, terutama pada ayam muda.</li>
//                        <li><b>3. Penyebaran Cepat:</b> Penyakit ini sangat menular dan dapat menyebar dengan cepat melalui feses yang terkontaminasi.</li>
//                    </ul>
//                """.trimIndent(),
//                        quickTreatment = """
//                    <p>Solusi Penanganan Cepat</p>
//                    <ul>
//                        <li><b>1. Isolasi Ayam yang Terinfeksi:</b> Segera pisahkan ayam yang menunjukkan gejala untuk mencegah penyebaran ke ayam lain.</li>
//                        <li><b>2. Pemberian Obat Anticoccidial:</b> Gunakan obat seperti Amprolium sesuai dosis yang dianjurkan untuk mengobati infeksi.</li>
//                        <li><b>3. Pembersihan Kandang:</b> Bersihkan dan desinfeksi kandang secara menyeluruh untuk menghilangkan oocyst coccidia dari lingkungan.</li>
//                    </ul>
//                """.trimIndent(),
//                        longTermSolution = """
//                    <p>Solusi Berkepanjangan</p>
//                    <ul>
//                        <li><b>1. Pemberian Vaksin:</b> Vaksinasi ayam muda dapat membantu membangun kekebalan terhadap coccidiosis.</li>
//                        <li><b>2. Manajemen Kandang yang Baik:</b> Pastikan kandang memiliki ventilasi yang baik, tidak lembab, dan tidak terlalu padat untuk mencegah penyebaran penyakit.</li>
//                        <li><b>3. Pemberian Probiotik:</b> Probiotik dapat membantu menjaga kesehatan usus ayam dan meningkatkan sistem kekebalan tubuh mereka.</li>
//                    </ul>
//                """.trimIndent()
//                    )
//                    val diseaseId = dao.insertDisease(disease).toInt()
//                    dao.insertDiseaseImages(
//                        listOf(
//                            DiseaseImages(
//                                diseaseId = diseaseId,
//                                imageText = "Ayam dengan Bulu Kusut dan Lesu",
//                                imageResId = R.drawable.coccidiosis_1
//                            ),
//                            DiseaseImages(
//                                diseaseId = diseaseId,
//                                imageText = "Ayam dengan Postur Membungkuk dan Tidak Aktif",
//                                imageResId = R.drawable.coccidiosis_2
//                            ),
//                            DiseaseImages(
//                                diseaseId = diseaseId,
//                                imageText = "Ayam dengan Bulu Kotor dan Tampak Sakit",
//                                imageResId = R.drawable.coccidiosis_3
//                            )
//                        )
//                    )
//                    dao.insertMedicines(
//                        listOf(
//                            Medicine(
//                                diseaseId = diseaseId,
//                                medicineName = "AMPROL PLUS 100 GRAM",
//                                medicineLink = "https://tk.tokopedia.com/ZSkYWpmWj/"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId,
//                                medicineName = "Toltraplus - toltrazuril 5% Obat Koksidiosis ",
//                                medicineLink = "https://id.shp.ee/URNP3M2"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId,
//                                medicineName = "VITA CHICKS 5GR VITACHICK MEDION",
//                                medicineLink = "https://tk.tokopedia.com/ZSkYcDKJT/"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId,
//                                medicineName = "Super elektrolit 100gram ",
//                                medicineLink = "https://tk.tokopedia.com/ZSkY3UYwD/"
//                            )
//                        )
//                    )
//
//                    val disease1 = Diseases(
//                        diseaseName = "Salmonella/Berak Kapur",
//                        symptoms = """
//                    <p>Ciri-Ciri Ayam yang Mengidap Penyakit</p>
//                    <ul>
//                        <li><b>1. Diare berwarna putih seperti kapur:</b> Feses cair berwarna putih atau coklat kehijauan, sering disebut sebagai "feses kapur" atau "berak kapur".</li>
//                        <li><b>2. Penurunan nafsu makan:</b> Ayam menjadi lesu dan tidak tertarik pada pakan.</li>
//                        <li><b>3. Demam ringan:</b> Meningkatkan suhu tubuh ayam.</li>
//                        <li><b>4. Lesu dan sayap menggantung:</b> Ayam tampak lemah dan tidak aktif.</li>
//                        <li><b>5. Bulu kusam dan kotor:</b> Kondisi tubuh yang tidak terawat.</li>
//                        <li><b>6. Kematian mendadak pada ayam muda:</b> Terutama pada ayam usia 1–10 hari.</li>
//                    </ul>
//                """.trimIndent(),
//                        sideEffects = """
//                    <p>Efek Samping Penyakit</p>
//                    <ul>
//                        <li><b>1. Penurunan produktivitas:</b> Menurunnya produksi telur dan kualitas daging.</li>
//                        <li><b>2. Kematian mendadak:</b> Terutama pada ayam muda, dapat menyebabkan kerugian ekonomi.</li>
//                        <li><b>3. Penurunan daya tahan tubuh:</b> Ayam menjadi lebih rentan terhadap penyakit lain.</li>
//                        <li><b>4. Penyebaran cepat:</b> Bakteri mudah menyebar melalui feses dan peralatan kandang yang terkontaminasi.</li>
//                    </ul>
//                """.trimIndent(),
//                        quickTreatment = """
//                    <p>Solusi Penanganan Cepat</p>
//                    <ul>
//                        <li><b>1. Isolasi ayam yang terinfeksi:</b> Pisahkan ayam yang menunjukkan gejala untuk mencegah penyebaran.</li>
//                        <li><b>2. Pembersihan dan desinfeksi kandang:</b> Gunakan desinfektan yang efektif untuk membunuh bakteri.</li>
//                        <li><b>3. Pemberian antibiotik:</b> Obat seperti amoxicillin, fluoroquinolones, dan tetracyclines dapat digunakan sesuai petunjuk dokter hewan.</li>
//                        <li><b>4. Pemberian cairan elektrolit:</b> Untuk mencegah dehidrasi akibat diare.</li>
//                    </ul>
//                """.trimIndent(),
//                        longTermSolution = """
//                    <p>Solusi Berkepanjangan</p>
//                    <ul>
//                        <li><b>1. Vaksinasi rutin:</b> Memberikan vaksin untuk meningkatkan kekebalan tubuh ayam terhadap Salmonella.</li>
//                        <li><b>2. Manajemen kandang yang baik:</b> Menjaga kebersihan kandang dan peralatan secara berkala.</li>
//                        <li><b>3. Penyediaan pakan dan air bersih:</b> Menghindari kontaminasi dari pakan dan air minum.</li>
//                        <li><b>4. Pemantauan kesehatan rutin:</b> Melakukan pemeriksaan kesehatan secara berkala untuk deteksi dini.</li>
//                    </ul>
//                """.trimIndent()
//                    )
//                    val diseaseId2 = dao.insertDisease(disease1).toInt()
//                    dao.insertDiseaseImages(
//                        listOf(
//                            DiseaseImages(
//                                diseaseId = diseaseId2,
//                                imageText = "Ayam terindikasi salmonella",
//                                imageResId = R.drawable.salmonella_1
//                            )
//                        )
//                    )
//                    dao.insertMedicines(
//                        listOf(
//                            Medicine(
//                                diseaseId = diseaseId2,
//                                medicineName = "MEDION AMOXITIN 100 g",
//                                medicineLink = "https://www.tokopedia.com/saranaproduksiternak/medion-amoxitin-100-g-obat-ayam-ngorok-pilek-antibiotik"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId2,
//                                medicineName = "AGRIXINE SOLUTION 100ML",
//                                medicineLink = "https://www.lazada.co.id/products/agrixine-solution-100ml-obat-ayam-crd-snot-pilek-korisa-kolera-ampuh-unggas-i8010620764.html"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId2,
//                                medicineName = "Probiotik Ayam",
//                                medicineLink = "https://tk.tokopedia.com/ZSkYcDKJT/"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId2,
//                                medicineName = "VITA CHICKS 5GR VITACHICK MEDION",
//                                medicineLink = "https://tk.tokopedia.com/ZSkYcDKJT/"
//                            )
//                        )
//                    )
//
//                    val disease2 = Diseases(
//                        diseaseName = "NewCastle Disease/Tetelo",
//                        symptoms = """
//                    <p>Ciri-Ciri Ayam yang Mengidap Penyakit</p>
//                    <ul>
//                        <li>1. Tubuh ayam terlihat pucat, lesu, dan lemas.</li>
//                        <li>2. Lendir berlebihan di Trakea.</li>
//                        <li>3. Pernafasan ayam terganggu, umumnya ayam akan sering bersin-bersin, ngorok, batuk dan nafas tersendat..</li>
//                        <li>4. Bagi ayam betina, akan menurunkan produktivitas telur.</li>
//                        <li>5. Kotoran encer dan berwarna kehijauan.</li>
//                        <li>6. Kornea mata ayam terlihat keruh.</li>
//                        <li>7. Jengger berwarna biru dan sayam akan menurun.</li>
//                        <li>8. Untuk kondisi yang sudah sangat parah, ayam dapat mengalami kelumpuhan saraf sehingga menyebabkan kejang dan leher terpelintir ke bawah.</li>
//                    </ul>
//                """.trimIndent(),
//                        sideEffects = """
//                    <p>Efek Samping Penyakit</p>
//                    <ul>
//                        <li><b>1. Penyakit sangat menular:</b> Risiko penyebaran wabah ke peternakan lain sangat tinggi.</li>
//                        <li><b>2. Kematian tinggi:</b> Dapat menyebabkan tingkat kematian yang signifikan pada ayam.</li>
//                        <li><b>3. Hambatan pertumbuhan dan penurunan kualitas produk:</b> Termasuk penurunan produksi telur dan daya tetas.</li>
//                        <li><b>4. Kerugian ekonomi:</b> Penurunan hasil ternak mengakibatkan kerugian bagi peternak.</li>
//                        <li><b>5. Biaya tinggi:</b> Pengeluaran besar untuk pengobatan dan pencegahan penyakit.</li>
//                    </ul>
//                """.trimIndent(),
//                        quickTreatment = """
//                    <p>Solusi Penanganan Cepat</p>
//                        <ul>
//                            <li><b>1. Pemusnahan unggas sakit:</b> Ayam yang terdiagnosa menderita Tetelo harus dibunuh dan dibakar untuk mencegah penyebaran virus.</li>
//                            <li><b>2. Isolasi:</b> Ayam yang terdiagnosa menderita Tetelo harus dipisahkan dari ayam lain dan ditempatkan dalam kandang yang terisolasi untuk mencegah penyebaran virus.</li>
//                            <li><b>3. Vaksinasi:</b> Vaksinasi merupakan cara yang efektif untuk mencegah infeksi Tetelo. Selain itu, pemberian vaksin juga dapat mengurangi gejala yang muncul pada ayam yang telah terinfeksi.</li>
//                            <li><b>4. Pengendalian burung liar:</b> Pengendalian burung liar yang menyebar virus Tetelo harus dilakukan dengan baik, seperti dengan menangkap atau membunuh burung liar yang terjangkit.</li>
//                        </ul>
//                """.trimIndent(),
//                        longTermSolution = """
//                    <p>Solusi Berkepanjangan</p>
//                    <ul>
//                        <li><b>1. Penerapan Sistem Sanitasi dan Biosecurity:</b>
//                            <ul>
//                                <li>Penyemprotan dengan desinfektan setiap pagi dan sore pada masa ada ayam produksi akan membantu menurunkan konsentrasi virus ND di sekitar kandang.</li>
//                                <li>Meningkatkan pola kebersihan pada peralatan, kotoran, dan vektor penyakit terutama kutu dan tikus saat kosong kandang akan sangat membantu menurunkan keganasan virus ND di sekitar kandang.</li>
//                                <li>Pengendalian akses: Mengontrol akses ke kandang dengan mengurangi jumlah orang yang diperbolehkan masuk dan melakukan pemeriksaan riwayat perjalanan pada semua yang masuk.</li>
//                                <li>Edukasi dan pelatihan: Memberikan edukasi dan pelatihan kepada peternak dan pekerja kandang tentang teknik biosecurity dan sanitasi yang tepat serta cara mengendalikan infeksi.</li>
//                            </ul>
//                        </li>
//                        <li><b>2. Program Vaksinasi yang Ketat dan Tepat:</b>
//                            <ul>
//                                <li><b>Vaksin ND Live di Awal:</b> Perlu diberikan sejak awal kehidupan ayam karena kekebalan lokal dari induk sangat minim. Metode spray direkomendasikan karena bisa merangsang kekebalan lokal lebih cepat dan efektif.</li>
//                                <li><b>Vaksin ND Kill di Awal:</b> Diberikan dengan dosis penuh dan kadar virus tinggi untuk membantu kekebalan tubuh ayam meskipun masih ada kekebalan dari induk.</li>
//                                <li><b>Booster ND Live (Lasota) di Umur 14–16 Hari:</b> Disarankan karena tantangan penyakit datang lebih cepat. Booster ini penting untuk perlindungan optimal.</li>
//                                <li><b>Booster Kedua dengan ND Lasota:</b> Booster kedua ini efektif karena cepat menyebar dan kuat merangsang kekebalan. Kombinasi ND Live + ND Kill + booster ND Lasota terbukti lebih baik untuk melawan virus ND Genotipe VII.</li>
//                                <li><b>Catatan:</b> Semua vaksin ND bisa melindungi terhadap Genotipe VII karena virus ND hanya memiliki satu serotipe.</li>
//                            </ul>
//                        </li>
//                        <li><b>3. Mempertahankan Kenyamanan Ayam di Umur 2–3 Minggu:</b>
//                            <ul>
//                                <li>Memaksimalkan performa ayam pada periode ini untuk memperoleh sistem imunitas yang optimal.</li>
//                                <li>Menjaga kualitas litter untuk meminimalkan konsentrasi amonia dan mengurangi stres ayam.</li>
//                                <li>Mempercepat pelebaran kandang untuk mengurangi stres akibat kepadatan.</li>
//                                <li>Menurunkan sekam secara bertahap dan terkontrol untuk mengurangi stres eksternal.</li>
//                                <li>Memberikan bahan supportive seperti sorbitol lewat air minum untuk meningkatkan energi ayam.</li>
//                                <li>Memberikan pakan berkualitas dan pola pemberian yang tepat untuk mendukung sistem imun ayam.</li>
//                                <li>Menyuntikkan vaksin yang sesuai untuk mencegah dan mengatasi infeksi virus ND.</li>
//                            </ul>
//                        </li>
//                    </ul>
//                """.trimIndent()
//                    )
//                    val diseaseId3 = dao.insertDisease(disease2).toInt()
//                    dao.insertDiseaseImages(
//                        listOf(
//                            DiseaseImages(
//                                diseaseId = diseaseId3,
//                                imageText = "Tortikolis",
//                                imageResId = R.drawable.newcastle_1
//                            ),
//                            DiseaseImages(
//                                diseaseId = diseaseId3,
//                                imageText = "Pembengkakan dan hemoragi pada daerah mata",
//                                imageResId = R.drawable.newcastle_2
//                            ),
//                            DiseaseImages(
//                                diseaseId = diseaseId3,
//                                imageText = "Pembengkakan pada kelopak mata",
//                                imageResId = R.drawable.newcastle_3
//                            )
//                        )
//                    )
//                    dao.insertMedicines(
//                        listOf(
//                            Medicine(
//                                diseaseId = diseaseId3,
//                                medicineName = "VAKSIN MEDIVAC ND IB DOSIS 500 EKOR + PELARUT MEDIVAC MEDION",
//                                medicineLink = "https://s.lazada.co.id/s.Zbre7o"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId3,
//                                medicineName = "LS/H120 ND IB LIVE SANAVAC dosis 1000",
//                                medicineLink = "https://id.shp.ee/YFegTsh"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId3,
//                                medicineName = "Vaksinasi Vaksin Ayam ND KILL ND CLONE 45 Dosis 500 Ayam",
//                                medicineLink = "https://tk.tokopedia.com/ZSkFmUYoP/"
//                            ),
//                            Medicine(
//                                diseaseId = diseaseId3,
//                                medicineName = "SORBITOL PLUS Cairan Pengganti Gula DOC Ayam",
//                                medicineLink = "https://id.shp.ee/oz83wCe"
//                            )
//                        )
//                    )
//                }
//            }
//        }
//    }

