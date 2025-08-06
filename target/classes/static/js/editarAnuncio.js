/**let anunciosGlobal = [];  // guardar os anúncios para reutilizar no editar
let editarId = null;

async function carregarAnuncios() {
    try {
        const response = await fetch('/api/anuncio/meus', {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao buscar anúncios');
        }

        const anuncios = await response.json();
        anunciosGlobal = anuncios; // salvar globalmente

        const container = document.getElementById('anuncios-container');

        if (anuncios.length === 0) {
            container.innerHTML = '<p>Você ainda não cadastrou nenhum anúncio.</p>';
            return;
        }

        let html = '<ul>';
        anuncios.forEach(anuncio => {
            html += `
                <li>
                    <strong>${anuncio.titulo}</strong><br>
                    ${anuncio.descricao}<br>
                    <a href="${anuncio.url}" target="_blank">Visitar link</a><br><br>
                    <button onclick="abrirFormularioEditar(${anuncio.id})">Editar</button>
                </li>
                <hr>
            `;
        });
        html += '</ul>';

        container.innerHTML = html;

    } catch (error) {
        console.error('Erro:', error);
        document.getElementById('anuncios-container').innerHTML = '<p>Erro ao carregar anúncios.</p>';
    }
}

function abrirFormularioEditar(id) {
    editarId = id;

    const anuncio = anunciosGlobal.find(a => a.id === id);
    if (!anuncio) {
        alert('Anúncio não encontrado');
        return;
    }

    document.getElementById('edit-titulo').value = anuncio.titulo;
    document.getElementById('edit-descricao').value = anuncio.descricao;
    document.getElementById('edit-url').value = anuncio.url;

    document.getElementById('form-editar').style.display = 'block';
    window.scrollTo(0, document.getElementById('form-editar').offsetTop);
}

function fecharFormulario() {
    document.getElementById('form-editar').style.display = 'none';
    editarId = null;
}

async function salvarEdicao() {
    const dadosEdicao = {
        titulo: document.getElementById('edit-titulo').value,
        descricao: document.getElementById('edit-descricao').value,
        url: document.getElementById('edit-url').value,
    };

    if (!editarId) {
        alert('Nenhum anúncio selecionado para edição.');
        return;
    }

    try {
        const response = await fetch(`/api/anuncio/editar/${editarId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosEdicao)
        });

        if (!response.ok) {
            throw new Error('Erro ao editar anúncio');
        }
		Swal.fire({
		   icon: 'success',
		   title: 'Anúncios',
		   text: '✅ Anúncio editado com sucesso!',
		   timer: 5000,
		   timerProgressBar: true,
		   showConfirmButton: false
		 });
        fecharFormulario();
        carregarAnuncios();

    } catch (error) {
        alert('Erro na edição: ' + error.message);
    }
}

carregarAnuncios();
*/
  //------------------------------- FUNCIONANADO -------------------------------------------------------