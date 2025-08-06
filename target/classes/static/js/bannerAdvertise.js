document.addEventListener('DOMContentLoaded', () => {
  const btnMostrarFormBanner = document.getElementById('btnMostrarFormBanner');
  const uploadBannerSection = document.getElementById('upload-banner');
  const outroPainel = document.getElementById('outro-painel');
  const saldoTokensElement = document.getElementById('saldo-tokens');
  const formUpload = document.getElementById('form-upload');
  const fileInput = document.getElementById('file');
  const previewImg = document.getElementById('preview');
  const formCadastro = document.getElementById('form-cadastro');
  const tituloInput = document.getElementById('titulo-banner');
  const linkInput = document.getElementById('link-banner');
  const dataExpInput = document.getElementById('data-expiracao');
  const statusSelect = document.getElementById('status-banner');
  const tempoSelect = document.getElementById('tempo-banner');
  const custoPreview = document.getElementById('custo-preview');
  const btnConfirmar = document.getElementById('btn-confirmar-cadastro');
  const feedback        = document.getElementById('feedbackBanner');
  
  let saldoAtual = 0;
  let imagemUrl = '';

  const custoPorTempo = {
    '10': 1200,
    '20': 1300,
    '30': 1400,
    '45': 1500,
    '60': 1600
  };

  // Inicialmente esconde o formulário de cadastro e preview
  formCadastro.style.display = 'none';
  previewImg.style.display = 'none';
  uploadBannerSection.style.display = 'none'; // ocultar o formulário inicialmente

  // Desabilita botão inicialmente
  btnConfirmar.disabled = true;

  // Função para buscar saldo do usuário logado
  async function buscarSaldo() {
    try {
      const res = await fetch('/api/saldo');
      if (!res.ok) throw new Error('Erro ao buscar saldo');
      const data = await res.json();
      saldoAtual = data.saldo;
      if (saldoTokensElement) {
        saldoTokensElement.textContent = `Saldo atual: ${saldoAtual} tokens`;
      }
    } catch (err) {
      console.error(err);
      if (saldoTokensElement) {
        saldoTokensElement.textContent = 'Não foi possível obter saldo';
      }
    }
  }
  
  // Calcula dias restantes da data expiração
  function diasRestantes() {
    const hoje = new Date();
    const exp = new Date(dataExpInput.value);
    const diff = (exp - hoje) / (1000 * 60 * 60 * 24);
    return diff > 0 ? Math.ceil(diff) : 0;
  }

  // Função que valida se todos os campos necessários estão preenchidos e corretos
  function validaCampos() {
    const titulo = tituloInput.value.trim();
    const urlDestino = linkInput.value.trim();
    const dataExp = dataExpInput.value;
    const tempo = tempoSelect.value;
    const dias = diasRestantes();

    if (!titulo) return false;
    if (!urlDestino) return false;
    if (!dataExp) return false;
    if (!tempo || !(tempo in custoPorTempo)) return false;
    if (!imagemUrl) return false;
    if (dias <= 0) return false;

    return true;
  }

  // Atualiza custo total estimado e botão confirmar
  function atualizarCustoEBotao() {
    const t = tempoSelect.value;
    const custoDia = custoPorTempo[t];
    const dias = diasRestantes();

    if (custoDia && dias > 0) {
      const custoTotal = custoDia * dias;
      custoPreview.textContent = `Custo estimado: ${custoTotal} tokens (${dias} dias)`;

      if (saldoAtual < custoTotal || !validaCampos()) {
        custoPreview.textContent += ' — Saldo insuficiente ou campos inválidos!';
        btnConfirmar.disabled = true;
      } else {
        btnConfirmar.disabled = false;
      }
    } else {
      custoPreview.textContent = '';
      btnConfirmar.disabled = true;
    }
  }

  // Mostrar/esconder formulário upload e atualizar saldo
  btnMostrarFormBanner.addEventListener('click', async () => {
    const isVisible = window.getComputedStyle(uploadBannerSection).display !== 'none';
    uploadBannerSection.style.display = isVisible ? 'none' : 'block';
    if (outroPainel) {
      outroPainel.style.display = isVisible ? 'block' : 'none';
    }
    if (!isVisible) {
      await buscarSaldo();
      atualizarCustoEBotao();
    }
  });

  // Atualiza custo e botão ao mudar valores
  tempoSelect.addEventListener('change', atualizarCustoEBotao);
  dataExpInput.addEventListener('change', atualizarCustoEBotao);

  // Atualiza custo e botão ao mudar inputs de texto (titulo, link)
  tituloInput.addEventListener('input', atualizarCustoEBotao);
  linkInput.addEventListener('input', atualizarCustoEBotao);

  // Upload da imagem
  formUpload.addEventListener('submit', async e => {
    e.preventDefault();
    if (!fileInput.files[0]) {
      alert('Selecione uma imagem!');
      return;
    }
    const fd = new FormData();
    fd.append('file', fileInput.files[0]);

    try {
      const res = await fetch('/api/banners/upload', { method: 'POST', body: fd });
      if (!res.ok) throw new Error('Upload falhou');
      imagemUrl = await res.text();
      previewImg.src = imagemUrl;
      previewImg.style.display = 'block';
      formCadastro.style.display = 'block';
      custoPreview.textContent = '';
      atualizarCustoEBotao();
    } catch (err) {
      alert(err.message);
    }
  });

  // Confirmar cadastro do banner
  btnConfirmar.addEventListener('click', async e => {
    e.preventDefault();

    if (btnConfirmar.disabled) return;
    btnConfirmar.disabled = true;

    // Verifica se o checkbox dos termos está marcado
    if (!checkboxTermos.checked) {
		Swal.fire({
		  icon: 'warning',
		  title: 'Atenção',
		  text: 'Você precisa aceitar os termos de privacidade para continuar.',
		  confirmButtonText: 'OK',
		  background: '#fff',
		  color: '#000'
		});

	  feedback.textContent = '⛔ Você deve aceitar os Termos de Uso para continuar.';
	     feedback.style.color = 'red';
	     checkboxTermos?.focus();
      btnConfirmar.disabled = false;
      return;
    }

    const titulo = tituloInput.value.trim();
    const urlDestino = linkInput.value.trim();
    const dataExp = `${dataExpInput.value}T00:00:00`;
    const ativo = statusSelect.value === 'ativo';
    const tempo = parseInt(tempoSelect.value, 10);
    const dias = diasRestantes();

    if (!validaCampos()) {
		Swal.fire({
		  icon: 'warning',
		  title: 'Atenção',
		  text: 'Preencha todos os campos corretamente e faça upload da imagem.',
		  confirmButtonText: 'OK',
		  background: '#fff',
		  color: '#000'
		});

      btnConfirmar.disabled = false;
      return;
    }

    const custoDia = custoPorTempo[tempo];
    const custoTotal = custoDia * dias;

    if (saldoAtual < custoTotal) {
		Swal.fire({
		  icon: 'error',
		  title: 'Saldo insuficiente',
		  text: 'Saldo insuficiente para o tempo e duração selecionados.',
		  confirmButtonText: 'OK',
		  background: '#fff',
		  color: '#000'
		});

      btnConfirmar.disabled = false;
      return;
    }

    const bannerDTO = {
      titulo,
      urlDestino,
      imagemUrl,
      ativo,
      dataExpiracao: dataExp,
      tempoExibicao: tempo,
      tokensGastos: custoTotal
    };

    try {
      const res = await fetch('/api/banners/banner', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(bannerDTO)
      });

      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || 'Falha ao criar banner');
      }

      const data = await res.json();
	  Swal.fire({
	    icon: 'success',
	    title: 'Sucesso!',
	    text:  data.mensagem || 'Banner criado com sucesso!',
	    timer: 2500,
	    timerProgressBar: true,
	    showConfirmButton: false,
	    background: '#fff',
	    color: '#000'
	  });


      // Limpa tudo após cadastro
      formUpload.reset();
      formCadastro.reset();
      previewImg.style.display = 'none';
      formCadastro.style.display = 'none';
      custoPreview.textContent = '';
      imagemUrl = '';

      // Atualiza saldo para refletir o gasto
      await buscarSaldo();
      atualizarCustoEBotao();
      location.reload();

    } catch (err) {
      alert(err.message);
    } finally {
      btnConfirmar.disabled = false;
    }
  });

  setInterval(() => {
  	 // Inicialização
  buscarSaldo();
  atualizarCustoEBotao(); 
	
	
  	}, 3000); // Atualiza a cada 3 segundos
 
});

const tituloBannerInput = document.getElementById('titulo-banner');
const restantesTituloBanner = document.getElementById('restantes-titulo-banner');
const formCadastro = document.getElementById('form-cadastro');
const checkboxTermos = document.getElementById('aceitoTermos');



tituloBannerInput.addEventListener('input', () => {
  const max = 30;
  const atual = tituloBannerInput.value.length;
  const restantes = max - atual;

  restantesTituloBanner.textContent = restantes;

  if (restantes <= 0) {
    restantesTituloBanner.style.color = 'red';
  } else {
    restantesTituloBanner.style.color = 'gray';
  }
});