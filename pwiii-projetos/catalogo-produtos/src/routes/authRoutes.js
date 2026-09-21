import { Router } from 'express'
import rateLimit from 'express-rate-limit'
import { registrar, login, loginGoogle } from '../controllers/authController.js'

const router = Router()

const limiterLogin = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 5,
  message: { erro: 'Muitas tentativas de login, aguarde antes de tentar novamente' },
})

/**
 * @openapi
 * /auth/registrar:
 *   post:
 *     summary: Cria um novo usuário
 *     responses:
 *       201:
 *         description: Usuário criado
 */
router.post('/auth/registrar', registrar)

/**
 * @openapi
 * /auth/login:
 *   post:
 *     summary: Autentica um usuário e retorna um JWT
 *     responses:
 *       200:
 *         description: Login bem-sucedido
 *       401:
 *         description: Credenciais inválidas
 *       429:
 *         description: Muitas tentativas
 */
router.post('/auth/login', limiterLogin, login)

/**
 * @openapi
 * /auth/google:
 *   post:
 *     summary: Autentica um usuário via ID token do Google Identity Services e retorna um JWT
 *     responses:
 *       200:
 *         description: Login bem-sucedido
 *       401:
 *         description: Token do Google inválido
 *       429:
 *         description: Muitas tentativas
 */
router.post('/auth/google', limiterLogin, loginGoogle)

export default router
