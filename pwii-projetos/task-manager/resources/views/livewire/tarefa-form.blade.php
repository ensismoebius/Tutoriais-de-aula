<form wire:submit="salvar">
    <input type="text" wire:model="titulo" placeholder="Título">
    @error('titulo') <span class="erro">{{ $message }}</span> @enderror

    <input type="date" wire:model="prazo">
    @error('prazo') <span class="erro">{{ $message }}</span> @enderror

    <button type="submit">Criar tarefa</button>
</form>
