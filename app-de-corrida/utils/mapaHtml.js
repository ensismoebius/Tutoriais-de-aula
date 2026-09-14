// utils/mapaHtml.js
// Origem: Tutorial_PAMII.md — 5. Localização e Mapas (Passo 4)
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
