document.addEventListener('DOMContentLoaded', function () {
  function getUsuarioLogadoId() {
    const meta = document.querySelector('meta[name="user-id"]');
    return meta ? parseInt(meta.getAttribute('content')) : null;
  }

  async function carregarHistoricoDepositos(userId) {
    if (!userId) {
      document.getElementById('historicoDepositos').innerText = 'Usuário não identificado.';
      return;
    }

    try {
      const response = await fetch(`/api/depositos/historico/${userId}`);
      if (!response.ok) throw new Error('Erro ao carregar histórico');
      const depositos = await response.json();

      let html = `<table border="1" style="width: 100%; border-collapse: collapse;">
                    <thead>
                      <tr>
                        <th>Valor</th>
                        <th>Data</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>`;

      if (depositos.length === 0) {
        html += `<tr><td colspan="3" style="text-align:center;">Nenhum depósito encontrado.</td></tr>`;
      } else {
        depositos.forEach(deposito => {
          const dataFormatada = new Date(deposito.dataDeposito).toLocaleString();
          html += `<tr>
                     <td>${parseFloat(deposito.valor).toFixed(2)}</td>
                     <td>${dataFormatada}</td>
                     <td>${deposito.status}</td>
                   </tr>`;
        });
      }

      html += `</tbody></table>`;
      document.getElementById('historicoDepositos').innerHTML = html;
	

    } catch (error) {
      document.getElementById('historicoDepositos').innerText = 'Erro ao carregar histórico.';
      console.error("Erro ao buscar histórico:", error);
    }
  }

  const userId = getUsuarioLogadoId();
  if (userId) {
    carregarHistoricoDepositos(userId); // chama ao carregar a página

    // Atualiza a cada 100 segundos (100000 milissegundos)
    setInterval(() => {
      carregarHistoricoDepositos(userId);
    }, 10000);
  } else {
    document.getElementById('historicoDepositos').innerText = 'Usuário não identificado.';
  }
});