// Corrigido: chamar a função corretamente
function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}

// Depósito

// Escuta envio do formulário
document.getElementById('formDeposito').addEventListener('submit', async function(event) {
  event.preventDefault();

  const valor = parseFloat(document.getElementById('valorDeposito').value);

  if (isNaN(valor) || valor < 3) {
    alert('Valor mínimo para depósito é 3 USDT');
    return;
  }

  const userId = getUsuarioLogadoId();

  if (!userId) {
    alert('Usuário não identificado.');
    return;
  }

  const payload = {
    userId: userId,
    valor: valor
  };

  try {
    const response = await fetch('/api/depositos/fazer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const errorText = await response.text();
      alert('Erro: ' + errorText);
      return;
    }

    const data = await response.json();

    // Mostrar endereço no <code> do seu HTML
    document.getElementById('usdtAddress').innerText = data.endereco;
    document.getElementById('enderecoDeposito').style.display = 'block'; // <-- ESSENCIAL

    // Mostrar mensagem de aguardando confirmação
    document.getElementById('mensagemAguardando').style.display = 'block';
    document.getElementById('mensagemConfirmado').style.display = 'none';

    // Limpar campo do valor após sucesso
    document.getElementById('valorDeposito').value = '';

  } catch (error) {
    alert('Erro ao solicitar depósito: ' + error.message);
  }
});



