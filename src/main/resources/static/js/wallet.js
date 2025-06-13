// wallet.js

document.addEventListener('DOMContentLoaded', () => {
  const tabs = document.querySelectorAll('.wallet-tab');
  const sections = document.querySelectorAll('.wallet-section');

  // Função para ativar aba e seção correspondente
  function ativarAba(targetId) {
    tabs.forEach(tab => {
      tab.classList.toggle('active', tab.dataset.target === targetId);
    });
    sections.forEach(section => {
      section.classList.toggle('active', section.id === targetId);
    });
  }

  // Inicializa com a aba "depositos" ativa
  ativarAba('depositos');

  // Adiciona evento de clique para trocar abas
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      ativarAba(tab.dataset.target);
    });
  });

  // Histórico simples para depósitos e retiradas
  const historicoDepositos = document.getElementById('historicoDepositos');
  const historicoRetiradas = document.getElementById('historicoRetiradas');

  const formDeposito = document.getElementById('formDeposito');
  const formRetirada = document.getElementById('formRetirada');

  // Array para guardar os valores (pode trocar por backend ou localStorage)
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
      alert('Please enter a valid deposit amount.');
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
      alert('Please enter a valid withdrawal amount.');
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
});
