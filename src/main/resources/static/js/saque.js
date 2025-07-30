document.addEventListener('DOMContentLoaded', () => {
  const withdrawForm = document.getElementById('withdrawForm');
  const withdrawAmountInput = document.getElementById('withdrawAmount');
  const valorReceberFeedback = document.getElementById('valorReceberFeedback'); // Campo de feedback
  const TAXA_SAQUE = 1.0; // taxa fixa em USDT

  const botaoSaque = withdrawForm.querySelector('button[type="submit"]');

  // Função para obter o ID do usuário logado da meta tag
  function getUsuarioLogadoId() {
    const meta = document.querySelector('meta[name="user-id"]');
    return meta ? parseInt(meta.getAttribute('content')) : null;
  }

  const userId = getUsuarioLogadoId();

  // Atualiza o valor a receber enquanto o usuário digita
  withdrawAmountInput.addEventListener('input', () => {
    const valorDigitado = parseFloat(withdrawAmountInput.value);

    if (isNaN(valorDigitado) || valorDigitado <= TAXA_SAQUE) {
      valorReceberFeedback.textContent = `Valor a receber: ${valorLiquido.toFixed(2)} 'USDT`;
      valorReceberFeedback.style.color = 'red';
    } else {
      const valorLiquido = valorDigitado - TAXA_SAQUE;
    valorReceberFeedback.textContent = `Valor a receber: ${valorLiquido.toFixed(2)} 'USDT`;
      valorReceberFeedback.style.color = 'green';
    }
  });

  withdrawForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const valorSolicitado = parseFloat(withdrawAmountInput.value);
    if (isNaN(valorSolicitado) || valorSolicitado <= TAXA_SAQUE) {
      await Swal.fire({
        icon: 'warning',
        title: 'Valor inválido',
        text: `O valor mínimo para saque é 3 USDT`,
        confirmButtonColor: '#3085d6',
      });
      return;
    }

    if (!userId) {
      await Swal.fire({
        icon: 'error',
        title: 'Usuário não identificado',
        text: 'Faça login novamente.',
        confirmButtonColor: '#d33',
      });
      return;
    }

    // Consulta saldo no backend
    let saldoAtual = 0;
    try {
      const saldoResponse = await fetch(`/api/saldoUsdt?userId=${userId}`);
      if (!saldoResponse.ok) throw new Error('Erro ao buscar saldo');
      const saldoData = await saldoResponse.json();
      saldoAtual = parseFloat(saldoData.saldoUsdt);
    } catch (err) {
      await Swal.fire({
        icon: 'error',
        title: 'Erro',
        text: 'Erro ao verificar saldo: ',
        confirmButtonColor: '#d33',
      });
      return;
    }

    const valorTotal = valorSolicitado;
    const valorReceber = valorTotal - TAXA_SAQUE;

    if (valorTotal > saldoAtual) {
      await Swal.fire({
        icon: 'error',
        title: 'Saldo insuficiente',
        text: `Seu saldo: ${saldoAtual.toFixed(2)} USDT.`,
        confirmButtonColor: '#d33',
      });
      return;
    }

    // Conecta à carteira MetaMask
    let carteiraDestino = '';
    try {
      const provider = new ethers.providers.Web3Provider(window.ethereum);
      await provider.send("eth_requestAccounts", []);
      const signer = provider.getSigner();
      const userAccount = await signer.getAddress();
      carteiraDestino = userAccount;
    } catch (err) {
      await Swal.fire({
        icon: 'error',
        title: 'Erro na carteira',
        text: "Erro ao conectar à carteira: ",
        confirmButtonColor: '#d33',
      });
      return;
    }

    const payload = {
      userId,
      carteiraDestino,
      valorUSDT: parseFloat(valorTotal.toFixed(2))
    };

    // Desabilita botão enquanto processa
    botaoSaque.disabled = true;
    botaoSaque.textContent = "Processando...";

    try {
      const response = await fetch('/api/saque', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errorText = await response.text();
        await Swal.fire({
          icon: 'error',
          title: 'Erro no saque',
          text: errorText,
          confirmButtonColor: '#d33',
        });
        return;
      }

      const txHash = await response.text();

      await Swal.fire({
        icon: 'success',
        title: 'Saque realizado com sucesso!',
        html: `Você irá receber: <b>${valorReceber.toFixed(2)} USDT</b><br>Taxa: <b>${TAXA_SAQUE.toFixed(2)} USDT</b><br>TxHash: <a href="https://bscscan.com/tx/${txHash}" target="_blank">${txHash}</a>`,
        timer: 6000,
        timerProgressBar: true,
        showConfirmButton: false,
        background: '#fff',
        color: '#000'
      });

      withdrawAmountInput.value = '';
      valorReceberFeedback.textContent = 'Valor a receber: ??? USDT';
      valorReceberFeedback.style.color = 'black';

      adicionarHistorico(txHash, valorReceber, carteiraDestino);
      atualizarSaldoUsdt();

    } catch (error) {
      await Swal.fire({
        icon: 'error',
        title: 'Erro',
        text: 'Erro ao realizar saque: ',
        confirmButtonColor: '#d33',
      });
    } finally {
      botaoSaque.disabled = false;
      botaoSaque.textContent = "Sacar";
    }
  });

  function adicionarHistorico(txHash, valor, carteira) {
    const container = document.getElementById('historicoRetiradas');
    const novoItem = document.createElement('div');
    novoItem.innerHTML = `
      💸 Saque de <b>${valor.toFixed(2)} USDT</b> para <code>${carteira}</code><br>
      Tx: <a href="https://bscscan.com/tx/${txHash}" target="_blank">${txHash}</a>
    `;
    container.prepend(novoItem);
  }

  async function atualizarSaldoUsdt() {
    try {
      const saldoResponse = await fetch(`/api/saldoUsdt?userId=${userId}`);
      if (!saldoResponse.ok) throw new Error('Erro ao buscar saldo');
      const saldoData = await saldoResponse.json();
      const saldoSpan = document.getElementById('saldoUsdt');
      if (saldoSpan) {
        saldoSpan.textContent = `${parseFloat(saldoData.saldoUsdt).toFixed(2)} USDT`;
      }
    } catch (err) {
      console.error('Erro ao atualizar saldo:');
    }
  }

  // Carrega saldo na inicialização
  if (userId) {
    atualizarSaldoUsdt();
  }
});

