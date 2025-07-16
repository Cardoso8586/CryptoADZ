// slogans.js
const slogans = [
  "CryptoADZ — A inovação começa aqui!",
  "Desbloqueie o poder das criptomoedas!",
  "Sua jornada cripto, nosso compromisso.",
  "Transforme seu investimento digital!",
  "Simplificando o mundo cripto para você.",
  "Segurança, rapidez e inovação.",
  "Conectando você ao futuro das finanças.",
  "O mercado cripto na palma da sua mão.",
  "Explore, invista, cresça com CryptoADZ!",
  "Cripto para todos, fácil e rápido."
];

let index = 0;
const sloganElement = document.querySelector('.slogan-crypto-adz');

setInterval(() => {
  index = (index + 1) % slogans.length;
  sloganElement.textContent = slogans[index];
}, 10000);
