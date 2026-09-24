<?php

namespace Tests\Feature;

use App\Models\Tarefa;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class TarefaFactoryTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Uma Tarefa criada pela factory aparece na listagem, sem que o teste
     * precise digitar manualmente todos os campos obrigatórios do Model.
     */
    public function test_tarefa_criada_pela_factory_aparece_na_listagem(): void
    {
        $tarefa = Tarefa::factory()->create(['titulo' => 'Tarefa de teste via factory']);

        $response = $this->get('/tarefas');

        $response->assertStatus(200);
        $response->assertSee('Tarefa de teste via factory');
    }

    /**
     * O state urgente() gera uma Tarefa que se comporta como urgente de
     * verdade — conectando a factory à regra de negócio do Model.
     */
    public function test_state_urgente_produz_tarefa_urgente_com_prioridade_maxima(): void
    {
        $tarefa = Tarefa::factory()->urgente()->create();

        $this->assertTrue($tarefa->isUrgente());
        $this->assertSame(10, $tarefa->calcularPrioridade());
    }

    /**
     * O state concluida() fixa concluida=true, e uma tarefa concluída
     * nunca é urgente, mesmo com tipo='urgente' e prazo vencendo.
     */
    public function test_state_concluida_nunca_e_urgente(): void
    {
        $tarefa = Tarefa::factory()->concluida()->create(['tipo' => 'urgente']);

        $this->assertTrue($tarefa->concluida);
        $this->assertFalse($tarefa->isUrgente());
    }
}
