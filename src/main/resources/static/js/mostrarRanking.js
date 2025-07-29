function preencherRankingLista(dadosRanking) {
  const lista = document.getElementById("rankingLista");
  lista.innerHTML = "";

  dadosRanking.forEach((item, index) => {
    const div = document.createElement("div");
    div.className = "ranking-item";
    div.innerHTML = `
      <span>${index + 1}</span>
      <span>${item.nome || item.username}</span>
      <span>${item.visualizacoes || item.quantidadeVisualizacaoSemanal}</span>
    `;
    lista.appendChild(div);
  });
}

function preencherMinhaPosicao(data) {
  const divPosicao = document.getElementById('posicaoUsuario');
  if (!divPosicao) return;

  const visualizacoes = data.quantidadeVisualizacaoSemanal ?? data.visualizacoes ?? '0';

  divPosicao.innerHTML = `
    Sua posição atual no ranking semanal é: <strong>#${data.posicao}</strong><br/>
    Visualizações nesta semana: <strong>${visualizacoes}</strong>
  `;
}


function esperarAbaAnuncio(callback) {
  const intervalo = setInterval(() => {
    const sec = document.getElementById("Ranking");
    if (sec && sec.offsetParent !== null) {
      clearInterval(intervalo);
      callback();
    }
  }, 100);
}

function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}

document.addEventListener("DOMContentLoaded", () => {
  esperarAbaAnuncio(() => {
    fetch("/api/ranking-semanal")
      .then(res => res.json())
      .then(data => preencherRankingLista(data))
      .catch(err => {
        console.error("Erro ao carregar ranking:", err);
        document.getElementById("rankingLista").innerHTML =
          `<div class="ranking-item"><span>-</span><span>Erro</span><span>-</span></div>`;
      });

    const usuarioId = getUsuarioLogadoId();

    if (usuarioId) {
      fetch(`/api/minha-posicao?usuarioId=${usuarioId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      .then(res => {
        if (!res.ok) throw new Error("Erro ao buscar ranking");
        return res.json();
      })
      .then(data => {
        preencherMinhaPosicao(data);
      })
      .catch(err => {
        console.error("Erro:", err);
      });
    } else {
      console.error("Usuário não está logado ou meta tag não encontrada.");
    }
  });
});
