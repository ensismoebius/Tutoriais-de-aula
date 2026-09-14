# Tutorial — Programação para Aplicativos Móveis II (PAMII)

Este material ensina React Native com Expo, sempre em JavaScript, do zero até um aplicativo completo de corrida — funcional, publicável, e construído inteiramente ao longo deste tutorial. Não é preciso ter visto nada de React Native antes: os fundamentos de JavaScript e React necessários aparecem logo nos primeiros tópicos.

A ideia central é que você veja resultado rápido: já no início, com o projeto recém-criado, vamos colocar algo na tela em poucos minutos, e cada tópico novo termina com um pedaço a mais do mesmo app rodando de verdade no celular — nunca só teoria sem nada para testar. Cada tópico é organizado em **passos** — construa o código na ordem apresentada, um pedaço de cada vez, testando no celular ou no emulador a cada passo, em vez de copiar o arquivo inteiro de uma vez só.

> 💡 **Dica de organização:** nunca commite as pastas `node_modules/` e `.expo/` no Git — ambas são geradas automaticamente (a primeira por `npm install`, a segunda pelo próprio Expo) e mudam de máquina para máquina. E mantenha hooks reutilizáveis em `hooks/` ou `utils/` (como faremos com `useTracking`, `useAcelerometro`) em vez de escrever a lógica direto dentro do arquivo da tela — isso facilita testar a mesma lógica em mais de uma tela.

### Ferramentas do projeto — criar, resetar e manter

**Objetivo:** antes de escrever a primeira linha de código do app de corrida, entender o que é uma CLI e um SDK, e revisar os comandos que criam, resetam, diagnosticam e atualizam um projeto Expo — a caixa de ferramentas que vamos usar do início ao fim deste tutorial.

**O que é uma CLI**

CLI é a sigla de *Command Line Interface* — uma interface de linha de comando, ou seja, um programa que você controla digitando comandos no terminal, em vez de clicar em botões. `npm`, `npx`, `expo` e `eas` (que aparece lá no fim deste tutorial) são todos exemplos de CLIs. A CLI do Expo especificamente (a que roda por trás de `npx expo start`, `npx expo export`, etc.) é o que cria projetos, inicia o servidor de desenvolvimento, empacota o código para rodar no celular, e por aí vai — é a ferramenta que vamos usar o tempo todo, do primeiro ao último tópico.

**O que é um SDK**

SDK é a sigla de *Software Development Kit* — um conjunto de bibliotecas, ferramentas e regras prontas para desenvolver para uma plataforma específica. O **Expo SDK** é justamente esse conjunto: as bibliotecas `expo-*` que vamos instalar ao longo do tutorial (`expo-location`, `expo-sqlite`, `expo-notifications`...) fazem parte dele, e todas são testadas para funcionar bem juntas em uma mesma versão. É por isso que o SDK tem um número de versão só (57, 58...) mesmo sendo formado por dezenas de pacotes separados: cada versão do Expo SDK fixa quais versões de React Native, React e de cada biblioteca `expo-*` funcionam entre si, para você não precisar descobrir isso manualmente.

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

**Passo 6 — editar o `package.json` diretamente**

`package.json` é o arquivo que descreve o projeto: nome, versão, os *scripts* que `npm run` sabe executar (como o `reset-project` do Passo 3), e as duas listas de dependências (`dependencies`, usadas pelo app em produção, e `devDependencies`, usadas só durante o desenvolvimento). Na prática, você edita esse arquivo de duas formas:

- **Indiretamente**, deixando um comando fazer a edição por você — é o que `npm install algum-pacote` e `npx expo install algum-pacote` já fazem, adicionando a linha correspondente em `dependencies` sozinhos. É a forma preferida na maioria dos casos, porque o comando já grava a versão certa.
- **Diretamente**, abrindo o arquivo no editor — necessário para tarefas que nenhum comando faz por você, como adicionar um novo `script` (por exemplo, um atalho `"limpar": "expo start --clear"`), ou fixar manualmente a versão de uma dependência antes de rodar `npm install` de novo.

Depois de editar `package.json` à mão, sempre rode `npm install` em seguida — é esse comando que lê o arquivo e efetivamente baixa/atualiza o que mudou dentro de `node_modules/`; só editar o texto do `package.json` não baixa nada sozinho.

**Passo 7 — quando (e por que) apagar `node_modules/` e `package-lock.json`**

De vez em quando, o projeto entra num estado confuso — um erro de dependência que não devia mais existir, um pacote que parece "meio instalado" depois de uma instalação interrompida. Como `node_modules/` é inteiramente reconstruível a partir de `package.json`, a solução mais confiável costuma ser recomeçar do zero:

```bash
rm -rf node_modules package-lock.json
npm install
```

Isso reinstala tudo, resolvendo as versões de cada dependência de novo, sem carregar nenhum resquício de uma instalação anterior. `package-lock.json` guarda exatamente qual versão de cada dependência (inclusive as dependências das dependências) foi instalada da última vez — apagá-lo junto com `node_modules/` faz o `npm install` recalcular essas versões do zero a partir das faixas de versão declaradas em `package.json`, em vez de apenas repetir o que já estava travado no lock file.

**Passo 8 — `npx expo start` e `npx expo start --clear`**

```bash
npx expo start
```

