function atualizarUsername() {
  fetch('/usuario/username-atual')
    .then(res => res.json())
    .then(data => {
      if (data.username) {
        document.getElementById('nomeUsuario').innerText = data.username;
      }
    })
    .catch(console.error);
}

// Chama a função imediatamente ao carregar a página
atualizarUsername();

// E repete a cada 10 segundos (10.000 milissegundos)
setInterval(atualizarUsername, 5000);
