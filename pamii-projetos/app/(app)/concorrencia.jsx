// app/(app)/concorrencia.jsx
// Origem: Tutorial_PAMII.md — 3. Concorrência e Threads (Passos 2 e 3)
import { useState } from 'react';
import { View, Button, Text, StyleSheet } from 'react-native';
import { distanciaTotal, distanciaTotalEmLotes } from '../../utils/calculos';

function gerarPontosDeTeste(quantidade) {
  const pontos = [];
  for (let i = 0; i < quantidade; i++) {
    pontos.push({ latitude: -23.55 + i * 0.00001, longitude: -46.63 + i * 0.00001 });
  }
  return pontos;
}

export default function Concorrencia() {
  const [resultado, setResultado] = useState(null);

  function rodarBloqueante() {
    const pontos = gerarPontosDeTeste(2_000_000);
    const metros = distanciaTotal(pontos);
    setResultado(metros);
  }

  function rodarEmLotes() {
    const pontos = gerarPontosDeTeste(2_000_000);
    distanciaTotalEmLotes(pontos, (metros) => setResultado(metros));
  }

  return (
    <View style={styles.container}>
      <Button title="Rodar bloqueante" onPress={rodarBloqueante} />
      <Button title="Rodar em lotes" onPress={rodarEmLotes} />
      {resultado != null && <Text>{Math.round(resultado)} metros</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12, justifyContent: 'center' },
});
