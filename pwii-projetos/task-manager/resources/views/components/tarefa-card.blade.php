<div class="tarefa-card {{ $tarefa->isUrgente() ? 'urgente' : '' }}">
    <h3>{{ $tarefa->titulo }}</h3>
    <span>{{ formatar_prazo($tarefa->prazo) }} ({{ tempo_restante($tarefa->prazo) }})</span>

    @urgente($tarefa)
        <span class="badge">URGENTE</span>
    @endurgente
</div>
