@echo off
chcp 65001 >nul
cd /d "%~dp0"

if not exist node_modules (
  echo Instalando dependencias ^(so na primeira vez^)...
  call npm install --no-audit --no-fund
  if errorlevel 1 (
    echo.
    echo Tentando de novo usando os certificados do Windows ^(redes com inspecao HTTPS^)...
    set "NODE_OPTIONS=--use-system-ca"
    call npm install --no-audit --no-fund
    if errorlevel 1 (
      echo.
      echo Nao foi possivel instalar as dependencias.
      pause
      exit /b 1
    )
  )
)

node servidor.mjs %*
pause
