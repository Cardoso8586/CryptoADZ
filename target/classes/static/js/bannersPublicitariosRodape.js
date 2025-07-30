document.addEventListener('DOMContentLoaded', () => {
  let listaBanners = [];
  let indiceAtual = 0;

  async function buscarBannersAtivos() {
    try {
      const resposta = await fetch('/api/banners/ativos');
      if (!resposta.ok) throw new Error('Erro ao obter banners');

      listaBanners = await resposta.json();
      console.log('Banners carregados:', listaBanners);

      if (listaBanners.length === 0) {
        document.getElementById('bannersPublicitariosRodape').innerHTML = '<p>Nenhum anúncio disponível.</p>';
        return;
      }

      exibirBannerAtual();
    } catch (erro) {
      console.error('Erro ao buscar banners:', erro);
    }
  }

  function exibirBannerAtual() {
    if (listaBanners.length === 0) return;

    const banner = listaBanners[indiceAtual % listaBanners.length];
    const container = document.getElementById('bannersPublicitariosRodape');

    container.innerHTML = gerarHTMLAnuncio(banner);
    registrarExibicao(banner.id);

    const tempoExibicao = (banner.tempoExibicao || 10) * 1000;

    setTimeout(() => {
      indiceAtual = (indiceAtual + 1) % listaBanners.length;
      exibirBannerAtual();
    }, tempoExibicao);
  }

  function gerarHTMLAnuncio(banner) {
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

  async function registrarExibicao(bannerId) {
    const usuarioId = getUsuarioLogadoId(); // substitua por sua lógica real
    if (!usuarioId || !bannerId) return;

    try {
      const resposta = await fetch(`/api/visualizacoes/banner/registrar?bannerId=${bannerId}&usuarioId=${usuarioId}&limitePorDia=1`, {
        method: 'POST'
      });

      if (!resposta.ok) {
        const erroTexto = await resposta.text();
        console.warn("Falha ao registrar visualização:", erroTexto);
      } else {
        const resultado = await resposta.json();
        console.log("Visualização registrada com sucesso:", resultado);
      }
    } catch (erro) {
      console.error("Erro ao registrar visualização:", erro);
    }
  }

  buscarBannersAtivos();
});