Inicia o servidor de desenvolvimento (o Metro bundler) e mostra o QR code para abrir o app no celular via Expo Go, além de atalhos de teclado (`r` para recarregar, `a`/`i` para abrir num emulador). É o comando que fica rodando durante todo o desenvolvimento.

```bash
npx expo start --clear
```

Faz a mesma coisa, mas primeiro **limpa o cache do Metro** antes de iniciar. Use essa versão quando uma mudança que deveria aparecer não aparece mesmo depois de salvar o arquivo e recarregar manualmente — sintoma comum depois de editar `babel.config.js`, mudar variáveis de ambiente, ou instalar um pacote novo com código nativo.

**Exercícios:**
- **Fácil:** Rode `npx expo-doctor` no seu projeto atual e leia o resultado — mesmo sem nenhum problema, entenda o que cada checagem está validando.
- **Médio:** Crie um projeto novo só para teste (`npx create-expo-app@latest teste-reset`), rode `npm run reset-project` nele, e confira o que mudou em `app/` e o que apareceu em `app-example/`.
- **Desafio:** Depois de instalar todos os pacotes usados neste tutorial (`expo-location`, `expo-sqlite`, `expo-notifications`, etc.), rode `npx expo install --check` e veja se algum ficou desalinhado — se sim, rode `--fix` e confirme que o app ainda bundla normalmente depois.
- **Desafio:** Apague `node_modules/` e `package-lock.json` do seu projeto, rode `npm install` de novo, e confirme que `npx expo start --clear` sobe o app normalmente depois — sem essa prática, um projeto "quebrado" costuma parecer bem mais grave do que realmente é.

**Perguntas para fixação:**
1. Por que `npx create-expo-app` usa `npx`, e não `npm install create-expo-app` seguido de rodar o comando instalado?
2. Qual é a diferença entre uma CLI e um SDK — por que `expo` é uma CLI, e as bibliotecas `expo-location`/`expo-sqlite` fazem parte de um SDK?

### Seu primeiro app — resultado em minutos

**Objetivo:** sair do projeto recém-criado direto para algo rodando no seu celular, sem enrolação — e entender a sintaxe `StyleSheet` que vamos usar em toda tela deste tutorial.

**Passo 1 — a primeira tela**

Depois de criar o projeto e rodar `npm run reset-project` (visto na seção anterior), abra `app/index.jsx` e substitua o conteúdo por isto:

```javascript
// app/index.jsx
import { StyleSheet, Text, View } from 'react-native';

export default function Inicio() {
  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>App de Corrida</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, alignItems: 'center', justifyContent: 'center' },
  titulo: { fontSize: 28, fontWeight: 'bold' },
});
```

**Passo 2 — rodar de verdade**

```bash
npx expo start
```

Escaneie o QR code exibido no terminal com o app **Expo Go** (Android) ou a câmera do iPhone (iOS), ou aperte `a`/`i` para abrir num emulador já configurado. Em poucos segundos o título "App de Corrida" aparece na tela do seu celular — esse é o app que vai crescer, tópico a tópico, até o fim deste tutorial.

**Por que `StyleSheet.create`, e não só um objeto comum**

Repare que `styles` não é um objeto `{ container: {...}, titulo: {...} }` qualquer usado direto — ele passa por `StyleSheet.create(...)`. Isso funciona diferente de CSS na web: não existe cascata nem seletor, cada componente recebe seu próprio objeto de estilo via prop `style`, com propriedades parecidas com CSS mas em `camelCase` (`backgroundColor`, não `background-color`) e sem unidade (`16`, não `"16px"` — o número já é interpretado em pixels de densidade independente). `StyleSheet.create` existe por dois motivos:
- **Valida em tempo de desenvolvimento.** Uma propriedade inexistente ou um valor de tipo errado gera um aviso imediato, em vez de silenciosamente não fazer nada.
- **Evita recriar o objeto a cada renderização.** Um objeto `{ flex: 1 }` escrito direto dentro do `return` é recriado do zero toda vez que o componente renderiza de novo; com `StyleSheet.create`, o objeto é criado uma única vez (fora do componente, como `styles` acima) e reaproveitado.

Também é possível passar um objeto comum direto em `style={{ flex: 1 }}` — funciona, e vamos usar essa forma para estilos muito pequenos e pontuais ao longo do tutorial — mas para os estilos de uma tela inteira, `StyleSheet.create` no fim do arquivo (como fizemos acima) é o padrão que vamos seguir sempre.

**Exercícios:**
- **Fácil:** Mude a cor e o tamanho do texto em `styles.titulo`, salve, e veja a tela atualizar sozinha (Fast Refresh) sem precisar reiniciar `npx expo start`.
- **Médio:** Adicione um segundo `<Text>` como subtítulo, com seu próprio estilo em `styles`.
- **Desafio:** Tente colocar uma propriedade CSS que não existe em React Native (por exemplo, `float: 'left'`) em `styles.container` dentro de `StyleSheet.create` e observe o aviso que aparece — depois remova e confirme que o app volta ao normal.

### Verificação de plataforma (`Platform.OS`)

**Objetivo:** ajustar código e estilo conforme o app está rodando no Android, no iOS ou na Web — usando a própria tela do Passo anterior como exemplo.

Apesar de "escrever uma vez, rodar em qualquer lugar" ser a proposta do React Native, algumas diferenças entre sistemas operacionais não somem sozinhas: a altura da barra de status, como sombras são desenhadas, certos comportamentos de teclado. O módulo `Platform`, do próprio `react-native`, resolve isso.

