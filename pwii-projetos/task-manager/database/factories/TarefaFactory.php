<?php

namespace Database\Factories;

use App\Models\Tarefa;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Tarefa>
 */
class TarefaFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'titulo' => fake()->sentence(4),
            'prazo' => fake()->dateTimeBetween('now', '+30 days'),
            'concluida' => fake()->boolean(20),
            'tipo' => fake()->randomElement(['urgente', 'rotina']),
        ];
    }

    /**
     * Indica que a tarefa é urgente e tem prazo próximo (dentro de 2 dias),
     * para bater com a regra de `Tarefa::isUrgente()`.
     */
    public function urgente(): static
    {
        return $this->state(fn (array $attributes) => [
            'tipo' => 'urgente',
            'prazo' => fake()->dateTimeBetween('now', '+2 days'),
            'concluida' => false,
        ]);
    }

    /**
     * Indica que a tarefa já foi concluída.
     */
    public function concluida(): static
    {
        return $this->state(fn (array $attributes) => [
            'concluida' => true,
        ]);
    }
}
