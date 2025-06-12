// slogans.js
const slogans = [
  "CryptoADZ — Innovation starts here!",
  "Unlock the power of cryptocurrencies!",
  "Your crypto journey, our commitment.",
  "Transform your digital investment!",
  "Simplifying the crypto world for you.",
  "Security, speed, and innovation.",
  "Connecting you to the future of finance.",
  "The crypto market in the palm of your hand.",
  "Explore, invest, grow with CryptoADZ!",
  "Crypto for everyone, easy and fast."
];

let index = 0;
const sloganElement = document.querySelector('.slogan-crypto-adz');

setInterval(() => {
  index = (index + 1) % slogans.length;
  sloganElement.textContent = slogans[index];
}, 10000);
