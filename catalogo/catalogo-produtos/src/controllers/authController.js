import bcrypt from 'bcryptjs'
import jwt from 'jsonwebtoken'
import prisma from '../database.js'

const SEGREDO = process.env.JWT_SECRET

export async function registrar(req, res) {
  const { nome, email, senha } = req.body
  const senhaHash = await bcrypt.hash(senha, 10)

  const usuario = await prisma.usuario.create({
    data: { nome, email, senhaHash },
  })

  res.status(201).json({ id: usuario.id, nome: usuario.nome, email: usuario.email })
}

export async function login(req, res) {
  const { email, senha } = req.body

  const usuario = await prisma.usuario.findUnique({ where: { email } })
  if (!usuario) {
    return res.status(401).json({ erro: 'Credenciais inválidas' })
  }

  const senhaValida = await bcrypt.compare(senha, usuario.senhaHash)
  if (!senhaValida) {
    return res.status(401).json({ erro: 'Credenciais inválidas' })
  }

  const token = jwt.sign(
    { id: usuario.id, email: usuario.email },
    SEGREDO,
    { expiresIn: '2h' }
  )

  res.json({ token })
}
