document.getElementById('btnBanners').addEventListener('click', () => {
  let banners = [];
  let indexEsquerdo = 0;
  let indexDireito = 1;

  async function carregarBanners() {
    try {
      const response = await fetch('/api/banners/ativos');
      if (!response.ok) throw new Error('Erro ao buscar banners');

      banners = await response.json();
      console.log('Banners recebidos:', banners);

      if (banners.length === 0) {
        document.getElementById('bannerEsquerdo').innerHTML = '<p>"No current banners. Advertise with us!"</p>';
        document.getElementById('bannerDireito').innerHTML = '<p>"No current banners. Advertise with us!"</p>';
        return;
      }

      exibirBannerEsquerdo();
      exibirBannerDireito();

    } catch (error) {
      console.error('Erro ao carregar banners:', error);
    }
  }

  function exibirBannerEsquerdo() {
    if (banners.length === 0) return;

    const banner = banners[indexEsquerdo % banners.length];
    const div = document.getElementById('bannerEsquerdo');

    div.innerHTML = gerarHTMLBanner(banner);
    registrarVisualizacao(banner.id);

    const tempo = (banner.tempoExibicao || 10) * 1000;
	
	
	setTimeout(() => {
	  let proximo;
	  do {
	    proximo = (indexEsquerdo + 1) % banners.length;
	    indexEsquerdo = proximo;
	  } while (indexEsquerdo === indexDireito && banners.length > 1);

	  exibirBannerEsquerdo();
	}, tempo);

  }

  function exibirBannerDireito() {
    if (banners.length === 0) return;

    const banner = banners[indexDireito % banners.length];
    const div = document.getElementById('bannerDireito');

    div.innerHTML = gerarHTMLBanner(banner);
    registrarVisualizacao(banner.id);

    const tempo = (banner.tempoExibicao || 10) * 1000;

   setTimeout(() => {
	  let proximo;
	  do {
	    proximo = (indexDireito + 1) % banners.length;
	    indexDireito = proximo;
	  } while (indexDireito === indexEsquerdo && banners.length > 1);

	  exibirBannerDireito();
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
    const usuarioId = getUsuarioLogadoId(); // coloque sua lógica real aqui

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

  carregarBanners();
});

