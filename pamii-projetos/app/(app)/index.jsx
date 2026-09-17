// app/(app)/index.jsx
// Origem: Tutorial_PAMII.md — Fundamentos de JavaScript para React (Passo 5, lista de favoritos)
//         + 1. Componentes e Navegação (link para /rotas)
//         + Autenticação e Proteção de Telas (Passo 7, botão Sair)
import { useState } from 'react';
import { StyleSheet, Text, View, Button, FlatList, Platform } from 'react-native';
import { useRouter } from 'expo-router';
import { useAuth } from '../../context/AuthContext';

export default function Inicio() {
  const router = useRouter();
  const { sair } = useAuth();
  const [favoritos, setFavoritos] = useState([]);
  let proximoId = favoritos.length + 1;

  function adicionarFavorito() {
    setFavoritos((anteriores) => [...anteriores, { id: String(proximoId), nome: `Rota ${proximoId}` }]);
  }

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>App de Corrida</Text>
      <Button title="Ver rotas planejadas" onPress={() => router.push('/rotas')} />
      <Button title="Adicionar rota favorita" onPress={() => adicionarFavorito()} />
      <FlatList
        data={favoritos}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => <Text>{item.nome}</Text>}
      />
      <Button title="Sair" onPress={() => sair()} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
    paddingTop: Platform.select({ ios: 20, android: 32, default: 0 }),
  },
  titulo: { fontSize: 28, fontWeight: 'bold' },
});
