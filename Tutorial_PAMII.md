# Tutorial — Programação para Aplicativos Móveis II (PAMII)

Este material ensina React Native com Expo, sempre em JavaScript, do zero até um aplicativo completo de corrida — funcional, publicável, e construído inteiramente ao longo deste tutorial. Não é preciso ter visto nada de React Native antes: os fundamentos de JavaScript e React necessários aparecem logo nos primeiros tópicos.

Já com o projeto recém-criado, o primeiro tópico coloca algo na tela, e cada tópico seguinte acrescenta um pedaço a mais do mesmo app, rodando no celular. Cada tópico é organizado em **passos** — construa o código na ordem apresentada, um pedaço de cada vez, testando no celular ou no emulador a cada passo, em vez de copiar o arquivo inteiro de uma vez só.

> 💡 **Dica de organização:** nunca commite as pastas `node_modules/` e `.expo/` no Git — ambas são geradas automaticamente (a primeira por `npm install`, a segunda pelo próprio Expo) e mudam de máquina para máquina. E mantenha hooks reutilizáveis em `hooks/` ou `utils/` (como faremos com `useTracking`, `useAcelerometro`) em vez de escrever a lógica direto dentro do arquivo da tela — isso facilita testar a mesma lógica em mais de uma tela.

**Sobre os nomes usados no código:** sempre que possível, nomes de variáveis, funções, componentes e constantes que **nós escolhemos** (`distanciaTotal`, `useTracking`, `assinaturaRef`, `CartaoDeRota`) aparecem em português neste tutorial. Nomes em inglês, por outro lado, quase sempre indicam algo que **vem de fora** — uma função, prop ou método definido pelo React, pelo Expo ou por alguma outra biblioteca (`useState`, `onPress`, `StyleSheet.create`, `getAllAsync`). Essa distinção é proposital: ao ler um trecho de código, o idioma do identificador já é uma pista de se aquele nome pode ser mudado livremente (é nosso) ou se precisa ser escrito exatamente daquele jeito, porque pertence a uma API externa (não é nosso, e renomear quebraria o código).

## Sumário

