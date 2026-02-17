# 🧪 Guide Complet de Test - User Service API

## 📋 Prérequis

- ✅ Services démarrés : `docker-compose up -d`
- ✅ Keycloak accessible : http://localhost:8080
- ✅ User Service accessible : http://localhost:8081
- ✅ Email configuré (Gmail)

---

## 🎯 Scénario 1 : Inscription et Connexion (Flux Complet)

### **1.1 - Inscription**

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ghada Fatnassi",
    "email": "ghada@example.com",
    "password": "Password123!",
    "phoneNumber": "+21624625506",
    "company": "TestAI",
    "role": "DEVELOPER"
  }'
```

**Réponse attendue** :
```json
{
  "success": true,
  "message": "📧 Un email de vérification a été envoyé à ghada@example.com. Veuillez vérifier votre email pour activer votre compte.",
  "email": "ghada@example.com",
  "requiresEmailVerification": true,
  "requiresPhoneVerification": false,
  "note": "⚠️ Vérification par téléphone temporairement désactivée"
}
```

### **1.2 - Récupérer le token de vérification email**

**Option A : Depuis les logs Docker**
```bash
docker logs testai-user-service --tail 100 | grep "token="
```

**Option B : Depuis l'email Gmail**
Ouvrir l'email et copier le token depuis l'URL :
```
http://localhost:8081/api/auth/verify-email?token=abc123-def456...
```

### **1.3 - Vérifier l'email**

```bash
# Remplacer VOTRE_TOKEN par le token récupéré
curl "http://localhost:8081/api/auth/verify-email?token=VOTRE_TOKEN"
```

**Réponse attendue** :
```json
{
  "success": true,
  "message": "🎉 Votre compte est maintenant activé ! Email vérifié. Vous pouvez vous connecter.",
  "emailVerified": true,
  "phoneVerified": true,
  "accountActive": true
}
```

### **1.4 - Connexion**

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ghada@example.com",
    "password": "Password123!"
  }'
```

**Réponse attendue** :
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Ghada Fatnassi",
    "email": "ghada@example.com",
    "role": "DEVELOPER",
    "isActive": true,
    "createdAt": "2026-02-16T12:00:00Z"
  }
}
```

**💾 Sauvegarder le token pour les requêtes suivantes** :
```bash
export TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🎯 Scénario 2 : Réinitialisation de Mot de Passe

### **2.1 - Demander la réinitialisation**

```bash
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ghada@example.com"
  }'
```

**Réponse attendue** :
```json
{
  "success": true,
  "message": "📧 Un email de réinitialisation a été envoyé à ghada@example.com. Le lien est valable pendant 1 heure.",
  "email": "ghada@example.com"
}
```

### **2.2 - Récupérer le token de réinitialisation**

**Option A : Depuis les logs**
```bash
docker logs testai-user-service --tail 100 | grep "Token de réinitialisation"
```

**Option B : Depuis l'email**
Copier le token depuis l'URL dans l'email.

### **2.3 - Vérifier que le token est valide (optionnel)**

```bash
curl "http://localhost:8081/api/auth/validate-reset-token?token=VOTRE_TOKEN"
```

**Réponse attendue** :
```json
{
  "success": true,
  "email": "ghada@example.com",
  "message": "Token valide"
}
```

### **2.4 - Réinitialiser le mot de passe**

```bash
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "VOTRE_TOKEN",
    "newPassword": "NewPassword123!",
    "confirmPassword": "NewPassword123!"
  }'
```

**Réponse attendue** :
```json
{
  "success": true,
  "message": "✅ Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.",
  "email": "ghada@example.com"
}
```

### **2.5 - Se connecter avec le nouveau mot de passe**

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ghada@example.com",
    "password": "NewPassword123!"
  }'
```

**✅ Connexion réussie !**

---

## 🎯 Scénario 3 : Gestion du Profil

### **3.1 - Récupérer son profil par email**

```bash
curl -X GET "http://localhost:8081/api/users/email/ghada@example.com" \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse attendue** :
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Ghada Fatnassi",
  "email": "ghada@example.com",
  "role": "DEVELOPER",
  "avatar": null,
  "company": "TestAI",
  "isActive": true,
  "createdAt": "2026-02-16T12:00:00Z",
  "lastLogin": "2026-02-16T12:05:00Z"
}
```

### **3.2 - Récupérer son profil par ID**

```bash
# Remplacer USER_ID par l'ID récupéré
curl -X GET "http://localhost:8081/api/users/USER_ID" \
  -H "Authorization: Bearer $TOKEN"