**Passo 1 — `Platform.OS`**

```javascript
import { Platform } from 'react-native';

console.log(Platform.OS); // 'ios', 'android', ou 'web'
```

Um simples `if (Platform.OS === 'android') { ... }` já resolve casos pontuais. Para diferenças de **estilo**, existe uma forma mais direta.

**Passo 2 — `Platform.select`**

`Platform.select` recebe um objeto com uma chave por plataforma (mais uma `default` opcional) e devolve o valor da chave correspondente à plataforma atual — útil dentro de um `StyleSheet.create`, onde um `if` não caberia. Vamos aplicar isso ao `container` da tela criada no Passo anterior, empurrando o conteúdo alguns pixels a mais no Android, onde a barra de status costuma ocupar mais espaço:

```javascript
// app/index.jsx — ajuste em styles.container
const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingTop: Platform.select({ ios: 20, android: 32, default: 0 }),
  },
  titulo: { fontSize: 28, fontWeight: 'bold' },
});
```

Não esqueça de importar `Platform` de `react-native` junto dos outros imports desse arquivo. Teste no Android e, se tiver acesso, no iOS — o espaçamento no topo deve variar entre os dois.

**Exercícios:**
- **Fácil:** Use `Platform.OS` para mostrar um `<Text>` diferente conforme a plataforma ("Rodando no Android" / "Rodando no iOS").
- **Médio:** Use `Platform.select` para dar uma sombra ao `titulo` — no iOS, com `shadowColor`/`shadowOffset`/`shadowOpacity`/`shadowRadius`; no Android, com a propriedade única `elevation` (os dois sistemas implementam sombra de formas totalmente diferentes, daí a necessidade do `Platform.select`).

**Pergunta para fixação:** por que a sombra de uma `View` no React Native precisa de propriedades diferentes no iOS e no Android, em vez de uma única propriedade `shadow` que funcionasse em ambos?

### Fundamentos de JavaScript para React

**Objetivo:** revisar, antes de seguir para hooks e componentes, quatro peças de sintaxe JavaScript que aparecem em praticamente toda linha de código deste tutorial: funções lambda, import/export, e a imutabilidade que o React espera do seu estado.

**Passo 1 — funções lambda (arrow functions)**

Uma função lambda (também chamada de *arrow function*, por causa do `=>`) é só outra forma de escrever uma função:

```javascript
// função tradicional
function somar(a, b) {
  return a + b;
}

// a mesma função, como lambda
const somar = (a, b) => {
  return a + b;
};

// com corpo de uma linha só, o "return" pode ser implícito
const somar = (a, b) => a + b;
```

Na prática, usamos lambdas o tempo todo como **callbacks** — funções passadas como argumento para rodar depois, em resposta a algo (um toque, uma resposta de rede, um item de lista sendo desenhado):

```javascript
<Button title="Buscar" onPress={() => consultarCep()} />

<FlatList
  data={rotas}
  renderItem={({ item }) => <Text>{item.nome}</Text>}
/>
```

Uma lambda escrita direto onde é usada, como nesses dois exemplos, nem precisa de nome — ela existe só para aquele uso específico, exatamente como as funções passadas para `.then()`, `.map()` e `useEffect()` que já apareceram até aqui.

**Passo 2 — import com e sem chaves, `export` e `export default`**

Um arquivo pode exportar de duas formas. **Export default** (no máximo um por arquivo) é o "produto principal" daquele arquivo:

```javascript
// hooks/useTracking.js
export default function useTracking() { /* ... */ }
```

Um export default é importado **sem chaves**, e pode receber qualquer nome na importação (o nome não precisa bater com o original):

```javascript
import useTracking from '../hooks/useTracking';
```

**Export nomeado** (quantos quiser por arquivo) exporta um valor pelo próprio nome:

```javascript
// utils/calculos.js
export function distanciaEntre(p1, p2) { /* ... */ }
export function distanciaTotal(pontos) { /* ... */ }
```

Um export nomeado é importado **com chaves**, e o nome dentro das chaves precisa bater exatamente com o nome exportado (`import { distanciaEntre } from '../utils/calculos'`) — a não ser que você o renomeie explicitamente com `as` (`import { distanciaEntre as calcularDistancia } from ...`). Um mesmo arquivo pode ter os dois tipos ao mesmo tempo, inclusive um import misturando ambos:

```javascript
import Tela, { algumaFuncaoAuxiliar } from './algumArquivo';
```

Ao longo deste tutorial, cada tela em `app/` usa `export default` (é assim que o Expo Router identifica qual componente é a tela daquela rota), enquanto hooks e funções utilitárias em `hooks/`/`utils/` costumam usar export nomeado, para permitir importar só a função específica que uma tela precisa.

**Passo 3 — imutabilidade e o operador spread**

React decide se um componente precisa renderizar de novo comparando se o valor guardado em `useState` **mudou de referência** — não se o conteúdo mudou. Isso significa que **mutar** um array ou objeto de estado diretamente (`array.push(item)`, `objeto.campo = valor`) não funciona como se espera: o conteúdo até muda, mas a referência continua a mesma, então o React pode nem perceber que algo mudou e não redesenhar a tela.

