document.addEventListener('DOMContentLoaded', () => {
  let banners = [];
  let indexTopo = 0;
  let indexRodape = 1;

  async function carregarBannersRanking() {
    try {
      const response = await fetch('/api/banners/ativos');
      if (!response.ok) throw new Error('Erro ao buscar banners');

      banners = await response.json();
      console.log('Banners para ranking:', banners);

      if (banners.length === 0) {
     
        document.getElementById('bannerRankingRodape').innerHTML = '<p>"Nenhum banner disponível."</p>';
        return;
      }

    
      exibirBannerRodape();
    } catch (error) {
      console.error('Erro ao carregar banners de ranking:', error);
    }
  }

 
  function exibirBannerRodape() {
    if (banners.length === 0) return;
    const banner = banners[indexRodape % banners.length];
    const div = document.getElementById('bannerRankingRodape');

    div.innerHTML = gerarHTMLBanner(banner);
    registrarVisualizacao(banner.id);

    const tempo = (banner.tempoExibicao || 10) * 1000;

    setTimeout(() => {
      let proximo;
      do {
        proximo = (indexRodape + 1) % banners.length;
        indexRodape = proximo;
      } while (indexRodape === indexTopo && banners.length > 1);

      exibirBannerRodape();
    }, tempo);
  }

  function gerarHTMLBanner(banner) {
    return `
      <div style="background:#fff; padding:12px; border-radius:12px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
        <div style="text-align:center; font-size:20px; font-weight:bold; margin-bottom:10px; color:#333;">
          ${banner.titulo}
        </div>
        <a href="${banner.urlDestino}" target="_blank" style="display:block; text-decoration:none;">
          <img src="${banner.imagemUrl}" alt="${banner.titulo}" style="width:100%; height:280px; object-fit:contain; border-radius:10px;" />
        </a>
      </div>
    `;
  }

  async function registrarVisualizacao(bannerId) {
    const usuarioId = getUsuarioLogadoId(); // sua lógica aqui
    if (!usuarioId || !bannerId) return;

    try {
      const response = await fetch(`/api/visualizacoes/banner/registrar?bannerId=${bannerId}&usuarioId=${usuarioId}&limitePorDia=1`, {
        method: 'POST'
      });
      if (!response.ok) {
        const erro = await response.text();
        console.warn("Erro ao registrar visualização:", erro);
      } else {
        const result = await response.json();
        console.log("Visualização registrada:", result);
      }
    } catch (err) {
      console.error("Erro ao registrar visualização:", err);
    }
  }

  carregarBannersRanking();
});
