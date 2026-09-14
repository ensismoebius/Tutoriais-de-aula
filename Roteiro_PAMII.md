# Apostila — Programação para Aplicativos Móveis II (PAMII)

Este material usa React Native com Expo, sempre em JavaScript, como já vinha sendo feito ao longo do curso.

Cada tópico é organizado em **passos** — construa o código na ordem apresentada, um pedaço de cada vez, testando no celular ou no emulador a cada passo, em vez de copiar o arquivo inteiro de uma vez só. É assim que vocês já vinham trabalhando em aula.

> 💡 **Dica de organização:** nunca commite as pastas `node_modules/` e `.expo/` no Git — ambas são geradas automaticamente (a primeira por `npm install`, a segunda pelo próprio Expo) e mudam de máquina para máquina. E mantenha hooks reutilizáveis em `hooks/` ou `utils/` (como faremos com `useTracking`, `useAcelerometro`) em vez de escrever a lógica direto dentro do arquivo da tela — isso facilita testar a mesma lógica em mais de uma tela.

### Ferramentas do projeto — criar, resetar e manter

**Objetivo:** antes de escrever a primeira linha de código do app de corrida, revisar os comandos que criam, resetam, diagnosticam e atualizam um projeto Expo — a caixa de ferramentas que vamos usar o semestre inteiro.

**Passo 1 — a diferença entre `npm` e `npx`**

`npm` (Node Package Manager) instala e gerencia pacotes: `npm install` baixa as dependências listadas no `package.json` para dentro de `node_modules/`, e `npm install algum-pacote` adiciona um pacote novo a essa lista. `npx` faz outra coisa: ele **executa** o binário de um pacote, sem precisar instalá-lo permanentemente no projeto. Se o pacote já existe em `node_modules/.bin` (caso de `expo`, por exemplo, depois de instalado), `npx` roda essa cópia local; se não existe, ele baixa uma cópia temporária, executa uma vez só, e não deixa nada instalado no projeto.

É por isso que comandos como `npx expo start` e `npx create-expo-app` usam `npx`, e não `npm`: o primeiro roda a CLI do Expo que já está instalada no projeto; o segundo nem precisa que nada esteja instalado antes — ele baixa a ferramenta de criação de projetos, cria o projeto, e pronto, a ferramenta não fica pendurada em lugar nenhum depois.

**Passo 2 — criar um projeto novo**

```bash
npx create-expo-app@latest nome-do-projeto
```

O template padrão (o que vem se você não especificar nenhum `--template`) já inclui **Expo Router** configurado — é o mesmo template que gera a pasta `app/` com rotas por arquivo que vimos no Tópico 1. Para começar com um projeto realmente vazio, sem nenhuma tela de exemplo, use `--template blank` em vez disso.

**Passo 3 — resetar o projeto para um estado limpo**

Todo projeto criado pelo template padrão já vem com um script pronto para descartar as telas de exemplo:

```bash
npm run reset-project
```

Esse comando move o conteúdo atual de `app/` para uma pasta `app-example/` (guardando o código de exemplo, caso você queira consultar depois) e cria uma `app/` nova, com só o essencial para começar do zero. Use-o logo depois de criar o projeto, antes de começar a escrever as telas do Tópico 1 — não há motivo para carregar telas de demonstração que não fazem parte do app de corrida.

**Passo 4 — diagnosticar problemas no projeto**

Dois comandos cobrem propósitos diferentes:

```bash
npx expo-doctor
```

Analisa especificamente a saúde de um projeto Expo: versões de dependências incompatíveis com o SDK instalado, configurações do `app.json` fora de sincronia com as pastas nativas (`android/`, `ios/`, quando existem), e outros problemas comuns desse tipo de projeto.

```bash
npm audit
```

Já esse é um comando do próprio `npm`, sem relação nenhuma com o Expo — ele varre todas as dependências do projeto em busca de vulnerabilidades de segurança conhecidas e reportadas publicamente. Vocês provavelmente já viram esse aviso aparecer depois de um `npm install`, algo como "14 moderate severity vulnerabilities" — vale rodar `npm audit` de vez em quando para ver o detalhe do que foi encontrado, mesmo que nem toda vulnerabilidade reportada afete de fato como o app é usado.

**Passo 5 — atualizar (e entender os limites de rebaixar) o SDK e as bibliotecas**

Para colocar todas as bibliotecas do projeto na versão certa para o SDK do Expo já instalado, sem precisar descobrir manualmente qual versão de cada uma é compatível:

```bash
npx expo install --check   # só avisa quais pacotes estão desalinhados
npx expo install --fix     # corrige automaticamente as versões desalinhadas
```

Para subir de versão de SDK (por exemplo, do 57 para o 58), o caminho oficial é:

```bash
npm install expo@^58.0.0
npx expo install --fix
npx expo-doctor
```

Primeiro atualiza o pacote `expo` em si para a nova faixa de versão, depois realinha todas as outras bibliotecas a essa nova versão do SDK, e por fim roda o diagnóstico para pegar qualquer problema restante antes de testar o app. A documentação oficial recomenda subir **um SDK de cada vez**, e sempre ler as notas de lançamento daquela versão específica em busca de mudanças que quebram compatibilidade.

Já **rebaixar** o SDK (por exemplo, voltar do 58 para o 57) não tem um comando oficial dedicado — a documentação do Expo só documenta o caminho de subir de versão. Na prática, o mais seguro costuma ser reverter o `package.json`/`package-lock.json` para o commit em que o projeto ainda usava aquela versão mais antiga (com `git checkout` desses dois arquivos, ou revertendo o commit da atualização) e rodar `npm install` de novo, em vez de tentar editar manualmente as versões de cada biblioteca à mão — o risco de deixar combinações incompatíveis dessa forma é bem maior do que ao subir de versão.

**Exercícios:**
- **Fácil:** Rode `npx expo-doctor` no seu projeto atual e leia o resultado — mesmo sem nenhum problema, entenda o que cada checagem está validando.
- **Médio:** Crie um projeto novo só para teste (`npx create-expo-app@latest teste-reset`), rode `npm run reset-project` nele, e confira o que mudou em `app/` e o que apareceu em `app-example/`.
- **Desafio:** Depois de instalar todos os pacotes usados neste roteiro (`expo-location`, `expo-sqlite`, `expo-notifications`, etc.), rode `npx expo install --check` e veja se algum ficou desalinhado — se sim, rode `--fix` e confirme que o app ainda bundla normalmente depois.

**Pergunta para fixação:** por que `npx create-expo-app` usa `npx`, e não `npm install create-expo-app` seguido de rodar o comando instalado?

### Revisão — hooks, useState, useEffect e const

**O que é um hook?** É uma função especial do React, sempre com nome começando em `use` (`useState`, `useEffect`, `useRouter`, e os hooks próprios que vamos construir ao longo deste roteiro, como `useTracking`), que dá a um componente acesso a recursos do React — guardar um valor entre renderizações, reagir a mudanças, ler o contexto de navegação, etc. — sem precisar transformar esse componente em uma classe (o jeito antigo de fazer a mesma coisa, hoje raramente usado).

Hooks só podem ser chamados em dois lugares: direto no corpo de um componente, ou dentro de outro hook (é assim que `useTracking`, no Tópico 5, usa `useState` e `useRef` por baixo dos panos). Duas regras simples valem para todos eles:
- **Sempre no topo da função**, nunca dentro de `if`, `for` ou depois de um `return` — o React identifica cada hook pela ordem em que são chamados, então chamá-los condicionalmente bagunça essa ordem entre uma renderização e outra.
- **O nome sempre denuncia o que ele faz**: quando você criar seu próprio hook (como veremos em vários tópicos deste roteiro), comece o nome com `use` — é assim que tanto o React quanto quem lê seu código reconhece que aquela função segue essas regras.

Os exemplos deste roteiro usam os hooks `useState` e `useEffect` (ambos vêm prontos do React) o tempo todo, então vale revisar rapidamente o papel de cada um antes de seguir em frente.

**`useState`** guarda um valor que pode mudar ao longo do tempo e que, quando muda, faz o componente ser desenhado de novo na tela. Ele devolve dois itens: o valor atual e uma função para atualizá-lo.

