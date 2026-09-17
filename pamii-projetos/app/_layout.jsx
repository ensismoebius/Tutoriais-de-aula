// app/_layout.jsx
// Origem: Tutorial_PAMII.md — Tópico 1 (Passo 1, layout básico) + Tópico 8 (Passo 2, handler de notificação)
//         + Autenticação e Proteção de Telas (Passo 6, Stack.Protected + providers)
import { Stack } from 'expo-router';
import { QueryClientProvider, QueryClient } from '@tanstack/react-query';
import * as Notifications from 'expo-notifications';
import { AuthProvider, useAuth } from '../context/AuthContext';
import { CorridaProvider } from '../context/CorridaContext';

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

const queryClient = new QueryClient();

function NavegacaoRaiz() {
  const { token, carregando } = useAuth();

  if (carregando) return null; // ainda checando se já existe um token salvo

  return (
    <Stack>
      <Stack.Protected guard={!!token}>
        <Stack.Screen name="(app)" options={{ headerShown: false }} />
      </Stack.Protected>

      <Stack.Protected guard={!token}>
        <Stack.Screen name="login" />
      </Stack.Protected>
    </Stack>
  );
}

export default function RootLayout() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <CorridaProvider>
          <NavegacaoRaiz />
        </CorridaProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}