```

### **3.3 - Mettre à jour son profil**

```bash
curl -X PUT "http://localhost:8081/api/users/USER_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ghada Fatnassi Updated",
    "company": "TestAI Corp",
    "avatar": "https://example.com/avatar.jpg"
  }'
```

**Réponse attendue** :
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Ghada Fatnassi Updated",
  "email": "ghada@example.com",
  "role": "DEVELOPER",
  "avatar": "https://example.com/avatar.jpg",
  "company": "TestAI Corp",
  "isActive": true,
  "createdAt": "2026-02-16T12:00:00Z",
  "lastLogin": "2026-02-16T12:05:00Z"
}
```

---

## 🎯 Scénario 4 : Rafraîchir le Token

### **4.1 - Rafraîchir le token d'accès**

```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "VOTRE_REFRESH_TOKEN"
  }'
```

**Réponse attendue** :
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

---

## 🎯 Scénario 5 : Renvoyer les Emails de Vérification

### **5.1 - Renvoyer l'email de vérification**

```bash
curl -X POST http://localhost:8081/api/auth/resend-email-verification \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ghada@example.com"
  }'
```

**Réponse attendue** :
```json
{
  "success": true,
  "message": "📧 Un nouvel email de vérification a été envoyé à ghada@example.com"
}
```

---

## 🎯 Scénario 6 : Logout (Côté Client)

**Note** : Le logout dans une architecture JWT se fait **côté client** en supprimant simplement le token.

### **Côté Frontend (exemple)**

```javascript
// Supprimer le token du localStorage
localStorage.removeItem('accessToken');
localStorage.removeItem('refreshToken');

// Rediriger vers la page de login
window.location.href = '/login';
```

### **Endpoint Logout (optionnel - pour traçabilité)**

```bash
curl -X POST http://localhost:8081/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse attendue** :
```json
{
  "message": "Déconnexion réussie"
}
```

---

## 🎯 Scénario 7 : Tests d'Erreurs

### **7.1 - Inscription avec email déjà utilisé**

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "ghada@example.com",
    "password": "Password123!"
  }'
```

**Réponse attendue** :
```json
{
  "success": false,
  "message": "❌ Cet email est déjà utilisé"
}
```

### **7.2 - Login avec mauvais mot de passe**

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ghada@example.com",
    "password": "WrongPassword123!"
  }'
```

**Réponse attendue** :
```json
{
  "success": false,
  "message": "❌ Email ou mot de passe incorrect"
}
```

### **7.3 - Login avant vérification email**

```bash
# S'inscrire
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test2@example.com",
    "password": "Password123!"
  }'

# Essayer de se connecter SANS vérifier l'email
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test2@example.com",
    "password": "Password123!"
  }'
```

**Réponse attendue** :
```json
{
  "success": false,
  "message": "❌ Veuillez d'abord vérifier votre email. Un lien de vérification vous a été envoyé."
}
```

### **7.4 - Réinitialisation avec token expiré**

```bash
# Utiliser un vieux token (> 1 heure)
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "old-expired-token",
    "newPassword": "NewPassword123!",
    "confirmPassword": "NewPassword123!"
  }'
```

**Réponse attendue** :
```json
{
  "success": false,
  "message": "❌ Ce lien de réinitialisation a expiré. Veuillez en demander un nouveau."
}
```

### **7.5 - Mot de passe trop court**

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "test3@example.com",
    "password": "123"
  }'
```

**Réponse attendue** :
```json
{
  "success": false,
  "message": "❌ Le mot de passe doit contenir au moins 8 caractères"
}
```

---

## 🎯 Scénario 8 : Vérification dans la Base de Données

### **8.1 - Se connecter à pgAdmin**

1. Ouvrir : http://localhost:5050
2. Login : `admin@admin.com` / `admin123`
3. Se connecter au serveur `TestAI User DB`

### **8.2 - Vérifier les utilisateurs**

```sql
SELECT 
    id,
    name,
    email,
    role,
    email_verified,
    phone_verified,
    is_active,
    keycloak_id,
    created_at,
    last_login
FROM users
ORDER BY created_at DESC;
```

### **8.3 - Vérifier les tokens de réinitialisation**

```sql
SELECT 
    email,
    password_reset_token,
    password_reset_token_expires_at,
    password_reset_attempts,
    password_reset_requested_at
FROM users
WHERE password_reset_token IS NOT NULL;
```

### **8.4 - Vérifier les tokens de vérification email**

```sql
SELECT 
    email,
    email_verification_token,
    verification_token_expires_at,
    email_verified
FROM users
WHERE email_verified = false;
```

---

## 🎯 Scénario 9 : Vérification dans Keycloak

### **9.1 - Se connecter à Keycloak**

