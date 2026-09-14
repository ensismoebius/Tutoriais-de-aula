import { registerWebModule, NativeModule } from 'expo';

// InformacoesDoDispositivoModule is not available on the web platform.
class InformacoesDoDispositivoModule extends NativeModule<{}> {}

export default registerWebModule(InformacoesDoDispositivoModule, 'InformacoesDoDispositivoModule');
