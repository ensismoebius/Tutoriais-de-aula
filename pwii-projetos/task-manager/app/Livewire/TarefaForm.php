<?php

namespace App\Livewire;

use App\DAO\TarefaDAOInterface;
use Livewire\Attributes\Validate;
use Livewire\Component;

class TarefaForm extends Component
{
    #[Validate('required|string|max:150')]
    public string $titulo = '';

    #[Validate('required|date|after:today')]
    public string $prazo = '';

    public function salvar(TarefaDAOInterface $dao): void
    {
        $this->validate();

        $dao->criar(['titulo' => $this->titulo, 'prazo' => $this->prazo, 'tipo' => 'rotina']);

        $this->reset(['titulo', 'prazo']);
        $this->dispatch('tarefa-criada');
    }

    public function render()
    {
        return view('livewire.tarefa-form');
    }
}
