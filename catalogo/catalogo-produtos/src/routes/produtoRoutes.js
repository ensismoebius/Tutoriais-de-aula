import { Router } from 'express'
import { create } from 'xmlbuilder2'
import prisma from '../database.js'
import { listar, criar, atualizar, deletar, listarCategorias } from '../controllers/produtoController.js'
import { autenticar } from '../middlewares/autenticar.js'
import { buscarCotacaoComFallback } from '../services/cotacaoService.js'

const router = Router()

/**
 * @openapi
 * /produtos:
 *   get:
 *     summary: Lista produtos do catálogo (paginado)
 *     responses:
 *       200:
 *         description: Lista de produtos
 *   post:
 *     summary: Cria um novo produto
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       201:
 *         description: Produto criado
 *       422:
 *         description: Dados inválidos
 */
router.get('/produtos', listar)
router.post('/produtos', autenticar, criar)

/**
 * @openapi
 * /produtos/{id}:
 *   get:
 *     summary: Busca um produto pelo id (JSON ou XML via Accept header)
 *     security: []
 *     responses:
 *       200:
 *         description: Produto encontrado
 *       404:
 *         description: Produto não encontrado
 *   patch:
 *     summary: Atualiza um produto e notifica observadores em tempo real
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Produto atualizado
 *   delete:
 *     summary: Remove um produto
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       204:
 *         description: Produto removido
 */
router.get('/produtos/:id', async (req, res) => {
  const produto = await prisma.produto.findUnique({
    where: { id: Number(req.params.id) },
    include: { categoria: true },
  })
  if (!produto) return res.status(404).json({ erro: 'Produto não encontrado' })

  if (req.accepts('xml')) {
    const produtoPlano = JSON.parse(JSON.stringify(produto))
    const xml = create({ produto: produtoPlano }).end({ prettyPrint: true })
    return res.type('application/xml').send(xml)
  }
  res.json(produto)
})

router.patch('/produtos/:id', autenticar, atualizar)
router.delete('/produtos/:id', autenticar, deletar)

/**
 * @openapi
 * /produtos/{id}/preco-usd:
 *   get:
 *     summary: Retorna o produto com o preço convertido para dólar
 *     responses:
 *       200:
 *         description: Produto com precoUsd calculado
 *       503:
 *         description: Serviço de cotação indisponível
 */
router.get('/produtos/:id/preco-usd', async (req, res) => {
  try {
    const produto = await prisma.produto.findUnique({ where: { id: Number(req.params.id) } })
    if (!produto) return res.status(404).json({ erro: 'Produto não encontrado' })

    const cotacao = await buscarCotacaoComFallback()
    res.json({ ...produto, precoUsd: (produto.preco / cotacao).toFixed(2) })
  } catch {
    res.status(503).json({ erro: 'Serviço de cotação indisponível' })
  }
})

/**
 * @openapi
 * /categorias:
 *   get:
 *     summary: Lista categorias com seus produtos
 *     responses:
 *       200:
 *         description: Lista de categorias
 */
router.get('/categorias', listarCategorias)

export default router
