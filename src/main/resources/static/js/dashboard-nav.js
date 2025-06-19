document.addEventListener('DOMContentLoaded', () => {
  const menuMap = {
    btnVerAnuncios: 'VerAnuncios',
    btnAnunciar: 'Anunciar',
    btnRanking: 'Ranking',
    btnSwap: 'Swap',
    btnPerfil: 'Perfil',
    btnWallet: 'Wallet',
    btnMissoes: 'secaoMissoes',
    btnAvisos: 'Avisos'
  };

  function esconderTodasSecoes() {
    Object.values(menuMap).forEach(id => {
      const secao = document.getElementById(id);
      if (secao) secao.style.display = 'none';
    });
  }

  // Esconde tudo no começo
  esconderTodasSecoes();

  // Exibe a seção inicial
  const secaoInicial = document.getElementById('VerAnuncios');
  if (secaoInicial) secaoInicial.style.display = 'block';

  // Evento clique nos botões do menu
  Object.entries(menuMap).forEach(([btnId, secaoId]) => {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener('click', e => {
      e.preventDefault();
      esconderTodasSecoes();
      const secao = document.getElementById(secaoId);
      if (secao) secao.style.display = 'block';
    });
  });

  // Configura o cardAvisos para abrir a seção Avisos
  const cardAvisos = document.getElementById('cardAvisos');
  if (cardAvisos) {
    cardAvisos.addEventListener('click', () => {
      esconderTodasSecoes();
      const secaoAvisos = document.getElementById('Avisos');
      if (secaoAvisos) secaoAvisos.style.display = 'block';
    });
  }

  // --- Aqui seu código para carregar e alternar avisos ---

  let avisos = [];
  let avisoAtual = 0;
  const textoAvisos = document.getElementById('textoAvisos');

  async function carregarAvisos() {
    try {
      const response = await fetch('/api/avisos');
      if (!response.ok) throw new Error('Erro ao buscar avisos');

      avisos = await response.json();

      if (avisos.length === 0) {
        textoAvisos.textContent = "Nenhum aviso no momento.";
      } else {
        exibirAvisoAtual();
        setInterval(alternarAviso, 10000);
      }

    } catch (error) {
      textoAvisos.textContent = 'Erro ao carregar avisos.';
      console.error(error);
    }
  }

  function exibirAvisoAtual() {
    const aviso = avisos[avisoAtual];
    textoAvisos.textContent = aviso.titulo;
  }

  function alternarAviso() {
    if (avisos.length === 0) return;
    avisoAtual = (avisoAtual + 1) % avisos.length;
    exibirAvisoAtual();
  }

  carregarAvisos();

});

