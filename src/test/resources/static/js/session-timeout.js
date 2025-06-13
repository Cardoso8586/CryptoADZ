// Redireciona após ? minuto (600000 milissegundos)
const SESSION_TIMEOUT = 300000;

let timeout;

function resetTimer() {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
        alert("Your session has expired. You will be redirected.");
        window.location.href = "/logout"; // ou outra rota desejada
    }, SESSION_TIMEOUT);
}

window.onload = resetTimer;
document.onmousemove = resetTimer;
document.onkeypress = resetTimer;
