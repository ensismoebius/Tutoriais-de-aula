import axios from 'axios'

let cotacaoCache = { valor: null, expiraEm: 0 }

async function buscarCotacaoDolar() {
  const { data } = await axios.get('https://economia.awesomeapi.com.br/json/last/USD-BRL', { timeout: 5000 })
  return parseFloat(data.USDBRL.bid)
}

export async function buscarCotacaoComCache() {
  if (cotacaoCache.valor && Date.now() < cotacaoCache.expiraEm) {
    return cotacaoCache.valor
  }
  const valor = await buscarCotacaoDolar()
  cotacaoCache = { valor, expiraEm: Date.now() + 60_000 }
  return cotacaoCache.valor
}

export async function buscarCotacaoComFallback() {
  try {
    return await buscarCotacaoComCache()
  } catch {
    console.warn('API de cotação indisponível, usando valor de fallback')
    return 5.0
  }
}