A solução é sempre criar uma **cópia nova** com a mudança já aplicada, usando o operador **spread** (`...`), em vez de alterar o original:

```javascript
// ERRADO — muta o array existente, o React pode não perceber a mudança
function adicionarPontoErrado(pontos, novoPonto) {
  pontos.push(novoPonto);
  return pontos; // mesma referência de antes
}

// CERTO — cria um array novo, com os itens antigos mais o novo
function adicionarPontoCerto(pontos, novoPonto) {
  return [...pontos, novoPonto]; // nova referência
}
```

O mesmo vale para objetos — copiar os campos existentes com `{ ...objeto }` e sobrescrever só o campo que muda:

```javascript
const usuario = { nome: 'Ana', idade: 20 };
const usuarioAniversariante = { ...usuario, idade: usuario.idade + 1 };
// usuario continua { nome: 'Ana', idade: 20 }; usuarioAniversariante é um objeto novo
```

Essa é exatamente a forma usada em `setPontos((anteriores) => [...anteriores, posicao.coords])`, que vamos escrever de verdade dentro de `useTracking` mais adiante — e no reducer de estado global, mais para o fim do tutorial (`{ ...state, pontos: [...state.pontos, action.ponto] }`).

**Passo 4 — juntando os três, em um app que já roda**

Vamos aplicar lambda, import/export e imutabilidade de uma vez, evoluindo a tela criada há pouco com uma lista simples de favoritos:

```javascript
// app/index.jsx
import { useState } from 'react';
import { StyleSheet, Text, View, Button, FlatList } from 'react-native';
import { Platform } from 'react-native';

export default function Inicio() {
  const [favoritos, setFavoritos] = useState([]);
  let proximoId = favoritos.length + 1;

  function adicionarFavorito() {
    // imutável: cria um array novo em vez de mutar "favoritos"
    setFavoritos((anteriores) => [...anteriores, { id: String(proximoId), nome: `Rota ${proximoId}` }]);
  }

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>App de Corrida</Text>
      <Button title="Adicionar rota favorita" onPress={() => adicionarFavorito()} />
      <FlatList
        data={favoritos}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => <Text>{item.nome}</Text>}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
    paddingTop: Platform.select({ ios: 20, android: 32, default: 0 }),
  },
  titulo: { fontSize: 28, fontWeight: 'bold' },
});
```

Rode `npx expo start` de novo (ou deixe rodando e salve o arquivo) e toque no botão algumas vezes — cada toque adiciona um item à lista, sem nunca mutar `favoritos` diretamente. `adicionarFavorito` é uma função nomeada comum, mas repare que ela é chamada de dentro de uma lambda (`onPress={() => adicionarFavorito()}`) — um padrão que vamos repetir sempre que uma função precisar receber argumentos ou fazer mais de uma coisa antes/depois da chamada.

**Exercícios:**
- **Fácil:** Adicione um botão "Limpar favoritos" que troca `favoritos` por um array vazio (`setFavoritos([])`).
- **Médio:** Extraia `adicionarFavorito` para `utils/favoritos.js` como export nomeado, recebendo `favoritos` e devolvendo o array novo (sem `useState` dentro dela), e importe-a de volta em `app/index.jsx` com chaves.
- **Desafio:** Adicione um botão "Remover último" usando `favoritos.slice(0, -1)` (que também não muta o array original) em vez de `pop()` (que mutaria).

**Perguntas para fixação:**
1. Por que `pontos.push(novoPonto); setPontos(pontos);` não faz a tela atualizar, mesmo o array tendo o item novo de fato?
2. Nas duas formas de export vistas no Passo 2, qual delas permite importar o valor com um nome diferente do original, sem precisar de `as`?

### Revisão — hooks, useState, useEffect e const

**O que é um hook?** É uma função especial do React, sempre com nome começando em `use` (`useState`, `useEffect`, `useRouter`, e os hooks próprios que vamos construir ao longo deste tutorial, como `useTracking`), que dá a um componente acesso a recursos do React — guardar um valor entre renderizações, reagir a mudanças, ler o contexto de navegação, etc. — sem precisar transformar esse componente em uma classe (o jeito antigo de fazer a mesma coisa, hoje raramente usado).

Hooks só podem ser chamados em dois lugares: direto no corpo de um componente, ou dentro de outro hook (é assim que `useTracking`, no Tópico 5, usa `useState` e `useRef` por baixo dos panos). Duas regras simples valem para todos eles:
- **Sempre no topo da função**, nunca dentro de `if`, `for` ou depois de um `return` — o React identifica cada hook pela ordem em que são chamados, então chamá-los condicionalmente bagunça essa ordem entre uma renderização e outra.
- **O nome sempre denuncia o que ele faz**: quando você criar seu próprio hook (como veremos em vários tópicos deste tutorial), comece o nome com `use` — é assim que tanto o React quanto quem lê seu código reconhece que aquela função segue essas regras.

Os exemplos deste tutorial usam os hooks `useState` e `useEffect` (ambos vêm prontos do React) o tempo todo, então vale revisar rapidamente o papel de cada um antes de seguir em frente.

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

Uma lista de dependências vazia (`[]`, como em vários exemplos deste tutorial) significa "rode só uma vez, quando o componente aparecer na tela pela primeira vez".

