// components/AvisoPermissaoNegada.jsx
// Origem: Tutorial_PAMII.md — 4. Permissões Avançadas (Passo 4)
import { View, Text, Button, StyleSheet } from 'react-native';
import * as Linking from 'expo-linking';

export default function AvisoPermissaoNegada({ recurso, podePedirNovamente = true, aoTentarNovamente }) {
  return (
    <View style={styles.caixa}>
      <Text>Sem acesso {recurso}. Ative nas configurações para usar este recurso.</Text>
      {podePedirNovamente ? (
        <Button title="Tentar novamente" onPress={aoTentarNovamente} />
      ) : (
        <Button title="Abrir Configurações" onPress={() => Linking.openSettings()} />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  caixa: { backgroundColor: '#fff3e0', borderRadius: 8, padding: 12, gap: 8 },
});
