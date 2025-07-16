document.getElementById('formDeposito').addEventListener('submit', async function(event) {
  event.preventDefault();

  const valor = parseFloat(document.getElementById('valorDeposito').value);

  if (isNaN(valor) || valor < 3) {
    alert('⚠️ Valor mínimo para depósito é 3 USDT');
    return;
  }

  const userId = getUsuarioLogadoId();

  if (!userId) {
    alert('❌ Usuário não identificado.');
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
      alert('❌ Erro: ' + errorText);
      return;
    }

    const data = await response.json();

    document.getElementById('usdtAddress').innerText = data.endereco;
    document.getElementById('enderecoDeposito').style.display = 'block';

    // Mensagem aguardando com ícone
    const msgAguardando = document.getElementById('mensagemAguardando');
    msgAguardando.innerText = '⏳ Aguardando confirmação do depósito...';
    msgAguardando.style.display = 'block';

    const msgConfirmado = document.getElementById('mensagemConfirmado');
    msgConfirmado.style.display = 'none';

    document.getElementById('valorDeposito').value = '';

    localStorage.setItem('abaAtiva', 'wallet');

    const checkStatusInterval = setInterval(async () => {
      try {
        const statusResponse = await fetch(`/api/depositos/status?userId=${userId}`);
        if (!statusResponse.ok) {
          throw new Error('Erro ao consultar status do depósito');
        }

        const statusData = await statusResponse.json();

        if (statusData.status === 'confirmado') {
          clearInterval(checkStatusInterval);

          msgAguardando.style.display = 'none';
          msgConfirmado.innerText = '✅ Depósito confirmado com sucesso!';
          msgConfirmado.style.display = 'block';

          location.reload();
        } else if (statusData.status === 'rejeitado') {
          clearInterval(checkStatusInterval);

          msgAguardando.style.display = 'none';
          alert('❌ Depósito rejeitado.');

          location.reload();
        }
      } catch (err) {
        clearInterval(checkStatusInterval);
        // alert('Erro ao verificar status: ' + err.message);
      }
    }, 30000);

  } catch (error) {
    alert('❌ Erro ao solicitar depósito: ' + error.message);
  }
});
