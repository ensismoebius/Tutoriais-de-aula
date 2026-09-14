// app/(app)/_layout.jsx
// Origem: Tutorial_PAMII.md — Autenticação e Proteção de Telas (Passo 5, base)
//         + Menu de navegação (Drawer) — acesso a todas as telas
import { Drawer } from 'expo-router/drawer';
import { DrawerContentScrollView, DrawerItemList, DrawerItem } from 'expo-router/drawer';
import { useAuth } from '../../context/AuthContext';

function ConteudoDoMenu(props) {
  const { sair } = useAuth();

  return (
    <DrawerContentScrollView {...props}>
      <DrawerItemList {...props} />
      <DrawerItem label="Sair" onPress={() => sair()} />
    </DrawerContentScrollView>
  );
}

export default function AppLayout() {
  return (
    <Drawer drawerContent={(props) => <ConteudoDoMenu {...props} />}>
      <Drawer.Screen name="index" options={{ drawerLabel: 'Início', title: 'App de Corrida' }} />
      <Drawer.Screen name="rotas" options={{ drawerLabel: 'Rotas planejadas', headerShown: false }} />
      <Drawer.Screen name="corrida" options={{ drawerLabel: 'Corrida', title: 'Corrida' }} />
      <Drawer.Screen name="historico" options={{ drawerLabel: 'Histórico', title: 'Histórico' }} />
      <Drawer.Screen name="cep" options={{ drawerLabel: 'Consulta de CEP', title: 'CEP' }} />
      <Drawer.Screen name="compartilhar" options={{ drawerLabel: 'Compartilhar', title: 'Compartilhar' }} />
      <Drawer.Screen name="concorrencia" options={{ drawerLabel: 'Concorrência (demo)', title: 'Concorrência' }} />
    </Drawer>
  );
}
