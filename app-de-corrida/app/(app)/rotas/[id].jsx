// app/(app)/rotas/[id].jsx
// Origem: Tutorial_PAMII.md — 1. Componentes e Navegação no Framework (Passo 3)
import { useLocalSearchParams, useRouter } from 'expo-router';
import { Button, StyleSheet, Text, View } from 'react-native';

export default function DetalhesRota() {
  const { id, nome, distanciaEstimadaKm } = useLocalSearchParams();
  const router = useRouter();

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>{nome || `Rota ${id}`}</Text>
      {distanciaEstimadaKm && <Text>{distanciaEstimadaKm} km estimados</Text>}
      <Button title="Iniciar corrida" onPress={() => router.push('/corrida')} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 8 },
  titulo: { fontSize: 20, fontWeight: '600' },
});
