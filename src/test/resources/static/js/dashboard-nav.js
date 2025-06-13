document.addEventListener('DOMContentLoaded', () => {
  // Mapeia o botão para a seção correspondente
  const menuMap = {
    btnVerAnuncios: 'VerAnuncios',
    btnAnunciar: 'Anunciar',
    btnRanking: 'Ranking',
    btnSwap: 'Swap',
    btnPerfil: 'Perfil',
    btnWallet: 'Wallet',
    btnMissoes: 'secaoMissoes'  // Corrigido para o ID correto da seção Missões
  };

  // Função para esconder todas as seções
  function esconderTodasSeções() {
    Object.values(menuMap).forEach(id => {
      const section = document.getElementById(id);
      if (section) section.style.display = 'none';
    });
  }

  // Esconde todas as seções no início
  esconderTodasSeções();

  // Exibe uma seção padrão ao carregar a página (opcional)
  const secaoInicial = document.getElementById('VerAnuncios');
  if (secaoInicial) secaoInicial.style.display = 'block';

  // Adiciona evento de clique para cada botão
  Object.entries(menuMap).forEach(([btnId, secaoId]) => {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener('click', e => {
      e.preventDefault();

      esconderTodasSeções(); // Oculta todas as seções
      const secao = document.getElementById(secaoId);
      if (secao) secao.style.display = 'block'; // Mostra a seção correspondente
    });
  });
});

