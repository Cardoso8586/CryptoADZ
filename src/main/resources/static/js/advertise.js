const btnAnunciar     = document.getElementById('btnAnunciar');
const sectionAnunciar = document.getElementById('Anunciar');
const btnMostrarForm  = document.getElementById('btnMostrarForm');
const formAnuncio     = document.getElementById('formAnuncio');
const feedback        = document.getElementById('feedback');
const btnCancelar     = document.getElementById('btnCancelar');
const tempoInput      = document.getElementById('tempoVisualizacao');
const maxVisInput     = document.getElementById('maxVisualizacoes');
const infoPagamento   = document.getElementById('infoPagamento');
const feedbackCadastro  = document.getElementById('feedbackCadastro');

// Mostrar/ocultar a seção de anúncio
btnAnunciar.addEventListener('click', e => {
  e.preventDefault();
  const vis = sectionAnunciar.style.display === 'none' ? 'block' : 'none';
  sectionAnunciar.style.display = vis;
  esconderFormulario();
  limparFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
});

// Mostrar formulário de anúncio
btnMostrarForm.addEventListener('click', () => {
  limparFormulario();
  mostrarFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
});

// Cancelar formulário
btnCancelar.addEventListener('click', () => {
  esconderFormulario();
  limparFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
});

function mostrarFormulario() {
  formAnuncio.style.display = 'block';
}

function esconderFormulario() {
  formAnuncio.style.display = 'none';
}

function limparFormulario() {
  formAnuncio.reset();
}

tempoInput.addEventListener('input', calcularCustoEstimado);
maxVisInput.addEventListener('input', calcularCustoEstimado);

let custoEstimado = 0;

function calcularCustoEstimado() {
  const tempo = parseInt(tempoInput.value, 10);
  const maxVis = parseInt(maxVisInput.value, 10);

  if (isNaN(tempo) || isNaN(maxVis) || tempo <= 0 || maxVis <= 0) {
    infoPagamento.textContent = '';
    custoEstimado = 0;
    return;
  }

  let tokensPorView;

  if (tempo >= 45) {
    tokensPorView = 4.50;
  } else if (tempo === 30) {
    tokensPorView = 3.55;
  } else if (tempo === 20) {
    tokensPorView = 2.25;
  } else if (tempo === 10) {
    tokensPorView = 1.10;
  } else {
    tokensPorView = 1.30;
  }

  custoEstimado = tokensPorView * maxVis;

  infoPagamento.textContent = `💰 Custo Estimado:  ${custoEstimado.toFixed(2)} tokens`;
}

