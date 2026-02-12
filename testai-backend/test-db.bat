@echo off
docker exec testai-user-db psql -U postgres -d user_db -c "SELECT COUNT(*) as nombre_users FROM users;"
pause
