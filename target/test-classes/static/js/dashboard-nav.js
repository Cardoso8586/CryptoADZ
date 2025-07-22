document.addEventListener('DOMContentLoaded', () => {
  // Mapeamento dos botões para as respectivas seções
  const menuMap = {
    btnVerAnuncios: 'VerAnuncios',
    btnAnunciar: 'Anunciar',
    btnRanking: 'Ranking',
    btnSwap: 'Swap',
    btnBanners: 'bannersPublicitarios',
    btnWallet: 'wallet-container',
    btnMissoes: 'secaoMissoes',
    btnAvisos: 'Avisos'
  };

  // Seção que exibe os anúncios (para ocultar em certas seções)
  const adsSection = document.querySelector('.ads-section');

  // Função que esconde todas as seções listadas no menuMap
  function esconderTodasSecoes() {
    Object.values(menuMap).forEach(id => {
      const secao = document.getElementById(id);
      if (secao) secao.style.display = 'none';
    });
  }

  // Esconder todas seções inicialmente
  esconderTodasSecoes();

  // Mostrar a seção inicial (VerAnuncios)
  const secaoInicial = document.getElementById('VerAnuncios');
  if (secaoInicial) secaoInicial.style.display = 'block';

  // Adiciona evento de clique em cada botão para alternar seções
  Object.entries(menuMap).forEach(([btnId, secaoId]) => {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener('click', e => {
      e.preventDefault();
      esconderTodasSecoes();

      const secao = document.getElementById(secaoId);
      if (secao) secao.style.display = 'block';

      // Controla visibilidade da seção de anúncios
      if (adsSection) {
        if (secaoId === 'bannersPublicitarios' || secaoId === 'wallet-container') {
          adsSection.style.display = 'none';
        } else {
          adsSection.style.display = 'block';
        }
      }

      // Ação extra para missões: recarregar status se existir função
      if (btnId === 'btnMissoes') {
        if (typeof carregarStatusMissoes === 'function') {
          carregarStatusMissoes();
        }
      }
    });
  });

  // Clique especial no cardAvisos que também mostra a seção de Avisos
  const cardAvisos = document.getElementById('cardAvisos');
  if (cardAvisos) {
    cardAvisos.addEventListener('click', () => {
      esconderTodasSecoes();
      const secaoAvisos = document.getElementById('Avisos');
      if (secaoAvisos) secaoAvisos.style.display = 'block';

      if (adsSection) {
        adsSection.style.display = 'block';
      }
    });
  }

  // --- Código para carregar e rotacionar avisos ---
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
        setInterval(alternarAviso, 10000); // alterna a cada 10s
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

  // Inicializa carregamento de avisos
  carregarAvisos();
});
