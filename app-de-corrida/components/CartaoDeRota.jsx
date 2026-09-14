// components/CartaoDeRota.jsx
// Origem: Tutorial_PAMII.md — 1. Componentes e Navegação no Framework
//         + Mídia — imagens, sons e outros arquivos (Passo 2, imagem do cartão)
import { TouchableOpacity, Text } from 'react-native';
import { Image } from 'expo-image';

export default function CartaoDeRota({ rota, onPress }) {
  return (
    <TouchableOpacity onPress={onPress}>
      <Image source={rota.imagemUrl} style={{ width: 60, height: 60, borderRadius: 8 }} contentFit="cover" />
      <Text>{rota.nome}</Text>
      <Text>{rota.distanciaEstimadaKm} km</Text>
    </TouchableOpacity>
  );
}
