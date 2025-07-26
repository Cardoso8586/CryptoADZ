document.addEventListener('DOMContentLoaded', function () {
  const menuMap = {
    btnVerAnuncios: 'VerAnuncios',
    btnAnunciar: 'Anunciar',
    btnRanking: 'RankingPlaceholder',
    btnSwap: 'Swap',
    btnBanners: 'bannersPublicitarios',
    btnWallet: 'wallet-container',
    btnMissoes: 'secaoMissoes',
    btnAvisos: 'Avisos'
  };

  const sidebarLinks = document.querySelectorAll('.sidebar ul li a');
  const contentSections = document.querySelectorAll('.content-section');
  const adsSection = document.querySelector('.ads-section');

  const cardAvisos = document.getElementById('cardAvisos');
  const btnAvisos = document.getElementById('btnAvisos');
  if (cardAvisos && btnAvisos) {
    cardAvisos.addEventListener('click', e => {
      e.preventDefault();
      btnAvisos.click();
    });
  }

  // Esconde todas as seções
  function esconderTodasSecoes() {
    contentSections.forEach(section => {
      section.classList.remove('active-section');
    });

    sidebarLinks.forEach(link => {
      link.classList.remove('active-nav-link');
    });
  }

  // Mostra a seção correta
  function showSection(targetId) {
    esconderTodasSecoes();

    const secao = document.getElementById(targetId);
    if (secao) {
      secao.classList.add('active-section');
    }

    const clickedLink = document.querySelector(`.sidebar ul li a[data-target="${targetId}"]`);
    if (clickedLink) {
      clickedLink.classList.add('active-nav-link');
    }

    // Controla visibilidade dos anúncios
    if (adsSection) {
      if (targetId === 'bannersPublicitarios' || targetId === 'wallet-container') {
        adsSection.style.display = 'none';
      } else {
        adsSection.style.display = 'block';
      }
    }

    // Lógica específica para cada seção
    if (targetId === 'secaoMissoes' && typeof carregarStatusMissoes === 'function') {
      carregarStatusMissoes();
    }
    if (targetId === 'Anunciar' && typeof carregarQuantidadeDeAnuncios === 'function') {
      carregarQuantidadeDeAnuncios();
    }

    // Para a seção Swap, adicionar estilo especial
    if (targetId === 'Swap') {
      document.body.classList.add('only-swap');
    } else {
      document.body.classList.remove('only-swap');
    }
  }

  // Inicialização: garante que só a seção padrão aparece
  esconderTodasSecoes();
  const defaultSectionId = 'VerAnuncios';
  showSection(defaultSectionId);

  // Eventos de clique do menu
  Object.entries(menuMap).forEach(([btnId, secaoId]) => {
    const btn = document.getElementById(btnId);
    if (!btn) {
      console.warn(`Botão com ID '${btnId}' não encontrado`);
      return;
    }
    btn.addEventListener('click', e => {
      e.preventDefault();
      showSection(secaoId);
    });
  });
});
