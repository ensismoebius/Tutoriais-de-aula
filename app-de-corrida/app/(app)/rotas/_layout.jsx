// app/(app)/rotas/_layout.jsx
// Origem: Tutorial_PAMII.md — Menu de navegação (Drawer) — acesso a todas as telas
//
// Sem este arquivo, "rotas/index" e "rotas/[id]" apareceriam como duas entradas
// soltas no Drawer do layout pai. Com um Stack próprio aqui dentro, as duas telas
// continuam empilhando normalmente entre si, e o Drawer enxerga só uma entrada:
// "rotas" (o nome desta pasta) — o cabeçalho do Drawer para essa entrada fica
// desligado (Passo 3, no layout pai), e este Stack assume o próprio cabeçalho,
// com o botão de abrir o menu só na primeira tela (a de detalhe já ganha uma
// seta de voltar automática, que seria substituída se também tivesse esse botão).
import { Stack } from 'expo-router';
import { DrawerToggleButton } from 'expo-router/drawer';

export default function RotasLayout() {
  return (
    <Stack>
      <Stack.Screen
        name="index"
        options={{ title: 'Rotas planejadas', headerLeft: () => <DrawerToggleButton /> }}
      />
      <Stack.Screen name="[id]" options={{ title: 'Detalhes da rota' }} />
    </Stack>
  );
}