```javascript
import { useState } from 'react';

const [contador, setContador] = useState(0); // 0 é o valor inicial

// mais tarde, em resposta a algum evento:
setContador(contador + 1);
```

Chamar `setContador` não muda `contador` na hora — ele agenda uma nova renderização do componente, e só nessa nova renderização é que `contador` vem com o valor atualizado.

**`useEffect`** roda um trecho de código em resposta a mudanças — geralmente para sincronizar o componente com algo de fora do React, como uma assinatura de GPS, uma chamada de rede ou a criação de uma tabela no banco. Ele recebe uma função e uma lista de dependências: o efeito roda de novo sempre que algum valor dessa lista muda.

```javascript
import { useEffect, useState } from 'react';

function TelaExemplo({ usuarioId }) {
  const [usuario, setUsuario] = useState(null);

  useEffect(() => {
    // roda quando o componente aparece na tela, e de novo
    // toda vez que "usuarioId" mudar
    fetch(`https://api.exemplo.com/usuarios/${usuarioId}`)
      .then((resposta) => resposta.json())
      .then(setUsuario);
  }, [usuarioId]);

  // ...
}
```

Uma lista de dependências vazia (`[]`, como em vários exemplos deste roteiro) significa "rode só uma vez, quando o componente aparecer na tela pela primeira vez".

**Por que declarar com `const` sempre que possível:** ao longo de todo o roteiro, a maioria das variáveis é declarada com `const`, e não com `let` ou `var`. `const` impede que a variável seja reatribuída depois — se você tentar fazer `contador = 5` mais adiante no código (em vez de usar `setContador`), o JavaScript já aponta o erro na hora, em vez de deixar passar um bug para ser descoberto só depois, ao rodar o app. Isso também deixa o código mais fácil de ler: quem vê um `const` já sabe que aquele valor não muda depois de criado, sem precisar ler o resto da função para ter certeza. Reserve `let` só para os poucos casos em que a variável realmente precisa ser reatribuída (como o `i` de um `for`, ou o `soma` acumulado em `calculoPesado`, no Tópico 3).

---

### 1. Componentes e Navegação no Framework

**Objetivo:** revisar o que é um componente e como organizá-los bem, e revisar a navegação por arquivos do Expo Router, aprendendo a passar dados entre telas por meio de rotas dinâmicas.

**O que é um componente**

Um componente é apenas uma função JavaScript que devolve JSX (a descrição de um pedaço de interface) e que começa com letra maiúscula — é assim que o React distingue um componente (`<TelaProdutos />`) de um elemento HTML/nativo comum (`<view />` não existiria; é `<View />`, também com maiúscula, pelo mesmo motivo). Um componente recebe dados de fora por meio de **props** (o primeiro parâmetro da função, geralmente desestruturado) e pode guardar dados que mudam ao longo do tempo com **estado** (`useState`, já revisado no início deste roteiro).

```javascript
// um componente simples: recebe "nome" por prop, não guarda estado próprio
function Saudacao({ nome }) {
  return <Text>Olá, {nome}!</Text>;
}

// uso:
<Saudacao nome="Ana" />
```

**Quando extrair um novo componente**

Nem tudo precisa virar um componente separado, mas alguns sinais indicam que vale a pena extrair um pedaço de JSX para o seu próprio componente:
- **Está sendo repetido.** Se o mesmo bloco de JSX aparece em duas telas (ou duas vezes na mesma tela) com pequenas variações, ele é candidato a virar um componente parametrizado por props.
- **Tem uma responsabilidade própria e isolada.** Um cartão de produto, um item de lista, um cabeçalho — cada um pode evoluir (ganhar um botão, mudar de estilo) sem que isso afete o resto da tela, se estiver isolado em seu próprio componente.
- **A função está ficando difícil de ler.** Se o `return` de um componente já passa de umas 30-40 linhas de JSX aninhado, geralmente há um pedaço lógico dentro dele que merece seu próprio nome — o nome do componente extraído já documenta o que aquele pedaço faz.

Por exemplo, o item de uma lista de rotas planejadas — a mesma lista que vamos construir no Passo 2 a seguir — pode viver dentro da própria tela, ou ser extraído assim, se o cartão crescer (com distância estimada, ícone, última vez que foi percorrida):

```javascript
// components/CardRota.jsx
function CardRota({ rota, onPress }) {
  return (
    <TouchableOpacity onPress={onPress}>
      <Text>{rota.nome}</Text>
      <Text>{rota.distanciaEstimadaKm} km</Text>
    </TouchableOpacity>
  );
}
```

**Onde colocar cada componente**

Seguindo o que já vínhamos fazendo: componentes usados por **uma tela só** ficam no próprio arquivo daquela tela, dentro de `app/`; componentes reutilizados por **mais de uma tela** (como `CardRota` acima, ou `AvisoPermissaoNegada`, que construímos no Tópico 4) vão para `components/`, com um arquivo por componente, nomeado igual ao componente (`AvisoPermissaoNegada.jsx` exporta `AvisoPermissaoNegada`). É a mesma lógica de organização que já aplicamos a hooks (`hooks/`, `utils/`) desde o início deste roteiro.

**Passo 1 — o que é um layout, e o que é um Stack**

Como no 1º semestre, a navegação é feita com **Expo Router** — rotas de arquivo dentro de `app/`, sem configurar manualmente um `Stack.Navigator`. Um arquivo `_layout.jsx` não é uma tela: ele é o **contêiner** que envolve as telas de uma pasta, decidindo como o usuário se move entre elas (efeito visual da transição, se existe um menu, uma barra de abas, um botão de voltar, etc.). O `_layout.jsx` na raiz de `app/` vale para o app inteiro, a menos que uma subpasta tenha o seu próprio.

```javascript
// app/_layout.jsx
import { Stack } from 'expo-router';

export default function RootLayout() {
  return <Stack />;
}
```

`<Stack />` empilha as telas: cada `router.push` coloca uma tela nova por cima da anterior (como uma pilha de cartas), e o botão de voltar do celular — ou `router.back()` — desempilha, voltando para a tela de baixo. É o tipo de navegação mais comum para fluxos do tipo lista → detalhe, como o de lojas que vamos construir no Passo 2.

> 💡 **Dica:** se depois de salvar um arquivo o app parecer "preso" na versão antiga do código, o Fast Refresh (a atualização automática) nem sempre acompanha mudanças estruturais como um `_layout.jsx` novo. Dê um reload manual — agite o celular (ou `Cmd+D`/`Ctrl+M` no emulador) para abrir o menu de desenvolvedor e escolha "Reload", ou aperte `r` no terminal onde `npx expo start` está rodando. Se mesmo assim o problema persistir, `npx expo start -c` limpa o cache do Metro antes de subir o servidor de novo — e depois de instalar qualquer pacote novo (`npx expo install ...`), sempre reinicie o `npx expo start`, já que pacotes novos não aparecem para um servidor que já estava rodando.

**Outros tipos de layout**

Nem todo app é só uma pilha de telas. O Expo Router oferece outros dois layouts prontos, para os casos em que faz mais sentido o usuário escolher livremente para onde ir, em vez de seguir um fluxo de ida e volta:

- **`<Tabs />`** — uma barra de abas fixa (geralmente embaixo da tela), cada aba levando a uma seção independente do app. Útil quando existem duas ou mais áreas de mesma importância, sem uma ordem natural entre elas (por exemplo, "Corrida atual" e "Histórico").
- **`<Drawer />`** — um menu lateral que desliza a partir da borda da tela, útil quando existem muitas seções (mais do que caberia em uma barra de abas) ou quando algumas delas são usadas com menos frequência.

Um layout de abas para separar a corrida em andamento do histórico ficaria assim, dentro de `app/(tabs)/_layout.jsx` (o nome entre parênteses agrupa rotas sem aparecer na URL):

```javascript
// app/(tabs)/_layout.jsx
import { Tabs } from 'expo-router';

