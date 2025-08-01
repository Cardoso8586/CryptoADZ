const API_URL = '/api/banners/ativos';
const CONTAINER = document.getElementById('adsContainer');
let banners = [];
let currentIndex = 0;

// Substituições aleatórias para o texto "Expira em"
const nomes = [
  '🔥 Patrocinado',
  '🚀 Publicidade',
  '💎 Anúncio Premium',
  '📢 Promoção',
  '⭐ Oferta Especial',
  '🧠 Recomendado',
  '🏆 Destaque',
  '✨ Sugestão do Dia'
];

// Observador para alterar os textos dinamicamente após inserção
const observer = new MutationObserver(() => {
  document.querySelectorAll('#adsContainer p').forEach(el => {
    if (el.innerText.includes('Expira em')) {
      const aleatorio = nomes[Math.floor(Math.random() * nomes.length)];
      el.innerText = aleatorio;
    }
  });
});
observer.observe(CONTAINER, { childList: true, subtree: true });

// Carrega os banners da API
fetch(API_URL)
  .then(response => {
    if (!response.ok) throw new Error('Erro na requisição');
    return response.json();
  })
  .then(data => {
    if (!Array.isArray(data)) throw new Error('Formato inválido retornado da API.');
    if (data.length === 0) {
      CONTAINER.innerHTML = '<p>Sem banners no momento. Anuncie conosco!</p>';
      return;
    }

    banners = data;
    exibirBannerSequencial(currentIndex);
  })
  .catch(error => {
    console.error('Erro ao carregar banners:', error);
    CONTAINER.innerHTML = '<p>Erro ao carregar banners.</p>';
  });

// Função para exibir um banner por vez com tempo individual
function exibirBannerSequencial(index) {
  const banner = banners[index];
  if (!banner) return;

  renderBanner(banner);

  const tempo = (banner.tempoExibicao || 10) * 1000; // Tempo individual ou 10s padrão

  setTimeout(() => {
    currentIndex = (currentIndex + 1) % banners.length;
    exibirBannerSequencial(currentIndex);
  }, tempo);
}

// Função para renderizar um banner
function renderBanner(banner) {
  CONTAINER.innerHTML = '';

  const div = document.createElement('div');
  div.className = 'ad-box';
  div.setAttribute('data-id', banner.id);
  div.style = 'margin-bottom: 15px; padding: 10px; border: 1px solid #ccc; border-radius: 10px; text-align: center; background: #fff;';

  div.innerHTML = `
    <h3>${banner.titulo}</h3>
    <a href="${banner.urlDestino}" target="_blank">
      <img src="${banner.imagemUrl}" alt="${banner.titulo}" style="max-width: 100%; height: auto; border-radius: 8px;">
    </a>
    <p style="color: #888; font-size: 14px;">Expira em: ${banner.dataExpiracao}</p>
  `;

  div.addEventListener('click', async () => {
    const bannerId = banner.id;
    const usuarioId = getUsuarioLogadoId();

    if (!usuarioId || !bannerId) {
      console.warn("Usuário ou banner ID ausente");
      return;
    }

    try {
      const response = await fetch(`/api/visualizacoes/banner/registrar?bannerId=${bannerId}&usuarioId=${usuarioId}&limitePorDia=1`, {
        method: 'POST'
      });

      if (!response.ok) {
        const erro = await response.text();
        console.warn(erro);
      } else {
        const result = await response.json();
        console.log("Visualização registrada com sucesso:", result);
      }
    } catch (err) {
      console.error("Erro na requisição:", err);
      alert("Erro de rede ao registrar visualização.");
    }
  });

  CONTAINER.appendChild(div);
}
