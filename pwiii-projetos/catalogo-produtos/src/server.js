import express from 'express'
import cors from 'cors'
import 'dotenv/config'
import { createServer } from 'http'
import { Server } from 'socket.io'
import rateLimit from 'express-rate-limit'
import swaggerUi from 'swagger-ui-express'
import { swaggerSpec } from './swagger.js'
import produtoRoutes from './routes/produtoRoutes.js'
import authRoutes from './routes/authRoutes.js'

const app = express()
app.use(express.json())
app.use(cors({
  origin: ['http://localhost:5173'],
  methods: ['GET', 'POST', 'PATCH', 'DELETE'],
}))
app.use('/docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec))

app.get('/health', (req, res) => res.json({ status: 'ok', message: 'API funcionando!' }))

const limiterGeral = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 100,
  message: { erro: 'Muitas requisições, tente novamente mais tarde' },
})
app.use('/produtos', limiterGeral)

app.use(produtoRoutes)
app.use(authRoutes)

const httpServer = createServer(app)
const io = new Server(httpServer, { cors: { origin: '*' } })

io.on('connection', (socket) => {
  socket.on('produto:observar', (id) => socket.join(`produto:${id}`))
  socket.on('produto:parar-observar', (id) => socket.leave(`produto:${id}`))
})

app.set('io', io)

const PORT = process.env.PORT || 3000
httpServer.listen(PORT, () => {
  console.log(`Servidor rodando em http://localhost:${PORT}`)
  console.log(`Documentação Swagger em http://localhost:${PORT}/docs`)
})
