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

    document.getElementById('usdtAddress').innerText = data.endereco;
    document.getElementById('enderecoDeposito').style.display = 'block';

    document.getElementById('mensagemAguardando').style.display = 'block';
    document.getElementById('mensagemConfirmado').style.display = 'none';

    document.getElementById('valorDeposito').value = '';

    localStorage.setItem('abaAtiva', 'wallet');

    // Inicia polling para verificar status
    const checkStatusInterval = setInterval(async () => {
      try {
        const statusResponse = await fetch(`/api/depositos/status?userId=${userId}`);
        if (!statusResponse.ok) {
          throw new Error('Erro ao consultar status do depósito');
        }

        const statusData = await statusResponse.json();

        if (statusData.status === 'confirmado') {
          clearInterval(checkStatusInterval);
          document.getElementById('mensagemAguardando').style.display = 'none';
          document.getElementById('mensagemConfirmado').style.display = 'block';
		  
          // reload após confirmação
          location.reload();
        } else if (statusData.status === 'rejeitado') {
          clearInterval(checkStatusInterval);
          document.getElementById('mensagemAguardando').style.display = 'none';
          alert('Depósito rejeitado.');
          // reload após rejeição, se quiser
          location.reload();
        }
        // Se status for outro, continua aguardando...
      } catch (err) {
        clearInterval(checkStatusInterval);
        //alert('Erro ao verificar status: ' + err.message);
      }
    }, 5000); // checa a cada 5 segundos

  } catch (error) {
    alert('Erro ao solicitar depósito: ' + error.message);
  }
});

