. Projenin Adı
Restoran Garson Sipariş Yönetim Sistemi (Android – Java – SQLite)
________________________________________
2. Amaç ve Hedef
Bu projenin amacı, bir restoranda:
•	Garsonların Android cihaz üzerinden masa bazlı, görsel menü destekli sipariş alabilmesini,
•	Admin kullanıcıların menü, garson, masa ve satış istatistiklerini yönetebilmesini
sağlayan bir mobil uygulama geliştirmektir.
Bu aşamada sistem:
•	Tek Android cihazda çalışır.
•	Tüm veriler lokal SQLite veritabanında tutulur.
•	İnternet bağlantısına ihtiyaç duymadan (offline) çalışabilir.
________________________________________
3. Kapsam
Uygulama iki ana rol içerir:
•	Admin
o	Garson kullanıcı yönetimi
o	Menü (kategori + ürün) yönetimi
o	Masa yönetimi
o	Satış raporlama/istatistikleri görüntüleme
•	Garson
o	Giriş yapma
o	Masaların durumunu görme
o	Masa için sipariş oluşturma, güncelleme, iptal etme
o	Aktif siparişleri görüntüleme ve “tamamlandı” olarak işaretleme
Ödeme alma, fiş/adisyon yazdırma, stok yönetimi, rezervasyon gibi özellikler bu versiyon kapsamı dışındadır.
________________________________________
4. Teknolojiler ve Mimari
•	Platform: Android
•	IDE: Android Studio
•	Programlama dili: Java
•	Veritabanı: SQLite (lokal, cihaz içi)
Basit katmanlı mimari hedeflenir:
•	UI katmanı: Activity / Fragment, Adapter’lar
•	İş mantığı katmanı: Manager / Service sınıfları
•	Veri erişim katmanı: SQLiteOpenHelper tabanlı DatabaseHelper, DAO sınıfları
________________________________________
5. Aktörler ve Roller
5.1. Admin
•	Sistemin yönetiminden sorumlu yetkili kullanıcıdır.
•	Görevleri:
o	Garson kullanıcı ekleme, güncelleme, pasif yapma/silme
o	Menü kategorilerini ve ürünleri yönetme
o	Masaları (masa sayısı, isimleri, aktif/pasif durumu) yönetme
o	Satış özetlerini ve istatistiklerini görüntüleme
5.2. Garson
•	Admin tarafından tanımlanan kullanıcıdır.
•	Görevleri:
o	Kullanıcı adı ve şifre ile sisteme giriş
o	Masaların durumunu görme (boş / aktif)
o	Masaya sipariş oluşturma, güncelleme, iptal etme
o	Aktif siparişleri listeleme ve siparişi “tamamlandı” olarak işaretleme
________________________________________
6. Fonksiyonel Gereksinimler
6.1. Kimlik Doğrulama ve Oturum Yönetimi
•	G1. Uygulama açıldığında LoginActivity gösterilir.
•	G2. Kullanıcı, kullanıcı adı ve şifresini girerek giriş yapar.
•	G3. Kullanıcı adı veya şifre alanı boş ise, kullanıcıya uygun bir uyarı mesajı gösterilir.
•	G4. Veritabanında eşleşen kayıt yoksa veya şifre yanlışsa, “Kullanıcı adı veya şifre hatalı” mesajı gösterilir.
•	G5. Giriş başarılı olduğunda kullanıcı rolü (ADMIN / WAITER) okunur:
o	ADMIN → AdminMainActivity
o	WAITER → WaiterMainActivity
•	G6. Admin ana ekranında “Çıkış Yap” seçeneği bulunur. Seçildiğinde:
o	Oturum bilgileri temizlenir.
o	Login ekranına dönülür.
•	G7. Garson ana ekranında (masa listesi) “Çıkış Yap” seçeneği bulunur. Seçildiğinde:
o	Oturum bilgileri temizlenir.
o	Login ekranına dönülür.
________________________________________
6.2. Admin Paneli Fonksiyonları
6.2.1. Menü Yönetimi (Kategori + Ürün)
•	G8. Admin yeni kategori ekleyebilir (örneğin: Çorbalar, Ana Yemekler, Tatlılar, İçecekler, Alkoller).
•	G9. Admin mevcut kategorileri listeleyebilir, güncelleyebilir, pasif duruma getirebilir veya silebilir.
•	G10. Bir kategorinin altında ürün varsa doğrudan silinemez; sistem uyarı verir (“Bu kategoride ürünler var…”).
•	G11. Admin yeni ürün ekleyebilir:
o	Ürün adı
o	Açıklama (opsiyonel)
o	Fiyat
o	Kategori (zorunlu, seçilebilir)
o	Görsel alanı (image_path veya benzeri) – opsiyonel
o	Aktif/pasif durumu
•	G12. Admin ürünleri listeleyebilir:
o	İsim
o	Fiyat
o	Kategorisi
o	Aktif/pasif durumu
•	G13. Admin ürün bilgilerini güncelleyebilir (isim, açıklama, fiyat, kategori, görsel, aktif/pasif).
•	G14. Admin ürünleri silebilir veya pasif duruma getirebilir.
•	G15. Bir ürün geçmiş siparişlerde kullanılmışsa:
o	Tam silme yerine “pasif” yapılması esas alınır;
o	Pasif ürün yeni sipariş ekranında görünmez; geçmiş sipariş detayında görünmeye devam eder.
•	G16. Menü yönetimi ekranında:
o	Kategoriye göre filtreleme yapılabilir (sadece seçili kategorinin ürünleri listelenir).
o	Ürün adı için basit arama alanı bulunur; ürünler isim bazlı filtrelenebilir.
6.2.2. Garson Yönetimi
•	G17. Admin yeni garson ekleyebilir:
o	İsim
o	Kullanıcı adı
o	Şifre
o	Aktif/pasif durumu
•	G18. Admin mevcut garsonları listeleyebilir (isim, kullanıcı adı, aktif/pasif).
•	G19. Admin garson bilgilerini güncelleyebilir (isim, kullanıcı adı, şifre, aktif/pasif).
•	G20. Admin garsonları silebilir veya pasif duruma getirebilir.
•	G21. Pasif bir garson kullanıcı adı/şifreyle giriş yapmaya çalıştığında giriş engellenir; “Hesabınız pasif durumdadır” benzeri bir mesaj gösterilir.
6.2.3. Masa Yönetimi
•	G22. Admin, restoran için masa tanımları oluşturabilir:
o	Masa numarası (unique)
o	Masa adı (opsiyonel, örn. “Bahçe 1”)
o	Aktif/pasif durumu (restoranda kullanılıyor mu)
•	G23. Admin masaları listeleyebilir ve düzenleyebilir (isim, aktif/pasif).
•	G24. Masanın “status” alanı (EMPTY / ACTIVE) siparişler tarafından otomatik yönetilir; admin bu alana manuel müdahale etmez.
•	G25. Pasif yapılan masalar garson tarafındaki masa listesinde gösterilmez.
6.2.4. Raporlama / Satış İstatistikleri
•	G26. Admin, raporlar ekranında bugüne ait özet bilgileri görebilir:
o	Günlük toplam ciro
o	Günlük toplam satılan ürün adedi
o	En çok satılan ilk 3 ürün (ürün adı + adet)
•	G27. Admin, “Günlük / Haftalık / Aylık” seçimlerine göre ürün bazlı satış listesi görebilir:
o	Ürün adı
o	Toplam adet
o	Toplam tutar
•	G28. Rapor listesindeki ürünler için basit isim arama filtresi bulunur.
________________________________________
6.3. Garson Modülü Fonksiyonları
6.3.1. Masa Listesi ve Durumları
•	G29. Garson giriş yaptığında, WaiterMainActivity üzerinde restoran masalarının listesi gösterilir.
•	G30. Her masa için:
o	Masa numarası / adı
o	Durum: EMPTY veya ACTIVE
görsel olarak belirtilir (renk/ikon ile desteklenebilir).
•	G31. İlk kez sipariş açılan masanın durumu otomatik olarak ACTIVE yapılır.
•	G32. Masadaki tüm aktif siparişler kapandığında, masa durumu otomatik olarak EMPTY yapılır.
6.3.2. Sipariş Oluşturma (OrderActivity)
•	G33. Garson, masa kartına tıklayarak seçili masa için yeni sipariş ekranını açabilir.
•	G34. Sipariş ekranında:
o	Üstte seçili masa bilgisi (Masa numarası/adı)
o	Üst kısımda “Toplam: … ₺” alanı
o	Kategoriler için sekmeler (Çorbalar, Ana Yemekler, Tatlılar, İçecekler, Alkoller…)
o	Kategori altında arama alanı (search) – ürün adıyla filtreleme
o	Altında ürün listesi (RecyclerView) yer alır.
•	G35. Ürün kartında:
o	Ürün fotoğrafı
o	Ürün adı
o	Varsa kısa açıklama
o	Fiyat
o	
	/ – butonları ile adet ayarlama bulunur.
