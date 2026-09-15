<div>
    @foreach($tarefas as $tarefa)
        <div class="tarefa-card {{ $tarefa->concluida ? 'concluida' : '' }}">
            <span>{{ $tarefa->titulo }}</span>

            @unless($tarefa->concluida)
                <button
                    wire:click="concluir({{ $tarefa->id }})"
                    wire:loading.attr="disabled"
                    wire:target="concluir({{ $tarefa->id }})"
                >
                    <span wire:loading.remove wire:target="concluir({{ $tarefa->id }})">Concluir</span>
                    <span wire:loading wire:target="concluir({{ $tarefa->id }})">Salvando...</span>
                </button>
            @endunless

            <button wire:click="remover({{ $tarefa->id }})">Excluir</button>
        </div>
    @endforeach
</div>
