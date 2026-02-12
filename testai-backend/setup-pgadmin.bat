@echo off
REM Script pour configurer pgAdmin avec PostgreSQL

REM Arrêter et supprimer l'ancienne instance pgAdmin
echo Arrêt de l'ancienne instance pgAdmin...
docker stop testai-pgadmin >nul 2>&1
docker rm testai-pgadmin >nul 2>&1

REM Redémarrer PostgreSQL pour s'assurer qu'il est en bon état
echo Redémarrage de PostgreSQL...
docker restart testai-user-db

REM Attendre que PostgreSQL soit prêt
echo Attente de 5 secondes...
timeout /t 5 /nobreak

REM Définir le mot de passe PostgreSQL
echo Configuration du mot de passe PostgreSQL...
docker exec testai-user-db psql -U postgres -d user_db -c "ALTER USER postgres PASSWORD 'postgres';" >nul 2>&1

REM Démarrer pgAdmin avec la configuration serveur
echo Démarrage de pgAdmin...
docker run -d ^
  --name testai-pgadmin ^
  --network testai-network ^
  -p 5050:80 ^
  -e PGADMIN_DEFAULT_EMAIL=admin@test.com ^
  -e PGADMIN_DEFAULT_PASSWORD=admin123 ^
  -v "C:\Users\user\Documents\pfe_project_test\testai-backend\pgadmin_servers.json:/pgadmin4/servers.json" ^
  dpage/pgadmin4

REM Attendre que pgAdmin démarre
echo Attente de 10 secondes pour le démarrage de pgAdmin...
timeout /t 10 /nobreak

REM Afficher le statut
echo.
echo ========================================
echo Configuration terminée !
echo ========================================
echo.
echo pgAdmin est disponible sur: http://localhost:5050
echo Email: admin@test.com
echo Mot de passe: admin123
echo.
echo PostgreSQL:
echo - Host: testai-user-db (sur le réseau Docker)
echo - Host: localhost (depuis votre machine)
echo - Port: 5432
echo - Database: user_db
echo - Username: postgres
echo - Password: postgres
echo.

REM Vérifier le statut des conteneurs
echo Statut des conteneurs:
docker ps --filter "name=testai" --format "table {{.Names}}\t{{.Status}}"

pause
