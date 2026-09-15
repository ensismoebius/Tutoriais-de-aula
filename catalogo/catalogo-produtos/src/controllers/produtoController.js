import prisma from '../database.js'

export async function listar(req, res) {
  const pagina = Number(req.query.pagina) || 1
  const porPagina = Number(req.query.porPagina) || 10

  const produtos = await prisma.produto.findMany({
    include: { categoria: true },
    skip: (pagina - 1) * porPagina,
    take: porPagina,
  })

  const total = await prisma.produto.count()

  res.json({ pagina, porPagina, total, produtos })
}

export async function criar(req, res) {
  const { nome, sku, preco, estoque, categoriaNome } = req.body

  if (!nome) {
    return res.status(422).json({ erro: 'Campo "nome" é obrigatório', status: 422 })
  }
  if (preco === undefined || Number(preco) <= 0) {
    return res.status(422).json({ erro: 'Campo "preco" deve ser maior que zero', status: 422 })
  }

  const produto = await prisma.produto.create({
    data: {
      nome, sku, preco, estoque,
      ...(categoriaNome && {
        categoria: {
          connectOrCreate: {
            where: { nome: categoriaNome },
            create: { nome: categoriaNome },
          },
        },
      }),
    },
    include: { categoria: true },
  })

  res.status(201).json(produto)
}

export async function atualizar(req, res) {
  const { id } = req.params
  const produto = await prisma.produto.update({
    where: { id: Number(id) },
    data: req.body,
  })

  const io = req.app.get('io')
  io.to(`produto:${produto.id}`).emit('produto:atualizado', produto)

  res.json(produto)
}

export async function deletar(req, res) {
  const { id } = req.params
  await prisma.produto.delete({ where: { id: Number(id) } })
  res.status(204).send()
}

export async function listarCategorias(req, res) {
  const categorias = await prisma.categoria.findMany({
    include: { produtos: true },
  })
  res.json(categorias)
}
