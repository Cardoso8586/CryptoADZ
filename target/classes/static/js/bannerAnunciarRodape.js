document.addEventListener('DOMContentLoaded', () => {
  let banners = [];
  let indexAtual = 0;

  async function carregarBannersUsuario() {
    try {
      const response = await fetch('/api/banners/ativos');
      if (!response.ok) throw new Error('Erro ao buscar banners');

      banners = await response.json();
      console.log('Banners para usuário:', banners);

      if (banners.length === 0) {
        document.getElementById('bannerAnunciarRodape').innerHTML = '<p>Nenhum banner disponível.</p>';
        return;
      }

      exibirBannerUsuario();
    } catch (error) {
      console.error('Erro ao carregar banners de usuário:', error);
    }
  }

  function exibirBannerUsuario() {
    if (banners.length === 0) return;
    const banner = banners[indexAtual % banners.length];
    const div = document.getElementById('bannerAnunciarRodape');

    div.innerHTML = gerarHTMLBanner(banner);
    registrarVisualizacao(banner.id);

    const tempo = (banner.tempoExibicao || 10) * 1000;

    setTimeout(() => {
      indexAtual = (indexAtual + 1) % banners.length;
      exibirBannerUsuario();
    }, tempo);
  }

  function gerarHTMLBanner(banner) {
    return `
      <div style="
        display: flex;
        align-items: center;
        gap: 20px;
        background: #fff;
        padding: 12px;
        border-radius: 12px;
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        color: #333;
      ">
        <a href="${banner.urlDestino}" target="_blank" style="flex-shrink: 0;">
          <img src="${banner.imagemUrl}" alt="${banner.titulo}" style="width: 150px; height: 150px; object-fit: cover; border-radius: 10px;" />
        </a>
        <div style="flex: 1;">
          <div style="font-size: 20px; font-weight: bold; margin-bottom: 10px;">
            ${banner.titulo}
          </div>
          <a href="${banner.urlDestino}" target="_blank" style="color: #00cc99; text-decoration: underline;">
            Visitar
          </a>
        </div>
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

  carregarBannersUsuario();
});
