// utils/exportarHistorico.js
// Origem: Tutorial_PAMII.md — 7. Armazenamento Local no Framework (Passo 5)
import { File, Paths } from 'expo-file-system';

export function exportarHistoricoComoJson(corridas) {
  const arquivo = new File(Paths.document, 'historico-corridas.json');
  arquivo.write(JSON.stringify(corridas, null, 2));
  return arquivo.uri; // caminho local do arquivo gerado
}
