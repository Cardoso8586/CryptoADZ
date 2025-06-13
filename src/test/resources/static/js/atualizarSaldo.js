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

 setInterval(atualizarSaldo, 10000);
 atualizarSaldo();