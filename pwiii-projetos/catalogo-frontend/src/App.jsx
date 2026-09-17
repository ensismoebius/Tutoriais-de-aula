import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { io } from 'socket.io-client'
import './App.css'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000'
const socket = io(API_URL)

async function criarProduto(dados) {
  const resposta = await fetch(`${API_URL}/produtos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
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

function FormularioProduto({ aoCriar }) {
  const { t } = useTranslation()
  const [nome, setNome] = useState('')
  const [preco, setPreco] = useState('')

  async function enviar(e) {
    e.preventDefault()
    if (!nome || !preco) return
    const novoProduto = await criarProduto({
      nome,
      sku: `${nome.toUpperCase().replace(/\s+/g, '').slice(0, 5)}-${Date.now()}`,
      preco: Number(preco),
      estoque: 0,
    })
    aoCriar(novoProduto)
    setNome('')
    setPreco('')
  }

  return (
    <form onSubmit={enviar}>
      <h2>{t('produtos.novo')}</h2>
      <input value={nome} onChange={e => setNome(e.target.value)} placeholder={t('produtos.nome')} />
      <input value={preco} onChange={e => setPreco(e.target.value)} placeholder={t('produtos.preco')} type="number" step="0.01" />
      <button type="submit">{t('produtos.criar')}</button>
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

  if (carregando) return <p>Carregando...</p>
  if (erro) return <p>Erro: {erro}</p>

  return (
    <>
      <SeletorIdioma />
      <h1>{t('produtos.titulo')}</h1>
      <FormularioProduto aoCriar={aoCriarProduto} />
      <ul>
        {produtos.map(p => <ItemProduto key={p.id} produto={p} />)}
      </ul>
    </>
  )
}

export default App
