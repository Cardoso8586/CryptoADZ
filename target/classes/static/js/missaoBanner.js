async function verificarStatusVisualizacoes() {
  const usuarioId = getUsuarioLogadoId();

  try {
    const response = await fetch(`/api/visualizacoes/banner/status?usuarioId=${usuarioId}`);
    if (!response.ok) throw new Error("Erro ao buscar status");

    const data = await response.json();
    const { bannersVistos, limitePorDia, tokensAtualizados, jaColetouHoje, limitePorBanner, recompensaMissao } = data;

    const statusSpan = document.getElementById('statusBanners');
    const btnClaim = document.getElementById('btnColetarBanners');
    const qtdeBannersDiferentesSpan = document.getElementById('qtdeBannersDiferentes');
    const recompensaMissaoHoje = document.getElementById('recompensaMissaoHoje');

    statusSpan.textContent = `${bannersVistos}/${limitePorDia}`;

    if (qtdeBannersDiferentesSpan && limitePorBanner != null) {
      qtdeBannersDiferentesSpan.textContent = limitePorBanner;
    }

	if (recompensaMissaoHoje && recompensaMissao != null) {
	  recompensaMissaoHoje.innerHTML = `${recompensaMissao} <img src="/icones/adz-token.png" alt="ADZ Token">`;
	}


    if (jaColetouHoje) {
      btnClaim.style.display = 'none';

      let textoColetado = document.getElementById('textoColetadoBanners');
      if (!textoColetado) {
        textoColetado = document.createElement('span');
        textoColetado.id = 'textoColetadoBanners';
        textoColetado.style.color = 'green';
        textoColetado.style.marginLeft = '10px';
        btnClaim.parentNode.appendChild(textoColetado);
		
      }
      textoColetado.textContent = 'Você já coletou hoje.';
	
	  Swal.fire({
	    icon: 'info',
	    title: 'Aviso',
	    text: 'Você já coletou hoje.',
	    timer: 2500,
	    timerProgressBar: true,
	    showConfirmButton: false,
	    background: '#fff',
	    color: '#000'
	  });

      textoColetado.style.display = 'inline';

      statusSpan.textContent = `✅ Completo! (${bannersVistos}/${limitePorDia})`;
    } else {
      btnClaim.style.display = (bannersVistos >= limitePorDia) ? 'inline-block' : 'none';

      const textoColetado = document.getElementById('textoColetadoBanners');
      if (textoColetado) textoColetado.style.display = 'none';
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

  
	Swal.fire({
	  icon: 'success',
	  title: '🎉 Recompensa coletada!',
	  text: 'Recompensa coletada com sucesso!',
	  timer: 2500,
	  timerProgressBar: true,
	  showConfirmButton: false,
	  background: '#fff',
	  color: '#000'
	});

    verificarStatusVisualizacoes();
  } catch (error) {
   
	Swal.fire({
			    icon: 'info',
			    title: 'Aviso',
			    text: '⚠️ Erro ao coletar recompensa:' + error.message,
			    timer: 2500,
			    timerProgressBar: true,
			    showConfirmButton: false,
			    background: '#fff',
			    color: '#000'
			  });
  }
}

// Garante que o DOM esteja pronto antes de iniciar
document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('btnColetarBanners').addEventListener('click', coletarRecompensaBanners);
  

  verificarStatusVisualizacoes();
  setInterval(verificarStatusVisualizacoes, 30000);
});
