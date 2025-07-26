// Alterna a exibição do formulário com base no ID recebido
function toggleFormulario(id) {
  const form = document.getElementById(id);
  form.style.display = (form.style.display === 'none') ? 'block' : 'none';
}

// Atualiza o nome do usuário com integração backend e SweetAlert2
function salvarNovoNome(event) {
  event.preventDefault();

  const novoNome = document.getElementById('inputNovoNome').value.trim();
  const usuarioSpan = document.getElementById('nomeUsuario');
  const usuarioId = getUsuarioLogadoId(); // Busca ID do usuário via <meta>

  if (!novoNome || !usuarioId) {
    Swal.fire({
      icon: 'error',
      title: 'Oops...',
      text: 'Nome inválido ou ID não encontrado.',
      timer: 2500,
      timerProgressBar: true,
      showConfirmButton: false
    });
    return;
  }

  // Envia a atualização para o backend
  fetch(`/atualizar-nome/${usuarioId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ novoNome })
  })
  .then(response => {
    if (!response.ok) throw new Error("Erro ao atualizar nome.");
    return response.text();
  })
  .then(msg => {
    usuarioSpan.textContent = novoNome;

    Swal.fire({
      icon: 'success',
      title: 'Nome atualizado!',
      text: msg,
      timer: 2000,
      timerProgressBar: true,
      showConfirmButton: false
    }).then(() => {
      return Swal.fire({
        icon: 'warning',
        title: 'Atenção!',
        text: 'Por segurança, você será deslogado e deverá fazer login novamente.',
        confirmButtonText: 'OK'
      });
    }).then(() => {
      document.getElementById('formEditarPerfil').style.display = 'none';
      window.location.href = '/logout'; // Logout automático
    });

  })
  .catch(error => {
    Swal.fire({
      icon: 'error',
      title: 'Erro',
      text: error.message,
      confirmButtonText: 'Tentar novamente'
    });
  });
}

// Atualiza a senha do usuário com feedback visual e segurança
function salvarNovaSenha(event) {
  event.preventDefault();

  const novaSenha = document.getElementById('inputNovaSenha').value.trim();
  const usuarioId = getUsuarioLogadoId(); // Mesmo método de busca do ID

  if (!novaSenha || !usuarioId) {
    Swal.fire({
      icon: 'warning',
      title: '⚠️ Campo vazio',
      text: 'Por favor, insira a nova senha.',
      timer: 3000,
      timerProgressBar: true,
      showConfirmButton: false
    });
    return;
  }

  // Envia a nova senha para o backend
  fetch(`/atualizar-senha/${usuarioId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ novaSenha })
  })
  .then(response => {
    if (!response.ok) throw new Error("Erro ao atualizar a senha.");
    return response.text();
  })
  .then(msg => {
    Swal.fire({
      icon: 'success',
      title: 'Senha Atualizada!',
      text: '✅ Sua senha foi alterada com sucesso.',
      timer: 3000,
      timerProgressBar: true,
      showConfirmButton: false
    });

    document.getElementById('formAlterarSenha').style.display = 'none';

    // Força logout após troca de senha
    setTimeout(() => {
      Swal.fire({
		icon: 'warning',
		      title: 'Atenção!',
		      text: 'Por segurança, você será deslogado e deverá fazer login novamente.',
		      confirmButtonText: 'OK'
      }).then(() => {
        window.location.href = '/logout';
      });
    }, 1000);
  })
  .catch(error => {
    Swal.fire({
      icon: 'error',
      title: '❌ Erro',
      text: error.message || 'Erro desconhecido ao atualizar senha.',
    });
  });
}

// Busca o ID do usuário logado no <meta name="user-id" content="1">
function getUsuarioLogadoId() {
  const meta = document.querySelector('meta[name="user-id"]');
  return meta ? parseInt(meta.getAttribute('content')) : null;
}
