async function carregarAvisos() {
  try {
    const response = await fetch('/api/avisos');  // Ajuste a URL conforme sua API
    if (!response.ok) throw new Error('Erro ao buscar avisos');

    const avisos = await response.json();

    const container = document.getElementById('containerAvisos');
    container.innerHTML = ''; // limpa antes de mostrar

    if (avisos.length === 0) {
      container.innerHTML = '<p>Sem avisos no momento.</p>';
    } else {
      avisos.forEach(aviso => {
        const card = document.createElement('div');
        card.classList.add('aviso-card');

        const titulo = document.createElement('h3');
        titulo.textContent = aviso.titulo;

        const mensagem = document.createElement('p');
        mensagem.textContent = aviso.mensagem;

        card.appendChild(titulo);
        card.appendChild(mensagem);

        container.appendChild(card);
      });
    }


  } catch (error) {
    console.error('Erro ao carregar avisos:', error);
  }
}



window.addEventListener('load', carregarAvisos);
