@echo off
chcp 65001 > nul
title Lancement Micro-services E-bulltin

echo ========================================
echo  Lancement des micro-services Spring Boot
echo ========================================

:: Liste des services dans l'ordre de démarrage 
@REM set SERVICES=config-service discovery-service user-service ai-service chat-service chatbot-service gateway-service  

@REM set SERVICES=config-service ms-discovery user-service ai-service chat-service chatbot-service ms-gateway 

set SERVICES=config-service ms-discovery user-service ai-service chat-service chatbot-service ms-gateway 

:: Pour chaque service
for %%s in (%SERVICES%) do (
    echo.
    echo Démarrage de %%s...
    
    if exist "%%s\pom.xml" (
        start "%%s" cmd /c "cd /d "%%s" && mvn spring-boot:run > "..\%%s.log" 2>&1"
        timeout /t 8 /nobreak > nul
        echo ✓ %%s démarré
    ) else (
        echo ✗ %%s : pom.xml introuvable
    )
)

echo.
echo ========================================
echo  Tous les services sont en cours de démarrage
echo ========================================
echo.
echo Fichiers de logs créés :
dir *.log /b
echo.


echo ========================================
echo  Lancement du frontend React
echo ========================================
if exist "frontend\package.json" (
    start "frontend" cmd /k "cd /d frontend && npm run dev"
    echo ✓ Frontend en cours d'exécution (Fenêtre séparée)
) else (
    echo ✗ Aucun frontend trouvé dans /frontend
)

echo.
echo Logs frontend :
if exist "..\frontend.log" (
    echo frontend.log
) else (
    echo Aucun fichier frontend.log trouvé
)
echo.


echo ========================================
echo Tous les services sont en cours d'exécution
echo Fermez les fenêtres pour arrêter.
echo ========================================
echo.

pause >nul

:: Nettoyage à la fermeture (tuer tous les mvn lancés)
echo Arrêt de tous les micro-services...
taskkill /F /T /IM mvn.cmd >nul 2>&1
taskkill /F /T /IM java.exe >nul 2>&1
taskkill /F /T /IM node.exe >nul 2>&1

echo Tous les services sont arrêtés.
timeout /t 2 >nul
 

@REM  http://localhost:8085/emprunt2023/emprunts
@REM  http://localhost:2025/emprunts