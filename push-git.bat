@echo off
REM Vai para a pasta do projeto
cd /d "C:\Users\jucel\Area de programação Java\FaucetSite\CryptoAdz"

REM Inicializa o repositório Git
git init

REM Adiciona todos os arquivos
git add .

REM Configura o usuário globalmente (se já estiver configurado, pode comentar essas linhas)
git config --global user.email "jucelino.7785mendes@gmail.com"
git config --global user.name "Cardoso8586"

REM Faz o commit com a mensagem
git commit -m "primeiro commit"

REM Cria ou muda para o branch main
git branch -M main

REM Adiciona o repositório remoto
git remote add origin https://github.com/Cardoso8586/CryptoADZ.git

REM Envia o commit para o GitHub
git push -u origin main

pause



