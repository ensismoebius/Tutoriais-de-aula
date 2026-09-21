import { useEffect, useRef } from 'react'

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID

function EntrarComGoogle({ aoAutenticar }) {
  const botaoRef = useRef(null)

  useEffect(() => {
    // window.google só existe depois que o script do Google Identity
    // Services (carregado em index.html) terminar de baixar.
    if (!window.google || !botaoRef.current) return

    window.google.accounts.id.initialize({
      client_id: GOOGLE_CLIENT_ID,
      callback: (resposta) => aoAutenticar(resposta.credential),
    })

    window.google.accounts.id.renderButton(botaoRef.current, {
      theme: 'outline',
      size: 'large',
    })
  }, [aoAutenticar])

  return <div ref={botaoRef} />
}

export default EntrarComGoogle
