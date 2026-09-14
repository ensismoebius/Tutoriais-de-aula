import { NativeModule, requireNativeModule } from 'expo';

declare class InformacoesDoDispositivoModule extends NativeModule<{}> {
  nivelDeBateriaBruto(): number;
}

export default requireNativeModule<InformacoesDoDispositivoModule>('InformacoesDoDispositivo');