**Por que declarar com `const` sempre que possível:** ao longo de todo o tutorial, a maioria das variáveis é declarada com `const`, e não com `let` ou `var`. `const` impede que a variável seja reatribuída depois — se você tentar fazer `contador = 5` mais adiante no código (em vez de usar `setContador`), o JavaScript já aponta o erro na hora, em vez de deixar passar um bug para ser descoberto só depois, ao rodar o app. Isso também deixa o código mais fácil de ler: quem vê um `const` já sabe que aquele valor não muda depois de criado, sem precisar ler o resto da função para ter certeza. Reserve `let` só para os poucos casos em que a variável realmente precisa ser reatribuída (como o `i` de um `for`, ou o `soma` acumulado em `distanciaTotal`, no Tópico 3).

**Como a interface reage a uma mudança de estado — e as regras que isso impõe**

Quando `setContador` (ou qualquer outro `setAlgumaCoisa` vindo de `useState`) é chamado, o React não altera a tela na hora, ali mesmo, na linha em que `setContador` foi chamado — ele **agenda** uma nova renderização daquele componente. "Renderizar", para um componente React, significa simplesmente chamar a função do componente de novo, do início ao fim, com o novo valor de estado, e comparar o JSX resultante com o que estava na tela antes, atualizando só as partes que realmente mudaram (React não redesenha a tela inteira do zero a cada mudança, ele reconcilia).

Esse mecanismo tem três consequências práticas que valem a pena internalizar desde já, porque vão aparecer sem aviso o tutorial inteiro:

- **Só `useState`/`useReducer` disparam nova renderização.** Alterar uma variável comum (`let x = 5; x = 6;`) ou mutar um objeto/array existente não faz o React perceber nada — é por isso que a imutabilidade (Passo 3 da seção anterior) não é só estilo, é o que faz a tela realmente atualizar.
- **Atualizações de estado dentro do mesmo evento são agrupadas (*batching*).** Chamar `setA(1)` e `setB(2)` seguidos, dentro do mesmo `onPress`, não causa duas renderizações separadas — o React espera o evento terminar e renderiza uma única vez com os dois valores já atualizados. Isso evita telas "piscando" com estados intermediários que o usuário nunca deveria ver.
- **O valor de uma variável de estado dentro de uma função é sempre o da renderização em que aquela função foi criada** — nunca o mais atual "por fora". É por isso que `setContador(contador + 1)` chamado duas vezes seguidas soma só 1, não 2 (as duas chamadas enxergam o mesmo `contador` antigo); a forma seguraria seria `setContador((valorAtual) => valorAtual + 1)`, passando uma função em vez de um valor — o mesmo padrão já usado em `setPontos((anteriores) => [...anteriores, coords])`.

---

### 1. Componentes e Navegação no Framework

**Objetivo:** revisar o que é um componente e como organizá-los bem, e revisar a navegação por arquivos do Expo Router, aprendendo a passar dados entre telas por meio de rotas dinâmicas.

**O que é um componente**

Um componente é apenas uma função JavaScript que devolve JSX (a descrição de um pedaço de interface) e que começa com letra maiúscula — é assim que o React distingue um componente (`<TelaProdutos />`) de um elemento HTML/nativo comum (`<view />` não existiria; é `<View />`, também com maiúscula, pelo mesmo motivo). Um componente recebe dados de fora por meio de **props** (o primeiro parâmetro da função, geralmente desestruturado) e pode guardar dados que mudam ao longo do tempo com **estado** (`useState`, já revisado no início deste tutorial).

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

Seguindo a convenção adotada desde o início deste tutorial: componentes usados por **uma tela só** ficam no próprio arquivo daquela tela, dentro de `app/`; componentes reutilizados por **mais de uma tela** (como `CardRota` acima, ou `AvisoPermissaoNegada`, que construímos no Tópico 4) vão para `components/`, com um arquivo por componente, nomeado igual ao componente (`AvisoPermissaoNegada.jsx` exporta `AvisoPermissaoNegada`). É a mesma lógica de organização que já aplicamos a hooks (`hooks/`, `utils/`) desde o início deste tutorial.

**Passo 1 — o que é um layout, e o que é um Stack**

A navegação é feita com **Expo Router** — rotas de arquivo dentro de `app/`, sem configurar manualmente um `Stack.Navigator`. Um arquivo `_layout.jsx` não é uma tela: ele é o **contêiner** que envolve as telas de uma pasta, decidindo como o usuário se move entre elas (efeito visual da transição, se existe um menu, uma barra de abas, um botão de voltar, etc.). O `_layout.jsx` na raiz de `app/` vale para o app inteiro, a menos que uma subpasta tenha o seu próprio.

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

Vamos construir a tela inicial do app que fica no ar até o final deste tutorial: uma lista de rotas planejadas para correr. Cada arquivo dentro de `app/` já vira uma rota automaticamente. Para navegar entre eles, use `useRouter()` e `router.push`, passando os dados que a próxima tela vai precisar como parâmetros:

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

### Promises, async/await e funções assíncronas

**Objetivo:** entender o que é uma Promise, o que `async`/`await` fazem por baixo dos panos, e por que certos comandos do dia a dia (rede, banco de dados, permissões) são sempre assíncronos — construindo, de ponta a ponta, uma tela real de consulta de CEP.

**Passo 1 — o que é uma Promise**

