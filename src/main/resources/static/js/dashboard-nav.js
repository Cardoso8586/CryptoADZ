document.addEventListener('DOMContentLoaded', () => {
  const menuMap = {
    btnVerAnuncios: 'VerAnuncios',
    btnAnunciar: 'Anunciar',
    btnRanking: 'Ranking',
    btnSwap: 'Swap',
    btnBanners: 'bannersPublicitarios',
    btnWallet: 'wallet-container',
    btnMissoes: 'secaoMissoes',
    btnAvisos: 'Avisos',
	btnUsuario: 'painelUsuario'
	
	
  };

  const adsSection = document.querySelector('.ads-section');
  const cardAvisos = document.getElementById('cardAvisos');
  const btnAvisos = document.getElementById('btnAvisos');

  

  if (cardAvisos && btnAvisos) {
    cardAvisos.addEventListener('click', e => {
      e.preventDefault();
      btnAvisos.click();
    });
  }

  function esconderTodasSecoes() {
    Object.values(menuMap).forEach(id => {
      const secao = document.getElementById(id);
      if (secao) {
        secao.style.display = 'none';
        secao.style.visibility = 'hidden';
      }
    });
  }

  esconderTodasSecoes();

  const secaoInicial = document.getElementById('VerAnuncios');
  if (secaoInicial) {
    secaoInicial.style.display = 'block';
    secaoInicial.style.visibility = 'visible';
  }

  Object.entries(menuMap).forEach(([btnId, secaoId]) => {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener('click', e => {
      e.preventDefault();
      esconderTodasSecoes();

      const secao = document.getElementById(secaoId);
      if (secao) {
        secao.style.display = 'block';
        secao.style.visibility = 'visible';
      }

      if (adsSection) {
        if (secaoId === 'bannersPublicitarios' 
			|| secaoId === 'wallet-container'
		  
		
			
			) {
          adsSection.style.display = 'none';
        } else {
          adsSection.style.display = 'block';
        }
      }

      if (btnId === 'btnMissoes') {
        if (typeof carregarStatusMissoes === 'function') {
          carregarStatusMissoes();
        }
      }
    });
  });
});
