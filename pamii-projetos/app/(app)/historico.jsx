// app/(app)/historico.jsx
// Origem: Tutorial_PAMII.md — 7. Armazenamento Local no Framework (Passos 1 a 5)
//         + Gráficos — em tempo real e estáticos (Passo 2)
import * as SQLite from 'expo-sqlite';
import { useEffect, useState } from 'react';
import { BarChart } from 'react-native-gifted-charts';

export default function Historico() {
  const db = SQLite.openDatabaseSync('corridas.db');
  const [corridas, setCorridas] = useState([]);

  useEffect(() => {
    db.execSync(
      'CREATE TABLE IF NOT EXISTS corridas (id INTEGER PRIMARY KEY AUTOINCREMENT, distancia REAL, duracao INTEGER, data TEXT)'
    );
    carregarCorridas();
  }, []);

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
    </>
  );
}
