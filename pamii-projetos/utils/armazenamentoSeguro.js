// utils/armazenamentoSeguro.js
// Origem: Tutorial_PAMII.md — Autenticação e Proteção de Telas (Passo 2)
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

const CHAVE_TOKEN = 'corrida_token';

export async function salvarTokenSeguro(token) {
  if (Platform.OS === 'web') {
    localStorage.setItem(CHAVE_TOKEN, token);
    return;
  }
  await SecureStore.setItemAsync(CHAVE_TOKEN, token);
}

export async function lerTokenSeguro() {
  if (Platform.OS === 'web') {
    return localStorage.getItem(CHAVE_TOKEN);
  }
  return SecureStore.getItemAsync(CHAVE_TOKEN);
}

export async function apagarTokenSeguro() {
  if (Platform.OS === 'web') {
    localStorage.removeItem(CHAVE_TOKEN);
    return;
  }
  await SecureStore.deleteItemAsync(CHAVE_TOKEN);
}
