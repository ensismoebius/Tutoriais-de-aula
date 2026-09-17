// servidor-rotas/index.js
// Origem: Tutorial_PAMII.md — Criando sua própria API (Node.js/Express) e autenticação com JWT
//         (Passos 2, 4 e 5)
const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');

const app = express();
app.use(cors());
app.use(express.json());

const SEGREDO = 'troque-isso-por-uma-variavel-de-ambiente-em-producao';

const ROTAS_PLANEJADAS = [
  { id: '1', nome: 'Volta do parque', distanciaEstimadaKm: 3.2 },
  { id: '2', nome: 'Orla da praia', distanciaEstimadaKm: 5.8 },
];

app.get('/rotas', (req, res) => {
  res.json(ROTAS_PLANEJADAS);
});

app.post('/login', (req, res) => {
  const { email, senha } = req.body;

  if (email === 'atleta@exemplo.com' && senha === '123456') {
    const token = jwt.sign({ email }, SEGREDO, { expiresIn: '1h' });
    return res.json({ token });
  }

  res.status(401).json({ erro: 'E-mail ou senha inválidos.' });
});

function exigirToken(req, res, next) {
  const cabecalho = req.headers.authorization; // formato esperado: "Bearer <token>"
  const token = cabecalho?.split(' ')[1];

  if (!token) {
    return res.status(401).json({ erro: 'Token não enviado.' });
  }

  try {
    req.usuario = jwt.verify(token, SEGREDO);
    next();
  } catch {
    res.status(401).json({ erro: 'Token inválido ou expirado.' });
  }
}

app.post('/corridas', exigirToken, (req, res) => {
  const { distancia, duracao } = req.body;
  res.status(201).json({ mensagem: `Corrida de ${req.usuario.email} salva.`, distancia, duracao });
});

app.listen(3000, () => console.log('Servidor rodando em http://localhost:3000'));
