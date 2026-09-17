// utils/useDetectorDeChacoalhada.js
// Origem: Tutorial_PAMII.md — 6. Acesso a Recursos Nativos via Framework (Passo 3)
import { useEffect, useRef } from 'react';
import { useAcelerometro } from './useAcelerometro';

export function useDetectorDeChacoalhada(aoChacoalhar, limiar = 2.5) {
  const { x, y, z } = useAcelerometro();
  const ultimaChacoalhada = useRef(0);

  useEffect(() => {
    const forca = Math.sqrt(x * x + y * y + z * z);
    const agora = Date.now();

    // exige pelo menos 1 segundo entre uma detecção e outra
    if (forca > limiar && agora - ultimaChacoalhada.current > 1000) {
      ultimaChacoalhada.current = agora;
      aoChacoalhar();
    }
  }, [x, y, z, limiar]);
}
