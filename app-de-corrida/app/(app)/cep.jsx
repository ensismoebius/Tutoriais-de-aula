// app/(app)/cep.jsx
// Origem: Tutorial_PAMII.md — Promises, async/await e funções assíncronas (Passo 4)
import { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';

export default function ConsultaCep() {
  const [cep, setCep] = useState('');
  const [dados, setDados] = useState(null);
  const [erro, setErro] = useState(null);

  async function consultarCep() {
    setErro(null);
    setDados(null);
    try {
      const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
      const dadoEmJson = await resposta.json();
      if (dadoEmJson.erro) {
        setErro('CEP não encontrado.');
      } else {
        setDados(dadoEmJson);
      }
    } catch (erroDeRede) {
      setErro('Falha de conexão. Tente novamente.');
    }
  }

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="Digite o CEP"
        value={cep}
        onChangeText={setCep}
        keyboardType="numeric"
      />
      <Button title="Buscar" onPress={() => consultarCep()} />
      {erro && <Text style={styles.erro}>{erro}</Text>}
      {dados && (
        <Text>{dados.logradouro}, {dados.localidade} - {dados.uf}</Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12, justifyContent: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 8, padding: 10 },
  erro: { color: '#c62828' },
});
