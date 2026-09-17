// components/RaceMap.jsx
// Origem: Tutorial_PAMII.md — 5. Localização e Mapas (Passos 5 e 6)
//         + Animações com Reanimated (Passo 1, fade-in ao carregar)
import { useEffect, useRef, useState } from 'react';
import { StyleSheet } from 'react-native';
import { WebView } from 'react-native-webview';
import Animated, { useAnimatedStyle, useSharedValue, withTiming } from 'react-native-reanimated';
import { obterMapaCorridaHtml } from '../utils/mapaHtml';

const REGIAO_INICIAL = { latitude: -23.5505, longitude: -46.6333 };

export function RaceMap({ pontos }) {
  const webviewRef = useRef(null);
  const pontosEnviadosRef = useRef(0);
  const [mapaPronto, setMapaPronto] = useState(false);
  const opacidade = useSharedValue(0);

  const [htmlInicial] = useState(() =>
    obterMapaCorridaHtml(REGIAO_INICIAL.latitude, REGIAO_INICIAL.longitude)
  );

  useEffect(() => {
    if (mapaPronto) {
      opacidade.value = withTiming(1, { duration: 400 });
    }
  }, [mapaPronto]);

  const estiloAnimado = useAnimatedStyle(() => ({
    opacity: opacidade.value,
  }));

  useEffect(() => {
    const webview = webviewRef.current;
    if (!mapaPronto || !webview) return;

    for (let i = pontosEnviadosRef.current; i < pontos.length; i++) {
      const { latitude, longitude } = pontos[i];
      webview.injectJavaScript(
        `window.adicionarPonto && window.adicionarPonto(${latitude}, ${longitude}); true;`
      );
    }
    pontosEnviadosRef.current = pontos.length;
  }, [pontos, mapaPronto]);

  return (
    <Animated.View style={[styles.map, estiloAnimado]}>
      <WebView
        ref={webviewRef}
        originWhitelist={['*']}
        source={{ html: htmlInicial }}
        style={styles.map}
        onMessage={(evento) => {
          if (evento.nativeEvent.data === 'pronto') setMapaPronto(true);
        }}
      />
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  map: { flex: 1 },
});
