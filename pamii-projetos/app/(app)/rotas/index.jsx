// app/(app)/rotas/index.jsx
// Origem: Tutorial_PAMII.md — 1. Componentes e Navegação no Framework (Passo 2)
//         + Mídia — imagens, sons e outros arquivos (Passo 2, campo imagemUrl)
import { useRouter } from 'expo-router';
import { FlatList } from 'react-native';
import CartaoDeRota from '../../../components/CartaoDeRota';

const ROTAS_PLANEJADAS = [
  { id: '1', nome: 'Volta do parque', distanciaEstimadaKm: 3.2, imagemUrl: 'https://exemplo.com/imagens/parque.jpg' },
  { id: '2', nome: 'Orla da praia', distanciaEstimadaKm: 5.8, imagemUrl: 'https://exemplo.com/imagens/praia.jpg' },
];

export default function ListaDeRotas() {
  const router = useRouter();

  return (
    <FlatList
      data={ROTAS_PLANEJADAS}
      keyExtractor={(item) => item.id}
      renderItem={({ item }) => (
        <CartaoDeRota
          rota={item}
          onPress={() =>
            router.push({
              pathname: '/rotas/[id]',
              params: { id: item.id, nome: item.nome, distanciaEstimadaKm: item.distanciaEstimadaKm },
            })
          }
        />
      )}
    />
  );
}