export default function TabsLayout() {
  return (
    <Tabs>
      <Tabs.Screen name="index" options={{ title: 'Corrida' }} />
      <Tabs.Screen name="historico" options={{ title: 'Histórico' }} />
    </Tabs>
  );
}
```

Repare que `Stack`, `Tabs` e `Drawer` não são excludentes — é comum um `Drawer` ou `Tabs` na raiz, e um `Stack` dentro de cada aba/seção, para que navegar para um detalhe dentro de uma aba continue empilhando telas normalmente ali dentro.

**Exercício:** transforme o layout raiz do seu app em `<Tabs />` com duas abas, e confirme que a navegação em pilha (`router.push` para uma tela de detalhe) continua funcionando dentro de cada aba.

**Passo 2 — uma lista que navega para o detalhe**

Vamos construir a tela inicial do app que fica no ar até o final deste roteiro: uma lista de rotas planejadas para correr. Cada arquivo dentro de `app/` já vira uma rota automaticamente. Para navegar entre eles, use `useRouter()` e `router.push`, passando os dados que a próxima tela vai precisar como parâmetros:

```javascript
// app/rotas/index.jsx
import { useRouter } from 'expo-router';
import { FlatList, Text, TouchableOpacity, View } from 'react-native';

const ROTAS_PLANEJADAS = [
  { id: '1', nome: 'Volta do parque', distanciaEstimadaKm: 3.2 },
  { id: '2', nome: 'Orla da praia', distanciaEstimadaKm: 5.8 },
];

export default function ListaDeRotas() {
  const router = useRouter();

  return (
    <FlatList
      data={ROTAS_PLANEJADAS}
      keyExtractor={(item) => item.id}
      renderItem={({ item }) => (
        <TouchableOpacity
          onPress={() =>
            router.push({
              pathname: '/rotas/[id]',
              params: { id: item.id, nome: item.nome, distanciaEstimadaKm: item.distanciaEstimadaKm },
            })
          }
        >
          <Text>{item.nome}</Text>
        </TouchableOpacity>
      )}
    />
  );
}
```

**Passo 3 — a rota dinâmica que recebe esses dados**

O nome do arquivo entre colchetes — `[id].jsx` — vira uma **rota dinâmica**. Dentro dela, `useLocalSearchParams()` lê tanto o segmento da URL (`id`) quanto os parâmetros extras enviados pelo `router.push` (`nome`, `distanciaEstimadaKm`). Um botão "Iniciar" leva à tela de rastreamento que vamos construir no Tópico 5:

```javascript
// app/rotas/[id].jsx
import { useLocalSearchParams, useRouter } from 'expo-router';
import { Button, StyleSheet, Text, View } from 'react-native';

export default function DetalhesRota() {
  const { id, nome, distanciaEstimadaKm } = useLocalSearchParams();
  const router = useRouter();

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>{nome || `Rota ${id}`}</Text>
      {distanciaEstimadaKm && <Text>{distanciaEstimadaKm} km estimados</Text>}
      <Button title="Iniciar corrida" onPress={() => router.push('/corrida')} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 8 },
  titulo: { fontSize: 20, fontWeight: '600' },
});
```

Teste tocando em um item da lista do Passo 2 — a tela de detalhe deve abrir já com o nome e a distância preenchidos, sem precisar buscar esses dados de novo. O botão "Iniciar corrida" ainda não leva a lugar nenhum de útil (`app/corrida.jsx` só existe a partir do Tópico 5) — por enquanto ele só confirma que a navegação por rota nomeada funciona.

**Exercícios:**
- **Fácil:** Adicione um botão "Voltar" na tela de detalhe usando `router.back()`.
- **Médio:** Adicione mais rotas planejadas ao array `ROTAS_PLANEJADAS`, incluindo uma imagem (`imagemUrl`) exibida no card da lista.
- **Desafio:** Em vez de passar `nome` e `distanciaEstimadaKm` como parâmetros, passe apenas o `id` e busque os dados completos da rota dentro de `[id].jsx` (com `fetch` a uma API própria, ou consultando o banco local) — compare as duas abordagens.

**Pergunta para fixação:** o que `useLocalSearchParams()` retornaria se a tela `[id].jsx` fosse aberta digitando a URL diretamente (sem vir de um `router.push` com `params`)?

---

### 2. Consumo de APIs no Framework

**Objetivo:** ir do padrão manual de `fetch` + `useState`/`useEffect` (já dominado) para uma biblioteca que cuida de cache e dos estados de carregando/erro automaticamente.

**Passo 1 — revisão do padrão já conhecido**

```javascript
// app/cep.jsx — revisão
async function consultarCep() {
  await fetch(`https://viacep.com.br/ws/${cep}/json/`)
    .then((resposta) => resposta.json())
    .then((dadoEmJson) => {
      if (dadoEmJson.erro) {
        setCep('CEP não existe');
      } else {
        setDados(dadoEmJson);
      }
    });
}
```

Esse padrão funciona bem para uma consulta pontual, disparada por um botão. O problema aparece quando a mesma informação precisa ser buscada de novo toda vez que algo muda (por exemplo, o clima atualizado conforme a localização do usuário muda) — repetir `useState` para dados/carregando/erro em cada tela começa a pesar.

> 💡 **Dica de depuração:** toda `Promise` rejeitada silenciosamente é um bug escondido. O exemplo acima não trata o caso de a rede falhar — sempre que usar `fetch` (ou, mais adiante, `runAsync` do banco local) sem `try/catch`, um erro desaparece sem avisar em vez de aparecer no console. Prefira sempre `try { await ... } catch (erro) { console.log(erro); }` enquanto estiver desenvolvendo, mesmo que o tratamento final do erro na interface venha depois. E lembre que `console.log` aparece tanto no terminal onde `npx expo start` está rodando quanto no menu de desenvolvedor do próprio app.

**Passo 2 — instale e importe o TanStack Query**

```bash
npx expo install @tanstack/react-query axios
```

```javascript
import { useQuery } from '@tanstack/react-query';
import axios from 'axios';
```

**Passo 3 — escreva a consulta como um hook**

```javascript
function useClimaAtual(latitude, longitude) {
  return useQuery({
    queryKey: ['clima', latitude, longitude],
    queryFn: async () => {
      const resposta = await axios.get('https://api.open-meteo.com/v1/forecast', {
        params: { latitude, longitude, current_weather: true },
      });
      return resposta.data.current_weather;
    },
    enabled: latitude != null && longitude != null, // só busca quando já tem coordenadas
  });
}
```

**Passo 4 — use o hook na tela**

```javascript
const { data: clima, isLoading, error } = useClimaAtual(pontos.at(-1)?.latitude, pontos.at(-1)?.longitude);
```

Repare que `isLoading` e `error` já vêm prontos — não precisamos declarar `useState` nenhum para eles, nem escrever `useEffect` para disparar a busca de novo quando `latitude`/`longitude` mudam.

**Exercícios:**
- **Fácil:** Exiba `isLoading` como um texto "Carregando clima..." enquanto a consulta não termina.
- **Médio:** Troque a tela de CEP do Passo 1 para usar `useQuery` em vez de `fetch` manual, comparando a quantidade de código necessária.
- **Desafio:** Use a opção `refetchInterval` do `useQuery` para atualizar o clima automaticamente a cada 60 segundos enquanto a tela estiver aberta.

---

### 3. Concorrência e Threads

**Objetivo:** entender, na prática, por que um cálculo pesado trava a interface — usando o próprio cálculo de distância percorrida que a tela de rastreamento do Tópico 5 vai precisar fazer — e três formas diferentes de evitar isso.

JavaScript roda em uma única **thread JS**. Tudo que trava essa thread — um loop pesado, um cálculo grande — trava a interface inteira, mesmo enquanto animações nativas (Reanimated) continuam rodando em sua própria thread, sem travar.

**Passo 1 — a distância entre dois pontos de GPS (fórmula de Haversine)**

Para somar a distância total de uma corrida, primeiro precisamos calcular a distância entre dois pontos de latitude/longitude consecutivos. A fórmula de Haversine faz isso considerando a curvatura da Terra:

```javascript
// utils/calculos.js
export function distanciaEntre(p1, p2) {
  const R = 6371000; // raio médio da Terra, em metros
  const rad = Math.PI / 180;
  const dLat = (p2.latitude - p1.latitude) * rad;
  const dLon = (p2.longitude - p1.longitude) * rad;
  const lat1 = p1.latitude * rad;
  const lat2 = p2.latitude * rad;

  const a = Math.sin(dLat / 2) ** 2 + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c; // em metros
}
```

**Passo 2 — somar a distância de um trajeto inteiro**

```javascript
// utils/calculos.js — adicione esta função
export function distanciaTotal(pontos) {
  let soma = 0;
  for (let i = 1; i < pontos.length; i++) {
    soma += distanciaEntre(pontos[i - 1], pontos[i]);
  }
  return soma;
}
```

Para um trajeto de corrida normal (algumas centenas ou milhares de pontos, um a cada poucos metros), isso roda instantaneamente. O problema aparece em outro cenário: imagine recalcular a distância total de **todo o histórico** de corridas salvas de um usuário assíduo, de uma vez, ao abrir a tela de estatísticas — potencialmente milhões de pontos somados. Vamos simular esse caso:

```javascript
// app/concorrencia.jsx — trecho relevante
import { distanciaTotal } from '../utils/calculos';

