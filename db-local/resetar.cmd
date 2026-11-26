@echo off
chcp 65001 >nul
echo Isto APAGA todos os dados do banco local e carrega de novo o schema.sql e o carga.sql.
set /p CONFIRMA=Tem certeza? (s/n)
if /i not "%CONFIRMA%"=="s" exit /b 0
call "%~dp0iniciar.cmd" --resetar