Uma `Promise` é um objeto que representa um valor que **ainda não existe, mas vai existir (ou falhar) em algum momento no futuro** — o resultado de uma operação que leva tempo, como esperar a resposta de um servidor. Toda Promise está em um de três estados: *pending* (pendente, ainda esperando), *fulfilled* (resolvida com sucesso) ou *rejected* (rejeitada, com um erro). `fetch`, por exemplo, devolve uma Promise imediatamente, antes mesmo da resposta da rede chegar:

```javascript
fetch('https://viacep.com.br/ws/01001000/json/')
  .then((resposta) => resposta.json()) // roda quando a Promise resolve
  .then((dados) => console.log(dados))
  .catch((erro) => console.log('Deu erro:', erro)); // roda se a Promise for rejeitada
```

`.then()` registra uma função (um **callback**) para rodar quando a Promise for resolvida; `.catch()` registra uma para rodar se ela for rejeitada. Repare que `fetch(...).then(...)` não trava a linha seguinte do código — o JavaScript segue em frente imediatamente, e o callback dentro de `.then()` só roda quando a resposta chegar, mais tarde.

**Passo 2 — `async`/`await`: a mesma coisa, com outra aparência**

Encadear vários `.then()` fica difícil de ler quando um resultado depende do anterior. `async`/`await` é **açúcar sintático** sobre Promises — não é um mecanismo novo, só uma forma de escrever o mesmo código parecendo síncrono:

```javascript
async function buscarEndereco(cep) {
  const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
  const dados = await resposta.json();
  return dados;
}
```

Toda função `async` devolve uma Promise automaticamente, mesmo que o `return` dentro dela pareça devolver um valor comum. Dentro dela, `await` pausa **só a execução daquela função** (nunca a thread JS inteira, nem o resto do app) até a Promise à direita resolver, e então continua com o valor já resolvido em mãos — daí `const resposta = await fetch(...)` já vir com a resposta pronta, sem precisar de `.then()`. Erros são tratados com `try`/`catch`, em vez de `.catch()`:

```javascript
async function buscarEnderecoSeguro(cep) {
  try {
    const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    return await resposta.json();
  } catch (erro) {
    console.log('Falha de rede:', erro);
    return null;
  }
}
```

**Passo 3 — por que certos comandos são sempre assíncronos**

Toda operação que espera por algo **fora do controle imediato do JavaScript** é assíncrona: uma resposta de rede (`fetch`, `axios`), uma leitura/escrita em disco (`AsyncStorage`, arquivos), uma consulta ao banco local (`runAsync`/`getAllAsync` do `expo-sqlite`, mais adiante), ou uma resposta do próprio sistema operacional (`requestPermissionsAsync`, ao pedir uma permissão). Se essas operações fossem síncronas, a thread JS ficaria parada esperando — travando a interface inteira, o mesmo problema visto no Tópico de Concorrência, só que causado por espera de I/O em vez de cálculo pesado. É por isso que praticamente toda função deste tipo, em React Native, termina em `Async` no nome (`getAllAsync`, `requestForegroundPermissionsAsync`) e devolve uma Promise.

> 💡 **Dica de depuração:** toda `Promise` rejeitada silenciosamente é um bug escondido. Sem `try/catch` (ou `.catch()`), um erro de rede desaparece sem avisar em vez de aparecer no console. Prefira sempre tratar o erro enquanto estiver desenvolvendo, mesmo que o tratamento final na interface venha depois — e lembre que `console.log` aparece tanto no terminal onde `npx expo start` está rodando quanto no menu de desenvolvedor do próprio app.

**Passo 4 — montar a tela de consulta de CEP**

Juntando os três passos acima em uma tela completa:

```javascript
// app/cep.jsx
import { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';

export default function ConsultaCep() {
  const [cep, setCep] = useState('');
  const [dados, setDados] = useState(null);
  const [erro, setErro] = useState(null);

  async function consultarCep() {
    setErro(null);
    setDados(null);
    try {
      const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
      const dadoEmJson = await resposta.json();
      if (dadoEmJson.erro) {
        setErro('CEP não encontrado.');
      } else {
        setDados(dadoEmJson);
      }
    } catch (erroDeRede) {
      setErro('Falha de conexão. Tente novamente.');
    }
  }

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="Digite o CEP"
        value={cep}
        onChangeText={setCep}
        keyboardType="numeric"
      />
      <Button title="Buscar" onPress={() => consultarCep()} />
      {erro && <Text style={styles.erro}>{erro}</Text>}
      {dados && (
        <Text>{dados.logradouro}, {dados.localidade} - {dados.uf}</Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12, justifyContent: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 8, padding: 10 },
  erro: { color: '#c62828' },
});
```

Rode o app, digite um CEP válido (`01001000`, por exemplo) e confirme que o endereço aparece. Digite um CEP inexistente e confirme que a mensagem de erro aparece no lugar certo.

**Exercícios:**
- **Fácil:** Desligue o Wi-Fi do celular e teste `consultarCep` — confirme que a mensagem "Falha de conexão" aparece, em vez do app travar ou não fazer nada.
- **Médio:** Mostre um texto "Buscando..." enquanto `consultarCep` está em andamento, usando mais um `useState` (`carregando`).
- **Desafio:** Reescreva `consultarCep` usando só `.then()`/`.catch()`, sem `async`/`await`, e compare a legibilidade das duas versões.

