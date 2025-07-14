function atualizarSaldoUsdt() {
  fetch('/api/saldoUsdt')
    .then(res => {
      if (!res.ok) throw new Error("Erro na requisição saldoUsdt");
      return res.json();
    })
    .then(data => {
      console.log("Saldo USDT:", data);
      document.getElementById('saldoUsdt').innerText = data.saldoUsdt.toFixed(2);
    })
    .catch(err => console.error('Erro ao atualizar saldoUsdt:', err));
}

// Atualiza os saldos a cada 10 segundos
setInterval(atualizarSaldoUsdt, 10000);

// Atualiza imediatamente ao carregar a página
atualizarSaldoUsdt();

