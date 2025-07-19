document.addEventListener('DOMContentLoaded', () => {
  carregarHistoricoSaques();

  // Atualiza a cada 15 segundos (opcional)
  setInterval(carregarHistoricoSaques, 15000);
});

// Função auxiliar para obter o ID do usuário logado
function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}

// Função para buscar o histórico de saques confirmados
async function carregarHistoricoSaques() {
  const userId = getUsuarioLogadoId();
  const container = document.getElementById('historicoRetiradas');
  if (!userId || !container) return;

  try {
    const response = await fetch(`/api/saques/historico/${userId}`);
    if (!response.ok) throw new Error('Erro ao buscar histórico de saques');

    const saques = await response.json();
    atualizarHistoricoReal(container, saques);
  } catch (error) {
    console.error('Erro ao carregar histórico de saques:', error);
    container.innerHTML = '<div class="erro">Erro ao carregar histórico.</div>';
  }
}

// Função para exibir os saques na tela
function atualizarHistoricoReal(container, lista) {
  container.innerHTML = '';

  if (lista.length === 0) {
    container.textContent = 'Nenhuma retirada confirmada.';
    return;
  }

  lista.forEach((saque, index) => {
    const item = document.createElement('div');
    item.classList.add('historico-item');

    const valor = parseFloat(saque.valor_solicitado).toFixed(2);
    const data = new Date(saque.dataSolicitacao).toLocaleString('pt-BR');
    const status = saque.status;

    item.innerHTML = `<strong>#${index + 1}</strong>: R$ ${valor} | ${data} | Status: <span class="status">${status}</span>`;
    container.appendChild(item);
  });
}
