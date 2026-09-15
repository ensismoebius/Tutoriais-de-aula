<?php

use App\Http\Controllers\NewsletterController;
use Illuminate\Support\Facades\Route;

Route::post('/inspecionar', [NewsletterController::class, 'inspecionar']);