- [Por que frameworks multiplataforma existem](#por-que-frameworks-multiplataforma-existem)
- [Ferramentas do projeto — criar, resetar e manter](#ferramentas-do-projeto-criar-resetar-e-manter)
- [Seu primeiro app](#seu-primeiro-app)
- [Personalizando o ícone e a tela de abertura (splash screen)](#personalizando-o-ícone-e-a-tela-de-abertura-splash-screen)
- [Depurando o app (debugging)](#depurando-o-app-debugging)
- [Verificação de plataforma (`Platform.OS`)](#verificação-de-plataforma-platformos)
- [Fundamentos de JavaScript para React](#fundamentos-de-javascript-para-react)
- [Revisão — hooks, useState, useEffect e const](#revisão-hooks-usestate-useeffect-e-const)
- [1. Componentes e Navegação no Framework](#1-componentes-e-navegação-no-framework)
- [Promises, async/await e funções assíncronas](#promises-asyncawait-e-funções-assíncronas)
- [2. Consumo de APIs no Framework](#2-consumo-de-apis-no-framework)
- [Criando sua própria API (Node.js/Express) e autenticação com JWT](#criando-sua-própria-api-nodejsexpress-e-autenticação-com-jwt)
- [3. Concorrência e Threads](#3-concorrência-e-threads)
- [4. Permissões Avançadas](#4-permissões-avançadas)
- [5. Localização e Mapas](#5-localização-e-mapas)
- [Mídia — imagens, sons e outros arquivos](#mídia-imagens-sons-e-outros-arquivos)
- [Animações com Reanimated](#animações-com-reanimated)
- [6. Acesso a Recursos Nativos via Framework](#6-acesso-a-recursos-nativos-via-framework)
- [Chamando código nativo (Kotlin/Java) a partir do React Native](#chamando-código-nativo-kotlinjava-a-partir-do-react-native)
- [7. Armazenamento Local no Framework](#7-armazenamento-local-no-framework)
- [Gráficos — em tempo real e estáticos](#gráficos-em-tempo-real-e-estáticos)
- [8. Notificações](#8-notificações)
- [9. Gerenciamento de Estado (Context API, Redux)](#9-gerenciamento-de-estado-context-api-redux)
- [Autenticação e Proteção de Telas](#autenticação-e-proteção-de-telas)
- [Menu de navegação (Drawer) — acesso a todas as telas](#menu-de-navegação-drawer-acesso-a-todas-as-telas)
- [10. Publicação de Apps (builds, lojas)](#10-publicação-de-apps-builds-lojas)
- [Projeto completo — todos os arquivos juntos](#projeto-completo-todos-os-arquivos-juntos)

### Por que frameworks multiplataforma existem

**Objetivo:** entender o problema que o React Native resolve, e onde ele se encaixa comparado a desenvolvimento nativo.

Construir um app nativo "puro" significa manter **duas bases de código completamente separadas**: uma em Kotlin (ou Java) para Android, outra em Swift (ou Objective-C) para iOS — cada tela, cada regra de negócio, cada correção de bug, escrita e testada duas vezes, em duas linguagens diferentes, por equipes que muitas vezes nem se falam.

Um framework multiplataforma como o React Native resolve isso compartilhando **uma única base de código** em JavaScript, que roda nos dois sistemas — o que vamos construir ao longo deste tutorial funciona igual num Android e num iPhone, sem duplicar nada. A troca não é de graça: parte do controle de baixíssimo nível que o nativo puro oferece desde o primeiro dia fica, no React Native, atrás de uma camada de abstração (os módulos do Expo, que usaremos o tutorial inteiro). Para a grande maioria dos apps — os chamados "apps de negócio", com telas, listas, formulários, mapas, câmera — essa troca compensa amplamente a velocidade de desenvolvimento ganha; para o pequeno grupo de apps que dependem de algo extremamente específico de uma plataforma (processamento de áudio em tempo real, por exemplo), o nativo puro ainda tem seu lugar.

Comparando os elementos mais básicos entre os dois mundos, para dar uma ideia concreta do que "uma camada de abstração" significa na prática:

| Nativo (Android/Kotlin) | React Native |
|---|---|
| `TextView` | `<Text>` |
| `LinearLayout` | `<View style={{ flexDirection: ... }}>` |
| Duas bases de código (Kotlin + Swift) | Uma base compartilhada |
| Acesso a 100% das APIs do sistema operacional desde o primeiro dia | Acesso via módulos do Expo (cobre a grande maioria dos casos de uso) |

Cada linha dessa tabela vai aparecer na prática ao longo do tutorial: `<Text>` e `<View>` já apareceram no primeiro app rodando, a seguir; e o "acesso via módulos do Expo" é exatamente o que os tópicos de localização, sensores, câmera e notificações vão explorar — cada um desses recursos nativos chega até o JavaScript through um módulo Expo, não por acesso direto à API do sistema operacional.

**Exercício:** pesquise rapidamente sobre o Flutter (o principal concorrente do React Native, mantido pelo Google, que usa Dart em vez de JavaScript) e monte uma tabela de prós/contras comparando os dois — em que tipo de projeto cada um tende a ser a escolha mais natural?

---

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

**Passo 8 — o que é o Metro, e `npx expo start`/`npx expo start --clear`**

**Metro** é o *bundler* (empacotador) de JavaScript usado pelo React Native — o programa que pega todos os seus arquivos `.js`/`.jsx`, mais os de cada dependência instalada, e os transforma em um único pacote de código que o celular consegue executar. Ele faz isso seguindo a árvore de `import`s a partir do ponto de entrada do app: começa em `expo-router/entry`, abre cada arquivo importado, entende quais outros arquivos aquele arquivo importa, e repete até ter mapeado tudo o que o app realmente usa (foi exatamente esse processo que rodamos manualmente na verificação deste tutorial, com `npx expo export`, contando quantos "módulos" o Metro conseguiu empacotar sem erro). Enquanto o projeto está em desenvolvimento, o Metro também fica de olho nos arquivos: ao salvar um deles, ele reprocessa só o que mudou e manda a atualização para o celular na hora (é isso que torna o Fast Refresh possível), em vez de reempacotar o projeto inteiro a cada salvamento. Para acelerar esse trabalho, o Metro guarda um **cache** de resultados de transformações já feitas — cache esse que, ocasionalmente, fica desatualizado ou corrompido (mudou uma configuração, um pacote nativo novo foi instalado) e precisa ser limpo manualmente, o que nos leva ao segundo comando abaixo.

```bash
npx expo start
```

Inicia o servidor de desenvolvimento (o próprio Metro, rodando em modo servidor) e mostra o QR code para abrir o app no celular via Expo Go, além de atalhos de teclado (`r` para recarregar, `a`/`i` para abrir num emulador). É o comando que fica rodando durante todo o desenvolvimento.

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

### Seu primeiro app

**Objetivo:** sair do projeto recém-criado direto para algo rodando no seu celular, e entender a sintaxe `StyleSheet` que vamos usar em toda tela deste tutorial.

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

Repare que `styles` não é um objeto `{ container: {...}, titulo: {...} }` qualquer usado direto — ele passa por `StyleSheet.create(...)`. Isso funciona diferente de CSS na web: não existe cascata nem seletor, cada componente recebe seu próprio objeto de estilo via prop `style`, com propriedades parecidas com CSS mas em `camelCase` (`backgroundColor`, não `background-color`) e sem unidade (`16`, não `"16px"` — o número já é interpretado em pixels de densidade independente).

**O que são "pixels de densidade independente"**

Celulares diferentes têm densidades de tela bem diferentes — o mesmo espaço físico (por exemplo, 1 centímetro) pode corresponder a 300 pixels reais em um aparelho e a 450 em outro mais denso. Se o React Native usasse pixels reais diretamente, um `fontSize: 16` ficaria visivelmente menor em um aparelho de tela mais densa do que em outro — o mesmo número de pixels reais ocupa menos espaço físico quanto mais denso for o painel. Um **pixel de densidade independente** (também chamado de `dp` ou `dip`) é uma unidade que já leva essa densidade em conta: o sistema converte esse número para a quantidade certa de pixels reais em cada aparelho, de forma que `fontSize: 16` pareça do mesmo tamanho físico em qualquer tela, densa ou não. Por isso, todo número usado em `StyleSheet.create` (`16`, `flex: 1`, `padding: 8`) é sempre density-independent, nunca pixel real — e é também por isso que não existe unidade nenhuma depois do número, diferente do `"16px"` do CSS: no React Native, "pixel" já significa, por padrão, esse pixel independente de densidade.

`StyleSheet.create` existe por dois motivos:
- **Valida em tempo de desenvolvimento.** Uma propriedade inexistente ou um valor de tipo errado gera um aviso imediato, em vez de silenciosamente não fazer nada.
- **Evita recriar o objeto a cada renderização.** Um objeto `{ flex: 1 }` escrito direto dentro do `return` é recriado do zero toda vez que o componente renderiza de novo; com `StyleSheet.create`, o objeto é criado uma única vez (fora do componente, como `styles` acima) e reaproveitado.

Também é possível passar um objeto comum direto em `style={{ flex: 1 }}` — funciona, e vamos usar essa forma para estilos muito pequenos e pontuais ao longo do tutorial — mas para os estilos de uma tela inteira, `StyleSheet.create` no fim do arquivo (como fizemos acima) é o padrão que vamos seguir sempre.

**Por que não dá para usar um arquivo `.css` comum**

O Expo também consegue exportar um app para rodar no navegador (`npx expo export --platform web`), e nesse alvo específico existe uma engine de CSS de verdade por baixo — é por isso que, pesquisando por aí, é possível encontrar exemplos de projeto Expo com um arquivo `.css` importado e uma prop `className`, e aquilo parecer funcionar. O problema é que isso só funciona **no navegador**. No Android e no iOS não existe HTML, não existe DOM, e não existe motor nenhum de CSS rodando por trás — o React Native não desenha `<div>`s que um navegador depois estiliza; ele traduz cada `<View>`/`<Text>` diretamente em um componente de interface nativo do sistema operacional (`UIView` no iOS, `ViewGroup`/`View` no Android). Não existe nada ali para interpretar um seletor CSS ou um arquivo `.css` — essas plataformas simplesmente não sabem o que fazer com eles.

O sistema `style`/`StyleSheet` existe justamente para funcionar **igual nas três plataformas**: o mesmo `styles.container` do topo deste tópico roda sem alteração nenhuma no Android, no iOS e na Web, porque ele nunca dependeu de CSS — sempre foi um objeto JavaScript comum, interpretado pelo próprio React Native (ou pelo `react-native-web`, no caso do navegador) para desenhar a tela. Usar `.css`/`className` amarra parte do código só ao alvo Web, quebrando o mesmo código nos outros dois. Por isso, ao longo deste tutorial, todo estilo é escrito com `style`/`StyleSheet.create`, mesmo quando o app também for exportado para a Web.

**Exercícios:**
- **Fácil:** Mude a cor e o tamanho do texto em `styles.titulo`, salve, e veja a tela atualizar sozinha (Fast Refresh) sem precisar reiniciar `npx expo start`.
- **Médio:** Adicione um segundo `<Text>` como subtítulo, com seu próprio estilo em `styles`.
- **Desafio:** Tente colocar uma propriedade CSS que não existe em React Native (por exemplo, `float: 'left'`) em `styles.container` dentro de `StyleSheet.create` e observe o aviso que aparece — depois remova e confirme que o app volta ao normal.

### Personalizando o ícone e a tela de abertura (splash screen)

**Objetivo:** trocar o ícone genérico do Expo (e a tela branca que pisca ao abrir o app) pela identidade visual do seu próprio app — tudo configurado num único lugar, `app.json`.

**Passo 1 — onde cada coisa é configurada**

Tanto o ícone quanto a splash screen são controlados por `app.json`, não por código React. O ícone tem uma entrada simples (`icon`) mais uma versão específica para Android (`android.adaptiveIcon`); a splash é configurada como opções do plugin `expo-splash-screen`, já presente por padrão em todo projeto criado com `create-expo-app`:

```json
// app.json — trecho
{
  "expo": {
    "icon": "./assets/images/icon.png",
    "android": {
      "adaptiveIcon": {
        "backgroundColor": "#2196F3",
        "foregroundImage": "./assets/images/android-icon-foreground.png",
        "backgroundImage": "./assets/images/android-icon-background.png",
        "monochromeImage": "./assets/images/android-icon-monochrome.png"
      }
    },
    "web": {
      "favicon": "./assets/images/favicon.png"
    },
    "plugins": [
      [
        "expo-splash-screen",
        {
          "backgroundColor": "#2196F3",
          "image": "./assets/images/splash-icon.png",
          "imageWidth": 160
        }
      ]
    ]
  }
}
```

**Passo 2 — o ícone: um arquivo simples, e a versão adaptativa do Android**

`icon` é o caminho mais direto: um único PNG quadrado (pelo menos 1024×1024, sem partes transparentes essenciais — em telas antigas do Android ele pode aparecer sem nenhum recorte especial) usado como ícone genérico (Web e, quando nenhuma opção mais específica existe, iOS também).

Desde o Android 8, porém, o sistema não usa um ícone único — ele monta um **ícone adaptativo** a partir de até três camadas separadas, todas do mesmo tamanho (1024×1024) mas com papéis diferentes:
- **`foregroundImage`** — só o desenho/logo em si, com fundo **transparente**. O sistema recorta essa camada em formatos diferentes (círculo, "squircle", quadrado com cantos arredondados...) dependendo do fabricante do aparelho — por isso o desenho precisa caber numa **zona segura** central, cerca de 65% do canvas; qualquer coisa mais perto da borda pode ser cortada dependendo do aparelho.
- **`backgroundImage`** (ou, mais simples, só `backgroundColor`) — o que fica atrás do desenho. Uma cor sólida, como usamos aqui, funciona bem e é a opção mais simples.
- **`monochromeImage`** — uma versão de **uma cor só** do mesmo desenho, com fundo transparente. É usada pelos "ícones temáticos" do Android 13+ (quando o usuário ativa cores dinâmicas no sistema) — o próprio Android tinge essa camada com a cor do tema do usuário, então ela precisa ser só o desenho, sem nenhuma cor própria.

**Passo 3 — a splash screen**

A splash é mais simples: uma imagem central (`image`, tipicamente só o logo/símbolo, sem o fundo — o `backgroundColor` da própria configuração já preenche o resto da tela) e `imageWidth`, a largura em pixels de densidade independente (o mesmo conceito visto lá em "Seu primeiro app") em que essa imagem deve aparecer na tela — não precisa bater com o tamanho do arquivo fonte, que pode (e deve) ser maior, para não ficar borrado em telas de alta densidade.

**Passo 4 — por que o Expo Go não mostra nada disso**

Esse é o detalhe que mais confunde quem está vendo isso pela primeira vez: **o Expo Go sempre mostra o próprio ícone e a própria splash do Expo Go**, nunca os do seu projeto — faz sentido, já que o Expo Go é um app só, capaz de abrir qualquer projeto, e o ícone dele fica fixo na tela inicial do celular independentemente de qual projeto você abrir por dentro. Ícone e splash **customizados só aparecem num build de verdade** — o mesmo tipo gerado com `npx expo run:android` (visto lá em "Chamando código nativo") ou com `eas build`, no tópico de Publicação. Rode `npx expo run:android` depois de trocar essas imagens e confira o ícone novo na gaveta de apps do celular, e a splash na primeira tela que aparece ao abrir.

**Exercícios:**
- **Fácil:** Troque `backgroundColor` da splash e do `adaptiveIcon` por outra cor, rode `npx expo run:android` de novo e confirme a mudança tanto na splash quanto no ícone.
- **Médio:** Gere as quatro imagens (`icon.png`, `android-icon-foreground.png`, `android-icon-background.png`, `android-icon-monochrome.png`) a partir de uma arte própria — qualquer editor de imagem serve, desde que exporte PNG com fundo transparente nas camadas que precisam dele.
- **Desafio:** Depois de publicado ao menos um build de preview (Tópico 10), troque o ícone de novo e gere um novo build — note que o ícone antigo continua na tela inicial do celular até você desinstalar e reinstalar o app; diferente do JavaScript (atualizado via Fast Refresh ou OTA), ícone e splash exigem uma reinstalação para atualizar.

**Pergunta para fixação:** por que a camada `monochromeImage` do ícone adaptativo não pode ter cor própria, ao contrário de `foregroundImage`?

### Depurando o app (debugging)

**Objetivo:** conhecer as ferramentas que mostram o que está acontecendo por trás da tela — antes de precisar delas de verdade, num bug real.

**Passo 1 — o menu de desenvolvedor**

Com o app aberto (Expo Go ou um development build), abra o **menu de desenvolvedor**: aperte `m` no terminal onde `npx expo start` está rodando, ou agite o celular fisicamente (Android), ou toque a tela com três dedos (iOS). Ele traz, entre outras opções:

- **Reload** — recarrega o app do zero (raramente necessário, já que o Fast Refresh atualiza sozinho a maior parte das mudanças).
- **Toggle performance monitor** — mostra, em tempo real, uso de RAM, memória heap do JavaScript (ajuda a notar vazamentos de memória) e quadros por segundo (FPS) separados por thread de UI e thread JS — os mesmos dois "lados" que discutimos no Tópico de Concorrência.
- **Toggle element inspector** — sobrepõe a tela com a árvore de componentes; tocar em qualquer elemento mostra suas props e estilos aplicados, útil para descobrir por que um espaçamento está errado sem precisar adivinhar.
- **Open DevTools** — abre o React Native DevTools, o assunto do próximo passo.

**Passo 2 — React Native DevTools**

Com o app rodando, aperte `j` no terminal do `npx expo start`. Isso abre o **React Native DevTools** — uma ferramenta parecida com o DevTools do Chrome, mas conectada diretamente ao JavaScript do seu app, com abas de Console, Sources, Network e Memory, além de Components e Profiler (herdadas do React DevTools).

- **Console** — o mesmo `console.log` que já usamos o tutorial inteiro aparece aqui, mas a aba também é um terminal interativo: dá para digitar qualquer expressão JavaScript e ela roda no contexto atual do app.
- **Sources** — clique num número de linha para colocar um **breakpoint**; quando a execução chegar ali, o app inteiro pausa (não trava — pausa mesmo, esperando você inspecionar) e você pode ver o valor de cada variável naquele exato momento. O mesmo efeito acontece escrevendo a palavra `debugger;` em qualquer linha do código.
- **Pause on exceptions** — ative essa opção (no painel do Sources) para que o app pause automaticamente assim que um erro for lançado, mesmo que algum componente (como o Expo Router) acabe capturando esse erro depois — sem isso, um erro tratado silenciosamente nunca chega a aparecer.
- **Network** (só em apps Expo) — mostra cada `fetch`/`axios` feito pelo app, com status, tempo de resposta e corpo da requisição/resposta — útil para depurar exatamente os `fetch` que construímos nos tópicos de Promises e Consumo de APIs, sem precisar espalhar `console.log` em cada um.

**Passo 3 — disciplina de depuração, não só ferramentas**

Ferramenta nenhuma substitui alguns hábitos básicos, já mencionados em dicas espalhadas pelo tutorial, reunidos aqui:
- Sempre trate erros de `Promise` com `try/catch` (visto na seção de Promises) — um erro pego sem aviso não aparece em lugar nenhum, nem no Console do DevTools.
- `console.log` dentro do `<script>` de uma `WebView` (Tópico 5) não aparece no DevTools nem no terminal — é um ambiente JavaScript totalmente separado; a única ponte de volta é `postMessage`, como já vimos.
- Depois de instalar um pacote novo ou editar `app.json`/`babel.config.js`, reinicie `npx expo start` (de preferência com `--clear`, visto na seção de Ferramentas) antes de assumir que o código está errado — muitas vezes o problema é só o Metro não ter recarregado a mudança de configuração.

**Exercícios:**
- **Fácil:** Coloque um breakpoint dentro de `adicionarFavorito` (seção anterior) usando a aba Sources, toque no botão, e inspecione o valor de `proximoId` no momento da pausa.
- **Médio:** Ative "Pause on exceptions", force um erro de propósito (por exemplo, chame uma função que não existe), e observe o app pausar exatamente na linha do erro.
- **Desafio:** Abra a aba Network do DevTools enquanto testa a tela de CEP (seção de Promises) e confirme visualmente o corpo da resposta de uma consulta bem-sucedida e de uma malsucedida.

**Pergunta para fixação:** por que um `console.log` colocado dentro do HTML de uma `WebView` não aparece nem no terminal do Metro nem no React Native DevTools, mesmo os dois rodando no mesmo processo do app?

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

**Passo 2 — desestruturação (destructuring)**

"Desestruturar" é extrair valores de dentro de um array ou objeto direto para variáveis separadas, numa única linha, em vez de acessar cada posição/campo manualmente:

```javascript
// sem desestruturar
const resultado = useState(0);
const contador = resultado[0];
const setContador = resultado[1];

// desestruturando um array — funciona porque useState devolve um array de 2 posições
const [contador, setContador] = useState(0);
```

```javascript
// sem desestruturar
function Saudacao(props) {
  return <Text>Olá, {props.nome}!</Text>;
}

// desestruturando um objeto — extrai só o campo "nome" das props recebidas
function Saudacao({ nome }) {
  return <Text>Olá, {nome}!</Text>;
}
```

A diferença entre as duas formas acima é sintaticamente importante: desestruturar um **array** (como `useState` devolve) usa colchetes `[a, b]` e extrai **por posição** — o primeiro item vai para a primeira variável, não importa o nome dela. Desestruturar um **objeto** (como as props de um componente) usa chaves `{ a, b }` e extrai **por nome do campo** — `{ nome }` só funciona porque o objeto de props tem um campo chamado exatamente `nome`.

Esse é o padrão por trás de quase toda função deste tutorial que recebe props (`{ rota, onPress }` em `CartaoDeRota`, `{ id, nome, distanciaEstimadaKm }` em `useLocalSearchParams()`) e por trás de todo `useState`/`useReducer` — vale reconhecer a sintaxe agora para não estranhá-la mais adiante.

**Passo 3 — import com e sem chaves, `export` e `export default`**

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

**Passo 4 — imutabilidade e o operador spread**

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

**Passo 5 — juntando tudo, em um app que já roda**

Vamos aplicar lambda, desestruturação, import/export e imutabilidade de uma vez, evoluindo a tela criada há pouco com uma lista simples de favoritos:

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
2. Nas duas formas de export vistas no Passo 3, qual delas permite importar o valor com um nome diferente do original, sem precisar de `as`?
3. `const [id, nome] = useLocalSearchParams();` funcionaria para ler os parâmetros de `app/rotas/[id].jsx` (Tópico 1) do mesmo jeito que `const { id, nome } = useLocalSearchParams();`? Por quê?

### Revisão — hooks, useState, useEffect e const

**O que é um hook?** É uma função especial do React, sempre com nome começando em `use` (`useState`, `useEffect`, `useRouter`, e os hooks próprios que vamos construir ao longo deste tutorial, como `useTracking`), que dá a um componente acesso a recursos do React — guardar um valor entre renderizações, reagir a mudanças, ler o contexto de navegação, etc. — sem precisar transformar esse componente em uma classe (o jeito antigo de fazer a mesma coisa, hoje raramente usado).

**Por que um hook é diferente de uma função comum**

Uma função JavaScript comum não tem memória própria: toda vez que ela é chamada, começa do zero, sem lembrar de nada da chamada anterior — é por isso que uma variável declarada dentro dela (`let x = 0`) volta a `0` a cada nova chamada, nunca acumula nada entre uma execução e a próxima. Um hook quebra essa regra de propósito: por baixo dos panos, o React associa cada chamada de hook a uma "posição" fixa dentro daquele componente específico, e guarda o valor daquele hook nessa posição, entre uma renderização e outra — é assim que `useState` consegue lembrar o valor de `contador` mesmo depois do componente inteiro ter sido chamado de novo (lembre-se: renderizar é só chamar a função do componente de novo, como vimos na seção de "Fundamentos de JavaScript"). Uma função comum não tem onde guardar esse tipo de memória; um hook tem, porque o React reserva esse espaço para ele.

É exatamente essa "posição fixa" que explica a regra mais estranha dos hooks — a de nunca chamá-los dentro de um `if`. Hooks só podem ser chamados em dois lugares: direto no corpo de um componente, ou dentro de outro hook (é assim que `useTracking`, no Tópico 5, usa `useState` e `useRef` por baixo dos panos). Duas regras simples valem para todos eles:
- **Sempre no topo da função**, nunca dentro de `if`, `for` ou depois de um `return` — o React não identifica cada hook pelo nome da variável nem por nenhum identificador explícito, e sim pela **ordem** em que são chamados dentro do componente, a cada renderização. Se a primeira renderização chama `useState` e depois `useEffect`, mas uma renderização seguinte pula o `useState` por causa de um `if`, o React associa o valor errado de memória ao `useEffect` seguinte — a ordem das posições fica toda desalinhada. Uma função comum não tem esse problema porque não guarda memória nenhuma entre chamadas; um hook tem, e é exatamente essa memória que a ordem fixa protege.
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

**`useRef`** guarda um valor entre renderizações, igual `useState` — mas com uma diferença essencial: **mudar um ref não faz o componente renderizar de novo**. Ele devolve um único objeto, sempre o mesmo a cada renderização, com uma propriedade `.current` que você lê e escreve livremente:

```javascript
import { useRef } from 'react';

const contadorDeCliques = useRef(0);

function aoClicar() {
  contadorDeCliques.current = contadorDeCliques.current + 1;
  console.log(contadorDeCliques.current); // já mostra o valor novo
  // mas a tela não é redesenhada por causa dessa linha
}
```

**Quando usar `useRef` em vez de `useState`:** sempre que um valor precisa **persistir** entre renderizações, mas **não deve aparecer na tela** — ou seja, mudá-lo não deveria disparar um redesenho. Ao longo deste tutorial, `useRef` aparece em três situações que se repetem:
- **Guardar um identificador para cancelar algo depois** — `assinaturaRef`, no Tópico 5, guarda a assinatura do GPS só para poder chamar `.remove()` nela mais tarde, dentro de `parar()`. O próprio valor da assinatura nunca precisa aparecer na interface.
- **Guardar uma referência a um componente nativo, para chamar um método nele** — `webviewRef`, no Tópico 5, guarda uma referência à `WebView` para poder chamar `injectJavaScript` nela diretamente, por fora do ciclo normal de renderização.
- **Controlar algo "por trás dos panos", sem exibir esse controle na tela** — `pontosEnviadosRef`, também no Tópico 5, guarda quantos pontos já foram enviados ao mapa; e `ultimaChacoalhada`, no Tópico 6, guarda o instante da última detecção, só para calcular o intervalo até a próxima. Se qualquer um desses dois virasse `useState`, cada atualização causaria uma renderização inteira só para atualizar um número que ninguém vê na tela.

Repare que, diferente de `useState`, mudar um ref é feito **mutando `.current` diretamente** (`assinaturaRef.current = valor`), sem uma função `setAlgumaCoisa` — a regra de imutabilidade da seção de Fundamentos de JavaScript vale para **estado** (`useState`/`useReducer`), não para refs, exatamente porque um ref nunca aciona a comparação que o React faz para decidir se redesenha o componente.

**Por que declarar com `const` sempre que possível:** ao longo de todo o tutorial, a maioria das variáveis é declarada com `const`, e não com `let` ou `var`. `const` impede que a variável seja reatribuída depois — se você tentar fazer `contador = 5` mais adiante no código (em vez de usar `setContador`), o JavaScript já aponta o erro na hora, em vez de deixar passar um bug para ser descoberto só depois, ao rodar o app. Isso também deixa o código mais fácil de ler: quem vê um `const` já sabe que aquele valor não muda depois de criado, sem precisar ler o resto da função para ter certeza. Reserve `let` só para os poucos casos em que a variável realmente precisa ser reatribuída (como o `i` de um `for`, ou o `soma` acumulado em `distanciaTotal`, no Tópico 3).

**Como a interface reage a uma mudança de estado — e as regras que isso impõe**

Quando `setContador` (ou qualquer outro `setAlgumaCoisa` vindo de `useState`) é chamado, o React não altera a tela na hora, ali mesmo, na linha em que `setContador` foi chamado — ele **agenda** uma nova renderização daquele componente. "Renderizar", para um componente React, significa simplesmente chamar a função do componente de novo, do início ao fim, com o novo valor de estado, e comparar o JSX resultante com o que estava na tela antes, atualizando só as partes que realmente mudaram (React não redesenha a tela inteira do zero a cada mudança, ele reconcilia).

Esse mecanismo tem três consequências práticas que valem a pena internalizar desde já, porque vão aparecer sem aviso o tutorial inteiro:

- **Só `useState`/`useReducer` disparam nova renderização.** Alterar uma variável comum (`let x = 5; x = 6;`) ou mutar um objeto/array existente não faz o React perceber nada — é por isso que a imutabilidade (Passo 4 da seção anterior) não é só estilo, é o que faz a tela realmente atualizar.
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
// components/CartaoDeRota.jsx
function CartaoDeRota({ rota, onPress }) {
  return (
    <TouchableOpacity onPress={onPress}>
      <Text>{rota.nome}</Text>
      <Text>{rota.distanciaEstimadaKm} km</Text>
    </TouchableOpacity>
  );
}
```

**Onde colocar cada componente**

Seguindo a convenção adotada desde o início deste tutorial: componentes usados por **uma tela só** ficam no próprio arquivo daquela tela, dentro de `app/`; componentes reutilizados por **mais de uma tela** (como `CartaoDeRota` acima, ou `AvisoPermissaoNegada`, que construímos no Tópico 4) vão para `components/`, com um arquivo por componente, nomeado igual ao componente (`AvisoPermissaoNegada.jsx` exporta `AvisoPermissaoNegada`). É a mesma lógica de organização que já aplicamos a hooks (`hooks/`, `utils/`) desde o início deste tutorial.

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

### Criando sua própria API (Node.js/Express) e autenticação com JWT

**Objetivo:** sair do lado de **consumir** uma API (tópico anterior) para o lado de **construir** uma — e proteger um endpoint dela com um token JWT, gerado e validado pelo próprio servidor.

Esse servidor é um **projeto separado** do app Expo — mesma linguagem (JavaScript), mas outro `package.json`, outra pasta, rodando no computador (ou, mais adiante, na nuvem), não no celular. O mesmo passo a passo funciona igual com Python (Flask/FastAPI) ou PHP (visto em detalhe no tutorial de PWIII) — Express foi escolhido aqui só por já estarmos em JavaScript.

**Passo 1 — criar o projeto do servidor**

```bash
mkdir servidor-rotas
cd servidor-rotas
npm init -y
npm install express cors jsonwebtoken
```

**Passo 2 — um endpoint que devolve as rotas planejadas**

Isso substitui o array `ROTAS_PLANEJADAS` fixo do Tópico 1 por dados vindos de um servidor de verdade:

```javascript
// servidor-rotas/index.js
const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const ROTAS_PLANEJADAS = [
  { id: '1', nome: 'Volta do parque', distanciaEstimadaKm: 3.2 },
  { id: '2', nome: 'Orla da praia', distanciaEstimadaKm: 5.8 },
];

app.get('/rotas', (req, res) => {
  res.json(ROTAS_PLANEJADAS);
});

app.listen(3000, () => console.log('Servidor rodando em http://localhost:3000'));
```

```bash
node index.js
```

**Passo 3 — o que é um JWT**

JWT (**J**SON **W**eb **T**oken) é o formato de token mais comum para autenticação em APIs — vale esclarecer o nome porque é fácil confundi-lo com siglas parecidas: não é "secure web token" nem nenhuma outra variação, é especificamente **JSON Web Token**. Um JWT é uma string com três partes separadas por ponto (`cabecalho.payload.assinatura`), cada uma em Base64: o `payload` carrega dados sobre quem está autenticado (aqui, o e-mail) e uma validade; a `assinatura` é gerada com uma chave secreta que só o servidor conhece, e é o que garante que ninguém alterou o conteúdo do token no caminho — qualquer alteração no payload invalida a assinatura, e o servidor rejeita o token.

**Passo 4 — gerar um JWT no login**

```javascript
// servidor-rotas/index.js — acrescente
const jwt = require('jsonwebtoken');

const SEGREDO = 'troque-isso-por-uma-variavel-de-ambiente-em-producao';

app.post('/login', (req, res) => {
  const { email, senha } = req.body;

  if (email === 'atleta@exemplo.com' && senha === '123456') {
    const token = jwt.sign({ email }, SEGREDO, { expiresIn: '1h' });
    return res.json({ token });
  }

  res.status(401).json({ erro: 'E-mail ou senha inválidos.' });
});
```

`jwt.sign(payload, segredo, opções)` monta e assina o token; `expiresIn: '1h'` já embute a validade no próprio payload, sem precisar de nenhuma lógica extra para expirar o token depois.

**Passo 5 — proteger um endpoint, exigindo o token**

```javascript
// servidor-rotas/index.js — acrescente
function exigirToken(req, res, next) {
  const cabecalho = req.headers.authorization; // formato esperado: "Bearer <token>"
  const token = cabecalho?.split(' ')[1];

  if (!token) {
    return res.status(401).json({ erro: 'Token não enviado.' });
  }

  try {
    req.usuario = jwt.verify(token, SEGREDO);
    next();
  } catch {
    res.status(401).json({ erro: 'Token inválido ou expirado.' });
  }
}

app.post('/corridas', exigirToken, (req, res) => {
  const { distancia, duracao } = req.body;
  res.status(201).json({ mensagem: `Corrida de ${req.usuario.email} salva.`, distancia, duracao });
});
```

`exigirToken` é um **middleware** — uma função que roda antes da rota de verdade (`app.post('/corridas', exigirToken, ...)`), e só chama `next()` (deixando a requisição seguir) se o token for válido. `jwt.verify` refaz a checagem de assinatura usando o mesmo `SEGREDO`; se o token foi alterado, expirou, ou nunca existiu, ele lança um erro — capturado pelo `catch`, que devolve `401` antes mesmo do código da rota `/corridas` rodar.

**Passo 6 — chamando o servidor a partir do app**

```javascript
async function fazerLogin(email, senha) {
  const resposta = await fetch('http://SEU_IP_LOCAL:3000/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, senha }),
  });
  const dados = await resposta.json();
  return dados.token;
}

async function salvarCorridaNoServidor(token, distancia, duracao) {
  await fetch('http://SEU_IP_LOCAL:3000/corridas', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ distancia, duracao }),
  });
}
```

**A pegadinha do `localhost` num celular físico:** `http://localhost:3000` funciona no navegador do computador (aponta para ele mesmo), mas **não funciona no celular** — ali, `localhost` aponta para o próprio celular, não para o computador rodando o servidor. Descubra o IP local do computador na mesma rede Wi-Fi (`ipconfig` no Windows, `ifconfig`/`ip addr` no Linux/macOS — algo como `192.168.0.x`) e use esse endereço no lugar de `SEU_IP_LOCAL`. Celular e computador precisam estar na **mesma rede Wi-Fi** para isso funcionar — a mesma exigência de rede que torna a opção `--tunnel` (seção de Ferramentas) útil quando essa condição não pode ser garantida.

**Exercícios:**
- **Fácil:** Rode `curl -X POST http://localhost:3000/login -H "Content-Type: application/json" -d '{"email":"atleta@exemplo.com","senha":"123456"}'` no terminal e confirme que um token volta na resposta.
- **Médio:** Tente chamar `POST /corridas` sem o header `Authorization` (via `curl`, sem o `-H`) e confirme que o servidor responde `401`.
- **Desafio:** Troque o array fixo `ROTAS_PLANEJADAS` do servidor por leitura/escrita num arquivo JSON local (com `fs.readFileSync`/`fs.writeFileSync`), para as rotas sobreviverem a um reinício do servidor.

**Pergunta para fixação:** por que alterar manualmente o `payload` de um JWT (por exemplo, trocando o e-mail dentro dele) não funciona para "enganar" o servidor, mesmo sendo só texto em Base64, fácil de decodificar e reescrever?

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
  const assinaturaRef = useRef(null); // vai guardar a assinatura do GPS, para poder cancelar depois
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

    assinaturaRef.current = await Location.watchPositionAsync(
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
    assinaturaRef.current?.remove();
    assinaturaRef.current = null;
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
export function obterMapaCorridaHtml(latitudeInicial, longitudeInicial) {
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

    window.adicionarPonto = function (lat, lon) {
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
import { obterMapaCorridaHtml } from '../utils/mapaHtml';

const REGIAO_INICIAL = { latitude: -23.5505, longitude: -46.6333 };

export function RaceMap({ pontos }) {
  const webviewRef = useRef(null);
  const [mapaPronto, setMapaPronto] = useState(false);

  // useState com função: obterMapaCorridaHtml só roda na primeira renderização,
  // não a cada re-render — senão o HTML seria recriado (e a WebView recarregada)
  // toda vez que "pontos" mudasse.
  const [htmlInicial] = useState(() =>
    obterMapaCorridaHtml(REGIAO_INICIAL.latitude, REGIAO_INICIAL.longitude)
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

Recarregar a `WebView` a cada ponto novo apagaria a linha desenhada até então. Em vez disso, injetamos JavaScript no mapa já carregado, chamando o `window.adicionarPonto` que definimos no Passo 4. Para isso, primeiro adicione mais uma ref no topo do componente, junto da `webviewRef` já existente — ela vai lembrar quantos pontos já foram enviados ao mapa:

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
        `window.adicionarPonto && window.adicionarPonto(${latitude}, ${longitude}); true;`
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
- **Desafio:** Adicione um botão "Reiniciar" que limpa `pontos` e também limpa a linha desenhada no mapa (dica: envie um `window.reiniciarRota()` parecido com o `window.adicionarPonto`, apagando `rota` e `marcador` dentro do HTML do Passo 4).

**Perguntas para fixação:**
1. Por que `obterMapaCorridaHtml` é chamado dentro de `useState(() => ...)`, e não direto como `useState(obterMapaCorridaHtml(...))`?
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

E exiba a imagem no `CartaoDeRota` (Tópico 1):

```javascript
// components/CartaoDeRota.jsx
import { Image } from 'expo-image';

function CartaoDeRota({ rota, onPress }) {
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

Para efeitos sonoros curtos (um apito ao iniciar a corrida, por exemplo), `expo-audio` expõe o hook `useAudioPlayer`, que recebe a origem do som (também aceita `require(...)` para um arquivo local) e devolve um tocador pronto para usar:

```javascript
// app/corrida.jsx — trecho
import { useAudioPlayer } from 'expo-audio';

const somDeApito = require('../assets/sons/apito.mp3');

export default function Corrida() {
  const tocador = useAudioPlayer(somDeApito);
  // ...
}
```

**Passo 4 — tocar o som ao iniciar e ao parar**

Adicione um arquivo de áudio curto em `assets/sons/apito.mp3` (qualquer efeito sonoro curto serve) e chame `tocador.play()` dentro das próprias funções `iniciar`/`parar` do `useTracking` (Tópico 5), ou diretamente na tela, envolvendo a chamada existente. O projeto de referência (seção "Projeto completo", mais adiante) usa um apito de verdade, recortado de uma gravação licenciada em CC BY-SA — veja `assets/sons/CREDITS.md` ali para a atribuição exigida pela licença caso você reaproveite o mesmo arquivo.

```javascript
// app/corrida.jsx
import { View, Button, Text } from 'react-native';
import { useAudioPlayer } from 'expo-audio';
import { useTracking } from '../hooks/useTracking';
import { RaceMap } from '../components/RaceMap';

const somDeApito = require('../assets/sons/apito.mp3');

export default function Corrida() {
  const { pontos, rastreando, erro, iniciar, parar } = useTracking();
  const tocador = useAudioPlayer(somDeApito);

  function iniciarComSom() {
    tocador.seekTo(0);
    tocador.play();
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

`tocador.seekTo(0)` garante que o som toque do início toda vez, mesmo que a corrida anterior tenha tocado o mesmo apito há pouco.

**Exercícios:**
- **Fácil:** Troque as URLs de `imagemUrl` por imagens de verdade (pode ser qualquer imagem hospedada publicamente), e confirme que aparecem nos cards da lista.
- **Médio:** Adicione um segundo som, tocado quando o usuário aperta "Parar", com um efeito sonoro diferente do de "Iniciar".
- **Desafio:** Troque a imagem remota do detalhe da rota (`app/rotas/[id].jsx`) por uma vinda de `require(...)`, empacotada localmente — compare o tempo de carregamento entre as duas abordagens, principalmente com a rede lenta.

**Pergunta para fixação:** por que `require('../assets/sons/apito.mp3')` funciona para referenciar um arquivo local, mas não funcionaria se o caminho dentro do `require` fosse construído dinamicamente (por exemplo, `require('../assets/sons/' + nomeDoArquivo)`)?

---

### Animações com Reanimated

**Objetivo:** animar a interface sem travar a tela — usando exatamente a ideia de *worklet*/thread de UI que fechou o tópico anterior.

```bash
npx expo install react-native-reanimated react-native-worklets
```

Nenhuma configuração extra é necessária — o plugin do Babel que o Reanimated precisa já vem configurado automaticamente pelo `babel-preset-expo` do projeto.

**Por que não animar só com `useState`**

Seria possível animar trocando um número em `useState` a cada quadro (60 vezes por segundo) e recalculando o estilo a cada renderização — mas cada `setState` agenda uma renderização inteira do componente na thread JS, exatamente o gargalo que vimos no Tópico de Concorrência. O Reanimated evita isso com o **shared value**: um valor que vive fora do ciclo normal de renderização do React, lido e escrito diretamente pela thread de UI, sem precisar re-renderizar o componente a cada mudança — é o mesmo tipo de *worklet* mencionado ali.

**Passo 1 — um fade-in no mapa, quando ele terminar de carregar**

O componente `RaceMap` (Tópico 5) já guarda `mapaPronto`, que vira `true` assim que o Leaflet termina de montar. Vamos usar esse mesmo estado para disparar um fade-in suave, em vez do mapa simplesmente aparecer de repente:

```javascript
// components/RaceMap.jsx — imports adicionais
import { useEffect } from 'react';
import Animated, { useAnimatedStyle, useSharedValue, withTiming } from 'react-native-reanimated';
```

```javascript
  // dentro do componente RaceMap, junto dos outros estados
  const opacidade = useSharedValue(0);

  useEffect(() => {
    if (mapaPronto) {
      opacidade.value = withTiming(1, { duration: 400 });
    }
  }, [mapaPronto]);

  const estiloAnimado = useAnimatedStyle(() => ({
    opacity: opacidade.value,
  }));
```

E troque o `return` para envolver a `WebView` num `Animated.View` que recebe o estilo animado:

```javascript
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
```

Repare que `estiloAnimado` é lido dentro de `useAnimatedStyle`, uma função especial do Reanimated — ela roda como *worklet*, na thread de UI, então mudar `opacidade.value` anima o mapa mesmo que a thread JS esteja ocupada com outra coisa (como o cálculo de distância do Tópico 3).

**Passo 2 — feedback ao tocar no botão de iniciar/parar**

`withSpring` cria uma animação com efeito de mola, útil para feedback de toque. Aplique um leve encolhimento ao `Button` de iniciar/parar (Tópico 5) usando `Pressable` no lugar dele, já que `Button` não permite estilo customizado:

```javascript
// app/corrida.jsx — trecho
import Animated, { useAnimatedStyle, useSharedValue, withSpring } from 'react-native-reanimated';
import { Pressable, Text } from 'react-native';

const escala = useSharedValue(1);

const estiloBotao = useAnimatedStyle(() => ({
  transform: [{ scale: escala.value }],
}));

<Pressable
  onPressIn={() => { escala.value = withSpring(0.92); }}
  onPressOut={() => { escala.value = withSpring(1); }}
  onPress={rastreando ? parar : iniciarComSom}
>
  <Animated.View style={estiloBotao}>
    <Text>{rastreando ? 'Parar' : 'Iniciar'}</Text>
  </Animated.View>
</Pressable>
```

**Exercícios:**
- **Fácil:** Troque a duração do fade-in do mapa para 1000ms e observe a diferença.
- **Médio:** Anime também a lista de rotas planejadas (Tópico 1) com um fade-in ao carregar a tela, usando o mesmo padrão de `useSharedValue`/`withTiming`.
- **Desafio:** Pesquise `withSequence` e `withRepeat` do Reanimated, e use-os para fazer o contador de pontos do trajeto (`pontos.length`, Tópico 5) pulsar brevemente toda vez que um novo ponto de GPS chegar.

**Pergunta para fixação:** por que animar `opacidade.value` dentro de `useAnimatedStyle` não causa uma nova renderização do componente `RaceMap` a cada quadro da animação, diferente de animar um valor guardado em `useState`?

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
    let assinatura;

    Accelerometer.isAvailableAsync().then((temSensor) => {
      if (cancelado) return;
      setDisponivel(temSensor);
      if (!temSensor) return;

      Accelerometer.setUpdateInterval(intervaloMs);
      assinatura = Accelerometer.addListener(setDados);
    });

    return () => {
      cancelado = true;
      assinatura?.remove();
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
    let assinatura;
    let cancelado = false;

    Accelerometer.isAvailableAsync().then((temSensor) => {
      if (cancelado) return;
      setDisponivel(temSensor);
      if (!temSensor) return;

      Accelerometer.setUpdateInterval(intervaloMs);
      assinatura = Accelerometer.addListener(setDados);
    });

    return () => {
      cancelado = true;
      assinatura?.remove();
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

**Passo 4 — vibração com `expo-haptics`**

```bash
npx expo install expo-haptics
```

Sacudir o celular durante a corrida não serve de nada se o app não confirmar que percebeu — quem está correndo não vai parar para olhar a tela. `expo-haptics` resolve isso com vibração tátil, e não pede permissão nenhuma para funcionar:

```javascript
import * as Haptics from 'expo-haptics';

// padrões prontos de sucesso / aviso / erro — o mesmo que o próprio sistema usa em outros apps
await Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);

// toque físico curto, para botões e outras interações diretas — de Light a Heavy
await Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
```

**Passo 5 — sacudir marca um ponto de interesse, com vibração confirmando**

Junte `useDetectorDeChacoalhada` e `Haptics.notificationAsync` na tela de rastreamento (Tópico 5), para que chacoalhar o celular durante a corrida marque um ponto de interesse no trajeto — só enquanto a corrida estiver de fato rodando:

```javascript
// app/corrida.jsx — acrescente aos imports já existentes
import { useState } from 'react';
import * as Haptics from 'expo-haptics';
import { useDetectorDeChacoalhada } from '../utils/useDetectorDeChacoalhada';
```

```javascript
// dentro do componente Corrida, junto dos outros hooks
const [pontosDeInteresse, setPontosDeInteresse] = useState([]);

useDetectorDeChacoalhada(() => {
  if (!rastreando) return;

  Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
  setPontosDeInteresse((anteriores) => [...anteriores, pontos.at(-1)]);
});
```

O `if (!rastreando) return;` no início do callback é o que impede que sacudir o celular no bolso, antes de iniciar a corrida, marque pontos à toa. Mostre a contagem na tela, junto do botão de iniciar/parar:

```javascript
<Text>Pontos de interesse: {pontosDeInteresse.length}</Text>
```

**Exercícios:**
- **Fácil:** Mostre `x`, `y`, `z` na tela em tempo real, arredondados para 2 casas decimais.
- **Médio:** Troque `Haptics.NotificationFeedbackType.Success` por `Haptics.ImpactFeedbackStyle.Heavy` (via `impactAsync`) e compare a sensação da vibração entre os dois.
- **Desafio:** Ajuste o `limiar` de `useDetectorDeChacoalhada` experimentalmente até encontrar um valor que detecte um chacoalhar intencional, mas ignore o balanço normal do celular no bolso durante a corrida.

**Pergunta para fixação:** por que `Haptics.notificationAsync`/`impactAsync` não exigem nenhum pedido de permissão, diferente de câmera, contatos ou localização?

---

### Chamando código nativo (Kotlin/Java) a partir do React Native

**Objetivo:** entender quando vale a pena escrever código nativo, e como o **Expo Modules API** conecta uma função Kotlin ao JavaScript do app.

> ✅ O módulo descrito abaixo foi gerado, prebuilado e **compilado de verdade** com `./gradlew :informacoes-do-dispositivo:compileDebugKotlin` (não precisa de Android Studio para esse passo — só do Android SDK e do JDK, que o próprio Gradle usa por trás). O único lado que este tutorial não cobre é iOS/Swift (deixado como Desafio) — esse, sim, exigiria um Mac com Xcode.

Todo módulo que usamos até aqui (`expo-location`, `expo-sqlite`, `expo-haptics`...) é, por baixo dos panos, código nativo (Kotlin no Android, Swift no iOS) com uma API em JavaScript por cima. A imensa maioria dos apps nunca precisa escrever esse código nativo diretamente — mas quando o app precisa de algo **muito específico**, sem nenhum módulo Expo ou biblioteca pronta que cubra (um SDK proprietário de terceiros que só existe em Kotlin, por exemplo), o caminho é escrever esse pedaço nativo você mesmo e expô-lo ao JavaScript.

**Passo 1 — criar um módulo local**

```bash
npx create-expo-module@latest --local informacoes-do-dispositivo
```

Esse comando pergunta, um de cada vez: o nome do módulo (`InformacoesDoDispositivo`), uma descrição, o nome do pacote Android (`expo.modules.informacoesdodispositivo`), autor, licença, versão inicial, quais plataformas (`android`, já que este tutorial não cobre a parte de iOS/Swift) e quais exemplos de recurso incluir no template (`Function` é o suficiente aqui). Para automatizar isso num script (ou repetir exatamente a mesma criação depois), os mesmos valores podem ser passados direto como flags, sem nenhum prompt interativo:

```bash
npx create-expo-module@latest --local \
  --name InformacoesDoDispositivo \
  --description "Leitura de informações do dispositivo via código nativo" \
  --package expo.modules.informacoesdodispositivo \
  --license MIT \
  --module-version 1.0.0 \
  --platform android \
  --features Function \
  --package-manager npm \
  informacoes-do-dispositivo
```

> ⚠️ **Pegadinha do caminho:** com `--local`, o último argumento (o caminho) já é interpretado **relativo à pasta `modules/`**, não à raiz do projeto — escrever `modules/informacoes-do-dispositivo` em vez de só `informacoes-do-dispositivo` (como nos dois comandos acima) cria o módulo em `modules/modules/informacoes-do-dispositivo`, uma pasta aninhada a mais. Confira a estrutura gerada com `ls modules/` antes de seguir para o próximo passo, e mova a pasta um nível acima se isso acontecer.

O resultado é uma pasta `modules/informacoes-do-dispositivo/`, com uma subpasta `android/` (Kotlin), um `expo-module.config.json` (é ele que diz ao Expo Modules API onde encontrar o módulo — sem esse arquivo, o autolinking do Passo 2 não o encontraria) e uma `src/` com o arquivo TypeScript que o resto do app importa (`InformacoesDoDispositivoModule.ts`). Diferente dos pacotes instalados via `npx expo install`, esse módulo mora **dentro do próprio projeto** — é código seu, não uma dependência de terceiros.

**Passo 2 — gerar as pastas nativas**

Antes de rodar `prebuild`, defina o identificador do app em `app.json` — o Android exige um pacote único mesmo antes de qualquer build de verdade (`android.package`, o mesmo campo revisitado no Tópico 10, de Publicação):

```json
// app.json — trecho
{
  "expo": {
    "android": {
      "package": "com.suaescola.appdecorrida"
    }
  }
}
```

```bash
npx expo prebuild --platform android
```

Managed workflow (o que usamos o tutorial inteiro) não tem pastas `android/`/`ios/` — o Expo as gera sob demanda. `prebuild` cria essas pastas nativas reais a partir da configuração do projeto (`app.json` e os módulos instalados, incluindo o que acabamos de criar no Passo 1) — e já inclui o autolinking do nosso módulo local, sem precisar registrá-lo manualmente em nenhum `settings.gradle` (confirme com `npx expo-modules-autolinking resolve -p android`, que deve listar `informacoes-do-dispositivo` entre os módulos resolvidos). A partir daqui, o projeto ainda funciona com `npx expo start`, mas testar o módulo nativo em si exige compilar essas pastas com Gradle (linha de comando) ou Android Studio — não roda mais só no Expo Go.

**Passo 3 — a função nativa em Kotlin**

Dentro de `modules/<nome-do-modulo>/android/src/main/java/.../<NomeDoModulo>Module.kt`, o template já gerado tem uma função `hello()` de exemplo. Trocando o nome e o corpo para algo do nosso app:

```kotlin
// modules/informacoes-do-dispositivo/android/src/main/java/expo/modules/informacoesdodispositivo/InformacoesDoDispositivoModule.kt
package expo.modules.informacoesdodispositivo

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class InformacoesDoDispositivoModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("InformacoesDoDispositivo")

    Function("nivelDeBateriaBruto") {
      // um exemplo simples: no Kotlin de verdade, isso viria de
      // android.os.BatteryManager — aqui só para ilustrar a ponte
      return@Function 87
    }
  }
}
```

`Name(...)` define como o módulo aparece do lado JavaScript; `Function(...)` expõe uma função síncrona chamável do JS (existe também `AsyncFunction` para algo que leva tempo, seguindo o mesmo padrão `Async`/Promise já visto o tutorial inteiro).

**Passo 4 — usar a função no lado JavaScript**

```javascript
// utils/informacoesDoDispositivo.js
import InformacoesDoDispositivo from '../modules/informacoes-do-dispositivo/src/InformacoesDoDispositivoModule';

export function lerNivelDeBateriaBruto() {
  return InformacoesDoDispositivo.nivelDeBateriaBruto();
}
```

Repare que o import aponta para o arquivo `.ts` dentro de `src/`, e não para a pasta do módulo diretamente — o `create-expo-module` não gera um arquivo de re-exportação (`index.ts`) por padrão; ele existe como opção (`--barrel`, no Passo 1), mas sem ela, quem importa o módulo precisa apontar para o arquivo certo dentro de `src/`.

Do ponto de vista de quem importa, `InformacoesDoDispositivo.nivelDeBateriaBruto()` não parece diferente de chamar qualquer outra função de um módulo Expo — e essa é a ideia: o Expo Modules API existe justamente para que módulos próprios pareçam, para o resto do app, tão naturais quanto os módulos oficiais.

**Passo 5 — compilar e testar**

Para desenvolver de verdade (com o app rodando e o Fast Refresh valendo para o lado JS), rode `npx expo run:android`, que compila as pastas nativas do Passo 2 e instala o app no emulador/celular conectado — substitui o `npx expo start` sempre que o projeto tiver módulos nativos próprios, já que o Expo Go genérico não contém código que não seja dele. Alternativamente, abra a pasta `android/` no Android Studio e rode por ali (botão "Run"). Para só **compilar** o módulo Kotlin isoladamente — sem instalar em nenhum aparelho, útil para confirmar que o código compila antes de testar no dispositivo — é possível chamar o Gradle diretamente pelo nome do módulo:

```bash
cd android
./gradlew :informacoes-do-dispositivo:compileDebugKotlin
```

De qualquer uma das três formas, qualquer mudança no arquivo `.kt` exige recompilar — o Fast Refresh do Metro não alcança código nativo, só JavaScript.

**Turbo Modules, para contexto:** existe também o caminho "Turbo Modules", da própria comunidade React Native (sem depender do pacote `expo`), preferível quando o módulo precisa de C++ para acesso de baixíssimo nível. Para a grande maioria dos casos — e para todo o restante deste tutorial — o Expo Modules API é a opção recomendada, por ter uma experiência de desenvolvimento mais simples.

**Exercícios:**
- **Fácil:** Rode `npx create-expo-module@latest --local` num projeto de teste e explore a estrutura de pastas gerada, sem alterar nada ainda.
- **Médio:** Troque o valor fixo `87` de `nivelDeBateriaBruto` por uma leitura real da bateria, usando a API `android.os.BatteryManager` do Android (pesquise o método `getIntProperty`).
- **Desafio:** Implemente a versão iOS da mesma função, em `modules/informacoes-do-dispositivo/ios/InformacoesDoDispositivoModule.swift`, usando `UIDevice.current.batteryLevel`.

**Pergunta para fixação:** por que salvar o arquivo `.kt` de um módulo nativo não aciona o Fast Refresh do Metro, diferente de salvar qualquer arquivo `.jsx` usado neste tutorial até aqui?

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

**Passo 5 — exportar o histórico para um arquivo, com `expo-file-system`**

`AsyncStorage` e `expo-sqlite` guardam dados que só o próprio app lê. Às vezes o objetivo é diferente: gerar um arquivo de verdade, que o usuário possa compartilhar ou abrir em outro programa — por exemplo, exportar o histórico de corridas como um `.json`.

```bash
npx expo install expo-file-system
```

```javascript
// utils/exportarHistorico.js
import { File, Paths } from 'expo-file-system';

export function exportarHistoricoComoJson(corridas) {
  const arquivo = new File(Paths.document, 'historico-corridas.json');
  arquivo.write(JSON.stringify(corridas, null, 2));
  return arquivo.uri; // caminho local do arquivo gerado
}
```

`Paths.document` aponta para a pasta de documentos do próprio app — privada, mas persistente entre uma abertura e outra (diferente de `Paths.cache`, que o sistema pode apagar para liberar espaço a qualquer momento). `arquivo.write(...)` grava o conteúdo de forma síncrona; para ler de volta, `arquivo.textSync()` devolve o texto salvo.

**Passo 6 — o limite real de "acessar dados do aparelho"**

Vale ser direto sobre um limite de plataforma, e não só de Expo: mesmo um app nativo puro (Passo anterior à parte) não consegue ler o **histórico de ligações** de um usuário sem passar por uma revisão extremamente restrita da Play Store (a permissão `READ_CALL_LOG` é classificada como de uso sensível, reservada a apps de identificação de chamadas/discador padrão) — e no iOS não existe, para nenhum app de terceiros, absolutamente nenhuma API pública para isso, sob nenhuma condição. Diferente de contatos (Tópico 4), câmera, localização (Tópico 5) e arquivos (Passo 5 acima), que são acessíveis com uma permissão comum, chamadas telefônicas ficam de fora por design — é uma categoria de dado que as duas plataformas tratam como sensível demais para conceder a um app comum, ainda que tecnicamente pedir a permissão fosse possível no Android.

**Exercícios:**
- **Fácil:** Liste as corridas salvas em uma `FlatList`, mostrando data e distância de cada uma.
- **Médio:** Ao terminar uma corrida na tela do Tópico 5, chame `salvarCorrida` com a distância total e a duração, e confira se ela aparece em `app/historico.jsx`.
- **Médio:** Adicione um botão "Exportar" em `app/historico.jsx` que chama `exportarHistoricoComoJson(corridas)` e mostra o `uri` retornado na tela.
- **Desafio:** Adicione um botão para apagar uma corrida do histórico (`DELETE FROM corridas WHERE id = ?`), atualizando a lista depois.
- **Desafio:** Instale `expo-sharing` e use `Sharing.shareAsync(uri)` para abrir o menu de compartilhamento do sistema com o arquivo exportado no Passo 5, em vez de só mostrar o caminho na tela.

---

### Gráficos — em tempo real e estáticos

**Objetivo:** visualizar dados numéricos como gráfico, tanto atualizando sozinho durante a corrida quanto de forma parada no histórico.

```bash
npx expo install react-native-gifted-charts expo-linear-gradient react-native-svg
```

**Passo 1 — um gráfico de linha que atualiza sozinho: velocidade durante a corrida**

Cada ponto de GPS que `useTracking` (Tópico 5) já coleta vem com mais informação do que usamos até agora: além de `latitude`/`longitude`, o objeto `coords` devolvido pelo `expo-location` traz `speed` — a velocidade instantânea do aparelho, em metros por segundo, medida pelo próprio GPS. Como `pontos` já guarda o `coords` inteiro (`setPontos((anteriores) => [...anteriores, posicao.coords])`, no Tópico 5), essa informação já está disponível, sem precisar calcular nada:

```javascript
// app/corrida.jsx — trecho
import { LineChart } from 'react-native-gifted-charts';

const dadosDeVelocidade = pontos.map((ponto) => ({
  value: Math.round((ponto.speed ?? 0) * 3.6), // m/s para km/h
}));
```

```javascript
<LineChart data={dadosDeVelocidade} />
```

Como `dadosDeVelocidade` é recalculado a cada renderização, e `pontos` cresce a cada nova leitura do GPS (a cada poucos segundos, conforme configurado em `useTracking`), o gráfico se atualiza sozinho conforme a corrida avança — sem nenhum código extra de "tempo real": é a mesma reatividade de sempre, aplicada a mais um lugar.

**Passo 2 — um gráfico estático: distância das últimas corridas**

Na tela de histórico (Tópico 7), as corridas já estão carregadas em `corridas` (via `getAllAsync`). Um gráfico de barras compara a distância de cada uma, de uma vez só — sem precisar atualizar sozinho, já que o histórico só muda quando uma corrida nova é salva:

```javascript
// app/historico.jsx — trecho
import { BarChart } from 'react-native-gifted-charts';

const dadosDeDistancia = corridas.map((corrida) => ({
  value: Math.round(corrida.distancia),
  label: new Date(corrida.data).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }),
}));
```

```javascript
<BarChart data={dadosDeDistancia} />
```

**Exercícios:**
- **Fácil:** Adicione a prop `areaChart` ao `LineChart` do Passo 1, e compare visualmente com a versão sem ela.
- **Médio:** Limite `dadosDeVelocidade` aos últimos 30 pontos (`pontos.slice(-30)`) antes de montar o array, para o gráfico não ficar cada vez mais apertado conforme a corrida for ficando longa.
- **Desafio:** No `BarChart` do histórico, adicione uma cor diferente para a barra da corrida mais longa (dica: compare `corrida.distancia` com `Math.max(...corridas.map((c) => c.distancia))` e passe uma prop de cor por item, se a versão instalada da biblioteca suportar).

**Pergunta para fixação:** por que o gráfico de velocidade do Passo 1 não precisa de nenhum `setInterval` ou lógica de "atualizar a cada X segundos" para funcionar em tempo real?

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

**Por que não dá para "escutar" SMS ou mensagens de WhatsApp**

É comum perguntar: já que o app pode receber uma notificação push de um servidor, por que não simplesmente "escutar" quando chega um SMS ou uma mensagem de WhatsApp, em vez de depender de notificação? A resposta é uma restrição de plataforma, não uma limitação do Expo:

- **SMS, no Android:** ler o conteúdo de SMS recebidos (`RECEIVE_SMS`/`READ_SMS`) é uma permissão que o Google classifica como *restrita* — só apps que são o **aplicativo de SMS padrão** do aparelho podem pedi-la e ainda assim passar pela revisão da Play Store. Um app de corrida jamais se qualificaria para isso, e nenhum módulo do Expo expõe esse acesso, exatamente por essa restrição valer para qualquer app fora desse caso de uso. No iOS, não existe **nenhuma** API pública para ler SMS de terceiros, em hipótese alguma.
- **WhatsApp:** não existe nenhuma API pública, do WhatsApp ou do sistema operacional, que permita a um outro app ler mensagens do WhatsApp. A única forma tecnicamente possível de fazer algo parecido (um *Accessibility Service* lendo o conteúdo da tela) viola os termos de uso tanto do Android quanto do WhatsApp, e apps que tentam isso são banidos das lojas quando descobertos.

É exatamente por essas portas estarem fechadas — de propósito, por motivos de privacidade — que "meu servidor manda uma notificação push" (Passos 1 a 5 deste tópico) é o único caminho real para avisar o usuário de algo que aconteceu longe do app, e não um substituto de segunda escolha.

---

### 9. Gerenciamento de Estado (Context API, Redux)

**Objetivo:** compartilhar o estado da corrida entre telas diferentes (mapa, histórico, resumo) sem precisar repassar props manualmente por vários níveis de componentes.

**Três termos que vamos usar o tempo todo neste tópico**

- **Context** (contexto) é um canal de dados do React que qualquer componente, em qualquer profundidade da árvore, pode ler diretamente — sem que os componentes no meio do caminho precisem saber que aquele dado existe. É criado com `createContext` e lido com `useContext`.
- **Provider** (provedor) é o componente que "alimenta" um contexto com um valor — tudo que estiver dentro dele (em qualquer profundidade) enxerga aquele valor. Todo contexto vem com seu próprio Provider (`AlgumContext.Provider`); ao longo deste tutorial, empacotamos esse Provider numa função própria (`CorridaProvider`, no Passo 2) só para deixar o uso mais legível.
- **Reducer** é uma função pura — sem efeitos colaterais, sem chamadas de rede, sem mexer em nada fora dela — que recebe o estado atual mais uma **ação** (um objeto descrevendo o que aconteceu) e devolve o **novo** estado, sem nunca alterar o antigo. É a mesma ideia de imutabilidade da seção de Fundamentos de JavaScript, só que centralizada num único lugar: em vez de espalhar `setAlgumaCoisa` por vários componentes, toda mudança de estado passa por essa função só, o que torna mais fácil auditar e testar. `useReducer` é o hook que liga um reducer a um componente, do mesmo jeito que `useState` liga um valor simples.

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

### Autenticação e Proteção de Telas

**Objetivo:** adicionar login ao app, guardando o token de acesso com segurança, e proteger as telas de corrida para que só um usuário autenticado consiga abri-las.

**Autenticar** é confirmar quem o usuário é — normalmente pedindo e-mail/senha e recebendo de volta um **token**, um código que passa a representar aquele usuário logado em cada requisição seguinte, sem precisar enviar a senha de novo. **Proteger uma tela** é a etapa seguinte: decidir, antes mesmo de desenhar aquela tela, se o usuário atual tem um token válido — e mandá-lo para o login caso não tenha.

**Passo 1 — instale o `expo-secure-store`**

```bash
npx expo install expo-secure-store
```

O `AsyncStorage`, usado no Tópico 7 para a unidade de distância preferida, guarda os dados em texto puro — aceitável para uma preferência sem valor nenhum se vazada, mas não para um token de acesso. `expo-secure-store` guarda a informação criptografada, usando o Keychain do iOS ou o Keystore do Android por baixo dos panos — por isso é a escolha certa especificamente para credenciais e tokens.

**Passo 2 — um utilitário de armazenamento seguro, com um detalhe para a Web**

`expo-secure-store` não existe na Web (não há Keychain/Keystore em navegador) — por isso, sempre que o app também rodar na Web, é preciso desviar para outra forma de guardar o token ali, como o `localStorage` do próprio navegador:

```javascript
// utils/armazenamentoSeguro.js
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

const CHAVE_TOKEN = 'corrida_token';

export async function salvarTokenSeguro(token) {
  if (Platform.OS === 'web') {
    localStorage.setItem(CHAVE_TOKEN, token);
    return;
  }
  await SecureStore.setItemAsync(CHAVE_TOKEN, token);
}

export async function lerTokenSeguro() {
  if (Platform.OS === 'web') {
    return localStorage.getItem(CHAVE_TOKEN);
  }
  return SecureStore.getItemAsync(CHAVE_TOKEN);
}

export async function apagarTokenSeguro() {
  if (Platform.OS === 'web') {
    localStorage.removeItem(CHAVE_TOKEN);
    return;
  }
  await SecureStore.deleteItemAsync(CHAVE_TOKEN);
}
```

**Passo 3 — o `AuthContext`, no mesmo padrão do Tópico 9**

Assim como `CorridaContext` compartilha o estado da corrida com qualquer tela, `AuthContext` compartilha o token e as funções de entrar/sair — sem precisar passá-los por prop:

```javascript
// context/AuthContext.jsx
import { createContext, useContext, useEffect, useState } from 'react';
import { lerTokenSeguro, salvarTokenSeguro, apagarTokenSeguro } from '../utils/armazenamentoSeguro';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [carregando, setCarregando] = useState(true);

  // ao abrir o app, verifica se já existe um token salvo de uma sessão anterior
  useEffect(() => {
    lerTokenSeguro().then((tokenSalvo) => {
      setToken(tokenSalvo);
      setCarregando(false);
    });
  }, []);

  async function entrar(email, senha) {
    // Usuário de teste local — não depende do reqres.in nem de internet,
    // útil para demonstrar o app offline ou em sala de aula.
    if (email === 'teste@teste.com' && senha === '1234') {
      const tokenLocal = 'token-de-teste-local';
      await salvarTokenSeguro(tokenLocal);
      setToken(tokenLocal);
      return;
    }

    const resposta = await fetch('https://reqres.in/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password: senha }),
    });
    const dados = await resposta.json();

    if (!resposta.ok) {
      throw new Error(dados.error ?? 'Falha no login.');
    }

    await salvarTokenSeguro(dados.token);
    setToken(dados.token);
  }

  async function sair() {
    await apagarTokenSeguro();
    setToken(null);
  }

  return (
    <AuthContext.Provider value={{ token, carregando, entrar, sair }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
```

`reqres.in` é uma API pública gratuita, feita justamente para testar telas de login — `eve.holt@reqres.in`/`cityslicka` é uma das combinações de teste documentadas por ela, e devolve um token de verdade. Como é uma API de terceiros, ela só reconhece os e-mails de teste que ela mesma documenta — não dá para "cadastrar" nenhum e-mail novo nela. Por isso o atalho local no início de `entrar`: `teste@teste.com`/`1234` nunca chega a fazer uma requisição de rede, e funciona mesmo sem internet — útil sempre que testar o fluxo de login não for o ponto principal (por exemplo, ao demonstrar as telas depois do login para a turma). Note que `entrar` **lança** um erro (`throw`) em vez de tratá-lo ali dentro — quem chama `entrar` (a tela de login, no Passo 5) é quem decide como mostrar esse erro para o usuário.

**Passo 4 — a tela de login**

```javascript
// app/login.jsx
import { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { entrar } = useAuth();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);

  async function aoEntrar() {
    setErro(null);
    setCarregando(true);
    try {
      await entrar(email, senha);
    } catch (erroDeLogin) {
      setErro(erroDeLogin.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>Entrar</Text>
      <TextInput style={styles.input} placeholder="E-mail" value={email} onChangeText={setEmail} autoCapitalize="none" />
      <TextInput style={styles.input} placeholder="Senha" value={senha} onChangeText={setSenha} secureTextEntry />
      <Button title={carregando ? 'Entrando...' : 'Entrar'} onPress={() => aoEntrar()} disabled={carregando} />
      {erro && <Text style={styles.erro}>{erro}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12, justifyContent: 'center' },
  titulo: { fontSize: 24, fontWeight: 'bold' },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 8, padding: 10 },
  erro: { color: '#c62828' },
});
```

Teste com `eve.holt@reqres.in` / `cityslicka` — e depois com uma senha qualquer errada, para confirmar que a mensagem de erro aparece.

**Passo 5 — agrupe as telas que exigem login**

O Expo Router protege rotas **em grupo**, não tela por tela. Crie uma pasta `app/(app)/` — o nome entre parênteses agrupa rotas sem aparecer na URL, o mesmo mecanismo já usado em `app/(tabs)/` no Tópico 1 — e mova para dentro dela **todas as telas já existentes, exceto `app/login.jsx`**: `index.jsx`, `cep.jsx`, `corrida.jsx`, `historico.jsx`, `compartilhar.jsx`, `concorrencia.jsx` e a pasta `rotas/` inteira. Como o nome do grupo não entra na URL, nenhuma chamada a `router.push` feita em tópicos anteriores precisa mudar — `router.push('/corrida')` continua abrindo `app/(app)/corrida.jsx` normalmente.

O grupo precisa do próprio `_layout.jsx`, com um `Stack` normal:

```javascript
// app/(app)/_layout.jsx
import { Stack } from 'expo-router';

export default function AppLayout() {
  return <Stack />;
}
```

**Passo 6 — proteja o grupo no layout raiz, com `Stack.Protected`**

`Stack.Protected` decide, com base em uma condição (`guard`), se um `Stack.Screen` fica disponível para navegação ou não — se o usuário estiver em uma tela que deixou de estar disponível, o Expo Router já redireciona sozinho para a primeira tela disponível:

```javascript
// app/_layout.jsx
import { Stack } from 'expo-router';
import { QueryClientProvider, QueryClient } from '@tanstack/react-query';
import * as Notifications from 'expo-notifications';
import { AuthProvider, useAuth } from '../context/AuthContext';
import { CorridaProvider } from '../context/CorridaContext';

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

const queryClient = new QueryClient();

function NavegacaoRaiz() {
  const { token, carregando } = useAuth();

  if (carregando) return null; // ainda checando se já existe um token salvo

  return (
    <Stack>
      <Stack.Protected guard={!!token}>
        <Stack.Screen name="(app)" options={{ headerShown: false }} />
      </Stack.Protected>

      <Stack.Protected guard={!token}>
        <Stack.Screen name="login" />
      </Stack.Protected>
    </Stack>
  );
}

export default function RootLayout() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <CorridaProvider>
          <NavegacaoRaiz />
        </CorridaProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}
```

Repare na ordem dos dois `Stack.Protected`: o primeiro só fica disponível com `token` preenchido (usuário logado); o segundo, só sem `token` (usuário deslogado) — nunca os dois ao mesmo tempo. Assim que `entrar()` preenche o `token` (Passo 3), o grupo `(app)` passa a existir e o Expo Router já leva o usuário para lá sozinho, sem nenhum `router.push` manual saindo da tela de login.

**Passo 7 — um botão de sair**

Adicione, em `app/(app)/index.jsx`, um botão que chama `sair()` do `AuthContext` — assim que o `token` for apagado, `Stack.Protected` reage e devolve o usuário para o login automaticamente:

```javascript
import { useAuth } from '../../context/AuthContext';

// dentro do componente da tela
const { sair } = useAuth();

<Button title="Sair" onPress={() => sair()} />
```

**Exercícios:**
- **Fácil:** Tente digitar uma senha errada na tela de login e confirme que a mensagem de erro aparece, sem o app travar nem navegar para lugar nenhum.
- **Médio:** Depois de fazer login, feche o app completamente (não só minimizar) e abra de novo — confirme que ele volta direto para dentro de `(app)`, sem pedir login de novo, graças ao `token` salvo no Passo 1.
- **Desafio:** Troque a tela de "Carregando..." (`if (carregando) return null`) por um indicador visual de verdade, usando `ActivityIndicator` do `react-native`.

**Perguntas para fixação:**
1. Por que o token fica em `expo-secure-store`, e não em `AsyncStorage`, como a unidade de distância preferida do Tópico 7?
2. Se o usuário apertar "Sair" enquanto estiver na tela `corrida`, o que faz o Expo Router tirá-lo de lá e mandá-lo para o login, já que nenhum código chamou `router.push('/login')` manualmente?

---

### Menu de navegação (Drawer) — acesso a todas as telas

**Objetivo:** trocar o `<Stack />` simples do grupo `(app)` (Autenticação, Passo 5) por um menu hambúrguer que dá acesso direto a qualquer tela do app, de qualquer lugar — em vez de depender só de botões espalhados por cada tela.

Lá no Tópico 1 (Componentes e Navegação), a seção "Outros tipos de layout" já mencionou o `<Drawer />` de passagem, ao lado de `<Stack />` e `<Tabs />`, mas nenhum dos dois grupos de rotas usados até aqui precisava de um menu lateral. Agora que o app já tem sete telas dentro de `(app)` (`index`, `rotas`, `corrida`, `historico`, `cep`, `compartilhar`, `concorrencia`), navegar entre elas só por botão a botão (como o "Ver rotas planejadas" do Tópico 1) fica limitado — daí o Drawer.

**Passo 1 — confirme as dependências**

A partir do SDK 56, o Drawer já vem embutido no próprio `expo-router` (antes disso, era preciso instalar `@react-navigation/drawer` à parte). Ele ainda depende de `react-native-reanimated`, `react-native-worklets` e `react-native-gesture-handler` para animar a abertura/fechamento — os dois primeiros já foram instalados no tópico de Animações; confirme que os três estão presentes:

```bash
npx expo install react-native-reanimated react-native-worklets react-native-gesture-handler
```

**Passo 2 — troque o `Stack` do grupo `(app)` por um `Drawer`**

```javascript
// app/(app)/_layout.jsx
import { Drawer } from 'expo-router/drawer';

export default function AppLayout() {
  return (
    <Drawer>
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
```

Cada `Drawer.Screen` corresponde a um arquivo (ou pasta) direto dentro de `app/(app)/`, do mesmo jeito que `Stack.Screen` já funcionava — `name` precisa bater exatamente com o nome do arquivo/pasta. Rode o app e repare: o cabeçalho de cada tela agora ganhou, de graça, um ícone de menu (☰) no canto superior esquerdo — o próprio Drawer já cuida de mostrá-lo e de ligá-lo ao gesto de abrir o menu, sem escrever nada a mais para isso.

**Passo 3 — o caso de `rotas`, que já é um Stack por dentro**

`rotas/` não é uma tela só, é uma pasta com duas (`index.jsx` e `[id].jsx`, do Tópico 1) — sem tratamento especial, o Drawer trataria as duas como entradas soltas e desconectadas. A correção é dar a essa pasta o próprio `_layout.jsx`, com um `Stack` por dentro, exatamente como fizemos para `(app)/` lá na Autenticação:

```javascript
// app/(app)/rotas/_layout.jsx
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
```

Repare em dois detalhes que se conectam com o Passo 2: o `Drawer.Screen name="rotas"` ganhou `headerShown: false` — porque, sem isso, a tela teria **dois cabeçalhos empilhados** (um do Drawer, outro deste Stack). E só a tela `index` (a primeira do Stack) recebe `headerLeft` com o `DrawerToggleButton` manualmente — a tela `[id]` já ganha sozinha uma seta de "voltar" (por ter uma tela anterior na pilha), e sobrescrever `headerLeft` ali apagaria essa seta, trocando-a pelo ícone de menu, que faria menos sentido numa tela de detalhe.

**Passo 4 — um botão "Sair" dentro do próprio menu**

O Drawer aceita um `drawerContent` customizado — sem ele, o menu já lista as telas do Passo 2 automaticamente, mas nada impede de acrescentar itens que não são telas, como o logout do `AuthContext` (Autenticação, Passo 3):

```javascript
// app/(app)/_layout.jsx — acrescente aos imports
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
```

`DrawerItemList` desenha a lista padrão (os `Drawer.Screen` do Passo 2); `DrawerItem` acrescenta uma linha extra, com a mesma aparência, mas sem levar a rota nenhuma — só dispara `sair()`. Ligue esse componente ao Drawer:

```javascript
// app/(app)/_layout.jsx — troque a linha do Drawer
<Drawer drawerContent={(props) => <ConteudoDoMenu {...props} />}>
```

Toque em "Sair" no menu e confirme que o mesmo redirecionamento automático do `Stack.Protected` (Autenticação, Passo 6) acontece aqui — o Drawer não precisa saber nada sobre autenticação para isso funcionar, porque a proteção continua sendo feita uma camada acima, no `app/_layout.jsx` raiz.

**Exercícios:**
- **Fácil:** Troque o ícone de cada `Drawer.Screen` usando a opção `drawerIcon`, que recebe uma função `({ color, size }) => <AlgumIcone ... />` (dá para usar `@expo/vector-icons`, já incluso no template padrão do Expo).
- **Médio:** Adicione um cabeçalho customizado acima da lista de telas em `ConteudoDoMenu` (por exemplo, o e-mail do usuário logado, lido de algum lugar do `AuthContext`).
- **Desafio:** Configure `screenOptions={{ drawerType: 'permanent' }}` no `Drawer` e observe a diferença de comportamento numa tela larga (ou no navegador, via `npx expo start --web`) — pesquise por que esse tipo de Drawer é comum em apps para tablet/desktop, mas raro em telas de celular.

**Pergunta para fixação:** por que a tela `rotas/[id]` não precisou de nenhum ajuste no Passo 3 além do `title`, enquanto `rotas/index` precisou do `headerLeft` manual?

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

### Projeto completo — todos os arquivos juntos

Este é o app de corrida montado por inteiro, juntando o resultado final de cada tópico anterior num único projeto — cada arquivo abaixo traz um comentário no topo apontando de qual(is) tópico(s) ele veio, para servir de referência rápida caso algum trecho pareça desconectado do resto. É o mesmo código já apresentado ao longo do tutorial, sem nenhuma novidade — só organizado junto, como ficaria numa cópia real do projeto.

```
pamii-projetos/
├── app/
│   ├── _layout.jsx
│   ├── login.jsx
│   └── (app)/
│       ├── _layout.jsx
│       ├── index.jsx
│       ├── cep.jsx
│       ├── corrida.jsx
│       ├── historico.jsx
│       ├── compartilhar.jsx
│       ├── concorrencia.jsx
│       └── rotas/
│           ├── _layout.jsx
│           ├── index.jsx
│           └── [id].jsx
├── components/
│   ├── CartaoDeRota.jsx
│   ├── AvisoPermissaoNegada.jsx
│   └── RaceMap.jsx
├── context/
│   ├── AuthContext.jsx
│   └── CorridaContext.jsx
├── hooks/
│   └── useTracking.js
├── utils/
│   ├── calculos.js
│   ├── mapaHtml.js
│   ├── armazenamentoSeguro.js
│   ├── exportarHistorico.js
│   ├── useAcelerometro.js
│   ├── useDetectorDeChacoalhada.js
│   └── informacoesDoDispositivo.js
├── modules/
│   └── informacoes-do-dispositivo/
│       ├── expo-module.config.json
│       ├── src/InformacoesDoDispositivoModule.ts
│       └── android/.../InformacoesDoDispositivoModule.kt
├── assets/
│   ├── sons/
│   │   ├── apito.mp3
│   │   └── CREDITS.md      ← atribuição do som (CC BY-SA)
│   └── images/
│       ├── icon.png                     ← ícone genérico (iOS/web)
│       ├── android-icon-foreground.png  ← camada de frente do ícone adaptativo
│       ├── android-icon-background.png  ← camada de fundo do ícone adaptativo
│       ├── android-icon-monochrome.png  ← versão monocromática (Android 13+)
│       ├── splash-icon.png              ← imagem da splash screen
│       └── favicon.png                  ← ícone da versão web
├── app.json                 ← nome/ícone/splash/plugins (ver "Personalizando o ícone e a tela de abertura")
└── servidor-rotas/          ← projeto Node separado, fora do app Expo
    ├── package.json
    └── index.js
```

**`app/_layout.jsx`** — layout raiz. Vem do Tópico 1 (`<Stack />` inicial), depois reescrito na Autenticação, Passo 6 (`Stack.Protected`, `AuthProvider`, `CorridaProvider`), com o registro de notificações do Tópico 8, Passo 2.

```javascript
// app/_layout.jsx
// Origem: Tópico 1 (Passo 1, layout básico) + Tópico 8 (Passo 2, handler de notificação)
//         + Autenticação e Proteção de Telas (Passo 6, Stack.Protected + providers)
import { Stack } from 'expo-router';
import { QueryClientProvider, QueryClient } from '@tanstack/react-query';
import * as Notifications from 'expo-notifications';
import { AuthProvider, useAuth } from '../context/AuthContext';
import { CorridaProvider } from '../context/CorridaContext';

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

const queryClient = new QueryClient();

function NavegacaoRaiz() {
  const { token, carregando } = useAuth();

  if (carregando) return null; // ainda checando se já existe um token salvo

  return (
    <Stack>
      <Stack.Protected guard={!!token}>
        <Stack.Screen name="(app)" options={{ headerShown: false }} />
      </Stack.Protected>

      <Stack.Protected guard={!token}>
        <Stack.Screen name="login" />
      </Stack.Protected>
    </Stack>
  );
}

export default function RootLayout() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <CorridaProvider>
          <NavegacaoRaiz />
        </CorridaProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}
```

**`app/(app)/_layout.jsx`** — layout do grupo protegido. Origem: Autenticação, Passo 5 (base) + Menu de navegação (Drawer), Passos 2 e 4 (o Drawer em si e o botão "Sair" no menu).

```javascript
// app/(app)/_layout.jsx
// Origem: Autenticação e Proteção de Telas (Passo 5, base)
//         + Menu de navegação (Drawer) — acesso a todas as telas (Passos 2 e 4)
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
```

**`app/(app)/rotas/_layout.jsx`** — Origem: Menu de navegação (Drawer), Passo 3.

```javascript
// app/(app)/rotas/_layout.jsx
// Origem: Menu de navegação (Drawer) — acesso a todas as telas (Passo 3)
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
```

**`app/login.jsx`** — Origem: Autenticação, Passo 4.

```javascript
// app/login.jsx
// Origem: Autenticação e Proteção de Telas (Passo 4)
import { useState } from 'react';
import { View, TextInput, Button, Text, StyleSheet } from 'react-native';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { entrar } = useAuth();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);

  async function aoEntrar() {
    setErro(null);
    setCarregando(true);
    try {
      await entrar(email, senha);
    } catch (erroDeLogin) {
      setErro(erroDeLogin.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>Entrar</Text>
      <TextInput style={styles.input} placeholder="E-mail" value={email} onChangeText={setEmail} autoCapitalize="none" />
      <TextInput style={styles.input} placeholder="Senha" value={senha} onChangeText={setSenha} secureTextEntry />
      <Button title={carregando ? 'Entrando...' : 'Entrar'} onPress={() => aoEntrar()} disabled={carregando} />
      {erro && <Text style={styles.erro}>{erro}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12, justifyContent: 'center' },
  titulo: { fontSize: 24, fontWeight: 'bold' },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 8, padding: 10 },
  erro: { color: '#c62828' },
});
```

**`app/(app)/index.jsx`** — tela inicial. Origem: Fundamentos de JavaScript, Passo 5 (lista de favoritos) + Autenticação, Passo 7 (botão "Sair"), mais um link para a lista de rotas (Tópico 1) para conectar as duas telas.

```javascript
// app/(app)/index.jsx
// Origem: Fundamentos de JavaScript para React (Passo 5, lista de favoritos)
//         + 1. Componentes e Navegação (link para /rotas)
//         + Autenticação e Proteção de Telas (Passo 7, botão Sair)
import { useState } from 'react';
import { StyleSheet, Text, View, Button, FlatList, Platform } from 'react-native';
import { useRouter } from 'expo-router';
import { useAuth } from '../../context/AuthContext';

export default function Inicio() {
  const router = useRouter();
  const { sair } = useAuth();
  const [favoritos, setFavoritos] = useState([]);
  let proximoId = favoritos.length + 1;

  function adicionarFavorito() {
    setFavoritos((anteriores) => [...anteriores, { id: String(proximoId), nome: `Rota ${proximoId}` }]);
  }

  return (
    <View style={styles.container}>
      <Text style={styles.titulo}>App de Corrida</Text>
      <Button title="Ver rotas planejadas" onPress={() => router.push('/rotas')} />
      <Button title="Adicionar rota favorita" onPress={() => adicionarFavorito()} />
      <FlatList
        data={favoritos}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => <Text>{item.nome}</Text>}
      />
      <Button title="Sair" onPress={() => sair()} />
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

**`app/(app)/cep.jsx`** — Origem: Promises, async/await, Passo 4.

```javascript
// app/(app)/cep.jsx
// Origem: Promises, async/await e funções assíncronas (Passo 4)
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

**`app/(app)/concorrencia.jsx`** — Origem: 3. Concorrência e Threads, Passos 2-3.

```javascript
// app/(app)/concorrencia.jsx
// Origem: 3. Concorrência e Threads (Passos 2 e 3)
import { useState } from 'react';
import { View, Button, Text, StyleSheet } from 'react-native';
import { distanciaTotal, distanciaTotalEmLotes } from '../../utils/calculos';

function gerarPontosDeTeste(quantidade) {
  const pontos = [];
  for (let i = 0; i < quantidade; i++) {
    pontos.push({ latitude: -23.55 + i * 0.00001, longitude: -46.63 + i * 0.00001 });
  }
  return pontos;
}

export default function Concorrencia() {
  const [resultado, setResultado] = useState(null);

  function rodarBloqueante() {
    const pontos = gerarPontosDeTeste(2_000_000);
    const metros = distanciaTotal(pontos);
    setResultado(metros);
  }

  function rodarEmLotes() {
    const pontos = gerarPontosDeTeste(2_000_000);
    distanciaTotalEmLotes(pontos, (metros) => setResultado(metros));
  }

  return (
    <View style={styles.container}>
      <Button title="Rodar bloqueante" onPress={rodarBloqueante} />
      <Button title="Rodar em lotes" onPress={rodarEmLotes} />
      {resultado != null && <Text>{Math.round(resultado)} metros</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 12, justifyContent: 'center' },
});
```

**`app/(app)/compartilhar.jsx`** — Origem: 4. Permissões Avançadas, Passos 1-5.

```javascript
// app/(app)/compartilhar.jsx
// Origem: 4. Permissões Avançadas (Passos 1 a 5)
import { useState } from 'react';
import { View, Button, Text, FlatList, StyleSheet } from 'react-native';
import * as Contacts from 'expo-contacts/legacy';
import AvisoPermissaoNegada from '../../components/AvisoPermissaoNegada';

export default function Compartilhar() {
  const [contatos, setContatos] = useState([]);
  const [status, setStatus] = useState(null);
  const [podePedirNovamente, setPodePedirNovamente] = useState(true);
  const [carregando, setCarregando] = useState(false);

  async function buscarContatos() {
    setCarregando(true);
    try {
      const permissao = await Contacts.requestPermissionsAsync();
      setStatus(permissao.status);
      setPodePedirNovamente(permissao.canAskAgain);

      if (permissao.status !== 'granted') return;

      const { data } = await Contacts.getContactsAsync({
        fields: [Contacts.Fields.PhoneNumbers],
      });
      setContatos(data.filter((c) => c.phoneNumbers?.length > 0));
    } finally {
      setCarregando(false);
    }
  }

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

**`app/(app)/rotas/index.jsx`** — Origem: 1. Componentes e Navegação, Passo 2 + Mídia, Passo 2 (`imagemUrl`).

```javascript
// app/(app)/rotas/index.jsx
// Origem: 1. Componentes e Navegação no Framework (Passo 2)
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
```

> Nota: o servidor construído em "Criando sua própria API (Node.js/Express) e autenticação com JWT" expõe esse mesmo array via `GET /rotas` — trocar `ROTAS_PLANEJADAS` por um `useQuery` consumindo esse endpoint (padrão do Tópico 2) é exatamente o Desafio proposto naquele tópico.

**`app/(app)/rotas/[id].jsx`** — Origem: 1. Componentes e Navegação, Passo 3.

```javascript
// app/(app)/rotas/[id].jsx
// Origem: 1. Componentes e Navegação no Framework (Passo 3)
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

**`app/(app)/corrida.jsx`** — a tela que mais tópicos alimentam. Origem: Localização e Mapas (Passo 7, base), Mídia (Passo 4, som), Animações com Reanimated (Passo 2, feedback do botão), 6. Acesso a Recursos Nativos (Passo 5, chacoalhada + haptics), Gráficos (Passo 1, velocidade em tempo real).

```javascript
// app/(app)/corrida.jsx
// Origem: 5. Localização e Mapas (Passo 7, base da tela)
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
```

**`app/(app)/historico.jsx`** — Origem: 7. Armazenamento Local (Passos 1-5) + Gráficos (Passo 2).

```javascript
// app/(app)/historico.jsx
// Origem: 7. Armazenamento Local no Framework (Passos 1 a 5)
//         + Gráficos — em tempo real e estáticos (Passo 2)
import * as SQLite from 'expo-sqlite';
import { useEffect, useState } from 'react';
import { BarChart } from 'react-native-gifted-charts';
import { exportarHistoricoComoJson } from '../../utils/exportarHistorico';

export default function Historico() {
  const db = SQLite.openDatabaseSync('corridas.db');
  const [corridas, setCorridas] = useState([]);

  useEffect(() => {
    db.execSync(
      'CREATE TABLE IF NOT EXISTS corridas (id INTEGER PRIMARY KEY AUTOINCREMENT, distancia REAL, duracao INTEGER, data TEXT)'
    );
    carregarCorridas();
  }, []);

  function salvarCorrida(distancia, duracao) {
    db.runAsync(
      'INSERT INTO corridas (distancia, duracao, data) VALUES (?, ?, ?)',
      [distancia, duracao, new Date().toISOString()]
    ).then(carregarCorridas);
  }

  function carregarCorridas() {
    db.getAllAsync('SELECT * FROM corridas ORDER BY data DESC;').then(setCorridas);
  }

  const dadosDeDistancia = corridas.map((corrida) => ({
    value: Math.round(corrida.distancia),
    label: new Date(corrida.data).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }),
  }));

  return (
    <>
      <BarChart data={dadosDeDistancia} />
      {/* lista de corridas + botão "Exportar" (exportarHistoricoComoJson) ficam
          a cargo dos Exercícios do Tópico 7 — omitidos aqui por já estarem
          descritos passo a passo naquela seção */}
    </>
  );
}
```

**`components/CartaoDeRota.jsx`** — Origem: 1. Componentes e Navegação + Mídia, Passo 2 (imagem).

```javascript
// components/CartaoDeRota.jsx
// Origem: 1. Componentes e Navegação no Framework
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
```

**`components/AvisoPermissaoNegada.jsx`** — Origem: 4. Permissões Avançadas, Passo 4.

```javascript
// components/AvisoPermissaoNegada.jsx
// Origem: 4. Permissões Avançadas (Passo 4)
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

**`components/RaceMap.jsx`** — Origem: 5. Localização e Mapas (Passos 5-6) + Animações com Reanimated, Passo 1 (fade-in).

```javascript
// components/RaceMap.jsx
// Origem: 5. Localização e Mapas (Passos 5 e 6)
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
```

**`context/AuthContext.jsx`** — Origem: Autenticação e Proteção de Telas, Passo 3.

```javascript
// context/AuthContext.jsx
// Origem: Autenticação e Proteção de Telas (Passo 3)
import { createContext, useContext, useEffect, useState } from 'react';
import { lerTokenSeguro, salvarTokenSeguro, apagarTokenSeguro } from '../utils/armazenamentoSeguro';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    lerTokenSeguro().then((tokenSalvo) => {
      setToken(tokenSalvo);
      setCarregando(false);
    });
  }, []);

  async function entrar(email, senha) {
    // Usuário de teste local, sem depender do reqres.in (Autenticação, Passo 3)
    if (email === 'teste@teste.com' && senha === '1234') {
      const tokenLocal = 'token-de-teste-local';
      await salvarTokenSeguro(tokenLocal);
      setToken(tokenLocal);
      return;
    }

    const resposta = await fetch('https://reqres.in/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password: senha }),
    });
    const dados = await resposta.json();

    if (!resposta.ok) {
      throw new Error(dados.error ?? 'Falha no login.');
    }

    await salvarTokenSeguro(dados.token);
    setToken(dados.token);
  }

  async function sair() {
    await apagarTokenSeguro();
    setToken(null);
  }

  return (
    <AuthContext.Provider value={{ token, carregando, entrar, sair }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
```

**`context/CorridaContext.jsx`** — Origem: 9. Gerenciamento de Estado, Passos 1-3.

```javascript
// context/CorridaContext.jsx
// Origem: 9. Gerenciamento de Estado (Context API, Redux) (Passos 1 a 3)
import { createContext, useContext, useReducer } from 'react';

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

const CorridaContext = createContext(null);

export function CorridaProvider({ children }) {
  const [state, dispatch] = useReducer(corridaReducer, { pontos: [] });
  return (
    <CorridaContext.Provider value={{ state, dispatch }}>
      {children}
    </CorridaContext.Provider>
  );
}

export function useCorrida() {
  return useContext(CorridaContext);
}
```

> Nota: `useTracking` (abaixo) guarda `pontos` com `useState` local, próprio da tela `corrida.jsx` — trocá-lo por `CorridaProvider`/`useCorrida` (para que `historico.jsx` também veja a corrida em andamento) é exatamente o Exercício Médio proposto no Tópico 9.

**`hooks/useTracking.js`** — Origem: 5. Localização e Mapas, Passos 1-3.

```javascript
// hooks/useTracking.js
// Origem: 5. Localização e Mapas (Passos 1 a 3)
import { useRef, useState } from 'react';
import * as Location from 'expo-location';

export function useTracking() {
  const [pontos, setPontos] = useState([]);
  const [rastreando, setRastreando] = useState(false);
  const [erro, setErro] = useState(null);
  const assinaturaRef = useRef(null);

  async function iniciar() {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== 'granted') {
      setErro('Permissão de localização negada. Ative-a nas configurações do app para rastrear.');
      return;
    }

    setErro(null);
    setPontos([]);
    setRastreando(true);

    assinaturaRef.current = await Location.watchPositionAsync(
      {
        accuracy: Location.Accuracy.High,
        timeInterval: 3000,
        distanceInterval: 5,
      },
      (posicao) => {
        setPontos((anteriores) => [...anteriores, posicao.coords]);
      }
    );
  }

  function parar() {
    assinaturaRef.current?.remove();
    assinaturaRef.current = null;
    setRastreando(false);
  }

  return { pontos, rastreando, erro, iniciar, parar };
}
```

**`utils/calculos.js`** — Origem: 3. Concorrência e Threads, Passos 1-3.

```javascript
// utils/calculos.js
// Origem: 3. Concorrência e Threads (Passos 1 a 3)
export function distanciaEntre(p1, p2) {
  const R = 6371000;
  const rad = Math.PI / 180;
  const dLat = (p2.latitude - p1.latitude) * rad;
  const dLon = (p2.longitude - p1.longitude) * rad;
  const lat1 = p1.latitude * rad;
  const lat2 = p2.latitude * rad;

  const a = Math.sin(dLat / 2) ** 2 + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

export function distanciaTotal(pontos) {
  let soma = 0;
  for (let i = 1; i < pontos.length; i++) {
    soma += distanciaEntre(pontos[i - 1], pontos[i]);
  }
  return soma;
}

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

**`utils/mapaHtml.js`** — Origem: 5. Localização e Mapas, Passo 4.

```javascript
// utils/mapaHtml.js
// Origem: 5. Localização e Mapas (Passo 4)
export function obterMapaCorridaHtml(latitudeInicial, longitudeInicial) {
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

    window.adicionarPonto = function (lat, lon) {
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

**`utils/armazenamentoSeguro.js`** — Origem: Autenticação e Proteção de Telas, Passo 2.

```javascript
// utils/armazenamentoSeguro.js
// Origem: Autenticação e Proteção de Telas (Passo 2)
import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

const CHAVE_TOKEN = 'corrida_token';

export async function salvarTokenSeguro(token) {
  if (Platform.OS === 'web') {
    localStorage.setItem(CHAVE_TOKEN, token);
    return;
  }
  await SecureStore.setItemAsync(CHAVE_TOKEN, token);
}

export async function lerTokenSeguro() {
  if (Platform.OS === 'web') {
    return localStorage.getItem(CHAVE_TOKEN);
  }
  return SecureStore.getItemAsync(CHAVE_TOKEN);
}

export async function apagarTokenSeguro() {
  if (Platform.OS === 'web') {
    localStorage.removeItem(CHAVE_TOKEN);
    return;
  }
  await SecureStore.deleteItemAsync(CHAVE_TOKEN);
}
```

**`utils/exportarHistorico.js`** — Origem: 7. Armazenamento Local, Passo 5.

```javascript
// utils/exportarHistorico.js
// Origem: 7. Armazenamento Local no Framework (Passo 5)
import { File, Paths } from 'expo-file-system';

export function exportarHistoricoComoJson(corridas) {
  const arquivo = new File(Paths.document, 'historico-corridas.json');
  arquivo.write(JSON.stringify(corridas, null, 2));
  return arquivo.uri;
}
```

**`utils/useAcelerometro.js`** e **`utils/useDetectorDeChacoalhada.js`** — Origem: 6. Acesso a Recursos Nativos, Passos 1-3.

```javascript
// utils/useAcelerometro.js
// Origem: 6. Acesso a Recursos Nativos via Framework (Passos 1 e 2)
import { Accelerometer } from 'expo-sensors';
import { useEffect, useState } from 'react';

export function useAcelerometro(intervaloMs = 200) {
  const [dados, setDados] = useState({ x: 0, y: 0, z: 0 });
  const [disponivel, setDisponivel] = useState(false);

  useEffect(() => {
    let assinatura;
    let cancelado = false;

    Accelerometer.isAvailableAsync().then((temSensor) => {
      if (cancelado) return;
      setDisponivel(temSensor);
      if (!temSensor) return;

      Accelerometer.setUpdateInterval(intervaloMs);
      assinatura = Accelerometer.addListener(setDados);
    });

    return () => {
      cancelado = true;
      assinatura?.remove();
    };
  }, [intervaloMs]);

  return { ...dados, disponivel };
}
```

```javascript
// utils/useDetectorDeChacoalhada.js
// Origem: 6. Acesso a Recursos Nativos via Framework (Passo 3)
import { useEffect, useRef } from 'react';
import { useAcelerometro } from './useAcelerometro';

export function useDetectorDeChacoalhada(aoChacoalhar, limiar = 2.5) {
  const { x, y, z } = useAcelerometro();
  const ultimaChacoalhada = useRef(0);

  useEffect(() => {
    const forca = Math.sqrt(x * x + y * y + z * z);
    const agora = Date.now();

    if (forca > limiar && agora - ultimaChacoalhada.current > 1000) {
      ultimaChacoalhada.current = agora;
      aoChacoalhar();
    }
  }, [x, y, z, limiar]);
}
```

**`utils/informacoesDoDispositivo.js`** e o módulo nativo — Origem: "Chamando código nativo (Kotlin/Java)", Passos 3-4. Diferente do resto deste apêndice (só reorganizado a partir do que já tinha sido mostrado), este módulo foi gerado do zero com `npx create-expo-module@latest --local` dentro do próprio `pamii-projetos/`, depois prebuilado e compilado de verdade com `./gradlew :informacoes-do-dispositivo:compileDebugKotlin` — `BUILD SUCCESSFUL`, com o `.class` do módulo gerado em `android/build/tmp/kotlin-classes/debug/`.

```javascript
// utils/informacoesDoDispositivo.js
// Origem: Chamando código nativo (Kotlin/Java) a partir do React Native (Passo 4)
import InformacoesDoDispositivo from '../modules/informacoes-do-dispositivo/src/InformacoesDoDispositivoModule';

export function lerNivelDeBateriaBruto() {
  return InformacoesDoDispositivo.nivelDeBateriaBruto();
}
```

```kotlin
// modules/informacoes-do-dispositivo/android/src/main/java/expo/modules/informacoesdodispositivo/InformacoesDoDispositivoModule.kt
// Origem: Chamando código nativo (Kotlin/Java) a partir do React Native (Passo 3)
package expo.modules.informacoesdodispositivo

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class InformacoesDoDispositivoModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("InformacoesDoDispositivo")

    Function("nivelDeBateriaBruto") {
      return@Function 87
    }
  }
}
```

**`servidor-rotas/index.js`** — projeto Node separado. Origem: "Criando sua própria API (Node.js/Express) e autenticação com JWT", Passos 2, 4 e 5 juntos (verificado de verdade rodando `node index.js` + `curl` naquele tópico).

```javascript
// servidor-rotas/index.js
// Origem: Criando sua própria API (Node.js/Express) e autenticação com JWT (Passos 2, 4 e 5)
const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');

const app = express();
app.use(cors());
app.use(express.json());

const SEGREDO = 'troque-isso-por-uma-variavel-de-ambiente-em-producao';

const ROTAS_PLANEJADAS = [
  { id: '1', nome: 'Volta do parque', distanciaEstimadaKm: 3.2 },
  { id: '2', nome: 'Orla da praia', distanciaEstimadaKm: 5.8 },
];

app.get('/rotas', (req, res) => {
  res.json(ROTAS_PLANEJADAS);
});

app.post('/login', (req, res) => {
  const { email, senha } = req.body;

  if (email === 'atleta@exemplo.com' && senha === '123456') {
    const token = jwt.sign({ email }, SEGREDO, { expiresIn: '1h' });
    return res.json({ token });
  }

  res.status(401).json({ erro: 'E-mail ou senha inválidos.' });
});

function exigirToken(req, res, next) {
  const cabecalho = req.headers.authorization;
  const token = cabecalho?.split(' ')[1];

  if (!token) {
    return res.status(401).json({ erro: 'Token não enviado.' });
  }

  try {
    req.usuario = jwt.verify(token, SEGREDO);
    next();
  } catch {
    res.status(401).json({ erro: 'Token inválido ou expirado.' });
  }
}

app.post('/corridas', exigirToken, (req, res) => {
  const { distancia, duracao } = req.body;
  res.status(201).json({ mensagem: `Corrida de ${req.usuario.email} salva.`, distancia, duracao });
});

app.listen(3000, () => console.log('Servidor rodando em http://localhost:3000'));
```

**Pacotes usados no app inteiro** (todos instaláveis com `npx expo install <pacote>`, exceto os do servidor, que usam `npm install`): `expo-router`, `expo-location`, `react-native-webview`, `expo-image`, `expo-audio`, `react-native-reanimated`, `react-native-worklets`, `expo-sensors`, `expo-haptics`, `expo-sqlite`, `@react-native-async-storage/async-storage`, `expo-file-system`, `react-native-gifted-charts`, `expo-linear-gradient`, `react-native-svg`, `expo-notifications`, `expo-constants`, `@tanstack/react-query`, `axios`, `expo-contacts`, `expo-linking`, `expo-secure-store`. No servidor: `express`, `cors`, `jsonwebtoken`.
