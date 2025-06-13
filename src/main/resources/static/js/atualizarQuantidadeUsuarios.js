/**
 * 
 */
async function atualizarQuantidadeUsuarios() {
  try {
    const response = await fetch('/usuarios/quantidade');
    if (!response.ok) {
      throw new Error(`Erro na requisição: ${response.status}`);
    }
    const data = await response.json();
    const total = data.total;
    document.getElementById('quantidadeUsuarios').textContent = total;
  } catch (error) {
    console.error('Erro ao buscar quantidade de usuários:', error);
    document.getElementById('quantidadeUsuarios').textContent = 'Erro ao carregar';
  }
}

// Executa quando a página carregar
document.addEventListener('DOMContentLoaded', atualizarQuantidadeUsuarios);
