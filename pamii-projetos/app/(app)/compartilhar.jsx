// app/(app)/compartilhar.jsx
// Origem: Tutorial_PAMII.md — 4. Permissões Avançadas (Passos 1 a 5)
import { useState } from 'react';
import { View, Button, Text, FlatList, StyleSheet } from 'react-native';
import * as Contacts from 'expo-contacts/legacy';
import AvisoPermissaoNegada from '../../components/AvisoPermissaoNegada';

export default function Compartilhar() {
  const [contatos, setContatos] = useState([]);
  const [status, setStatus] = useState(null);
  const [podePedirNovamente, setPodePedirNovamente] = useState(true);
  const [carregando, setCarregando] = useState(false);

  async function buscarContatos() {
    setCarregando(true);
    try {
      const permissao = await Contacts.requestPermissionsAsync();
      setStatus(permissao.status);
      setPodePedirNovamente(permissao.canAskAgain);

      if (permissao.status !== 'granted') return;

      const { data } = await Contacts.getContactsAsync({
        fields: [Contacts.Fields.PhoneNumbers],
      });
      setContatos(data.filter((c) => c.phoneNumbers?.length > 0));
    } finally {
      setCarregando(false);
    }
  }

  return (
    <View style={styles.container}>
      <Button
        title={carregando ? 'Carregando...' : 'Carregar contatos'}
        onPress={buscarContatos}
        disabled={carregando}
      />

      {status && status !== 'granted' && (
        <AvisoPermissaoNegada
          recurso="aos contatos"
          podePedirNovamente={podePedirNovamente}
          aoTentarNovamente={buscarContatos}
        />
      )}

      <FlatList
        data={contatos}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => <Text style={styles.item}>{item.name}</Text>}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12 },
  item: { paddingVertical: 4 },
});
