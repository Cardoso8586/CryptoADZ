document.getElementById('btnBanners').addEventListener('click', () => {
    const targetNode = document.getElementById('banners');

    if (targetNode.dataset.loaded === "true") return;

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

    const observer = new MutationObserver(() => {
        document.querySelectorAll('#banners p').forEach(el => {
            if (el.innerText.includes('Expira em')) {
                const aleatorio = nomes[Math.floor(Math.random() * nomes.length)];
                el.innerText = aleatorio;
            }
        });
    });
    observer.observe(targetNode, { childList: true, subtree: true });

    const API_URL = '/api/banners/ativos';
    const bannersPerPage = 3;
    let currentPage = 1;
    let allBanners = [];

	function renderPage(page) {
	    targetNode.innerHTML = '';

	    const start = (page - 1) * bannersPerPage;
	    const end = start + bannersPerPage;
	    const bannersToShow = allBanners.slice(start, end);

	    bannersToShow.forEach(banner => {
	        const div = document.createElement('div');
	        div.className = 'banner';
	        div.setAttribute('data-id', banner.id);

	        div.innerHTML = `
	            <h3>${banner.titulo}</h3>
	            <a href="${banner.urlDestino}" target="_blank">
	                <img src="${banner.imagemUrl}" alt="${banner.titulo}">
	            </a>
	            <p>Expira em: ${banner.dataExpiracao}</p>
	        `;

	        // Listener para registrar visualização no clique
	        div.addEventListener('click', async () => {
	            const bannerId = banner.id;
	            const usuarioId = getUsuarioLogadoId();  // sua função para obter usuário

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
	                   // console.error("Erro ao registrar visualização:", erro);
	                   // alert("Erro ao registrar visualização: " + erro);
	                } else {
	                    const result = await response.json();
	                    console.log("Visualização registrada com sucesso:", result);
	                }
	            } catch (err) {
	                console.error("Erro na requisição:", err);
	                alert("Erro de rede ao registrar visualização.");
	            }
	        });

	        targetNode.appendChild(div);
	    });

	    renderPagination();
	}


    function renderPagination() {
        const totalPages = Math.ceil(allBanners.length / bannersPerPage);
        const paginationDiv = document.createElement('div');
        paginationDiv.className = 'pagination';

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement('button');
            btn.textContent = i;
            btn.disabled = (i === currentPage);
            btn.addEventListener('click', () => {
                currentPage = i;
                renderPage(currentPage);
            });
            paginationDiv.appendChild(btn);
        }

        targetNode.appendChild(paginationDiv);
    }

    fetch(API_URL)
        .then(response => {
            if (!response.ok) throw new Error('Erro na requisição');
            return response.json();
        })
        .then(banners => {
            targetNode.dataset.loaded = "true";
            if (banners.length === 0) {
                targetNode.innerHTML = '<p>"No current banners. Advertise with us!"</p>';
                return;
            }

            allBanners = banners;
            renderPage(currentPage);
        })
        .catch(error => {
            console.error('Erro ao carregar banners:', error);
            targetNode.innerHTML = '<p>Error loading banners.</p>';
        });
});