function gerarPontosDeTeste(quantidade) {
  const pontos = [];
  for (let i = 0; i < quantidade; i++) {
    pontos.push({ latitude: -23.55 + i * 0.00001, longitude: -46.63 + i * 0.00001 });
  }
  return pontos;
}

function rodarBloqueante() {
  const pontos = gerarPontosDeTeste(2_000_000);
  const metros = distanciaTotal(pontos);
  setResultado(metros);
}
```

Ligue esse `rodarBloqueante` a um botão e teste no celular: por alguns segundos, nada mais na tela responde — nem um simples toque em outro botão.

**Passo 3 — fatie o mesmo cálculo em lotes**

A ideia é processar só um pedaço do trajeto por vez e devolver o controle à thread JS entre um lote e outro, usando `setTimeout(fn, 0)`. Isso dá tempo da interface "respirar" entre os lotes.

```javascript
// utils/calculos.js — adicione esta função
export function distanciaTotalEmLotes(pontos, aoTerminar, tamanhoDoLote = 50_000) {
  let soma = 0;
  let i = 1;

  function proximoLote() {
    const fim = Math.min(i + tamanhoDoLote, pontos.length);
    for (; i < fim; i++) soma += distanciaEntre(pontos[i - 1], pontos[i]);

    if (i < pontos.length) {
      setTimeout(proximoLote, 0);
    } else {
      aoTerminar(soma);
    }
  }

  proximoLote();
}
```

```javascript
// app/concorrencia.jsx
import { distanciaTotalEmLotes } from '../utils/calculos';

function rodarEmLotes() {
  const pontos = gerarPontosDeTeste(2_000_000);
  distanciaTotalEmLotes(pontos, (metros) => setResultado(metros));
}
```

Ligue esse segundo botão e compare: o mesmo cálculo, mas agora a tela continua respondendo durante todo o processamento.

**Passo 4 — adiar trabalho até depois de uma transição de tela**

Quando o trabalho pesado precisa rodar logo após abrir uma tela, ele pode acabar disputando a thread JS com a própria animação de transição, fazendo-a engasgar. `InteractionManager` resolve isso — é o mesmo mecanismo que o React Navigation usa internamente durante suas próprias animações. Na tela de histórico (Tópico 7), por exemplo, faz sentido esperar a transição terminar antes de carregar e somar as corridas salvas:

```javascript
import { InteractionManager } from 'react-native';

function aoAbrirTela() {
  InteractionManager.runAfterInteractions(() => {
    carregarHistoricoDeCorridas();
  });
}
```

**Diferença entre as três formas:** `async/await` resolve I/O (rede, disco) sem travar a interface, mas continua na mesma thread JS; processamento pesado de CPU precisa ser fatiado em lotes (Passo 3), adiado com `InteractionManager` (Passo 4), ou movido para uma *worklet* do Reanimated, que roda em sua própria thread nativa e nunca trava, mesmo com a thread JS ocupada.

**Exercícios:**
- **Fácil:** Meça e mostre na tela quanto tempo cada versão (`rodarBloqueante` e `rodarEmLotes`) levou para terminar, usando `Date.now()` antes e depois.
- **Médio:** Na tela de rastreamento do Tópico 5, use `InteractionManager.runAfterInteractions` para só começar a calcular `distanciaTotal(pontos)` depois que a tela terminar de abrir/animar.
- **Desafio:** Experimente tamanhos de lote diferentes (`tamanhoDoLote`) em `distanciaTotalEmLotes` e observe o efeito: lotes grandes demais voltam a travar por um instante perceptível; lotes pequenos demais deixam o cálculo total mais lento (mais chamadas de `setTimeout`).

---

### 4. Permissões Avançadas

**Objetivo:** pedir acesso aos **contatos** para compartilhar um resultado com um amigo, tratando de forma amigável o caso em que a permissão é negada — em vez de o app simplesmente não fazer nada.

**Passo 1 — o import certo**

A partir do Expo SDK 56, o import padrão de `expo-contacts` (e de `expo-media-library`) passou a usar uma API nova baseada em classes. Para continuar usando `getContactsAsync`, como em aula, importe de `expo-contacts/legacy`:

```javascript
// app/compartilhar.jsx
import { useState } from 'react';
import { View, Button, Text, FlatList, StyleSheet } from 'react-native';
import * as Contacts from 'expo-contacts/legacy';
```

**Passo 2 — pedir a permissão e guardar o resultado**

O padrão é o mesmo já usado com `expo-location`: chamar `request<X>PermissionsAsync()` e checar o `status` retornado.

```javascript
export default function Compartilhar() {
  const [contatos, setContatos] = useState([]);
  const [status, setStatus] = useState(null); // null | 'granted' | 'denied' | 'undetermined'
  const [podePedirNovamente, setPodePedirNovamente] = useState(true);
  const [carregando, setCarregando] = useState(false);

  async function buscarContatos() {
    setCarregando(true);
    try {
      const permissao = await Contacts.requestPermissionsAsync();
      setStatus(permissao.status);
      setPodePedirNovamente(permissao.canAskAgain);

      if (permissao.status !== 'granted') return;
    } finally {
      setCarregando(false);
    }
  }

  // ... return vem no próximo passo
}
```

**Passo 3 — buscar os contatos, só se a permissão foi concedida**

Complete a função `buscarContatos`, adicionando a busca logo após o `if`:

```javascript
      if (permissao.status !== 'granted') return;

      const { data } = await Contacts.getContactsAsync({
        fields: [Contacts.Fields.PhoneNumbers], // só o campo necessário
      });
      setContatos(data.filter((c) => c.phoneNumbers?.length > 0));
```

**Passo 4 — um componente próprio para o aviso de permissão negada**

Em vez de repetir esse tratamento em cada tela que pede uma permissão, isole-o em um componente. A diferença importante é o `canAskAgain`: se for `true`, oferecemos "tentar de novo"; se for `false` (o usuário já negou de forma permanente, por exemplo marcando "não perguntar novamente" no Android), só resta abrir as Configurações do sistema.

```javascript
// components/AvisoPermissaoNegada.jsx
import { View, Text, Button, StyleSheet } from 'react-native';
import * as Linking from 'expo-linking';

