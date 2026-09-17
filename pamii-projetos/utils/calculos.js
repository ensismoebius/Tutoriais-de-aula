// utils/calculos.js
// Origem: Tutorial_PAMII.md — 3. Concorrência e Threads (Passos 1 a 3)
export function distanciaEntre(p1, p2) {
  const R = 6371000; // raio médio da Terra, em metros
  const rad = Math.PI / 180;
  const dLat = (p2.latitude - p1.latitude) * rad;
  const dLon = (p2.longitude - p1.longitude) * rad;
  const lat1 = p1.latitude * rad;
  const lat2 = p2.latitude * rad;

  const a = Math.sin(dLat / 2) ** 2 + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c; // em metros
}

export function distanciaTotal(pontos) {
  let soma = 0;
  for (let i = 1; i < pontos.length; i++) {
    soma += distanciaEntre(pontos[i - 1], pontos[i]);
  }
  return soma;
}

export function distanciaTotalEmLotes(pontos, aoTerminar, tamanhoDoLote = 50_000) {
  let soma = 0;
  let i = 1;

  function proximoLote() {
    const fim = Math.min(i + tamanhoDoLote, pontos.length);
    for (; i < fim; i++) soma += distanciaEntre(pontos[i - 1], pontos[i]);

    if (i < pontos.length) {
      setTimeout(proximoLote, 0);
    } else {
      aoTerminar(soma);
    }
  }

  proximoLote();
}
