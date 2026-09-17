import { PrismaClient } from '../generated/prisma/client.ts'
import { PrismaMariaDb } from '@prisma/adapter-mariadb'
import 'dotenv/config'

const url = new URL(process.env.DATABASE_URL)

const adapter = new PrismaMariaDb({
  host: url.hostname,
  port: Number(url.port) || 3306,
  user: decodeURIComponent(url.username),
  password: decodeURIComponent(url.password),
  database: url.pathname.replace(/^\//, ''),
})
const prisma = new PrismaClient({ adapter })

async function main() {
  const perifericos = await prisma.categoria.create({ data: { nome: 'Periféricos' } })
  const monitores = await prisma.categoria.create({ data: { nome: 'Monitores' } })

  const produtos = [
    { nome: 'Teclado Mecânico', sku: 'TEC-001', preco: 249.90, estoque: 15, categoriaId: perifericos.id },
    { nome: 'Mouse Sem Fio', sku: 'MOU-002', preco: 89.90, estoque: 30, categoriaId: perifericos.id },
    { nome: 'Monitor 24"', sku: 'MON-003', preco: 799.00, estoque: 8, categoriaId: monitores.id },
  ]

  for (const p of produtos) {
    await prisma.produto.create({ data: p })
    console.log(`Criado: ${p.nome}`)
  }
}

main()
  .then(() => prisma.$disconnect())
  .catch(e => { console.error(e); prisma.$disconnect(); process.exit(1) })