export default function AvisoPermissaoNegada({ recurso, podePedirNovamente = true, aoTentarNovamente }) {
  return (
    <View style={styles.caixa}>
      <Text>Sem acesso {recurso}. Ative nas configurações para usar este recurso.</Text>
      {podePedirNovamente ? (
        <Button title="Tentar novamente" onPress={aoTentarNovamente} />
      ) : (
        <Button title="Abrir Configurações" onPress={() => Linking.openSettings()} />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  caixa: { backgroundColor: '#fff3e0', borderRadius: 8, padding: 12, gap: 8 },
});
```

**Passo 5 — montar a tela final**

```javascript
  return (
    <View style={styles.container}>
      <Button
        title={carregando ? 'Carregando...' : 'Carregar contatos'}
        onPress={buscarContatos}
        disabled={carregando}
      />

      {status && status !== 'granted' && (
        <AvisoPermissaoNegada
          recurso="aos contatos"
          podePedirNovamente={podePedirNovamente}
          aoTentarNovamente={buscarContatos}
        />
      )}

      <FlatList
        data={contatos}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => <Text style={styles.item}>{item.name}</Text>}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12 },
  item: { paddingVertical: 4 },
});
```

Não esqueça de importar `AvisoPermissaoNegada` no topo do arquivo, junto dos outros imports do Passo 1.

**Exercícios:**
- **Fácil:** Negue a permissão de propósito (nas configurações do app) e confira se o aviso aparece corretamente.
- **Médio:** Reutilize `AvisoPermissaoNegada` na tela de localização do Tópico 5, trocando apenas o texto de `recurso`.
- **Desafio:** Peça também permissão de câmera (`expo-camera`) na mesma tela, mostrando dois avisos independentes conforme cada permissão for negada.

**Pergunta para fixação:** por que pedimos só `Contacts.Fields.PhoneNumbers` em vez de todos os campos disponíveis?

---

### 5. Localização e Mapas

**Objetivo:** sair da leitura única de posição (já vista no 1º semestre) para um **rastreamento contínuo**, exibido em tempo real em um mapa que não recarrega a cada atualização.

**Passo 1 — o hook de rastreamento, só com os estados**

Comece criando o arquivo `hooks/useTracking.js` apenas com o que ele vai guardar: a lista de pontos percorridos, se está rastreando, e um possível erro.

```javascript
// hooks/useTracking.js
import { useRef, useState } from 'react';
import * as Location from 'expo-location';

export function useTracking() {
  const [pontos, setPontos] = useState([]);
  const [rastreando, setRastreando] = useState(false);
  const [erro, setErro] = useState(null);
  const subscriptionRef = useRef(null); // vai guardar a assinatura do GPS, para poder cancelar depois
}
```

**Passo 2 — pedir permissão e começar a assinar posições**

Agora adicione a função `iniciar`. A diferença para o que já vimos no 1º semestre é o uso de `watchPositionAsync` em vez de `getCurrentPositionAsync`: em vez de ler a posição uma única vez, ele chama a função passada a cada nova posição, até que você cancele a assinatura.

```javascript
  async function iniciar() {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== 'granted') {
      setErro('Permissão de localização negada. Ative-a nas configurações do app para rastrear.');
      return;
    }

    setErro(null);
    setPontos([]);
    setRastreando(true);

    subscriptionRef.current = await Location.watchPositionAsync(
      {
        accuracy: Location.Accuracy.High,
        timeInterval: 3000,   // a cada 3 segundos
        distanceInterval: 5,  // ou a cada 5 metros percorridos
      },
      (posicao) => {
        setPontos((anteriores) => [...anteriores, posicao.coords]);
      }
    );
  }
```

**Passo 3 — parar o rastreamento e expor tudo do hook**

Sem cancelar a assinatura, o GPS continuaria sendo lido mesmo depois que o usuário saísse da tela. Feche o hook assim:

```javascript
  function parar() {
    subscriptionRef.current?.remove();
    subscriptionRef.current = null;
    setRastreando(false);
  }

  return { pontos, rastreando, erro, iniciar, parar };
}
```

Com isso, `useTracking()` já é utilizável de qualquer tela — mas ainda não temos onde exibir o trajeto. Vamos ao mapa.

> 💡 **Dica:** teste esse hook em um celular físico, não só no emulador. Emuladores/simuladores normalmente não têm um GPS real — alguns simulam uma posição fixa ou zerada, o que faz um código correto parecer "quebrado" quando na verdade só está recebendo sempre a mesma leitura.

**Passo 4 — o HTML do mapa, como uma função separada**

O mapa em si é uma página HTML com Leaflet, carregada dentro de uma `WebView`. Colocá-lo em sua própria função facilita reutilizar em outras telas depois. Repare que não estamos usando `react-native-maps`: essa biblioteca exige uma chave do Google Cloud e, por depender de código nativo próprio, **não funciona no Expo Go** (só em um *development build*) — daí a escolha por `WebView` + Leaflet, que roda em qualquer lugar sem configuração extra. Crie `utils/mapaHtml.js`:

```javascript
// utils/mapaHtml.js
export function getMapaCorridaHtml(latitudeInicial, longitudeInicial) {
  return `
<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
  <style>
    body { margin: 0; padding: 0; }
    #map { height: 100vh; width: 100vw; }
  </style>
</head>
<body>
  <div id="map"></div>
  <script>
    var map = L.map('map').setView([${latitudeInicial}, ${longitudeInicial}], 16);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    var rota = L.polyline([], { color: '#2196F3', weight: 4 }).addTo(map);
    var marcador = null;

    window.addPonto = function (lat, lon) {
      var ponto = [lat, lon];
      rota.addLatLng(ponto);
      if (marcador) {
        marcador.setLatLng(ponto);
      } else {
        marcador = L.marker(ponto, { title: 'Posição atual' }).addTo(map);
      }
      map.panTo(ponto);
    };

    window.ReactNativeWebView.postMessage('pronto');
  </script>
</body>
</html>
`;
}
```

Repare no final do script: assim que o Leaflet termina de montar o mapa, ele avisa o React Native com `postMessage('pronto')`. Vamos usar esse aviso no próximo passo.

> 💡 **Dica de depuração:** o código dentro desse `<script>` roda em um mundo totalmente separado do React Native — um `console.log` colocado ali dentro não aparece no terminal do Metro nem no menu de desenvolvedor do app. `window.ReactNativeWebView.postMessage(...)` é a única ponte de volta para o lado React (é assim que descobrimos, no passo seguinte, quando o mapa termina de carregar).

**Passo 5 — o componente do mapa, carregando o HTML uma única vez**

```javascript
// components/RaceMap.jsx
import { useEffect, useRef, useState } from 'react';
import { StyleSheet } from 'react-native';
import { WebView } from 'react-native-webview';
import { getMapaCorridaHtml } from '../utils/mapaHtml';

const REGIAO_INICIAL = { latitude: -23.5505, longitude: -46.6333 };

export function RaceMap({ pontos }) {
  const webviewRef = useRef(null);
  const [mapaPronto, setMapaPronto] = useState(false);

  // useState com função: getMapaCorridaHtml só roda na primeira renderização,
  // não a cada re-render — senão o HTML seria recriado (e a WebView recarregada)
  // toda vez que "pontos" mudasse.
  const [htmlInicial] = useState(() =>
    getMapaCorridaHtml(REGIAO_INICIAL.latitude, REGIAO_INICIAL.longitude)
  );

  return (
    <WebView
      ref={webviewRef}
      originWhitelist={['*']}
      source={{ html: htmlInicial }}
      style={styles.map}
      onMessage={(evento) => {
        if (evento.nativeEvent.data === 'pronto') setMapaPronto(true);
      }}
    />
  );
}

const styles = StyleSheet.create({
  map: { flex: 1 },
});
```

Nesse ponto o mapa já aparece na tela, mas ainda não recebe os pontos do rastreamento.

**Passo 6 — atualizar o trajeto sem recarregar a página**

Recarregar a `WebView` a cada ponto novo apagaria a linha desenhada até então. Em vez disso, injetamos JavaScript no mapa já carregado, chamando o `window.addPonto` que definimos no Passo 4. Para isso, primeiro adicione mais uma ref no topo do componente, junto da `webviewRef` já existente — ela vai lembrar quantos pontos já foram enviados ao mapa:

```javascript
  const pontosEnviadosRef = useRef(0);
```

Agora adicione este efeito logo abaixo, antes do `return` do componente:

```javascript
  useEffect(() => {
    const webview = webviewRef.current;
    if (!mapaPronto || !webview) return;

    // Só os pontos que ainda não foram enviados — reenviar tudo a cada
    // ponto novo ficaria cada vez mais lento conforme a corrida avança.
    for (let i = pontosEnviadosRef.current; i < pontos.length; i++) {
      const { latitude, longitude } = pontos[i];
      webview.injectJavaScript(
        `window.addPonto && window.addPonto(${latitude}, ${longitude}); true;`
      );
    }
    pontosEnviadosRef.current = pontos.length;
  }, [pontos, mapaPronto]);
