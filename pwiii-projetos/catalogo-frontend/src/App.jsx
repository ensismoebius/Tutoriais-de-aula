import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { io } from 'socket.io-client'
import EntrarComGoogle from './components/EntrarComGoogle.jsx'
import './App.css'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000'
const socket = io(API_URL)

async function criarProduto(dados, token) {
  const resposta = await fetch(`${API_URL}/produtos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
    },
    body: JSON.stringify(dados),
  })

  if (!resposta.ok) throw new Error('Falha ao criar produto')
  return resposta.json()
}

function usarEstoqueTempoReal(produtoId, estoqueInicial) {
  const [estoque, setEstoque] = useState(estoqueInicial)

  useEffect(() => {
    socket.emit('produto:observar', produtoId)

    function aoAtualizar(produto) {
      if (produto.id === produtoId) setEstoque(produto.estoque)
    }
    socket.on('produto:atualizado', aoAtualizar)

    return () => {
      socket.emit('produto:parar-observar', produtoId)
      socket.off('produto:atualizado', aoAtualizar)
    }
  }, [produtoId])

  return estoque
}

function ItemProduto({ produto }) {
  const { t } = useTranslation()
  const estoque = usarEstoqueTempoReal(produto.id, produto.estoque)

  return (
    <li>
      <strong>{produto.nome}</strong> — {t('produtos.preco')}: R$ {produto.preco}
      {' — '}
      {t('produtos.estoque', { quantidade: estoque })}
      {produto.categoria && ` (${produto.categoria.nome})`}
    </li>
  )
}

function FormularioProduto({ aoCriar, token }) {
  const { t } = useTranslation()
  const [nome, setNome] = useState('')
  const [preco, setPreco] = useState('')
  const [erroCriacao, setErroCriacao] = useState(null)

  async function enviar(e) {
    e.preventDefault()
    if (!nome || !preco) return
    setErroCriacao(null)
    try {
      const novoProduto = await criarProduto({
        nome,
        sku: `${nome.toUpperCase().replace(/\s+/g, '').slice(0, 5)}-${Date.now()}`,
        preco: Number(preco),
        estoque: 0,
      }, token)
      aoCriar(novoProduto)
      setNome('')
      setPreco('')
    } catch {
      setErroCriacao(t('auth.loginNecessario'))
    }
  }

  return (
    <form onSubmit={enviar}>
      <h2>{t('produtos.novo')}</h2>
      <input value={nome} onChange={e => setNome(e.target.value)} placeholder={t('produtos.nome')} />
      <input value={preco} onChange={e => setPreco(e.target.value)} placeholder={t('produtos.preco')} type="number" step="0.01" />
      <button type="submit">{t('produtos.criar')}</button>
      {erroCriacao && <p>{erroCriacao}</p>}
    </form>
  )
}

function SeletorIdioma() {
  const { i18n } = useTranslation()

  return (
    <select value={i18n.language} onChange={(e) => i18n.changeLanguage(e.target.value)}>
      <option value="pt">Português</option>
      <option value="en">English</option>
    </select>
  )
}

function App() {
  const { t } = useTranslation()
  const [produtos, setProdutos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)
  const [usuarioLogado, setUsuarioLogado] = useState(null)
  const [token, setToken] = useState(null)

  useEffect(() => {
    fetch(`${API_URL}/produtos`)
      .then(res => {
        if (!res.ok) throw new Error(`Erro HTTP: ${res.status}`)
        return res.json()
      })
      .then(dados => setProdutos(dados.produtos))
      .catch(err => setErro(err.message))
      .finally(() => setCarregando(false))
  }, [])

  function aoCriarProduto(novoProduto) {
    setProdutos(anteriores => [...anteriores, novoProduto])
  }

  async function loginComGoogle(credential) {
    // O payload do credential já vem no navegador (é só a segunda parte de
    // um JWT em Base64URL) — decodificá-lo aqui só serve para exibir o nome
    // na hora; quem valida a assinatura de verdade é o backend, no passo
    // seguinte.
    const payloadGoogle = JSON.parse(atob(credential.split('.')[1]))

    const resposta = await fetch(`${API_URL}/auth/google`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ credential }),
    })
    if (!resposta.ok) return

    const dados = await resposta.json()
    setUsuarioLogado({ nome: payloadGoogle.name, email: payloadGoogle.email })
    setToken(dados.token)
  }

  if (carregando) return <p>Carregando...</p>
  if (erro) return <p>Erro: {erro}</p>

  return (
    <>
      <SeletorIdioma />
      <h1>{t('produtos.titulo')}</h1>
      {usuarioLogado
        ? <p>{t('auth.saudacao', { nome: usuarioLogado.nome })}</p>
        : <EntrarComGoogle aoAutenticar={loginComGoogle} />}
      <FormularioProduto aoCriar={aoCriarProduto} token={token} />
      <ul>
        {produtos.map(p => <ItemProduto key={p.id} produto={p} />)}
      </ul>
    </>
  )
}

export default App
