# ResultBot-Finance-Bot-
### **📌 FinanceTracker – Telegram bot orqali moliyaviy hisobotlar**  

**FinanceTracker** – kompaniyaning moliyaviy hisob-kitoblarini yuritish uchun ishlab chiqilgan **Telegram-bot**. U **daromad va xarajatlarni** kuzatish, **to‘liq hisobotlarni yaratish** va **ma’lumotlarni xavfsiz saqlash** imkonini beradi.  

---

## **🚀 Loyiha imkoniyatlari**  
✅ **Daromad va xarajatlarni yuritish** (kategoriya bo‘yicha).  
✅ **CRUD operatsiyalar** (tranzaksiyalar, mijozlar va xizmatlarni boshqarish).  
✅ **Excel formatida hisobotlar yaratish va yuklab olish**.  
✅ **Notion bilan integratsiya** – barcha moliyaviy ma’lumotlarni sinxronizatsiya qilish.  
✅ **Telegram Webhook** orqali real vaqt rejimida ishlash.  
✅ **Foydalanuvchi rollari**: Administrator va Kuzatuvchi.  
✅ **Xavfsiz autentifikatsiya** (JWT + Spring Security).  

---

## **🛠 Texnologiyalar**  
🔹 **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA.  
🔹 **Ma’lumotlar bazasi:** PostgreSQL.  
🔹 **API:** REST API, Telegram Bot API.  
🔹 **Hisobotlar:** Apache POI (Excel).  
🔹 **CI/CD:** GitHub.  
🔹 **Dokumentatsiya:** Swagger UI.  

---

## **📦 O‘rnatish va ishga tushirish**  
1️⃣ **Loyihani yuklab oling**  
```bash
git clone https://github.com/username/FinanceTracker.git
cd FinanceTracker
```
2️⃣ **Kerakli kutubxonalarni yuklash**  
```bash
mvn clean install
```
3️⃣ **.env yoki `application.yml` faylida quyidagi sozlamalarni belgilang:**  
```yaml
telegram:
  bot:
    token: YOUR_BOT_TOKEN
    webhook-path: https://your-ngrok-url/telegram/webhook
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/financetracker
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD
```
4️⃣ **Loyihani ishga tushiring**  
```bash
mvn spring-boot:run
```

---

## **📊 Telegram orqali hisobot olish**  
1. **Botni ishga tushiring** va parol bilan autentifikatsiya qiling.  
2. **Oylik hisobotlar** bo‘limiga kiring va kerakli filtrni tanlang.  
3. Bot **Excel fayl** shaklida hisobotni yuboradi.  

---