```

**Passo 7 — juntar tudo em uma tela**

```javascript
// app/corrida.jsx
import { View, Button, Text } from 'react-native';
import { useTracking } from '../hooks/useTracking';
import { RaceMap } from '../components/RaceMap';

export default function Corrida() {
  const { pontos, rastreando, erro, iniciar, parar } = useTracking();

  return (
    <View style={{ flex: 1 }}>
      <RaceMap pontos={pontos} />
      <Button title={rastreando ? 'Parar' : 'Iniciar'} onPress={rastreando ? parar : iniciar} />
      {erro && <Text>{erro}</Text>}
    </View>
  );
}
```

**Exercícios:**
- **Fácil:** Mostre na tela quantos pontos já foram registrados (`pontos.length`).
- **Médio:** Importe `distanciaTotal` de `utils/calculos.js` (construída no Tópico 3) para somar a distância total do trajeto em `pontos` e exibi-la ao lado do botão.
- **Desafio:** Adicione um botão "Reiniciar" que limpa `pontos` e também limpa a linha desenhada no mapa (dica: envie um `window.resetRota()` parecido com o `window.addPonto`, apagando `rota` e `marcador` dentro do HTML do Passo 4).

**Perguntas para fixação:**
1. Por que `getMapaCorridaHtml` é chamado dentro de `useState(() => ...)`, e não direto como `useState(getMapaCorridaHtml(...))`?
2. O que aconteceria com o desenho do trajeto se, em vez de `injectJavaScript`, recarregássemos a `WebView` a cada novo ponto?

---

### 6. Acesso a Recursos Nativos via Framework

**Objetivo:** além de câmera/galeria (já vistas), aprender a ler **sensores de movimento** do aparelho.

`expo-sensors` expõe o acelerômetro, giroscópio e magnetômetro por assinatura — o mesmo padrão de `addListener`/`remove` que vocês já usaram em outros contextos.

**Passo 1 — verifique se o sensor existe antes de assinar**

Nem todo aparelho tem acelerômetro (principalmente emuladores). Sempre confira com `isAvailableAsync` antes de assinar:

> 💡 **Dica:** assim como o GPS (Tópico 5), sensores de movimento costumam vir zerados ou simulados em emuladores — teste sempre em um celular físico antes de concluir que o código está com bug.

```javascript
// utils/useAcelerometro.js
import { Accelerometer } from 'expo-sensors';
import { useEffect, useState } from 'react';

export function useAcelerometro(intervaloMs = 200) {
  const [dados, setDados] = useState({ x: 0, y: 0, z: 0 });
  const [disponivel, setDisponivel] = useState(false);

  useEffect(() => {
    let cancelado = false;

    Accelerometer.isAvailableAsync().then((temSensor) => {
      if (cancelado) return;
      setDisponivel(temSensor);
      // próximo passo: assinar, se existir
    });

    return () => { cancelado = true; };
  }, [intervaloMs]);

  return { ...dados, disponivel };
}
```

**Passo 2 — assine as leituras, e cancele a assinatura ao desmontar**

Complete o `then`, e guarde a assinatura em uma variável para poder removê-la quando o componente sair da tela (senão o sensor continuaria sendo lido em segundo plano, gastando bateria):

```javascript
    let subscription;

    Accelerometer.isAvailableAsync().then((temSensor) => {
      if (cancelado) return;
      setDisponivel(temSensor);
      if (!temSensor) return;

      Accelerometer.setUpdateInterval(intervaloMs);
      subscription = Accelerometer.addListener(setDados);
    });

    return () => {
      cancelado = true;
      subscription?.remove();
    };
```

O hook completo (Passos 1 + 2) fica assim:

```javascript
// utils/useAcelerometro.js
import { Accelerometer } from 'expo-sensors';
import { useEffect, useState } from 'react';

export function useAcelerometro(intervaloMs = 200) {
  const [dados, setDados] = useState({ x: 0, y: 0, z: 0 });
  const [disponivel, setDisponivel] = useState(false);

  useEffect(() => {
    let subscription;
    let cancelado = false;

    Accelerometer.isAvailableAsync().then((temSensor) => {
      if (cancelado) return;
      setDisponivel(temSensor);
      if (!temSensor) return;

      Accelerometer.setUpdateInterval(intervaloMs);
      subscription = Accelerometer.addListener(setDados);
    });

    return () => {
      cancelado = true;
      subscription?.remove();
    };
  }, [intervaloMs]);

  return { ...dados, disponivel };
}
```

**Passo 3 — construa um detector de gesto em cima do hook**

Com `x`, `y`, `z` em mãos, dá para calcular a força total do movimento (norma do vetor) e disparar uma ação quando ela ultrapassa um limiar — por exemplo, detectar que o usuário chacoalhou o celular:

```javascript
// utils/useDetectorDeChacoalhada.js
import { useEffect, useRef } from 'react';
import { useAcelerometro } from './useAcelerometro';

export function useDetectorDeChacoalhada(aoChacoalhar, limiar = 2.5) {
  const { x, y, z } = useAcelerometro();
  const ultimaChacoalhada = useRef(0);

  useEffect(() => {
    const forca = Math.sqrt(x * x + y * y + z * z);
    const agora = Date.now();

    // exige pelo menos 1 segundo entre uma detecção e outra, para não
    // disparar várias vezes seguidas com um único movimento
    if (forca > limiar && agora - ultimaChacoalhada.current > 1000) {
      ultimaChacoalhada.current = agora;
      aoChacoalhar();
    }
  }, [x, y, z, limiar]);
}
```

**Exercícios:**
- **Fácil:** Mostre `x`, `y`, `z` na tela em tempo real, arredondados para 2 casas decimais.
- **Médio:** Use `useDetectorDeChacoalhada` na tela de rastreamento (Tópico 5) para que chacoalhar o celular durante a corrida marque um "ponto de interesse" no trajeto.
- **Desafio:** Ajuste o `limiar` experimentalmente até encontrar um valor que detecte um chacoalhar intencional, mas ignore o balanço normal do celular no bolso durante a corrida.

---

### 7. Armazenamento Local no Framework

**Objetivo:** persistir os dados da corrida localmente, revisando o padrão de CRUD com `expo-sqlite` já usado em aula.

**Passo 1 — abrir o banco e criar a tabela**

Como no CRUD do 1º semestre, `expo-sqlite` abre o banco de forma síncrona (`openDatabaseSync`), e a criação da tabela também roda síncrona (`execSync`) — as próximas operações dependem da tabela já existir, então não faz sentido continuar antes disso terminar.

```javascript
// app/historico.jsx
import * as SQLite from 'expo-sqlite';
import { useEffect, useState } from 'react';

export default function Historico() {
  const db = SQLite.openDatabaseSync('corridas.db');
  const [corridas, setCorridas] = useState([]);

  useEffect(() => {
    db.execSync(
      'CREATE TABLE IF NOT EXISTS corridas (id INTEGER PRIMARY KEY AUTOINCREMENT, distancia REAL, duracao INTEGER, data TEXT)'
    );
  }, []);
}
```

**Passo 2 — salvar uma corrida**

Diferente da criação da tabela, inserir e ler linhas usa as versões assíncronas (`runAsync`/`getAllAsync`):

```javascript
  function salvarCorrida(distancia, duracao) {
    db.runAsync(
      'INSERT INTO corridas (distancia, duracao, data) VALUES (?, ?, ?)',
      [distancia, duracao, new Date().toISOString()]
    ).then(carregarCorridas);
  }
```

**Passo 3 — carregar e listar as corridas salvas**

```javascript
  function carregarCorridas() {
    db.getAllAsync('SELECT * FROM corridas ORDER BY data DESC;').then(setCorridas);
  }
