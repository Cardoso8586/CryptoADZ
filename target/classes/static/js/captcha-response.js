// Callback chamado quando o CAPTCHA é resolvido com sucesso
  function onTurnstileSuccess(token) {
    document.getElementById('captcha-response').value = token;
    document.getElementById('submitBtn').disabled = false;  // habilita o botão
  }

  // Se ocorrer erro no CAPTCHA
  function onTurnstileError() {
    document.getElementById('captcha-response').value = '';
    document.getElementById('submitBtn').disabled = true;
  }

  // Se o token expirar (usuário demora muito)
  function onTurnstileExpired() {
    document.getElementById('captcha-response').value = '';
    document.getElementById('submitBtn').disabled = true;
  }