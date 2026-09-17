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

export default prisma
