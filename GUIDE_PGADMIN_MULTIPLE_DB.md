# 🗄️ Configuration pgAdmin - Gérer Plusieurs Bases de Données

## ℹ️ Information Importante

**Une seule instance de pgAdmin peut gérer PLUSIEURS bases de données PostgreSQL !**

Vous n'avez PAS besoin d'une nouvelle image pgAdmin. L'instance existante peut se connecter à :
- ✅ user-db (port 5432)
- ✅ project-db (port 5433)
- ✅ keycloak-db

---

## 🚀 Configuration pgAdmin

### **Étape 1 : Accéder à pgAdmin**

```
http://localhost:5050
```

**Credentials** :
- Email : `admin@admin.com`
- Password : `admin123`

---

### **Étape 2 : Ajouter la Base User-DB (si pas déjà fait)**

1. Clic droit sur **Servers** → **Register** → **Server**

2. **Onglet General** :
   - Name : `TestAI User DB`

3. **Onglet Connection** :
   - Host : `testai-user-db`
   - Port : `5432`
   - Maintenance database : `user_db`
   - Username : `postgres`
   - Password : `postgres`
   - ✅ Save password

4. Cliquer **Save**

---

### **Étape 3 : Ajouter la Base Project-DB (NOUVEAU)**

1. Clic droit sur **Servers** → **Register** → **Server**

2. **Onglet General** :
   - Name : `TestAI Project DB`

3. **Onglet Connection** :
   - Host : `testai-project-db`
   - Port : `5432`  ⚠️ Port INTERNE (pas 5433)
   - Maintenance database : `project_db`
   - Username : `postgres`
   - Password : `postgres`
   - ✅ Save password

4. Cliquer **Save**

---

### **Étape 4 : Vérifier les Tables**

#### **User DB**

```
TestAI User DB → Databases → user_db → Schemas → public → Tables
```

Tables :
- ✅ `users`
- ✅ `developer_invitations`

#### **Project DB**

```
TestAI Project DB → Databases → project_db → Schemas → public → Tables
```

Tables :
- ✅ `projects`

---

## 🔍 Requêtes SQL Utiles

### **User DB**

```sql
-- Voir tous les utilisateurs
SELECT id, email, name, role, is_active 
FROM users;

-- Voir tous les projets d'un utilisateur
-- (requête à faire depuis project-db)
```

### **Project DB**

```sql
-- Voir tous les projets
SELECT 
    id, 
    user_id, 
    name, 
    description, 
    project_url, 
    doc_mode, 
    auth_type
FROM projects;

-- Voir les projets d'un utilisateur spécifique
SELECT * FROM projects 
WHERE user_id = 'USER_UUID_ICI';

-- Compter les projets par type d'auth
SELECT auth_type, COUNT(*) 
FROM projects 
GROUP BY auth_type;
```

---

## 📊 Architecture des Bases

```
┌─────────────────────────────────────────┐
│           pgAdmin (5050)                │
│    Une seule instance pour tout !       │
└────────────┬────────────────────────────┘
             │
     ┌───────┴───────┐
     │               │
┌────▼─────┐  ┌─────▼──────┐
│ user-db  │  │ project-db │
│  :5432   │  │  :5433     │
└──────────┘  └────────────┘
```

---

## ✅ Résumé

- ✅ **1 pgAdmin** gère **2 bases de données** (user-db + project-db)
- ✅ Pas besoin de nouvelle image pgAdmin
- ✅ Juste ajouter une nouvelle connexion dans pgAdmin
- ✅ Ports différents en externe (5432 et 5433)
- ✅ Port interne toujours 5432 dans Docker

**C'est tout !** 🎉
