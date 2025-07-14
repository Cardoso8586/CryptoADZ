const API_URL = '/api/banners/ativos';
const CONTAINER = document.getElementById('adsContainer');
const ITEMS_PER_PAGE = 4;
let banners = [];
let currentPage = 1;

// Cria e adiciona o container de paginação
const PAGINATION = document.createElement('div');
PAGINATION.id = 'pagination';
PAGINATION.style.marginTop = '20px';
PAGINATION.style.textAlign = 'center';
CONTAINER.parentElement.appendChild(PAGINATION);

// Substituições aleatórias para o texto "Expira em"
const nomes = [
  '🔥 Sponsored',
  '🚀 Advertising',
  '💎 Premium Ad',
  '📢 Promotion',
  '⭐ Special Offer',
  '🧠 Recommended',
  '🏆 Featured',
  '✨ Suggestion of the Day'
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
      CONTAINER.innerHTML = '<p>"No current banners. Advertise with us!"</p>';
      return;
    }

    banners = data;
    renderPage(1);
    createPagination();
  })
  .catch(error => {
    console.error('Erro ao carregar banners:', error);
    CONTAINER.innerHTML = '<p>Erro ao carregar banners.</p>';
  });

  function renderPage(page) {
    CONTAINER.innerHTML = '';
    const start = (page - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;
    const currentItems = banners.slice(start, end);

    currentItems.forEach(banner => {
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

      // Aqui adicionamos o listener para registrar a visualização no clique
      div.addEventListener('click', async () => {
        const bannerId = banner.id;
        const usuarioId = getUsuarioLogadoId();

        if (!usuarioId || !bannerId) {
          console.warn("Usuário ou banner ID ausente");
          return;
        }

        console.log(`Registrando visualização do banner ${bannerId} para usuário ${usuarioId}`);

        try {
          const response = await fetch(`/api/visualizacoes/banner/registrar?bannerId=${bannerId}&usuarioId=${usuarioId}&limitePorDia=1`, {
            method: 'POST'
          });

          if (!response.ok) {
            const erro = await response.text();
          // console.error(erro);
          // alert(erro);
          } else {
            const result = await response.json();
            console.log("Visualização registrada com sucesso:", result);
            // Aqui pode atualizar saldo ou mostrar recompensa, se quiser
          }
        } catch (err) {
          console.error("Erro na requisição:", err);
          alert("Erro de rede ao registrar visualização.");
        }
      });

      CONTAINER.appendChild(div);
    });
  }


// Cria os botões de paginação
function createPagination() {
  PAGINATION.innerHTML = '';
  const totalPages = Math.ceil(banners.length / ITEMS_PER_PAGE);

  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement('button');
    btn.innerText = i;
    btn.style.margin = '0 5px';
    btn.style.padding = '6px 12px';
    btn.style.border = '1px solid #ccc';
    btn.style.borderRadius = '6px';
    btn.style.backgroundColor = i === currentPage ? '#3b82f6' : '#fff';
    btn.style.color = i === currentPage ? '#fff' : '#000';
    btn.style.cursor = 'pointer';

    btn.onclick = () => {
      currentPage = i;
      renderPage(i);
      createPagination();
    };

    PAGINATION.appendChild(btn);
  }
}

