package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.address.Account
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleParams
import network.xyo.client.module.Witness
import network.xyo.payload.Payload
import org.json.JSONArray

class SystemInfoWitnessParams<TConfig : ModuleConfig>(
    val context: Context, account: Account, config: TConfig
): ModuleParams<ModuleConfig>(account, config)
@RequiresApi(Build.VERSION_CODES.M)
class SystemInfoWitness(params: SystemInfoWitnessParams<ModuleConfig>) : Witness<ModuleConfig, SystemInfoWitnessParams<ModuleConfig>>(params) {
    override fun observe(payloads: JSONArray?): JSONArray {
        SystemInfoPayload.detect(this.params.context)
        return super.observe(payloads)
    }
}