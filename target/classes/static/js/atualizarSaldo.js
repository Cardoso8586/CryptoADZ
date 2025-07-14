function atualizarSaldo() {
  fetch('/api/saldo')
    .then(res => {
      if (!res.ok) throw new Error("Erro na requisição");
      return res.json();
    })
    .then(data => {
      document.getElementById('saldoTokens').innerText = data.saldo.toFixed(2);
    })
    .catch(err => console.error('Erro ao atualizar saldo:', err));
}


// Atualiza os saldos a cada 10 segundos

setInterval(atualizarSaldo, 10000);

// Atualiza imediatamente ao carregar a página

atualizarSaldo();