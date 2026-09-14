// utils/useAcelerometro.js
// Origem: Tutorial_PAMII.md — 6. Acesso a Recursos Nativos via Framework (Passos 1 e 2)
import { Accelerometer } from 'expo-sensors';
import { useEffect, useState } from 'react';

export function useAcelerometro(intervaloMs = 200) {
  const [dados, setDados] = useState({ x: 0, y: 0, z: 0 });
  const [disponivel, setDisponivel] = useState(false);

  useEffect(() => {
    let assinatura;
    let cancelado = false;

    Accelerometer.isAvailableAsync().then((temSensor) => {
      if (cancelado) return;
      setDisponivel(temSensor);
      if (!temSensor) return;

      Accelerometer.setUpdateInterval(intervaloMs);
      assinatura = Accelerometer.addListener(setDados);
    });

    return () => {
      cancelado = true;
      assinatura?.remove();
    };
  }, [intervaloMs]);

  return { ...dados, disponivel };
}
