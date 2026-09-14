package expo.modules.informacoesdodispositivo

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class InformacoesDoDispositivoModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("InformacoesDoDispositivo")

    Function("nivelDeBateriaBruto") {
      // um exemplo simples: no Kotlin de verdade, isso viria de
      // android.os.BatteryManager — aqui só para ilustrar a ponte
      return@Function 87
    }
  }
}
