async function carregarAnuncios() {
    const response = await fetch('/api/anuncio/listar-anuncios', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token') // Remova se não estiver usando JWT
        }
    });

    const anuncios = await response.json();
    const container = document.getElementById('lista-anuncios');
    container.innerHTML = '';

    anuncios.forEach(anuncio => {
        const div = document.createElement('div');
        div.innerHTML = `
            <h3>${anuncio.titulo}</h3>
            <p>${anuncio.descricao}</p>
            <p><a href="${anuncio.url}" target="_blank">${anuncio.url}</a></p>
            <button onclick="abrirFormularioEdicao(${anuncio.id}, \`${anuncio.titulo}\`, \`${anuncio.descricao}\`, \`${anuncio.url}\`)">Editar</button>
            <hr/>
        `;
        container.appendChild(div);
    });
}

function abrirFormularioEdicao(id, titulo, descricao, url) {
    const formHtml = `
        <h3>Editar Anúncio</h3>
        <form onsubmit="salvarEdicao(event, ${id})">
            <input type="text" id="edit-titulo" value="${titulo}" required><br/>
            <input type="text" id="edit-descricao" value="${descricao}" required><br/>
            <input type="url" id="edit-url" value="${url}" required><br/>
            <button type="submit">Salvar</button>
        </form>
    `;
    document.getElementById('lista-anuncios').innerHTML = formHtml;
}

async function salvarEdicao(event, id) {
    event.preventDefault();
    const dto = {
        titulo: document.getElementById('edit-titulo').value,
        descricao: document.getElementById('edit-descricao').value,
        url: document.getElementById('edit-url').value
    };

    const response = await fetch(`/api/anuncio/editar-anuncios/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token') // Remova se não usar JWT
        },
        body: JSON.stringify(dto)
    });

    if (response.ok) {
      
		Swal.fire({
		    icon: 'success',
		    title: '✅ Anúncio atualizado com sucesso!',
		    text: 'Seu anúncio estará disponível em breve.',
		    timer: 5000,
		    timerProgressBar: true,
		    showConfirmButton: false
		});


        carregarAnuncios();
    } else {
      
		Swal.fire({
		  icon: 'warning',
		  title: '⚠️ Erro',
		  text: 'Erro ao editar o anúncio:',
		  timer: 5000,
		  timerProgressBar: true,
		  showConfirmButton: false
		});
    }
}

carregarAnuncios();