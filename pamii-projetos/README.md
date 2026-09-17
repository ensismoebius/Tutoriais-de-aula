# App de Corrida

Implementação de referência do app construído ao longo de `Tutorial_PAMII.md` (pasta acima). Cada arquivo traz um comentário no topo apontando de qual tópico do tutorial ele veio — veja a seção "Projeto completo — todos os arquivos juntos" no final do tutorial para o mesmo conteúdo comentado, lado a lado com a explicação de cada passo.

## Rodando o app

```bash
npm install
npx expo start
```

Escaneie o QR code com o Expo Go (Android) ou a câmera do iPhone, ou aperte `a`/`i` para abrir num emulador.

O login (tela `app/login.jsx`) usa a API pública de teste `reqres.in` — entre com `eve.holt@reqres.in` / `cityslicka`. Também existe um usuário de teste **local**, que não depende de internet nem do reqres.in: `teste@teste.com` / `1234` (ver `context/AuthContext.jsx`).

## Rodando o servidor (Node/Express + JWT)

Projeto separado, em `servidor-rotas/` — não faz parte do bundle do app:

```bash
cd servidor-rotas
npm install
npm start
```

Sobe em `http://localhost:3000`. Do celular físico, troque `localhost` pelo IP local do computador na mesma rede Wi-Fi (ver o tópico "Criando sua própria API" no tutorial).

## Módulo nativo (Kotlin)

`modules/informacoes-do-dispositivo/` foi gerado com `npx create-expo-module@latest --local`, depois prebuilado (`npx expo prebuild --platform android`, o que criou a pasta `android/` — gitignorada, como é padrão em projetos Expo com Continuous Native Generation) e **compilado de verdade** com:

```bash
cd android
./gradlew :informacoes-do-dispositivo:compileDebugKotlin
```

`BUILD SUCCESSFUL`, com o `.class` do módulo em `modules/informacoes-do-dispositivo/android/build/tmp/kotlin-classes/debug/`. Nenhuma tela do app importa `utils/informacoesDoDispositivo.js` ainda (é só a ponte JS↔nativo, sem uso na interface) — para rodar o app com esse módulo embarcado, use `npx expo run:android` em vez de `npx expo start` (o Expo Go genérico não contém código nativo de terceiros).

## Ícone e splash screen

`app.json` define um ícone próprio (`assets/images/icon.png` + as três camadas do ícone adaptativo do Android em `android-icon-*.png`) e uma splash screen própria (`assets/images/splash-icon.png`, cor de fundo `#2196F3` — a mesma cor da rota no mapa). Veja o tópico "Personalizando o ícone e a tela de abertura (splash screen)" no tutorial para o significado de cada arquivo.

**Importante:** o Expo Go sempre mostra o ícone e a splash do próprio Expo Go, nunca os deste projeto — para ver o ícone/splash de verdade é preciso um build nativo (`npx expo run:android`, ou o `eas build` do Tópico 10), não `npx expo start` sozinho. Depois de trocar essas imagens, rode `npx expo prebuild --platform android --clean` antes de `npx expo run:android`, para regenerar os recursos nativos a partir dos arquivos novos.

## O que ainda falta para produção

- A versão iOS/Swift do módulo nativo não foi implementada (fica como Desafio no tutorial) — exigiria um Mac com Xcode para compilar.
- O array `ROTAS_PLANEJADAS` em `app/(app)/rotas/index.jsx` é fixo; o servidor em `servidor-rotas/` já expõe o mesmo dado via `GET /rotas` — trocar um pelo outro é um dos Desafios do tutorial.

## Verificação

Este projeto inteiro (todas as telas, hooks, contexts e utils juntos) foi exportado com `npx expo export --platform android` e bundlou sem erros antes de ser entregue nesta pasta — repetido depois de trocar `assets/sons/apito.mp3` por um som de verdade e de gerar/compilar o módulo nativo, para confirmar que nada quebrou. O módulo Kotlin foi compilado de verdade com Gradle (ver acima).

Além disso, o app inteiro foi instalado e testado à mão num celular físico (Motorola Moto G53 5G, Android 14) via `npx expo run:android`: login (com erro real da API e sucesso real), lista e detalhe de rotas, tela de corrida completa (mapa Leaflet real, permissão de localização concedida de verdade, GPS real marcando a posição no mapa, animação do botão, gráfico de velocidade), histórico (SQLite + gráfico), permissão de contatos (concedida de verdade, carregando os contatos reais do aparelho) e a tela de concorrência (cálculo bloqueante vs. em lotes, ambos concluindo corretamente). Nenhum erro apareceu no `logcat` durante todo o teste.

## Problema conhecido ao compilar do zero — cache do Gradle corrompido

Na primeira tentativa de `npx expo run:android` neste projeto, uma falha pontual (`:react-native-svg:compileDebugJavaWithJavac FAILED`, causada por uma condição de corrida ao apagar um diretório — provavelmente dois processos Gradle rodando ao mesmo tempo) deixou uma entrada **corrompida** no cache de build do Gradle (`~/.gradle/caches/build-cache-1`). Builds seguintes reaproveitaram essa entrada corrompida sem revalidar (`FROM-CACHE`), o que fazia o módulo `react-native-svg` parecer compilado com sucesso, mas **faltando duas classes** (`SvgView`, `VirtualView`) — e isso só se manifestava bem mais adiante, como um erro totalmente desconexo em outro módulo (`react-native-gesture-handler:compileDebugKotlin`, com "Unresolved reference 'SvgView'"), o que tornava a causa raiz difícil de enxergar a partir da mensagem de erro sozinha.

**Se isso acontecer com você:** pare os daemons e limpe o cache de build antes de tentar de novo:

```bash
cd android
./gradlew --stop
rm -rf ~/.gradle/caches/build-cache-1
rm -rf ../node_modules/*/android/build build app/build
cd ..
npx expo run:android
```

Essa é uma boa demonstração prática de por que "limpar o cache" (já mencionado no tópico de Ferramentas, sobre `npx expo start --clear`) também vale para o lado nativo/Gradle, não só para o Metro.
