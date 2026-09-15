<?php

namespace App\Providers;

use App\DAO\TarefaDAO;
use App\DAO\TarefaDAOInterface;
use App\Models\Tarefa;
use Illuminate\Support\Facades\Blade;
use Illuminate\Support\Facades\View;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        $this->app->bind(TarefaDAOInterface::class, TarefaDAO::class);
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        View::composer('layouts.app', function ($view) {
            $view->with(
                'totalUrgentes',
                Tarefa::where('concluida', false)
                    ->whereNotNull('prazo')
                    ->where('prazo', '<=', now()->addDays(2))
                    ->count()
            );
        });

        Blade::directive('urgente', function ($tarefa) {
            return "<?php if(($tarefa)->isUrgente()): ?>";
        });

        Blade::directive('endurgente', function () {
            return '<?php endif; ?>';
        });
    }
}
