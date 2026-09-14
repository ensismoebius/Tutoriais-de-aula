// context/CorridaContext.jsx
// Origem: Tutorial_PAMII.md — 9. Gerenciamento de Estado (Context API, Redux) (Passos 1 a 3)
import { createContext, useContext, useReducer } from 'react';

function corridaReducer(state, action) {
  switch (action.type) {
    case 'adicionarPonto':
      return { ...state, pontos: [...state.pontos, action.ponto] };
    case 'reiniciar':
      return { pontos: [] };
    default:
      return state;
  }
}

const CorridaContext = createContext(null);

export function CorridaProvider({ children }) {
  const [state, dispatch] = useReducer(corridaReducer, { pontos: [] });
  return (
    <CorridaContext.Provider value={{ state, dispatch }}>
      {children}
    </CorridaContext.Provider>
  );
}

export function useCorrida() {
  return useContext(CorridaContext);
}
