<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('configuracoes', function (Blueprint $table) {
            $table->id();
            $table->unsignedTinyInteger('tarefas_por_pagina')->default(10);
            $table->timestamps();
        });

        // Uma tabela "singleton" só faz sentido com exatamente uma linha —
        // ela já nasce populada, em vez de esperar um primeiro cadastro.
        DB::table('configuracoes')->insert([
            'tarefas_por_pagina' => 10,
            'created_at' => now(),
            'updated_at' => now(),
        ]);
    }

    public function down(): void
    {
        Schema::dropIfExists('configuracoes');
    }
};
