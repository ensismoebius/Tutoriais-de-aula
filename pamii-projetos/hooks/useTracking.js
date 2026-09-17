// hooks/useTracking.js
// Origem: Tutorial_PAMII.md — 5. Localização e Mapas (Passos 1 a 3)
import { useRef, useState } from 'react';
import * as Location from 'expo-location';

export function useTracking() {
  const [pontos, setPontos] = useState([]);
  const [rastreando, setRastreando] = useState(false);
  const [erro, setErro] = useState(null);
  const assinaturaRef = useRef(null); // guarda a assinatura do GPS, para poder cancelar depois

  async function iniciar() {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== 'granted') {
      setErro('Permissão de localização negada. Ative-a nas configurações do app para rastrear.');
      return;
    }

    setErro(null);
    setPontos([]);
    setRastreando(true);

    assinaturaRef.current = await Location.watchPositionAsync(
      {
        accuracy: Location.Accuracy.High,
        timeInterval: 3000,   // a cada 3 segundos
        distanceInterval: 5,  // ou a cada 5 metros percorridos
      },
      (posicao) => {
        setPontos((anteriores) => [...anteriores, posicao.coords]);
      }
    );
  }

  function parar() {
    assinaturaRef.current?.remove();
    assinaturaRef.current = null;
    setRastreando(false);
  }

  return { pontos, rastreando, erro, iniciar, parar };
}
