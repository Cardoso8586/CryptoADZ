// deposito.js

// Endereços
const USDT_ADDRESS = "0x55d398326f99059fF775485246999027B3197955";
const PLATFORM_RECEIVER = "0x5EDE4a58cDEaa4C6626d114fD9eF623f2728c706";

// ABI do contrato
const USDT_ABI = [
  "function transfer(address to, uint amount) public returns (bool)",
  "function allowance(address owner, address spender) public view returns (uint)",
  "function approve(address spender, uint amount) public returns (bool)",
  "function decimals() view returns (uint8)",
  "function balanceOf(address owner) public view returns (uint256)"
];

async function depositUSDT(amount) {
  try {
    const usdt = new ethers.Contract(USDT_ADDRESS, USDT_ABI, signer);
    const decimals = await usdt.decimals();
    const amountInWei = ethers.utils.parseUnits(amount.toString(), decimals);

    const balance = await usdt.balanceOf(userAccount);
    if (balance.lt(amountInWei)) {
      status.innerText = "❌ Saldo insuficiente de USDT para este depósito.";
      Swal.fire({
        icon: 'error',
        title: 'Saldo Insuficiente!',
        text: '❌ Saldo insuficiente de USDT para este depósito.',
        confirmButtonText: 'Entendi',
        background: '#fff',
        color: '#000'
      });
      return;
    }

    status.innerText = "⏳ Enviando transação...";
    Swal.fire({
      title: '⏳ Enviando transação...',
      text: 'Por favor, aguarde.',
      allowOutsideClick: false,
      allowEscapeKey: false,
      showConfirmButton: false,
      background: '#fff',
      color: '#000',
      didOpen: () => Swal.showLoading()
    });

    const timeout = new Promise((_, reject) =>
      setTimeout(() => reject(new Error("Tempo esgotado")), 30000)
    );

    const txPromise = usdt.transfer(PLATFORM_RECEIVER, amountInWei);
    const tx = await Promise.race([txPromise, timeout]);

    status.innerText = "⏳ Transação enviada. Aguardando confirmação...";
    Swal.fire({
      title: '⏳ Transação enviada',
      text: 'Aguardando confirmação...',
      allowOutsideClick: false,
      allowEscapeKey: false,
      showConfirmButton: false,
      didOpen: () => Swal.showLoading(),
      background: '#fff',
      color: '#000'
    });

    const receipt = await tx.wait();
    const userId = getUsuarioLogadoId();

    if (receipt.status === 1) {
      status.innerText = "✅ Depósito confirmado com sucesso!";
      Swal.close();
      Swal.fire({
        icon: 'success',
        title: 'Depósito Confirmado!',
        text: '✅ Depósito confirmado com sucesso!',
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
        background: '#fff',
        color: '#000'
      });

      await fetch('/api/depositos/fazer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userId,
          valor: amount,
          confirmado: true
        })
      });

    } else {
      status.innerText = "❌ Transação rejeitada ou falhou.";
      Swal.fire({
        icon: 'error',
        title: '❌ Transação falhou',
        text: 'Transação rejeitada ou falhou.',
        confirmButtonText: 'OK',
        background: '#fff',
        color: '#000'
      });
    }

  } catch (err) {
    console.error("Verifique seu saldo de USDT (BEP20) ou feche a carteira MetaMask e tente novamente.");
    Swal.close();
    if (err.message === "Tempo esgotado") {
      status.innerText = "⏱️ Tempo esgotado. Tente novamente.";
      Swal.fire({
        icon: 'error',
        title: 'Tempo esgotado',
        text: 'A transação demorou muito ou foi cancelada.',
        timer: 4000,
        timerProgressBar: true,
        showConfirmButton: false,
        background: '#fff',
        color: '#000'
      });
    } else {
      status.innerText = "❌ Erro ao fazer depósito: ";
      Swal.fire({
        icon: 'error',
        title: 'Erro',
		text: 'Verifique seu saldo de USDT (BEP20) ou feche a carteira MetaMask e tente novamente.',
        confirmButtonText: 'OK',
        background: '#fff',
        color: '#000'
      });
    }
  }
}

function withdraw(amount) {
  status.innerText = `📤 Saque de ${amount} USDT solicitado. Esta função será implementada em breve.`;
}

document.getElementById('depositForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  if (!userAccount) {
    status.innerText = "⚠️ Conecte sua carteira MetaMask antes.";
    Swal.fire({
      icon: 'warning',
      title: '⚠️ Carteira não conectada',
      text: 'Conecte sua carteira MetaMask antes de continuar.',
      confirmButtonText: 'OK',
      background: '#fff',
      color: '#000'
    });
    return;
  }

  const amount = parseFloat(document.getElementById('depositAmount').value);
  if (isNaN(amount) || amount < 3) {
    status.innerText = "⚠️ O valor mínimo para depósito é 3 USDT.";
    return;
  }

  await depositUSDT(amount);
});

document.getElementById('withdrawForm').addEventListener('submit', (e) => {
  e.preventDefault();
  if (!userAccount) {
    status.innerText = "⚠️ Conecte sua carteira MetaMask antes.";
    Swal.fire({
      icon: 'warning',
      title: '⚠️ Carteira não conectada',
      text: 'Conecte sua carteira MetaMask antes de continuar.',
      confirmButtonText: 'OK',
      background: '#fff',
      color: '#000'
    });
    return;
  }

  const amount = parseFloat(document.getElementById('withdrawAmount').value);
  withdraw(amount);
});

function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}