•	G36. Garson + / – ile ürünleri sepete ekler; adet 0’a inerse o ürün sepetten çıkar.
•	G37. Garson “Sepeti Gör” (bottom sheet vb.) alanında:
o	Siparişe eklenmiş ürünleri (ürün adı, adet, birim fiyat, satır toplamı)
o	Genel toplam tutarı görebilir.
•	G38. “Siparişi Onayla” butonuna basıldığında:
o	Yeni sipariş kaydı orders tablosuna eklenir.
o	Siparişe ait kalemler order_items tablosuna eklenir.
o	orders.total_price sepetteki toplam tutar olarak kaydedilir.
o	İlgili masa ACTIVE yapılır.
6.3.3. Sipariş Güncelleme / İptal
•	G39. Masanın zaten açık bir siparişi varsa:
o	Masa seçildiğinde ilgili sipariş detayına gidilebilir veya sipariş düzenleme ekranı açılabilir.
•	G40. Garson, açık sipariş için:
o	Yeni ürün ekleyebilir,
o	Ürün adetini artırabilir/azaltabilir,
o	Ürünü tamamen silebilir.
•	G41. Sipariş güncellendiğinde:
o	İlgili order_items satırları güncellenir.
o	orders.total_price yeni toplama göre yeniden hesaplanır ve güncellenir.
•	G42. Gerekirse sipariş tamamen iptal edilebilir:
o	Sipariş durumu CANCELLED olarak güncellenir.
o	O masada başka aktif sipariş yoksa masa EMPTY yapılır.
6.3.4. Aktif Siparişler ve Sipariş Detayı
•	G43. Garson, “Aktif Siparişler” ekranında tüm aktif siparişleri görebilir.
•	G44. Aktif sipariş listesinde her satırda en az şu bilgiler gösterilir:
o	Masa numarası/adı
o	Sipariş durumu (PENDING / IN_PROGRESS / SERVED vb.)
o	Sipariş oluşturulma zamanı
o	Sipariş toplam tutarı
•	G45. Sipariş satırına tıklanınca OrderDetailActivity açılır. Bu ekranda:
o	Siparişin durumu
o	Sipariş toplam tutarı
o	Sipariş zamanı
o	Siparişe ait ürünlerin listesi (ürün adı, adet, birim fiyat, satır toplamı) gösterilir.
•	G46. Sipariş detay ekranından garson:
o	Siparişi güncelleyebilir (ürün ekleme/çıkarma, adet değiştirme)
o	Siparişi “Tamamlandı” olarak işaretleyebilir.
•	G47. Sipariş “Tamamlandı” işaretlendiğinde:
o	Sipariş durumu SERVED veya uygun tamamlanmış statüye çekilir.
o	Aktif siparişler listesinden çıkarılır.
o	Aynı masada başka aktif sipariş yoksa masa EMPTY duruma alınır.
________________________________________
7. Veri Modeli Gereksinimleri (SQLite Taslak)
Tabloların mantıksal yapısı:
7.1. users
•	id (INTEGER, PK, AUTOINCREMENT)
•	username (TEXT, UNIQUE, NOT NULL)
•	password (TEXT, NOT NULL)
•	role (TEXT, NOT NULL) – ADMIN veya WAITER
•	is_active (INTEGER, NOT NULL, 1=aktif, 0=pasif)
•	created_at (TEXT)
•	updated_at (TEXT)
7.2. categories
•	id (INTEGER, PK, AUTOINCREMENT)
•	name (TEXT, UNIQUE, NOT NULL)
•	is_active (INTEGER, NOT NULL)
•	created_at (TEXT)
•	updated_at (TEXT)
7.3. products
•	id (INTEGER, PK, AUTOINCREMENT)
•	name (TEXT, NOT NULL)
•	description (TEXT, NULL)
•	price (REAL, NOT NULL)
•	category_id (INTEGER, NOT NULL, FK → categories.id)
•	image_path (TEXT, NULL) – ürün görseli için lokal yol veya referans
•	is_active (INTEGER, NOT NULL)
•	created_at (TEXT)
•	updated_at (TEXT)
7.4. restaurant_tables
•	id (INTEGER, PK, AUTOINCREMENT)
•	table_number (INTEGER, UNIQUE, NOT NULL)
•	table_name (TEXT, NULL)
•	status (TEXT, NOT NULL) – EMPTY / ACTIVE
•	is_active (INTEGER, NOT NULL)
•	created_at (TEXT)
•	updated_at (TEXT)
7.5. orders
•	id (INTEGER, PK, AUTOINCREMENT)
•	table_id (INTEGER, NOT NULL, FK → restaurant_tables.id)
•	waiter_id (INTEGER, NOT NULL, FK → users.id)
•	status (TEXT, NOT NULL) – PENDING, IN_PROGRESS, SERVED, CANCELLED, PAID
•	total_price (REAL, NOT NULL, varsayılan 0)
•	created_at (TEXT)
•	updated_at (TEXT)
7.6. order_items
•	id (INTEGER, PK, AUTOINCREMENT)
•	order_id (INTEGER, NOT NULL, FK → orders.id)
•	product_id (INTEGER, NOT NULL, FK → products.id)
•	quantity (INTEGER, NOT NULL)
•	unit_price (REAL, NOT NULL)
•	line_total (REAL, NOT NULL)
•	created_at (TEXT)
•	updated_at (TEXT)
Satış raporları için gerekli tüm bilgiler orders ve order_items üzerinden hesaplanır.
________________________________________
8. Durum (State) Gereksinimleri
8.1. Masa Durumları
•	EMPTY – Masada aktif sipariş yok.
•	ACTIVE – Masada en az bir aktif sipariş var.
Masa durumu, sipariş açma/kapama işlemlerine göre sistem tarafından otomatik güncellenir.
8.2. Sipariş Durumları
•	PENDING – Yeni oluşturulmuş sipariş.
•	IN_PROGRESS – Hazırlanıyor (ileride kullanılabilir).
•	SERVED – Masaya servis edilmiş, tamamlanmış.
•	CANCELLED – İptal edilmiş.
•	PAID – Ödemesi alınmış (veritabanında alan olarak hazır; UI’de bu aşamada zorunlu değil).
Aktif sipariş listesinde genellikle PENDING / IN_PROGRESS ve gerekirse SERVED durumları gösterilir; CANCELLED ve PAID geçmiş siparişler için kullanılır.
________________________________________
9. Hata Yönetimi ve Doğrulama
•	Login ekranında:
o	Boş alan kontrolü yapılır.
o	Yanlış kullanıcı adı/şifre için net hata mesajı gösterilir.
•	Admin formlarında:
o	Ürün adı, fiyat, kategori, kullanıcı adı, masa numarası gibi zorunlu alanlarda boş geçmeye izin verilmez.
o	Fiyat ve adet gibi alanlar için sayısal format kontrolü yapılır.
•	Sipariş ekranında:
o	Adet 0 veya negatif olduğunda sipariş satırı oluşturulmaz.
o	Pasif ürünler yeni sipariş ekranında listelenmez.
•	Veritabanı işlemlerinde:
o	Kayıt ekleme/güncelleme/silme sırasında hata oluşursa kullanıcıya genel bir hata mesajı gösterilir; detaylar uygulama içinde loglanabilir.
________________________________________
10. Kullanıcı Arayüzü Gereksinimleri (Özet)
•	Garson tarafı:
o	Ana ekran: masa listesi (grid), EMPTY/ACTIVE durumu renk/ikon ile ayırt edilir.
o	Sipariş ekranı: kategori sekmeleri, ürün arama, fotoğraflı ürün kartları, +/– ile adet yönetimi, sepet özeti ve toplam tutar.
o	Aktif sipariş ekranı: masa, durum, zaman, toplam fiyat gösterimi; detay ekranına geçiş.
•	Admin tarafı:
o	Ana ekran: 4 ana kart – Menü Yönetimi, Garson Yönetimi, Masa Yönetimi, Raporlar.
o	Menü/garson/masa ekranları: liste + ekle/düzenle form yapısı.
o	Raporlar ekranı: üstte özet kartlar (ciro, adet, en çok satan), altta filtreli ürün satış listesi.
Bu haliyle doküman, ilk aşama (Android + Java + SQLite, tek cihaz, offline) için güncel ve tam fonksiyonel gereksinim analizinin son halidir.

