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

  lista.forEach((saque, index) => {
    const valorNum = parseFloat(saque.valorUSDT);
    const valor = isNaN(valorNum) ? '0.00' : valorNum.toFixed(2);

    const dataObj = new Date(saque.data);
    const data = isNaN(dataObj.getTime()) ? 'Data inválida' : dataObj.toLocaleString('pt-BR');

    const status = saque.status || 'Desconhecido';

    const item = document.createElement('div');
    item.classList.add('historico-item');
    item.innerHTML = `<strong>#${index + 1}</strong>: R$ ${valor} | ${data} | Status: <span class="status">${status}</span>`;
    container.appendChild(item);
  });
}


// Função para obter o ID do usuário logado da meta tag
function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}