```

Chame `carregarCorridas()` também dentro do `useEffect` do Passo 1, logo após criar a tabela, para que a lista já venha carregada assim que a tela abrir.

**Passo 4 — preferências simples, sem precisar de SQL**

Para um dado isolado (por exemplo, a unidade de distância preferida — km ou mi), `AsyncStorage` é mais direto do que criar uma tabela inteira:

```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';

async function salvarUnidade(unidade) {
  await AsyncStorage.setItem('unidade', unidade);
}

async function lerUnidade() {
  return (await AsyncStorage.getItem('unidade')) ?? 'km';
}
```

**Exercícios:**
- **Fácil:** Liste as corridas salvas em uma `FlatList`, mostrando data e distância de cada uma.
- **Médio:** Ao terminar uma corrida na tela do Tópico 5, chame `salvarCorrida` com a distância total e a duração, e confira se ela aparece em `app/historico.jsx`.
- **Desafio:** Adicione um botão para apagar uma corrida do histórico (`DELETE FROM corridas WHERE id = ?`), atualizando a lista depois.

---

### 8. Notificações

**Objetivo:** avisar o usuário mesmo com o app fechado — por exemplo, quando uma corrida planejada está prestes a começar — entendendo a diferença entre uma notificação local e uma notificação push, e por que uma delas tem uma limitação importante no Expo Go.

**Passo 1 — pedir permissão**

Assim como câmera e localização, notificações exigem permissão do usuário antes de aparecerem:

```javascript
import * as Notifications from 'expo-notifications';

async function pedirPermissaoDeNotificacao() {
  const { status } = await Notifications.requestPermissionsAsync();
  return status === 'granted';
}
```

**Passo 2 — decidir como uma notificação recebida deve se comportar**

Configure isso uma vez, fora de qualquer componente (por exemplo, no topo de `app/_layout.jsx`), para dizer ao app o que fazer quando uma notificação chega com o app aberto:

```javascript
Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});
```

**Passo 3 — uma notificação local, para testar o fluxo inteiro**

Uma notificação **local** é agendada e disparada pelo próprio aparelho, sem depender de nenhum servidor — é o tipo mais simples de testar, e funciona normalmente no Expo Go:

```javascript
async function agendarLembreteDeCorrida(segundosAtePartida) {
  await Notifications.scheduleNotificationAsync({
    content: {
      title: 'Hora de correr!',
      body: 'Sua corrida planejada está começando agora.',
    },
    trigger: { seconds: segundosAtePartida },
  });
}
```

Teste chamando essa função com `segundosAtePartida = 5` e deixando o app em segundo plano — a notificação deve aparecer normalmente, mesmo rodando pelo Expo Go.

**Passo 4 — a limitação: notificação push no Expo Go**

Uma notificação **push** é diferente: ela é enviada de um servidor (o seu backend, ou o próprio servidor da Expo) para o aparelho, mesmo que o app esteja completamente fechado. É o tipo usado, por exemplo, para avisar "seu amigo compartilhou uma corrida com você" a qualquer momento.

Aqui está a limitação importante: **a partir do SDK 53, notificações push não funcionam mais no Expo Go no Android** (no iOS, o Expo Go ainda suporta). A própria Expo explica o motivo: não é possível configurar automaticamente as credenciais de push (chaves do Firebase/APNs) dentro do ambiente compartilhado e genérico do Expo Go — cada projeto precisa das suas próprias credenciais, geradas para aquele app específico.

**Como resolver:** gerar um *development build* do seu próprio app (em vez de usar o Expo Go genérico) com `eas build --profile development`. Esse build já sai configurado com as credenciais de push do seu projeto, e a partir dele notificações push funcionam normalmente em qualquer plataforma.

**Passo 5 — obter o token de push (já pensando no development build)**

Para uma notificação push chegar até um aparelho específico, o servidor precisa de um "endereço" daquele aparelho — o **token de push**. Obtê-lo exige o `projectId` do seu projeto EAS:

```javascript
import Constants from 'expo-constants';

async function obterTokenDePush() {
  const projectId = Constants?.expoConfig?.extra?.eas?.projectId;
  if (!projectId) {
    throw new Error('projectId não encontrado — configure o EAS no projeto primeiro.');
  }

  const { data: token } = await Notifications.getExpoPushTokenAsync({ projectId });
  return token; // envie este token para o seu backend guardar
}
```

**Exercícios:**
- **Fácil:** Peça a permissão de notificação na abertura do app e mostre na tela se foi concedida.
- **Médio:** Use `agendarLembreteDeCorrida` para lembrar o usuário 10 segundos depois de ele tocar em "Iniciar" na tela de rastreamento (Tópico 5), como se fosse um aviso de "corrida em andamento".
- **Desafio:** Gere um development build (`eas build --profile development`) e obtenha um token de push de verdade com `obterTokenDePush`, confirmando que ele só funciona a partir desse build — não do Expo Go comum no Android.

**Perguntas para fixação:**
1. Por que uma notificação local funciona no Expo Go, mas uma notificação push (no Android) não?
2. No Android, o que precisa existir antes de `getExpoPushTokenAsync` poder ser chamado com sucesso (dica: revise o Tópico 4 sobre permissões, e pense no que acontece na primeira vez que o Android pede autorização para notificações)?

---

### 9. Gerenciamento de Estado (Context API, Redux)

**Objetivo:** compartilhar o estado da corrida entre telas diferentes (mapa, histórico, resumo) sem precisar repassar props manualmente por vários níveis de componentes.

No 1º semestre, a Context API já apareceu para tema/idioma do app — um contexto envolvendo toda a árvore de telas. Agora aplicamos a mesma ideia a um estado de negócio: o rastreamento da corrida.

**O problema: prop drilling**

Sem Context API, a única forma de um componente bem lá no fundo da árvore receber um dado é passando esse dado como prop por **todo componente intermediário**, mesmo que esses intermediários não usem o dado para nada além de repassá-lo adiante. Isso se chama *prop drilling* ("perfurar" a árvore de componentes com uma prop até o destino):

```javascript
// TelaCorrida não usa "pontos" diretamente, só repassa
function TelaCorrida({ pontos }) {
  return <PainelCorrida pontos={pontos} />;
}

// PainelCorrida também não usa "pontos", só repassa de novo
function PainelCorrida({ pontos }) {
  return <ResumoDoTrajeto pontos={pontos} />;
}

// só aqui, três níveis abaixo, "pontos" é realmente usado
function ResumoDoTrajeto({ pontos }) {
  return <Text>{pontos.length} pontos registrados</Text>;
}
```

O problema cresce junto com o app: toda vez que `ResumoDoTrajeto` precisar de mais um dado (por exemplo, a duração da corrida), é preciso adicionar mais uma prop e repassá-la por `TelaCorrida` e `PainelCorrida` de novo — mesmo eles continuando sem usar esse dado para nada. Renomear ou reorganizar um componente no meio do caminho quebra a cadeia inteira.

**A solução: Context API**

Um contexto cria um "atalho": qualquer componente dentro dele pode ler o valor diretamente, sem depender dos componentes intermediários repassando prop nenhuma.

```javascript
// TelaCorrida nem precisa mais saber que "pontos" existe
function TelaCorrida() {
  return <PainelCorrida />;
}

function PainelCorrida() {
  return <ResumoDoTrajeto />;
}

// ResumoDoTrajeto lê o contexto direto, sem passar por ninguém no meio
function ResumoDoTrajeto() {
  const { state } = useCorrida();
  return <Text>{state.pontos.length} pontos registrados</Text>;
}
```

Isso não significa que Context API deva substituir todo uso de props — para um dado usado por só um ou dois níveis, passar por prop continua mais simples e mais fácil de rastrear. Context vale a pena quando o mesmo dado precisa alcançar vários componentes espalhados pela árvore, distantes de quem o possui. Vamos construir esse contexto (`useCorrida`, usado no exemplo acima) nos próximos passos.

**Passo 1 — o reducer**

Assim como fizemos com o cálculo pesado, comece pela peça mais simples: uma função pura que recebe o estado atual e uma ação, e devolve o novo estado.

```javascript
function corridaReducer(state, action) {
  switch (action.type) {
    case 'adicionarPonto':
      return { ...state, pontos: [...state.pontos, action.ponto] };
    case 'reiniciar':
      return { pontos: [] };
    default:
      return state;
  }
}
```

**Passo 2 — o contexto e o provider**

```javascript
import { createContext, useContext, useReducer } from 'react';

