document.addEventListener('DOMContentLoaded', () => {
  carregarHistoricoSaques();

  // Atualiza o histórico a cada 15 segundos (opcional)
  setInterval(carregarHistoricoSaques, 15000);
});

async function carregarHistoricoSaques() {
  const userId = getUsuarioLogadoId();
  const container = document.getElementById('historicoRetiradas');
  if (!userId || !container) return;

  try {
    const response = await fetch(`/api/saque/historico/${userId}`);
    if (!response.ok) {
      const text = await response.text();
      throw new Error(`HTTP ${response.status}: ${text}`);
    }

    const saques = await response.json();
    atualizarHistoricoReal(container, saques);
  } catch (error) {
    console.error('Erro ao carregar histórico de saques:', error);
    container.innerHTML = '<div class="erro">Erro ao carregar histórico.</div>';
  }
}

function atualizarHistoricoReal(container, lista) {
  container.innerHTML = '';

  if (!lista.length) {
    container.textContent = 'Nenhuma retirada confirmada.';
    return;
  }

  const tabela = document.createElement('table');
  tabela.style.width = '100%';
  tabela.style.borderCollapse = 'collapse';

  // Cabeçalho atualizado com a coluna Status
  tabela.innerHTML = `
      <thead>
        <tr style="background-color:#f8f9fa; color:#333;">
          <th style="border: 1px solid #dee2e6; padding: 10px;">Data</th>
          <th style="border: 1px solid #dee2e6; padding: 10px;">Valor (USDT)</th>
          <th style="border: 1px solid #dee2e6; padding: 10px;">Status</th> <!-- Coluna nova -->
        </tr>
      </thead>
      <tbody></tbody>
    `;

  const corpo = tabela.querySelector('tbody');

  lista.forEach((saque) => {
    const valorNum = parseFloat(saque.valorUSDT);
    const valor = isNaN(valorNum) ? '0.00' : valorNum.toFixed(2);

    const dataObj = new Date(saque.dataHora);
    const data = isNaN(dataObj.getTime()) ? 'Data inválida' : dataObj.toLocaleString('pt-BR');

    const status = saque.status || 'Desconhecido'; // Pega o status do objeto ou valor padrão

    const linha = document.createElement('tr');
    linha.innerHTML = `
      <td style="border: 1px solid #ccc; padding: 8px;">${data}</td>
      <td style="border: 1px solid #ccc; padding: 8px;">${valor}</td>
      <td style="border: 1px solid #ccc; padding: 8px;">${status}</td> <!-- Status -->
    `;
    corpo.appendChild(linha);
  });

  container.appendChild(tabela);
}

function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}
