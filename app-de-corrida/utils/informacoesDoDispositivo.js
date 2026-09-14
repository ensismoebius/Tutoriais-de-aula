// utils/informacoesDoDispositivo.js
// Origem: Tutorial_PAMII.md — Chamando código nativo (Kotlin/Java) a partir do React Native (Passo 4)
//
// O módulo nativo (modules/informacoes-do-dispositivo/) foi gerado com
// "npx create-expo-module@latest --local" e compilado de verdade com
// "npx expo prebuild --platform android" + Gradle — ver modules/informacoes-do-dispositivo/README.md.
import InformacoesDoDispositivo from '../modules/informacoes-do-dispositivo/src/InformacoesDoDispositivoModule';

export function lerNivelDeBateriaBruto() {
  return InformacoesDoDispositivo.nivelDeBateriaBruto();
}