const CorridaContext = createContext(null);

export function CorridaProvider({ children }) {
  const [state, dispatch] = useReducer(corridaReducer, { pontos: [] });
  return (
    <CorridaContext.Provider value={{ state, dispatch }}>
      {children}
    </CorridaContext.Provider>
  );
}
```

**Passo 3 — um hook para facilitar o uso**

Em vez de cada tela importar `useContext(CorridaContext)` diretamente, um pequeno hook próprio deixa o uso mais limpo e esconde o detalhe de qual contexto está por trás:

```javascript
export function useCorrida() {
  return useContext(CorridaContext);
}
```

Com isso, qualquer tela dentro de `<CorridaProvider>` pode fazer `const { state, dispatch } = useCorrida()` e ler ou alterar os pontos da corrida, sem precisar receber essa informação via props.

**Passo 4 — quando migrar para Redux**

Context API resolve bem esse caso. Se o app crescer — várias telas, vários tipos de eventos alterando o mesmo estado, necessidade de depurar cada mudança — o **Redux Toolkit** organiza tudo isso em um único lugar auditável, com ferramentas de depuração (Redux DevTools):

```javascript
import { createSlice, configureStore } from '@reduxjs/toolkit';

const corridaSlice = createSlice({
  name: 'corrida',
  initialState: { pontos: [] },
  reducers: {
    adicionarPonto: (state, action) => { state.pontos.push(action.payload); },
    reiniciar: (state) => { state.pontos = []; },
  },
});

const store = configureStore({ reducer: { corrida: corridaSlice.reducer } });
```

Note a semelhança com o reducer do Passo 1 — a diferença é que o Redux Toolkit permite escrever `state.pontos.push(...)` como se fosse uma mutação direta (por baixo dos panos, a biblioteca Immer cuida de gerar um novo estado imutável a partir disso).

**Exercícios:**
- **Fácil:** Envolva `app/_layout.jsx` com `<CorridaProvider>`, para que o contexto fique disponível em todas as telas.
- **Médio:** Substitua o `useState` local de pontos da tela de rastreamento (Tópico 5) por `CorridaProvider`/`useCorrida`, de forma que a tela de histórico também possa exibir a corrida em andamento.
- **Desafio:** Reimplemente o mesmo estado com Redux Toolkit e compare a quantidade de código contra a versão com Context API — em que ponto um deles começaria a compensar mais que o outro?

---

### 10. Publicação de Apps (builds, lojas)

**Objetivo:** sair do Expo Go e gerar um build de verdade, pronto para as lojas.

Com Expo, o build e a publicação são feitos via **EAS (Expo Application Services)**. Siga os passos na ordem:

**Passo 1 — identificação do app**

Configure ícone, nome e identificador único em `app.json` (`android.package`/`ios.bundleIdentifier`). Esse identificador não pode mudar depois que o app for publicado pela primeira vez.

**Passo 2 — perfis de build**

Configure o `eas.json` com os perfis de build (`development`, `preview`, `production`) — cada um pode ter configurações diferentes (por exemplo, `development` inclui ferramentas de debug que não devem ir para produção).

**Passo 3 — gerar o build**

```bash
eas build --platform android --profile production
eas build --platform ios --profile production
```

**Passo 4 — versionamento**

Configure o campo `version` em `app.json`, incrementado a cada novo envio — as lojas rejeitam um envio com a mesma versão de um já publicado.

**Passo 5 — enviar para a loja**

```bash
eas submit --platform android
eas submit --platform ios
```

**Passo 6 — ficha da loja**

Preencha descrição, screenshots e política de privacidade, e envie para revisão.

**Boas práticas:** nunca commitar credenciais de assinatura no repositório (o EAS pode gerenciá-las de forma segura na nuvem); usar `eas.json` com perfis diferentes para desenvolvimento e produção; sempre testar um build `preview` antes de enviar para a loja.

**Exercícios:**
- **Fácil:** Gere um build de preview do seu app com `eas build --profile preview`, documentando cada etapa (ícone, versão, perfil) em um checklist.
- **Médio:** Instale o build de preview gerado em um celular físico (via QR code do EAS) e confira se tudo funciona fora do Expo Go — preste atenção especial em `expo-contacts/legacy` e outros módulos nativos.

---

### Visão geral — o app de corrida completo

Repare que nenhum tópico deste roteiro ficou isolado: cada um acrescentou uma peça ao mesmo aplicativo de corrida, começando pela lista de rotas planejadas no Tópico 1 e terminando no build publicável do Tópico 10. Juntando tudo o que foi construído passo a passo, a estrutura final de pastas fica assim:

```
app/
  _layout.jsx                     — Stack raiz (Tópico 1); é aqui que entram o
                                     CorridaProvider (Tópico 9) e o
                                     setNotificationHandler (Tópico 8)
  (tabs)/_layout.jsx              — alternativa em abas para o layout raiz (Tópico 1, opcional)
  rotas/
    index.jsx                     — lista de rotas planejadas (Tópico 1)
    [id].jsx                      — detalhe da rota, com botão "Iniciar corrida" (Tópico 1)
  corrida.jsx                     — tela de rastreamento em tempo real, com mapa (Tópico 5)
  historico.jsx                   — corridas salvas, lidas do SQLite (Tópico 7)
  compartilhar.jsx                — compartilhar um resultado com os contatos (Tópico 4)
  concorrencia.jsx                — comparação bloqueante x em lotes x InteractionManager (Tópico 3)

components/
  CardRota.jsx                    — item da lista de rotas (Tópico 1)
  RaceMap.jsx                     — WebView + Leaflet exibindo o trajeto (Tópico 5)
  AvisoPermissaoNegada.jsx        — aviso reutilizável de permissão negada (Tópico 4)

context/
  CorridaContext.jsx              — CorridaProvider/useCorrida, ou o equivalente em Redux Toolkit (Tópico 9)

hooks/
  useTracking.js                  — assina o GPS e devolve os pontos do trajeto (Tópico 5)
  useClimaAtual.js                — clima na posição atual, via TanStack Query (Tópico 2)

utils/
  calculos.js                     — distanciaEntre/distanciaTotal (Haversine) e as versões
                                     bloqueante/em lotes (Tópico 3)
  mapaHtml.js                     — HTML do mapa Leaflet injetado na WebView (Tópico 5)
  useAcelerometro.js              — leitura do acelerômetro (Tópico 6)
  useDetectorDeChacoalhada.js     — detecta chacoalhar o celular durante a corrida (Tópico 6)
  preferencias.js                 — unidade de distância preferida, via AsyncStorage (Tópico 7)
```

**Como as peças se encaixam:** o usuário abre o app e vê a lista de rotas planejadas (Tópico 1); ao tocar em uma, cai na tela de detalhe e, de lá, em "Iniciar corrida", chega à tela de rastreamento (Tópico 5), que desenha o trajeto ao vivo no mapa enquanto `useTracking` assina o GPS. Os mesmos `pontos` do rastreamento alimentam `distanciaTotal` (Tópico 3, cuidando para não travar a interface) e servem de coordenada para consultar o clima atual (Tópico 2). Chacoalhar o celular durante a corrida marca um ponto de interesse no trajeto (Tópico 6). Ao terminar, o resultado pode ser compartilhado com um contato (Tópico 4), fica salvo no histórico local (Tópico 7) e pode disparar um lembrete para a próxima corrida (Tópico 8). O estado da corrida em andamento — em vez de ser repassado por prop de tela em tela — fica acessível de qualquer lugar via `useCorrida()` (Tópico 9). E, para sair do Expo Go e colocar esse app nas mãos de usuários de verdade, o Tópico 10 fecha o roteiro com o processo de build e publicação.

Nenhum tópico precisa ser "encaixado" à força depois: a essa altura, seguir os passos de cada um, na ordem em que aparecem, já constrói o app inteiro.
