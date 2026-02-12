@echo off
REM Vérifier la configuration PostgreSQL

echo.
echo ========================================
echo CONFIGURATION POSTGRESQL - DOCKER
echo ========================================
echo.

REM Vérifier que le conteneur PostgreSQL est en cours d'exécution
docker ps --filter name=testai-user-db --format "table {{.Names}}\t{{.Ports}}\t{{.Status}}"

echo.
echo ========================================
echo INSTRUCTIONS POUR PGADMIN LOCAL
echo ========================================
echo.
echo Pour connecter pgAdmin LOCAL a PostgreSQL:
echo.
echo 1. Ouvrez pgAdmin local sur votre machine
echo.
echo 2. Cliquez sur: Register ^> Server (ou Add New Server)
echo.
echo 3. Onglet "General":
echo    - Name: testai-user-db
echo.
echo 4. Onglet "Connection":
echo    - Host name/address: localhost
echo    - Port: 5432
echo    - Maintenance database: user_db
echo    - Username: postgres
echo    - Password: postgres
echo    - Cochez "Save password?"
echo.
echo 5. Cliquez sur "Save"
echo.
echo 6. Allez dans: Servers ^> testai-user-db ^> Databases ^> user_db ^> Schemas ^> public ^> Tables ^> users
echo.
echo 7. Clic droit sur "users" ^> View/Edit Data ^> All Rows
echo.
echo ========================================
echo TEST DE CONNEXION
echo ========================================
echo.

REM Test de connexion
echo Test de connexion a PostgreSQL depuis votre machine...
psql -h localhost -U postgres -d user_db -c "SELECT COUNT(*) as nombre_utilisateurs FROM users;" 2>nul

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [OK] La connexion a PostgreSQL fonctionne !
) else (
    echo.
    echo [ERREUR] Impossible de se connecter a PostgreSQL
    echo Assurez-vous que:
    echo - Le conteneur testai-user-db est en cours d'exécution
    echo - PostgreSQL écoute sur le port 5432
    echo - Le mot de passe est "postgres"
)

echo.
pause