formAnuncio.addEventListener('submit', async e => {
  e.preventDefault();

  // ✅ VERIFICA SE O CHECKBOX FOI MARCADO
  const checkboxTermos = document.getElementById('aceitoTermosAnuncio');
  if (!checkboxTermos || !checkboxTermos.checked) {
	
	Swal.fire({
	  icon: 'warning',
	  title: 'Atenção',
	  text: 'Você precisa aceitar os termos de privacidade para continuar.',
	  confirmButtonText: 'Ok',
	  background: '#fff',
	  color: '#000'
	});

    feedback.textContent = '⛔ Você deve aceitar os Termos de Uso para continuar.';
    feedback.style.color = 'red';
    checkboxTermos?.focus();
    return;
  }


  const tempo = parseInt(tempoInput.value, 10);
  const maxVis = parseInt(maxVisInput.value, 10);

  if (isNaN(tempo) || isNaN(maxVis) || tempo <= 0 || maxVis <= 0) {
    feedback.textContent = '⛔ Tempo e visualizações devem ser válidos.';
    feedback.style.color = 'red';
    return;
  }

  const limitesMinimos = {
    10: 300,
    20: 500,
    30: 700,
    45: 1000,
  };

  const minimoExigido = limitesMinimos[tempo];

  if (minimoExigido && maxVis < minimoExigido) {
	Swal.fire({
	  icon: 'warning',
	  title: 'Aviso!',
	  text: `⚠️ O número mínimo de visualizações para ${tempo} segundos é ${minimoExigido}.`,
	  confirmButtonText: 'Entendi',
	  background: '#fff',
	  color: '#000'
	});

    feedback.textContent = `⚠️ O número mínimo de visualizações para ${tempo} segundos é ${minimoExigido}.`;
    feedback.style.color = 'red';
    maxVisInput.focus();
    return;
  }

  const dto = {
    titulo: document.getElementById('titulo').value,
    descricao: document.getElementById('descricao').value,
    url: document.getElementById('url').value,
    tempoVisualizacao: tempo,
    maxVisualizacoes: maxVis,
    usuarioId: 1,
    tokensGastos: Number(custoEstimado)
  };

  try {
    const response = await fetch('/api/anuncio/cadastrar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dto)
    });

    if (!response.ok) {
      const err = await response.text();

      if (err.toLowerCase().includes('saldo insuficiente')) {
        feedback.textContent = '⛔ Saldo insuficiente para cadastrar o anúncio. Por favor, recarregue sua conta.';
		Swal.fire({
		  icon: 'warning',
		  title: 'Aviso!',
		  text: `⛔ Saldo insuficiente para cadastrar o anúncio. Por favor, recarregue sua conta.`,
		  confirmButtonText: 'Entendi',
		  background: '#fff',
		  color: '#000'
		});

      } else {
        feedback.textContent = 'Erro: ' + err;
      }

      feedback.style.color = 'red';
      return;
    }

    const data = await response.json();
    console.log('Server response:', data);

    feedbackCadastro.textContent = '✅ Anúncio cadastrado com sucesso!';
	Swal.fire({
	  icon: 'success',
	  title: 'Sucesso',
	  text: '✅ Anúncio cadastrado com sucesso!',
	  timer: 2500,
	  timerProgressBar: true,
	  showConfirmButton: false,
	  background: '#fff',
	  color: '#000'
	});

    feedbackCadastro.style.color = 'green';
    esconderFormulario();
    limparFormulario();
    registrarMissaoCadastrar();
    infoPagamento.textContent = '';
    atualizarSaldo();

	
    setInterval(() => {
      carregarQuantidadeDeAnuncios();
    }, 3000);

  } catch (error) {
    feedback.textContent = 'Falha ao salvar o anúncio: ' + error.message;
    feedback.style.color = 'red';
  }
});

function registrarMissaoCadastrar() {
  const usuarioId = document.querySelector('meta[name="user-id"]')?.content;
  if (!usuarioId) {
    console.error('Usuário não encontrado para missão de cadastro.');
    return;
  }

  fetch(`/api/missoes/incrementar-cadastro/${usuarioId}`, { method: 'POST' })
    .then(res => {
      if (!res.ok) throw new Error('Falha ao registrar missão de cadastro');
      return res.text();
    })
    .then(msg => {
      console.log('Missão de cadastro registrada:', msg);
      return fetch(`/api/missoes/incrementar-cadastro/${usuarioId}`, { method: 'POST' });
    })
    .then(res => {
      if (!res.ok) throw new Error('Falha ao incrementar cadastro');
      console.log('Incremento de cadastro realizado com sucesso.');
      carregarStatusMissoes();
    })
    .catch(err => {
      console.error('Erro ao registrar/incrementar missão de cadastro:', err);
    });
}

const descricaoInput = document.getElementById('descricao');
const contadorDescricao = document.getElementById('contadorDescricao');
const spanRestantes = document.getElementById('restantes');
const limiteDescricao = 200;

descricaoInput.addEventListener('input', () => {
  const restante = limiteDescricao - descricaoInput.value.length;

  if (restante < 0) {
    descricaoInput.value = descricaoInput.value.substring(0, limiteDescricao);
    spanRestantes.textContent = '0';
  } else {
    spanRestantes.textContent = restante;
  }

  if (restante <= 20 && restante >= 0) {
    spanRestantes.style.color = 'orange';
  } else if (restante === 0) {
    spanRestantes.style.color = 'red';
  } else {
    spanRestantes.style.color = 'gray';
  }
});

const tituloInput = document.getElementById('titulo');
const restantesTitulo = document.getElementById('restantesTitulo');

tituloInput.addEventListener('input', () => {
  const max = 30;
  const atual = tituloInput.value.length;
  const restantes = max - atual;

  restantesTitulo.textContent = restantes;

  if (restantes <= 0) {
    restantesTitulo.style.color = 'red';
  } else {
    restantesTitulo.style.color = 'gray';
  }
});
