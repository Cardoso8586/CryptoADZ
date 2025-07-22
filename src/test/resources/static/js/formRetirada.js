// Variável global para controlar timeout da mensagem de erro
let timeoutErro;

// Função para exibir erro de forma mais amigável na tela
function exibirErroUsuario(mensagem) {
  const divErro = document.getElementById('mensagemErroSaque');
  if (divErro) {
    divErro.textContent = mensagem;
    divErro.style.display = 'block';

    // Limpa timeout anterior, se existir, para não acumular
    if (timeoutErro) clearTimeout(timeoutErro);

    // Esconde a mensagem após 6 segundos
    timeoutErro = setTimeout(() => {
      divErro.style.display = 'none';
    }, 6000);
  } else {
    alert(mensagem);
  }
}

document.getElementById('formRetirada').addEventListener('submit', async function (event) {
  event.preventDefault();

  const valor = parseFloat(document.getElementById('valorRetirada').value);
  const enderecoDestino = document.getElementById('enderecoDestino').value.trim();

  if (isNaN(valor) || valor < 6) {
    exibirErroUsuario('⚠️ Valor mínimo para retirada é 6 USDT');
    return;
  }

  if (!enderecoDestino) {
    exibirErroUsuario('⚠️ Endereço destino é obrigatório.');
    return;
  }

  const userId = getUsuarioLogadoId();

  if (!userId) {
    exibirErroUsuario('❌ Usuário não identificado.');
    return;
  }

  const payload = {
    userId: userId,
    valor_solicitado: valor,
    enderecoDestino: enderecoDestino
  };

  try {
    const response = await fetch('/api/saques/solicitar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await response.json();

    if (!response.ok || !data.sucesso) {
      exibirErroUsuario('❌ ' + (data.mensagem || 'Erro ao solicitar saque.'));
      return;
    }

    // Esconde mensagem de erro se houver
    const divErro = document.getElementById('mensagemErroSaque');
    if (divErro) divErro.style.display = 'none';

    // Mensagens e monitoramento de status
    const msgAguardando = document.getElementById('mensagemAguardandoSaque');
    msgAguardando.innerText = '⏳ Aguardando confirmação do saque...';
    msgAguardando.style.display = 'block';

    const msgConfirmado = document.getElementById('mensagemConfirmadoSaque');
    msgConfirmado.style.display = 'none';

    document.getElementById('valorRetirada').value = '';
    document.getElementById('enderecoDestino').value = '';

    localStorage.setItem('abaAtiva', 'wallet');

    const checkStatusInterval = setInterval(async () => {
      try {
        const statusResponse = await fetch(`/api/saques/status?userId=${userId}`);
        if (!statusResponse.ok) throw new Error('Erro ao consultar status do saque');

        const statusData = await statusResponse.json();

        if (statusData.status === 'confirmado') {
          clearInterval(checkStatusInterval);

          msgAguardando.style.display = 'none';
          msgConfirmado.innerText = '✅ Saque confirmado com sucesso!';
          msgConfirmado.style.display = 'block';

          location.reload();
        } else if (statusData.status === 'rejeitado') {
          clearInterval(checkStatusInterval);

          msgAguardando.style.display = 'none';
          exibirErroUsuario('❌ Saque rejeitado.');

          location.reload();
        }
      } catch (err) {
        clearInterval(checkStatusInterval);
        console.error('Erro ao verificar status do saque:', err.message);
      }
    }, 30000);

  } catch (error) {
    exibirErroUsuario('❌ Erro ao solicitar saque: ' + error.message);
  }
});


document.getElementById('valorRetirada').addEventListener('input', function () {
  const valor = parseFloat(this.value);
  const taxaFixa = 5.00; // valor fixo da taxa

  if (!isNaN(valor)) {
    const liquido = valor - taxaFixa;
    document.getElementById('valorLiquidoPreview').textContent = liquido > 0 ? liquido.toFixed(2) : '0.00';
  } else {
    document.getElementById('valorLiquidoPreview').textContent = '...';
  }
});

  
