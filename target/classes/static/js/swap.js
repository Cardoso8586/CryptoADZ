  // Variáveis globais de saldo
  let saldoTokens = 0;
  let saldoUsdt = 0;

  document.addEventListener('DOMContentLoaded', () => {
    const fromToken = document.getElementById('fromToken');
    const toToken = document.getElementById('toToken');
    const fromAmount = document.getElementById('fromAmount');
    const toAmount = document.getElementById('toAmount');
    const preview = document.getElementById('previewSwap');
    const btnConfirmSwap = document.getElementById('btnConfirmSwap');
    const taxa = 0.02; // 2%

    // Obtém o usuário logado ao carregar

// Função auxiliar
function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}
    // Função para obter saldo atual conforme o token selecionado
    function obterSaldo(token) {
      if (token === 'token') return saldoTokens;
      if (token === 'usdt') return saldoUsdt;
      return 0;
    }

    // Simulação de cotação fixa
    async function getCotacao(from, to) {
      if (from === 'token' && to === 'usdt') return 0.001;
      if (from === 'usdt' && to === 'token') return 1000;
      return 1;
    }

    async function atualizarResultado() {
      const from = fromToken.value;
      const to = toToken.value;
      const valor = parseFloat(fromAmount.value);

      if (!valor || valor <= 0) {
        toAmount.value = '';
        preview.textContent = '';
        return;
      }

      if (from === to) {
        toAmount.value = valor.toFixed(2);
        preview.textContent = 'Selecione moedas diferentes para trocar.';
        return;
      }

      const saldoAtual = obterSaldo(from);
      if (valor > saldoAtual) {
        toAmount.value = '';
        preview.innerHTML = `<span style="color: red;">Saldo insuficiente! Você tem apenas ${saldoAtual.toFixed(2)} ${from.toUpperCase()}.</span>`;
        return;
      }

      if (from === 'token' && valor < 1000) {
        preview.innerHTML = `<span style="color: red;">O valor mínimo para troca de ADZ Token é 1000.</span>`;
        toAmount.value = '';
        return;
      }

      if (from === 'usdt' && valor < 1) {
        preview.innerHTML = `<span style="color: red;">O valor mínimo para troca de USDT é 1.</span>`;
        toAmount.value = '';
        return;
      }

      const cotacao = await getCotacao(from, to);
      const bruto = valor * cotacao;
      const taxaAplicada = bruto * taxa;
      const liquido = bruto - taxaAplicada;

      toAmount.value = liquido.toFixed(2);

      const nomeFrom = from === 'token' ? 'ADZ Token' : 'USDT';
      const nomeTo = to === 'token' ? 'ADZ Token' : 'USDT';

      preview.innerHTML = `
        💱 Você receberá aproximadamente <strong>${liquido.toFixed(2)}</strong> ${nomeTo} (já descontada a taxa de ${taxaAplicada.toFixed(2)} ${nomeTo})
      `;
    }

    btnConfirmSwap.addEventListener('click', async () => {
      const valor = parseFloat(fromAmount.value);
      const de = fromToken.value;
      const para = toToken.value;

      if (!valor || valor <= 0) {
        alert("Digite um valor válido.");
        return;
      }

      if (de === para) {
        alert("Selecione moedas diferentes.");
        return;
      }

      const saldoAtual = obterSaldo(de);
      if (valor > saldoAtual) {
        alert(`Saldo insuficiente! Você tem apenas ${saldoAtual.toFixed(2)} ${de.toUpperCase()}.`);
        return;
      }

      if (de === 'token' && valor < 1000) {
        alert("Valor mínimo para troca de ADZ Token é 1000.");
        return;
      }

      if (de === 'usdt' && valor < 1) {
        alert("Valor mínimo para troca de USDT é 1.");
        return;
      }

      try {
    	  const resultado = await realizarSwap(de, para, valor, getUsuarioLogadoId());


        if (resultado.sucesso) {
          alert(`✅ Troca realizada com sucesso!\nVocê recebeu: ${resultado.valorRecebido.toFixed(2)} ${para.toUpperCase()}`);

          if (de === 'token') {
            saldoTokens -= valor;
            saldoUsdt += resultado.valorRecebido;
          } else {
            saldoUsdt -= valor;
            saldoTokens += resultado.valorRecebido;
          }

          document.getElementById('saldoTokens').innerText = saldoTokens.toFixed(2);
          document.getElementById('saldoUsdt').innerText = saldoUsdt.toFixed(2);

          fromAmount.value = '';
          toAmount.value = '';
          preview.textContent = '';
        } else {
          alert('Erro na troca: ' + (resultado.erro || 'Erro desconhecido'));
        }
      } catch (err) {
        alert('Erro ao realizar troca: ' + err.message);
      }
    });

    fromToken.addEventListener('change', atualizarResultado);
    toToken.addEventListener('change', atualizarResultado);
    fromAmount.addEventListener('input', atualizarResultado);

    atualizarResultado(); // Atualiza ao iniciar
  });

  // Atualiza o saldo de Tokens
  function atualizarSaldoTokens() {
    return fetch('/api/saldo')
      .then(res => res.ok ? res.json() : Promise.reject("Erro na requisição saldo"))
      .then(data => {
        saldoTokens = data.saldo;
        document.getElementById('saldoTokens').innerText = saldoTokens.toFixed(2);
      })
      .catch(err => console.error(err));
  }

  // Atualiza o saldo de USDT
  function atualizarSaldoUsdt() {
    return fetch('/api/saldoUsdt')
      .then(res => res.ok ? res.json() : Promise.reject("Erro na requisição saldoUsdt"))
      .then(data => {
        saldoUsdt = data.saldoUsdt;
        document.getElementById('saldoUsdt').innerText = saldoUsdt.toFixed(2);
      })
      .catch(err => console.error(err));
  }


  
  async function realizarSwap(from, to, fromAmount, userId) {
	  const url = '/api/swap';
	  const swapRequest = {
	    from: from,
	    to: to,
	    fromAmount: fromAmount,
	    userId: userId // ← aqui mudou de 'username' para 'userId'
	  };

	  const response = await fetch(url, {
	    method: 'POST',
	    headers: { 'Content-Type': 'application/json' },
	    body: JSON.stringify(swapRequest)
	  });

	  if (!response.ok) {
	    const errorData = await response.json();
	    return { sucesso: false, erro: errorData.erro || 'Erro desconhecido' };
	  }

	  const data = await response.json();
	  return data;
	}

  // Inicializa saldo na página
  atualizarSaldoTokens();
  atualizarSaldoUsdt();