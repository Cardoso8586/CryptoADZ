// deposito.js

// Endereços
const USDT_ADDRESS = "0x55d398326f99059fF775485246999027B3197955";
const PLATFORM_RECEIVER = "0xbc398989648b4b8e986f69c16a93889e83269e85";
                         //   MEXC

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
	  didOpen: () => {
	    Swal.showLoading();

	    // Fecha o Swal automaticamente após 30 segundos
	    setTimeout(() => {
	      Swal.close();
	      Swal.fire({
	        icon: 'error',
	        title: 'Tempo esgotado',
	        text: 'A transação demorou muito para ser confirmada ou foi cancelada.',
	        timer: 4000,
	        timerProgressBar: true,
	        showConfirmButton: false,
	        background: '#fff',
	        color: '#000'
	      });
	    }, 30000); // 30000 ms = 30 segundos
	  }
	});


    const tx = await usdt.transfer(PLATFORM_RECEIVER, amountInWei);
    status.innerText = "⏳ Transação enviada. Aguardando confirmação...";
	Swal.fire({
	  title: '⏳ Transação enviada',
	  text: 'Aguardando confirmação...',
	  allowOutsideClick: false,
	  allowEscapeKey: false,
	  showConfirmButton: false,
	  didOpen: () => {
	    Swal.showLoading();
	  },
	  background: '#fff',
	  color: '#000'
	});
	
    const receipt = await tx.wait();
    const userId = getUsuarioLogadoId();

    if (receipt.status === 1) {
      status.innerText = "✅ Depósito confirmado com sucesso!";
	  
	  // Fecha o alerta de carregamento
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
    console.error("Erro detalhado:", err);
    if (err.code === "UNPREDICTABLE_GAS_LIMIT") {
      status.innerText = "❌ Erro: Transação pode falhar. Verifique se você tem saldo suficiente ou tente um valor menor.";
    } else if (err.code === "CALL_EXCEPTION") {
      status.innerText = "❌ Erro: A chamada foi revertida. Isso pode acontecer por falta de permissão ou saldo.";
    } else {
      status.innerText = "❌ Erro ao fazer depósito: " + (err.reason || err.message);
	  
    }
  }
}

function withdraw(amount) {
  status.innerText = `📤 Saque de ${amount} USDT solicitado. Esta função será implementada em breve.`;
}

// Formulário de depósito
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

// Formulário de saque
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

// Função auxiliar
function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}
