// context/AuthContext.jsx
// Origem: Tutorial_PAMII.md — Autenticação e Proteção de Telas (Passo 3)
import { createContext, useContext, useEffect, useState } from 'react';
import { lerTokenSeguro, salvarTokenSeguro, apagarTokenSeguro } from '../utils/armazenamentoSeguro';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [carregando, setCarregando] = useState(true);

  // ao abrir o app, verifica se já existe um token salvo de uma sessão anterior
  useEffect(() => {
    lerTokenSeguro().then((tokenSalvo) => {
      setToken(tokenSalvo);
      setCarregando(false);
    });
  }, []);

  async function entrar(email, senha) {
    // Usuário de teste local — não depende do reqres.in nem de internet,
    // útil para demonstrar o app offline ou em sala de aula.
    if (email === 'teste@teste.com' && senha === '1234') {
      const tokenLocal = 'token-de-teste-local';
      await salvarTokenSeguro(tokenLocal);
      setToken(tokenLocal);
      return;
    }

    const resposta = await fetch('https://reqres.in/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password: senha }),
    });
    const dados = await resposta.json();

    if (!resposta.ok) {
      throw new Error(dados.error ?? 'Falha no login.');
    }

    await salvarTokenSeguro(dados.token);
    setToken(dados.token);
  }

  async function sair() {
    await apagarTokenSeguro();
    setToken(null);
  }

  return (
    <AuthContext.Provider value={{ token, carregando, entrar, sair }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
