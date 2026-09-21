// Teste automatizado do fluxo de login com Google, cobrindo tudo o que roda
// DEPOIS da verificação do ID token: upsert do usuário, emissão do JWT e a
// aceitação desse JWT pelo middleware autenticar numa rota protegida real.
//
// A única coisa que este teste substitui é o módulo 'google-auth-library'
// (mock.module, nativo do node:test) — ou seja, o limite exato onde este
// ambiente não tem como obter um ID token assinado de verdade pelo Google
// (isso exigiria um Client ID real e um login num navegador real). Tudo a
// partir do payload devolvido pelo mock — upsert no Prisma, jwt.sign,
// jwt.verify dentro de autenticar — usa o código de produção real, contra o
// banco MySQL/MariaDB real usado pelo resto deste projeto.
//
// Rode com: npm test
// (mock.module ainda é experimental no Node 26, daí a flag
// --experimental-test-module-mocks configurada no script "test" do package.json)
import { test, after } from 'node:test'
import assert from 'node:assert/strict'
import 'dotenv/config'
import jwt from 'jsonwebtoken'
import prisma from '../database.js'

after(async () => {
  await prisma.$disconnect()
})

test('loginGoogle cria usuário via Google (verifyIdToken mockado) e emite um JWT que autenticar aceita de verdade', async (t) => {
  const sufixo = Date.now()
  const emailTeste = `teste.google.${sufixo}@gmail.com`
  const googleIdTeste = `google-id-teste-${sufixo}`
  const payloadFalso = {
    sub: googleIdTeste,
    email: emailTeste,
    name: 'Teste Google',
    email_verified: true,
  }

  // Mock confinado a este arquivo de teste: substitui inteiramente o módulo
  // 'google-auth-library' antes de importar o controller, então
  // authController.js roda com seu próprio código de produção inalterado —
  // só a chamada de rede para as chaves públicas do Google é que nunca
  // acontece aqui.
  t.mock.module('google-auth-library', {
    exports: {
      OAuth2Client: class OAuth2ClientFalso {
        constructor() {}
        async verifyIdToken({ idToken }) {
          assert.equal(idToken, 'credential-de-teste')
          return { getPayload: () => payloadFalso }
        }
      },
    },
  })

  const { loginGoogle } = await import('./authController.js')
  const { autenticar } = await import('../middlewares/autenticar.js')

  // 1) Chama o controller real como o Express chamaria, com req/res mínimos.
  const req = { body: { credential: 'credential-de-teste' } }
  let statusCode = null
  let corpo = null
  const res = {
    status(c) { statusCode = c; return this },
    json(b) { corpo = b ?? corpo; return this },
  }

  await loginGoogle(req, res)

  assert.equal(statusCode, null, 'loginGoogle não deve retornar erro para um payload válido')
  assert.ok(corpo?.token, 'loginGoogle deve devolver um token')

  // 2) Confirma a linha real criada no banco: senhaHash nulo, googleId salvo.
  const usuario = await prisma.usuario.findUnique({ where: { googleId: googleIdTeste } })
  assert.ok(usuario, 'usuário deveria ter sido criado no banco real')
  assert.equal(usuario.email, emailTeste)
  assert.equal(usuario.senhaHash, null)

  t.after(async () => {
    await prisma.usuario.delete({ where: { id: usuario.id } })
  })

  // 3) Confirma que o JWT emitido é o mesmo formato usado pelo login por
  // senha (mesmo jwt.sign, mesmo SEGREDO) e que jwt.verify o aceita.
  const payloadToken = jwt.verify(corpo.token, process.env.JWT_SECRET)
  assert.equal(payloadToken.id, usuario.id)
  assert.equal(payloadToken.email, usuario.email)

  // 4) Confirma que o middleware autenticar — o mesmo usado em POST
  // /produtos — aceita esse token de verdade, sem qualquer mock.
  const reqProtegido = { headers: { authorization: `Bearer ${corpo.token}` } }
  let bloqueado = false
  const resProtegido = { status() { bloqueado = true; return this }, json() { return this } }
  let liberado = false
  autenticar(reqProtegido, resProtegido, () => { liberado = true })

  assert.equal(bloqueado, false, 'autenticar não deveria bloquear um token válido emitido pelo login Google')
  assert.equal(liberado, true, 'autenticar deveria chamar next() e liberar a requisição')
  assert.equal(reqProtegido.usuario.id, usuario.id)
})
