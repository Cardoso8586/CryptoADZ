function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? meta.getAttribute('content') : null;
}

async function verificarStatusVisualizacoes() {
  const usuarioId = getUsuarioLogadoId();

  try {
    const response = await fetch(`/api/visualizacoes/banner/status?usuarioId=${usuarioId}`);
    if (!response.ok) throw new Error("Erro ao buscar status");

    const data = await response.json();
    const { bannersVistos, limitePorDia, tokensAtualizados } = data;

    const statusSpan = document.getElementById('statusBanners');
    const btnClaim = document.getElementById('btnColetarBanners');

    statusSpan.textContent = `${bannersVistos}/${limitePorDia}`;

    if (bannersVistos >= limitePorDia) {
      btnClaim.style.display = 'inline-block';
      statusSpan.textContent = `✅ Completo! (${bannersVistos}/${limitePorDia})`;
    } else {
      btnClaim.style.display = 'none';
    }
  } catch (error) {
    console.error('Erro ao verificar status:', error.message);
  }
}

async function coletarRecompensaBanners() {
  const usuarioId = getUsuarioLogadoId();

  try {
    const response = await fetch(`/api/visualizacoes/banner/coletar?usuarioId=${usuarioId}`, {
      method: 'POST'
    });

    if (!response.ok) {
      const err = await response.text();
      throw new Error(err);
    }

    alert('🎉 Recompensa coletada com sucesso!');
    verificarStatusVisualizacoes();
  } catch (error) {
    alert('Erro ao coletar recompensa: ' + error.message);
  }
}

document.getElementById('btnColetarBanners').addEventListener('click', coletarRecompensaBanners);

// Atualiza status ao carregar e a cada minuto (60.000 ms)
verificarStatusVisualizacoes();
setInterval(verificarStatusVisualizacoes, 5000);
