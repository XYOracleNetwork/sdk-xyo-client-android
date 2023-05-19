package network.xyo.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.account.Account
import network.xyo.witness.Witness
import network.xyo.witness.WitnessConfig
import network.xyo.witness.WitnessParams
import network.xyo.payload.IPayload

class SystemInfoWitnessParams<TConfig : WitnessConfig>(
    val context: Context, account: Account, config: TConfig
): WitnessParams<TConfig>(account, config)
@RequiresApi(Build.VERSION_CODES.M)
class SystemInfoWitness(params: SystemInfoWitnessParams<WitnessConfig>) : Witness<WitnessConfig, SystemInfoWitnessParams<WitnessConfig>>(params) {
    override fun observe(payloads: Set<IPayload>?): Set<IPayload> {
        SystemInfoPayload.detect(this.params.context)
        return super.observe(payloads)
    }
}