1. Ouvrir : http://localhost:8080
2. Login : `admin` / `admin123`
3. Sélectionner le realm : `testai`

### **9.2 - Voir les utilisateurs**

1. Menu : `Users`
2. Chercher par email
3. Vérifier le rôle assigné
4. Vérifier les credentials

### **9.3 - Tester l'authentification Keycloak directement**

```bash
curl -X POST http://localhost:8080/realms/testai/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=user-service" \
  -d "client_secret=0W791wbIPTtDR3v7noogqAFpBBsOZpw0" \
  -d "username=ghada@example.com" \
  -d "password=Password123!" \
  -d "grant_type=password"
```

---

## 🎯 Scénario 10 : Health Check

### **10.1 - Vérifier que le service est en ligne**

```bash
curl http://localhost:8081/actuator/health
```

**Réponse attendue** :
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

---

## 📊 Résumé des Endpoints

| Méthode | Endpoint | Description | Auth Requise |
|---------|----------|-------------|--------------|
| POST | `/api/auth/register` | Inscription | Non |
| GET | `/api/auth/verify-email` | Vérifier email | Non |
| POST | `/api/auth/login` | Connexion | Non |
| POST | `/api/auth/logout` | Déconnexion | Oui |
| POST | `/api/auth/refresh` | Rafraîchir token | Non |
| POST | `/api/auth/forgot-password` | Demander réinitialisation | Non |
| GET | `/api/auth/validate-reset-token` | Vérifier token reset | Non |
| POST | `/api/auth/reset-password` | Réinitialiser password | Non |
| POST | `/api/auth/resend-email-verification` | Renvoyer email | Non |
| GET | `/api/users/{id}` | Récupérer user par ID | Oui |
| GET | `/api/users/email/{email}` | Récupérer user par email | Oui |
| PUT | `/api/users/{id}` | Mettre à jour profil | Oui |
| GET | `/actuator/health` | Health check | Non |

---

## 🎉 Flux Complet en Une Seule Séquence

```bash
# 1. Inscription
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"Password123!"}'

# 2. Récupérer token depuis logs
TOKEN_EMAIL=$(docker logs testai-user-service --tail 100 | grep "token=" | tail -1 | sed 's/.*token=\([^ ]*\).*/\1/')

# 3. Vérifier email
curl "http://localhost:8081/api/auth/verify-email?token=$TOKEN_EMAIL"

# 4. Login
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Password123!"}')

# 5. Extraire le token
ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.accessToken')
USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.user.id')

# 6. Récupérer le profil
curl "http://localhost:8081/api/users/$USER_ID" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# 7. Mettre à jour le profil
curl -X PUT "http://localhost:8081/api/users/$USER_ID" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User Updated","company":"My Company"}'

# 8. Demander réinitialisation
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# 9. Récupérer token reset
TOKEN_RESET=$(docker logs testai-user-service --tail 100 | grep "Token de réinitialisation" | tail -1 | sed 's/.*token: \([^ ]*\).*/\1/')

# 10. Réinitialiser password
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d "{\"token\":\"$TOKEN_RESET\",\"newPassword\":\"NewPassword123!\",\"confirmPassword\":\"NewPassword123!\"}"

# 11. Login avec nouveau password
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"NewPassword123!"}'

echo "✅ Tous les tests sont passés !"
```

---

## 🔧 Scripts de Test Automatiques

### **Bash (Linux/Mac)**

Créer `test-api.sh` :

```bash
#!/bin/bash

echo "🧪 Tests User Service API"
echo ""

BASE_URL="http://localhost:8081"

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

# Test 1: Health Check
echo "1️⃣ Health Check..."
HEALTH=$(curl -s "$BASE_URL/actuator/health" | jq -r '.status')
if [ "$HEALTH" == "UP" ]; then
    echo -e "${GREEN}✅ Service UP${NC}"
else
    echo -e "${RED}❌ Service DOWN${NC}"
    exit 1
fi

# Test 2: Register
echo "2️⃣ Register..."
REGISTER=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Test User\",\"email\":\"test-$(date +%s)@example.com\",\"password\":\"Password123!\"}")
  
if echo "$REGISTER" | jq -e '.success' > /dev/null; then
    echo -e "${GREEN}✅ Register OK${NC}"
    EMAIL=$(echo "$REGISTER" | jq -r '.email')
else
    echo -e "${RED}❌ Register Failed${NC}"
    exit 1
fi

echo ""
echo "✅ Tous les tests sont passés !"
```

Rendre exécutable et lancer :
```bash
chmod +x test-api.sh
./test-api.sh
```

---

**Tous les scénarios de test sont maintenant documentés !** 🎉
