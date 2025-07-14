document.addEventListener('DOMContentLoaded', () => {
  const tabs = document.querySelectorAll('.wallet-tab');
  const sections = document.querySelectorAll('.wallet-section');

  // Função para ativar aba e seção correspondente
  function ativarAba(targetId) {
    tabs.forEach(tab => {
      tab.classList.toggle('active', tab.dataset.target === targetId);
    });

    sections.forEach(section => {
      section.style.display = section.id === targetId ? 'block' : 'none';
    });
  }

  // Inicializa com a aba "depositos" ativa
  ativarAba('depositos');

  // Troca de abas
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      ativarAba(tab.dataset.target);
    });
  });

  // Histórico simples
  const historicoDepositos = document.getElementById('historicoDepositos');
  const historicoRetiradas = document.getElementById('historicoRetiradas');
  const formDeposito = document.getElementById('formDeposito');
  const formRetirada = document.getElementById('formRetirada');

  const depositos = [];
  const retiradas = [];

  formDeposito.addEventListener('submit', (e) => {
    e.preventDefault();
    const valor = parseFloat(document.getElementById('valorDeposito').value);
    if (valor > 0) {
      depositos.push(valor);
      atualizarHistorico(historicoDepositos, depositos, 'Depósito');
      formDeposito.reset();
    } else {
      alert('Por favor, insira um valor de depósito válido.');
    }
  });

  formRetirada.addEventListener('submit', (e) => {
    e.preventDefault();
    const valor = parseFloat(document.getElementById('valorRetirada').value);
    if (valor > 0) {
      retiradas.push(valor);
      atualizarHistorico(historicoRetiradas, retiradas, 'Retirada');
      formRetirada.reset();
    } else {
      alert('Por favor, insira um valor de retirada válido.');
    }
  });

  function atualizarHistorico(container, lista, tipo) {
    container.innerHTML = '';
    lista.forEach((valor, index) => {
      const item = document.createElement('div');
      item.textContent = `${tipo} #${index + 1}: R$ ${valor.toFixed(2)}`;
      container.appendChild(item);
    });
  }

  // Chamada imediata do status
  atualizarStatusDeposito();

  // Atualização periódica a cada 10 segundos
  setInterval(atualizarStatusDeposito, 1000);
});

// Função auxiliar para obter ID do usuário logado
function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}

// Consulta e atualiza o status do depósito
async function atualizarStatusDeposito() {
  const userId = getUsuarioLogadoId();
  if (!userId) return;

  try {
    console.log('Consultando status do depósito para o usuário:', userId);
    const response = await fetch(`/api/depositos/status/${userId}`);
    if (!response.ok) throw new Error('Erro ao consultar status');

    const status = await response.text();
    console.log('Status recebido:', status);

    document.getElementById('statusDeposito').innerText = status;

    const aguardando = document.getElementById('mensagemAguardando');
    const confirmado = document.getElementById('mensagemConfirmado');
    const formDeposito = document.getElementById('formDeposito');

    if (status === 'PENDENTE') {
      aguardando.style.display = 'block';
      confirmado.style.display = 'none';
      formDeposito.style.display = 'none';  // Esconde o formulário de depósito
    } else if (status === 'CONFIRMADO') {
      aguardando.style.display = 'none';
      confirmado.style.display = 'block';
      formDeposito.style.display = 'block'; // Mostra o formulário de depósito
    } else {
      aguardando.style.display = 'none';
      confirmado.style.display = 'none';
      formDeposito.style.display = 'block'; // Mostra o formulário de depósito
    }

  } catch (error) {
    console.error('Erro na atualização de status:', error);
  }
}

