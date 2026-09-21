import bcrypt from 'bcryptjs'
import jwt from 'jsonwebtoken'
import { OAuth2Client } from 'google-auth-library'
import prisma from '../database.js'

const SEGREDO = process.env.JWT_SECRET
const clienteGoogle = new OAuth2Client(process.env.GOOGLE_CLIENT_ID)

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
  if (!usuario || !usuario.senhaHash) {
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

// Confere a assinatura do credential (ID token) junto às chaves públicas do
// Google — chamada de rede real, feita pela própria google-auth-library — e
// devolve o payload já validado (sub, email, name, ...).
export async function verificarIdTokenGoogle(credential) {
  const ticket = await clienteGoogle.verifyIdToken({
    idToken: credential,
    audience: process.env.GOOGLE_CLIENT_ID,
  })
  return ticket.getPayload()
}

export async function loginGoogle(req, res) {
  const { credential } = req.body

  let payload
  try {
    payload = await verificarIdTokenGoogle(credential)
  } catch (err) {
    return res.status(401).json({ erro: 'Token do Google inválido' })
  }

  const { sub: googleId, email, name: nome } = payload

  const usuario = await prisma.usuario.upsert({
    where: { googleId },
    update: {},
    create: { nome, email, googleId },
  })

  const token = jwt.sign(
    { id: usuario.id, email: usuario.email },
    SEGREDO,
    { expiresIn: '2h' }
  )

  res.json({ token })
}
