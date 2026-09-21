<?php

namespace App\Providers;

use App\DAO\PostDAO;
use App\DAO\PostDAOInterface;
use App\Services\GoogleClientIdTokenVerifier;
use App\Services\GoogleIdTokenVerifier;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        $this->app->bind(PostDAOInterface::class, PostDAO::class);

        $this->app->bind(GoogleIdTokenVerifier::class, fn () => new GoogleClientIdTokenVerifier(
            config('services.google.client_id')
        ));
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        //
    }
}
