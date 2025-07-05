// Função para obter ID do usuário logado, do meta tag por exemplo
 function getUsuarioLogadoId() {
   const meta = document.querySelector('meta[name="user-id"]');
   return meta ? meta.getAttribute('content') : null;
 }
 const usuarioId = getUsuarioLogadoId();

 function abrirModal() {
   const modal = document.getElementById("bonusModal");
   modal.classList.add('show');
   modal.setAttribute('aria-hidden', 'false');
 }

 function fecharModal() {
   const modal = document.getElementById("bonusModal");
   modal.classList.remove('show');
   modal.setAttribute('aria-hidden', 'true');
 }

 function verificarBonus(usuarioId) {
   fetch(`/api/bonus/verificar/${usuarioId}`)
     .then(res => res.json())
     .then(data => {
       if (data.disponivel) {
         document.getElementById("bonusMensagem").innerText = data.mensagem;
         abrirModal();
       }
     })
     .catch(err => console.error('Erro na verificação:', err));
 }

 // Evento de clique no botão para coletar o bônus
 document.getElementById("btnColetarBonus").addEventListener("click", function() {
   const btn = this;
   btn.disabled = true;

   fetch(`/api/bonus/coletar/${usuarioId}`, {
     method: 'POST'
   })
   .then(res => res.text())
   .then(data => {
     // Atualiza mensagem dentro do modal
     document.getElementById("bonusMensagem").innerText = data;

     // Atualiza o botão para indicar bônus coletado e mantém desabilitado
     btn.innerText = "Bônus Coletado";
     btn.disabled = true;

     // Fecha modal automaticamente depois de 3 segundos
     setTimeout(() => fecharModal(), 3000);
   })
   .catch(err => {
     document.getElementById("bonusMensagem").innerText = "❌ Erro ao coletar bônus, tente novamente.";
     btn.disabled = false;
     btn.innerText = "Coletar Bônus";
     console.error(err);
   });
 });

 // Fechar modal clicando no "x"
 document.querySelector(".close").onclick = function() {
   fecharModal();
 };

 // Fechar modal clicando fora do conteúdo
 window.onclick = function(event) {
   const modal = document.getElementById("bonusModal");
   if (event.target === modal) {
     fecharModal();
   }
 };

 // Verifica bônus ao carregar a página
 window.addEventListener('DOMContentLoaded', () => {
   verificarBonus(usuarioId);
 });