**Perguntas para fixação:**
1. O que exatamente `await` pausa — a função em que ele está, a tela inteira, ou o app todo?
2. Por que `getAllAsync` (do banco local) devolve uma Promise, em vez de devolver as linhas diretamente?

---

### 2. Consumo de APIs no Framework

**Objetivo:** ir do padrão manual de `fetch` + `useState`/`useEffect` (construído na seção anterior) para uma biblioteca que cuida de cache e dos estados de carregando/erro automaticamente.

**Passo 1 — o padrão manual, revisitado**

A tela `app/cep.jsx`, construída na seção de Promises, segue o padrão manual: um `useState` para o dado, outro para o erro, e uma função `async` disparada por um botão.

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
- **Médio:** Troque `app/cep.jsx` (construída na seção de Promises) para usar `useQuery` em vez de `fetch` manual, comparando a quantidade de código necessária.
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

A partir do Expo SDK 56, o import padrão de `expo-contacts` (e de `expo-media-library`) passou a usar uma API nova baseada em classes. Para continuar usando `getContactsAsync`, importe de `expo-contacts/legacy`:

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

**Objetivo:** sair da leitura única de posição para um **rastreamento contínuo**, exibido em tempo real em um mapa que não recarrega a cada atualização.

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

Agora adicione a função `iniciar`. A diferença para uma leitura pontual de localização é o uso de `watchPositionAsync` em vez de `getCurrentPositionAsync`: em vez de ler a posição uma única vez, ele chama a função passada a cada nova posição, até que você cancele a assinatura.

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

### Mídia — imagens, sons e outros arquivos

**Objetivo:** exibir imagens (locais e remotas) e tocar um som durante a corrida, usando os pacotes que o próprio Expo recomenda para cada tipo de mídia.

```bash
npx expo install expo-image expo-audio
```

**Passo 1 — imagens locais e remotas com `expo-image`**

O componente de imagem do Expo (`expo-image`, não o `Image` do `react-native` puro) aceita duas formas de origem: um arquivo empacotado dentro do próprio app, referenciado com `require(...)`, ou uma URL remota, como texto simples:

```javascript
import { Image } from 'expo-image';

// arquivo local, empacotado no app — o caminho é relativo ao arquivo atual
<Image source={require('../assets/rotas/parque.png')} style={{ width: 80, height: 80 }} contentFit="cover" />

// imagem remota, carregada pela rede
<Image source="https://exemplo.com/imagens/orla.jpg" style={{ width: 80, height: 80 }} contentFit="cover" />
```

`contentFit` substitui o `resizeMode` do `Image` tradicional (`'cover'`, `'contain'`, `'fill'`, ...), e o `expo-image` já vem com cache de disco e memória prontos, sem configuração extra.

**Passo 2 — aplicando isso à lista de rotas**

Vamos usar imagens de verdade na lista de rotas planejadas construída no Tópico 1. Adicione o campo `imagemUrl` a cada rota:

```javascript
// app/rotas/index.jsx — trecho
const ROTAS_PLANEJADAS = [
  { id: '1', nome: 'Volta do parque', distanciaEstimadaKm: 3.2, imagemUrl: 'https://exemplo.com/imagens/parque.jpg' },
  { id: '2', nome: 'Orla da praia', distanciaEstimadaKm: 5.8, imagemUrl: 'https://exemplo.com/imagens/praia.jpg' },
];
```

E exiba a imagem no `CardRota` (Tópico 1):

```javascript
// components/CardRota.jsx
import { Image } from 'expo-image';

function CardRota({ rota, onPress }) {
  return (
    <TouchableOpacity onPress={onPress}>
      <Image source={rota.imagemUrl} style={{ width: 60, height: 60, borderRadius: 8 }} contentFit="cover" />
      <Text>{rota.nome}</Text>
      <Text>{rota.distanciaEstimadaKm} km</Text>
    </TouchableOpacity>
  );
}
```

**Passo 3 — tocando um som com `expo-audio`**

Para efeitos sonoros curtos (um apito ao iniciar a corrida, por exemplo), `expo-audio` expõe o hook `useAudioPlayer`, que recebe a origem do som (também aceita `require(...)` para um arquivo local) e devolve um player pronto para tocar:

```javascript
// app/corrida.jsx — trecho
import { useAudioPlayer } from 'expo-audio';

const somDeApito = require('../assets/sons/apito.mp3');

export default function Corrida() {
  const player = useAudioPlayer(somDeApito);
  // ...
}
```

**Passo 4 — tocar o som ao iniciar e ao parar**

Adicione um arquivo de áudio curto em `assets/sons/apito.mp3` (qualquer efeito sonoro curto serve) e chame `player.play()` dentro das próprias funções `iniciar`/`parar` do `useTracking` (Tópico 5), ou diretamente na tela, envolvendo a chamada existente:

```javascript
// app/corrida.jsx
import { View, Button, Text } from 'react-native';
import { useAudioPlayer } from 'expo-audio';
import { useTracking } from '../hooks/useTracking';
import { RaceMap } from '../components/RaceMap';

const somDeApito = require('../assets/sons/apito.mp3');

export default function Corrida() {
  const { pontos, rastreando, erro, iniciar, parar } = useTracking();
  const player = useAudioPlayer(somDeApito);

  function iniciarComSom() {
    player.seekTo(0);
    player.play();
    iniciar();
  }

  return (
    <View style={{ flex: 1 }}>
      <RaceMap pontos={pontos} />
      <Button title={rastreando ? 'Parar' : 'Iniciar'} onPress={rastreando ? parar : iniciarComSom} />
      {erro && <Text>{erro}</Text>}
    </View>
  );
}
```

