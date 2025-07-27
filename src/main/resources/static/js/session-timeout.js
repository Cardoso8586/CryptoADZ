// Redireciona após ? minuto (600000 milissegundos)
const SESSION_TIMEOUT = 3000000;

let timeout;

function resetTimer() {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
        Swal.fire({
            icon: 'warning',
            title: 'Atenção',
            text: 'Sua sessão expirou. Você será redirecionado.',
            timer: 5000,
            timerProgressBar: true,
            showConfirmButton: false,
            allowOutsideClick: false,
            allowEscapeKey: false,
            allowEnterKey: false
        }).then(() => {
            // Executa o redirecionamento após o alerta fechar
            window.location.href = "/logout"; // ou outra rota desejada
        });
    }, SESSION_TIMEOUT);
}

window.onload = resetTimer;
document.onmousemove = resetTimer;
document.onkeypress = resetTimer;

