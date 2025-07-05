document.addEventListener('DOMContentLoaded', () => {
  let avisos = [];
  let avisoAtual = 0;
  const textoAvisos = document.getElementById('textoAvisos');
  const cardAvisos = document.getElementById('cardAvisos');

  async function carregarAvisos() {
    try {
      const response = await fetch('/api/avisos');
      if (!response.ok) throw new Error('Erro ao buscar avisos');

      avisos = await response.json();

      if (avisos.length === 0) {
        textoAvisos.textContent = "Nenhum aviso no momento.";
      } else {
        exibirAvisoAtual();
        setInterval(alternarAviso, 10000);
      }
    } catch (error) {
      textoAvisos.textContent = 'Erro ao carregar avisos.';
      console.error('Erro:', error);
    }
  }

  function exibirAvisoAtual() {
    const aviso = avisos[avisoAtual];
    textoAvisos.textContent = aviso.titulo;
  }

  function alternarAviso() {
    if (avisos.length === 0) return;

    avisoAtual = (avisoAtual + 1) % avisos.length;
    exibirAvisoAtual();
  }

  cardAvisos.addEventListener('click', () => {
    const secaoAvisos = document.getElementById('Avisos');
    if (secaoAvisos) {
      secaoAvisos.style.display = 'block';
      secaoAvisos.scrollIntoView({ behavior: 'smooth' });
    }
  });

  carregarAvisos();
});
