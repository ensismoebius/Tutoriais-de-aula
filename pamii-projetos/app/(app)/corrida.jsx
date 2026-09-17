// app/(app)/corrida.jsx
// Origem: Tutorial_PAMII.md — 5. Localização e Mapas (Passo 7, base da tela)
//         + Mídia — imagens, sons e outros arquivos (Passo 4, som ao iniciar)
//         + Animações com Reanimated (Passo 2, feedback do botão)
//         + 6. Acesso a Recursos Nativos via Framework (Passo 5, chacoalhada + haptics)
//         + Gráficos — em tempo real e estáticos (Passo 1, velocidade)
import { useState } from 'react';
import { View, Text, Pressable } from 'react-native';
import { useAudioPlayer } from 'expo-audio';
import * as Haptics from 'expo-haptics';
import { LineChart } from 'react-native-gifted-charts';
import Animated, { useAnimatedStyle, useSharedValue, withSpring } from 'react-native-reanimated';
import { useTracking } from '../../hooks/useTracking';
import { RaceMap } from '../../components/RaceMap';
import { useDetectorDeChacoalhada } from '../../utils/useDetectorDeChacoalhada';

const somDeApito = require('../../assets/sons/apito.mp3');

export default function Corrida() {
  const { pontos, rastreando, erro, iniciar, parar } = useTracking();
  const tocador = useAudioPlayer(somDeApito);
  const [pontosDeInteresse, setPontosDeInteresse] = useState([]);
  const escala = useSharedValue(1);

  const estiloBotao = useAnimatedStyle(() => ({
    transform: [{ scale: escala.value }],
  }));

  function iniciarComSom() {
    tocador.seekTo(0);
    tocador.play();
    iniciar();
  }

  useDetectorDeChacoalhada(() => {
    if (!rastreando) return;
    Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
    setPontosDeInteresse((anteriores) => [...anteriores, pontos.at(-1)]);
  });

  const dadosDeVelocidade = pontos.map((ponto) => ({
    value: Math.round((ponto.speed ?? 0) * 3.6), // m/s para km/h
  }));

  return (
    <View style={{ flex: 1 }}>
      <RaceMap pontos={pontos} />

      <Pressable
        onPressIn={() => { escala.value = withSpring(0.92); }}
        onPressOut={() => { escala.value = withSpring(1); }}
        onPress={rastreando ? parar : iniciarComSom}
      >
        <Animated.View style={estiloBotao}>
          <Text>{rastreando ? 'Parar' : 'Iniciar'}</Text>
        </Animated.View>
      </Pressable>

      <Text>Pontos de interesse: {pontosDeInteresse.length}</Text>
      <LineChart data={dadosDeVelocidade} />

      {erro && <Text>{erro}</Text>}
    </View>
  );
}