`player.seekTo(0)` garante que o som toque do início toda vez, mesmo que a corrida anterior tenha tocado o mesmo apito há pouco.

**Exercícios:**
- **Fácil:** Troque as URLs de `imagemUrl` por imagens de verdade (pode ser qualquer imagem hospedada publicamente), e confirme que aparecem nos cards da lista.
- **Médio:** Adicione um segundo som, tocado quando o usuário aperta "Parar", com um efeito sonoro diferente do de "Iniciar".
- **Desafio:** Troque a imagem remota do detalhe da rota (`app/rotas/[id].jsx`) por uma vinda de `require(...)`, empacotada localmente — compare o tempo de carregamento entre as duas abordagens, principalmente com a rede lenta.

**Pergunta para fixação:** por que `require('../assets/sons/apito.mp3')` funciona para referenciar um arquivo local, mas não funcionaria se o caminho dentro do `require` fosse construído dinamicamente (por exemplo, `require('../assets/sons/' + nomeDoArquivo)`)?

---

### 6. Acesso a Recursos Nativos via Framework

**Objetivo:** aprender a ler **sensores de movimento** do aparelho, além dos recursos nativos de mídia vistos no tópico de imagens e sons.

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

**Objetivo:** persistir os dados da corrida localmente, usando o padrão de CRUD do `expo-sqlite`.

**Passo 1 — abrir o banco e criar a tabela**

`expo-sqlite` abre o banco de forma síncrona (`openDatabaseSync`), e a criação da tabela também roda síncrona (`execSync`) — as próximas operações dependem da tabela já existir, então não faz sentido continuar antes disso terminar.

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

Um caso clássico de uso da Context API é o tema/idioma de um app — um contexto envolvendo toda a árvore de telas. Aqui aplicamos a mesma ideia a um estado de negócio: o rastreamento da corrida.

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

Repare que nenhum tópico deste tutorial ficou isolado: cada um acrescentou uma peça ao mesmo aplicativo de corrida, começando pela lista de rotas planejadas no Tópico 1 e terminando no build publicável do Tópico 10. Juntando tudo o que foi construído passo a passo, a estrutura final de pastas fica assim:

```
app/
  index.jsx                       — primeira tela rodando (seção "Seu primeiro app")
  _layout.jsx                     — Stack raiz (Tópico 1); é aqui que entram o
                                     CorridaProvider (Tópico 9) e o
                                     setNotificationHandler (Tópico 8)
  (tabs)/_layout.jsx              — alternativa em abas para o layout raiz (Tópico 1, opcional)
  cep.jsx                         — consulta de CEP, primeiro exemplo de Promise/async-await
                                     (seção "Promises, async/await e funções assíncronas")
  rotas/
    index.jsx                     — lista de rotas planejadas, com imagens (Tópico 1 + Mídia)
    [id].jsx                      — detalhe da rota, com botão "Iniciar corrida" (Tópico 1)
  corrida.jsx                     — tela de rastreamento em tempo real, com mapa e som de apito
                                     (Tópico 5 + Mídia)
  historico.jsx                   — corridas salvas, lidas do SQLite (Tópico 7)
  compartilhar.jsx                — compartilhar um resultado com os contatos (Tópico 4)
  concorrencia.jsx                — comparação bloqueante x em lotes x InteractionManager (Tópico 3)

components/
  CardRota.jsx                    — item da lista de rotas, com imagem (Tópico 1 + Mídia)
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

assets/
  sons/apito.mp3                  — efeito sonoro tocado ao iniciar a corrida (Mídia)
```

**Como as peças se encaixam:** desde a primeira seção deste tutorial (`app/index.jsx` rodando em minutos) até aqui, cada tópico acrescentou uma peça ao mesmo aplicativo. O usuário abre o app e vê a lista de rotas planejadas, já com imagem de cada rota (Tópico 1 + Mídia); ao tocar em uma, cai na tela de detalhe e, de lá, em "Iniciar corrida", chega à tela de rastreamento (Tópico 5), que toca um apito (Mídia), desenha o trajeto ao vivo no mapa enquanto `useTracking` assina o GPS. Os mesmos `pontos` do rastreamento alimentam `distanciaTotal` (Tópico 3, cuidando para não travar a interface) e servem de coordenada para consultar o clima atual (Tópico 2, construído sobre os fundamentos de Promise/async-await de `app/cep.jsx`). Chacoalhar o celular durante a corrida marca um ponto de interesse no trajeto (Tópico 6). Ao terminar, o resultado pode ser compartilhado com um contato (Tópico 4), fica salvo no histórico local (Tópico 7) e pode disparar um lembrete para a próxima corrida (Tópico 8). O estado da corrida em andamento — em vez de ser repassado por prop de tela em tela — fica acessível de qualquer lugar via `useCorrida()` (Tópico 9). E, para sair do Expo Go e colocar esse app nas mãos de usuários de verdade, o Tópico 10 fecha o tutorial com o processo de build e publicação.

Nenhum tópico precisa ser "encaixado" à força depois: a essa altura, seguir os passos de cada um, na ordem em que aparecem, já constrói o app inteiro.
